package org.flexitech.projects.erp.services.inventory;

import java.util.List;
import java.util.stream.Collectors;

import org.flexitech.projects.erp.dto.SearchResultDTO;
import org.flexitech.projects.erp.dto.inventory.StockBalanceDTO;
import org.flexitech.projects.erp.dto.inventory.StockLedgerDTO;
import org.flexitech.projects.erp.dto.inventory.search.StockBalanceSearchDTO;
import org.flexitech.projects.erp.dto.inventory.search.StockLedgerSearchDTO;
import org.flexitech.projects.erp.persistence.entities.inventory.StockBalance;
import org.flexitech.projects.erp.persistence.entities.inventory.StockLedger;
import org.flexitech.projects.erp.persistence.repositories.inventory.StockBalanceRepository;
import org.flexitech.projects.erp.persistence.repositories.inventory.StockLedgerRepository;
import org.flexitech.projects.erp.services.specifications.inventory.StockBalanceSpecification;
import org.flexitech.projects.erp.services.specifications.inventory.StockLedgerSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class StockReportServiceImpl implements StockReportService {

	private final StockBalanceRepository stockBalanceRepository;
	private final StockLedgerRepository stockLedgerRepository;

	public StockReportServiceImpl(StockBalanceRepository stockBalanceRepository, StockLedgerRepository stockLedgerRepository) {
		this.stockBalanceRepository = stockBalanceRepository;
		this.stockLedgerRepository = stockLedgerRepository;
	}

	@Override
	public SearchResultDTO<StockBalanceDTO> searchStockOnHand(StockBalanceSearchDTO searchDTO, Pageable pageable) throws Exception {
		try {
			Specification<StockBalance> spec = StockBalanceSpecification.withSearchCriteria(searchDTO);
			Page<StockBalance> page = this.stockBalanceRepository.findAll(spec, pageable);
			return convertBalances(page);
		} catch (Exception e) {
			throw new Exception("Error loading stock on hand: " + e.getMessage(), e);
		}
	}

	@Override
	public SearchResultDTO<StockLedgerDTO> getStockCard(StockLedgerSearchDTO searchDTO, Pageable pageable) throws Exception {
		try {
			Specification<StockLedger> spec = StockLedgerSpecification.withSearchCriteria(searchDTO);
			Page<StockLedger> page = this.stockLedgerRepository.findAll(spec, pageable);
			return convertLedger(page);
		} catch (Exception e) {
			throw new Exception("Error loading stock card: " + e.getMessage(), e);
		}
	}

	private SearchResultDTO<StockBalanceDTO> convertBalances(Page<StockBalance> page) {
		SearchResultDTO<StockBalanceDTO> result = new SearchResultDTO<>();
		result.setPageNo(page.getNumber());
		result.setLimit(page.getSize());
		result.setTotalPage(page.getTotalPages());
		result.setTotalRecords((int) page.getTotalElements());
		result.setPageCount(page.getNumberOfElements());
		result.setHasNextPage(page.hasNext());

		List<StockBalanceDTO> dtos = page.getContent().stream().map(StockBalanceDTO::new).collect(Collectors.toList());
		result.setResults(dtos);
		return result;
	}

	private SearchResultDTO<StockLedgerDTO> convertLedger(Page<StockLedger> page) {
		SearchResultDTO<StockLedgerDTO> result = new SearchResultDTO<>();
		result.setPageNo(page.getNumber());
		result.setLimit(page.getSize());
		result.setTotalPage(page.getTotalPages());
		result.setTotalRecords((int) page.getTotalElements());
		result.setPageCount(page.getNumberOfElements());
		result.setHasNextPage(page.hasNext());

		List<StockLedgerDTO> dtos = page.getContent().stream().map(StockLedgerDTO::new).collect(Collectors.toList());
		result.setResults(dtos);
		return result;
	}
}