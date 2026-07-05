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
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "products")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Product implements Data {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(length = 100, nullable = false)
    private String name;
    @Column(length = 20, nullable = false)
    private String version;
    private LocalDateTime publishDate;
    @Column(length = 500)
    private String description;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", referencedColumnName = "id", nullable = false)
    private Project project;

    @Override
    public void validateData() throws InvalidDataException {
        if (name == null || name.isBlank()) throw new InvalidDataException("Product name is required");
        if (version == null || version.isBlank()) throw new InvalidDataException("Product version is required");
        if (!version.matches("^v\\d{2}\\.\\d{2}\\.\\d{3}$")) throw new InvalidDataException("Version format must be v00.00.000");
        if (project == null) throw new InvalidDataException("Project is required");
    }
}
