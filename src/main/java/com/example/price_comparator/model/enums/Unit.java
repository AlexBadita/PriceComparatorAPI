package com.example.price_comparator.model.enums;

import lombok.Getter;

@Getter
public enum Unit {
    // Weight units
    GRAMS("g", UnitCategory.WEIGHT),
    KILOGRAMS("kg", UnitCategory.WEIGHT),

    // Volume units
    MILLILITERS("ml", UnitCategory.VOLUME),
    LITERS("l", UnitCategory.VOLUME),

    // Countable units
    PIECES("buc", UnitCategory.COUNTABLE),
    ROLLS("role", UnitCategory.COUNTABLE);

    private final String abbreviation;
    private final UnitCategory category;

    Unit(String abbreviation, UnitCategory category) {
        this.abbreviation = abbreviation;
        this.category = category;
    }

    public enum UnitCategory {
        WEIGHT, VOLUME, COUNTABLE
    }

    // Helper method to get enum from abbreviation
    public static Unit fromAbbreviation(String abbr) {
        for (Unit unit : values()) {
            if (unit.abbreviation.equalsIgnoreCase(abbr)) {
                return unit;
            }
        }
        throw new IllegalArgumentException("Unknown unit abbreviation: " + abbr);
    }
}
