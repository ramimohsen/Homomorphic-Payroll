package org.homomorphicpayroll.service.payroll;

import lombok.RequiredArgsConstructor;
import org.homomorphicpayroll.dto.CreatePayrollRequest;
import org.homomorphicpayroll.dto.PayrollResponse;
import org.homomorphicpayroll.exception.custom.ResourceNotFoundException;
import org.homomorphicpayroll.model.Employee;
import org.homomorphicpayroll.model.Payroll;
import org.homomorphicpayroll.repository.PayrollRepository;
import org.homomorphicpayroll.service.PHEncryptionService;
import org.homomorphicpayroll.service.employee.EmployeeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PayrollServiceImpl implements PayrollService {

    private final PHEncryptionService phEncryptionService;
    private final PayrollRepository payrollRepository;
    private final EmployeeService employeeService;

    @Transactional
    @Override
    public PayrollResponse addPayroll(CreatePayrollRequest request) throws ResourceNotFoundException {

        Employee employee = employeeService.getEmployeeById(request.getEmployeeId());

        Payroll payroll = Payroll.builder()
                .gross(phEncryptionService.encrypt(request.getGross()).toString())
                .net(phEncryptionService.encrypt(request.getNet()).toString())
                .deduction(phEncryptionService.encrypt(request.getDeduction()).toString())
                .tax(phEncryptionService.encrypt(request.getTax()).toString())
                .pension(phEncryptionService.encrypt(request.getPension()).toString())
                .payrollDate(request.getDate())
                .employee(employee)
                .build();

        employee.getPayrolls().add(payroll);

        var payrollSaved = this.payrollRepository.save(payroll);

        return payrollSaved.toResponse();
    }
}
