package com.junho.excel.example.style;

import com.junho.excel.annotation.ExcelColumn;
import com.junho.excel.annotation.ExcelSheet;
import com.junho.excel.annotation.ConditionalStyle;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 조건부 스타일 사용 예제 DTO
 * <p>표현식 기반 조건 + 논리 연산자로 셀 스타일이 동적으로 변경됩니다.</p>
 *
 * <h3>사용된 표현식 예시</h3>
 * <ul>
 *     <li>{@code value < 0} - 음수인 경우</li>
 *     <li>{@code value > 1000000} - 백만 초과인 경우</li>
 *     <li>{@code value > 0 && value < 100} - AND: 0 초과 100 미만</li>
 *     <li>{@code value < 0 || value > 100000} - OR: 0 미만 또는 10만 초과</li>
 *     <li>{@code !(value >= 0 && value <= 100)} - NOT: 0~100 범위 밖</li>
 *     <li>{@code value equals '완료' || value equals '승인'} - 완료 또는 승인</li>
 * </ul>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ExcelSheet(value = "조건부 스타일 예제", hasHeader = true)
public class ConditionalStyleExampleDTO {

    @ExcelColumn(
            header = "거래번호",
            order = 1,
            width = 120
    )
    private String transactionId;

    @ExcelColumn(
            header = "금액",
            order = 2,
            width = 150,
            conditionalStyles = {
                    // 음수
                    @ConditionalStyle(
                            when = "value < 0",
                            style = LightPurpleColumnStyle.class,
                            priority = 30
                    ),
                    // 백만 초과
                    @ConditionalStyle(
                            when = "value > 1000000",
                            style = CriticalAlertStyle.class,
                            priority = 20
                    ),
                    // 0 초과 1000 이하
                    @ConditionalStyle(
                            when = "value > 0 && value <= 1000",
                            style = SignatureStyle.class,
                            priority = 10
                    )
            }
    )
    private BigDecimal amount;

    @ExcelColumn(
            header = "잔액",
            order = 3,
            width = 150,
            conditionalStyles = {
                    // 음수이거나 10만 초과
                    @ConditionalStyle(
                            when = "value < 0 || value > 100000",
                            style = CriticalAlertStyle.class,
                            priority = 10
                    )
            }
    )
    private BigDecimal balance;

    @ExcelColumn(
            header = "상태",
            order = 4,
            width = 100,
            conditionalStyles = {
                    // 완료 또는 승인
                    @ConditionalStyle(
                            when = "value equals '완료' || value equals '승인'",
                            style = SignatureStyle.class,
                            priority = 10
                    ),
                    // 진행중 포함
                    @ConditionalStyle(
                            when = "value contains '진행'",
                            style = SignatureStyle.class,
                            priority = 9
                    ),
                    // 완료도 아니고 진행중도 아님
                    @ConditionalStyle(
                            when = "!(value equals '완료') && !(value contains '진행')",
                            style = SignatureStyle.class,
                            priority = 5
                    )
            }
    )
    private String status;

    @ExcelColumn(
            header = "수량",
            order = 5,
            width = 100,
            conditionalStyles = {
                    // 0이거나 음수
                    @ConditionalStyle(
                            when = "value <= 0",
                            style = CriticalAlertStyle.class,
                            priority = 30
                    ),
                    // 1 이상 10 이하
                    @ConditionalStyle(
                            when = "value >= 1 && value <= 10",
                            style = HighlightStyle.class,
                            priority = 20
                    ),
                    // 100 이상 1000 이하
                    @ConditionalStyle(
                            when = "value between 100 and 1000",
                            style = SignatureStyle.class,
                            priority = 10
                    )
            }
    )
    private Integer quantity;

    @ExcelColumn(
            header = "진행률",
            order = 6,
            width = 100,
            conditionalStyles = {
                    // 100 이상
                    @ConditionalStyle(
                            when = "value >= 100",
                            style = SignatureStyle.class,
                            priority = 20
                    ),
                    // 50 이상 100 미만
                    @ConditionalStyle(
                            when = "value >= 50 && value < 100",
                            style = SignatureStyle.class,
                            priority = 15
                    ),
                    // 0 초과 50 미만
                    @ConditionalStyle(
                            when = "value > 0 && value < 50",
                            style = SignatureStyle.class,
                            priority = 10
                    ),
                    // 0 이하
                    @ConditionalStyle(
                            when = "value <= 0",
                            style = SignatureStyle.class,
                            priority = 5
                    )
            }
    )
    private Double progressPercent;

    @ExcelColumn(
            header = "비고",
            order = 7,
            width = 200
    )
    private String remarks;
}
