package kz.orda.components.screens;

import com.vaadin.data.Validator;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import kz.orda.components.widgetset.CodeCatcher;
import kz.orda.jpa.*;
import kz.orda.services.CategoryService;
import kz.orda.services.OperationService;
import kz.orda.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.Date;

/**
 * Created by Ali on 03.01.2016.
 */
@SpringView(name = "product")
public class ProductPage extends VerticalLayout implements View {

    private BeanFieldGroup beanFieldGroup;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ProductService productService;

    @Autowired
    private OperationService operationService;

    private Product currentProduct;
    private Double currentPrice;
    private TextField amountField;

    @PostConstruct
    public void init() {
        beanFieldGroup = new BeanFieldGroup(Product.class);
        beanFieldGroup.setBuffered(true);
        FormLayout formLayout = new FormLayout();

        TextField field = (TextField) beanFieldGroup.buildAndBind("Название", "name");
        field.setRequired(true);
        field.setNullRepresentation("");
        field.setWidth("100%");
        formLayout.addComponent(field);

        TextField codeTextField = (TextField) beanFieldGroup.buildAndBind("Код", "code");
        codeTextField.setRequired(true);
        codeTextField.setNullRepresentation("");
        codeTextField.setWidth("100%");
        formLayout.addComponent(codeTextField);
        CodeCatcher codeCatcher = new CodeCatcher(this);
        codeCatcher.setStartCharacter('-');
        codeCatcher.setEndCharacter('*');
        codeCatcher.addListener(event -> {
            codeTextField.setValue(event.getCode());
        });
        codeTextField.addValidator((Validator) o -> {
            String code = (String) o;
            if (currentProduct.getCode() != null && !currentProduct.getCode().equals(code) || currentProduct.getCode() == null) {
                if (productService.findByCode(code) != null) {
                    throw new Validator.InvalidValueException("Товар с таким кодом уже существует");
                }
            }
        });

        field = (TextField) beanFieldGroup.buildAndBind("Цена", "price");
        field.setRequired(true);
        field.setNullRepresentation("");
        field.setWidth("100%");
        formLayout.addComponent(field);

        amountField = (TextField) beanFieldGroup.buildAndBind("Колич./масса", "amount");
        amountField.setRequired(true);
        amountField.setNullRepresentation("");
        amountField.setWidth("100%");
        formLayout.addComponent(amountField);

        ComboBox unitComboBox = new ComboBox("Ед. измерения");
        unitComboBox.addItem(ProductUnit.кг);
        unitComboBox.addItem(ProductUnit.шт);
        unitComboBox.setNullSelectionAllowed(false);
        unitComboBox.setRequired(true);
        unitComboBox.setWidth("100%");
        beanFieldGroup.bind(unitComboBox, "unit");
        formLayout.addComponent(unitComboBox);

        BeanItemContainer beanItemContainer = new BeanItemContainer(Category.class);
        beanItemContainer.addAll(categoryService.getAll());
        ComboBox categoryComboBox = new ComboBox("Категория");
        categoryComboBox.setContainerDataSource(beanItemContainer);
        categoryComboBox.setItemCaptionMode(AbstractSelect.ItemCaptionMode.PROPERTY);
        categoryComboBox.setItemCaptionPropertyId("name");
        categoryComboBox.setNullSelectionAllowed(false);
        categoryComboBox.setRequired(true);
        categoryComboBox.setWidth("100%");
        beanFieldGroup.bind(categoryComboBox, "category");
        formLayout.addComponent(categoryComboBox);

        Button saveButton = new Button("Сохранить");
        saveButton.setStyleName(ValoTheme.BUTTON_PRIMARY);
        saveButton.addClickListener(clickEvent -> {
            if (beanFieldGroup.isValid()) {
                try {
                    beanFieldGroup.commit();
                    Product product = (Product) beanFieldGroup.getItemDataSource().getBean();
                    if (product.getAmount() > 0) {
                        if (product.getId() == null) {
                            Operation operation = new Operation();
                            operation.setAmount(product.getAmount());
                            operation.setDate(new Date());
                            operation.setOperationType(OperationType.COMING);
                            operation.setPrice(product.getPrice());
                            operation.setProduct(product);
                            productService.save(product);
                            operationService.save(operation);
                        } else if (currentPrice != null && !currentPrice.equals(product.getPrice()) && product.getAmount() > 0) {
                            Operation priceChangedConsumptionOperation = new Operation();
                            priceChangedConsumptionOperation.setAmount(product.getAmount());
                            priceChangedConsumptionOperation.setDate(new Date());
                            priceChangedConsumptionOperation.setOperationType(OperationType.CONSUMPTION);
                            priceChangedConsumptionOperation.setPrice(currentPrice);
                            priceChangedConsumptionOperation.setProduct(product);
                            productService.save(product);
                            operationService.save(priceChangedConsumptionOperation);

                            Operation priceChangedComingOperation = new Operation();
                            priceChangedComingOperation.setAmount(product.getAmount());
                            priceChangedComingOperation.setDate(new Date());
                            priceChangedComingOperation.setOperationType(OperationType.COMING);
                            priceChangedComingOperation.setPrice(product.getPrice());
                            priceChangedComingOperation.setProduct(product);
                            operationService.save(priceChangedComingOperation);
                        }
                    }
                    UI.getCurrent().getNavigator().navigateTo("products");
                } catch (FieldGroup.CommitException e) {
                    e.printStackTrace();
                }
            } else {
                Notification.show("Неверно заполнены обязательные поля(*)");
            }
        });
        Button cancelButton = new Button("Отмена");
        cancelButton.addClickListener(clickEvent -> UI.getCurrent().getNavigator().navigateTo("products"));
        HorizontalLayout saveCancelWrapper = new HorizontalLayout(cancelButton, saveButton);
        saveCancelWrapper.setSpacing(true);
        saveCancelWrapper.setWidth("100%");
        saveCancelWrapper.setComponentAlignment(cancelButton, Alignment.TOP_LEFT);
        saveCancelWrapper.setComponentAlignment(saveButton, Alignment.TOP_RIGHT);
        formLayout.addComponent(saveCancelWrapper);
        formLayout.setWidth("50%");
        addComponent(formLayout);
        setComponentAlignment(formLayout, Alignment.TOP_CENTER);
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent viewChangeEvent) {
        String parameters = viewChangeEvent.getParameters();
        if (parameters != null && !parameters.isEmpty()) {
            currentProduct = productService.findByCode(parameters);
            if (currentProduct != null) {
                beanFieldGroup.setItemDataSource(currentProduct);
                amountField.setReadOnly(true);
                currentPrice = currentProduct.getPrice();
            } else {
                removeAllComponents();
                Notification.show("Товар не найден");
            }
        } else {
            currentProduct = new Product();
            beanFieldGroup.setItemDataSource(currentProduct);
        }
    }
}
