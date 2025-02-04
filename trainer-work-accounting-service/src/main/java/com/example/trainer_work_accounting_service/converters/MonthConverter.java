package com.example.trainer_work_accounting_service.converters;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.time.Month;

@Converter(autoApply = true)
public class MonthConverter implements AttributeConverter<Month,Integer> {
    @Override
    public Integer convertToDatabaseColumn(Month attribute) {
        return attribute!= null ? attribute.getValue() : null;
    }

    @Override
    public Month convertToEntityAttribute(Integer dbData) {
        return dbData != null ? Month.of(dbData) : null;
    }
}
