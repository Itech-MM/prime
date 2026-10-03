package org.flexitech.projects.erp.services.inventory;

import org.flexitech.projects.erp.dto.SearchResultDTO;
import org.flexitech.projects.erp.dto.inventory.SupplierDTO;
import org.flexitech.projects.erp.dto.inventory.search.SupplierSearchDTO;
import org.springframework.data.domain.Pageable;

public interface SupplierService {

    SupplierDTO manageSupplier(SupplierDTO supplierDTO) throws Exception;

    SupplierDTO getSupplierById(Long id) throws Exception;

    SearchResultDTO<SupplierDTO> searchSuppliers(SupplierSearchDTO searchDTO, Pageable pageable) throws Exception;

    boolean deleteSupplier(Long id) throws Exception;
}