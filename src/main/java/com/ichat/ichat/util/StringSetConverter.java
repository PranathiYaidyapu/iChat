package com.ichat.ichat.util;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Converter
public class StringSetConverter implements AttributeConverter<Set<String>, String> {

    private static final String SPLIT_CHAR = ",";

    @Override
    public String convertToDatabaseColumn(Set<String> attribute) {
        return attribute != null ? String.join(SPLIT_CHAR, attribute) : "";
    }

    @Override
    public Set<String> convertToEntityAttribute(String dbData) {
        return dbData != null && !dbData.isEmpty()
                ? Arrays.stream(dbData.split(SPLIT_CHAR)).collect(Collectors.toSet())
                : Set.of();
    }
}
