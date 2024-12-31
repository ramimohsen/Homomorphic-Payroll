package org.homomorphicpayroll.service.payroll;

import com.n1analytics.paillier.EncryptedNumber;
import lombok.RequiredArgsConstructor;
import org.homomorphicpayroll.dto.CreatePayrollRequest;
import org.homomorphicpayroll.dto.PayrollResponse;
import org.homomorphicpayroll.dto.TotalPayrollResponse;
import org.homomorphicpayroll.exception.custom.ResourceNotFoundException;
import org.homomorphicpayroll.model.Employee;
import org.homomorphicpayroll.model.Payroll;
import org.homomorphicpayroll.repository.PayrollRepository;
import org.homomorphicpayroll.service.PHEncryptionService;
import org.homomorphicpayroll.service.employee.EmployeeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

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
                .gross(this.phEncryptionService.serializeEncryptedNumber(phEncryptionService.encrypt(request.getGross())))
                .net(this.phEncryptionService.serializeEncryptedNumber(phEncryptionService.encrypt(request.getNet())))
                .deduction(this.phEncryptionService.serializeEncryptedNumber(phEncryptionService.encrypt(request.getDeduction())))
                .tax(this.phEncryptionService.serializeEncryptedNumber(phEncryptionService.encrypt(request.getTax())))
                .pension(this.phEncryptionService.serializeEncryptedNumber(phEncryptionService.encrypt(request.getPension())))
                .payrollDate(request.getDate())
                .employee(employee)
                .build();

        employee.getPayrolls().add(payroll);

        var payrollSaved = this.payrollRepository.save(payroll);

        return payrollSaved.toResponse();
    }

    @Override
    public TotalPayrollResponse getTotalPayroll(Long employeeId, LocalDate from, LocalDate to) throws ResourceNotFoundException {
        Employee employee = this.employeeService.getEmployeeById(employeeId);

        var payrolls = this.payrollRepository.findByEmployeeIdAndPayrollDateBetween(employee.getId(), from, to);

        List<EncryptedNumber> netPayrolls = payrolls.stream().map(p ->
                phEncryptionService.deserializeEncryptedNumber(p.getNet(), phEncryptionService.getContext())).toList();

        double totalNetPayroll = phEncryptionService.decrypt(phEncryptionService.addEncryptedNumbers(netPayrolls));

        return TotalPayrollResponse.builder()
                .employeeId(employeeId)
                .from(from)
                .to(to)
                .totalNetPayroll(totalNetPayroll)
                .build();
    }
}
