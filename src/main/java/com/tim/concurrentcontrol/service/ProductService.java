package com.tim.concurrentcontrol.service;

import com.fasterxml.jackson.databind.node.JsonNodeCreator;
import com.tim.concurrentcontrol.model.Product;
import com.tim.concurrentcontrol.repository.ProductRepository;
import io.github.resilience4j.retry.annotation.Retry;
import jakarta.persistence.OptimisticLockException;
import lombok.extern.slf4j.Slf4j;
import netscape.javascript.JSObject;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Transactional
    @Retryable(value = ObjectOptimisticLockingFailureException.class, maxAttempts = 3, backoff = @Backoff(delay = 1500, maxDelay = 3000, random = true, multiplier = 1.25))
    public void purchaseProduct(Long id) {

            try {
                // Fetch the latest product from the database
                Product product = this.findById(id);

                if (product.getQuantity() > 0) {
                    product.setQuantity(product.getQuantity() - 1);
                    productRepository.save(product);
                    // If save is successful, break the loop
                } else {
                    throw new RuntimeException("Out of stock");
                }
            } catch (OptimisticLockException ex) {
                // Log the exception at info level, we will retry
                log.info("OptimisticLockException occurred, retrying...", ex);
            }

    }

    public Product findById(Long id) {
        return productRepository.findById(id).orElseThrow(() -> new RuntimeException("Product not found"));
    }

    @Transactional
    public void updateProductStock(Long productId, int quantity) {
        Product product = this.findById(productId);
        int newQuantity = product.getQuantity() + quantity;
        if (newQuantity < 0) {
            throw new RuntimeException("Insufficient stock");
        }
        product.setQuantity(newQuantity);
        productRepository.save(product);
    }
}