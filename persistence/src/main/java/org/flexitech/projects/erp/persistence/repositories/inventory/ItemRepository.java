package org.flexitech.projects.erp.persistence.repositories.inventory;

import java.util.Optional;

import org.flexitech.projects.erp.persistence.entities.inventory.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ItemRepository extends JpaRepository<Item, Long>, JpaSpecificationExecutor<Item> {
    Optional<Item> findByCode(String code);
    Optional<Item> findByBarcode(String barcode);
    boolean existsByCode(String code);
    boolean existsByCodeAndIdNot(String code, Long id);
    boolean existsBySku(String sku);
    boolean existsBySkuAndIdNot(String sku, Long id);
    boolean existsByBarcode(String barcode);
    boolean existsByBarcodeAndIdNot(String barcode, Long id);
    boolean existsByCategoryId(Long categoryId);
    boolean existsByBrandId(Long brandId);
    boolean existsByBaseUomId(Long uomId);
}