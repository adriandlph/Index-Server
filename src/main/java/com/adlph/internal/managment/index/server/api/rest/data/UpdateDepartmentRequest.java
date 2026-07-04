package com.adlph.internal.managment.index.server.api.rest.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateDepartmentRequest {
    private String name;
    private Long divisionId;
}
