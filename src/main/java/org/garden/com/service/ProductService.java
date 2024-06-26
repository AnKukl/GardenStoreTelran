package org.garden.com.service;

import org.garden.com.entity.CartItem;
import org.garden.com.entity.Product;

import java.util.List;

public interface ProductService {

    Product create(Product product);

    List<Product> getFiltered(Long categoryId, Double minPrice, Double maxPrice, Boolean discount, String sort);

    Product editById(long id, Product product);

    Product findById(long id);

    void deleteById(long id);

    CartItem addToCart(Product product, long quantity, Long userId);
}
