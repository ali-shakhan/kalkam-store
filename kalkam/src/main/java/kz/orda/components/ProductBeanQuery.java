package kz.orda.components;

import com.vaadin.data.Container;
import com.vaadin.data.util.filter.SimpleStringFilter;
import com.vaadin.server.SystemError;
import kz.orda.jpa.Product;
import kz.orda.services.ProductService;
import org.vaadin.addons.lazyquerycontainer.AbstractBeanQuery;
import org.vaadin.addons.lazyquerycontainer.QueryDefinition;

import java.util.List;
import java.util.Map;

/**
 * Created by Ali on 27.12.2015.
 */
public class ProductBeanQuery extends AbstractBeanQuery<Product> {

    public ProductBeanQuery(QueryDefinition queryDefinition, Map<String, Object> queryConfiguration, Object[] sortPropertyIds, boolean[] sortStates) {
        super(queryDefinition, queryConfiguration, sortPropertyIds, sortStates);
    }

    @Override
    protected Product constructBean() {
        return new Product();
    }

    @Override
    public int size() {
        List<Container.Filter> filters = getQueryDefinition().getFilters();
        if (!filters.isEmpty()) {
            SimpleStringFilter stringFilter = (SimpleStringFilter) filters.get(0);
            if ("name".equals(stringFilter.getPropertyId())) {
                return ((ProductService) getQueryConfiguration().get("productService")).countByName(stringFilter.getFilterString()).intValue();
            } else {
                return ((ProductService) getQueryConfiguration().get("productService")).countByCode(stringFilter.getFilterString()).intValue();
            }
        }
        return ((ProductService) getQueryConfiguration().get("productService")).size();
    }

    @Override
    protected List<Product> loadBeans(int start, int size) {
        List<Container.Filter> filters = getQueryDefinition().getFilters();
        if (getSortPropertyIds().length > 0) {
            String sortPropertyId = getSortPropertyIds()[0].toString();
            boolean sortState = getSortStates()[0];
        }

        if (!filters.isEmpty()) {
            SimpleStringFilter stringFilter = (SimpleStringFilter) filters.get(0);
            if ("name".equals(stringFilter.getPropertyId())) {
                return ((ProductService) getQueryConfiguration().get("productService")).pageWhereName(stringFilter.getFilterString(), start, size);
            } else {
                return ((ProductService) getQueryConfiguration().get("productService")).pageWhereCode(stringFilter.getFilterString(), start, size);
            }
        }
        return ((ProductService) getQueryConfiguration().get("productService")).page(start, size);
    }

    @Override
    protected void saveBeans(List<Product> list, List<Product> list1, List<Product> list2) {

    }
}
