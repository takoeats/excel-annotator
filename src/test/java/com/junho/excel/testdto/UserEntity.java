package com.junho.excel.testdto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity {
    private Long id;
    private String username;
    private String email;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime lastLoginAt;
}
