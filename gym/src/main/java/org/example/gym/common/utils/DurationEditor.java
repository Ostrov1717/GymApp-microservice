package org.example.gym.common.utils;

import java.beans.PropertyEditorSupport;
import java.time.Duration;

public class DurationEditor extends PropertyEditorSupport {
    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        try {
            setValue(Duration.parse(text));
        } catch (Exception e) {
            if (text.startsWith("P") && text.endsWith("H")) {
                String hours = text.substring(1, text.length() - 1);
                setValue(Duration.ofHours(Long.parseLong(hours)));
            } else {
                throw new IllegalArgumentException("Invalid duration format: " + text, e);
            }
        }
    }
}