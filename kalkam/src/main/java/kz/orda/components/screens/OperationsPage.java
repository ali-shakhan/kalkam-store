package kz.orda.components.screens;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.Page;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.VerticalLayout;
import kz.orda.components.screens.tabs.OperationsTab;
import kz.orda.jpa.Operation;
import kz.orda.jpa.OperationType;
import kz.orda.services.OperationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import javax.annotation.PostConstruct;

/**
 * Created by Ali on 03.01.2016.
 */
@SpringView(name = "operations")
public class OperationsPage extends VerticalLayout implements View {

    @Autowired
    private OperationService operationService;

    private TabSheet tabSheet;
    private OperationsTab salesTab;
    private OperationsTab comingsTab;
    private OperationsTab consumptionsTap;
    @PostConstruct
    public void init() {
        setSizeFull();
        tabSheet = new TabSheet();
        salesTab = new OperationsTab(OperationType.SALE, operationService);
        tabSheet.addTab(salesTab, "Продажи");
        comingsTab = new OperationsTab(OperationType.COMING, operationService);
        tabSheet.addTab(comingsTab, "Приходы");
        consumptionsTap = new OperationsTab(OperationType.CONSUMPTION, operationService);
        tabSheet.addTab(consumptionsTap, "Расходы");
        tabSheet.setWidth("80%");
        tabSheet.setHeight("100%");
        addComponent(tabSheet);
        setExpandRatio(tabSheet, 1f);
        setComponentAlignment(tabSheet, Alignment.TOP_CENTER);
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent viewChangeEvent) {
        String parms = viewChangeEvent.getParameters();
        if(parms!=null&&!parms.isEmpty()){
            switch (parms){
                case "CONSUMPTION":
                    tabSheet.setSelectedTab(consumptionsTap);
                    break;
                case "COMING":
                    tabSheet.setSelectedTab(comingsTab);
                    break;
                default:
                    tabSheet.setSelectedTab(salesTab);
                    break;
            }
        }
    }
}
