package com.adlph.internal.managment.index.server.api.rest.data;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateProductRequest {
    private String name;
    private String version;
    private LocalDateTime publishDate;
    private String description;
    private Long projectId;
}
