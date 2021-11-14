package org.apache.hadoop.hdfs.server.namenode.upgradefuzzing;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;

import org.apache.commons.io.IOUtils;

public class RandomSource {
    byte[] source;
    Integer cursor;

    public RandomSource(InputStream input) throws IOException {
        source = IOUtils.toByteArray(input);
        cursor = 0;
    }

    public Integer nextInt() {
        byte[] bytes = Arrays.copyOfRange(source, cursor, cursor + 4);
        cursor+=4;
        ByteBuffer wrapped = ByteBuffer.wrap(bytes);
        return wrapped.getInt() & 0x7fffffff;
    }

    public Integer nextInt(int bound){
        byte[] bytes = Arrays.copyOfRange(source, cursor, cursor + 4);
        cursor+=4;
        ByteBuffer wrapped = ByteBuffer.wrap(bytes);
        return (wrapped.getInt() & (0x7fffffff) )%bound;
    }

    public Boolean nextBoolean() {
        int bool = (source[cursor++] & 0xFF);
        return bool%2==0;
    }
}
