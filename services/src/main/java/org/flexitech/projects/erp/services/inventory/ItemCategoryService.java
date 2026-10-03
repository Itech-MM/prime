package org.flexitech.projects.erp.services.inventory;

import org.flexitech.projects.erp.dto.SearchResultDTO;
import org.flexitech.projects.erp.dto.inventory.ItemCategoryDTO;
import org.flexitech.projects.erp.dto.inventory.search.ItemCategorySearchDTO;
import org.springframework.data.domain.Pageable;

public interface ItemCategoryService {

    ItemCategoryDTO manageCategory(ItemCategoryDTO categoryDTO) throws Exception;

    ItemCategoryDTO getCategoryById(Long id) throws Exception;

    SearchResultDTO<ItemCategoryDTO> searchCategories(ItemCategorySearchDTO searchDTO, Pageable pageable) throws Exception;

    boolean deleteCategory(Long id) throws Exception;
}