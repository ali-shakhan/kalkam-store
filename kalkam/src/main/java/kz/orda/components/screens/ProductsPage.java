package kz.orda.components.screens;

import com.vaadin.data.util.filter.SimpleStringFilter;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.*;
import kz.orda.components.ProductBeanQuery;
import kz.orda.components.widgetset.CodeCatcher;
import kz.orda.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.addons.lazyquerycontainer.BeanQueryFactory;
import org.vaadin.addons.lazyquerycontainer.LazyQueryContainer;
import org.vaadin.addons.lazyquerycontainer.LazyQueryDefinition;
import org.vaadin.addons.lazyquerycontainer.QueryDefinition;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Ali on 02.01.2016.
 */
@SpringView(name = "products")
public class ProductsPage extends VerticalLayout implements View {

    @Autowired
    private ProductService productService;

    private LazyQueryContainer container;

    @PostConstruct
    public void init() {
        setSizeFull();
        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setMargin(new MarginInfo(true, false, false, false));
        verticalLayout.setSpacing(true);
        verticalLayout.setWidth("80%");
        verticalLayout.setHeight("100%");
        TextField nameFilterTextField = new TextField();
        nameFilterTextField.setInputPrompt("Название товара");
        nameFilterTextField.setImmediate(true);
        TextField codeFilterTextField = new TextField();
        codeFilterTextField.setInputPrompt("Код товара");
        codeFilterTextField.setImmediate(true);
        nameFilterTextField.addValueChangeListener(valueChangeEvent -> {
            codeFilterTextField.clear();
        });
        codeFilterTextField.addValueChangeListener(valueChangeEvent -> {
            nameFilterTextField.clear();
        });
        CodeCatcher codeCatcher = new CodeCatcher(this);
        codeCatcher.setStartCharacter('-');
        codeCatcher.setEndCharacter('*');
        codeCatcher.addListener(event -> {
            codeFilterTextField.setValue(event.getCode());
        });
        Button searchButton = new Button(FontAwesome.SEARCH);
        searchButton.setDescription("Найти");
        searchButton.addClickListener(clickEvent -> {
            if (!nameFilterTextField.getValue().isEmpty()) {
                addNameFilter(nameFilterTextField.getValue(), "name");
            } else {
                addNameFilter(codeFilterTextField.getValue(), "code");
            }
        });
        ShortcutListener enterListener = new ShortcutListener("enter", ShortcutAction.KeyCode.ENTER, null) {
            @Override
            public void handleAction(Object o, Object o1) {
                searchButton.click();
            }
        };
        nameFilterTextField.addFocusListener(focusEvent -> nameFilterTextField.addShortcutListener(enterListener));
        nameFilterTextField.addBlurListener(blurEvent -> nameFilterTextField.removeShortcutListener(enterListener));
        codeFilterTextField.addFocusListener(focusEvent -> codeFilterTextField.addShortcutListener(enterListener));
        codeFilterTextField.addBlurListener(blurEvent -> codeFilterTextField.removeShortcutListener(enterListener));
        Button editButton = new Button(FontAwesome.EDIT);
        editButton.setDescription("Редактировать");
        editButton.setEnabled(false);
        Button addButton = new Button(FontAwesome.PLUS);
        addButton.setDescription("Добавить");
        addButton.addClickListener(clickEvent -> UI.getCurrent().getNavigator().navigateTo("product"));
        HorizontalLayout toolBar = new HorizontalLayout(nameFilterTextField, codeFilterTextField, searchButton, editButton, addButton);
        toolBar.setSpacing(true);
        verticalLayout.addComponent(toolBar);
        BeanQueryFactory<ProductBeanQuery> queryFactory = new BeanQueryFactory<>(ProductBeanQuery.class);
        Map<String, Object> queryConfiguration = new HashMap<>();
        queryConfiguration.put("productService", productService);
        queryFactory.setQueryConfiguration(queryConfiguration);
        LazyQueryDefinition queryDefinition = new LazyQueryDefinition(true, 50, "code");
        queryDefinition.setMaxNestedPropertyDepth(1);
        container = new LazyQueryContainer(queryDefinition, queryFactory);
        container.addContainerProperty("name", String.class, "", true, true);
        container.addContainerProperty("code", String.class, "", true, true);
        container.addContainerProperty("price", Double.class, "", true, true);
        container.addContainerProperty("amount", Double.class, "", true, true);
        container.addContainerProperty("unit", String.class, "", true, true);
        container.addContainerProperty("category.name", String.class, "", true, true);
        Table productsTable = new Table("", container);
        editButton.addClickListener(clickEvent -> {
            if (productsTable.getValue() != null) {
                UI.getCurrent().getNavigator().navigateTo("product/" + productsTable.getValue());
            }
        });
        productsTable.addValueChangeListener(valueChangeEvent -> editButton.setEnabled(productsTable.getValue() != null));
        productsTable.setColumnHeaders("Название", "Код", "Цена", "Колич./масса", "Ед. измерения", "Категория");
        productsTable.setSelectable(true);
        productsTable.setSizeFull();
        verticalLayout.addComponent(productsTable);
        verticalLayout.setExpandRatio(productsTable, 1f);
        addComponent(verticalLayout);
        setComponentAlignment(verticalLayout, Alignment.TOP_CENTER);
    }

    private void addNameFilter(String filter, String propertyId) {
        container.removeAllContainerFilters();
        if (!filter.isEmpty()) {
            container.addContainerFilter(new SimpleStringFilter(propertyId, filter, true, false));
        }
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent viewChangeEvent) {

    }
}
