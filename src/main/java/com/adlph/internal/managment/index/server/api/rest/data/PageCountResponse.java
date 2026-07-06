package com.adlph.internal.managment.index.server.api.rest.data;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PageCountResponse {
    private long totalCount;
    private int totalPages;
}
