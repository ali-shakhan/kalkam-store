package kz.orda.components.screens;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import kz.orda.jpa.Category;
import kz.orda.services.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;

/**
 * Created by Ali on 03.01.2016.
 */
@SpringView(name = "category")
public class CategoryPage extends VerticalLayout implements View {

    private Category currentCategory;

    @Autowired
    private CategoryService categoryService;

    private TextField nameTextField;

    @PostConstruct
    public void init() {
        setSizeFull();
        nameTextField = new TextField("Название");
        nameTextField.setRequired(true);
        nameTextField.setNullRepresentation("");
        nameTextField.setWidth("100%");
        Button saveButton = new Button("Сохранить");
        saveButton.setStyleName(ValoTheme.BUTTON_PRIMARY);
        saveButton.addClickListener(clickEvent -> {
            if (nameTextField.isValid()) {
                currentCategory.setName(nameTextField.getValue());
                categoryService.save(currentCategory);
                UI.getCurrent().getNavigator().navigateTo("categories");
            } else {
                Notification.show("Неверно заполнено обязательное поле");
            }
        });
        Button cancelButton = new Button("Отмена");
        cancelButton.addClickListener(clickEvent -> UI.getCurrent().getNavigator().navigateTo("categories"));
        HorizontalLayout horizontalLayout = new HorizontalLayout(cancelButton, saveButton);
        horizontalLayout.setSpacing(true);
        horizontalLayout.setWidth("100%");
        horizontalLayout.setComponentAlignment(cancelButton, Alignment.TOP_LEFT);
        horizontalLayout.setComponentAlignment(saveButton, Alignment.TOP_RIGHT);
        FormLayout formLayout = new FormLayout(nameTextField, horizontalLayout);
        formLayout.setSpacing(true);
        formLayout.setWidth("40%");
        addComponent(formLayout);
        setComponentAlignment(formLayout, Alignment.TOP_CENTER);
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent viewChangeEvent) {
        String parameters = viewChangeEvent.getParameters();
        if (parameters != null && !parameters.isEmpty()) {
            try {
                currentCategory = categoryService.find(Long.parseLong(parameters));
                if (currentCategory == null) {
                    throw new NumberFormatException();
                }
                nameTextField.setValue(currentCategory.getName());
            }catch (NumberFormatException e){
                removeAllComponents();
                Notification.show("Категория не найдена");
            }
        }else{
            currentCategory = new Category();
        }
    }
}
