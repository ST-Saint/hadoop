package org.apache.hadoop.hdfs.server.namenode.upgradefuzzing;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;

import org.apache.commons.io.IOUtils;

public class RandomSource {
    byte[] source;
    Integer cursor;

    public static byte[] repeat(byte[] array, int times) {
        byte[] repeated = new byte[times * array.length];
        for (int dest = 0; dest < repeated.length; dest += array.length) {
            System.arraycopy(array, 0, repeated, dest, array.length);
        }
        return repeated;
    }

    public static byte[] repeat(byte[] array) {
        return repeat(array, 2);
    }

    public RandomSource(byte[] bytes) {
        source = bytes;
        cursor = 0;
    }

    public RandomSource(InputStream input) throws IOException {
        source = IOUtils.toByteArray(input);
        cursor = 0;
    }

    public Integer nextInt() {
        if (cursor + 4 > source.length) {
            source = repeat(source);
        }
        byte[] bytes = Arrays.copyOfRange(source, cursor, cursor + 4);
        cursor += 4;
        ByteBuffer wrapped = ByteBuffer.wrap(bytes);
        return wrapped.getInt() & 0x7fffffff;
    }

    public Integer nextInt(int bound) {
        if (cursor + 4 > source.length) {
            source = repeat(source);
        }
        byte[] bytes = Arrays.copyOfRange(source, cursor, cursor + 4);
        cursor += 4;
        ByteBuffer wrapped = ByteBuffer.wrap(bytes);
        return (wrapped.getInt() & (0x7fffffff)) % bound;
    }

    public Boolean nextBoolean() {
        if (cursor + 1 > source.length) {
            source = repeat(source);
        }
        int bool = (source[cursor++] & 0xFF);
        return bool % 2 == 0;
    }

    public byte[] nextBytes() {
        Integer length = nextInt();
        return nextBytes(length);
    }

    public byte[] nextBytes(Integer length) {
        while (cursor + length > source.length) {
            source = repeat(source);
        }
        byte[] res = Arrays.copyOfRange(source, cursor, cursor + length);
        cursor += length;
        return res;
    }

}
