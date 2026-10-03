package org.flexitech.projects.erp.services.inventory;

import java.math.BigDecimal;

import org.flexitech.projects.erp.persistence.entities.inventory.Item;
import org.flexitech.projects.erp.persistence.entities.inventory.StockBatch;
import org.flexitech.projects.erp.persistence.entities.inventory.WarehouseLocation;

public interface StockMovementService {

	void postReceipt(Item item, WarehouseLocation location, StockBatch batch, BigDecimal qty, BigDecimal unitCost,
			String refDocType, Long refDocId, Long refLineId) throws Exception;

	BigDecimal postIssue(Item item, WarehouseLocation location, StockBatch batch, BigDecimal qty,
			String refDocType, Long refDocId, Long refLineId) throws Exception;

	void postAdjustment(Item item, WarehouseLocation location, StockBatch batch, BigDecimal differenceQty, BigDecimal unitCost,
			String refDocType, Long refDocId, Long refLineId) throws Exception;
}