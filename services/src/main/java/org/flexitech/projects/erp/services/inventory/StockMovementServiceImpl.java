package org.flexitech.projects.erp.services.inventory;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Optional;

import org.flexitech.projects.erp.commons.CommonValidators;
import org.flexitech.projects.erp.commons.enums.StockMovementType;
import org.flexitech.projects.erp.persistence.entities.inventory.Item;
import org.flexitech.projects.erp.persistence.entities.inventory.StockBalance;
import org.flexitech.projects.erp.persistence.entities.inventory.StockBatch;
import org.flexitech.projects.erp.persistence.entities.inventory.StockLedger;
import org.flexitech.projects.erp.persistence.entities.inventory.WarehouseLocation;
import org.flexitech.projects.erp.persistence.entities.user.User;
import org.flexitech.projects.erp.persistence.repositories.inventory.StockBalanceRepository;
import org.flexitech.projects.erp.persistence.repositories.inventory.StockLedgerRepository;
import org.flexitech.projects.erp.services.auth.AuthenticationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StockMovementServiceImpl implements StockMovementService {

	private final StockBalanceRepository stockBalanceRepository;
	private final StockLedgerRepository stockLedgerRepository;
	private final AuthenticationService authenticationService;

	public StockMovementServiceImpl(StockBalanceRepository stockBalanceRepository, StockLedgerRepository stockLedgerRepository,
			AuthenticationService authenticationService) {
		this.stockBalanceRepository = stockBalanceRepository;
		this.stockLedgerRepository = stockLedgerRepository;
		this.authenticationService = authenticationService;
	}

	@Override
	@Transactional(propagation = Propagation.MANDATORY)
	public void postReceipt(Item item, WarehouseLocation location, StockBatch batch, BigDecimal qty, BigDecimal unitCost,
			String refDocType, Long refDocId, Long refLineId) throws Exception {

		if (qty.compareTo(BigDecimal.ZERO) <= 0) {
			throw new Exception("Receipt quantity must be greater than zero!");
		}

		StockBalance balance = lockOrCreateBalance(item, location, batch);

		BigDecimal existingQty = balance.getQtyOnHand();
		BigDecimal existingCost = balance.getAvgCost() == null ? BigDecimal.ZERO : balance.getAvgCost();
		BigDecimal existingValue = existingQty.multiply(existingCost);
		BigDecimal incomingValue = qty.multiply(unitCost);
		BigDecimal newQty = existingQty.add(qty);

		BigDecimal newAvgCost = newQty.compareTo(BigDecimal.ZERO) == 0
				? BigDecimal.ZERO
				: existingValue.add(incomingValue).divide(newQty, 4, java.math.RoundingMode.HALF_UP);

		balance.setQtyOnHand(newQty);
		balance.setAvgCost(newAvgCost);
		balance.setLastMovementAt(new Date());
		this.stockBalanceRepository.save(balance);

		writeLedger(item, location, batch, null, StockMovementType.RECEIPT.getCode(), qty, BigDecimal.ZERO,
				newQty, unitCost, incomingValue, refDocType, refDocId, refLineId);
	}

	@Override
	@Transactional(propagation = Propagation.MANDATORY)
	public BigDecimal postIssue(Item item, WarehouseLocation location, StockBatch batch, BigDecimal qty,
			String refDocType, Long refDocId, Long refLineId) throws Exception {

		if (qty.compareTo(BigDecimal.ZERO) <= 0) {
			throw new Exception("Issue quantity must be greater than zero!");
		}

		StockBalance balance = lockOrCreateBalance(item, location, batch);

		BigDecimal available = balance.getQtyOnHand().subtract(balance.getQtyReserved());
		boolean allowNegative = Boolean.TRUE.equals(location.getWarehouse().getAllowNegativeStock());

		if (!allowNegative && qty.compareTo(available) > 0) {
			throw new Exception("Insufficient stock for " + item.getName() + " at " + location.getName()
					+ " (available: " + available + ", requested: " + qty + ")");
		}

		BigDecimal unitCost = balance.getAvgCost() == null ? BigDecimal.ZERO : balance.getAvgCost();
		BigDecimal newQty = balance.getQtyOnHand().subtract(qty);

		balance.setQtyOnHand(newQty);
		balance.setLastMovementAt(new Date());
		this.stockBalanceRepository.save(balance);

		writeLedger(item, location, batch, null, StockMovementType.ISSUE.getCode(), BigDecimal.ZERO, qty,
				newQty, unitCost, qty.multiply(unitCost), refDocType, refDocId, refLineId);

		return unitCost;
	}

	@Override
	@Transactional(propagation = Propagation.MANDATORY)
	public void postAdjustment(Item item, WarehouseLocation location, StockBatch batch, BigDecimal differenceQty, BigDecimal unitCost,
			String refDocType, Long refDocId, Long refLineId) throws Exception {

		if (differenceQty.compareTo(BigDecimal.ZERO) == 0) {
			return;
		}

		StockBalance balance = lockOrCreateBalance(item, location, batch);
		boolean isIncrease = differenceQty.compareTo(BigDecimal.ZERO) > 0;
		boolean allowNegative = Boolean.TRUE.equals(location.getWarehouse().getAllowNegativeStock());

		BigDecimal newQty = balance.getQtyOnHand().add(differenceQty);
		if (!allowNegative && newQty.compareTo(BigDecimal.ZERO) < 0) {
			throw new Exception("Adjustment would result in negative stock for " + item.getName() + " at " + location.getName());
		}

		BigDecimal appliedCost = unitCost != null ? unitCost : (balance.getAvgCost() == null ? BigDecimal.ZERO : balance.getAvgCost());

		if (isIncrease) {
			BigDecimal existingValue = balance.getQtyOnHand().multiply(balance.getAvgCost() == null ? BigDecimal.ZERO : balance.getAvgCost());
			BigDecimal incomingValue = differenceQty.multiply(appliedCost);
			balance.setAvgCost(newQty.compareTo(BigDecimal.ZERO) == 0
					? BigDecimal.ZERO
					: existingValue.add(incomingValue).divide(newQty, 4, java.math.RoundingMode.HALF_UP));
		}

		balance.setQtyOnHand(newQty);
		balance.setLastMovementAt(new Date());
		this.stockBalanceRepository.save(balance);

		int movementType = isIncrease ? StockMovementType.ADJUST_IN.getCode() : StockMovementType.ADJUST_OUT.getCode();
		BigDecimal absQty = differenceQty.abs();

		writeLedger(item, location, batch,
				null,
				movementType,
				isIncrease ? absQty : BigDecimal.ZERO,
				isIncrease ? BigDecimal.ZERO : absQty,
				newQty, appliedCost, absQty.multiply(appliedCost), refDocType, refDocId, refLineId);
	}

	private StockBalance lockOrCreateBalance(Item item, WarehouseLocation location, StockBatch batch) {
		Optional<StockBalance> existing = CommonValidators.isValidObject(batch)
				? this.stockBalanceRepository.findForUpdateWithBatch(item.getId(), location.getId(), batch.getId())
				: this.stockBalanceRepository.findForUpdateWithoutBatch(item.getId(), location.getId());

		if (existing.isPresent()) {
			return existing.get();
		}

		StockBalance balance = new StockBalance();
		balance.setItem(item);
		balance.setLocation(location);
		balance.setBatch(batch);
		balance.setQtyOnHand(BigDecimal.ZERO);
		balance.setQtyReserved(BigDecimal.ZERO);
		balance.setAvgCost(BigDecimal.ZERO);
		return this.stockBalanceRepository.save(balance);
	}

	private void writeLedger(Item item, WarehouseLocation location, StockBatch batch, org.flexitech.projects.erp.persistence.entities.inventory.StockSerial serial,
			Integer movementType, BigDecimal qtyIn, BigDecimal qtyOut, BigDecimal balanceAfter, BigDecimal unitCost, BigDecimal totalCost,
			String refDocType, Long refDocId, Long refLineId) {

		StockLedger ledger = new StockLedger();
		ledger.setItem(item);
		ledger.setLocation(location);
		ledger.setBatch(batch);
		ledger.setSerial(serial);
		ledger.setMovementType(movementType);
		ledger.setQtyIn(qtyIn);
		ledger.setQtyOut(qtyOut);
		ledger.setBalanceAfter(balanceAfter);
		ledger.setUnitCost(unitCost);
		ledger.setTotalCost(totalCost);
		ledger.setRefDocType(refDocType);
		ledger.setRefDocId(refDocId);
		ledger.setRefLineId(refLineId);
		ledger.setPostedAt(new Date());

		User currentUser = this.authenticationService.getLoggedInUser();
		ledger.setPostedBy(currentUser);

		this.stockLedgerRepository.save(ledger);
	}
}