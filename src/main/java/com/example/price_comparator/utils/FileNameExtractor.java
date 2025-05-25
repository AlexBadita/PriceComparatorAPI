package com.example.price_comparator.utils;

import java.time.LocalDate;

/**
 * Utility class for extracting store name and entry date from CSV filenames.
 *
 * Supported formats:
 * <store-name>_<entry-date>.csv (e.g. "kaufland_2025-05-01.csv")
 * <store-name>_discounts_<entry-date>.csv (e.g. "kaufland_discounts_2025-05-01.csv")
 */
public final class FileNameExtractor {

    // Inner class representing extracted metadata
    public static class StoreAndDate {
        public final String store;
        public final LocalDate entryDate;

        public StoreAndDate(String store, LocalDate entryDate) {
            this.store = store;
            this.entryDate = entryDate;
        }
    }

    /**
     * Extracts store and entry date from a supported filename format.
     *
     * @param filename the name of the CSV file
     * @return a StoreAndDate class containing the store name and entry date
     * @throws IllegalArgumentException if the filename doesn't match expected patterns
     */
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
