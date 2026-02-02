package com.order.make.inventory;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(schema = "inventory", name = "stock")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Stock {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private Long productId;

	@Column(nullable = false)
	private Long locationId;

	@Column(nullable = false)
	@Builder.Default
	private Integer quantity = 0; // 가용 재고

	@Column(nullable = false)
	@Builder.Default
	private Integer reservedQuantity = 0; // 예약 수량

	@Column(nullable = false)
	@Builder.Default
	private Integer safetyStock = 0; // 안전 수량
}
