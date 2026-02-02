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

	@Column(nullable = false, unique = true)
	private String code; // ITEM-STELL-01

	private String name; // 강철

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private ProductType type;


	@Column(nullable = false)
	private BigDecimal price; // 단가

	@Column(nullable = false)
	private Integer stockQuantity; // 재고

	@Column
	private String makeToOrder; // 주문생산여부

	@Column
	private Boolean standardProcessTime; // 표준 생산 시간

	@Column
	private Boolean isActive;
}
