package org.flexitech.projects.erp.persistence.repositories.inventory;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.flexitech.projects.erp.persistence.entities.inventory.StockBalance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;

public interface StockBalanceRepository extends JpaRepository<StockBalance, Long>, JpaSpecificationExecutor<StockBalance> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select b from StockBalance b where b.item.id = :itemId and b.location.id = :locationId and b.batch is null")
    Optional<StockBalance> findForUpdateWithoutBatch(@Param("itemId") Long itemId, @Param("locationId") Long locationId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select b from StockBalance b where b.item.id = :itemId and b.location.id = :locationId and b.batch.id = :batchId")
    Optional<StockBalance> findForUpdateWithBatch(@Param("itemId") Long itemId, @Param("locationId") Long locationId, @Param("batchId") Long batchId);

    Optional<StockBalance> findByItemIdAndLocationIdAndBatchIsNull(Long itemId, Long locationId);

    Optional<StockBalance> findByItemIdAndLocationIdAndBatchId(Long itemId, Long locationId, Long batchId);

    List<StockBalance> findByItemId(Long itemId);

    List<StockBalance> findByLocationWarehouseId(Long warehouseId);

    boolean existsByItemId(Long itemId);

    boolean existsByLocationId(Long locationId);

    @Query("select coalesce(sum(b.qtyOnHand), 0) from StockBalance b where b.item.id = :itemId")
    BigDecimal sumQtyOnHandByItemId(@Param("itemId") Long itemId);

    @Query("select coalesce(sum(b.qtyOnHand), 0) from StockBalance b where b.item.id = :itemId and b.location.warehouse.id = :warehouseId")
    BigDecimal sumQtyOnHandByItemIdAndWarehouseId(@Param("itemId") Long itemId, @Param("warehouseId") Long warehouseId);

	boolean existsByLocationWarehouseId(Long warehouseId);
}