package com.example.trainer_work_accounting_service.converters;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.time.Year;

@Converter(autoApply = true)
public class YearConverter implements AttributeConverter<Year,Integer> {
    @Override
    public Integer convertToDatabaseColumn(Year attribute) {
        return attribute!= null ? attribute.getValue() : null;
    }

    @Override
    public Year convertToEntityAttribute(Integer dbData) {
        return dbData != null ? Year.of(dbData) : null;
    }
}