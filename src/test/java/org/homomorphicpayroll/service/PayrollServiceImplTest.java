package org.homomorphicpayroll.service;

import com.n1analytics.paillier.EncryptedNumber;
import org.homomorphicpayroll.dto.CreatePayrollRequest;
import org.homomorphicpayroll.dto.PayrollResponse;
import org.homomorphicpayroll.dto.TotalPayrollResponse;
import org.homomorphicpayroll.exception.custom.ResourceNotFoundException;
import org.homomorphicpayroll.model.Employee;
import org.homomorphicpayroll.model.Payroll;
import org.homomorphicpayroll.repository.PayrollRepository;
import org.homomorphicpayroll.service.employee.EmployeeService;
import org.homomorphicpayroll.service.payroll.PayrollServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PayrollServiceImplTest {

    @Mock
    private PHEncryptionService phEncryptionService;

    @Mock
    private PayrollRepository payrollRepository;

    @Mock
    private EmployeeService employeeService;

    @InjectMocks
    private PayrollServiceImpl payrollService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddPayroll() throws ResourceNotFoundException {
        CreatePayrollRequest request = new CreatePayrollRequest(5000.00, 5000.00, 0.00, 0.00, 0.00, LocalDate.now(), 1L);
        Employee employee = new Employee();
        employee.setId(1L);

        Payroll payroll = Payroll.builder()
                .gross("encryptedGross")
                .net("encryptedNet")
                .deduction("encryptedDeduction")
                .tax("encryptedTax")
                .pension("encryptedPension")
                .payrollDate(request.getDate())
                .employee(employee)
                .build();

        when(employeeService.getEmployeeById(request.getEmployeeId())).thenReturn(employee);
        when(phEncryptionService.encrypt(anyDouble())).thenReturn(mock(EncryptedNumber.class));
        when(phEncryptionService.serializeEncryptedNumber(any(EncryptedNumber.class))).thenReturn("encryptedValue");
        when(payrollRepository.save(any(Payroll.class))).thenReturn(payroll);

        PayrollResponse response = payrollService.addPayroll(request);

        assertNotNull(response);
        assertEquals(request.getDate(), response.getPayrollDate());
        verify(employeeService, times(1)).getEmployeeById(request.getEmployeeId());
        verify(payrollRepository, times(1)).save(any(Payroll.class));
    }

    @Test
    void testAddPayroll_EmployeeNotFound() throws ResourceNotFoundException {
        CreatePayrollRequest request = new CreatePayrollRequest(5000.00, 5000.00, 0.00, 0.00, 0.00, LocalDate.now(), 1L);

        when(employeeService.getEmployeeById(request.getEmployeeId())).thenThrow(new ResourceNotFoundException("Employee not found"));

        assertThrows(ResourceNotFoundException.class, () -> payrollService.addPayroll(request));
        verify(employeeService, times(1)).getEmployeeById(request.getEmployeeId());
    }

    @Test
    void testGetTotalPayroll() throws ResourceNotFoundException {
        Long employeeId = 1L;
        LocalDate from = LocalDate.of(2023, 1, 1);
        LocalDate to = LocalDate.of(2023, 12, 31);
        Employee employee = new Employee();
        employee.setId(employeeId);

        Payroll payroll = Payroll.builder()
                .net("encryptedNet")
                .build();

        List<Payroll> payrolls = List.of(payroll);
        EncryptedNumber encryptedNet = mock(EncryptedNumber.class);

        when(employeeService.getEmployeeById(employeeId)).thenReturn(employee);
        when(payrollRepository.findByEmployeeIdAndPayrollDateBetween(employeeId, from, to)).thenReturn(payrolls);
        when(phEncryptionService.deserializeEncryptedNumber(anyString(), any())).thenReturn(encryptedNet);
        when(phEncryptionService.addEncryptedNumbers(anyList())).thenReturn(encryptedNet);
        when(phEncryptionService.decrypt(encryptedNet)).thenReturn(60000.00);

        TotalPayrollResponse response = payrollService.getTotalPayroll(employeeId, from, to);

        assertNotNull(response);
        assertEquals(60000.00, response.getTotalNetPayroll());
        verify(employeeService, times(1)).getEmployeeById(employeeId);
        verify(payrollRepository, times(1)).findByEmployeeIdAndPayrollDateBetween(employeeId, from, to);
    }

    @Test
    void testGetTotalPayroll_EmployeeNotFound() throws ResourceNotFoundException {
        Long employeeId = 1L;
        LocalDate from = LocalDate.of(2023, 1, 1);
        LocalDate to = LocalDate.of(2023, 12, 31);

        when(employeeService.getEmployeeById(employeeId)).thenThrow(new ResourceNotFoundException("Employee not found"));

        assertThrows(ResourceNotFoundException.class, () -> payrollService.getTotalPayroll(employeeId, from, to));
        verify(employeeService, times(1)).getEmployeeById(employeeId);
    }
}