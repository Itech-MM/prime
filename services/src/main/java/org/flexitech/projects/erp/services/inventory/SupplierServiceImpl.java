package org.flexitech.projects.erp.services.inventory;

import java.util.List;
import java.util.stream.Collectors;

import org.flexitech.projects.erp.commons.CommonValidators;
import org.flexitech.projects.erp.dto.SearchResultDTO;
import org.flexitech.projects.erp.dto.inventory.SupplierDTO;
import org.flexitech.projects.erp.dto.inventory.search.SupplierSearchDTO;
import org.flexitech.projects.erp.persistence.entities.inventory.Supplier;
import org.flexitech.projects.erp.persistence.repositories.inventory.GoodsReceiptRepository;
import org.flexitech.projects.erp.persistence.repositories.inventory.StockBatchRepository;
import org.flexitech.projects.erp.persistence.repositories.inventory.SupplierRepository;
import org.flexitech.projects.erp.services.specifications.inventory.SupplierSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class SupplierServiceImpl implements SupplierService {

    private final SupplierRepository supplierRepository;
    private final GoodsReceiptRepository goodsReceiptRepository;
    private final StockBatchRepository stockBatchRepository;

    public SupplierServiceImpl(SupplierRepository supplierRepository, GoodsReceiptRepository goodsReceiptRepository,
            StockBatchRepository stockBatchRepository) {
        this.supplierRepository = supplierRepository;
        this.goodsReceiptRepository = goodsReceiptRepository;
        this.stockBatchRepository = stockBatchRepository;
    }

    @Override
    public SupplierDTO manageSupplier(SupplierDTO supplierDTO) throws Exception {
        Supplier supplier;
        boolean isUpdate = CommonValidators.validLong(supplierDTO.getId());

        if (isUpdate) {
            supplier = this.supplierRepository.findById(supplierDTO.getId()).orElseThrow(() -> new Exception("Supplier not found!"));
            if (this.supplierRepository.existsByCodeAndIdNot(supplierDTO.getCode(), supplierDTO.getId())) {
                throw new Exception("Supplier code already exists!");
            }
        } else {
            supplier = new Supplier();
            if (this.supplierRepository.existsByCode(supplierDTO.getCode())) {
                throw new Exception("Supplier code already exists!");
            }
        }

        supplier.setCode(supplierDTO.getCode());
        supplier.setName(supplierDTO.getName());
        supplier.setContactPerson(supplierDTO.getContactPerson());
        supplier.setPhone(supplierDTO.getPhone());
        supplier.setEmail(supplierDTO.getEmail());
        supplier.setAddress(supplierDTO.getAddress());
        supplier.setPaymentTerms(supplierDTO.getPaymentTerms());
        supplier.setStatus(supplierDTO.getStatus());

        Supplier saved = this.supplierRepository.save(supplier);
        return new SupplierDTO(saved);
    }

    @Override
    public SupplierDTO getSupplierById(Long id) throws Exception {
        Supplier supplier = this.supplierRepository.findById(id).orElseThrow(() -> new Exception("Supplier not found!"));
        return new SupplierDTO(supplier);
    }

    @Override
    public SearchResultDTO<SupplierDTO> searchSuppliers(SupplierSearchDTO searchDTO, Pageable pageable) throws Exception {
        try {
            Specification<Supplier> spec = SupplierSpecification.withSearchCriteria(searchDTO);
            Page<Supplier> page = this.supplierRepository.findAll(spec, pageable);
            return convertToSearchResult(page);
        } catch (Exception e) {
            throw new Exception("Error searching suppliers: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean deleteSupplier(Long id) throws Exception {
        Supplier supplier = this.supplierRepository.findById(id).orElseThrow(() -> new Exception("Supplier not found!"));

        if (this.goodsReceiptRepository.existsBySupplierId(id)) {
            throw new Exception("Cannot delete: supplier has goods receipt history!");
        }
        if (this.stockBatchRepository.existsBySupplierId(id)) {
            throw new Exception("Cannot delete: supplier is referenced by one or more stock batches!");
        }

        this.supplierRepository.delete(supplier);
        return true;
    }

    private SearchResultDTO<SupplierDTO> convertToSearchResult(Page<Supplier> page) {
        SearchResultDTO<SupplierDTO> result = new SearchResultDTO<>();
        result.setPageNo(page.getNumber());
        result.setLimit(page.getSize());
        result.setTotalPage(page.getTotalPages());
        result.setTotalRecords((int) page.getTotalElements());
        result.setPageCount(page.getNumberOfElements());
        result.setHasNextPage(page.hasNext());

        List<SupplierDTO> dtos = page.getContent().stream().map(SupplierDTO::new).collect(Collectors.toList());
        result.setResults(dtos);
        return result;
    }
}