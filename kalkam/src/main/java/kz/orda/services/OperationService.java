package kz.orda.services;

import kz.orda.jpa.Operation;
import org.springframework.data.domain.Sort;

import java.util.List;

/**
 * Created by Ali on 27.12.2015.
 */
public interface OperationService {
    void save(Operation operation);
    Operation find(Long id);
    List<Operation> getAll();
    List<Operation> getAll(Sort sort);
}
