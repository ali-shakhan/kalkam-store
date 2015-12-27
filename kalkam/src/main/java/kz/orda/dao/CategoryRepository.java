package kz.orda.dao;

import kz.orda.jpa.Category;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by Ali on 27.12.2015.
 */
public interface CategoryRepository extends JpaRepository<Category, Long> {
}
