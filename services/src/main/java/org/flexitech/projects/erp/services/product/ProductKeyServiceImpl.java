package org.flexitech.projects.erp.services.product;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.util.Calendar;
import java.util.UUID;

import org.flexitech.projects.erp.commons.CommonConstants;
import org.flexitech.projects.erp.commons.enums.ProductKeyStatus;
import org.flexitech.projects.erp.commons.security.PemKeys;
import org.flexitech.projects.erp.commons.utils.FileUtils;
import org.flexitech.projects.erp.persistence.entities.product.Product;
import org.springframework.stereotype.Service;

@Service
public class ProductKeyServiceImpl implements ProductKeyService {

	private static final String ENTITY_TYPE = "product";

	private final FileUtils fileUtils;

	public ProductKeyServiceImpl(FileUtils fileUtils) {
		this.fileUtils = fileUtils;
	}

	@Override
	public void generateAndAssignKeyPair(Product product) throws Exception {
		KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
		generator.initialize(2048);
		KeyPair keyPair = generator.generateKeyPair();

		String randomId = UUID.randomUUID().toString();
		String privateKeyFileName = randomId + "-pri.pem";
		String publicKeyFileName = randomId + "-pub.pem";

		String privateKeyLocation = this.fileUtils.writeKeyFile(PemKeys.writePrivateKey(keyPair), ENTITY_TYPE,
				product.getId(), privateKeyFileName);
		String publicKeyLocation = this.fileUtils.writeKeyFile(PemKeys.writePublicKey(keyPair), ENTITY_TYPE,
				product.getId(), publicKeyFileName);

		product.setKeyAlgorithm(CommonConstants.RSA_2048);
		product.setPrivateKeyLocation(privateKeyLocation);
		product.setPublicKeyLocation(publicKeyLocation);
		product.setKeyGeneratedAt(Calendar.getInstance().getTime());
		product.setKeyStatus(ProductKeyStatus.ACTIVE.getCode());
	}
}