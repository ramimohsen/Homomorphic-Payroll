package org.homomorphicpayroll.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreatePayrollRequest {

    @NotNull
    private double gross;

    @NotNull
    private double net;

    @NotNull
    private double deduction;

    @NotNull
    private double tax;

    @NotNull
    private double pension;

    @NotNull
    private LocalDate date;

    @NotNull
    private Long employeeId;
}
