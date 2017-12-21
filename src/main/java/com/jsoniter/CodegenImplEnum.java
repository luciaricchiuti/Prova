package com.jsoniter;

import com.jsoniter.spi.ClassInfo;

import java.util.*;

class CodegenImplEnum {
	public static String genEnum(ClassInfo classInfo) {
		StringBuilder lines = new StringBuilder();
		append(lines, "if (iter.readNull()) { return null; }");
		append(lines, "com.jsoniter.spi.Slice field = com.jsoniter.CodegenAccess.readSlice(iter);");
		append(lines, "switch (field.len()) {");
		append(lines, renderTriTree(buildTriTree(Arrays.asList(classInfo.clazz.getEnumConstants()))));
		append(lines, "}"); // end of switch
		append(lines, String.format("throw iter.reportError(\"decode enum\", field + \" is not valid enum for %s\");",
				classInfo.clazz.getName()));
		return lines.toString();
	}

	private static Map<Integer, Object> buildTriTree(List<Object> allConsts) {
		Map<Integer, Object> trieTree = new HashMap<Integer, Object>();
		for (Object e : allConsts) {
			byte[] fromNameBytes = e.toString().getBytes();
			Map<Byte, Object> current = null;
			try {
				if (trieTree.get(fromNameBytes.length) instanceof Map<?, ?>) {
					current = (Map<Byte, Object>) trieTree.get(fromNameBytes.length);
				} else {
					throw new Exception();
				}
			} catch (Exception e1) {
				System.out.println("Exception " + e1);
			} finally {
				System.out.print("");
			}
			if (current == null) {
				current = new HashMap<Byte, Object>();
				trieTree.put(fromNameBytes.length, current);
			}
			for (int i = 0; i < fromNameBytes.length - 1; i++) {
				byte b = fromNameBytes[i];
				Map<Byte, Object> next = null;
				try {
					if (current.get(b) instanceof Map<?, ?>) {
						next = (Map<Byte, Object>) current.get(b);
					} else {
						throw new Exception();
					}
				} catch (Exception e1) {
					System.out.println("Exception " + e1);
				} finally {
					System.out.print("");
				}
				if (next == null) {
					next = new HashMap<Byte, Object>();
					current.put(b, next);
				}
				current = next;
			}
			current.put(fromNameBytes[fromNameBytes.length - 1], e);
		}
		return trieTree;
	}

	private static String renderTriTree(Map<Integer, Object> trieTree) {
		StringBuilder switchBody = new StringBuilder();
		for (Map.Entry<Integer, Object> entry : trieTree.entrySet()) {
			Integer len = entry.getKey();
			append(switchBody, "case " + len + ": ");
			Map<Byte, Object> current = null;
			try {
				if (entry.getValue() instanceof Map<?, ?>) {
					current = (Map<Byte, Object>) entry.getValue();
				} else {
					throw new Exception();
				}
			} catch (Exception e1) {
				System.out.println("Exception " + e1);
			} finally {
				System.out.print("");
			}
			addFieldDispatch(switchBody, len, 0, current, new ArrayList<Byte>());
			append(switchBody, "break;");
		}
		return switchBody.toString();
	}

	private static void addFieldDispatch(StringBuilder lines, int len, int i, Map<Byte, Object> current,
			List<Byte> bytesToCompare) {
		for (Map.Entry<Byte, Object> entry : current.entrySet()) {
			Byte b = entry.getKey();
			if (i == len - 1) {
				append(lines, "if (");
				for (int j = 0; j < bytesToCompare.size(); j++) {
					Byte a = bytesToCompare.get(j);
					append(lines, String.format("field.at(%d)==%s && ", i - bytesToCompare.size() + j, a));
				}
				append(lines, String.format("field.at(%d)==%s", i, b));
				append(lines, ") {");
				Object e = entry.getValue();
				append(lines, String.format("return %s.%s;", e.getClass().getName(), e.toString()));
				append(lines, "}");
				continue;
			}
			Map<Byte, Object> next = null;
			try {
				if (entry.getValue() instanceof Map<?, ?>) {
					next = (Map<Byte, Object>) entry.getValue();
				} else {
					throw new Exception();
				}
			} catch (Exception e1) {
				System.out.println("Exception " + e1);
			} finally {
				System.out.print("");
			}
			if (next.size() == 1) {
				ArrayList<Byte> nextBytesToCompare = new ArrayList<Byte>(bytesToCompare);
				nextBytesToCompare.add(b);
				addFieldDispatch(lines, len, i + 1, next, nextBytesToCompare);
				continue;
			}
			append(lines, "if (");
			for (int j = 0; j < bytesToCompare.size(); j++) {
				Byte a = bytesToCompare.get(j);
				append(lines, String.format("field.at(%d)==%s && ", i - bytesToCompare.size() + j, a));
			}
			append(lines, String.format("field.at(%d)==%s", i, b));
			append(lines, ") {");
			addFieldDispatch(lines, len, i + 1, next, new ArrayList<Byte>());
			append(lines, "}");
		}
	}

	private static void append(StringBuilder lines, String str) {
		lines.append(str);
		lines.append("\n");
	}
}
