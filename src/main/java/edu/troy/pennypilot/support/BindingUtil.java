package edu.troy.pennypilot.support;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.StringProperty;

public final class BindingUtil {
    private BindingUtil() {}

    public static BooleanBinding isBlank(StringProperty stringProperty) {
        return Bindings.createBooleanBinding(() -> stringProperty.get().isBlank(), stringProperty);
    }

}
