package com.jsoniter.any;

import com.jsoniter.*;
import com.jsoniter.output.JsonStream;
import com.jsoniter.spi.JsonException;
import com.jsoniter.spi.TypeLiteral;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

class ArrayLazyAny extends LazyAny {

    private final static TypeLiteral<List<Any>> typeLiteral = new TypeLiteral<List<Any>>() {
    };
    private List<Any> cache;
    private int lastParsedPos;

    public ArrayLazyAny(byte[] data, int head, int tail) {
        super(data, head, tail);
        lastParsedPos = head;
    }

    @Override
    public ValueType valueType() {
        return ValueType.ARRAY;
    }

    @Override
    public Object object() {
        fillCache();
        return cache;
    }

    @Override
    public boolean toBoolean() {
        JsonIterator iter = parse();
        try {
            return CodegenAccess.readArrayStart(iter);
        } catch (IOException e) {
            throw new JsonException(e);
        } finally {
            JsonIteratorPool.returnJsonIterator(iter);
        }
    }

    @Override
    public int toInt() {
        return size();
    }

    @Override
    public long toLong() {
        return size();
    }

    @Override
    public float toFloat() {
        return size();
    }

    @Override
    public double toDouble() {
        return size();
    }

    @Override
    public int size() {
        fillCache();
        return cache.size();
    }

    @Override
    public Iterator<Any> iterator() {
        if (lastParsedPos == tail) {
            return cache.iterator();
        } else {
            return new LazyIterator();
        }
    }

    @Override
    public Any get(int index) {
        try {
            return fillCacheUntil(index);
        } catch (IndexOutOfBoundsException e) {
            return new NotFoundAny(index, object());
        }
    }

    @Override
    public Any get(Object[] keys, int idx) {
        if (idx == keys.length) {
            return this;
        }
        Object key = keys[idx];
        if (isWildcard(key)) {
            fillCache();
            ArrayList<Any> result = new ArrayList<Any>();
            for (Any element : cache) {
                Any mapped = element.get(keys, idx + 1);
                if (mapped.valueType() != ValueType.INVALID) {
                    result.add(mapped);
                }
            }
            return Any.rewrap(result);
        }
        try {
        	if(key instanceof Integer) {
        		 return fillCacheUntil((Integer) key).get(keys, idx + 1);
        	}
           
        } catch (IndexOutOfBoundsException e) {
            return new NotFoundAny(keys, idx, object());
        } catch (ClassCastException e) {
            return new NotFoundAny(keys, idx, object());
        }
		return null;
    }

    private void fillCache() {
        if (lastParsedPos == tail) {
            return;
        }
        if (cache == null) {
            cache = new ArrayList<Any>(4);
        }
        JsonIterator iter = JsonIteratorPool.borrowJsonIterator();
        try {
            iter.reset(data, lastParsedPos, tail);
            if (lastParsedPos == head) {
                if (!CodegenAccess.readArrayStart(iter)) {
                    lastParsedPos = tail;
                    return;
                }
                cache.add(iter.readAny());
            }
            while (CodegenAccess.nextToken(iter) == ',') {
                cache.add(iter.readAny());
            }
            lastParsedPos = tail;
        } catch (IOException e) {
            throw new JsonException(e);
        } finally {
            JsonIteratorPool.returnJsonIterator(iter);
        }
    }

    private Any fillCacheUntil(int target) {
        if (lastParsedPos == tail) {
            return cache.get(target);
        }
        if (cache == null) {
            cache = new ArrayList<Any>(4);
        }
        int i = cache.size();
        if (target < i) {
            return cache.get(target);
        }
        JsonIterator iter = JsonIteratorPool.borrowJsonIterator();
        try {
            iter.reset(data, lastParsedPos, tail);
            if (lastParsedPos == head) {
                if (!CodegenAccess.readArrayStart(iter)) {
                    lastParsedPos = tail;
                    throw new IndexOutOfBoundsException();
                }
                Any element = iter.readAny();
                cache.add(element);
                if (target == 0) {
                    lastParsedPos = CodegenAccess.head(iter);
                    return element;
                }
                i = 1;
            }
            while (CodegenAccess.nextToken(iter) == ',') {
                Any element = iter.readAny();
                cache.add(element);
                if (i++ == target) {
                    lastParsedPos = CodegenAccess.head(iter);
                    return element;
                }
            }
            lastParsedPos = tail;
        } catch (IOException e) {
            throw new JsonException(e);
        } finally {
            JsonIteratorPool.returnJsonIterator(iter);
        }
        throw new IndexOutOfBoundsException();
    }

    private class LazyIterator implements Iterator<Any> {

        private Any successive;
        private int index;

        public LazyIterator() {
            index = 0;
            successive = fillCacheUntil(index);
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean hasNext() {
            return successive != null;
        }

        @Override
        public Any next() {
            if (successive == null) {
                throw new IndexOutOfBoundsException();
            }
            Any current = successive;
            try {
                index++;
                successive = fillCacheUntil(index);
            } catch (IndexOutOfBoundsException e){
            	successive = null;
            }
            return current;
        }
    }

    @Override
    public void writeTo(JsonStream stream) throws IOException {
        if (lastParsedPos == head) {
            super.writeTo(stream);
        } else {
            // there might be modification
            fillCache();
            stream.writeVal(typeLiteral, cache);
        }
    }

    @Override
    public String toString() {
        if (lastParsedPos == head) {
            return super.toString();
        } else {
            fillCache();
            return JsonStream.serialize(cache);
        }
    }
}