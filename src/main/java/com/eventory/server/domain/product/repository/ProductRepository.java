package com.eventory.server.domain.product.repository;

import com.eventory.server.domain.party.entity.enums.ParticipantType;
import com.eventory.server.domain.party.entity.enums.Purpose;
import com.eventory.server.domain.product.entity.Product;
import com.eventory.server.domain.product.entity.enums.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    
    List<Product> findByPurposeAndParticipantTypeAndCategoryIn(
            Purpose purpose, 
            ParticipantType participantType, 
            List<Category> categories
    );
    
    List<Product> findByProductNameContaining(String keyword);
}
