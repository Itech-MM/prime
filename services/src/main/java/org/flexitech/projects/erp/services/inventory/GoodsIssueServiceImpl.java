package org.flexitech.projects.erp.services.inventory;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.flexitech.projects.erp.commons.CommonConstants;
import org.flexitech.projects.erp.commons.CommonValidators;
import org.flexitech.projects.erp.commons.InventoryRefDocTypes;
import org.flexitech.projects.erp.commons.enums.InventoryAuditAction;
import org.flexitech.projects.erp.commons.enums.InventoryDocStatus;
import org.flexitech.projects.erp.commons.enums.StockSerialStatus;
import org.flexitech.projects.erp.commons.enums.TrackingType;
import org.flexitech.projects.erp.commons.utils.DateUtils;
import org.flexitech.projects.erp.dto.SearchResultDTO;
import org.flexitech.projects.erp.dto.inventory.GoodsIssueDTO;
import org.flexitech.projects.erp.dto.inventory.GoodsIssueLineDTO;
import org.flexitech.projects.erp.dto.inventory.search.GoodsIssueSearchDTO;
import org.flexitech.projects.erp.persistence.entities.customer.Customer;
import org.flexitech.projects.erp.persistence.entities.inventory.GoodsIssue;
import org.flexitech.projects.erp.persistence.entities.inventory.GoodsIssueLine;
import org.flexitech.projects.erp.persistence.entities.inventory.Item;
import org.flexitech.projects.erp.persistence.entities.inventory.StockBatch;
import org.flexitech.projects.erp.persistence.entities.inventory.StockSerial;
import org.flexitech.projects.erp.persistence.entities.inventory.UnitOfMeasure;
import org.flexitech.projects.erp.persistence.entities.inventory.UomConversion;
import org.flexitech.projects.erp.persistence.entities.inventory.Warehouse;
import org.flexitech.projects.erp.persistence.entities.inventory.WarehouseLocation;
import org.flexitech.projects.erp.persistence.entities.user.User;
import org.flexitech.projects.erp.persistence.repositories.customer.CustomerRepository;
import org.flexitech.projects.erp.persistence.repositories.inventory.GoodsIssueRepository;
import org.flexitech.projects.erp.persistence.repositories.inventory.ItemRepository;
import org.flexitech.projects.erp.persistence.repositories.inventory.StockBatchRepository;
import org.flexitech.projects.erp.persistence.repositories.inventory.StockSerialRepository;
import org.flexitech.projects.erp.persistence.repositories.inventory.UnitOfMeasureRepository;
import org.flexitech.projects.erp.persistence.repositories.inventory.UomConversionRepository;
import org.flexitech.projects.erp.persistence.repositories.inventory.WarehouseLocationRepository;
import org.flexitech.projects.erp.persistence.repositories.inventory.WarehouseRepository;
import org.flexitech.projects.erp.services.auth.AuthenticationService;
import org.flexitech.projects.erp.services.specifications.inventory.GoodsIssueSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class GoodsIssueServiceImpl implements GoodsIssueService {

	private static final String DOC_TYPE = "GI";
	private static final String DOC_PREFIX = "GI";

	private final GoodsIssueRepository issueRepository;
	private final ItemRepository itemRepository;
	private final WarehouseRepository warehouseRepository;
	private final WarehouseLocationRepository locationRepository;
	private final UnitOfMeasureRepository uomRepository;
	private final UomConversionRepository uomConversionRepository;
	private final StockBatchRepository stockBatchRepository;
	private final StockSerialRepository stockSerialRepository;
	private final CustomerRepository customerRepository;
	private final DocumentSequenceService documentSequenceService;
	private final StockMovementService stockMovementService;
	private final AuthenticationService authenticationService;
	private final InventoryAuditLogService auditLogService;

	public GoodsIssueServiceImpl(GoodsIssueRepository issueRepository, ItemRepository itemRepository,
			WarehouseRepository warehouseRepository, WarehouseLocationRepository locationRepository,
			UnitOfMeasureRepository uomRepository, UomConversionRepository uomConversionRepository,
			StockBatchRepository stockBatchRepository, StockSerialRepository stockSerialRepository,
			CustomerRepository customerRepository, DocumentSequenceService documentSequenceService,
			StockMovementService stockMovementService, AuthenticationService authenticationService,
			InventoryAuditLogService auditLogService) {
		this.issueRepository = issueRepository;
		this.itemRepository = itemRepository;
		this.warehouseRepository = warehouseRepository;
		this.locationRepository = locationRepository;
		this.uomRepository = uomRepository;
		this.uomConversionRepository = uomConversionRepository;
		this.stockBatchRepository = stockBatchRepository;
		this.stockSerialRepository = stockSerialRepository;
		this.customerRepository = customerRepository;
		this.documentSequenceService = documentSequenceService;
		this.stockMovementService = stockMovementService;
		this.authenticationService = authenticationService;
		this.auditLogService = auditLogService;
	}

	@Override
	@Transactional
	public GoodsIssueDTO saveDraft(GoodsIssueDTO issueDTO) throws Exception {

		GoodsIssue issue;
		boolean isUpdate = CommonValidators.validLong(issueDTO.getId());

		if (isUpdate) {
			issue = this.issueRepository.findById(issueDTO.getId()).orElseThrow(() -> new Exception("Goods issue not found!"));
			if (!InventoryDocStatus.DRAFT.getCode().equals(issue.getStatus())) {
				throw new Exception("Only draft goods issues can be edited!");
			}
		} else {
			issue = new GoodsIssue();
			issue.setDocNo(this.documentSequenceService.nextDocNo(DOC_TYPE, DOC_PREFIX));
			issue.setStatus(InventoryDocStatus.DRAFT.getCode());
		}

		Warehouse warehouse = this.warehouseRepository.findById(issueDTO.getWarehouseId())
				.orElseThrow(() -> new Exception("Warehouse not found!"));

		issue.setWarehouse(warehouse);
		issue.setIssueType(issueDTO.getIssueType());
		issue.setIssuedToName(issueDTO.getIssuedToName());
		issue.setRemarks(issueDTO.getRemarks());

		if (CommonValidators.validLong(issueDTO.getCustomerId())) {
			Customer customer = this.customerRepository.findById(issueDTO.getCustomerId())
					.orElseThrow(() -> new Exception("Customer not found!"));
			issue.setCustomer(customer);
		} else {
			issue.setCustomer(null);
		}

		if (CommonValidators.validString(issueDTO.getIssueDate())) {
			issue.setIssueDate(DateUtils.stringToDate(issueDTO.getIssueDate(), CommonConstants.STANDARD_12_HOUR_DATE_MINUTE_FORMAT));
		}

		if (issueDTO.getLines() == null || issueDTO.getLines().isEmpty()) {
			throw new Exception("At least one line item is required!");
		}

		issue.getLines().clear();

		for (GoodsIssueLineDTO lineDTO : issueDTO.getLines()) {
			GoodsIssueLine line = buildLine(issue, lineDTO);
			issue.getLines().add(line);
		}

		GoodsIssue saved = this.issueRepository.save(issue);
		this.auditLogService.log(InventoryRefDocTypes.GOODS_ISSUE, saved.getId(), saved.getDocNo(),
				isUpdate ? InventoryAuditAction.UPDATED.getCode() : InventoryAuditAction.CREATED.getCode(), null);

		return new GoodsIssueDTO(saved);
	}

	private GoodsIssueLine buildLine(GoodsIssue issue, GoodsIssueLineDTO lineDTO) throws Exception {

		Item item = this.itemRepository.findById(lineDTO.getItemId()).orElseThrow(() -> new Exception("Item not found!"));

		UnitOfMeasure uom = CommonValidators.validLong(lineDTO.getUomId())
				? this.uomRepository.findById(lineDTO.getUomId()).orElseThrow(() -> new Exception("Unit of measure not found!"))
				: item.getBaseUom();

		WarehouseLocation location = this.locationRepository.findById(lineDTO.getLocationId())
				.orElseThrow(() -> new Exception("Location not found!"));

		if (!location.getWarehouse().getId().equals(issue.getWarehouse().getId())) {
			throw new Exception("Location " + location.getName() + " does not belong to the selected warehouse!");
		}

		if (TrackingType.BATCH.getCode().equals(item.getTrackingType()) && !CommonValidators.validLong(lineDTO.getBatchId())) {
			throw new Exception("A batch must be selected for item " + item.getName() + "!");
		}
		if (TrackingType.SERIAL.getCode().equals(item.getTrackingType()) && !CommonValidators.validString(lineDTO.getSerialNos())) {
			throw new Exception("Serial numbers are required for item " + item.getName() + "!");
		}

		BigDecimal baseQty = convertToBaseQty(item, uom, lineDTO.getQty());

		if (TrackingType.SERIAL.getCode().equals(item.getTrackingType())) {
			long serialCount = splitSerials(lineDTO.getSerialNos()).size();
			if (BigDecimal.valueOf(serialCount).compareTo(baseQty) != 0) {
				throw new Exception("Number of serial numbers (" + serialCount + ") does not match quantity (" + baseQty + ") for item " + item.getName() + "!");
			}
		}

		GoodsIssueLine line = new GoodsIssueLine();
		line.setGoodsIssue(issue);
		line.setItem(item);
		line.setUom(uom);
		line.setLocation(location);
		line.setQty(lineDTO.getQty());
		line.setBaseQty(baseQty);
		line.setSerialNos(lineDTO.getSerialNos());

		if (CommonValidators.validLong(lineDTO.getBatchId())) {
			StockBatch batch = this.stockBatchRepository.findById(lineDTO.getBatchId())
					.orElseThrow(() -> new Exception("Batch not found!"));
			if (!batch.getItem().getId().equals(item.getId())) {
				throw new Exception("Selected batch does not belong to item " + item.getName() + "!");
			}
			line.setBatch(batch);
		} else {
			line.setBatch(null);
		}

		return line;
	}

	private BigDecimal convertToBaseQty(Item item, UnitOfMeasure lineUom, BigDecimal qty) throws Exception {

		if (lineUom.getId().equals(item.getBaseUom().getId())) {
			return qty;
		}

		UomConversion conversion = this.uomConversionRepository.findByFromUomIdAndToUomId(lineUom.getId(), item.getBaseUom().getId())
				.orElseThrow(() -> new Exception("No unit conversion configured from " + lineUom.getCode() + " to " + item.getBaseUom().getCode() + " for this item!"));

		return qty.multiply(conversion.getFactor());
	}

	private List<String> splitSerials(String serialNos) {
		List<String> result = new ArrayList<>();
		if (!CommonValidators.validString(serialNos)) {
			return result;
		}
		for (String s : serialNos.split(",")) {
			if (CommonValidators.validString(s.trim())) {
				result.add(s.trim());
			}
		}
		return result;
	}

	@Override
	public GoodsIssueDTO getIssueById(Long id) throws Exception {
		GoodsIssue issue = this.issueRepository.findById(id).orElseThrow(() -> new Exception("Goods issue not found!"));
		return new GoodsIssueDTO(issue);
	}

	@Override
	public SearchResultDTO<GoodsIssueDTO> searchIssues(GoodsIssueSearchDTO searchDTO, Pageable pageable) throws Exception {
		try {
			Specification<GoodsIssue> spec = GoodsIssueSpecification.withSearchCriteria(searchDTO);
			Page<GoodsIssue> page = this.issueRepository.findAll(spec, pageable);
			return convertToSearchResult(page);
		} catch (Exception e) {
			throw new Exception("Error searching goods issues: " + e.getMessage(), e);
		}
	}

	@Override
	@Transactional
	public GoodsIssueDTO submit(Long id) throws Exception {
		GoodsIssue issue = this.issueRepository.findById(id).orElseThrow(() -> new Exception("Goods issue not found!"));
		requireStatus(issue, InventoryDocStatus.DRAFT);
		issue.setStatus(InventoryDocStatus.SUBMITTED.getCode());
		GoodsIssue saved = this.issueRepository.save(issue);
		this.auditLogService.log(InventoryRefDocTypes.GOODS_ISSUE, saved.getId(), saved.getDocNo(), InventoryAuditAction.SUBMITTED.getCode(), null);
		return new GoodsIssueDTO(saved);
	}

	@Override
	@Transactional
	public GoodsIssueDTO approve(Long id) throws Exception {
		GoodsIssue issue = this.issueRepository.findById(id).orElseThrow(() -> new Exception("Goods issue not found!"));
		requireStatus(issue, InventoryDocStatus.SUBMITTED);

		User currentUser = this.authenticationService.getLoggedInUser();
		issue.setStatus(InventoryDocStatus.APPROVED.getCode());
		issue.setApprovedBy(currentUser);
		issue.setApprovedTime(new Date());

		GoodsIssue saved = this.issueRepository.save(issue);
		this.auditLogService.log(InventoryRefDocTypes.GOODS_ISSUE, saved.getId(), saved.getDocNo(), InventoryAuditAction.APPROVED.getCode(), null);
		return new GoodsIssueDTO(saved);
	}

	@Override
	@Transactional
	public GoodsIssueDTO post(Long id) throws Exception {
		GoodsIssue issue = this.issueRepository.findById(id).orElseThrow(() -> new Exception("Goods issue not found!"));
		requireStatus(issue, InventoryDocStatus.APPROVED);

		BigDecimal totalAmount = BigDecimal.ZERO;

		for (GoodsIssueLine line : issue.getLines()) {
			Item item = line.getItem();

			if (TrackingType.SERIAL.getCode().equals(item.getTrackingType())) {
				consumeSerialsForLine(item, line);
			}

			BigDecimal unitCost = this.stockMovementService.postIssue(item, line.getLocation(), line.getBatch(), line.getBaseQty(),
					InventoryRefDocTypes.GOODS_ISSUE, issue.getId(), line.getId());

			line.setUnitCost(unitCost);
			line.setLineTotal(unitCost.multiply(line.getBaseQty()));
			totalAmount = totalAmount.add(line.getLineTotal());
		}

		issue.setTotalAmount(totalAmount);

		User currentUser = this.authenticationService.getLoggedInUser();
		issue.setStatus(InventoryDocStatus.POSTED.getCode());
		issue.setPostedBy(currentUser);
		issue.setPostedTime(new Date());

		GoodsIssue saved = this.issueRepository.save(issue);
		this.auditLogService.log(InventoryRefDocTypes.GOODS_ISSUE, saved.getId(), saved.getDocNo(), InventoryAuditAction.POSTED.getCode(), null);
		return new GoodsIssueDTO(saved);
	}

	private void consumeSerialsForLine(Item item, GoodsIssueLine line) throws Exception {
		for (String serialNo : splitSerials(line.getSerialNos())) {
			StockSerial serial = this.stockSerialRepository.findByItemIdAndSerialNo(item.getId(), serialNo)
					.orElseThrow(() -> new Exception("Serial number " + serialNo + " not found for item " + item.getName() + "!"));

			if (!StockSerialStatus.IN_STOCK.getCode().equals(serial.getStatus())) {
				throw new Exception("Serial number " + serialNo + " is not currently in stock (status: "
						+ StockSerialStatus.getDescByCode(serial.getStatus()) + ")!");
			}
			if (!serial.getCurrentLocation().getId().equals(line.getLocation().getId())) {
				throw new Exception("Serial number " + serialNo + " is not at the selected location!");
			}

			serial.setStatus(StockSerialStatus.ISSUED.getCode());
			this.stockSerialRepository.save(serial);
		}
	}

	@Override
	@Transactional
	public GoodsIssueDTO cancel(Long id) throws Exception {
		GoodsIssue issue = this.issueRepository.findById(id).orElseThrow(() -> new Exception("Goods issue not found!"));

		if (InventoryDocStatus.POSTED.getCode().equals(issue.getStatus())) {
			throw new Exception("Posted goods issues cannot be cancelled! Use a stock adjustment to correct posted stock.");
		}
		if (InventoryDocStatus.CANCELLED.getCode().equals(issue.getStatus())) {
			throw new Exception("This goods issue is already cancelled!");
		}

		issue.setStatus(InventoryDocStatus.CANCELLED.getCode());
		GoodsIssue saved = this.issueRepository.save(issue);
		this.auditLogService.log(InventoryRefDocTypes.GOODS_ISSUE, saved.getId(), saved.getDocNo(), InventoryAuditAction.CANCELLED.getCode(), null);
		return new GoodsIssueDTO(saved);
	}

	@Override
	@Transactional
	public boolean deleteIssue(Long id) throws Exception {
		GoodsIssue issue = this.issueRepository.findById(id).orElseThrow(() -> new Exception("Goods issue not found!"));

		if (!InventoryDocStatus.DRAFT.getCode().equals(issue.getStatus())) {
			throw new Exception("Only draft goods issues can be deleted!");
		}

		this.auditLogService.log(InventoryRefDocTypes.GOODS_ISSUE, issue.getId(), issue.getDocNo(), InventoryAuditAction.DELETED.getCode(), null);
		this.issueRepository.delete(issue);
		return true;
	}

	private void requireStatus(GoodsIssue issue, InventoryDocStatus expected) throws Exception {
		if (!expected.getCode().equals(issue.getStatus())) {
			throw new Exception("Goods issue must be in " + expected.getDesc() + " status for this action! Current status: "
					+ InventoryDocStatus.getDescByCode(issue.getStatus()));
		}
	}

	private SearchResultDTO<GoodsIssueDTO> convertToSearchResult(Page<GoodsIssue> page) {
		SearchResultDTO<GoodsIssueDTO> result = new SearchResultDTO<>();
		result.setPageNo(page.getNumber());
		result.setLimit(page.getSize());
		result.setTotalPage(page.getTotalPages());
		result.setTotalRecords((int) page.getTotalElements());
		result.setPageCount(page.getNumberOfElements());
		result.setHasNextPage(page.hasNext());

		List<GoodsIssueDTO> dtos = page.getContent().stream().map(GoodsIssueDTO::new).collect(Collectors.toList());
		result.setResults(dtos);
		return result;
	}
}