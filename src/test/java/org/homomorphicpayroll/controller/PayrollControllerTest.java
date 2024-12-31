package org.homomorphicpayroll.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.homomorphicpayroll.dto.CreatePayrollRequest;
import org.homomorphicpayroll.dto.PayrollResponse;
import org.homomorphicpayroll.dto.TotalPayrollResponse;
import org.homomorphicpayroll.exception.custom.ResourceNotFoundException;
import org.homomorphicpayroll.model.Employee;
import org.homomorphicpayroll.service.employee.EmployeeService;
import org.homomorphicpayroll.service.payroll.PayrollService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext
class PayrollControllerTest {

    @MockitoBean
    private PayrollService payrollService;

    @MockitoBean
    private EmployeeService employeeService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testAddPayroll() throws Exception {
        // Arrange
        CreatePayrollRequest createPayrollRequest = new CreatePayrollRequest(5000.00, 5000.00, 0.00, 0.00, 0.00, LocalDate.now(), 1L);
        PayrollResponse payrollResponse = new PayrollResponse(LocalDate.now(), 1L, LocalDate.now().atStartOfDay());

        Employee employee = new Employee();
        employee.setId(1L);
        // Mock the EmployeeService behavior
        given(employeeService.getEmployeeById(createPayrollRequest.getEmployeeId())).willReturn(employee);

        // Mock the PayrollService behavior
        given(payrollService.addPayroll(createPayrollRequest)).willReturn(payrollResponse);

        // Act & Assert
        mockMvc.perform(post("/v1/api/payrolls")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createPayrollRequest)))
                .andExpect(status().isOk())  // Correct status for successful creation
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.payrollDate").value(LocalDate.now().toString()));
    }

    @Test
    void testAddPayroll_InvalidData() throws Exception {
        // Arrange
        CreatePayrollRequest invalidRequest = new CreatePayrollRequest(); // Missing data

        // Act & Assert
        mockMvc.perform(post("/v1/api/payrolls")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetTotalPayroll_NotFound() throws Exception {
        // Arrange
        Long invalidEmployeeId = 999L;
        LocalDate fromDate = LocalDate.of(2023, 1, 1);
        LocalDate toDate = LocalDate.of(2023, 12, 31);

        given(payrollService.getTotalPayroll(invalidEmployeeId, fromDate, toDate))
                .willThrow(new ResourceNotFoundException("Employee not found"));

        // Act & Assert
        mockMvc.perform(get("/v1/api/payrolls/total-payroll")
                        .param("employeeId", invalidEmployeeId.toString())
                        .param("from", fromDate.toString())
                        .param("to", toDate.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Employee not found"));
    }

    @Test
    void testGetTotalPayroll() throws Exception {
        // Arrange
        long employeeId = 1L;
        LocalDate fromDate = LocalDate.of(2023, 1, 1);
        LocalDate toDate = LocalDate.of(2023, 12, 31);

        // Prepare mock response
        TotalPayrollResponse totalPayrollResponse = new TotalPayrollResponse(60000.00, employeeId, fromDate, toDate);

        // Mock EmployeeService to return a valid employee
        Employee mockEmployee = new Employee();
        mockEmployee.setId(employeeId);
        given(employeeService.getEmployeeById(employeeId)).willReturn(mockEmployee);

        // Mock PayrollService to return the total payroll response
        given(payrollService.getTotalPayroll(employeeId, fromDate, toDate)).willReturn(totalPayrollResponse);

        // Act & Assert
        mockMvc.perform(get("/v1/api/payrolls/total-payroll")
                        .param("employeeId", Long.toString(employeeId))
                        .param("from", fromDate.toString())
                        .param("to", toDate.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())  // Correct status for success
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.totalNetPayroll").value(60000.00));  // Verify the total payroll value
    }

}
