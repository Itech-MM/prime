package org.flexitech.projects.erp.services.inventory;

import org.flexitech.projects.erp.dto.SearchResultDTO;
import org.flexitech.projects.erp.dto.inventory.UnitOfMeasureDTO;
import org.flexitech.projects.erp.dto.inventory.search.UnitOfMeasureSearchDTO;
import org.springframework.data.domain.Pageable;

public interface UnitOfMeasureService {

    UnitOfMeasureDTO manageUom(UnitOfMeasureDTO uomDTO) throws Exception;

    UnitOfMeasureDTO getUomById(Long id) throws Exception;

    SearchResultDTO<UnitOfMeasureDTO> searchUoms(UnitOfMeasureSearchDTO searchDTO, Pageable pageable) throws Exception;

    boolean deleteUom(Long id) throws Exception;
}