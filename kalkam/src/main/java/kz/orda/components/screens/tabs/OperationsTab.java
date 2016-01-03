package kz.orda.components.screens.tabs;

import com.vaadin.data.util.filter.Between;
import com.vaadin.data.util.filter.Compare;
import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.ui.*;
import kz.orda.components.OperationBeanQuery;
import kz.orda.jpa.Operation;
import kz.orda.jpa.OperationType;
import kz.orda.services.OperationService;
import org.vaadin.addons.lazyquerycontainer.BeanQueryFactory;
import org.vaadin.addons.lazyquerycontainer.LazyQueryContainer;
import org.vaadin.addons.lazyquerycontainer.LazyQueryDefinition;

import java.util.*;
import java.util.Calendar;

/**
 * Created by Ali on 03.01.2016.
 */
public class OperationsTab extends VerticalLayout {

    private LazyQueryContainer container;

    private OperationService operationService;
    private OperationType operationType;
    private Table operationsTable;

    public OperationsTab(OperationType operationType, OperationService operationService) {
        this.operationService = operationService;
        this.operationType = operationType;
        setSizeFull();
        BeanQueryFactory<OperationBeanQuery> queryFactory = new BeanQueryFactory<>(OperationBeanQuery.class);
        Map<String, Object> queryConfiguration = new HashMap<>();
        queryConfiguration.put("operationService", operationService);
        queryFactory.setQueryConfiguration(queryConfiguration);
        LazyQueryDefinition queryDefinition = new LazyQueryDefinition(true, 50, "id");
        queryDefinition.setMaxNestedPropertyDepth(1);
        container = new LazyQueryContainer(queryDefinition, queryFactory);
        container.addDefaultFilter(new Compare.Equal("operationType", operationType));
        container.addContainerProperty("product.name", String.class, "", true, true);
        container.addContainerProperty("price", Double.class, "", true, true);
        container.addContainerProperty("amount", Double.class, "", true, true);
        container.addContainerProperty("date", Double.class, "", true, true);
        operationsTable = new Table("", container);
        operationsTable.setSizeFull();
        operationsTable.setVisibleColumns("product.name", "price", "amount", "date");
        operationsTable.setColumnHeaders("Название товара", "Цена", "Колич./масса", "Дата и время");
        operationsTable.setFooterVisible(true);
        operationsTable.setColumnFooter("amount", "Общая сумма:");
        operationsTable.setColumnFooter("date", "0.0");
        Calendar date = new GregorianCalendar();
        date.set(Calendar.HOUR_OF_DAY, 0);
        date.set(Calendar.MINUTE, 0);
        date.set(Calendar.SECOND, 0);
        date.set(Calendar.MILLISECOND, 0);
        DateField startDateField = new DateField("C:");
        startDateField.setResolution(Resolution.MINUTE);
        startDateField.setValue(date.getTime());
        date.set(Calendar.HOUR_OF_DAY, 23);
        date.set(Calendar.MINUTE, 59);
        date.set(Calendar.SECOND, 59);
        date.set(Calendar.MILLISECOND, 999);
        DateField endDateField = new DateField("До:");
        endDateField.setValue(date.getTime());
        endDateField.setResolution(Resolution.MINUTE);
        addFilter(startDateField.getValue(), endDateField.getValue());
        Button searchButton = new Button(FontAwesome.SEARCH);
        searchButton.setDescription("Найти");
        searchButton.addClickListener(clickEvent -> addFilter(startDateField.getValue(), endDateField.getValue()));
        Button addButton = new Button(FontAwesome.PLUS);
        addButton.setDescription("Добавить");
        addButton.addClickListener(clickEvent -> UI.getCurrent().getNavigator().navigateTo("operation/" + operationType));
        HorizontalLayout datesWrapper = new HorizontalLayout(startDateField, endDateField, searchButton, addButton);
        datesWrapper.setComponentAlignment(searchButton, Alignment.BOTTOM_RIGHT);
        datesWrapper.setComponentAlignment(addButton, Alignment.BOTTOM_RIGHT);
        datesWrapper.setSpacing(true);
        VerticalLayout verticalLayout = new VerticalLayout(datesWrapper, operationsTable);
        verticalLayout.setSizeFull();
        verticalLayout.setExpandRatio(operationsTable, 1f);
        addComponent(verticalLayout);
    }

    private void addFilter(Date startDate, Date endDate) {
        container.removeAllContainerFilters();
        container.addContainerFilter(new Between("date", startDate, endDate));
        List<Operation> operations = operationService.findByDateBetween(operationType, startDate, endDate);
        double totalSum = operations.stream()
                .filter(operation -> operation.getAmount() != null && operation.getPrice() != null)
                .mapToDouble(o -> o.getPrice() * o.getAmount())
                .sum();
        operationsTable.setColumnFooter("date", totalSum + "");
    }
}
