package kz.orda.services.impl;

import kz.orda.dao.OperationRepository;
import kz.orda.jpa.Operation;
import kz.orda.jpa.OperationType;
import kz.orda.services.ChunkRequest;
import kz.orda.services.OperationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * Created by Ali on 27.12.2015.
 */
@Service("operationService")
public class OperationServiceImpl implements OperationService {
    @Autowired
    private OperationRepository operationRepository;

    @Override
    public void save(Operation operation) {
        operationRepository.saveAndFlush(operation);
    }

    @Override
    public Operation find(Long id) {
        return operationRepository.getOne(id);
    }

    @Override
    public List<Operation> getAll() {
        return operationRepository.findAll();
    }

    @Override
    public List<Operation> getAll(Sort sort) {
        return operationRepository.findAll(sort);
    }

    @Override
    public List<Operation> findByDateBetween(OperationType operationType, Date startDate, Date endDate, int start, int size) {
        return operationRepository.findByOperationTypeAndDateBetween(operationType, startDate, endDate, new ChunkRequest(start, size));
    }

    @Override
    public List<Operation> page(OperationType operationType, int start, int size) {
        return operationRepository.findAll(new ChunkRequest(start, size)).getContent();
    }

    @Override
    public int size(OperationType operationType) {
        return operationRepository.countByOperationType(operationType).intValue();
    }

    @Override
    public List<Operation> page(OperationType operationType, int start, int size, Sort sort) {
        return operationRepository.findByOperationType(operationType, new ChunkRequest(start, size, sort));
    }

    @Override
    public List<Operation> findByDateBetween(OperationType operationType, Date startValue, Date endValue, int start, int size, Sort sort) {
        return operationRepository.findByOperationTypeAndDateBetween(operationType, startValue, endValue, new ChunkRequest(start, size, sort));
    }

    @Override
    public List<Operation> findByDateBetween(OperationType operationType, Date startValue, Date endValue) {
        return operationRepository.findByOperationTypeAndDateBetween(operationType, startValue, endValue);
    }

    @Override
    public int countByDateBetween(OperationType operationType, Date startDate, Date endDate) {
        return operationRepository.countByOperationTypeAndDateBetween(operationType, startDate, endDate).intValue();
    }
}
