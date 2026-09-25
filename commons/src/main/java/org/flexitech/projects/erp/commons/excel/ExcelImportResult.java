package org.flexitech.projects.erp.commons.excel;

import java.util.ArrayList;
import java.util.List;

import org.flexitech.projects.erp.commons.enums.ImportStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExcelImportResult<T> {
    private List<T> successList = new ArrayList<>();
    private List<ImportError> errorList = new ArrayList<>();
    private int totalCount;
    private int successCount;
    private int errorCount;
    private ImportStatus status;
    private Integer statusCode;
    private String message;
}