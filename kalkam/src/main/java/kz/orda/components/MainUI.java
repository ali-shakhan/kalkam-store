package kz.orda.components;

import com.vaadin.annotations.Theme;
import com.vaadin.event.ShortcutAction;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.*;
import kz.orda.dao.ProductRepository;
import kz.orda.jpa.Product;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Created by Ali on 05.12.2015.
 */
@SpringUI
@Theme("valo")
public class MainUI extends UI {

    @Autowired
    private ProductRepository productRepository;

    @Override
    protected void init(VaadinRequest vaadinRequest) {
        TextField textField = new TextField("№");
        Button button = new Button("OK");
        button.setClickShortcut(ShortcutAction.KeyCode.ENTER);
        button.addClickListener(clickEvent -> {
            Notification.show(textField.getValue());
        });
        VerticalLayout verticalLayout = new VerticalLayout(textField, button);
        setContent(verticalLayout);
        List<Product> products = productRepository.findAll();
        Label label;
        if (products != null && !products.isEmpty()) {
            label = new Label(products.get(0).getName());
        }else {
            label = new Label("null");
        }
        verticalLayout.addComponent(label);
    }
}