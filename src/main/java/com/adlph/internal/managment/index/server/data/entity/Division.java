package com.adlph.internal.managment.index.server.data.entity;

import com.adlph.internal.managment.index.server.data.Data;
import com.adlph.internal.managment.index.server.exception.InvalidDataException;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "divisions")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Division implements Data {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 100)
    private String name;
    @OneToMany(mappedBy = "division")
    private List<Department> departments;

    @Override
    public void validateData() throws InvalidDataException {
        if (name == null || name.isBlank()) throw new InvalidDataException("Division name is required");
    }
}
