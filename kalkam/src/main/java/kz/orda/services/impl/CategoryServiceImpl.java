package kz.orda.services.impl;

import kz.orda.dao.CategoryRepository;
import kz.orda.jpa.Category;
import kz.orda.services.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by Ali on 27.12.2015.
 */
@Service("categoryService")
public class CategoryServiceImpl implements CategoryService{

    @Autowired
    private CategoryRepository categoryRepository;


    @Override
    public void save(Category category) {
        categoryRepository.saveAndFlush(category);
    }

    @Override
    public Category find(Long id) {
        return categoryRepository.findOne(id);
    }

    @Override
    public List<Category> getAll() {
        return categoryRepository.findAll();
    }

    @Override
    public List<Category> getAll(Sort sort) {
        return categoryRepository.findAll(sort);
    }
}
