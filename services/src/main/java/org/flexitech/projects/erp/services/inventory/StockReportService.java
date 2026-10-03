package org.flexitech.projects.erp.services.inventory;

import org.flexitech.projects.erp.dto.SearchResultDTO;
import org.flexitech.projects.erp.dto.inventory.StockBalanceDTO;
import org.flexitech.projects.erp.dto.inventory.StockLedgerDTO;
import org.flexitech.projects.erp.dto.inventory.search.StockBalanceSearchDTO;
import org.flexitech.projects.erp.dto.inventory.search.StockLedgerSearchDTO;
import org.springframework.data.domain.Pageable;

public interface StockReportService {

	SearchResultDTO<StockBalanceDTO> searchStockOnHand(StockBalanceSearchDTO searchDTO, Pageable pageable) throws Exception;

	SearchResultDTO<StockLedgerDTO> getStockCard(StockLedgerSearchDTO searchDTO, Pageable pageable) throws Exception;
}