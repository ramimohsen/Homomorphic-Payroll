package org.homomorphicpayroll.service.payroll;


import org.homomorphicpayroll.dto.CreatePayrollRequest;
import org.homomorphicpayroll.dto.PayrollResponse;
import org.homomorphicpayroll.exception.custom.ResourceNotFoundException;

public interface PayrollService {

    PayrollResponse addPayroll(CreatePayrollRequest request) throws ResourceNotFoundException;
}
