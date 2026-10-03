package org.flexitech.projects.erp.services.inventory;

import org.flexitech.projects.erp.dto.SearchResultDTO;
import org.flexitech.projects.erp.dto.inventory.ItemDTO;
import org.flexitech.projects.erp.dto.inventory.search.ItemSearchDTO;
import org.springframework.data.domain.Pageable;

public interface ItemService {

    ItemDTO manageItem(ItemDTO itemDTO) throws Exception;

    ItemDTO getItemById(Long id) throws Exception;

    SearchResultDTO<ItemDTO> searchItems(ItemSearchDTO searchDTO, Pageable pageable) throws Exception;

    boolean deleteItem(Long id) throws Exception;
}