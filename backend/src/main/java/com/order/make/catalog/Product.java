package com.order.make.catalog;

import jakarta.persistence.*;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(schema = "catalog", name = "product")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true, length = 50)
	private String code; // ITEM-STELL-01

	@Column(nullable = false, length = 100)
	private String name; // 강철

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private ProductType type;


	@Column(nullable = false, precision = 15, scale = 2)
	private BigDecimal price; // 단가

	@Column(nullable = false)
	private Integer stockQuantity; // 재고

	@Column(nullable = false)
	@Builder.Default
	private Boolean makeToOrder = Boolean.FALSE; // 주문생산여부

	@Column
	private Integer standardProcessTimeMinutes; // 표준 생산 시간(분)

	@Column(nullable = false)
	@Builder.Default
	private Boolean isActive = Boolean.TRUE;
}
