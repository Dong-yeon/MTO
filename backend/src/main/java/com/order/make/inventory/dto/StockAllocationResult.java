package com.order.make.inventory.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StockAllocationResult {
	private final Long productId;
	private final int requestedQuantity;
	private final int allocatedQuantity;  // 실제 차감된 수량
	private final int deficitQuantity;     // 부족한 수량
	private final boolean fullyAllocated;  // 전부 할당되었는가?
}
