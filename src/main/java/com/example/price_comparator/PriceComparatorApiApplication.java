package com.example.price_comparator;

import com.example.price_comparator.dto.PriceEntry;
import com.example.price_comparator.service.CSVParserService;
import com.example.price_comparator.service.DBService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

import java.util.List;

@SpringBootApplication
public class PriceComparatorApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(PriceComparatorApiApplication.class, args);
	}

	@Profile("load-data")
	@Bean
	CommandLineRunner loadData(CSVParserService csvParserService, DBService dbService){
		return args -> {
			try {
				List<PriceEntry> entries = csvParserService.parseAllCSVFiles();
				dbService.saveAllPriceEntries(entries);
				System.out.println("[SUCCESS] Loaded " + entries.size() + " price entries.");
			} catch (Exception e){
				System.err.println("[ERROR] Failed to load data: " + e.getMessage());
			}
		};
	}
}
