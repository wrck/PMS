package com.dp.plat.common.excel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Describes a single row-level validation error produced during Excel import.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExcelImportError {

    /** 1-based row index in the source Excel sheet (header row excluded). */
    private int rowIndex;

    /** Raw row content represented as a comma-separated string for troubleshooting. */
    private String rowData;

    /** Human-readable error message explaining why the row was rejected. */
    private String errorMessage;
}
