package com.ssx.resource.domain;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ResourceRepository extends JpaRepository<Resource, Long>{

    Resource findByName(String name);

    List<Resource> findByPriceBetween(Long low, Long high, Pageable pageable);

    List<Resource> findByPriceLessThan(Long price, Pageable pageable);

    List<Resource> findByPriceGreaterThan(Long price, Pageable pageable);

    Resource findByUuid(String uuid);
}
