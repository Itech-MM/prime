package org.flexitech.projects.erp.services.inventory;

public interface DocumentSequenceService {

	String nextDocNo(String docType, String prefix) throws Exception;
}