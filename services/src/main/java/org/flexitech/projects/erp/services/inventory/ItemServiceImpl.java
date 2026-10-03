package org.flexitech.projects.erp.services.inventory;

import java.util.List;
import java.util.stream.Collectors;

import org.flexitech.projects.erp.commons.CommonValidators;
import org.flexitech.projects.erp.dto.SearchResultDTO;
import org.flexitech.projects.erp.dto.inventory.ItemDTO;
import org.flexitech.projects.erp.dto.inventory.search.ItemSearchDTO;
import org.flexitech.projects.erp.persistence.entities.inventory.Brand;
import org.flexitech.projects.erp.persistence.entities.inventory.Item;
import org.flexitech.projects.erp.persistence.entities.inventory.ItemCategory;
import org.flexitech.projects.erp.persistence.entities.inventory.UnitOfMeasure;
import org.flexitech.projects.erp.persistence.repositories.inventory.BrandRepository;
import org.flexitech.projects.erp.persistence.repositories.inventory.ItemCategoryRepository;
import org.flexitech.projects.erp.persistence.repositories.inventory.ItemRepository;
import org.flexitech.projects.erp.persistence.repositories.inventory.StockBalanceRepository;
import org.flexitech.projects.erp.persistence.repositories.inventory.StockLedgerRepository;
import org.flexitech.projects.erp.persistence.repositories.inventory.UnitOfMeasureRepository;
import org.flexitech.projects.erp.services.specifications.inventory.ItemSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final ItemCategoryRepository categoryRepository;
    private final BrandRepository brandRepository;
    private final UnitOfMeasureRepository uomRepository;
    private final StockBalanceRepository stockBalanceRepository;
    private final StockLedgerRepository stockLedgerRepository;

    public ItemServiceImpl(ItemRepository itemRepository, ItemCategoryRepository categoryRepository,
            BrandRepository brandRepository, UnitOfMeasureRepository uomRepository,
            StockBalanceRepository stockBalanceRepository, StockLedgerRepository stockLedgerRepository) {
        this.itemRepository = itemRepository;
        this.categoryRepository = categoryRepository;
        this.brandRepository = brandRepository;
        this.uomRepository = uomRepository;
        this.stockBalanceRepository = stockBalanceRepository;
        this.stockLedgerRepository = stockLedgerRepository;
    }

    @Override
    public ItemDTO manageItem(ItemDTO itemDTO) throws Exception {
        Item item;
        boolean isUpdate = CommonValidators.validLong(itemDTO.getId());

        if (isUpdate) {
            item = this.itemRepository.findById(itemDTO.getId()).orElseThrow(() -> new Exception("Item not found!"));
            if (this.itemRepository.existsByCodeAndIdNot(itemDTO.getCode(), itemDTO.getId())) {
                throw new Exception("Item code already exists!");
            }
            if (CommonValidators.validString(itemDTO.getSku()) && this.itemRepository.existsBySkuAndIdNot(itemDTO.getSku(), itemDTO.getId())) {
                throw new Exception("SKU already exists!");
            }
            if (CommonValidators.validString(itemDTO.getBarcode()) && this.itemRepository.existsByBarcodeAndIdNot(itemDTO.getBarcode(), itemDTO.getId())) {
                throw new Exception("Barcode already exists!");
            }
        } else {
            item = new Item();
            if (this.itemRepository.existsByCode(itemDTO.getCode())) {
                throw new Exception("Item code already exists!");
            }
            if (CommonValidators.validString(itemDTO.getSku()) && this.itemRepository.existsBySku(itemDTO.getSku())) {
                throw new Exception("SKU already exists!");
            }
            if (CommonValidators.validString(itemDTO.getBarcode()) && this.itemRepository.existsByBarcode(itemDTO.getBarcode())) {
                throw new Exception("Barcode already exists!");
            }
        }

        item.setCode(itemDTO.getCode());
        item.setSku(itemDTO.getSku());
        item.setBarcode(itemDTO.getBarcode());
        item.setName(itemDTO.getName());
        item.setDescription(itemDTO.getDescription());
        item.setItemType(itemDTO.getItemType());
        item.setTrackingType(itemDTO.getTrackingType());
        item.setCostingMethod(itemDTO.getCostingMethod());
        item.setStandardCost(itemDTO.getStandardCost());
        item.setSalePrice(itemDTO.getSalePrice());
        item.setTaxRate(itemDTO.getTaxRate());
        item.setMinQty(itemDTO.getMinQty());
        item.setMaxQty(itemDTO.getMaxQty());
        item.setReorderLevel(itemDTO.getReorderLevel());
        item.setReorderQty(itemDTO.getReorderQty());
        item.setHasExpiry(itemDTO.getHasExpiry());
        item.setShelfLifeDays(itemDTO.getShelfLifeDays());
        item.setStatus(itemDTO.getStatus());

        ItemCategory category = this.categoryRepository.findById(itemDTO.getCategoryId())
                .orElseThrow(() -> new Exception("Category not found!"));
        item.setCategory(category);

        if (CommonValidators.validLong(itemDTO.getBrandId())) {
            Brand brand = this.brandRepository.findById(itemDTO.getBrandId())
                    .orElseThrow(() -> new Exception("Brand not found!"));
            item.setBrand(brand);
        } else {
            item.setBrand(null);
        }

        UnitOfMeasure baseUom = this.uomRepository.findById(itemDTO.getBaseUomId())
                .orElseThrow(() -> new Exception("Base unit of measure not found!"));
        item.setBaseUom(baseUom);

        if (isUpdate && this.stockLedgerRepository.existsByItemId(item.getId()) && !item.getTrackingType().equals(itemDTO.getTrackingType())) {
            throw new Exception("Cannot change tracking type after stock movements have been posted for this item!");
        }

        Item saved = this.itemRepository.save(item);
        return new ItemDTO(saved);
    }

    @Override
    public ItemDTO getItemById(Long id) throws Exception {
        Item item = this.itemRepository.findById(id).orElseThrow(() -> new Exception("Item not found!"));
        return new ItemDTO(item);
    }

    @Override
    public SearchResultDTO<ItemDTO> searchItems(ItemSearchDTO searchDTO, Pageable pageable) throws Exception {
        try {
            Specification<Item> spec = ItemSpecification.withSearchCriteria(searchDTO);
            Page<Item> page = this.itemRepository.findAll(spec, pageable);
            return convertToSearchResult(page);
        } catch (Exception e) {
            throw new Exception("Error searching items: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean deleteItem(Long id) throws Exception {
        Item item = this.itemRepository.findById(id).orElseThrow(() -> new Exception("Item not found!"));

        if (this.stockBalanceRepository.existsByItemId(id) || this.stockLedgerRepository.existsByItemId(id)) {
            throw new Exception("Cannot delete: item has stock movement history!");
        }

        this.itemRepository.delete(item);
        return true;
    }

    private SearchResultDTO<ItemDTO> convertToSearchResult(Page<Item> page) {
        SearchResultDTO<ItemDTO> result = new SearchResultDTO<>();
        result.setPageNo(page.getNumber());
        result.setLimit(page.getSize());
        result.setTotalPage(page.getTotalPages());
        result.setTotalRecords((int) page.getTotalElements());
        result.setPageCount(page.getNumberOfElements());
        result.setHasNextPage(page.hasNext());

        List<ItemDTO> dtos = page.getContent().stream().map(ItemDTO::new).collect(Collectors.toList());
        result.setResults(dtos);
        return result;
    }
}