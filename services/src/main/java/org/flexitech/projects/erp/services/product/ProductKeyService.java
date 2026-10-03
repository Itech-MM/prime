package org.flexitech.projects.erp.services.product;

import org.flexitech.projects.erp.persistence.entities.product.Product;

public interface ProductKeyService {
	void generateAndAssignKeyPair(Product product) throws Exception;
}