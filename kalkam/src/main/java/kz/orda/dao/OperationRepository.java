package kz.orda.dao;

import kz.orda.jpa.Operation;
import kz.orda.jpa.OperationType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;

/**
 * Created by Ali on 27.12.2015.
 */
public interface OperationRepository extends JpaRepository<Operation, Long> {
    List<Operation>findByOperationType(OperationType operationType, Pageable pageable);
    Long countByOperationType(OperationType operationType);
    List<Operation> findByOperationTypeAndDateBetween(OperationType operationType, Date startDate, Date endDate, Pageable pageable);
    List<Operation> findByOperationTypeAndDateBetween(OperationType operationType, Date startDate, Date endDate);
    Long countByOperationTypeAndDateBetween(OperationType operationType, Date startDate, Date endDate);
}
