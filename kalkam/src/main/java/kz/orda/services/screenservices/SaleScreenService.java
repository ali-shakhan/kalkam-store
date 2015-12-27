package kz.orda.services.screenservices;

import kz.orda.jpa.Product;
import kz.orda.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by Ali on 27.12.2015.
 */
@Service("saleScreen")
public class SaleScreenService {
    @Autowired
    private ProductService productService;

    public Product findProduct(String code){
        return productService.findByCode(code);
    }

}
