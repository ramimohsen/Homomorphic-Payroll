package org.homomorphicpayroll.model;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.homomorphicpayroll.dto.EmployeeResponse;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(unique = true, nullable = false)
    private String email;

    @Builder.Default
    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Payroll> payrolls = List.of();

    @CreationTimestamp
    private LocalDateTime createdAt;

    public EmployeeResponse toResponse() {
        return EmployeeResponse
                .builder()
                .employeeId(id)
                .firstName(firstName)
                .lastName(lastName)
                .email(email)
                .createdAt(createdAt)
                .build();
    }
}
