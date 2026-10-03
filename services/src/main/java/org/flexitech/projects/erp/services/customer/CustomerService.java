package org.flexitech.projects.erp.services.customer;

import org.flexitech.projects.erp.dto.SearchResultDTO;
import org.flexitech.projects.erp.dto.customer.CustomerDTO;
import org.flexitech.projects.erp.dto.customer.CustomerSearchDTO;
import org.springframework.data.domain.Pageable;

public interface CustomerService {

	CustomerDTO manageCustomer(CustomerDTO customerDTO) throws Exception;

	CustomerDTO getCustomerById(Long id) throws Exception;

	SearchResultDTO<CustomerDTO> searchCustomers(CustomerSearchDTO searchDTO, Pageable pageable) throws Exception;

	boolean deleteCustomer(Long id) throws Exception;
}