package com.adlph.internal.managment.index.server.rest;

import com.adlph.internal.managment.index.server.api.rest.ProductRestApi;
import com.adlph.internal.managment.index.server.controller.ProductControllerInterface;
import com.adlph.internal.managment.index.server.data.vo.ProductVO;
import com.adlph.internal.managment.index.server.exception.InvalidDataException;
import com.adlph.internal.managment.index.server.exception.ServerErrorException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ProductRestApiTest {

    private MockMvc mockMvc;

    @Mock
    private ProductControllerInterface productController;

    @BeforeEach
    void setUp() throws Exception {
        var restApi = new ProductRestApi();
        var field = ProductRestApi.class.getDeclaredField("productController");
        field.setAccessible(true);
        field.set(restApi, productController);
        mockMvc = MockMvcBuilders.standaloneSetup(restApi).build();
    }

    @Test
    void findAll_noParams() throws Exception {
        when(productController.findAllProducts(null, null, null, null, null)).thenReturn(List.of());
        mockMvc.perform(get("/products")).andExpect(status().isOk());
    }

    @Test
    void findAll_withAllFilters() throws Exception {
        when(productController.findAllProducts(1L, 2L, 3L, null, null)).thenReturn(List.of());
        mockMvc.perform(get("/products")
                .param("divisionId", "1")
                .param("departmentId", "2")
                .param("projectId", "3"))
            .andExpect(status().isOk());
    }

    @Test
    void findAll_serverError() throws Exception {
        when(productController.findAllProducts(any(), any(), any(), any(), any())).thenThrow(new ServerErrorException(-1, "err"));
        mockMvc.perform(get("/products")).andExpect(status().isInternalServerError());
    }

    @Test
    void pages_withProjectFilter() throws Exception {
        when(productController.countProducts(null, null, 3L)).thenReturn(23L);
        mockMvc.perform(get("/products/pages")
                .param("projectId", "3")
                .param("count", "10"))
            .andExpect(jsonPath("$.data.totalCount").value(23))
            .andExpect(jsonPath("$.data.totalPages").value(3));
    }

    @Test
    void pages_noCount() throws Exception {
        when(productController.countProducts(null, null, null)).thenReturn(7L);
        mockMvc.perform(get("/products/pages"))
            .andExpect(jsonPath("$.data.totalPages").value(1));
    }

    @Test
    void create_valid() throws Exception {
        when(productController.createProduct(any())).thenReturn(
            ProductVO.builder().id(1L).name("Prod").version("v01.02.003").projectId(1L).build());
        mockMvc.perform(post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Prod\",\"version\":\"v01.02.003\",\"projectId\":1}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.version").value("v01.02.003"));
    }

    @Test
    void create_noName() throws Exception {
        mockMvc.perform(post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"version\":\"v01.02.003\",\"projectId\":1}"))
            .andExpect(status().isBadRequest());
    }

    @Test
    void update_valid() throws Exception {
        when(productController.updateProduct(any())).thenReturn(
            ProductVO.builder().id(1L).name("Upd").version("v01.02.003").projectId(1L).build());
        mockMvc.perform(put("/products/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Upd\",\"version\":\"v01.02.003\",\"projectId\":1}"))
            .andExpect(status().isOk());
    }

    @Test
    void getById_exists() throws Exception {
        when(productController.getProductById(any())).thenReturn(
            ProductVO.builder().id(1L).name("P").version("v01.02.003").projectId(1L).build());
        mockMvc.perform(get("/products/1")).andExpect(status().isOk());
    }

    @Test
    void getById_notFound() throws Exception {
        when(productController.getProductById(any())).thenThrow(new InvalidDataException("not found"));
        mockMvc.perform(get("/products/99")).andExpect(status().isBadRequest());
    }

    @Test
    void delete_valid() throws Exception {
        doNothing().when(productController).deleteProduct(any());
        mockMvc.perform(delete("/products/1")).andExpect(status().isOk());
    }

    @Test
    void delete_notFound() throws Exception {
        doThrow(new InvalidDataException("not found")).when(productController).deleteProduct(any());
        mockMvc.perform(delete("/products/99")).andExpect(status().isBadRequest());
    }

    @Test
    void findAll_byProjectOnly() throws Exception {
        when(productController.findAllProducts(null, null, 1L, null, null)).thenReturn(List.of(
            ProductVO.builder().id(1L).name("P").version("v1").projectId(1L).build()));
        mockMvc.perform(get("/products").param("projectId", "1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.length()").value(1));
    }

    @Test
    void findAll_byDepartmentOnly() throws Exception {
        when(productController.findAllProducts(null, 1L, null, null, null)).thenReturn(List.of());
        mockMvc.perform(get("/products").param("departmentId", "1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.length()").value(0));
    }

    @Test
    void findAll_byDivisionOnly() throws Exception {
        when(productController.findAllProducts(1L, null, null, null, null)).thenReturn(List.of());
        mockMvc.perform(get("/products").param("divisionId", "1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.length()").value(0));
    }

    @Test
    void findAll_withPagination() throws Exception {
        when(productController.findAllProducts(null, null, null, 10, 0)).thenReturn(List.of());
        mockMvc.perform(get("/products").param("count", "10").param("page", "0"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.length()").value(0));
    }

    @Test
    void findAll_empty() throws Exception {
        when(productController.findAllProducts(any(), any(), any(), any(), any())).thenReturn(List.of());
        mockMvc.perform(get("/products"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.length()").value(0));
    }

    @Test
    void findById_exists_fieldAssertions() throws Exception {
        when(productController.getProductById(any())).thenReturn(
            ProductVO.builder().id(1L).name("Test Product").version("v01.02.003").projectId(2L).build());
        mockMvc.perform(get("/products/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.id").value(1))
            .andExpect(jsonPath("$.data.name").value("Test Product"))
            .andExpect(jsonPath("$.data.version").value("v01.02.003"))
            .andExpect(jsonPath("$.data.projectId").value(2));
    }

    @Test
    void findById_serverError() throws Exception {
        when(productController.getProductById(any())).thenThrow(new ServerErrorException(-1, "err"));
        mockMvc.perform(get("/products/1"))
            .andExpect(status().isInternalServerError());
    }

    @Test
    void create_noProjectId() throws Exception {
        when(productController.createProduct(any())).thenThrow(new InvalidDataException("Project ID is required"));
        mockMvc.perform(post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Prod\",\"version\":\"v01.02.003\"}"))
            .andExpect(status().isBadRequest());
    }

    @Test
    void create_blankName() throws Exception {
        mockMvc.perform(post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"\",\"version\":\"v01.02.003\",\"projectId\":1}"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value(1));
    }

    @Test
    void create_invalidVersion() throws Exception {
        when(productController.createProduct(any())).thenThrow(new InvalidDataException("Invalid version format"));
        mockMvc.perform(post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Prod\",\"version\":\"bad\",\"projectId\":1}"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value(1));
    }

    @Test
    void create_serverError() throws Exception {
        when(productController.createProduct(any())).thenThrow(new ServerErrorException(-1, "err"));
        mockMvc.perform(post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Prod\",\"version\":\"v01.02.003\",\"projectId\":1}"))
            .andExpect(status().isInternalServerError());
    }

    @Test
    void create_projectNotFound() throws Exception {
        when(productController.createProduct(any())).thenThrow(new InvalidDataException("Project not found"));
        mockMvc.perform(post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Prod\",\"version\":\"v01.02.003\",\"projectId\":99}"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value(1));
    }

    @Test
    void update_blankName() throws Exception {
        mockMvc.perform(put("/products/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"\",\"version\":\"v01.02.003\",\"projectId\":1}"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value(1));
    }

    @Test
    void update_invalidVersion() throws Exception {
        when(productController.updateProduct(any())).thenThrow(new InvalidDataException("Invalid version format"));
        mockMvc.perform(put("/products/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Prod\",\"version\":\"bad\",\"projectId\":1}"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value(1));
    }

    @Test
    void update_notFound() throws Exception {
        when(productController.updateProduct(any())).thenThrow(new InvalidDataException("Product not found"));
        mockMvc.perform(put("/products/99")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Prod\",\"version\":\"v01.02.003\",\"projectId\":1}"))
            .andExpect(status().isBadRequest());
    }

    @Test
    void update_serverError() throws Exception {
        when(productController.updateProduct(any())).thenThrow(new ServerErrorException(-1, "err"));
        mockMvc.perform(put("/products/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Prod\",\"version\":\"v01.02.003\",\"projectId\":1}"))
            .andExpect(status().isInternalServerError());
    }

    @Test
    void delete_serverError() throws Exception {
        doThrow(new ServerErrorException(-1, "err")).when(productController).deleteProduct(any());
        mockMvc.perform(delete("/products/1"))
            .andExpect(status().isInternalServerError());
    }

    @Test
    void pages_byDepartmentFilter() throws Exception {
        when(productController.countProducts(null, 1L, null)).thenReturn(8L);
        mockMvc.perform(get("/products/pages")
                .param("departmentId", "1")
                .param("count", "5"))
            .andExpect(jsonPath("$.data.totalCount").value(8))
            .andExpect(jsonPath("$.data.totalPages").value(2));
    }

    @Test
    void pages_byDivisionFilter() throws Exception {
        when(productController.countProducts(2L, null, null)).thenReturn(3L);
        mockMvc.perform(get("/products/pages")
                .param("divisionId", "2")
                .param("count", "10"))
            .andExpect(jsonPath("$.data.totalCount").value(3))
            .andExpect(jsonPath("$.data.totalPages").value(1));
    }

    @Test
    void pages_serverError() throws Exception {
        when(productController.countProducts(any(), any(), any())).thenThrow(new ServerErrorException(-1, "err"));
        mockMvc.perform(get("/products/pages"))
            .andExpect(status().isInternalServerError())
            .andExpect(jsonPath("$.code").value(-1));
    }

    @Test
    void pages_zeroRecords() throws Exception {
        when(productController.countProducts(null, null, null)).thenReturn(0L);
        mockMvc.perform(get("/products/pages").param("count", "10"))
            .andExpect(jsonPath("$.data.totalCount").value(0))
            .andExpect(jsonPath("$.data.totalPages").value(0));
    }

    @Test
    void findAll_invalidCount() throws Exception {
        mockMvc.perform(get("/products").param("count", "abc"))
            .andExpect(status().isBadRequest());
    }

    @Test
    void findAll_invalidPage() throws Exception {
        mockMvc.perform(get("/products").param("page", "abc"))
            .andExpect(status().isBadRequest());
    }

    @Test
    void findAll_countOnly() throws Exception {
        when(productController.findAllProducts(null, null, null, 5, null)).thenReturn(List.of());
        mockMvc.perform(get("/products").param("count", "5"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.length()").value(0));
    }

    @Test
    void findAll_pageOnly() throws Exception {
        when(productController.findAllProducts(null, null, null, null, 1)).thenReturn(List.of());
        mockMvc.perform(get("/products").param("page", "1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.length()").value(0));
    }

    @Test
    void pages_countOne() throws Exception {
        when(productController.countProducts(null, null, null)).thenReturn(3L);
        mockMvc.perform(get("/products/pages").param("count", "1"))
            .andExpect(jsonPath("$.data.totalPages").value(3));
    }

    @Test
    void pages_invalidCount() throws Exception {
        mockMvc.perform(get("/products/pages").param("count", "abc"))
            .andExpect(status().isBadRequest());
    }

    @Test
    void findById_nonNumericId() throws Exception {
        mockMvc.perform(get("/products/abc"))
            .andExpect(status().isBadRequest());
    }

    @Test
    void findById_negativeId() throws Exception {
        when(productController.getProductById(any())).thenThrow(new InvalidDataException("Invalid ID"));
        mockMvc.perform(get("/products/-1"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value(1));
    }

    @Test
    void create_emptyBody() throws Exception {
        mockMvc.perform(post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(""))
            .andExpect(status().isBadRequest());
    }

    @Test
    void create_nonJsonBody() throws Exception {
        mockMvc.perform(post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content("not-json"))
            .andExpect(status().isBadRequest());
    }

    @Test
    void create_malformedJson() throws Exception {
        mockMvc.perform(post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{malformed"))
            .andExpect(status().isBadRequest());
    }

    @Test
    void create_extraFields() throws Exception {
        when(productController.createProduct(any())).thenReturn(
            ProductVO.builder().id(1L).name("Prod").version("v1").projectId(1L).build());
        mockMvc.perform(post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Prod\",\"version\":\"v1\",\"projectId\":1,\"extra\":\"val\"}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.id").value(1));
    }

    @Test
    void update_nullName() throws Exception {
        mockMvc.perform(put("/products/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value(1));
    }

    @Test
    void update_emptyBody() throws Exception {
        mockMvc.perform(put("/products/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(""))
            .andExpect(status().isBadRequest());
    }

    @Test
    void update_malformedJson() throws Exception {
        mockMvc.perform(put("/products/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{broken"))
            .andExpect(status().isBadRequest());
    }

    @Test
    void update_invalidPathId() throws Exception {
        mockMvc.perform(put("/products/abc")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Prod\",\"version\":\"v1\",\"projectId\":1}"))
            .andExpect(status().isBadRequest());
    }

    @Test
    void delete_invalidPathId() throws Exception {
        mockMvc.perform(delete("/products/abc"))
            .andExpect(status().isBadRequest());
    }
}
