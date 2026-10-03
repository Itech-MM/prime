package org.flexitech.projects.erp.commons.security;

import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public final class PemKeys {

	private PemKeys() {
	}

	public static PublicKey readPublicKey(String pem) {
		try {
			byte[] der = decode(pem, "PUBLIC KEY");
			return KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(der));
		} catch (GeneralSecurityException e) {
			throw new IllegalStateException("Invalid license public key", e);
		}
	}

	public static PrivateKey readPrivateKey(String pem) {
		try {
			byte[] der = decode(pem, "PRIVATE KEY");
			return KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(der));
		} catch (GeneralSecurityException e) {
			throw new IllegalStateException("Invalid license private key", e);
		}
	}

	public static String writePublicKey(PublicKey key) {
		return encode(key.getEncoded(), "PUBLIC KEY");
	}

	public static String writePrivateKey(PrivateKey key) {
		return encode(key.getEncoded(), "PRIVATE KEY");
	}

	public static String writePublicKey(KeyPair pair) {
		return writePublicKey(pair.getPublic());
	}

	public static String writePrivateKey(KeyPair pair) {
		return writePrivateKey(pair.getPrivate());
	}

	private static byte[] decode(String pem, String label) {
		String body = pem
				.replace("-----BEGIN " + label + "-----", "")
				.replace("-----END " + label + "-----", "")
				.replaceAll("\\s", "");
		return Base64.getDecoder().decode(body);
	}

	private static String encode(byte[] der, String label) {
		String base64 = Base64.getEncoder().encodeToString(der);
		StringBuilder sb = new StringBuilder();
		sb.append("-----BEGIN ").append(label).append("-----\n");
		for (int i = 0; i < base64.length(); i += 64) {
			sb.append(base64, i, Math.min(i + 64, base64.length())).append("\n");
		}
		sb.append("-----END ").append(label).append("-----\n");
		return sb.toString();
	}
}