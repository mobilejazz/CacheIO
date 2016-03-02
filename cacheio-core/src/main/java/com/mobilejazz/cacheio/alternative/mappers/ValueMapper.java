package com.mobilejazz.cacheio.alternative.mappers;

import com.mobilejazz.cacheio.exceptions.SerializerException;

import java.io.InputStream;
import java.io.OutputStream;

interface ValueMapper {

    void write(Object value, OutputStream out) throws SerializerException;

    <T> T read(Class<T> type, InputStream in) throws SerializerException;

}
