package com.example.price_comparator;

import com.example.price_comparator.dto.csv.DiscountCSVEntry;
import com.example.price_comparator.dto.csv.PriceCSVEntry;
import com.example.price_comparator.service.CSVParserService;
import com.example.price_comparator.service.DBService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.dao.DataAccessException;

import java.io.IOException;
import java.util.List;

/**
 * Entry point for the Spring Boot application.
 * Includes a CommandLineRunner that loads CSV data into the database
 * if the 'load-data' Spring profile is active.
 */
@SpringBootApplication
public class PriceComparatorApiApplication {

	private static final Logger logger = LoggerFactory.getLogger(PriceComparatorApiApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(PriceComparatorApiApplication.class, args);
	}

	/**
	 * Loads price and discount data from CSV files into the database
	 * when the 'load-data' Spring profile is active.
	 *
	 * @param csvParserService Service to parse CSV files
	 * @param dbService Service to save data to the database
	 * @return CommandLineRunner to execute the load logic
	 */
	@Profile("load-data")
	@Bean
	CommandLineRunner loadData(CSVParserService csvParserService, DBService dbService){
		return args -> {
			try {
				// Load prices
				List<PriceCSVEntry> priceEntries = csvParserService.parseAllPriceCSVFiles();
				dbService.saveAllEntries(priceEntries);
				logger.info("[SUCCESS] Loaded {} price entries.", priceEntries.size());

				// Load discounts
				List<DiscountCSVEntry> discountEntries = csvParserService.parseAllDiscountCSVFiles();
				dbService.saveAllEntries(discountEntries);
				logger.info("[SUCCESS] Loaded {} discount entries.", discountEntries.size());
			} catch (IOException e) {
				logger.error("[ERROR] IO error while parsing CSV files: {}", e.getMessage(), e);
			} catch (DataAccessException e) {
				logger.error("[ERROR] Database error while saving entries: {}", e.getMessage(), e);
			} catch (Exception e){
				logger.error("[ERROR] Unexpected error during data load: {} ", e.getMessage(), e);
			}
		};
	}
}
