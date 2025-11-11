package com.nocturna.performance.dto.repository;

import com.nocturna.performance.dto.Product;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductEngRepository extends JpaRepository<Product, String> {
    @Transactional
    default Product updateOrInsert(Product entity){
        return save(entity);
    }
}
