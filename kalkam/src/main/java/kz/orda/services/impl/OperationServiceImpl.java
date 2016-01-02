package kz.orda.services.impl;

import kz.orda.dao.OperationRepository;
import kz.orda.jpa.Operation;
import kz.orda.services.OperationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

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
}
