package io.github.takoeats.excelannotator.testdto;

import io.github.takoeats.excelannotator.annotation.ExcelColumn;
import io.github.takoeats.excelannotator.annotation.ExcelSheet;
import io.github.takoeats.excelannotator.example.style.DateTimeStyle;
import io.github.takoeats.excelannotator.example.style.PurpleHeaderStyle;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ExcelSheet("User List")
public class UserExcelDTO {

    @ExcelColumn(header = "User ID", order = 1, headerStyle = PurpleHeaderStyle.class)
    private Long id;

    @ExcelColumn(header = "Username", order = 2)
    private String username;

    @ExcelColumn(header = "Email", order = 3)
    private String email;

    @ExcelColumn(header = "Status", order = 4)
    private String status;

    @ExcelColumn(header = "Created At", order = 5, columnStyle = DateTimeStyle.class)
    private LocalDateTime createdAt;

    @ExcelColumn(header = "Last Login", order = 6, columnStyle = DateTimeStyle.class)
    private LocalDateTime lastLoginAt;

    public static UserExcelDTO fromEntity(UserEntity entity) {
        return UserExcelDTO.builder()
            .id(entity.getId())
            .username(entity.getUsername())
            .email(entity.getEmail())
            .status(entity.getStatus())
            .createdAt(entity.getCreatedAt())
            .lastLoginAt(entity.getLastLoginAt())
            .build();
    }
}
