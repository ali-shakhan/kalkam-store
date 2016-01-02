package kz.orda.components;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.annotations.Widgetset;
import com.vaadin.navigator.Navigator;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.spring.navigator.SpringViewProvider;
import com.vaadin.ui.*;
import kz.orda.jpa.Category;
import kz.orda.jpa.Product;
import kz.orda.jpa.ProductUnit;
import kz.orda.services.CategoryService;
import kz.orda.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.annotation.WebServlet;

/**
 * Created by Ali on 05.12.2015.
 */
@SpringUI
@Theme("valo")
@Widgetset("kz.orda.components.widgetset.CodecatcherWidgetset")
public class MainUI extends UI {

    @Autowired
    private SpringViewProvider viewProvider;

    @Override
    protected void init(VaadinRequest vaadinRequest) {
        final VerticalLayout root = new VerticalLayout();
        root.setSizeFull();
        setContent(root);
        MenuBar menuBar = new MenuBar();
        menuBar.setSizeUndefined();
        menuBar.addItem("Касса", (MenuBar.Command) menuItem -> UI.getCurrent().getNavigator().navigateTo(""));
        menuBar.addItem("Все товары", (MenuBar.Command) menuItem -> UI.getCurrent().getNavigator().navigateTo("products"));
        VerticalLayout verticalLayout = new VerticalLayout(menuBar);
        verticalLayout.setSizeUndefined();
        verticalLayout.setMargin(true);
        root.addComponent(verticalLayout);
        root.setComponentAlignment(verticalLayout, Alignment.TOP_CENTER);
        final Panel viewContainer = new Panel();
        viewContainer.setSizeFull();
        root.addComponent(viewContainer);
        root.setExpandRatio(viewContainer, 1.0f);
        Navigator navigator = new Navigator(this, viewContainer);
        navigator.addProvider(viewProvider);
        navigator.navigateTo("");
    }

    @WebServlet(value = "/*", asyncSupported = true)
    @VaadinServletConfiguration(productionMode = false, ui = MainUI.class, widgetset = "kz.orda.components.widgetset.CodecatcherWidgetset")
    public static class Servlet extends VaadinServlet {
    }
}