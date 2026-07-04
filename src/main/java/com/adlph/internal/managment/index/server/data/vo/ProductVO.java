package com.adlph.internal.managment.index.server.data.vo;

import java.io.Serializable;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class ProductVO implements Serializable {
    private Long id;
    private String name;
    private String version;
    private LocalDate publishDate;
    private String descritpion;
    private Long projectId;
}
