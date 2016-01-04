package kz.orda.components;

import com.vaadin.data.Container;
import com.vaadin.data.util.filter.And;
import com.vaadin.data.util.filter.Between;
import com.vaadin.data.util.filter.Compare;
import kz.orda.jpa.Operation;
import kz.orda.jpa.OperationType;
import kz.orda.services.OperationService;
import org.springframework.data.domain.Sort;
import org.vaadin.addons.lazyquerycontainer.AbstractBeanQuery;
import org.vaadin.addons.lazyquerycontainer.QueryDefinition;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by Ali on 03.01.2016.
 */
public class OperationBeanQuery extends AbstractBeanQuery<Operation> {

    public OperationBeanQuery(QueryDefinition queryDefinition, Map<String, Object> queryConfiguration, Object[] sortPropertyIds, boolean[] sortStates) {
        super(queryDefinition, queryConfiguration, sortPropertyIds, sortStates);
    }

    @Override
    protected Operation constructBean() {
        return new Operation();
    }

    @Override
    public int size() {
        List<Container.Filter> filters = getQueryDefinition().getFilters();
        Compare.Equal equal = (Compare.Equal) getQueryDefinition().getDefaultFilters().get(0);
        OperationType operationType = (OperationType) equal.getValue();
        if (!filters.isEmpty()) {
            Between betweenFilter = (Between) filters.get(0);
            return ((OperationService) getQueryConfiguration().get("operationService")).countByDateBetween(operationType, (Date) betweenFilter.getStartValue(), (Date) betweenFilter.getEndValue());
        }
        return ((OperationService) getQueryConfiguration().get("operationService")).size(operationType);
    }

    @Override
    protected List<Operation> loadBeans(int start, int size) {
        List<Container.Filter> filters = getQueryDefinition().getFilters();
        Compare.Equal equal = (Compare.Equal) getQueryDefinition().getDefaultFilters().get(0);
        OperationType operationType = (OperationType) equal.getValue();
        Sort sort = null;
        if (getSortPropertyIds().length > 0) {
            String sortPropertyId = getSortPropertyIds()[0].toString();
            boolean sortState = getSortStates()[0];
            sort = new Sort(sortState ? Sort.Direction.ASC : Sort.Direction.DESC, sortPropertyId);
        }

        if (sort == null) {
            if (!filters.isEmpty()) {
                Between betweenFilter = (Between) filters.get(0);
                return ((OperationService) getQueryConfiguration().get("operationService")).findByDateBetween(operationType, (Date) betweenFilter.getStartValue(), (Date) betweenFilter.getEndValue(), start, size);
            }
            return ((OperationService) getQueryConfiguration().get("operationService")).page(operationType, start, size);
        }
        if (!filters.isEmpty()) {
            Between betweenFilter = (Between) filters.get(0);
            return ((OperationService) getQueryConfiguration().get("operationService")).findByDateBetween(operationType, (Date) betweenFilter.getStartValue(), (Date) betweenFilter.getEndValue(), start, size, sort);
        }
        return ((OperationService) getQueryConfiguration().get("operationService")).page(operationType, start, size, sort);
    }

    @Override
    protected void saveBeans(List<Operation> list, List<Operation> list1, List<Operation> list2) {

    }
}
