package com.adlph.internal.managment.index.server.rest;

import com.adlph.internal.managment.index.server.api.rest.DepartmentRestApi;
import com.adlph.internal.managment.index.server.controller.DepartmentControllerInterface;
import com.adlph.internal.managment.index.server.data.vo.DepartmentVO;
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
class DepartmentRestApiTest {

    private MockMvc mockMvc;

    @Mock
    private DepartmentControllerInterface departmentController;

    @BeforeEach
    void setUp() throws Exception {
        var restApi = new DepartmentRestApi();
        var field = DepartmentRestApi.class.getDeclaredField("departmentController");
        field.setAccessible(true);
        field.set(restApi, departmentController);
        mockMvc = MockMvcBuilders.standaloneSetup(restApi).build();
    }

    @Test
    void findAll_noParams() throws Exception {
        when(departmentController.findAllDepartments(null, null, null)).thenReturn(List.of());
        mockMvc.perform(get("/departments")).andExpect(status().isOk());
    }

    @Test
    void findAll_withDivision() throws Exception {
        when(departmentController.findAllDepartments(1L, null, null)).thenReturn(List.of(
            DepartmentVO.builder().id(1L).name("D").divisionId(1L).build()));
        mockMvc.perform(get("/departments").param("divisionId", "1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.length()").value(1));
    }

    @Test
    void findAll_serverError() throws Exception {
        when(departmentController.findAllDepartments(any(), any(), any())).thenThrow(new ServerErrorException(-1, "err"));
        mockMvc.perform(get("/departments")).andExpect(status().isInternalServerError());
    }

    @Test
    void pages_default() throws Exception {
        when(departmentController.countDepartments(null)).thenReturn(0L);
        mockMvc.perform(get("/departments/pages"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.totalPages").value(0));
    }

    @Test
    void pages_withDivisionFilter() throws Exception {
        when(departmentController.countDepartments(1L)).thenReturn(7L);
        mockMvc.perform(get("/departments/pages")
                .param("divisionId", "1")
                .param("count", "5"))
            .andExpect(jsonPath("$.data.totalCount").value(7))
            .andExpect(jsonPath("$.data.totalPages").value(2));
    }

    @Test
    void create_valid() throws Exception {
        when(departmentController.createDepartment(any())).thenReturn(
            DepartmentVO.builder().id(1L).name("Dept").divisionId(1L).build());
        mockMvc.perform(post("/departments")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Dept\",\"divisionId\":1}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.id").value(1));
    }

    @Test
    void create_blankName() throws Exception {
        mockMvc.perform(post("/departments")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"\",\"divisionId\":1}"))
            .andExpect(status().isBadRequest());
    }

    @Test
    void update_valid() throws Exception {
        when(departmentController.updateDepartment(any())).thenReturn(
            DepartmentVO.builder().id(1L).name("Upd").divisionId(1L).build());
        mockMvc.perform(put("/departments/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Upd\",\"divisionId\":1}"))
            .andExpect(status().isOk());
    }

    @Test
    void delete_valid() throws Exception {
        doNothing().when(departmentController).deleteDepartment(any());
        mockMvc.perform(delete("/departments/1")).andExpect(status().isOk());
    }

    @Test
    void delete_notExists() throws Exception {
        doThrow(new InvalidDataException("Department does not exists")).when(departmentController).deleteDepartment(any());
        mockMvc.perform(delete("/departments/99")).andExpect(status().isBadRequest());
    }

    @Test
    void findAll_withPagination() throws Exception {
        when(departmentController.findAllDepartments(null, 10, 0)).thenReturn(List.of());
        mockMvc.perform(get("/departments").param("count", "10").param("page", "0"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.length()").value(0));
    }

    @Test
    void findAll_emptyResult() throws Exception {
        when(departmentController.findAllDepartments(any(), any(), any())).thenReturn(List.of());
        mockMvc.perform(get("/departments"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.length()").value(0));
    }

    @Test
    void findById_exists() throws Exception {
        when(departmentController.getDepartmentById(any())).thenReturn(
            DepartmentVO.builder().id(1L).name("Dept").divisionId(2L).build());
        mockMvc.perform(get("/departments/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.id").value(1))
            .andExpect(jsonPath("$.data.name").value("Dept"))
            .andExpect(jsonPath("$.data.divisionId").value(2));
    }

    @Test
    void findById_notFound() throws Exception {
        when(departmentController.getDepartmentById(any())).thenThrow(new InvalidDataException("not found"));
        mockMvc.perform(get("/departments/99"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value(1));
    }

    @Test
    void findById_serverError() throws Exception {
        when(departmentController.getDepartmentById(any())).thenThrow(new ServerErrorException(-1, "err"));
        mockMvc.perform(get("/departments/1"))
            .andExpect(status().isInternalServerError());
    }

    @Test
    void create_nullDivisionId() throws Exception {
        when(departmentController.createDepartment(any())).thenThrow(new InvalidDataException("Division ID is required"));
        mockMvc.perform(post("/departments")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Dept\"}"))
            .andExpect(status().isBadRequest());
    }

    @Test
    void create_serverError() throws Exception {
        when(departmentController.createDepartment(any())).thenThrow(new ServerErrorException(-1, "err"));
        mockMvc.perform(post("/departments")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Dept\",\"divisionId\":1}"))
            .andExpect(status().isInternalServerError());
    }

    @Test
    void create_divisionNotFound() throws Exception {
        when(departmentController.createDepartment(any())).thenThrow(new InvalidDataException("Division not found"));
        mockMvc.perform(post("/departments")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Dept\",\"divisionId\":99}"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value(1));
    }

    @Test
    void update_blankName() throws Exception {
        mockMvc.perform(put("/departments/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"\",\"divisionId\":1}"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value(1));
    }

    @Test
    void update_notFound() throws Exception {
        when(departmentController.updateDepartment(any())).thenThrow(new InvalidDataException("Department not found"));
        mockMvc.perform(put("/departments/99")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Dept\",\"divisionId\":1}"))
            .andExpect(status().isBadRequest());
    }

    @Test
    void update_serverError() throws Exception {
        when(departmentController.updateDepartment(any())).thenThrow(new ServerErrorException(-1, "err"));
        mockMvc.perform(put("/departments/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Dept\",\"divisionId\":1}"))
            .andExpect(status().isInternalServerError());
    }

    @Test
    void delete_serverError() throws Exception {
        doThrow(new ServerErrorException(-1, "err")).when(departmentController).deleteDepartment(any());
        mockMvc.perform(delete("/departments/1"))
            .andExpect(status().isInternalServerError());
    }

    @Test
    void pages_serverError() throws Exception {
        when(departmentController.countDepartments(any())).thenThrow(new ServerErrorException(-1, "err"));
        mockMvc.perform(get("/departments/pages"))
            .andExpect(status().isInternalServerError())
            .andExpect(jsonPath("$.code").value(-1));
    }

    @Test
    void pages_zeroRecords() throws Exception {
        when(departmentController.countDepartments(null)).thenReturn(0L);
        mockMvc.perform(get("/departments/pages").param("count", "10"))
            .andExpect(jsonPath("$.data.totalCount").value(0))
            .andExpect(jsonPath("$.data.totalPages").value(0));
    }

    @Test
    void pages_exactDivision() throws Exception {
        when(departmentController.countDepartments(5L)).thenReturn(10L);
        mockMvc.perform(get("/departments/pages")
                .param("divisionId", "5")
                .param("count", "5"))
            .andExpect(jsonPath("$.data.totalCount").value(10))
            .andExpect(jsonPath("$.data.totalPages").value(2));
    }

    @Test
    void findAll_countOnly() throws Exception {
        when(departmentController.findAllDepartments(null, 5, null)).thenReturn(List.of());
        mockMvc.perform(get("/departments").param("count", "5"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.length()").value(0));
    }

    @Test
    void findAll_pageOnly() throws Exception {
        when(departmentController.findAllDepartments(null, null, 1)).thenReturn(List.of());
        mockMvc.perform(get("/departments").param("page", "1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.length()").value(0));
    }

    @Test
    void findAll_withDivisionAndPagination() throws Exception {
        when(departmentController.findAllDepartments(1L, 10, 0)).thenReturn(List.of(
            DepartmentVO.builder().id(1L).name("D").divisionId(1L).build()));
        mockMvc.perform(get("/departments")
                .param("divisionId", "1")
                .param("count", "10")
                .param("page", "0"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.length()").value(1));
    }

    @Test
    void findAll_invalidCount() throws Exception {
        mockMvc.perform(get("/departments").param("count", "abc"))
            .andExpect(status().isBadRequest());
    }

    @Test
    void findAll_invalidPage() throws Exception {
        mockMvc.perform(get("/departments").param("page", "abc"))
            .andExpect(status().isBadRequest());
    }

    @Test
    void pages_countOnly() throws Exception {
        when(departmentController.countDepartments(null)).thenReturn(25L);
        mockMvc.perform(get("/departments/pages").param("count", "10"))
            .andExpect(jsonPath("$.data.totalCount").value(25))
            .andExpect(jsonPath("$.data.totalPages").value(3));
    }

    @Test
    void pages_countOne() throws Exception {
        when(departmentController.countDepartments(null)).thenReturn(3L);
        mockMvc.perform(get("/departments/pages").param("count", "1"))
            .andExpect(jsonPath("$.data.totalPages").value(3));
    }

    @Test
    void pages_invalidCount() throws Exception {
        mockMvc.perform(get("/departments/pages").param("count", "abc"))
            .andExpect(status().isBadRequest());
    }

    @Test
    void pages_countZero() throws Exception {
        when(departmentController.countDepartments(null)).thenReturn(5L);
        mockMvc.perform(get("/departments/pages").param("count", "0"))
            .andExpect(jsonPath("$.data.totalPages").value(1));
    }

    @Test
    void pages_countEqualTotal() throws Exception {
        when(departmentController.countDepartments(null)).thenReturn(10L);
        mockMvc.perform(get("/departments/pages").param("count", "10"))
            .andExpect(jsonPath("$.data.totalCount").value(10))
            .andExpect(jsonPath("$.data.totalPages").value(1));
    }

    @Test
    void findById_nonNumericId() throws Exception {
        mockMvc.perform(get("/departments/abc"))
            .andExpect(status().isBadRequest());
    }

    @Test
    void findById_negativeId() throws Exception {
        when(departmentController.getDepartmentById(any())).thenThrow(new InvalidDataException("Invalid ID"));
        mockMvc.perform(get("/departments/-1"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value(1));
    }

    @Test
    void findById_emptyIdPath() throws Exception {
        mockMvc.perform(get("/departments/"))
            .andExpect(status().isNotFound());
    }

    @Test
    void create_nullName() throws Exception {
        mockMvc.perform(post("/departments")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value(1));
    }

    @Test
    void create_emptyBody() throws Exception {
        mockMvc.perform(post("/departments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(""))
            .andExpect(status().isBadRequest());
    }

    @Test
    void create_nonJsonBody() throws Exception {
        mockMvc.perform(post("/departments")
                .contentType(MediaType.APPLICATION_JSON)
                .content("not-json"))
            .andExpect(status().isBadRequest());
    }

    @Test
    void create_malformedJson() throws Exception {
        mockMvc.perform(post("/departments")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{malformed"))
            .andExpect(status().isBadRequest());
    }

    @Test
    void create_extraFields() throws Exception {
        when(departmentController.createDepartment(any())).thenReturn(
            DepartmentVO.builder().id(1L).name("Dept").divisionId(1L).build());
        mockMvc.perform(post("/departments")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Dept\",\"divisionId\":1,\"extra\":\"val\"}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.id").value(1));
    }

    @Test
    void update_nullName() throws Exception {
        mockMvc.perform(put("/departments/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value(1));
    }

    @Test
    void update_emptyBody() throws Exception {
        mockMvc.perform(put("/departments/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(""))
            .andExpect(status().isBadRequest());
    }

    @Test
    void update_nonJsonBody() throws Exception {
        mockMvc.perform(put("/departments/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("not-json"))
            .andExpect(status().isBadRequest());
    }

    @Test
    void update_malformedJson() throws Exception {
        mockMvc.perform(put("/departments/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{broken"))
            .andExpect(status().isBadRequest());
    }

    @Test
    void update_invalidPathId() throws Exception {
        mockMvc.perform(put("/departments/abc")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Dept\",\"divisionId\":1}"))
            .andExpect(status().isBadRequest());
    }

    @Test
    void delete_invalidPathId() throws Exception {
        mockMvc.perform(delete("/departments/abc"))
            .andExpect(status().isBadRequest());
    }

    @Test
    void delete_emptyIdPath() throws Exception {
        mockMvc.perform(delete("/departments/"))
            .andExpect(status().isNotFound());
    }
}
