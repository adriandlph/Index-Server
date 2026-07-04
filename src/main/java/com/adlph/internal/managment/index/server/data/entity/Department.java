package com.adlph.internal.managment.index.server.data.entity;

import com.adlph.internal.managment.index.server.data.Data;
import com.adlph.internal.managment.index.server.exception.InvalidDataException;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "departments")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Department implements Data {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 100)
    private String name;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "division_id", referencedColumnName = "id")
    private Division division;
    @OneToMany(mappedBy = "department")
    private List<Project> projects;

    @Override
    public void validateData() throws InvalidDataException {
        if (name == null || name.isBlank()) throw new InvalidDataException("Department name is required");
    }
}
