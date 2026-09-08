package com.example.api.service;

import com.example.api.entity.Product;

import java.util.List;

public interface ProductService {
    public void insert(Product prod);

    public List<Product> fetchProd();

    public void modifyProd(String id, Product prod);

    public void delProd(String id);
}
