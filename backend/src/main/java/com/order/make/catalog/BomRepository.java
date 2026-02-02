package com.order.make.catalog;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface BomRepository extends JpaRepository<Bom, Long> {

    List<Bom> findByParentProductId(Long parentProductId);

    List<Bom> findByChildProductId(Long childProductId);
}
