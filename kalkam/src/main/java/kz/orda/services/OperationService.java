package kz.orda.services;

import kz.orda.jpa.Operation;
import kz.orda.jpa.OperationType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Date;
import java.util.List;

/**
 * Created by Ali on 27.12.2015.
 */
public interface OperationService {
    void save(Operation operation);
    Operation find(Long id);
    List<Operation> getAll();
    List<Operation> getAll(Sort sort);
    List<Operation> findByDateBetween(OperationType operationType, Date startDate, Date endDate, int start, int size);
    List<Operation> page(OperationType operationType, int start, int size);
    int size(OperationType operationType);

    List<Operation> page(OperationType operationType, int start, int size, Sort sort);

    List<Operation> findByDateBetween(OperationType operationType, Date startValue, Date endValue, int start, int size, Sort sort);
    List<Operation> findByDateBetween(OperationType operationType, Date startValue, Date endValue);
    int countByDateBetween(OperationType operationType, Date startDate, Date endDate);
}
