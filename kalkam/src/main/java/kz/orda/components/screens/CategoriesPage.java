package kz.orda.components.screens;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.filter.SimpleStringFilter;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.*;
import kz.orda.jpa.Category;
import kz.orda.services.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;

/**
 * Created by Ali on 03.01.2016.
 */
@SpringView(name = "categories")
public class CategoriesPage extends VerticalLayout implements View {

    private BeanItemContainer beanItemContainer;

    @Autowired
    private CategoryService categoryService;

    @PostConstruct
    public void init() {
        TextField searchTextField = new TextField();
        searchTextField.setInputPrompt("Название категории");
        Button searchButton = new Button(FontAwesome.SEARCH);
        searchButton.setDescription("Найти");
        searchButton.addClickListener(clickEvent -> addFilter(searchTextField.getValue()));
        Button editButton = new Button(FontAwesome.EDIT);
        editButton.setDescription("Редактировать");
        editButton.setEnabled(false);
        Button addButton = new Button(FontAwesome.PLUS);
        addButton.setDescription("Добавить");
        addButton.addClickListener(clickEvent -> UI.getCurrent().getNavigator().navigateTo("category"));
        HorizontalLayout toolbar = new HorizontalLayout(searchTextField, searchButton, editButton, addButton);
        toolbar.setSpacing(true);
        beanItemContainer = new BeanItemContainer(Category.class);
        beanItemContainer.addAll(categoryService.getAll());
        Table table = new Table("", beanItemContainer);
        editButton.addClickListener(clickEvent -> {
            if (table.getValue() != null) {
                UI.getCurrent().getNavigator().navigateTo("category/" + ((Category) table.getValue()).getId());
            }
        });
        table.addValueChangeListener(valueChangeEvent -> {
            editButton.setEnabled(table.getValue() != null);
        });
        table.setVisibleColumns("name");
        table.setColumnHeaders("Название");
        table.setSizeFull();
        VerticalLayout verticalLayout = new VerticalLayout(toolbar, table);
        verticalLayout.setWidth("60%");
        verticalLayout.setMargin(true);
        verticalLayout.setHeight("100%");
        verticalLayout.setSpacing(true);
        verticalLayout.setExpandRatio(table, 1f);
        setSizeFull();
        addComponent(verticalLayout);
        setComponentAlignment(verticalLayout, Alignment.TOP_CENTER);
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent viewChangeEvent) {

    }

    private void addFilter(String filter) {
        beanItemContainer.removeAllContainerFilters();
        if (filter != null && !filter.isEmpty()) {
            beanItemContainer.addContainerFilter(new SimpleStringFilter("name", filter, true, false));
        }
    }
}
