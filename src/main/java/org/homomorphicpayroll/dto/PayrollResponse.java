package org.homomorphicpayroll.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PayrollResponse {

    private LocalDate payrollDate;

    private Long employeeId;

    private LocalDateTime createdAt;
}
