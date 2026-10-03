package org.flexitech.projects.erp.services.inventory;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.flexitech.projects.erp.commons.CommonConstants;
import org.flexitech.projects.erp.commons.CommonValidators;
import org.flexitech.projects.erp.commons.InventoryRefDocTypes;
import org.flexitech.projects.erp.commons.enums.InventoryDocStatus;
import org.flexitech.projects.erp.commons.enums.TrackingType;
import org.flexitech.projects.erp.commons.utils.DateUtils;
import org.flexitech.projects.erp.dto.SearchResultDTO;
import org.flexitech.projects.erp.dto.inventory.GoodsReceiptDTO;
import org.flexitech.projects.erp.dto.inventory.GoodsReceiptLineDTO;
import org.flexitech.projects.erp.dto.inventory.search.GoodsReceiptSearchDTO;
import org.flexitech.projects.erp.persistence.entities.inventory.GoodsReceipt;
import org.flexitech.projects.erp.persistence.entities.inventory.GoodsReceiptLine;
import org.flexitech.projects.erp.persistence.entities.inventory.Item;
import org.flexitech.projects.erp.persistence.entities.inventory.StockBatch;
import org.flexitech.projects.erp.persistence.entities.inventory.StockSerial;
import org.flexitech.projects.erp.persistence.entities.inventory.Supplier;
import org.flexitech.projects.erp.persistence.entities.inventory.UnitOfMeasure;
import org.flexitech.projects.erp.persistence.entities.inventory.UomConversion;
import org.flexitech.projects.erp.persistence.entities.inventory.Warehouse;
import org.flexitech.projects.erp.persistence.entities.inventory.WarehouseLocation;
import org.flexitech.projects.erp.persistence.entities.user.User;
import org.flexitech.projects.erp.persistence.repositories.inventory.GoodsReceiptRepository;
import org.flexitech.projects.erp.persistence.repositories.inventory.ItemRepository;
import org.flexitech.projects.erp.persistence.repositories.inventory.StockBatchRepository;
import org.flexitech.projects.erp.persistence.repositories.inventory.StockSerialRepository;
import org.flexitech.projects.erp.persistence.repositories.inventory.SupplierRepository;
import org.flexitech.projects.erp.persistence.repositories.inventory.UnitOfMeasureRepository;
import org.flexitech.projects.erp.persistence.repositories.inventory.UomConversionRepository;
import org.flexitech.projects.erp.persistence.repositories.inventory.WarehouseLocationRepository;
import org.flexitech.projects.erp.persistence.repositories.inventory.WarehouseRepository;
import org.flexitech.projects.erp.services.auth.AuthenticationService;
import org.flexitech.projects.erp.services.specifications.inventory.GoodsReceiptSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class GoodsReceiptServiceImpl implements GoodsReceiptService {

	private static final String DOC_TYPE = "GRN";
	private static final String DOC_PREFIX = "GRN";

	private final GoodsReceiptRepository receiptRepository;
	private final ItemRepository itemRepository;
	private final SupplierRepository supplierRepository;
	private final WarehouseRepository warehouseRepository;
	private final WarehouseLocationRepository locationRepository;
	private final StockBatchRepository stockBatchRepository;
	private final StockSerialRepository stockSerialRepository;
	private final UomConversionRepository uomConversionRepository;
	private final DocumentSequenceService documentSequenceService;
	private final StockMovementService stockMovementService;
	private final AuthenticationService authenticationService;
	private final UnitOfMeasureRepository uomRepository;


	public GoodsReceiptServiceImpl(GoodsReceiptRepository receiptRepository, ItemRepository itemRepository,
			SupplierRepository supplierRepository, WarehouseRepository warehouseRepository,
			WarehouseLocationRepository locationRepository, StockBatchRepository stockBatchRepository,
			StockSerialRepository stockSerialRepository, UomConversionRepository uomConversionRepository,
			DocumentSequenceService documentSequenceService, StockMovementService stockMovementService,
			AuthenticationService authenticationService, UnitOfMeasureRepository uomRepository) {
		this.receiptRepository = receiptRepository;
		this.itemRepository = itemRepository;
		this.supplierRepository = supplierRepository;
		this.warehouseRepository = warehouseRepository;
		this.locationRepository = locationRepository;
		this.stockBatchRepository = stockBatchRepository;
		this.stockSerialRepository = stockSerialRepository;
		this.uomConversionRepository = uomConversionRepository;
		this.documentSequenceService = documentSequenceService;
		this.stockMovementService = stockMovementService;
		this.authenticationService = authenticationService;
		this.uomRepository = uomRepository;
	}

	@Override
	@Transactional
	public GoodsReceiptDTO saveDraft(GoodsReceiptDTO receiptDTO) throws Exception {

		GoodsReceipt receipt;
		boolean isUpdate = CommonValidators.validLong(receiptDTO.getId());

		if (isUpdate) {
			receipt = this.receiptRepository.findById(receiptDTO.getId()).orElseThrow(() -> new Exception("Goods receipt not found!"));
			if (!InventoryDocStatus.DRAFT.getCode().equals(receipt.getStatus())) {
				throw new Exception("Only draft goods receipts can be edited!");
			}
		} else {
			receipt = new GoodsReceipt();
			receipt.setDocNo(this.documentSequenceService.nextDocNo(DOC_TYPE, DOC_PREFIX));
			receipt.setStatus(InventoryDocStatus.DRAFT.getCode());
		}

		Supplier supplier = this.supplierRepository.findById(receiptDTO.getSupplierId())
				.orElseThrow(() -> new Exception("Supplier not found!"));
		Warehouse warehouse = this.warehouseRepository.findById(receiptDTO.getWarehouseId())
				.orElseThrow(() -> new Exception("Warehouse not found!"));

		receipt.setSupplier(supplier);
		receipt.setWarehouse(warehouse);
		receipt.setDeliveryNoteNo(receiptDTO.getDeliveryNoteNo());
		receipt.setRemarks(receiptDTO.getRemarks());

		if (CommonValidators.validString(receiptDTO.getReceivedDate())) {
			receipt.setReceivedDate(DateUtils.stringToDate(receiptDTO.getReceivedDate(), CommonConstants.STANDARD_12_HOUR_DATE_MINUTE_FORMAT));
		}

		if (receiptDTO.getLines() == null || receiptDTO.getLines().isEmpty()) {
			throw new Exception("At least one line item is required!");
		}

		receipt.getLines().clear();
		BigDecimal totalAmount = BigDecimal.ZERO;

		for (GoodsReceiptLineDTO lineDTO : receiptDTO.getLines()) {
			GoodsReceiptLine line = buildLine(receipt, lineDTO);
			receipt.getLines().add(line);
			totalAmount = totalAmount.add(line.getLineTotal());
		}

		receipt.setTotalAmount(totalAmount);

		GoodsReceipt saved = this.receiptRepository.save(receipt);
		return new GoodsReceiptDTO(saved);
	}

	private GoodsReceiptLine buildLine(GoodsReceipt receipt, GoodsReceiptLineDTO lineDTO) throws Exception {

		Item item = this.itemRepository.findById(lineDTO.getItemId()).orElseThrow(() -> new Exception("Item not found!"));

		UnitOfMeasure uom = CommonValidators.validLong(lineDTO.getUomId())
				? this.uomRepository.findById(lineDTO.getUomId()).orElseThrow(() -> new Exception("Unit of measure not found!"))
				: item.getBaseUom();

		WarehouseLocation location = this.locationRepository.findById(lineDTO.getLocationId())
				.orElseThrow(() -> new Exception("Location not found!"));

		if (!location.getWarehouse().getId().equals(receipt.getWarehouse().getId())) {
			throw new Exception("Location " + location.getName() + " does not belong to the selected warehouse!");
		}

		if (TrackingType.BATCH.getCode().equals(item.getTrackingType()) && !CommonValidators.validString(lineDTO.getBatchNo())) {
			throw new Exception("Batch number is required for item " + item.getName() + "!");
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

		GoodsReceiptLine line = new GoodsReceiptLine();
		line.setGoodsReceipt(receipt);
		line.setItem(item);
		line.setUom(uom);
		line.setLocation(location);
		line.setQty(lineDTO.getQty());
		line.setBaseQty(baseQty);
		line.setUnitCost(lineDTO.getUnitCost());
		line.setLineTotal(lineDTO.getUnitCost().multiply(baseQty));
		line.setBatchNo(lineDTO.getBatchNo());
		line.setSerialNos(lineDTO.getSerialNos());

		if (CommonValidators.validString(lineDTO.getMfgDate())) {
			line.setMfgDate(DateUtils.stringToDate(lineDTO.getMfgDate(), CommonConstants.STANDARD_12_HOUR_DATE_MINUTE_FORMAT));
		}
		if (CommonValidators.validString(lineDTO.getExpiryDate())) {
			line.setExpiryDate(DateUtils.stringToDate(lineDTO.getExpiryDate(), CommonConstants.STANDARD_12_HOUR_DATE_MINUTE_FORMAT));
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
	public GoodsReceiptDTO getReceiptById(Long id) throws Exception {
		GoodsReceipt receipt = this.receiptRepository.findById(id).orElseThrow(() -> new Exception("Goods receipt not found!"));
		return new GoodsReceiptDTO(receipt);
	}

	@Override
	public SearchResultDTO<GoodsReceiptDTO> searchReceipts(GoodsReceiptSearchDTO searchDTO, Pageable pageable) throws Exception {
		try {
			Specification<GoodsReceipt> spec = GoodsReceiptSpecification.withSearchCriteria(searchDTO);
			Page<GoodsReceipt> page = this.receiptRepository.findAll(spec, pageable);
			return convertToSearchResult(page);
		} catch (Exception e) {
			throw new Exception("Error searching goods receipts: " + e.getMessage(), e);
		}
	}

	@Override
	@Transactional
	public GoodsReceiptDTO submit(Long id) throws Exception {
		GoodsReceipt receipt = this.receiptRepository.findById(id).orElseThrow(() -> new Exception("Goods receipt not found!"));
		requireStatus(receipt, InventoryDocStatus.DRAFT);
		receipt.setStatus(InventoryDocStatus.SUBMITTED.getCode());
		return new GoodsReceiptDTO(this.receiptRepository.save(receipt));
	}

	@Override
	@Transactional
	public GoodsReceiptDTO approve(Long id) throws Exception {
		GoodsReceipt receipt = this.receiptRepository.findById(id).orElseThrow(() -> new Exception("Goods receipt not found!"));
		requireStatus(receipt, InventoryDocStatus.SUBMITTED);

		User currentUser = this.authenticationService.getLoggedInUser();
		receipt.setStatus(InventoryDocStatus.APPROVED.getCode());
		receipt.setApprovedBy(currentUser);
		receipt.setApprovedTime(new Date());

		return new GoodsReceiptDTO(this.receiptRepository.save(receipt));
	}

	@Override
	@Transactional
	public GoodsReceiptDTO post(Long id) throws Exception {
		GoodsReceipt receipt = this.receiptRepository.findById(id).orElseThrow(() -> new Exception("Goods receipt not found!"));
		requireStatus(receipt, InventoryDocStatus.APPROVED);

		for (GoodsReceiptLine line : receipt.getLines()) {
			Item item = line.getItem();
			StockBatch batch = resolveBatchForPosting(item, line);

			this.stockMovementService.postReceipt(item, line.getLocation(), batch, line.getBaseQty(), line.getUnitCost(),
					InventoryRefDocTypes.GOODS_RECEIPT, receipt.getId(), line.getId());

			if (TrackingType.SERIAL.getCode().equals(item.getTrackingType())) {
				createSerialsForLine(item, batch, line);
			}
		}

		User currentUser = this.authenticationService.getLoggedInUser();
		receipt.setStatus(InventoryDocStatus.POSTED.getCode());
		receipt.setPostedBy(currentUser);
		receipt.setPostedTime(new Date());

		return new GoodsReceiptDTO(this.receiptRepository.save(receipt));
	}

	private StockBatch resolveBatchForPosting(Item item, GoodsReceiptLine line) {
		if (!CommonValidators.validString(line.getBatchNo())) {
			return null;
		}

		return this.stockBatchRepository.findByItemIdAndBatchNo(item.getId(), line.getBatchNo())
				.map(existing -> {
					if (line.getExpiryDate() != null) {
						existing.setExpiryDate(line.getExpiryDate());
					}
					return this.stockBatchRepository.save(existing);
				})
				.orElseGet(() -> {
					StockBatch batch = new StockBatch();
					batch.setItem(item);
					batch.setBatchNo(line.getBatchNo());
					batch.setMfgDate(line.getMfgDate());
					batch.setExpiryDate(line.getExpiryDate());
					batch.setSupplier(line.getGoodsReceipt().getSupplier());
					batch.setReceivedAt(new Date());
					return this.stockBatchRepository.save(batch);
				});
	}

	private void createSerialsForLine(Item item, StockBatch batch, GoodsReceiptLine line) throws Exception {
		for (String serialNo : splitSerials(line.getSerialNos())) {
			if (this.stockSerialRepository.existsByItemIdAndSerialNo(item.getId(), serialNo)) {
				throw new Exception("Serial number " + serialNo + " already exists for item " + item.getName() + "!");
			}

			StockSerial serial = new StockSerial();
			serial.setItem(item);
			serial.setSerialNo(serialNo);
			serial.setBatch(batch);
			serial.setCurrentLocation(line.getLocation());
			serial.setStatus(org.flexitech.projects.erp.commons.enums.StockSerialStatus.IN_STOCK.getCode());

			this.stockSerialRepository.save(serial);
		}
	}

	@Override
	@Transactional
	public GoodsReceiptDTO cancel(Long id) throws Exception {
		GoodsReceipt receipt = this.receiptRepository.findById(id).orElseThrow(() -> new Exception("Goods receipt not found!"));

		if (InventoryDocStatus.POSTED.getCode().equals(receipt.getStatus())) {
			throw new Exception("Posted goods receipts cannot be cancelled! Use a stock adjustment to correct posted stock.");
		}
		if (InventoryDocStatus.CANCELLED.getCode().equals(receipt.getStatus())) {
			throw new Exception("This goods receipt is already cancelled!");
		}

		receipt.setStatus(InventoryDocStatus.CANCELLED.getCode());
		return new GoodsReceiptDTO(this.receiptRepository.save(receipt));
	}

	@Override
	@Transactional
	public boolean deleteReceipt(Long id) throws Exception {
		GoodsReceipt receipt = this.receiptRepository.findById(id).orElseThrow(() -> new Exception("Goods receipt not found!"));

		if (!InventoryDocStatus.DRAFT.getCode().equals(receipt.getStatus())) {
			throw new Exception("Only draft goods receipts can be deleted!");
		}

		this.receiptRepository.delete(receipt);
		return true;
	}

	private void requireStatus(GoodsReceipt receipt, InventoryDocStatus expected) throws Exception {
		if (!expected.getCode().equals(receipt.getStatus())) {
			throw new Exception("Goods receipt must be in " + expected.getDesc() + " status for this action! Current status: "
					+ InventoryDocStatus.getDescByCode(receipt.getStatus()));
		}
	}

	private SearchResultDTO<GoodsReceiptDTO> convertToSearchResult(Page<GoodsReceipt> page) {
		SearchResultDTO<GoodsReceiptDTO> result = new SearchResultDTO<>();
		result.setPageNo(page.getNumber());
		result.setLimit(page.getSize());
		result.setTotalPage(page.getTotalPages());
		result.setTotalRecords((int) page.getTotalElements());
		result.setPageCount(page.getNumberOfElements());
		result.setHasNextPage(page.hasNext());

		List<GoodsReceiptDTO> dtos = page.getContent().stream().map(GoodsReceiptDTO::new).collect(Collectors.toList());
		result.setResults(dtos);
		return result;
	}
}