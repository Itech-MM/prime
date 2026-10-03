package org.flexitech.projects.erp.services.inventory;

import java.util.List;
import java.util.stream.Collectors;

import org.flexitech.projects.erp.commons.CommonValidators;
import org.flexitech.projects.erp.dto.SearchResultDTO;
import org.flexitech.projects.erp.dto.inventory.BrandDTO;
import org.flexitech.projects.erp.dto.inventory.search.BrandSearchDTO;
import org.flexitech.projects.erp.persistence.entities.inventory.Brand;
import org.flexitech.projects.erp.persistence.repositories.inventory.BrandRepository;
import org.flexitech.projects.erp.persistence.repositories.inventory.ItemRepository;
import org.flexitech.projects.erp.services.specifications.inventory.BrandSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class BrandServiceImpl implements BrandService {

    private final BrandRepository brandRepository;
    private final ItemRepository itemRepository;

    public BrandServiceImpl(BrandRepository brandRepository, ItemRepository itemRepository) {
        this.brandRepository = brandRepository;
        this.itemRepository = itemRepository;
    }

    @Override
    public BrandDTO manageBrand(BrandDTO brandDTO) throws Exception {
        Brand brand;
        boolean isUpdate = CommonValidators.validLong(brandDTO.getId());

        if (isUpdate) {
            brand = this.brandRepository.findById(brandDTO.getId()).orElseThrow(() -> new Exception("Brand not found!"));
            if (this.brandRepository.existsByCodeAndIdNot(brandDTO.getCode(), brandDTO.getId())) {
                throw new Exception("Brand code already exists!");
            }
        } else {
            brand = new Brand();
            if (this.brandRepository.existsByCode(brandDTO.getCode())) {
                throw new Exception("Brand code already exists!");
            }
        }

        brand.setCode(brandDTO.getCode());
        brand.setName(brandDTO.getName());
        brand.setStatus(brandDTO.getStatus());

        Brand saved = this.brandRepository.save(brand);
        return new BrandDTO(saved);
    }

    @Override
    public BrandDTO getBrandById(Long id) throws Exception {
        Brand brand = this.brandRepository.findById(id).orElseThrow(() -> new Exception("Brand not found!"));
        return new BrandDTO(brand);
    }

    @Override
    public SearchResultDTO<BrandDTO> searchBrands(BrandSearchDTO searchDTO, Pageable pageable) throws Exception {
        try {
            Specification<Brand> spec = BrandSpecification.withSearchCriteria(searchDTO);
            Page<Brand> page = this.brandRepository.findAll(spec, pageable);
            return convertToSearchResult(page);
        } catch (Exception e) {
            throw new Exception("Error searching brands: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean deleteBrand(Long id) throws Exception {
        Brand brand = this.brandRepository.findById(id).orElseThrow(() -> new Exception("Brand not found!"));

        if (this.itemRepository.existsByBrandId(id)) {
            throw new Exception("Cannot delete: brand is used by one or more items!");
        }

        this.brandRepository.delete(brand);
        return true;
    }

    private SearchResultDTO<BrandDTO> convertToSearchResult(Page<Brand> page) {
        SearchResultDTO<BrandDTO> result = new SearchResultDTO<>();
        result.setPageNo(page.getNumber());
        result.setLimit(page.getSize());
        result.setTotalPage(page.getTotalPages());
        result.setTotalRecords((int) page.getTotalElements());
        result.setPageCount(page.getNumberOfElements());
        result.setHasNextPage(page.hasNext());

        List<BrandDTO> dtos = page.getContent().stream().map(BrandDTO::new).collect(Collectors.toList());
        result.setResults(dtos);
        return result;
    }
}