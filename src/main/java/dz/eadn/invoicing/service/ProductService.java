/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dz.eadn.invoicing.service;

import dz.eadn.invoicing.model.Product;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author NAIMI Mohamed
 */
@Named
//@ApplicationScoped
@ViewScoped
public class ProductService implements Serializable{
        
    @PersistenceContext(unitName = "ADMIN_PU")
    private EntityManager entityManager;

    private List<Product> products;
    private Map<Long, Product> productsAsMap;

    @PostConstruct
    public void init() {
        products = new ArrayList<>();
        products = this.getListProducts();
        Collections.sort(products, (Product c1, Product c2) -> c1.getName().compareTo(c2.getName()));
    }

    public List<Product> getListProducts() {
            return this.entityManager
                      .createQuery("Select p from Product p", Product.class)
                      .getResultList();
    }
       
    public List<Product> getProducts() {
        return new ArrayList<>(products);
    }

    public Map<Long, Product> getProductsAsMap() {
        if (productsAsMap == null) {
            productsAsMap = getProducts().stream().collect(Collectors.toMap( Product::getId, Function.identity()));
        }
        return productsAsMap;
    }

}
