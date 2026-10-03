package org.flexitech.projects.erp.persistence.repositories.inventory;

import java.util.Optional;

import org.flexitech.projects.erp.persistence.entities.inventory.UomConversion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface UomConversionRepository extends JpaRepository<UomConversion, Long>, JpaSpecificationExecutor<UomConversion> {
    Optional<UomConversion> findByFromUomIdAndToUomId(Long fromUomId, Long toUomId);
    boolean existsByFromUomIdAndToUomId(Long fromUomId, Long toUomId);
    boolean existsByFromUomIdAndToUomIdAndIdNot(Long fromUomId, Long toUomId, Long id);
}