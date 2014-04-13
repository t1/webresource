package com.github.t1.webresource.codec;

import java.io.*;
import java.lang.reflect.Constructor;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.github.t1.webresource.meta.*;

@Slf4j
@AllArgsConstructor
public class FormUrlDecoder<T> {
    @AllArgsConstructor
    private static class Decoder {
        private final Item item;

        public void decode(BufferedReader reader) throws IOException {
            while (true) {
                String line = reader.readLine();
                if (line == null)
                    break;
                decodeLine(line);
            }
        }

        private void decodeLine(String line) {
            log.debug("decode line: {}", line);
            for (String assignment : line.split("&")) {
                decodeAssignment(assignment);
            }
        }

        private void decodeAssignment(String assignment) {
            String[] split = assignment.split("=");
            Trait trait = item.trait(split[0]);
            Item value = Items.newItem(split[1]);
            item.write(trait, value);
        }
    }

    private final Class<T> type;

    public T read(BufferedReader reader) throws IOException {
        log.debug("decoding {}", type);
        try {
            T instance = newInstance();
            Decoder decoder = new Decoder(Items.newItem(instance));
            decoder.decode(reader);
            return instance;
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    private T newInstance() throws ReflectiveOperationException {
        Constructor<T> constructor = type.getDeclaredConstructor();
        constructor.setAccessible(true);
        return constructor.newInstance();
    }
}
