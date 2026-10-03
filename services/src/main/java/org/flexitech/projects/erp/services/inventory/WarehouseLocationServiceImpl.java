package org.flexitech.projects.erp.services.inventory;

import java.util.List;
import java.util.stream.Collectors;

import org.flexitech.projects.erp.commons.CommonValidators;
import org.flexitech.projects.erp.dto.SearchResultDTO;
import org.flexitech.projects.erp.dto.inventory.WarehouseLocationDTO;
import org.flexitech.projects.erp.dto.inventory.search.WarehouseLocationSearchDTO;
import org.flexitech.projects.erp.persistence.entities.inventory.Warehouse;
import org.flexitech.projects.erp.persistence.entities.inventory.WarehouseLocation;
import org.flexitech.projects.erp.persistence.repositories.inventory.StockBalanceRepository;
import org.flexitech.projects.erp.persistence.repositories.inventory.StockLedgerRepository;
import org.flexitech.projects.erp.persistence.repositories.inventory.WarehouseLocationRepository;
import org.flexitech.projects.erp.persistence.repositories.inventory.WarehouseRepository;
import org.flexitech.projects.erp.services.specifications.inventory.WarehouseLocationSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class WarehouseLocationServiceImpl implements WarehouseLocationService {

    private final WarehouseLocationRepository locationRepository;
    private final WarehouseRepository warehouseRepository;
    private final StockBalanceRepository stockBalanceRepository;
    private final StockLedgerRepository stockLedgerRepository;

    public WarehouseLocationServiceImpl(WarehouseLocationRepository locationRepository, WarehouseRepository warehouseRepository,
            StockBalanceRepository stockBalanceRepository, StockLedgerRepository stockLedgerRepository) {
        this.locationRepository = locationRepository;
        this.warehouseRepository = warehouseRepository;
        this.stockBalanceRepository = stockBalanceRepository;
        this.stockLedgerRepository = stockLedgerRepository;
    }

    @Override
    public WarehouseLocationDTO manageLocation(WarehouseLocationDTO locationDTO) throws Exception {
        WarehouseLocation location;
        boolean isUpdate = CommonValidators.validLong(locationDTO.getId());

        Warehouse warehouse = this.warehouseRepository.findById(locationDTO.getWarehouseId())
                .orElseThrow(() -> new Exception("Warehouse not found!"));

        if (isUpdate) {
            location = this.locationRepository.findById(locationDTO.getId()).orElseThrow(() -> new Exception("Location not found!"));
            if (this.locationRepository.existsByWarehouseIdAndCodeAndIdNot(locationDTO.getWarehouseId(), locationDTO.getCode(), locationDTO.getId())) {
                throw new Exception("Location code already exists in this warehouse!");
            }
            if (CommonValidators.validLong(locationDTO.getParentId()) && locationDTO.getParentId().equals(locationDTO.getId())) {
                throw new Exception("A location cannot be its own parent!");
            }
        } else {
            location = new WarehouseLocation();
            if (this.locationRepository.existsByWarehouseIdAndCode(locationDTO.getWarehouseId(), locationDTO.getCode())) {
                throw new Exception("Location code already exists in this warehouse!");
            }
        }

        location.setWarehouse(warehouse);
        location.setCode(locationDTO.getCode());
        location.setName(locationDTO.getName());
        location.setType(locationDTO.getType());
        location.setStatus(locationDTO.getStatus());

        if (CommonValidators.validLong(locationDTO.getParentId())) {
            WarehouseLocation parent = this.locationRepository.findById(locationDTO.getParentId())
                    .orElseThrow(() -> new Exception("Parent location not found!"));
            if (!parent.getWarehouse().getId().equals(locationDTO.getWarehouseId())) {
                throw new Exception("Parent location must belong to the same warehouse!");
            }
            location.setParent(parent);
        } else {
            location.setParent(null);
        }

        WarehouseLocation saved = this.locationRepository.save(location);
        return new WarehouseLocationDTO(saved);
    }

    @Override
    public WarehouseLocationDTO getLocationById(Long id) throws Exception {
        WarehouseLocation location = this.locationRepository.findById(id).orElseThrow(() -> new Exception("Location not found!"));
        return new WarehouseLocationDTO(location);
    }

    @Override
    public SearchResultDTO<WarehouseLocationDTO> searchLocations(WarehouseLocationSearchDTO searchDTO, Pageable pageable) throws Exception {
        try {
            Specification<WarehouseLocation> spec = WarehouseLocationSpecification.withSearchCriteria(searchDTO);
            Page<WarehouseLocation> page = this.locationRepository.findAll(spec, pageable);
            return convertToSearchResult(page);
        } catch (Exception e) {
            throw new Exception("Error searching locations: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean deleteLocation(Long id) throws Exception {
        WarehouseLocation location = this.locationRepository.findById(id).orElseThrow(() -> new Exception("Location not found!"));

        if (this.locationRepository.existsByParentId(id)) {
            throw new Exception("Cannot delete: location has sub-locations!");
        }
        if (this.stockBalanceRepository.existsByLocationId(id) || this.stockLedgerRepository.existsByLocationId(id)) {
            throw new Exception("Cannot delete: location has stock movement history!");
        }

        this.locationRepository.delete(location);
        return true;
    }

    private SearchResultDTO<WarehouseLocationDTO> convertToSearchResult(Page<WarehouseLocation> page) {
        SearchResultDTO<WarehouseLocationDTO> result = new SearchResultDTO<>();
        result.setPageNo(page.getNumber());
        result.setLimit(page.getSize());
        result.setTotalPage(page.getTotalPages());
        result.setTotalRecords((int) page.getTotalElements());
        result.setPageCount(page.getNumberOfElements());
        result.setHasNextPage(page.hasNext());

        List<WarehouseLocationDTO> dtos = page.getContent().stream().map(WarehouseLocationDTO::new).collect(Collectors.toList());
        result.setResults(dtos);
        return result;
    }
}