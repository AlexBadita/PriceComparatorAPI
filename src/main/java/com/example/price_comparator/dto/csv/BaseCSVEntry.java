package com.example.price_comparator.dto.csv;

import lombok.Data;

import java.time.LocalDate;

@Data
public abstract class BaseCSVEntry {
    protected String store;
    protected LocalDate entryDate;
}
