package com.tim.concurrentcontrol.controller;

import com.tim.concurrentcontrol.model.Product;
import com.tim.concurrentcontrol.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping("/{id}/purchase")
    public ResponseEntity<Product> purchaseProduct(@PathVariable Long id) {
        productService.purchaseProduct(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProduct(@PathVariable Long id) {
        Product product = productService.findById(id);
        return ResponseEntity.ok(product);
    }

    @PutMapping("/{id}/stock")
    public ResponseEntity<Product> updateProductStock(@PathVariable Long id, @RequestParam int quantity) {
        productService.updateProductStock(id, quantity);
        return ResponseEntity.ok().build();
    }
}