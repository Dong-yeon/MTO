package com.order.make.catalog;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface BomRepository extends JpaRepository<Bom, Long> {

    List<Bom> findByParentProduct(Product parentProduct);

    List<Bom> findByComponentProduct(Product componentProduct);
}
