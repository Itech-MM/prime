package org.flexitech.projects.erp.services.inventory;

import org.flexitech.projects.erp.dto.SearchResultDTO;
import org.flexitech.projects.erp.dto.inventory.WarehouseDTO;
import org.flexitech.projects.erp.dto.inventory.search.WarehouseSearchDTO;
import org.springframework.data.domain.Pageable;

public interface WarehouseService {

    WarehouseDTO manageWarehouse(WarehouseDTO warehouseDTO) throws Exception;

    WarehouseDTO getWarehouseById(Long id) throws Exception;

    SearchResultDTO<WarehouseDTO> searchWarehouses(WarehouseSearchDTO searchDTO, Pageable pageable) throws Exception;

    boolean deleteWarehouse(Long id) throws Exception;
}