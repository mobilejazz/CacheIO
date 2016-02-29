package com.mobilejazz.cacheio.serializers.gson;

import com.google.gson.Gson;
import com.mobilejazz.cacheio.alternative.Cache;
import com.mobilejazz.cacheio.exceptions.SerializerException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

public class GsonValueMapper implements Cache.ValueMapper {

    private Gson gson;

    public GsonValueMapper(Gson gson) {
        this.gson = gson;
    }

    @Override
    public void write(Object value, OutputStream out) throws SerializerException {
        final byte[] bytes = gson.toJson(value).getBytes();
        try {
            out.write(bytes);
        } catch (IOException e) {
            throw new SerializerException("Failed to write to json", e);
        }
    }

    @Override
    public <T> T read(Class<T> type, InputStream in) throws SerializerException {
        final InputStreamReader reader = new InputStreamReader(in);
        return gson.fromJson(reader, type);
    }

}