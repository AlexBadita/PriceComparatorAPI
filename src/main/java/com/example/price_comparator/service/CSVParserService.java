package com.example.price_comparator.service;

import com.example.price_comparator.dto.PriceEntry;
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

@Service
public class CSVParserService {
    private final ResourceLoader resourceLoader;

    public CSVParserService(ResourceLoader resourceLoader){
        this.resourceLoader = resourceLoader;
    }

    public List<PriceEntry> parseAllCSVFiles() throws IOException {
        List<PriceEntry> allEntries = new ArrayList<>();
        Resource resource = resourceLoader.getResource("classpath:data/prices");
        Path pricesDir = Paths.get(resource.getURI());

        try (Stream<Path> paths = Files.list(pricesDir)){
            paths.forEach(csvFile -> {
                try {
                    allEntries.addAll(parseCSV(csvFile));
                } catch (Exception e){
                    throw new RuntimeException("Failed to parse " + csvFile, e);
                }
            });
        }

        return allEntries;
    }

    public List<PriceEntry> parseCSV(Path filePath) throws Exception {
        System.out.println("Parsing: " + filePath);

        try (Reader reader = Files.newBufferedReader(filePath)) {
            // Extract store and date from filename
            FileNameExtractor.StoreAndDate storeAndDate = FileNameExtractor.extract(filePath.getFileName().toString());

            // Configure CSV parsing
            HeaderColumnNameMappingStrategy<PriceEntry> strategy = new HeaderColumnNameMappingStrategy<>();
            strategy.setType(PriceEntry.class);

            CsvToBean<PriceEntry> csvToBean = new CsvToBeanBuilder<PriceEntry>(reader)
                    .withMappingStrategy(strategy)
                    .withSeparator(';')
                    .withIgnoreLeadingWhiteSpace(true)
                    .build();

            List<PriceEntry> entries = csvToBean.parse();

            // Set store and date for all entries
            entries.forEach(entry -> {
                entry.setStore(storeAndDate.store);
                entry.setEntryDate(storeAndDate.entryDate);
            });

            return entries;
        }
    }
}
