package io.github.takoeats.excelannotator.style.internal.wrapper;

import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Workbook;

/**
 * Apache POI Workbook Wrapper
 * <p>Shaded JAR에서 POI 타입을 public API에 노출하지 않기 위한 Wrapper</p>
 * <p>내부 구현용이며, 사용자는 이 클래스를 직접 사용하지 않음</p>
 */
public final class WorkbookWrapper {

    private final Workbook poiWorkbook;

    private WorkbookWrapper(Workbook poiWorkbook) {
        this.poiWorkbook = poiWorkbook;
    }

    /**
     * POI Workbook을 Wrapper로 감싸기
     */
    public static WorkbookWrapper wrap(Workbook workbook) {
        return new WorkbookWrapper(workbook);
    }

    /**
     * 내부 POI Workbook 반환 (internal use only)
     */
    public Workbook toPoi() {
        return poiWorkbook;
    }

    public Font createFont() {
        return poiWorkbook.createFont();
    }

    public DataFormat createDataFormat() {
        return poiWorkbook.createDataFormat();
    }
}
