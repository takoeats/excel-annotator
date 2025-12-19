package com.junho.excel.internal.writer.workbook;

import com.junho.excel.exception.ErrorCode;
import com.junho.excel.exception.ExcelExporterException;
import com.junho.excel.internal.writer.SheetWriteContext;
import com.junho.excel.internal.writer.SheetWriter;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

public final class SXSSFWorkbookBuilder {

    private static final int SHEET_BUFFER_LIMIT = 500;

    private final SheetWriter sheetWriter;

    public SXSSFWorkbookBuilder(SheetWriter sheetWriter) {
        this.sheetWriter = sheetWriter;
    }

    public <T> SXSSFWorkbook createWorkbookAndWrite(SheetWriteContext<T> context) {
        int bufferSize = context.isColumnBasedSplit()
                ? Math.max(20, SHEET_BUFFER_LIMIT / context.getSheetCount())
                : SHEET_BUFFER_LIMIT;

        SXSSFWorkbook wb = new SXSSFWorkbook(bufferSize);
        try {
            sheetWriter.write(wb, context);
            return wb;
        } catch (ExcelExporterException e) {
            closeQuietly(wb);
            throw e;
        } catch (Exception e) {
            closeQuietly(wb);
            throw new ExcelExporterException(ErrorCode.WORKBOOK_CREATION_FAILED, e);
        }
    }

    private void closeQuietly(SXSSFWorkbook wb) {
        if (wb != null) {
            try {
                wb.close();
            } catch (java.io.IOException ignored) {
                //this method for close workbook quietly
                //when using this method should be throw exception other line
            }
        }
    }
}
