package com.order.make.catalog;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
    schema = "catalog",
    name = "bom",
    uniqueConstraints = @UniqueConstraint(
        name = "uq_bom_parent_component",
        columnNames = {"parent_product_id", "component_product_id"}
    )
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Bom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "parent_product_id",
        nullable = false,
        foreignKey = @ForeignKey(name = "fk_bom_parent_product")
    )
    private Product parentProduct; // 완제품

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "component_product_id",
        nullable = false,
        foreignKey = @ForeignKey(name = "fk_bom_component_product")
    )
    private Product componentProduct; // 구성품

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isActive = Boolean.TRUE;
}
