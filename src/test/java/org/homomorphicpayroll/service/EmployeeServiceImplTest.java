package org.homomorphicpayroll.service;

import org.homomorphicpayroll.dto.CreateEmployeeRequest;
import org.homomorphicpayroll.dto.EmployeeResponse;
import org.homomorphicpayroll.exception.custom.EmployeeAlreadyExistException;
import org.homomorphicpayroll.exception.custom.ResourceNotFoundException;
import org.homomorphicpayroll.model.Employee;
import org.homomorphicpayroll.repository.EmployeeRepository;
import org.homomorphicpayroll.service.employee.EmployeeServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class EmployeeServiceImplTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private EmployeeServiceImpl employeeService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateEmployee_Success() throws EmployeeAlreadyExistException {
        CreateEmployeeRequest request = new CreateEmployeeRequest("John", "Doe", "john.doe@example.com");
        Employee employee = Employee.builder()
                .email(request.getEmail())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .payrolls(List.of())
                .id(1L)
                .build();

        when(employeeRepository.existsByEmail(anyString())).thenReturn(false);
        when(employeeRepository.save(any(Employee.class))).thenReturn(employee);

        EmployeeResponse response = employeeService.createEmployee(request);

        assertNotNull(response);
        assertEquals("John", response.getFirstName());
        assertEquals("Doe", response.getLastName());
        assertEquals("john.doe@example.com", response.getEmail());
        verify(employeeRepository, times(1)).existsByEmail(anyString());
        verify(employeeRepository, times(1)).save(any(Employee.class));
    }

    @Test
    void testCreateEmployee_AlreadyExists() {
        CreateEmployeeRequest request = new CreateEmployeeRequest("John", "Doe", "john.doe@example.com");

        when(employeeRepository.existsByEmail(anyString())).thenReturn(true);

        assertThrows(EmployeeAlreadyExistException.class, () -> employeeService.createEmployee(request));
        verify(employeeRepository, times(1)).existsByEmail(anyString());
        verify(employeeRepository, never()).save(any(Employee.class));
    }

    @Test
    void testGetEmployeeById_Success() throws ResourceNotFoundException {
        Employee employee = Employee.builder()
                .id(1L)
                .email("john.doe@example.com")
                .firstName("John")
                .lastName("Doe")
                .payrolls(List.of())
                .build();

        when(employeeRepository.findById(anyLong())).thenReturn(Optional.of(employee));

        Employee foundEmployee = employeeService.getEmployeeById(1L);

        assertNotNull(foundEmployee);
        assertEquals(1L, foundEmployee.getId());
        assertEquals("John", foundEmployee.getFirstName());
        assertEquals("Doe", foundEmployee.getLastName());
        assertEquals("john.doe@example.com", foundEmployee.getEmail());
        verify(employeeRepository, times(1)).findById(anyLong());
    }

    @Test
    void testGetEmployeeById_NotFound() {
        when(employeeRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> employeeService.getEmployeeById(1L));
        verify(employeeRepository, times(1)).findById(anyLong());
    }
}