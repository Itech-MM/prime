package org.flexitech.projects.erp.services.product;

import java.util.List;
import java.util.stream.Collectors;

import org.flexitech.projects.erp.commons.CommonValidators;
import org.flexitech.projects.erp.dto.SearchResultDTO;
import org.flexitech.projects.erp.dto.product.ProductDTO;
import org.flexitech.projects.erp.dto.product.ProductSearchDTO;
import org.flexitech.projects.erp.persistence.entities.product.Product;
import org.flexitech.projects.erp.persistence.entities.product.ProductFeature;
import org.flexitech.projects.erp.persistence.entities.product.ProductFeatureProduct;
import org.flexitech.projects.erp.persistence.repositories.product.ProductFeatureProductRepository;
import org.flexitech.projects.erp.persistence.repositories.product.ProductFeatureRepository;
import org.flexitech.projects.erp.persistence.repositories.product.ProductRepository;
import org.flexitech.projects.erp.services.specifications.product.ProductSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductServiceImpl implements ProductService {

	private final ProductRepository productRepository;
	private final ProductFeatureRepository productFeatureRepository;
	private final ProductFeatureProductRepository productFeatureProductRepository;
	private final ProductKeyService productKeyService;

	public ProductServiceImpl(ProductRepository productRepository, ProductFeatureRepository productFeatureRepository,
			ProductFeatureProductRepository productFeatureProductRepository, ProductKeyService productKeyService) {
		this.productRepository = productRepository;
		this.productFeatureRepository = productFeatureRepository;
		this.productFeatureProductRepository = productFeatureProductRepository;
		this.productKeyService = productKeyService;
	}

	@Override
	@Transactional
	public ProductDTO manageProduct(ProductDTO productDTO) throws Exception {
		Product product = null;
		boolean isNew = !CommonValidators.validLong(productDTO.getId());

		if (!isNew) {
			product = this.productRepository.findById(productDTO.getId())
					.orElseThrow(() -> new Exception("Product not found!"));
		} else {
			product = new Product();
		}

		product.setCode(productDTO.getCode());
		product.setName(productDTO.getName());
		product.setStatus(productDTO.getStatus());

		Product saved = this.productRepository.save(product);

		if (isNew) {
			this.productKeyService.generateAndAssignKeyPair(saved);
			saved = this.productRepository.save(saved);
		}

		this.productFeatureProductRepository.deleteByProductId(saved.getId());
		if (CommonValidators.isValidObject(productDTO.getFeatureIds()) && !productDTO.getFeatureIds().isEmpty()) {
			List<ProductFeature> features = this.productFeatureRepository.findAllById(productDTO.getFeatureIds());
			for (ProductFeature feature : features) {
				ProductFeatureProduct link = new ProductFeatureProduct();
				link.setProduct(saved);
				link.setFeature(feature);
				this.productFeatureProductRepository.save(link);
			}
		}

		return new ProductDTO(saved);
	}

	@Override
	public ProductDTO getProductById(Long id) throws Exception {
		Product product = this.productRepository.findById(id)
				.orElseThrow(() -> new Exception("Product not found!"));
		List<ProductFeatureProduct> links = this.productFeatureProductRepository.findByProductId(id);
		return new ProductDTO(product, links);
	}

	@Override
	public SearchResultDTO<ProductDTO> searchProducts(ProductSearchDTO searchDTO, Pageable pageable) throws Exception {
		try {
			Specification<Product> spec = ProductSpecification.withSearchCriteria(searchDTO);
			Page<Product> productPage = productRepository.findAll(spec, pageable);
			return convertToCommonSearchDTO(productPage);
		} catch (Exception e) {
			throw new Exception("Error searching products: " + e.getMessage(), e);
		}
	}

	@Override
	public boolean deleteProduct(Long id) throws Exception {
		Product product = this.productRepository.findById(id)
				.orElseThrow(() -> new Exception("Product not found!"));
		this.productFeatureProductRepository.deleteByProductId(id);
		this.productRepository.delete(product);
		return true;
	}

	private SearchResultDTO<ProductDTO> convertToCommonSearchDTO(Page<Product> productPage) {
		SearchResultDTO<ProductDTO> result = new SearchResultDTO<>();

		result.setPageNo(productPage.getNumber());
		result.setLimit(productPage.getSize());
		result.setTotalPage(productPage.getTotalPages());
		result.setTotalRecords((int) productPage.getTotalElements());
		result.setPageCount(productPage.getNumberOfElements());
		result.setHasNextPage(productPage.hasNext());

		List<ProductDTO> productDTOs = productPage.getContent().stream()
				.map(product -> {
					List<ProductFeatureProduct> links = this.productFeatureProductRepository.findByProductId(product.getId());
					return new ProductDTO(product, links);
				})
				.collect(Collectors.toList());

		result.setResults(productDTOs);
		return result;
	}
	
}