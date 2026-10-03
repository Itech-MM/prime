package org.flexitech.projects.erp.services.customer;

import org.flexitech.projects.erp.dto.SearchResultDTO;
import org.flexitech.projects.erp.dto.customer.CustomerProductDTO;
import org.flexitech.projects.erp.dto.customer.CustomerProductSearchDTO;
import org.springframework.data.domain.Pageable;

public interface CustomerProductService {

	CustomerProductDTO purchaseProduct(Long customerId, Long productId, Long planId) throws Exception;

	CustomerProductDTO getCustomerProductById(Long id) throws Exception;

	SearchResultDTO<CustomerProductDTO> searchCustomerProducts(CustomerProductSearchDTO searchDTO, Pageable pageable) throws Exception;

	boolean deleteCustomerProduct(Long id) throws Exception;
}