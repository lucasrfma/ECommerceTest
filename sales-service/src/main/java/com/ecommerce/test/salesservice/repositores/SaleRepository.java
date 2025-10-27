package com.ecommerce.test.salesservice.repositores;

import com.ecommerce.test.salesservice.entities.Sale;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SaleRepository extends JpaRepository<Sale, Long> {
    List<Sale> findAllByAccountId(Long accountId);
}
