package org.homomorphicpayroll.service.employee;

import org.homomorphicpayroll.dto.CreateEmployeeRequest;
import org.homomorphicpayroll.dto.EmployeeResponse;
import org.homomorphicpayroll.exception.custom.EmployeeAlreadyExistException;
import org.homomorphicpayroll.exception.custom.ResourceNotFoundException;
import org.homomorphicpayroll.model.Employee;

public interface EmployeeService {

    EmployeeResponse createEmployee(CreateEmployeeRequest request) throws EmployeeAlreadyExistException;

    Employee getEmployeeById(Long id) throws ResourceNotFoundException;
}
