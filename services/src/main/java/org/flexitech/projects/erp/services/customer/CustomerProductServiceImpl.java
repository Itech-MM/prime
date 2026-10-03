package org.flexitech.projects.erp.services.customer;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.flexitech.projects.erp.dto.SearchResultDTO;
import org.flexitech.projects.erp.dto.customer.CustomerProductDTO;
import org.flexitech.projects.erp.dto.customer.CustomerProductSearchDTO;
import org.flexitech.projects.erp.dto.license.LicenseDTO;
import org.flexitech.projects.erp.persistence.entities.customer.Customer;
import org.flexitech.projects.erp.persistence.entities.customer.CustomerProduct;
import org.flexitech.projects.erp.persistence.entities.product.Product;
import org.flexitech.projects.erp.persistence.repositories.customer.CustomerProductRepository;
import org.flexitech.projects.erp.persistence.repositories.customer.CustomerRepository;
import org.flexitech.projects.erp.persistence.repositories.product.ProductRepository;
import org.flexitech.projects.erp.services.license.LicenseService;
import org.flexitech.projects.erp.services.specifications.customer.CustomerProductSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomerProductServiceImpl implements CustomerProductService {

	private final CustomerProductRepository customerProductRepository;
	private final CustomerRepository customerRepository;
	private final ProductRepository productRepository;
	private final LicenseService licenseService;

	public CustomerProductServiceImpl(CustomerProductRepository customerProductRepository,
			CustomerRepository customerRepository, ProductRepository productRepository,
			LicenseService licenseService) {
		this.customerProductRepository = customerProductRepository;
		this.customerRepository = customerRepository;
		this.productRepository = productRepository;
		this.licenseService = licenseService;
	}

	@Override
	@Transactional
	public CustomerProductDTO purchaseProduct(Long customerId, Long productId, Long planId) throws Exception {
		Customer customer = this.customerRepository.findById(customerId)
				.orElseThrow(() -> new Exception("Customer not found!"));
		Product product = this.productRepository.findById(productId)
				.orElseThrow(() -> new Exception("Product not found!"));

		CustomerProduct customerProduct = new CustomerProduct();
		customerProduct.setCustomer(customer);
		customerProduct.setProduct(product);
		customerProduct.setStatus(1);
		customerProduct.setPurchasedAt(new Date());

		CustomerProduct saved = this.customerProductRepository.save(customerProduct);

		LicenseDTO license = this.licenseService.createLicenseForCustomerProduct(saved.getId(), planId);

		return new CustomerProductDTO(saved, license);
	}

	@Override
	public CustomerProductDTO getCustomerProductById(Long id) throws Exception {
		CustomerProduct customerProduct = this.customerProductRepository.findById(id)
				.orElseThrow(() -> new Exception("Customer product not found!"));
		LicenseDTO license = this.licenseService.getLicenseByCustomerProductId(id);
		return new CustomerProductDTO(customerProduct, license);
	}

	@Override
	public SearchResultDTO<CustomerProductDTO> searchCustomerProducts(CustomerProductSearchDTO searchDTO, Pageable pageable) throws Exception {
		try {
			Specification<CustomerProduct> spec = CustomerProductSpecification.withSearchCriteria(searchDTO);
			Page<CustomerProduct> page = customerProductRepository.findAll(spec, pageable);
			return convertToCommonSearchDTO(page);
		} catch (Exception e) {
			throw new Exception("Error searching customer products: " + e.getMessage(), e);
		}
	}

	@Override
	public boolean deleteCustomerProduct(Long id) throws Exception {
		CustomerProduct customerProduct = this.customerProductRepository.findById(id)
				.orElseThrow(() -> new Exception("Customer product not found!"));
		this.customerProductRepository.delete(customerProduct);
		return true;
	}

	private SearchResultDTO<CustomerProductDTO> convertToCommonSearchDTO(Page<CustomerProduct> page) {
		SearchResultDTO<CustomerProductDTO> result = new SearchResultDTO<>();

		result.setPageNo(page.getNumber());
		result.setLimit(page.getSize());
		result.setTotalPage(page.getTotalPages());
		result.setTotalRecords((int) page.getTotalElements());
		result.setPageCount(page.getNumberOfElements());
		result.setHasNextPage(page.hasNext());

		List<CustomerProductDTO> dtos = page.getContent().stream()
				.map(cp -> {
					try {
						return new CustomerProductDTO(cp, this.licenseService.getLicenseByCustomerProductId(cp.getId()));
					} catch (Exception e) {
						return new CustomerProductDTO(cp);
					}
				})
				.collect(Collectors.toList());

		result.setResults(dtos);
		return result;
	}
}