package com.order.make.config;

import java.math.BigDecimal;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.order.make.catalog.Product;
import com.order.make.catalog.ProductRepository;
import com.order.make.catalog.ProductType;
import com.order.make.inventory.Location;
import com.order.make.inventory.LocationRepository;
import com.order.make.inventory.LocationType;
import com.order.make.inventory.Stock;
import com.order.make.inventory.StockRepository;

@Configuration
public class DataInitializer {

    private static final String DEFAULT_LOCATION_CODE = "PLANT-001";
    private static final String DEFAULT_PRODUCT_CODE = "ITEM-TEST-001";

    @Bean
    CommandLineRunner loadDummyData(
            ProductRepository productRepository,
            LocationRepository locationRepository,
            StockRepository stockRepository
    ) {
        return args -> {
            Product product = productRepository.findByCode(DEFAULT_PRODUCT_CODE)
                    .orElseGet(() -> productRepository.save(Product.builder()
                            .code(DEFAULT_PRODUCT_CODE)
                            .name("테스트 제품")
                            .type(ProductType.FINISHED_GOOD)
                            .price(new BigDecimal("1000.00"))
                            .stockQuantity(0)
                            .makeToOrder(false)
                            .isActive(true)
                            .build()));

            Location location = locationRepository.findByCode(DEFAULT_LOCATION_CODE)
                    .orElseGet(() -> locationRepository.save(Location.builder()
                            .code(DEFAULT_LOCATION_CODE)
                            .type(LocationType.WAREHOUSE)
                            .isActive(true)
                            .build()));

            boolean hasStock = stockRepository.findByProductIdAndLocationId(product.getId(), location.getId()).stream()
                    .findAny()
                    .isPresent();

            if (!hasStock) {
                stockRepository.save(Stock.builder()
                        .productId(product.getId())
                        .locationId(location.getId())
                        .quantity(100)
                        .reservedQuantity(0)
                        .safetyStock(0)
                        .build());
            }
        };
    }
}
