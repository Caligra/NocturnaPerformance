package com.nocturna.performance.dto.catalog.repository;

import com.nocturna.performance.dto.catalog.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductEngRepository extends JpaRepository<Product, String> {

    List<Product> findByBrand(String brandCode);

    /**
     * To create custom queries
     */
    /*
    @Query("SELECT s FROM Student s WHERE s.course = ?1")
            List<Student> findByCourse(String course);
     */

}
