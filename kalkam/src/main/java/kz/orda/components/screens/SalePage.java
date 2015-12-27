package kz.orda.components.screens;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.FieldEvents;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutAction.ModifierKey;
import com.vaadin.event.ShortcutListener;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import kz.orda.components.ProductBeanQuery;
import kz.orda.components.widgetset.CodeCatcher;
import kz.orda.components.widgetset.client.codecatcher.CodeCatchedEvent;
import kz.orda.jpa.Operation;
import kz.orda.jpa.Product;
import kz.orda.jpa.ProductUnit;
import kz.orda.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.addons.lazyquerycontainer.BeanQueryFactory;
import org.vaadin.addons.lazyquerycontainer.LazyQueryContainer;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

/**
 * Created by Ali on 27.12.2015.
 */
@SpringView(name = "sale")
public class SalePage extends VerticalLayout implements View {

    private TextField codeTextField;

    @Autowired
    private ProductService productService;

    private VerticalLayout currentProductLayout;

    private BeanItemContainer beanItemContainer;

    @PostConstruct
    public void init() {
        setSizeFull();
        setMargin(true);
        codeTextField = new TextField("Код товара");
        codeTextField.setImmediate(true);
        codeTextField.setWidth("100%");
        CodeCatcher codeCatcher = new CodeCatcher(this);
        codeCatcher.setStartCharacter('*');
        codeCatcher.setStartCharacter('*');
        codeCatcher.addListener(new CodeCatcher.CodeCatchedListener() {
            @Override
            public void onCodeCatch(CodeCatchedEvent event) {
                codeTextField.setValue(event.getCode());
            }
        });
        codeTextField.addShortcutListener(new ShortcutListener("EnterOnTextAreaShorcut", ShortcutAction.KeyCode.ENTER, null) {
            @Override
            public void handleAction(Object o, Object o1) {
                Product product = productService.findByCode(codeTextField.getValue());
                if (product != null) {
                    selectProduct(product);
                } else {
                    Notification.show("Товар не найден");
                }
            }
        });
        BeanQueryFactory<ProductBeanQuery> queryFactory = new BeanQueryFactory<>(ProductBeanQuery.class);
        Map<String, Object> queryConfiguration = new HashMap<>();
        queryConfiguration.put("productService", productService);
        queryFactory.setQueryConfiguration(queryConfiguration);
        LazyQueryContainer container = new LazyQueryContainer(queryFactory, "code", 50, true);
        container.addContainerProperty("name", String.class, "");
        ComboBox productComboBox = new ComboBox("Товар");
        productComboBox.setItemCaptionMode(AbstractSelect.ItemCaptionMode.PROPERTY);
        productComboBox.setItemCaptionPropertyId("name");
        productComboBox.setContainerDataSource(container);
        productComboBox.setFilteringMode(FilteringMode.CONTAINS);
        productComboBox.setNullSelectionAllowed(false);
        productComboBox.setWidth("100%");
        productComboBox.addValueChangeListener(valueChangeEvent -> {
            selectProduct(productService.findByCode((String) valueChangeEvent.getProperty().getValue()));
        });

        HorizontalLayout searchWrapper = new HorizontalLayout(codeTextField, productComboBox);
        searchWrapper.setSpacing(true);
        searchWrapper.setWidth("60%");
        currentProductLayout = new VerticalLayout();
        currentProductLayout.setWidth("100%");
        currentProductLayout.setMargin(true);
        currentProductLayout.setSpacing(true);
        currentProductLayout.addComponent(searchWrapper);
        currentProductLayout.setComponentAlignment(searchWrapper, Alignment.TOP_CENTER);


        beanItemContainer = new BeanItemContainer(Operation.class);
        Table selectedProducts = new Table("В корзине", beanItemContainer);
        selectedProducts.setWidth("100%");
        Button completeButton = new Button("Завершить");
        completeButton.setStyleName(ValoTheme.BUTTON_PRIMARY);
        VerticalLayout productsLayout = new VerticalLayout(selectedProducts);
        productsLayout.setSpacing(true);
        productsLayout.setMargin(true);
        productsLayout.setExpandRatio(selectedProducts, 1f);
        VerticalSplitPanel verticalSplitPanel = new VerticalSplitPanel();
        verticalSplitPanel.setSizeFull();
        verticalSplitPanel.setFirstComponent(currentProductLayout);
        verticalSplitPanel.setSecondComponent(productsLayout);
        addComponent(verticalSplitPanel);
        setExpandRatio(verticalSplitPanel, 1f);
        addComponent(completeButton);
        setComponentAlignment(completeButton, Alignment.BOTTOM_RIGHT);
    }

    private void selectProduct(Product product) {
        TextField amountTextField = new TextField();
        amountTextField.addStyleName("align-right");
        if (ProductUnit.кг.equals(product.getUnit())) {
            amountTextField.setCaption("Масса (кг)");
        } else if (ProductUnit.шт.equals(product.getUnit())) {
            amountTextField.setCaption("Количество (шт)");
        }
        amountTextField.setImmediate(true);
        amountTextField.setTextChangeEventMode(AbstractTextField.TextChangeEventMode.EAGER);
        amountTextField.addTextChangeListener((FieldEvents.TextChangeListener) event -> {
            String text = event.getText();
            try {
                if (ProductUnit.шт.equals(product.getUnit())) {
                    amountTextField.setValue(new Integer(text).toString());
                } else if (ProductUnit.кг.equals(product.getUnit())) {
                    amountTextField.setValue(new Double(text).toString());
                }
            } catch (NumberFormatException e) {
            }
        });
        Button addButton = new Button("Добавить");
        addButton.addClickListener(clickEvent -> {
            if (amountTextField.getValue() != null && !amountTextField.getValue().isEmpty()) {
                Operation operation = new Operation();
                operation.setProduct(product);
                operation.setPrice(product.getPrice());
                operation.setDate(new Date());
                operation.setAmount(Double.parseDouble(amountTextField.getValue()));
                beanItemContainer.addItem(operation);
            }
        });

        HorizontalLayout horizontalLayout = new HorizontalLayout(amountTextField, addButton);
        horizontalLayout.setSpacing(true);
        horizontalLayout.setExpandRatio(amountTextField, 1f);
        horizontalLayout.setWidth("60%");
        horizontalLayout.setComponentAlignment(addButton, Alignment.BOTTOM_RIGHT);
        currentProductLayout.addComponent(horizontalLayout);
        currentProductLayout.setComponentAlignment(horizontalLayout, Alignment.TOP_CENTER);
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent viewChangeEvent) {
        codeTextField.focus();
    }
}
