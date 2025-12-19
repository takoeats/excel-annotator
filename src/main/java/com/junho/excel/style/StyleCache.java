package com.junho.excel.style;

import com.junho.excel.exception.ErrorCode;
import com.junho.excel.exception.ExcelExporterException;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Workbook;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 스타일 캐싱 시스템 (SXSSF 호환)
 * <p>커스텀 스타일 객체만 캐싱, POI CellStyle은 매번 생성</p>
 */
@Slf4j
public final class StyleCache {

    private static final Map<Class<?>, CustomExcelCellStyle> STYLE_INSTANCES = new ConcurrentHashMap<>();

    private StyleCache() {
        throw new AssertionError("Utility class cannot be instantiated");
    }

    /**
     * 스타일 클래스 인스턴스 캐싱 반환
     * <p>사용자 커스텀 스타일을 포함하여 모든 CustomExcelCellStyle 구현체를 지원합니다.</p>
     * <p>단, public no-arg 생성자가 필요합니다.</p>
     */
    public static CustomExcelCellStyle getStyleInstance(Class<? extends CustomExcelCellStyle> styleClass) {
        return STYLE_INSTANCES.computeIfAbsent(styleClass, k -> {
            try {
                Constructor<? extends CustomExcelCellStyle> constructor = styleClass.getDeclaredConstructor();
                if (!Modifier.isPublic(constructor.getModifiers())) {
                    throw new ExcelExporterException(
                            ErrorCode.STYLE_INSTANTIATION_FAILED,
                            "스타일 클래스는 public no-arg 생성자가 필요합니다: " + styleClass.getName()
                    );
                }

                return constructor.newInstance();
            } catch (NoSuchMethodException e) {
                throw new ExcelExporterException(
                        ErrorCode.STYLE_INSTANTIATION_FAILED,
                        "public no-arg 생성자를 찾을 수 없습니다: " + styleClass.getName(),
                        e
                );
            } catch (ExcelExporterException e) {
                throw e;
            } catch (Exception e) {
                throw new ExcelExporterException(
                        ErrorCode.STYLE_INSTANTIATION_FAILED,
                        "스타일 인스턴스 생성 실패: " + styleClass.getName(),
                        e
                );
            }
        });
    }

    /**
     * POI CellStyle 생성 (SXSSF 호환, 캐싱 안 됨)
     */
    public static CellStyle createPOIStyle(Workbook workbook,
                                           Class<? extends CustomExcelCellStyle> styleClass) {
        CellStyle poiStyle = workbook.createCellStyle();
        CustomExcelCellStyle customStyle = getStyleInstance(styleClass);
        customStyle.apply(poiStyle, workbook);
        return poiStyle;
    }
}