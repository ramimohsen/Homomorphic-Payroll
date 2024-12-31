package org.homomorphicpayroll.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.homomorphicpayroll.dto.PayrollResponse;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Payroll {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String gross; // Encrypted value

    @Column(nullable = false, columnDefinition = "TEXT")
    private String net; // Encrypted value

    @Column(nullable = false, columnDefinition = "TEXT")
    private String deduction; // Encrypted value

    @Column(nullable = false, columnDefinition = "TEXT")
    private String tax; // Encrypted value

    @Column(nullable = false, columnDefinition = "TEXT")
    private String pension; // Encrypted value

    @Column(nullable = false)
    private LocalDate payrollDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Employee employee;

    @CreationTimestamp
    private LocalDateTime createdAt;

    public PayrollResponse toResponse() {
        return PayrollResponse.builder()
                .employeeId(this.employee.getId())
                .payrollDate(this.payrollDate)
                .createdAt(this.createdAt)
                .build();
    }
}
