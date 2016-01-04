package kz.orda.services;

import kz.orda.jpa.Product;
import org.springframework.data.domain.Sort;
import org.springframework.instrument.classloading.LoadTimeWeaver;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by Ali on 27.12.2015.
 */
public interface ProductService {
    void save(Product product);
    Product find(Long id);
    Product findByCode(String code);
    List<Product> getAll();
    List<Product> getAll(Sort sort);
    int size();

    List<Product> page(int start, int number);
    List<Product> page(int start, int number, Sort sort);

    List<Product> pageWhereName(String name, int start, int size);
    List<Product> pageWhereName(String name, int start, int size, Sort sort);
    List<Product> pageWhereCode(String code, int start, int size);
    List<Product> pageWhereCode(String code, int start, int size, Sort sort);

    Long countByName(String name);
    Long countByCode(String code);
}
