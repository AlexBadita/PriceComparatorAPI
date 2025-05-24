package com.example.price_comparator.utils;

import java.time.LocalDate;

public class FileNameExtractor {
    public static class StoreAndDate {
        public final String store;
        public final LocalDate entryDate;

        public StoreAndDate(String store, LocalDate entryDate) {
            this.store = store;
            this.entryDate = entryDate;
        }
    }

    public static StoreAndDate extract(String filename){
        // Remove .csv extension
        String name = filename.replace(".csv", "");
        String[] parts = name.split("_");

        if(parts.length == 3 && "discounts".equals(parts[1])){
            // Handle discount file format: <store>_discounts_<entry-date>
            return new StoreAndDate(parts[0], LocalDate.parse(parts[2]));
        } else if(parts.length == 2) {
            // Handle file format: <store>_<date>
            return new StoreAndDate(parts[0], LocalDate.parse(parts[1]));
        } else {
            throw new IllegalArgumentException("Invalid filename format for " + filename);
        }
    }
}
