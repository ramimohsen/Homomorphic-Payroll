package org.homomorphicpayroll.service.payroll;


import org.homomorphicpayroll.dto.CreatePayrollRequest;
import org.homomorphicpayroll.dto.PayrollResponse;
import org.homomorphicpayroll.dto.TotalPayrollResponse;
import org.homomorphicpayroll.exception.custom.ResourceNotFoundException;

import java.time.LocalDate;

public interface PayrollService {

    PayrollResponse addPayroll(CreatePayrollRequest request) throws ResourceNotFoundException;

    TotalPayrollResponse getTotalPayroll(Long employeeId, LocalDate from, LocalDate to) throws ResourceNotFoundException;
}
