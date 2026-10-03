package org.flexitech.projects.erp.services.customer;

import java.util.List;
import java.util.stream.Collectors;

import org.flexitech.projects.erp.commons.CommonValidators;
import org.flexitech.projects.erp.dto.SearchResultDTO;
import org.flexitech.projects.erp.dto.customer.CustomerDTO;
import org.flexitech.projects.erp.dto.customer.CustomerSearchDTO;
import org.flexitech.projects.erp.persistence.entities.customer.Customer;
import org.flexitech.projects.erp.persistence.repositories.customer.CustomerRepository;
import org.flexitech.projects.erp.services.specifications.customer.CustomerSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class CustomerServiceImpl implements CustomerService {

	private final CustomerRepository customerRepository;

	public CustomerServiceImpl(CustomerRepository customerRepository) {
		this.customerRepository = customerRepository;
	}

	@Override
	public CustomerDTO manageCustomer(CustomerDTO customerDTO) throws Exception {
		Customer customer = null;
		if (CommonValidators.validLong(customerDTO.getId())) {
			customer = this.customerRepository.findById(customerDTO.getId())
					.orElseThrow(() -> new Exception("Customer not found!"));
		} else {
			customer = new Customer();
		}

		customer.setCode(customerDTO.getCode());
		customer.setName(customerDTO.getName());
		customer.setContactEmail(customerDTO.getContactEmail());
		customer.setStatus(customerDTO.getStatus());

		Customer saved = this.customerRepository.save(customer);
		return new CustomerDTO(saved);
	}

	@Override
	public CustomerDTO getCustomerById(Long id) throws Exception {
		Customer customer = this.customerRepository.findById(id)
				.orElseThrow(() -> new Exception("Customer not found!"));
		return new CustomerDTO(customer);
	}

	@Override
	public SearchResultDTO<CustomerDTO> searchCustomers(CustomerSearchDTO searchDTO, Pageable pageable) throws Exception {
		try {
			Specification<Customer> spec = CustomerSpecification.withSearchCriteria(searchDTO);
			Page<Customer> customerPage = customerRepository.findAll(spec, pageable);
			return convertToCommonSearchDTO(customerPage);
		} catch (Exception e) {
			throw new Exception("Error searching customers: " + e.getMessage(), e);
		}
	}

	@Override
	public boolean deleteCustomer(Long id) throws Exception {
		Customer customer = this.customerRepository.findById(id)
				.orElseThrow(() -> new Exception("Customer not found!"));
		this.customerRepository.delete(customer);
		return true;
	}

	private SearchResultDTO<CustomerDTO> convertToCommonSearchDTO(Page<Customer> customerPage) {
		SearchResultDTO<CustomerDTO> result = new SearchResultDTO<>();

		result.setPageNo(customerPage.getNumber());
		result.setLimit(customerPage.getSize());
		result.setTotalPage(customerPage.getTotalPages());
		result.setTotalRecords((int) customerPage.getTotalElements());
		result.setPageCount(customerPage.getNumberOfElements());
		result.setHasNextPage(customerPage.hasNext());

		List<CustomerDTO> customerDTOs = customerPage.getContent().stream().map(CustomerDTO::new).collect(Collectors.toList());

		result.setResults(customerDTOs);
		return result;
	}
}