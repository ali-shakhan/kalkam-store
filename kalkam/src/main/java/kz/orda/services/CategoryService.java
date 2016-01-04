package kz.orda.services;

import kz.orda.jpa.Category;
import org.springframework.data.domain.Sort;

import java.util.List;

/**
 * Created by Ali on 27.12.2015.
 */
public interface CategoryService {
    void save(Category category);
    Category find(Long id);
    List<Category> getAll();
    List<Category> getAll(Sort sort);
}
