package com.example.price_comparator.service;

import com.example.price_comparator.dto.csv.BaseCSVEntry;
import com.example.price_comparator.dto.csv.DiscountCSVEntry;
import com.example.price_comparator.dto.csv.PriceCSVEntry;
import com.example.price_comparator.utils.FileNameExtractor;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.HeaderColumnNameMappingStrategy;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * Service for parsing CSV files from the configured directories and mapping them
 * to DTOs (PriceCSVEntry and DiscountCSVEntry). Automatically assigns store name
 * and entry date based on the filename.
 */
@Service
public class CSVParserService {

    private final ResourceLoader resourceLoader;

    public CSVParserService(ResourceLoader resourceLoader){
        this.resourceLoader = resourceLoader;
    }

    /**
     * Parses all price CSV files from the data/prices directory and returns the parsed entries.
     *
     * @return list of parsed PriceCSVEntry objects
     * @throws IOException if an I/O error occurs during file access
     */
    public List<PriceCSVEntry> parseAllPriceCSVFiles() throws IOException {
        return parseCSVFilesFromDirectory("classpath:data/prices", PriceCSVEntry.class);
    }

    /**
     * Parses all discount CSV files from the data/discounts directory and returns the parsed entries.
     *
     * @return list of parsed DiscountCSVEntry objects
     * @throws IOException if an I/O error occurs during file access
     */
    public List<DiscountCSVEntry> parseAllDiscountCSVFiles() throws IOException {
        return parseCSVFilesFromDirectory("classpath:data/discounts", DiscountCSVEntry.class);
    }

    /**
     * Parses all CSV files from the given directory and maps them to the specified DTO class.
     *
     * @param directoryPath the classpath directory containing CSV files
     * @param entryClass the target class to map each CSV entry to
     * @return list of parsed DTO entries
     * @param <T> type parameter extending BaseCSVEntry
     * @throws IOException if an I/O error occurs while reading files
     */
    private <T extends BaseCSVEntry> List<T> parseCSVFilesFromDirectory(String directoryPath, Class<T> entryClass) throws IOException {
        List<T> allEntries = new ArrayList<>();
        Resource resource = resourceLoader.getResource(directoryPath);
        Path dir = Paths.get(resource.getURI());

        try (Stream<Path> paths = Files.list(dir)) {
            paths.filter(path -> path.toString().endsWith(".csv"))
                    .forEach(csvFile -> {
                        try {
                            allEntries.addAll(parseCSVFile(csvFile, entryClass));
                        } catch (Exception e) {
                            throw new RuntimeException("Failed to parse " + csvFile, e);
                        }
                    });
        }

        return allEntries;
    }

    /**
     * Parses a single CSV file and maps its rows to the specified DTO class.
     * Automatically sets the store and entry date from the filename.
     *
     * @param filePath the path to the CSV file
     * @param entryClass the target class to map each row to
     * @return list of parsed entries with metadata from the filename
     * @param <T> type parameter extending BaseCSVEntry
     * @throws Exception if parsing fails
     */
    private <T extends BaseCSVEntry> List<T> parseCSVFile(Path filePath, Class<T> entryClass) throws Exception {
        System.out.println("Parsing CSV file " + filePath);

        try (Reader reader = Files.newBufferedReader(filePath)) {
            // Extract store and date from filename
            FileNameExtractor.StoreAndDate storeAndDate = FileNameExtractor.extract(filePath.getFileName().toString());

            // Configure CSV parsing
            HeaderColumnNameMappingStrategy<T> strategy = new HeaderColumnNameMappingStrategy<>();
            strategy.setType(entryClass);
            CsvToBean<T> csvToBean = new CsvToBeanBuilder<T>(reader)
                    .withMappingStrategy(strategy)
                    .withSeparator(';')
                    .withIgnoreLeadingWhiteSpace(true)
                    .build();

            List<T> entries = csvToBean.parse();

            // Set store and date for all entries
            entries.forEach(entry -> {
                entry.setStore(storeAndDate.store);
                entry.setEntryDate(storeAndDate.entryDate);
            });

            return entries;
        }
    }
}
