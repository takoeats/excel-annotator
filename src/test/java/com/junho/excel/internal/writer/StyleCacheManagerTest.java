package com.junho.excel.internal.writer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;

import com.junho.excel.style.CustomExcelCellStyle;
import com.junho.excel.style.ExcelCellStyleConfigurer;
import com.junho.excel.style.ExcelColors;
import com.junho.excel.style.FontStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StyleCacheManagerTest {

  private SXSSFWorkbook workbook;
  private StyleCacheManager manager;

  @BeforeEach
  void setUp() {
    workbook = new SXSSFWorkbook();
    manager = new StyleCacheManager(workbook);
  }

  @AfterEach
  void tearDown() throws Exception {
    if (workbook != null) {
      workbook.close();
    }
  }

  @Test
  void getOrCreateStyle_withStyleClassOnly_createsCachedStyle() {
    CellStyle style1 = manager.getOrCreateStyle(TestStyle.class, null);
    CellStyle style2 = manager.getOrCreateStyle(TestStyle.class, null);

    assertNotNull(style1);
    assertSame(style1, style2);
  }

  @Test
  void getOrCreateStyle_withFormatOnly_createsCachedStyle() {
    CellStyle style1 = manager.getOrCreateStyle(null, "#,##0.00");
    CellStyle style2 = manager.getOrCreateStyle(null, "#,##0.00");

    assertNotNull(style1);
    assertSame(style1, style2);
  }

  @Test
  void getOrCreateStyle_withStyleClassAndFormat_createsCachedStyle() {
    CellStyle style1 = manager.getOrCreateStyle(TestStyle.class, "#,##0.00");
    CellStyle style2 = manager.getOrCreateStyle(TestStyle.class, "#,##0.00");

    assertNotNull(style1);
    assertSame(style1, style2);
  }

  @Test
  void getOrCreateStyle_withBothNull_createsDefaultStyle() {
    CellStyle style = manager.getOrCreateStyle(null, null);

    assertNotNull(style);
  }

  @Test
  void getOrCreateStyle_differentFormats_createsDifferentStyles() {
    CellStyle style1 = manager.getOrCreateStyle(TestStyle.class, "#,##0.00");
    CellStyle style2 = manager.getOrCreateStyle(TestStyle.class, "0.00%");

    assertNotNull(style1);
    assertNotNull(style2);
    assertNotSame(style1, style2);
  }

  @Test
  void getOrCreateStyle_differentStyleClasses_createsDifferentStyles() {
    CellStyle style1 = manager.getOrCreateStyle(TestStyle.class, null);
    CellStyle style2 = manager.getOrCreateStyle(AnotherTestStyle.class, null);

    assertNotNull(style1);
    assertNotNull(style2);
    assertNotSame(style1, style2);
  }

  @Test
  void getOrCreateStyle_sameStyleClassDifferentFormats_usesSameBaseStyle() {
    CellStyle style1 = manager.getOrCreateStyle(TestStyle.class, null);
    CellStyle style2 = manager.getOrCreateStyle(TestStyle.class, "#,##0.00");

    assertNotNull(style1);
    assertNotNull(style2);
    assertNotSame(style1, style2);
  }

  @Test
  void getOrCreateStyle_formatWithoutStyleClass_createsStyledCell() {
    CellStyle style = manager.getOrCreateStyle(null, "#,##0.00");

    assertNotNull(style);
    assertEquals(workbook.createDataFormat().getFormat("#,##0.00"), style.getDataFormat());
  }

  @Test
  void getOrCreateStyle_calledMultipleTimesWithSameParams_returnsSameInstance() {
    CellStyle style1 = manager.getOrCreateStyle(TestStyle.class, "#,##0.00");
    CellStyle style2 = manager.getOrCreateStyle(TestStyle.class, "#,##0.00");
    CellStyle style3 = manager.getOrCreateStyle(TestStyle.class, "#,##0.00");

    assertSame(style1, style2);
    assertSame(style2, style3);
  }

  @Test
  void getOrCreateStyle_withComplexFormat_appliesCorrectly() {
    String complexFormat = "[Red][<0]#,##0.00;[Blue][>=0]#,##0.00";
    CellStyle style = manager.getOrCreateStyle(TestStyle.class, complexFormat);

    assertNotNull(style);
    assertEquals(workbook.createDataFormat().getFormat(complexFormat), style.getDataFormat());
  }

  @Test
  void getOrCreateStyle_nullStyleClassWithFormat_cachesCorrectly() {
    CellStyle style1 = manager.getOrCreateStyle(null, "#,##0");
    CellStyle style2 = manager.getOrCreateStyle(null, "#,##0");

    assertSame(style1, style2);
  }

  @Test
  void getOrCreateStyle_multipleDefaultStyles_returnsDifferentInstances() {
    CellStyle style1 = manager.getOrCreateStyle(null, null);
    CellStyle style2 = manager.getOrCreateStyle(null, null);

    assertNotNull(style1);
    assertNotNull(style2);
    assertNotSame(style1, style2);
  }

  @Test
  void getOrCreateStyle_cacheIsolationBetweenFormatAndBase() {
    CellStyle baseStyle = manager.getOrCreateStyle(TestStyle.class, null);
    CellStyle formatStyle = manager.getOrCreateStyle(TestStyle.class, "#,##0.00");

    assertNotSame(baseStyle, formatStyle);

    CellStyle baseStyleAgain = manager.getOrCreateStyle(TestStyle.class, null);
    CellStyle formatStyleAgain = manager.getOrCreateStyle(TestStyle.class, "#,##0.00");

    assertSame(baseStyle, baseStyleAgain);
    assertSame(formatStyle, formatStyleAgain);
  }

  @Test
  void getOrCreateStyle_sameFormatDifferentStyleClass_createsIndependentStyles() {
    CellStyle style1 = manager.getOrCreateStyle(TestStyle.class, "#,##0");
    CellStyle style2 = manager.getOrCreateStyle(AnotherTestStyle.class, "#,##0");

    assertNotNull(style1);
    assertNotNull(style2);
    assertNotSame(style1, style2);

    assertEquals(workbook.createDataFormat().getFormat("#,##0"), style1.getDataFormat());
    assertEquals(workbook.createDataFormat().getFormat("#,##0"), style2.getDataFormat());
  }

  @Test
  void getOrCreateStyle_nullStyleClassWithSameFormat_cachesSeparately() {
    CellStyle style1 = manager.getOrCreateStyle(null, "#,##0.00");
    CellStyle style2 = manager.getOrCreateStyle(null, "#,##0.00");
    CellStyle style3 = manager.getOrCreateStyle(TestStyle.class, "#,##0.00");

    assertSame(style1, style2);
    assertNotSame(style1, style3);
  }

  @Test
  void getOrCreateStyle_formatStyleInheritsBaseStyle() {
    CellStyle baseStyle = manager.getOrCreateStyle(TestStyle.class, null);
    CellStyle formatStyle = manager.getOrCreateStyle(TestStyle.class, "0.00%");

    assertNotNull(formatStyle);
    assertNotSame(baseStyle, formatStyle);
    assertEquals(workbook.createDataFormat().getFormat("0.00%"), formatStyle.getDataFormat());
  }

  public static class TestStyle extends CustomExcelCellStyle {

    @Override
    protected void configure(ExcelCellStyleConfigurer configurer) {
      configurer
          .backgroundColor(ExcelColors.lightBlue())
          .font("Arial", 11, FontStyle.BOLD);
    }
  }

  public static class AnotherTestStyle extends CustomExcelCellStyle {

    @Override
    protected void configure(ExcelCellStyleConfigurer configurer) {
      configurer
          .backgroundColor(ExcelColors.lightGreen())
          .font("Arial", 11, FontStyle.ITALIC);
    }
  }
}
