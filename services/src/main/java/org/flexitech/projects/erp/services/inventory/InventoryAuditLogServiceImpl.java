package org.flexitech.projects.erp.services.inventory;

import java.util.List;
import java.util.stream.Collectors;

import org.flexitech.projects.erp.dto.SearchResultDTO;
import org.flexitech.projects.erp.dto.inventory.InventoryAuditLogDTO;
import org.flexitech.projects.erp.dto.inventory.search.InventoryAuditLogSearchDTO;
import org.flexitech.projects.erp.persistence.entities.inventory.InventoryAuditLog;
import org.flexitech.projects.erp.persistence.entities.user.User;
import org.flexitech.projects.erp.persistence.repositories.inventory.InventoryAuditLogRepository;
import org.flexitech.projects.erp.services.auth.AuthenticationService;
import org.flexitech.projects.erp.services.specifications.inventory.InventoryAuditLogSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class InventoryAuditLogServiceImpl implements InventoryAuditLogService {

	private final InventoryAuditLogRepository auditLogRepository;
	private final AuthenticationService authenticationService;

	public InventoryAuditLogServiceImpl(InventoryAuditLogRepository auditLogRepository, AuthenticationService authenticationService) {
		this.auditLogRepository = auditLogRepository;
		this.authenticationService = authenticationService;
	}

	@Override
	@Transactional(propagation = Propagation.MANDATORY)
	public void log(String docType, Long docId, String docNo, Integer action, String remarks) {
		try {
			InventoryAuditLog entry = new InventoryAuditLog();
			entry.setDocType(docType);
			entry.setDocId(docId);
			entry.setDocNo(docNo);
			entry.setAction(action);
			entry.setRemarks(remarks);

			User currentUser = this.authenticationService.getLoggedInUser();
			entry.setCreatedBy(currentUser);

			this.auditLogRepository.save(entry);
		} catch (Exception e) {
			log.error("Failed to write inventory audit log for {} #{}: {}", docType, docId, e.getMessage());
		}
	}

	@Override
	public SearchResultDTO<InventoryAuditLogDTO> searchAuditLog(InventoryAuditLogSearchDTO searchDTO, Pageable pageable) throws Exception {
		try {
			Specification<InventoryAuditLog> spec = InventoryAuditLogSpecification.withSearchCriteria(searchDTO);
			Page<InventoryAuditLog> page = this.auditLogRepository.findAll(spec, pageable);
			return convertToSearchResult(page);
		} catch (Exception e) {
			throw new Exception("Error searching audit log: " + e.getMessage(), e);
		}
	}

	private SearchResultDTO<InventoryAuditLogDTO> convertToSearchResult(Page<InventoryAuditLog> page) {
		SearchResultDTO<InventoryAuditLogDTO> result = new SearchResultDTO<>();
		result.setPageNo(page.getNumber());
		result.setLimit(page.getSize());
		result.setTotalPage(page.getTotalPages());
		result.setTotalRecords((int) page.getTotalElements());
		result.setPageCount(page.getNumberOfElements());
		result.setHasNextPage(page.hasNext());

		List<InventoryAuditLogDTO> dtos = page.getContent().stream().map(InventoryAuditLogDTO::new).collect(Collectors.toList());
		result.setResults(dtos);
		return result;
	}
}