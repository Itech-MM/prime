package org.flexitech.projects.erp.services.inventory;

import java.util.Calendar;
import java.util.Optional;

import org.flexitech.projects.erp.persistence.entities.inventory.DocumentSequence;
import org.flexitech.projects.erp.persistence.repositories.inventory.DocumentSequenceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DocumentSequenceServiceImpl implements DocumentSequenceService {

	private final DocumentSequenceRepository sequenceRepository;

	public DocumentSequenceServiceImpl(DocumentSequenceRepository sequenceRepository) {
		this.sequenceRepository = sequenceRepository;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public String nextDocNo(String docType, String prefix) throws Exception {

		int year = Calendar.getInstance().get(Calendar.YEAR);

		Optional<DocumentSequence> existing = this.sequenceRepository.findForUpdate(docType, year);
		DocumentSequence sequence;

		if (existing.isPresent()) {
			sequence = existing.get();
		} else {
			sequence = new DocumentSequence();
			sequence.setDocType(docType);
			sequence.setDocYear(year);
			sequence.setPrefix(prefix);
			sequence.setLastNumber(0L);
		}

		long nextNumber = sequence.getLastNumber() + 1;
		sequence.setLastNumber(nextNumber);
		this.sequenceRepository.save(sequence);

		return String.format("%s-%d-%06d", prefix, year, nextNumber);
	}
}