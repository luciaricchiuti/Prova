package com.jsoniter.fuzzy;

import com.jsoniter.CodegenAccess;
import com.jsoniter.JsonIterator;
import com.jsoniter.spi.Decoder;

import java.io.IOException;

public class StringIntDecoder extends Decoder.IntDecoder {

    @Override
    public int decodeInt(JsonIterator iter) throws IOException {
        byte c = CodegenAccess.nextToken(iter);
        if (c != '"') {
            throw iter.reportError("StringIntDecoder", "expect \", but found: " + Byte.toString(c).charAt(0));
        }
        int val = iter.readInt();
        c = CodegenAccess.nextToken(iter);
        if (c != '"') {
            throw iter.reportError("StringIntDecoder", "expect \", but found: " + Byte.toString(c).charAt(0));
        }
        return val;
    }
}
