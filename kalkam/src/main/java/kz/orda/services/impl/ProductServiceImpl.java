package kz.orda.services.impl;

import kz.orda.dao.ProductRepository;
import kz.orda.jpa.Product;
import kz.orda.services.ChunkRequest;
import kz.orda.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by Ali on 27.12.2015.
 */
@Service("productService")
public class ProductServiceImpl implements ProductService{

    @Autowired
    private ProductRepository productRepository;

    @Override
    public void save(Product product) {
        productRepository.saveAndFlush(product);
    }

    @Override
    public Product find(Long id) {
        return productRepository.getOne(id);
    }

    @Override
    public Product findByCode(String code) {
        return productRepository.findByCode(code);
    }

    @Override
    public List<Product> getAll() {
        return productRepository.findAll();
    }

    @Override
    public List<Product> getAll(Sort sort) {
        return productRepository.findAll(sort);
    }

    @Override
    public int size() {
        return (int) productRepository.count();
    }

    @Override
    public List<Product> page(int offset, int limit) {
        return productRepository.findAll(new ChunkRequest(offset, limit)).getContent();

    }

    @Override
    public List<Product> page(int start, int size, Sort sort) {
        return productRepository.findAll(new ChunkRequest(start, size ,sort)).getContent();
    }

    @Override
    public List<Product> pageWhereName(String name, int start, int size) {
        return productRepository.findByNameContaining(name, new ChunkRequest(start, size));
    }

    @Override
    public List<Product> pageWhereName(String name, int start, int size, Sort sort) {
        return productRepository.findByNameContaining(name, new ChunkRequest(start, size, sort));
    }

    @Override
    public List<Product> pageWhereCode(String code, int start, int size) {
        return productRepository.findByCode(code, new ChunkRequest(start, size));
    }

    @Override
    public List<Product> pageWhereCode(String code, int start, int size, Sort sort) {
        return productRepository.findByCode(code, new ChunkRequest(start, size, sort));
    }

    @Override
    public Long countByName(String name) {
        return productRepository.countByNameContaining(name);
    }

    @Override
    public Long countByCode(String code) {
        return productRepository.countByCode(code);
    }
}
