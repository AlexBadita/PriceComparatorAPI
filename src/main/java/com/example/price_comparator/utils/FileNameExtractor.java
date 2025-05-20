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
        String[] data = filename.split("_");
        if(data.length < 2) {
            throw new IllegalArgumentException("Filename must be in format 'Store_YYYY-MM-DD.csv' or 'Store_Discounts_YYYY-MM-DD.csv");
        }
        return new StoreAndDate(data[0], LocalDate.parse(data[1]));
    }
}
