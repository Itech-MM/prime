package org.flexitech.projects.erp.services.inventory;

import org.flexitech.projects.erp.dto.SearchResultDTO;
import org.flexitech.projects.erp.dto.inventory.BrandDTO;
import org.flexitech.projects.erp.dto.inventory.search.BrandSearchDTO;
import org.springframework.data.domain.Pageable;

public interface BrandService {

    BrandDTO manageBrand(BrandDTO brandDTO) throws Exception;

    BrandDTO getBrandById(Long id) throws Exception;

    SearchResultDTO<BrandDTO> searchBrands(BrandSearchDTO searchDTO, Pageable pageable) throws Exception;

    boolean deleteBrand(Long id) throws Exception;
}