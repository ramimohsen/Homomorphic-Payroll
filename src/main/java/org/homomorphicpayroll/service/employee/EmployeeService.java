package org.homomorphicpayroll.service.employee;

import org.homomorphicpayroll.dto.CreateEmployeeRequest;
import org.homomorphicpayroll.dto.EmployeeResponse;
import org.homomorphicpayroll.exception.custom.EmployeeAlreadyExistException;
import org.homomorphicpayroll.exception.custom.ResourceNotFoundException;
import org.homomorphicpayroll.model.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EmployeeService {

    EmployeeResponse createEmployee(CreateEmployeeRequest request) throws EmployeeAlreadyExistException;

    Page<EmployeeResponse> getAllEmployees(Pageable pageable);

    Employee getEmployeeById(Long id) throws ResourceNotFoundException;
}
