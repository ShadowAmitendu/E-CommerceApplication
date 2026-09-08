package com.example.api.service.impl;

import com.example.api.entity.Product;
import com.example.api.repository.ProductRepository;
import com.example.api.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {
    @Autowired
    ProductRepository productRepository;

    public void insert(Product prod) {
        productRepository.save(prod);

    }


    public List<Product> fetchProd() {

        return productRepository.findAll();
    }


    public void modifyProd(String id, Product prod) {
        Product product = productRepository.findById(id).orElseThrow();
        product.setQuantity(prod.getQuantity());
        product.setPrice(prod.getPrice());
        productRepository.save(product);
    }


    public void delProd(String id) {
        productRepository.deleteById(id);
    }

}
