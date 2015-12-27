package kz.orda.dao;

import kz.orda.jpa.Operation;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by Ali on 27.12.2015.
 */
public interface OperationRepository extends JpaRepository<Operation, Long> {
}
