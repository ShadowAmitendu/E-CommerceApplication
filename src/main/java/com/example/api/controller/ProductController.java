package com.example.api.controller;

import com.example.api.entity.Product;
import com.example.api.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    @Autowired
    private ProductService productService;

    @PostMapping("/add")
    public String addProduct(@RequestBody Product prod) {
        productService.insert(prod);
        return "Added Successfully!";
    }

    @GetMapping("/view")
    public List<Product> getAllProduct() {
        return productService.fetchProd();
    }

    @PutMapping("/update/{id}")
    public String updateProduct(@PathVariable String id, @RequestBody Product prod) {
        productService.modifyProd(id, prod);
        return "Updated Successfully!";
    }

    @DeleteMapping("/delete/{id}")
    public String deleteProduct(@PathVariable String id) {
        productService.delProd(id);
        return "Deleted Successfully!";
    }
}
