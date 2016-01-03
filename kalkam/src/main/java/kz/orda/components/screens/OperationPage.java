package kz.orda.components.screens;

import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import kz.orda.components.ProductBeanQuery;
import kz.orda.components.widgetset.CodeCatcher;
import kz.orda.jpa.Operation;
import kz.orda.jpa.OperationType;
import kz.orda.jpa.Product;
import kz.orda.jpa.ProductUnit;
import kz.orda.services.OperationService;
import kz.orda.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.addons.lazyquerycontainer.BeanQueryFactory;
import org.vaadin.addons.lazyquerycontainer.LazyQueryContainer;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Created by Ali on 03.01.2016.
 */
@SpringView(name = "operation")
public class OperationPage extends VerticalLayout implements View {
    private TextField codeTextField;
    private ComboBox productComboBox;

    @Autowired
    private ProductService productService;

    @Autowired
    private OperationService operationService;

    private OperationType operationType;

    private VerticalLayout verticalLayout;

    private boolean priceReadOnly;
    private FormLayout formLayout;
    private Panel panel;
    private String backUrl = "operations/";

    @PostConstruct
    public void init() {
        setSizeFull();
        setMargin(true);
        codeTextField = new TextField("Код товара");
        codeTextField.setImmediate(true);
        codeTextField.setWidth("100%");
        CodeCatcher codeCatcher = new CodeCatcher(this);
        codeCatcher.setStartCharacter('-');
        codeCatcher.setEndCharacter('*');
        codeCatcher.addListener(event -> {
            codeTextField.setValue(event.getCode());
            productComboBox.setValue(event.getCode());
            findProduct(event.getCode());
        });
        ShortcutListener enterListener = new ShortcutListener("EnterShortcut", ShortcutAction.KeyCode.ENTER, null) {
            @Override
            public void handleAction(Object o, Object o1) {
                productComboBox.setValue(codeTextField.getValue());
                findProduct(codeTextField.getValue());
            }
        };
        codeTextField.addFocusListener(focusEvent -> codeTextField.addShortcutListener(enterListener));
        codeTextField.addBlurListener(blurEvent -> codeTextField.removeShortcutListener(enterListener));
        BeanQueryFactory<ProductBeanQuery> queryFactory = new BeanQueryFactory<>(ProductBeanQuery.class);
        Map<String, Object> queryConfiguration = new HashMap<>();
        queryConfiguration.put("productService", productService);
        queryFactory.setQueryConfiguration(queryConfiguration);
        LazyQueryContainer container = new LazyQueryContainer(queryFactory, "code", 50, true);
        container.addContainerProperty("name", String.class, "");
        productComboBox = new ComboBox("Товар");
        productComboBox.setItemCaptionMode(AbstractSelect.ItemCaptionMode.PROPERTY);
        productComboBox.setItemCaptionPropertyId("name");
        productComboBox.setContainerDataSource(container);
        productComboBox.setFilteringMode(FilteringMode.CONTAINS);
        productComboBox.setNullSelectionAllowed(false);
        productComboBox.setWidth("100%");
        productComboBox.addValueChangeListener(valueChangeEvent -> {
            if (valueChangeEvent.getProperty() != null && valueChangeEvent.getProperty().getValue() != null) {
                String code = (String) valueChangeEvent.getProperty().getValue();
                codeTextField.setValue(code);
                findProduct(code);
            }
        });

        HorizontalLayout searchWrapper = new HorizontalLayout(codeTextField, productComboBox);
        searchWrapper.setSpacing(true);
        searchWrapper.setWidth("100%");
        formLayout = new FormLayout();
        formLayout.setWidth("100%");
        Button backButton = new Button("Назад");
        backButton.addClickListener(clickEvent -> UI.getCurrent().getNavigator().navigateTo(backUrl));
        verticalLayout = new VerticalLayout(searchWrapper, formLayout, backButton);
        verticalLayout.setWidth("100%");
        verticalLayout.setMargin(true);
        panel = new Panel(verticalLayout);
        panel.setWidth("60%");
        addComponent(panel);
        setComponentAlignment(panel, Alignment.TOP_CENTER);
    }

    private void findProduct(String code) {
        Product product = productService.findByCode(code);
        if (product != null) {
            selectProduct(product);
            codeTextField.setReadOnly(true);
            productComboBox.setReadOnly(true);
        } else {
            Notification.show("Товар не найден");
        }
    }

    private void selectProduct(Product product) {
        if (formLayout.getComponentCount() != 0) {
            return;
        }
        Operation operation = new Operation();
        operation.setProduct(product);
        operation.setDate(new Date());
        operation.setPrice(product.getPrice());
        operation.setOperationType(operationType);
        BeanFieldGroup beanFieldGroup = new BeanFieldGroup(Operation.class);
        beanFieldGroup.setBuffered(true);

        TextField priceTextField = (TextField) beanFieldGroup.buildAndBind("Цена", "price");
        priceTextField.setWidth("100%");
        priceTextField.setRequired(true);
        priceTextField.setNullRepresentation("");
        formLayout.addComponent(priceTextField);

        TextField amountTextField = (TextField) beanFieldGroup.buildAndBind(product.getUnit().equals(ProductUnit.кг) ? "Масса" : "Количество", "amount");
        amountTextField.setWidth("100%");
        amountTextField.setRequired(true);
        amountTextField.setNullRepresentation("");
        formLayout.addComponent(amountTextField);

        DateField dateField = (DateField) beanFieldGroup.buildAndBind("Дата", "date");
        dateField.setResolution(Resolution.MINUTE);
        formLayout.addComponent(dateField);

        beanFieldGroup.setItemDataSource(operation);
        priceTextField.setReadOnly(priceReadOnly);
        amountTextField.focus();
        Button cancelButton = new Button("Отмена");
        cancelButton.addClickListener(clickEvent -> clearFields());
        Button saveButton = new Button("Сохранить");
        saveButton.setStyleName(ValoTheme.BUTTON_PRIMARY);
        saveButton.addClickListener(clickEvent -> {
            if (beanFieldGroup.isValid()) {
                try {
                    beanFieldGroup.commit();
                    if (operationType.equals(OperationType.COMING)) {
                        if (!Objects.equals(product.getPrice(), operation.getPrice())) {
                            Operation priceChangedConsumptionOperation = new Operation();
                            priceChangedConsumptionOperation.setProduct(product);
                            priceChangedConsumptionOperation.setOperationType(OperationType.CONSUMPTION);
                            priceChangedConsumptionOperation.setDate(new Date());
                            priceChangedConsumptionOperation.setPrice(product.getPrice());
                            priceChangedConsumptionOperation.setAmount(product.getAmount());
                            operationService.save(priceChangedConsumptionOperation);
                            operation.setAmount(product.getAmount() + operation.getAmount());
                            product.setAmount(operation.getAmount());
                            product.setPrice(operation.getPrice());
                        } else {
                            product.setAmount(product.getAmount() != null ? product.getAmount() + operation.getAmount() : operation.getAmount());
                        }
                    } else {
                        if (product.getAmount() != null && product.getAmount() > operation.getAmount()) {
                            product.setAmount(product.getAmount() - operation.getAmount());
                        } else {
                            Notification.show("Максимальный объем товара: " + product.getAmount() + " (" + product.getUnit() + ")");
                        }
                    }
                    productService.save(product);
                    operationService.save(operation);
                    UI.getCurrent().getNavigator().navigateTo(backUrl);
                } catch (FieldGroup.CommitException e) {
                    e.printStackTrace();
                }
            } else {
                Notification.show("Неверно заполнены обязательные поля(*)");
            }
        });
        HorizontalLayout saveCancelWrapper = new HorizontalLayout(cancelButton, saveButton);
        saveCancelWrapper.setSpacing(true);
        saveCancelWrapper.setWidth("100%");
        saveCancelWrapper.setComponentAlignment(cancelButton, Alignment.TOP_LEFT);
        saveCancelWrapper.setComponentAlignment(saveButton, Alignment.TOP_RIGHT);
        formLayout.addComponent(saveCancelWrapper);
    }

    private void clearFields() {
        codeTextField.setReadOnly(false);
        productComboBox.setReadOnly(false);
        codeTextField.setValue("");
        productComboBox.setValue(null);
        formLayout.removeAllComponents();
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent viewChangeEvent) {
        String parameters = viewChangeEvent.getParameters();
        if (parameters != null && !parameters.isEmpty()) {
            operationType = OperationType.valueOf(parameters);
            if (operationType == null) {
                removeAllComponents();
                Notification.show("Тип операции не найден");
            } else {
                priceReadOnly = !operationType.equals(OperationType.COMING);
                backUrl += operationType;
                switch (operationType) {
                    case COMING:
                        panel.setCaption("Приход");
                        break;
                    case SALE:
                        panel.setCaption("Продажа");
                        break;
                    case CONSUMPTION:
                        panel.setCaption("Расход");
                        break;
                }
            }
        } else {
            removeAllComponents();
            Notification.show("Тип операции не найден");
        }
    }
}
