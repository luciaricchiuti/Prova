package com.jsoniter;

import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.jsoniter.spi.*;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

// only uesd by generated code to access decoder
public class CodegenAccess {

	public static void addMissingField(List missingFields, long tracker, long mask, String fieldName) {
		if ((tracker & mask) == 0) {
			missingFields.add(fieldName);
		}
	}

	public static <T extends Collection> T reuseCollection(T col) {
		col.clear();
		return col;
	}

	public static Object existingObject(JsonIterator iter) {
		return iter.existingObject;
	}

	public static Object resetExistingObject(JsonIterator iter) {
		Object obj = iter.existingObject;
		iter.existingObject = null;
		return obj;
	}

	public static void setExistingObject(JsonIterator iter, Object obj) {
		iter.existingObject = obj;
	}

	public final static boolean nextTokenIsComma(final JsonIterator iter) throws IOException {
		byte c = readByte(iter);
		if (c == ',') {
			return true;
		}
		return nextTokenIsCommaSlowPath(iter, c);
	}

	private static boolean nextTokenIsCommaSlowPath(JsonIterator iter, byte c) throws IOException {
		switch (c) {
		case ' ':
		case '\n':
		case '\r':
		case '\t':
			break;
		default:
			return false;
		}
		return nextToken(iter) == ',';
	}

	public static byte nextToken(JsonIterator iter) throws IOException {
		return IterImpl.nextToken(iter);
	}

	public static final boolean readBoolean(String cacheKey, JsonIterator iter) throws IOException {
		if (JsoniterSpi.getDecoder(cacheKey) instanceof Decoder.BooleanDecoder) {
			return ((Decoder.BooleanDecoder) JsoniterSpi.getDecoder(cacheKey)).decodeBoolean(iter);
		} else {
			throw new IOException();
		}
	}

	public static final short readShort(String cacheKey, JsonIterator iter) throws IOException {
		if (JsoniterSpi.getDecoder(cacheKey) instanceof Decoder.ShortDecoder) {
			return ((Decoder.ShortDecoder) JsoniterSpi.getDecoder(cacheKey)).decodeShort(iter);
		} else {
			throw new IOException();
		}
	}

	public static final int readInt(String cacheKey, JsonIterator iter) throws IOException {
		if (JsoniterSpi.getDecoder(cacheKey) instanceof Decoder.IntDecoder) {
			return ((Decoder.IntDecoder) JsoniterSpi.getDecoder(cacheKey)).decodeInt(iter);
		} else {
			throw new IOException();
		}
	}

	public static final long readLong(String cacheKey, JsonIterator iter) throws IOException {
		if (JsoniterSpi.getDecoder(cacheKey) instanceof Decoder.LongDecoder) {
			return ((Decoder.LongDecoder) JsoniterSpi.getDecoder(cacheKey)).decodeLong(iter);
		} else {
			throw new IOException();
		}
	}

	public static final float readFloat(String cacheKey, JsonIterator iter) throws IOException {
		if (JsoniterSpi.getDecoder(cacheKey) instanceof Decoder.FloatDecoder) {
			return ((Decoder.FloatDecoder) JsoniterSpi.getDecoder(cacheKey)).decodeFloat(iter);
		} else {
			throw new IOException();
		}
	}

	public static final double readDouble(String cacheKey, JsonIterator iter) throws IOException {
		if (JsoniterSpi.getDecoder(cacheKey) instanceof Decoder.DoubleDecoder) {
			return ((Decoder.DoubleDecoder) JsoniterSpi.getDecoder(cacheKey)).decodeDouble(iter);
		} else {
			throw new IOException();
		}
	}
/*
	public static final <T> T read(String cacheKey, JsonIterator iter) throws IOException {
		return ((T) Codegen.getDecoder(cacheKey, null).decode(iter));
	}
*/
	public static boolean readArrayStart(JsonIterator iter) throws IOException {
		byte c = IterImpl.nextToken(iter);
		if (c == '[') {
			c = IterImpl.nextToken(iter);
			if (c == ']') {
				return false;
			}
			iter.unreadByte();
			return true;
		}
		throw iter.reportError("readArrayStart", "expect [ or n");
	}

	public static boolean readObjectStart(JsonIterator iter) throws IOException {
		byte c = IterImpl.nextToken(iter);
		if (c == '{') {
			c = IterImpl.nextToken(iter);
			if (c == '}') {
				return false;
			}
			iter.unreadByte();
			return true;
		}
		throw iter.reportError("readObjectStart", "expect { or n, found: " + Byte.toString(c).charAt(0));
	}

	public static void reportIncompleteObject(JsonIterator iter) {
		throw iter.reportError("genObject", "expect }");
	}

	public static void reportIncompleteArray(JsonIterator iter) {
		throw iter.reportError("genArray", "expect ]");
	}

	public static final String readObjectFieldAsString(JsonIterator iter) throws IOException {
		String field = iter.readString();
		if (IterImpl.nextToken(iter) != ':') {
			throw iter.reportError("readObjectFieldAsString", "expect :");
		}
		return field;
	}

	public static final int readObjectFieldAsHash(JsonIterator iter) throws IOException {
		return IterImpl.readObjectFieldAsHash(iter);
	}

	public static final Slice readObjectFieldAsSlice(JsonIterator iter) throws IOException {
		return IterImpl.readObjectFieldAsSlice(iter);
	}

	public static final Slice readSlice(JsonIterator iter) throws IOException {
		return IterImpl.readSlice(iter);
	}

	public static final Object readMapKey(String cacheKey, JsonIterator iter) throws IOException {
		Slice encodedMapKey = readObjectFieldAsSlice(iter);
		MapKeyDecoder mapKeyDecoder = JsoniterSpi.getMapKeyDecoder(cacheKey);
		return mapKeyDecoder.decode(encodedMapKey);
	}

	final static boolean skipWhitespacesWithoutLoadMore(JsonIterator iter) throws IOException {
		for (int i = iter.head; i < iter.tail; i++) {
			byte c = iter.buf[i];
			switch (c) {
			case ' ':
			case '\n':
			case '\t':
			case '\r':
				continue;
			default:
				break;
			}
			iter.head = i;
			return false;
		}
		return true;
	}

	public static void staticGenDecoders(TypeLiteral[] typeLiterals, StaticCodegenTarget staticCodegenTarget) {
		Codegen.staticGenDecoders(typeLiterals, staticCodegenTarget);
	}

	public static int head(JsonIterator iter) {
		return iter.head;
	}

	public static void unreadByte(JsonIterator iter) throws IOException {
		iter.unreadByte();
	}

	public static byte readByte(JsonIterator iter) throws IOException {
		return IterImpl.readByte(iter);
	}

	public static int calcHash(String str) {
		return CodegenImplObjectHash.calcHash(str);
	}

	public static void skipFixedBytes(JsonIterator iter, int n) throws IOException {
		IterImpl.skipFixedBytes(iter, n);
	}

	public static class StaticCodegenTarget {
		public String outputDir;

		public StaticCodegenTarget(String outputDir) {
			this.outputDir = outputDir;
		}
	}
}
