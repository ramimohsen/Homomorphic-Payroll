package org.homomorphicpayroll.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.homomorphicpayroll.dto.CreateEmployeeRequest;
import org.homomorphicpayroll.dto.EmployeeResponse;
import org.homomorphicpayroll.exception.custom.EmployeeAlreadyExistException;
import org.homomorphicpayroll.service.employee.EmployeeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext
class EmployeeControllerTest {


    @MockitoBean
    private EmployeeService employeeService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testCreateEmployee() throws Exception {
        CreateEmployeeRequest createEmployeeRequest = new CreateEmployeeRequest("John", "Doe", "john.doe@example.com");
        EmployeeResponse employeeResponse = new EmployeeResponse(1L, "John", "Doe", "john.doe@example.com", LocalDateTime.now(), null);

        given(employeeService.createEmployee(createEmployeeRequest)).willReturn(employeeResponse);

        mockMvc.perform(post("/v1/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createEmployeeRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.firstName").value("John"));
    }

    @Test
    void testCreateEmployee_InvalidEmail() throws Exception {
        CreateEmployeeRequest createEmployeeRequest = new CreateEmployeeRequest("John", "Doe", "invalid-email");

        mockMvc.perform(post("/v1/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createEmployeeRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCreateEmployeeThrowsEmployeeAlreadyExistException() throws Exception {
        CreateEmployeeRequest createEmployeeRequest = new CreateEmployeeRequest("John", "Doe", "john.doe@example.com");

        given(employeeService.createEmployee(createEmployeeRequest)).willThrow(new EmployeeAlreadyExistException("Employee already exist"));

        mockMvc.perform(post("/v1/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createEmployeeRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Employee already exist"));
    }
}