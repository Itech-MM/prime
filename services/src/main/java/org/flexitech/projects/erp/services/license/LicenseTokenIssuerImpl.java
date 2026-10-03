package org.flexitech.projects.erp.services.license;

import java.security.PrivateKey;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.flexitech.projects.erp.commons.CommonValidators;
import org.flexitech.projects.erp.commons.security.PemKeys;
import org.flexitech.projects.erp.commons.utils.FileUtils;
import org.flexitech.projects.erp.dto.license.LicenseDTO;
import org.flexitech.projects.erp.persistence.entities.license.License;
import org.flexitech.projects.erp.persistence.entities.license.LicensePlan;
import org.flexitech.projects.erp.persistence.entities.product.Product;
import org.flexitech.projects.erp.persistence.repositories.license.LicenseRepository;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Service
public class LicenseTokenIssuerImpl implements LicenseTokenIssuer {

	private static final String ANY_FINGERPRINT = "ANY";
	private static final String ENTITY_TYPE = "license";

	private final LicenseRepository licenseRepository;
	private final LicenseAuditLogService licenseAuditLogService;
	private final FileUtils fileUtils;

	public LicenseTokenIssuerImpl(LicenseRepository licenseRepository, LicenseAuditLogService licenseAuditLogService,
			FileUtils fileUtils) {
		this.licenseRepository = licenseRepository;
		this.licenseAuditLogService = licenseAuditLogService;
		this.fileUtils = fileUtils;
	}

	@SuppressWarnings("deprecation")
	@Override
	public LicenseDTO issueToken(Long licenseId) throws Exception {
		License license = this.licenseRepository.findById(licenseId)
				.orElseThrow(() -> new Exception("License not found!"));

		LicensePlan plan = license.getPlan();
		Product product = plan.getProduct();

		if (!CommonValidators.validString(product.getPrivateKeyLocation())) {
			throw new Exception("Product does not have a signing key generated!");
		}

		String pkg = product.getCode() + "_" + plan.getCode();

		List<String> features = plan.getFeatures().stream()
				.map(f -> f.getCode())
				.collect(Collectors.toList());

		String fingerprint = CommonValidators.validString(license.getFingerprint())
				? license.getFingerprint()
				: ANY_FINGERPRINT;

		Map<String, Object> claims = new LinkedHashMap<>();
		claims.put("lid", license.getCode());
		claims.put("cust", license.getCustomerProduct().getCustomer().getCode());
		claims.put("pkg", pkg);
		claims.put("maxClients", plan.getMaxClients());
		claims.put("maxSites", plan.getMaxSites());
		claims.put("maxGates", plan.getMaxGates());
		claims.put("features", features);
		claims.put("fp", fingerprint);
		claims.put("validFrom", license.getIssuedAt().toInstant().getEpochSecond());
		claims.put("validUntil", license.getExpiresAt().toInstant().getEpochSecond());
		claims.put("graceDays", license.getGraceDays());

		String privateKeyPem = this.fileUtils.readFileAsString(product.getPrivateKeyLocation());
		PrivateKey privateKey = PemKeys.readPrivateKey(privateKeyPem);

		String token = Jwts.builder()
				.setClaims(claims)
				.setIssuedAt(new Date())
				.signWith(SignatureAlgorithm.RS256, privateKey)
				.compact();

		String fileName = UUID.randomUUID().toString() + "-token.fxs";
		String tokenLocation = this.fileUtils.writeKeyFile(token, ENTITY_TYPE, licenseId, fileName);

		license.setTokenLocation(tokenLocation);
		License saved = this.licenseRepository.save(license);

		this.licenseAuditLogService.log(licenseId, "TOKEN_ISSUED", "Token issued for license code " + license.getCode());

		return new LicenseDTO(saved);
	}
}