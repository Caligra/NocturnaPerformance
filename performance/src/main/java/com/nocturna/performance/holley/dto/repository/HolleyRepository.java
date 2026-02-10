package com.nocturna.performance.holley.dto.repository;

import com.nocturna.performance.holley.dto.HolleyProduct;
import com.nocturna.performance.holley.dto.HolleyProducts;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HolleyRepository extends JpaRepository<HolleyProduct, String> {

    List<HolleyProduct> findByBrand(String brandCode);

    /**
     * To create custom queries
     */
    /*
    @Query("SELECT s FROM Student s WHERE s.course = ?1")
            List<Student> findByCourse(String course);
     */

}
