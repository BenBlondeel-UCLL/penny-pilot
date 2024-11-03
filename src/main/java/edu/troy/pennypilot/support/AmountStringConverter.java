package edu.troy.pennypilot.support;

import javafx.util.StringConverter;

import java.util.Locale;

public class AmountStringConverter extends StringConverter<Float> {

    @Override
    public String toString(Float number) {
        return number == null ? "" : String.format(Locale.ROOT, "%.2f", number);
    }

    @Override
    public Float fromString(String number) {
        return number == null ? null : Float.parseFloat(number.trim());
    }
}
