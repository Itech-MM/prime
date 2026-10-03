package org.flexitech.projects.erp.services.inventory;

import java.util.List;
import java.util.stream.Collectors;

import org.flexitech.projects.erp.commons.CommonValidators;
import org.flexitech.projects.erp.dto.SearchResultDTO;
import org.flexitech.projects.erp.dto.inventory.ItemCategoryDTO;
import org.flexitech.projects.erp.dto.inventory.search.ItemCategorySearchDTO;
import org.flexitech.projects.erp.persistence.entities.inventory.ItemCategory;
import org.flexitech.projects.erp.persistence.repositories.inventory.ItemCategoryRepository;
import org.flexitech.projects.erp.persistence.repositories.inventory.ItemRepository;
import org.flexitech.projects.erp.services.specifications.inventory.ItemCategorySpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ItemCategoryServiceImpl implements ItemCategoryService {

    private final ItemCategoryRepository categoryRepository;
    private final ItemRepository itemRepository;

    public ItemCategoryServiceImpl(ItemCategoryRepository categoryRepository, ItemRepository itemRepository) {
        this.categoryRepository = categoryRepository;
        this.itemRepository = itemRepository;
    }

    @Override
    public ItemCategoryDTO manageCategory(ItemCategoryDTO categoryDTO) throws Exception {
        ItemCategory category;
        boolean isUpdate = CommonValidators.validLong(categoryDTO.getId());

        if (isUpdate) {
            category = this.categoryRepository.findById(categoryDTO.getId()).orElseThrow(() -> new Exception("Category not found!"));
            if (this.categoryRepository.existsByCodeAndIdNot(categoryDTO.getCode(), categoryDTO.getId())) {
                throw new Exception("Category code already exists!");
            }
            if (CommonValidators.validLong(categoryDTO.getParentId()) && categoryDTO.getParentId().equals(categoryDTO.getId())) {
                throw new Exception("A category cannot be its own parent!");
            }
        } else {
            category = new ItemCategory();
            if (this.categoryRepository.existsByCode(categoryDTO.getCode())) {
                throw new Exception("Category code already exists!");
            }
        }

        category.setCode(categoryDTO.getCode());
        category.setName(categoryDTO.getName());
        category.setStatus(categoryDTO.getStatus());

        if (CommonValidators.validLong(categoryDTO.getParentId())) {
            ItemCategory parent = this.categoryRepository.findById(categoryDTO.getParentId())
                    .orElseThrow(() -> new Exception("Parent category not found!"));
            category.setParent(parent);
        } else {
            category.setParent(null);
        }

        ItemCategory saved = this.categoryRepository.save(category);
        return new ItemCategoryDTO(saved);
    }

    @Override
    public ItemCategoryDTO getCategoryById(Long id) throws Exception {
        ItemCategory category = this.categoryRepository.findById(id).orElseThrow(() -> new Exception("Category not found!"));
        return new ItemCategoryDTO(category);
    }

    @Override
    public SearchResultDTO<ItemCategoryDTO> searchCategories(ItemCategorySearchDTO searchDTO, Pageable pageable) throws Exception {
        try {
            Specification<ItemCategory> spec = ItemCategorySpecification.withSearchCriteria(searchDTO);
            Page<ItemCategory> page = this.categoryRepository.findAll(spec, pageable);
            return convertToSearchResult(page);
        } catch (Exception e) {
            throw new Exception("Error searching categories: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean deleteCategory(Long id) throws Exception {
        ItemCategory category = this.categoryRepository.findById(id).orElseThrow(() -> new Exception("Category not found!"));

        if (this.categoryRepository.existsByParentId(id)) {
            throw new Exception("Cannot delete: category has sub-categories!");
        }
        if (this.itemRepository.existsByCategoryId(id)) {
            throw new Exception("Cannot delete: category is used by one or more items!");
        }

        this.categoryRepository.delete(category);
        return true;
    }

    private SearchResultDTO<ItemCategoryDTO> convertToSearchResult(Page<ItemCategory> page) {
        SearchResultDTO<ItemCategoryDTO> result = new SearchResultDTO<>();
        result.setPageNo(page.getNumber());
        result.setLimit(page.getSize());
        result.setTotalPage(page.getTotalPages());
        result.setTotalRecords((int) page.getTotalElements());
        result.setPageCount(page.getNumberOfElements());
        result.setHasNextPage(page.hasNext());

        List<ItemCategoryDTO> dtos = page.getContent().stream().map(ItemCategoryDTO::new).collect(Collectors.toList());
        result.setResults(dtos);
        return result;
    }
}