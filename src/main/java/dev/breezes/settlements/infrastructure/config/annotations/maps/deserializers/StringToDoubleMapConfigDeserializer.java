package dev.breezes.settlements.infrastructure.config.annotations.maps.deserializers;

import dev.breezes.settlements.infrastructure.config.annotations.maps.MapConfigDeserializationException;

import javax.annotation.Nonnull;

public class StringToDoubleMapConfigDeserializer implements MapConfigDeserializer<Double> {

    @Override
    public Double deserialize(@Nonnull String value) {
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            throw new MapConfigDeserializationException(value, "double");
        }
    }

    @Override
    public Class<Double> getType() {
        return Double.class;
    }

}
