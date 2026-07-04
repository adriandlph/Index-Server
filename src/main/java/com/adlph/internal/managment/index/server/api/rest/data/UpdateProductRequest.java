package com.adlph.internal.managment.index.server.api.rest.data;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProductRequest {
    private String name;
    private String version;
    private LocalDate publishDate;
    private String descritpion;
    private Long projectId;
}
