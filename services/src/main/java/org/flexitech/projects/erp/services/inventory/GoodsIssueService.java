package org.flexitech.projects.erp.services.inventory;

import org.flexitech.projects.erp.dto.SearchResultDTO;
import org.flexitech.projects.erp.dto.inventory.GoodsIssueDTO;
import org.flexitech.projects.erp.dto.inventory.search.GoodsIssueSearchDTO;
import org.springframework.data.domain.Pageable;

public interface GoodsIssueService {

	GoodsIssueDTO saveDraft(GoodsIssueDTO issueDTO) throws Exception;

	GoodsIssueDTO getIssueById(Long id) throws Exception;

	SearchResultDTO<GoodsIssueDTO> searchIssues(GoodsIssueSearchDTO searchDTO, Pageable pageable) throws Exception;

	GoodsIssueDTO submit(Long id) throws Exception;

	GoodsIssueDTO approve(Long id) throws Exception;

	GoodsIssueDTO post(Long id) throws Exception;

	GoodsIssueDTO cancel(Long id) throws Exception;

	boolean deleteIssue(Long id) throws Exception;
}