package kz.orda.components.screens;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.converter.StringToDoubleConverter;
import com.vaadin.data.validator.DoubleRangeValidator;
import com.vaadin.event.FieldEvents;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.shared.ui.combobox.FilteringMode;
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

/**
 * Created by Ali on 27.12.2015.
 */
@SpringView(name = "")
public class SalePage extends VerticalLayout implements View {

    private TextField codeTextField;

    @Autowired
    private ProductService productService;

    @Autowired
    private OperationService operationService;

    private VerticalLayout currentProductLayout;

    private BeanItemContainer beanItemContainer;
    private HorizontalLayout addProductLayout;
    private ComboBox productComboBox;
    private Table selectedProducts;
    private Window window;
    private Button toPayButton;
    private TextField totalSumTextField;

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
        currentProductLayout = new VerticalLayout();
        currentProductLayout.setWidth("100%");
        currentProductLayout.setSpacing(true);
        currentProductLayout.addComponent(searchWrapper);
        addProductLayout = new HorizontalLayout();
        currentProductLayout.addComponent(addProductLayout);

        beanItemContainer = new BeanItemContainer(Operation.class);
        selectedProducts = new Table("В корзине", beanItemContainer);
        selectedProducts.setSelectable(true);
        beanItemContainer.addNestedContainerProperty("product.name");
        selectedProducts.addGeneratedColumn("sum", (Table.ColumnGenerator) (table, o, o1) -> {
            Operation operation = (Operation) o;
            double sum = operation.getAmount() * operation.getPrice();
            Label label = new Label(String.valueOf(sum));
            return label;
        });

        selectedProducts.setVisibleColumns("product.name", "price", "amount", "sum");
        selectedProducts.setColumnHeaders("Товар", "Цена", "Колич./масса", "Сумма");
        selectedProducts.setFooterVisible(true);
        selectedProducts.setColumnFooter("amount", "Общая сумма:");
        selectedProducts.addShortcutListener(new ShortcutListener("deleteProductAction", ShortcutAction.KeyCode.DELETE, null) {
            @Override
            public void handleAction(Object o, Object o1) {
                if (selectedProducts.getValue() != null) {
                    Operation operation = (Operation) selectedProducts.getValue();
                    selectedProducts.removeItem(operation);
                    updateTotalSum();
                }
            }
        });
        selectedProducts.setSizeFull();
        toPayButton = new Button("К оплате");
        toPayButton.setClickShortcut(ShortcutAction.KeyCode.END);
        toPayButton.addClickListener(clickEvent -> {
            UI.getCurrent().addWindow(window);
            totalSumTextField.setReadOnly(false);
            totalSumTextField.setValue(String.valueOf(totalSum));
            totalSumTextField.setReadOnly(true);
        });
        toPayButton.setStyleName(ValoTheme.BUTTON_PRIMARY);
        toPayButton.setEnabled(false);
        Button clearAllButton = new Button("Отчистить все");
        clearAllButton.addClickListener(clickEvent -> {
            beanItemContainer.removeAllItems();
            clearFields();
        });
        HorizontalLayout clearToPayWrapper = new HorizontalLayout(clearAllButton, toPayButton);
        clearToPayWrapper.setSpacing(true);
        VerticalLayout verticalLayout = new VerticalLayout(currentProductLayout, selectedProducts, clearToPayWrapper);
        verticalLayout.setWidth("60%");
        verticalLayout.setHeight("100%");
        verticalLayout.setSpacing(true);
        verticalLayout.setExpandRatio(selectedProducts, 1f);
        verticalLayout.setComponentAlignment(clearToPayWrapper, Alignment.BOTTOM_RIGHT);
        addComponent(verticalLayout);
        setComponentAlignment(verticalLayout, Alignment.TOP_CENTER);
        crateToPayWindow();
    }

    private void crateToPayWindow() {
        window = new Window("Оплата");
        window.setWidth("400px");
        window.setResizable(false);
        window.center();
        window.setModal(true);
        totalSumTextField = new TextField("Всего к оплате");
        totalSumTextField.setReadOnly(true);
        totalSumTextField.setWidth("100%");
        TextField insertionTextField = new TextField("Вносимая сумма");
        insertionTextField.focus();
        insertionTextField.setImmediate(true);
        insertionTextField.setTextChangeEventMode(AbstractTextField.TextChangeEventMode.EAGER);
        insertionTextField.setWidth("100%");
        TextField depositTextField = new TextField("Сдача");
        depositTextField.setWidth("100%");
        depositTextField.setValue("0");
        depositTextField.setReadOnly(true);
        insertionTextField.addTextChangeListener((FieldEvents.TextChangeListener) textChangeEvent -> {
            try {
                if (!textChangeEvent.getText().isEmpty()) {
                    double insertion = Double.parseDouble(textChangeEvent.getText());
                    if (insertion > totalSum) {
                        depositTextField.setReadOnly(false);
                        depositTextField.setValue(String.valueOf(insertion - totalSum));
                        depositTextField.setReadOnly(true);
                    } else {
                        depositTextField.setReadOnly(false);
                        depositTextField.setValue("");
                        depositTextField.setReadOnly(true);
                    }
                }
            } catch (NumberFormatException e) {
                insertionTextField.setValue("");
            }
        });
        Button completeButton = new Button("Завершить");
        completeButton.addClickListener(clickEvent -> {
            complete();
            window.close();
        });
        completeButton.setStyleName(ValoTheme.BUTTON_PRIMARY);
        completeButton.setClickShortcut(ShortcutAction.KeyCode.ENTER);
        Button cancelButton = new Button("Отмена");
        cancelButton.addClickListener(clickEvent -> {
            insertionTextField.setValue("");
            depositTextField.setReadOnly(false);
            depositTextField.setValue("");
            depositTextField.setReadOnly(true);
            window.close();
        });
        HorizontalLayout completeCancelWrapper = new HorizontalLayout(cancelButton, completeButton);
        completeCancelWrapper.setWidth("100%");
        completeCancelWrapper.setComponentAlignment(cancelButton, Alignment.TOP_LEFT);
        completeCancelWrapper.setComponentAlignment(completeButton, Alignment.TOP_RIGHT);
        FormLayout formLayout = new FormLayout(totalSumTextField, insertionTextField, depositTextField, completeCancelWrapper);
        formLayout.setWidth("100%");
        formLayout.setMargin(true);
        window.setContent(formLayout);
    }

    private void complete() {
        beanItemContainer.getItemIds().forEach(o -> {
            Operation operation = (Operation) o;
            Product product = operation.getProduct();
            product.setAmount(product.getAmount() - operation.getAmount());
            productService.save(product);
            operation.setProduct(product);
            operationService.save(operation);
        });
        beanItemContainer.removeAllItems();
        clearFields();
    }

    private void clearFields() {
        codeTextField.setReadOnly(false);
        productComboBox.setReadOnly(false);
        codeTextField.setValue("");
        productComboBox.setValue(null);
        addProductLayout.removeAllComponents();
        updateTotalSum();
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

    private double totalSum = 0;

    private void updateTotalSum() {
        totalSum = 0;
        beanItemContainer.getItemIds().forEach(o -> {
            Operation operation = (Operation) o;
            totalSum += (operation.getAmount() * operation.getPrice());
        });
        toPayButton.setEnabled(totalSum > 0);
        selectedProducts.setColumnFooter("sum", String.valueOf(totalSum));
    }

    private void selectProduct(Product product) {
        if (addProductLayout.getComponentCount() == 0) {
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
                if (!text.isEmpty()) {
                    try {
                        if (ProductUnit.шт.equals(product.getUnit())) {
                            amountTextField.setValue(new Integer(text).toString());
                        } else if (ProductUnit.кг.equals(product.getUnit())) {
                            amountTextField.setValue(new Double(text).toString());
                        }
                    } catch (NumberFormatException e) {
                        amountTextField.setValue("");
                    }
                }
            });
            Button addButton = new Button("Добавить");
            ShortcutListener addShortcutListener = new ShortcutListener("addShortcutListener", ShortcutAction.KeyCode.ENTER, null) {
                @Override
                public void handleAction(Object o, Object o1) {
                    addButton.click();
                }
            };
            amountTextField.addFocusListener(focusEvent -> amountTextField.addShortcutListener(addShortcutListener));
            amountTextField.addBlurListener(blurEvent -> amountTextField.removeShortcutListener(addShortcutListener));
            addButton.addClickListener(clickEvent -> {
                try {
                    double amount = Double.parseDouble(amountTextField.getValue());
                    if (amount <= product.getAmount() && amount > 0) {
                        Operation operation = new Operation();
                        operation.setProduct(product);
                        operation.setPrice(product.getPrice());
                        operation.setDate(new Date());
                        operation.setOperationType(OperationType.SALE);
                        operation.setAmount(amount);
                        beanItemContainer.addItem(operation);
                        clearFields();
                        codeTextField.focus();
                    } else if (amount <= 0) {
                        Notification.show("Объем товара должен быть больше нуля");
                        amountTextField.focus();
                    } else {
                        Notification.show("Максимальный объем товара: " + product.getAmount() + " (" + product.getUnit() + ")");
                        amountTextField.focus();
                    }
                } catch (NumberFormatException e) {
                    Notification.show("Неправильно введен количество товара");
                    amountTextField.focus();
                }
            });
            addButton.setStyleName(ValoTheme.BUTTON_PRIMARY);
            Button cancelButton = new Button("Отмена");
            cancelButton.addClickListener(clickEvent -> {
                clearFields();
            });
            HorizontalLayout addCancelWrapper = new HorizontalLayout(cancelButton, addButton);
            addCancelWrapper.setSpacing(true);
            addProductLayout.addComponent(amountTextField);
            Label amountLabel = new Label("На складе: " + product.getAmount() + " (" + product.getUnit() + ")");
            addProductLayout.addComponent(amountLabel);
            addProductLayout.setComponentAlignment(amountLabel, Alignment.MIDDLE_CENTER);
            addProductLayout.addComponent(addCancelWrapper);
            addProductLayout.setComponentAlignment(addCancelWrapper, Alignment.BOTTOM_RIGHT);
            addProductLayout.setSpacing(true);
            addProductLayout.setWidth("100%");
            amountTextField.focus();
        }
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent viewChangeEvent) {
        codeTextField.focus();
    }
}
