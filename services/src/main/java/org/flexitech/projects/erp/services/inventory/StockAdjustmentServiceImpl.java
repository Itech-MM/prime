package org.flexitech.projects.erp.services.inventory;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.flexitech.projects.erp.commons.CommonConstants;
import org.flexitech.projects.erp.commons.CommonValidators;
import org.flexitech.projects.erp.commons.InventoryRefDocTypes;
import org.flexitech.projects.erp.commons.enums.InventoryDocStatus;
import org.flexitech.projects.erp.commons.utils.DateUtils;
import org.flexitech.projects.erp.dto.SearchResultDTO;
import org.flexitech.projects.erp.dto.inventory.StockAdjustmentDTO;
import org.flexitech.projects.erp.dto.inventory.StockAdjustmentLineDTO;
import org.flexitech.projects.erp.dto.inventory.search.StockAdjustmentSearchDTO;
import org.flexitech.projects.erp.persistence.entities.inventory.Item;
import org.flexitech.projects.erp.persistence.entities.inventory.StockBatch;
import org.flexitech.projects.erp.persistence.entities.inventory.StockAdjustment;
import org.flexitech.projects.erp.persistence.entities.inventory.StockAdjustmentLine;
import org.flexitech.projects.erp.persistence.entities.inventory.Warehouse;
import org.flexitech.projects.erp.persistence.entities.inventory.WarehouseLocation;
import org.flexitech.projects.erp.persistence.entities.user.User;
import org.flexitech.projects.erp.persistence.repositories.inventory.ItemRepository;
import org.flexitech.projects.erp.persistence.repositories.inventory.StockAdjustmentRepository;
import org.flexitech.projects.erp.persistence.repositories.inventory.StockBalanceRepository;
import org.flexitech.projects.erp.persistence.repositories.inventory.StockBatchRepository;
import org.flexitech.projects.erp.persistence.repositories.inventory.WarehouseLocationRepository;
import org.flexitech.projects.erp.persistence.repositories.inventory.WarehouseRepository;
import org.flexitech.projects.erp.services.auth.AuthenticationService;
import org.flexitech.projects.erp.services.specifications.inventory.StockAdjustmentSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class StockAdjustmentServiceImpl implements StockAdjustmentService {

	private static final String DOC_TYPE = "ADJ";
	private static final String DOC_PREFIX = "ADJ";

	private final StockAdjustmentRepository adjustmentRepository;
	private final ItemRepository itemRepository;
	private final WarehouseRepository warehouseRepository;
	private final WarehouseLocationRepository locationRepository;
	private final StockBatchRepository stockBatchRepository;
	private final StockBalanceRepository stockBalanceRepository;
	private final DocumentSequenceService documentSequenceService;
	private final StockMovementService stockMovementService;
	private final AuthenticationService authenticationService;

	public StockAdjustmentServiceImpl(StockAdjustmentRepository adjustmentRepository, ItemRepository itemRepository,
			WarehouseRepository warehouseRepository, WarehouseLocationRepository locationRepository,
			StockBatchRepository stockBatchRepository, StockBalanceRepository stockBalanceRepository,
			DocumentSequenceService documentSequenceService, StockMovementService stockMovementService,
			AuthenticationService authenticationService) {
		this.adjustmentRepository = adjustmentRepository;
		this.itemRepository = itemRepository;
		this.warehouseRepository = warehouseRepository;
		this.locationRepository = locationRepository;
		this.stockBatchRepository = stockBatchRepository;
		this.stockBalanceRepository = stockBalanceRepository;
		this.documentSequenceService = documentSequenceService;
		this.stockMovementService = stockMovementService;
		this.authenticationService = authenticationService;
	}

	@Override
	@Transactional
	public StockAdjustmentDTO saveDraft(StockAdjustmentDTO adjustmentDTO) throws Exception {

		StockAdjustment adjustment;
		boolean isUpdate = CommonValidators.validLong(adjustmentDTO.getId());

		if (isUpdate) {
			adjustment = this.adjustmentRepository.findById(adjustmentDTO.getId()).orElseThrow(() -> new Exception("Stock adjustment not found!"));
			if (!InventoryDocStatus.DRAFT.getCode().equals(adjustment.getStatus())) {
				throw new Exception("Only draft stock adjustments can be edited!");
			}
		} else {
			adjustment = new StockAdjustment();
			adjustment.setDocNo(this.documentSequenceService.nextDocNo(DOC_TYPE, DOC_PREFIX));
			adjustment.setStatus(InventoryDocStatus.DRAFT.getCode());
		}

		Warehouse warehouse = this.warehouseRepository.findById(adjustmentDTO.getWarehouseId())
				.orElseThrow(() -> new Exception("Warehouse not found!"));

		adjustment.setWarehouse(warehouse);
		adjustment.setReason(adjustmentDTO.getReason());
		adjustment.setRemarks(adjustmentDTO.getRemarks());

		if (CommonValidators.validString(adjustmentDTO.getAdjustmentDate())) {
			adjustment.setAdjustmentDate(DateUtils.stringToDate(adjustmentDTO.getAdjustmentDate(), CommonConstants.STANDARD_12_HOUR_DATE_MINUTE_FORMAT));
		}

		if (adjustmentDTO.getLines() == null || adjustmentDTO.getLines().isEmpty()) {
			throw new Exception("At least one line item is required!");
		}

		adjustment.getLines().clear();

		for (StockAdjustmentLineDTO lineDTO : adjustmentDTO.getLines()) {
			adjustment.getLines().add(buildLine(adjustment, lineDTO));
		}

		StockAdjustment saved = this.adjustmentRepository.save(adjustment);
		return new StockAdjustmentDTO(saved);
	}

	private StockAdjustmentLine buildLine(StockAdjustment adjustment, StockAdjustmentLineDTO lineDTO) throws Exception {

		Item item = this.itemRepository.findById(lineDTO.getItemId()).orElseThrow(() -> new Exception("Item not found!"));
		WarehouseLocation location = this.locationRepository.findById(lineDTO.getLocationId())
				.orElseThrow(() -> new Exception("Location not found!"));

		if (!location.getWarehouse().getId().equals(adjustment.getWarehouse().getId())) {
			throw new Exception("Location " + location.getName() + " does not belong to the selected warehouse!");
		}

		StockBatch batch = null;
		if (CommonValidators.validLong(lineDTO.getBatchId())) {
			batch = this.stockBatchRepository.findById(lineDTO.getBatchId()).orElseThrow(() -> new Exception("Batch not found!"));
			if (!batch.getItem().getId().equals(item.getId())) {
				throw new Exception("Selected batch does not belong to item " + item.getName() + "!");
			}
		}

		BigDecimal systemQty = CommonValidators.isValidObject(batch)
				? this.stockBalanceRepository.findByItemIdAndLocationIdAndBatchId(item.getId(), location.getId(), batch.getId())
						.map(b -> b.getQtyOnHand()).orElse(BigDecimal.ZERO)
				: this.stockBalanceRepository.findByItemIdAndLocationIdAndBatchIsNull(item.getId(), location.getId())
						.map(b -> b.getQtyOnHand()).orElse(BigDecimal.ZERO);

		if (lineDTO.getActualQty() == null) {
			throw new Exception("Actual quantity is required for item " + item.getName() + "!");
		}

		StockAdjustmentLine line = new StockAdjustmentLine();
		line.setStockAdjustment(adjustment);
		line.setItem(item);
		line.setLocation(location);
		line.setBatch(batch);
		line.setSystemQty(systemQty);
		line.setActualQty(lineDTO.getActualQty());
		line.setDifferenceQty(lineDTO.getActualQty().subtract(systemQty));
		line.setUnitCost(lineDTO.getUnitCost());
		line.setRemarks(lineDTO.getRemarks());

		return line;
	}

	@Override
	public StockAdjustmentDTO getAdjustmentById(Long id) throws Exception {
		StockAdjustment adjustment = this.adjustmentRepository.findById(id).orElseThrow(() -> new Exception("Stock adjustment not found!"));
		return new StockAdjustmentDTO(adjustment);
	}

	@Override
	public SearchResultDTO<StockAdjustmentDTO> searchAdjustments(StockAdjustmentSearchDTO searchDTO, Pageable pageable) throws Exception {
		try {
			Specification<StockAdjustment> spec = StockAdjustmentSpecification.withSearchCriteria(searchDTO);
			Page<StockAdjustment> page = this.adjustmentRepository.findAll(spec, pageable);
			return convertToSearchResult(page);
		} catch (Exception e) {
			throw new Exception("Error searching stock adjustments: " + e.getMessage(), e);
		}
	}

	@Override
	@Transactional
	public StockAdjustmentDTO submit(Long id) throws Exception {
		StockAdjustment adjustment = this.adjustmentRepository.findById(id).orElseThrow(() -> new Exception("Stock adjustment not found!"));
		requireStatus(adjustment, InventoryDocStatus.DRAFT);
		adjustment.setStatus(InventoryDocStatus.SUBMITTED.getCode());
		return new StockAdjustmentDTO(this.adjustmentRepository.save(adjustment));
	}

	@Override
	@Transactional
	public StockAdjustmentDTO approve(Long id) throws Exception {
		StockAdjustment adjustment = this.adjustmentRepository.findById(id).orElseThrow(() -> new Exception("Stock adjustment not found!"));
		requireStatus(adjustment, InventoryDocStatus.SUBMITTED);

		User currentUser = this.authenticationService.getLoggedInUser();
		adjustment.setStatus(InventoryDocStatus.APPROVED.getCode());
		adjustment.setApprovedBy(currentUser);
		adjustment.setApprovedTime(new Date());

		return new StockAdjustmentDTO(this.adjustmentRepository.save(adjustment));
	}

	@Override
	@Transactional
	public StockAdjustmentDTO post(Long id) throws Exception {
		StockAdjustment adjustment = this.adjustmentRepository.findById(id).orElseThrow(() -> new Exception("Stock adjustment not found!"));
		requireStatus(adjustment, InventoryDocStatus.APPROVED);

		for (StockAdjustmentLine line : adjustment.getLines()) {
			Item item = line.getItem();

			BigDecimal currentQty = CommonValidators.isValidObject(line.getBatch())
					? this.stockBalanceRepository.findByItemIdAndLocationIdAndBatchId(item.getId(), line.getLocation().getId(), line.getBatch().getId())
							.map(b -> b.getQtyOnHand()).orElse(BigDecimal.ZERO)
					: this.stockBalanceRepository.findByItemIdAndLocationIdAndBatchIsNull(item.getId(), line.getLocation().getId())
							.map(b -> b.getQtyOnHand()).orElse(BigDecimal.ZERO);

			BigDecimal liveDifference = line.getActualQty().subtract(currentQty);

			line.setSystemQty(currentQty);
			line.setDifferenceQty(liveDifference);

			if (liveDifference.compareTo(BigDecimal.ZERO) != 0) {
				this.stockMovementService.postAdjustment(item, line.getLocation(), line.getBatch(), liveDifference, line.getUnitCost(),
						InventoryRefDocTypes.STOCK_ADJUSTMENT, adjustment.getId(), line.getId());
			}
		}

		User currentUser = this.authenticationService.getLoggedInUser();
		adjustment.setStatus(InventoryDocStatus.POSTED.getCode());
		adjustment.setPostedBy(currentUser);
		adjustment.setPostedTime(new Date());

		return new StockAdjustmentDTO(this.adjustmentRepository.save(adjustment));
	}

	@Override
	@Transactional
	public StockAdjustmentDTO cancel(Long id) throws Exception {
		StockAdjustment adjustment = this.adjustmentRepository.findById(id).orElseThrow(() -> new Exception("Stock adjustment not found!"));

		if (InventoryDocStatus.POSTED.getCode().equals(adjustment.getStatus())) {
			throw new Exception("Posted stock adjustments cannot be cancelled! Create a reversing adjustment instead.");
		}
		if (InventoryDocStatus.CANCELLED.getCode().equals(adjustment.getStatus())) {
			throw new Exception("This stock adjustment is already cancelled!");
		}

		adjustment.setStatus(InventoryDocStatus.CANCELLED.getCode());
		return new StockAdjustmentDTO(this.adjustmentRepository.save(adjustment));
	}

	@Override
	@Transactional
	public boolean deleteAdjustment(Long id) throws Exception {
		StockAdjustment adjustment = this.adjustmentRepository.findById(id).orElseThrow(() -> new Exception("Stock adjustment not found!"));

		if (!InventoryDocStatus.DRAFT.getCode().equals(adjustment.getStatus())) {
			throw new Exception("Only draft stock adjustments can be deleted!");
		}

		this.adjustmentRepository.delete(adjustment);
		return true;
	}

	private void requireStatus(StockAdjustment adjustment, InventoryDocStatus expected) throws Exception {
		if (!expected.getCode().equals(adjustment.getStatus())) {
			throw new Exception("Stock adjustment must be in " + expected.getDesc() + " status for this action! Current status: "
					+ InventoryDocStatus.getDescByCode(adjustment.getStatus()));
		}
	}

	private SearchResultDTO<StockAdjustmentDTO> convertToSearchResult(Page<StockAdjustment> page) {
		SearchResultDTO<StockAdjustmentDTO> result = new SearchResultDTO<>();
		result.setPageNo(page.getNumber());
		result.setLimit(page.getSize());
		result.setTotalPage(page.getTotalPages());
		result.setTotalRecords((int) page.getTotalElements());
		result.setPageCount(page.getNumberOfElements());
		result.setHasNextPage(page.hasNext());

		List<StockAdjustmentDTO> dtos = page.getContent().stream().map(StockAdjustmentDTO::new).collect(Collectors.toList());
		result.setResults(dtos);
		return result;
	}
}