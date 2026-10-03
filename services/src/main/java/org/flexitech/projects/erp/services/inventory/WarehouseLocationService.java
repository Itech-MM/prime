package org.flexitech.projects.erp.services.inventory;

import org.flexitech.projects.erp.dto.SearchResultDTO;
import org.flexitech.projects.erp.dto.inventory.WarehouseLocationDTO;
import org.flexitech.projects.erp.dto.inventory.search.WarehouseLocationSearchDTO;
import org.springframework.data.domain.Pageable;

public interface WarehouseLocationService {

    WarehouseLocationDTO manageLocation(WarehouseLocationDTO locationDTO) throws Exception;

    WarehouseLocationDTO getLocationById(Long id) throws Exception;

    SearchResultDTO<WarehouseLocationDTO> searchLocations(WarehouseLocationSearchDTO searchDTO, Pageable pageable) throws Exception;

    boolean deleteLocation(Long id) throws Exception;
}