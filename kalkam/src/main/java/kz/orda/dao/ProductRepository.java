package kz.orda.dao;

import kz.orda.jpa.Product;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by Ali on 05.12.2015.
 */
public interface ProductRepository extends JpaRepository<Product, Long> {
}
