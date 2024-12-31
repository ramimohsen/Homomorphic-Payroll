package org.homomorphicpayroll.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.homomorphicpayroll.dto.CreatePayrollRequest;
import org.homomorphicpayroll.dto.EmployeeResponse;
import org.homomorphicpayroll.dto.PayrollResponse;
import org.homomorphicpayroll.dto.TotalPayrollResponse;
import org.homomorphicpayroll.exception.custom.ResourceNotFoundException;
import org.homomorphicpayroll.exception.response.ErrorDetails;
import org.homomorphicpayroll.service.payroll.PayrollService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/v1/api/payrolls")
@CrossOrigin(origins = "*")
@Tag(name = "Payroll Controller", description = "Payroll controller")
public class PayrollController {

    private final PayrollService payrollService;

    @Operation(summary = "Add a new payroll")
    @ApiResponse(responseCode = "201", content = {@Content(schema = @Schema(implementation = EmployeeResponse.class), mediaType = "application/json")})
    @ApiResponse(responseCode = "400", content = {@Content(schema = @Schema(implementation = ErrorDetails.class), mediaType = "application/json")})
    @PostMapping
    public PayrollResponse addPayroll(@RequestBody @Valid CreatePayrollRequest payrollRequest) throws ResourceNotFoundException {
        return this.payrollService.addPayroll(payrollRequest);
    }

    @Operation(summary = "Get Total Payroll")
    @ApiResponse(responseCode = "201", content = {@Content(schema = @Schema(implementation = EmployeeResponse.class), mediaType = "application/json")})
    @ApiResponse(responseCode = "400", content = {@Content(schema = @Schema(implementation = ErrorDetails.class), mediaType = "application/json")})
    @GetMapping("/total-payroll")
    public TotalPayrollResponse getTotalPayroll(@RequestParam Long employeeId,
                                                @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
                                                @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) throws ResourceNotFoundException {
        return this.payrollService.getTotalPayroll(employeeId, from, to);
    }
}
