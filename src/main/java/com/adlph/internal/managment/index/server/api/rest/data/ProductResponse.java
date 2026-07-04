package com.adlph.internal.managment.index.server.api.rest.data;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {
    private Long id;
    private String name;
    private String version;
    private LocalDate publishDate;
    private String descritpion;
    private Long projectId;
}
