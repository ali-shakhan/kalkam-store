package kz.orda.dao;

import kz.orda.jpa.Product;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by Ali on 05.12.2015.
 */
public interface ProductRepository extends JpaRepository<Product, Long> {
    Product findByCode(String code);
    List<Product> findByNameContaining(String name, Pageable pageable);
    List<Product> findByCode(String code, Pageable pageable);
    Long countByNameContaining(String name);
    Long countByCode(String code);
}
