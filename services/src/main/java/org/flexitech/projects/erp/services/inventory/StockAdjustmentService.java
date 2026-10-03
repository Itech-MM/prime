package org.flexitech.projects.erp.services.inventory;

import org.flexitech.projects.erp.dto.SearchResultDTO;
import org.flexitech.projects.erp.dto.inventory.StockAdjustmentDTO;
import org.flexitech.projects.erp.dto.inventory.search.StockAdjustmentSearchDTO;
import org.springframework.data.domain.Pageable;

public interface StockAdjustmentService {

	StockAdjustmentDTO saveDraft(StockAdjustmentDTO adjustmentDTO) throws Exception;

	StockAdjustmentDTO getAdjustmentById(Long id) throws Exception;

	SearchResultDTO<StockAdjustmentDTO> searchAdjustments(StockAdjustmentSearchDTO searchDTO, Pageable pageable) throws Exception;

	StockAdjustmentDTO submit(Long id) throws Exception;

	StockAdjustmentDTO approve(Long id) throws Exception;

	StockAdjustmentDTO post(Long id) throws Exception;

	StockAdjustmentDTO cancel(Long id) throws Exception;

	boolean deleteAdjustment(Long id) throws Exception;
}