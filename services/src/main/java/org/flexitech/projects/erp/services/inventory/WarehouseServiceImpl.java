package org.flexitech.projects.erp.services.inventory;

import java.util.List;
import java.util.stream.Collectors;

import org.flexitech.projects.erp.commons.CommonValidators;
import org.flexitech.projects.erp.dto.SearchResultDTO;
import org.flexitech.projects.erp.dto.inventory.WarehouseDTO;
import org.flexitech.projects.erp.dto.inventory.search.WarehouseSearchDTO;
import org.flexitech.projects.erp.persistence.entities.inventory.Warehouse;
import org.flexitech.projects.erp.persistence.repositories.inventory.StockBalanceRepository;
import org.flexitech.projects.erp.persistence.repositories.inventory.WarehouseLocationRepository;
import org.flexitech.projects.erp.persistence.repositories.inventory.WarehouseRepository;
import org.flexitech.projects.erp.services.specifications.inventory.WarehouseSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class WarehouseServiceImpl implements WarehouseService {

    private final WarehouseRepository warehouseRepository;
    private final WarehouseLocationRepository locationRepository;
    private final StockBalanceRepository stockBalanceRepository;

    public WarehouseServiceImpl(WarehouseRepository warehouseRepository, WarehouseLocationRepository locationRepository,
            StockBalanceRepository stockBalanceRepository) {
        this.warehouseRepository = warehouseRepository;
        this.locationRepository = locationRepository;
        this.stockBalanceRepository = stockBalanceRepository;
    }

    @Override
    public WarehouseDTO manageWarehouse(WarehouseDTO warehouseDTO) throws Exception {
        Warehouse warehouse;
        boolean isUpdate = CommonValidators.validLong(warehouseDTO.getId());

        if (isUpdate) {
            warehouse = this.warehouseRepository.findById(warehouseDTO.getId()).orElseThrow(() -> new Exception("Warehouse not found!"));
            if (this.warehouseRepository.existsByCodeAndIdNot(warehouseDTO.getCode(), warehouseDTO.getId())) {
                throw new Exception("Warehouse code already exists!");
            }
        } else {
            warehouse = new Warehouse();
            if (this.warehouseRepository.existsByCode(warehouseDTO.getCode())) {
                throw new Exception("Warehouse code already exists!");
            }
        }

        warehouse.setCode(warehouseDTO.getCode());
        warehouse.setName(warehouseDTO.getName());
        warehouse.setAddress(warehouseDTO.getAddress());
        warehouse.setManagerName(warehouseDTO.getManagerName());
        warehouse.setAllowNegativeStock(warehouseDTO.getAllowNegativeStock());
        warehouse.setStatus(warehouseDTO.getStatus());

        Warehouse saved = this.warehouseRepository.save(warehouse);
        return new WarehouseDTO(saved);
    }

    @Override
    public WarehouseDTO getWarehouseById(Long id) throws Exception {
        Warehouse warehouse = this.warehouseRepository.findById(id).orElseThrow(() -> new Exception("Warehouse not found!"));
        return new WarehouseDTO(warehouse);
    }

    @Override
    public SearchResultDTO<WarehouseDTO> searchWarehouses(WarehouseSearchDTO searchDTO, Pageable pageable) throws Exception {
        try {
            Specification<Warehouse> spec = WarehouseSpecification.withSearchCriteria(searchDTO);
            Page<Warehouse> page = this.warehouseRepository.findAll(spec, pageable);
            return convertToSearchResult(page);
        } catch (Exception e) {
            throw new Exception("Error searching warehouses: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean deleteWarehouse(Long id) throws Exception {
        Warehouse warehouse = this.warehouseRepository.findById(id).orElseThrow(() -> new Exception("Warehouse not found!"));

        if (this.locationRepository.existsByWarehouseId(id)) {
            throw new Exception("Cannot delete: warehouse has one or more locations!");
        }
        if (this.stockBalanceRepository.existsByLocationWarehouseId(id)) {
            throw new Exception("Cannot delete: warehouse has stock movement history!");
        }

        this.warehouseRepository.delete(warehouse);
        return true;
    }

    private SearchResultDTO<WarehouseDTO> convertToSearchResult(Page<Warehouse> page) {
        SearchResultDTO<WarehouseDTO> result = new SearchResultDTO<>();
        result.setPageNo(page.getNumber());
        result.setLimit(page.getSize());
        result.setTotalPage(page.getTotalPages());
        result.setTotalRecords((int) page.getTotalElements());
        result.setPageCount(page.getNumberOfElements());
        result.setHasNextPage(page.hasNext());

        List<WarehouseDTO> dtos = page.getContent().stream().map(WarehouseDTO::new).collect(Collectors.toList());
        result.setResults(dtos);
        return result;
    }
}