package org.flexitech.projects.erp.services.inventory;

import org.flexitech.projects.erp.dto.SearchResultDTO;
import org.flexitech.projects.erp.dto.inventory.GoodsReceiptDTO;
import org.flexitech.projects.erp.dto.inventory.search.GoodsReceiptSearchDTO;

public interface GoodsReceiptService {

	GoodsReceiptDTO saveDraft(GoodsReceiptDTO receiptDTO) throws Exception;

	GoodsReceiptDTO getReceiptById(Long id) throws Exception;

	SearchResultDTO<GoodsReceiptDTO> searchReceipts(GoodsReceiptSearchDTO searchDTO, org.springframework.data.domain.Pageable pageable) throws Exception;

	GoodsReceiptDTO submit(Long id) throws Exception;

	GoodsReceiptDTO approve(Long id) throws Exception;

	GoodsReceiptDTO post(Long id) throws Exception;

	GoodsReceiptDTO cancel(Long id) throws Exception;

	boolean deleteReceipt(Long id) throws Exception;
}