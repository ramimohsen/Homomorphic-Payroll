package org.homomorphicpayroll.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TotalPayrollResponse {

    private double totalNetPayroll;

    private long employeeId;

    private LocalDate from;

    private LocalDate to;
}
