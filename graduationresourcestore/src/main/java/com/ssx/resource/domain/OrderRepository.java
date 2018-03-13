package com.ssx.resource.domain;

import com.ssx.common.domain.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
   List<Order> findByUser(User user, Pageable pageable);

   Order findByUserAndResource(User user, Resource resource);

}
