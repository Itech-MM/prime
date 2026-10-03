package org.flexitech.projects.erp.services.inventory;

import java.util.List;
import java.util.stream.Collectors;

import org.flexitech.projects.erp.commons.CommonValidators;
import org.flexitech.projects.erp.dto.SearchResultDTO;
import org.flexitech.projects.erp.dto.inventory.UnitOfMeasureDTO;
import org.flexitech.projects.erp.dto.inventory.search.UnitOfMeasureSearchDTO;
import org.flexitech.projects.erp.persistence.entities.inventory.UnitOfMeasure;
import org.flexitech.projects.erp.persistence.repositories.inventory.UnitOfMeasureRepository;
import org.flexitech.projects.erp.services.specifications.inventory.UnitOfMeasureSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UnitOfMeasureServiceImpl implements UnitOfMeasureService {

    private final UnitOfMeasureRepository uomRepository;

    public UnitOfMeasureServiceImpl(UnitOfMeasureRepository uomRepository) {
        this.uomRepository = uomRepository;
    }

    @Override
    public UnitOfMeasureDTO manageUom(UnitOfMeasureDTO uomDTO) throws Exception {
        UnitOfMeasure uom;
        boolean isUpdate = CommonValidators.validLong(uomDTO.getId());

        if (isUpdate) {
            uom = this.uomRepository.findById(uomDTO.getId()).orElseThrow(() -> new Exception("Unit of measure not found!"));
            if (this.uomRepository.existsByCodeAndIdNot(uomDTO.getCode(), uomDTO.getId())) {
                throw new Exception("Unit of measure code already exists!");
            }
        } else {
            uom = new UnitOfMeasure();
            if (this.uomRepository.existsByCode(uomDTO.getCode())) {
                throw new Exception("Unit of measure code already exists!");
            }
        }

        uom.setCode(uomDTO.getCode());
        uom.setName(uomDTO.getName());
        uom.setStatus(uomDTO.getStatus());

        UnitOfMeasure saved = this.uomRepository.save(uom);
        return new UnitOfMeasureDTO(saved);
    }

    @Override
    public UnitOfMeasureDTO getUomById(Long id) throws Exception {
        UnitOfMeasure uom = this.uomRepository.findById(id).orElseThrow(() -> new Exception("Unit of measure not found!"));
        return new UnitOfMeasureDTO(uom);
    }

    @Override
    public SearchResultDTO<UnitOfMeasureDTO> searchUoms(UnitOfMeasureSearchDTO searchDTO, Pageable pageable) throws Exception {
        try {
            Specification<UnitOfMeasure> spec = UnitOfMeasureSpecification.withSearchCriteria(searchDTO);
            Page<UnitOfMeasure> page = this.uomRepository.findAll(spec, pageable);
            return convertToSearchResult(page);
        } catch (Exception e) {
            throw new Exception("Error searching units of measure: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean deleteUom(Long id) throws Exception {
        UnitOfMeasure uom = this.uomRepository.findById(id).orElseThrow(() -> new Exception("Unit of measure not found!"));

        if (this.uomRepository.existsByCode(uom.getCode())) {
            throw new Exception("Cannot delete: unit of measure is in use!");
        }

        this.uomRepository.delete(uom);
        return true;
    }

    private SearchResultDTO<UnitOfMeasureDTO> convertToSearchResult(Page<UnitOfMeasure> page) {
        SearchResultDTO<UnitOfMeasureDTO> result = new SearchResultDTO<>();
        result.setPageNo(page.getNumber());
        result.setLimit(page.getSize());
        result.setTotalPage(page.getTotalPages());
        result.setTotalRecords((int) page.getTotalElements());
        result.setPageCount(page.getNumberOfElements());
        result.setHasNextPage(page.hasNext());

        List<UnitOfMeasureDTO> dtos = page.getContent().stream().map(UnitOfMeasureDTO::new).collect(Collectors.toList());
        result.setResults(dtos);
        return result;
    }
}