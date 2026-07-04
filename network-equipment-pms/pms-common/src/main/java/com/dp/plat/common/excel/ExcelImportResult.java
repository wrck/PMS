package com.dp.plat.common.excel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Aggregated result of an Excel import operation: the rows that passed validation
 * plus the rows that were rejected with their corresponding errors.
 *
 * @param <T> the DTO type that maps the Excel rows
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExcelImportResult<T> {

    /** Rows that passed validation and should be persisted. */
    @Builder.Default
    private List<T> successList = new ArrayList<>();

    /** Validation errors keyed to their source row index. */
    @Builder.Default
    private List<ExcelImportError> errors = new ArrayList<>();

    /**
     * Convenience accessor for the success count.
     *
     * @return number of successfully validated rows
     */
    public int getSuccessCount() {
        return successList == null ? 0 : successList.size();
    }

    /**
     * Convenience accessor for the error count.
     *
     * @return number of rejected rows
     */
    public int getErrorCount() {
        return errors == null ? 0 : errors.size();
    }
}
