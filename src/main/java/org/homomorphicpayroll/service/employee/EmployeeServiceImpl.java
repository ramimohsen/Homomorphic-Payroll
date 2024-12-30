package org.homomorphicpayroll.service.employee;

import lombok.RequiredArgsConstructor;
import org.homomorphicpayroll.dto.CreateEmployeeRequest;
import org.homomorphicpayroll.dto.EmployeeResponse;
import org.homomorphicpayroll.exception.custom.EmployeeAlreadyExistException;
import org.homomorphicpayroll.exception.custom.ResourceNotFoundException;
import org.homomorphicpayroll.model.Employee;
import org.homomorphicpayroll.repository.EmployeeRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;

    @Transactional
    @Override
    public EmployeeResponse createEmployee(CreateEmployeeRequest request) throws EmployeeAlreadyExistException {

        if (Boolean.TRUE.equals(this.employeeRepository.existsByEmail(request.getEmail()))) {
            throw new EmployeeAlreadyExistException("Employee already exist");
        }

        Employee employee = Employee
                .builder()
                .email(request.getEmail())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .payrolls(List.of())
                .build();

        return this.employeeRepository.save(employee).toResponse();
    }


    @Override
    public Page<EmployeeResponse> getAllEmployees(Pageable pageable) {
        return this.employeeRepository.findAll(pageable).map(Employee::toResponse);
    }

    @Override
    public Employee getEmployeeById(Long id) throws ResourceNotFoundException {
        return this.employeeRepository
                .findById(id).orElseThrow(() -> new ResourceNotFoundException("Employee not found"));
    }
}
