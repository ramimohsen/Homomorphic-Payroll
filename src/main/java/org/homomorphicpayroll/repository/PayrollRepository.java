package org.homomorphicpayroll.repository;

import org.homomorphicpayroll.model.Payroll;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PayrollRepository extends JpaRepository<Payroll, Long> {

    List<Payroll> findByEmployeeIdAndPayrollDateBetween(Long employeeId, LocalDate startDate, LocalDate endDate);

}
