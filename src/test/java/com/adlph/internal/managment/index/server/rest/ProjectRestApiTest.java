package com.adlph.internal.managment.index.server.rest;

import com.adlph.internal.managment.index.server.api.rest.ProjectRestApi;
import com.adlph.internal.managment.index.server.controller.ProjectControllerInterface;
import com.adlph.internal.managment.index.server.data.vo.ProjectVO;
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
class ProjectRestApiTest {

    private MockMvc mockMvc;

    @Mock
    private ProjectControllerInterface projectController;

    @BeforeEach
    void setUp() throws Exception {
        var restApi = new ProjectRestApi();
        var field = ProjectRestApi.class.getDeclaredField("projectController");
        field.setAccessible(true);
        field.set(restApi, projectController);
        mockMvc = MockMvcBuilders.standaloneSetup(restApi).build();
    }

    @Test
    void findAll_noParams() throws Exception {
        when(projectController.findAllProjects(null, null, null, null)).thenReturn(List.of());
        mockMvc.perform(get("/projects")).andExpect(status().isOk());
    }

    @Test
    void findAll_withFilters() throws Exception {
        when(projectController.findAllProjects(1L, 2L, null, null)).thenReturn(List.of(
            ProjectVO.builder().id(1L).name("P").departmentId(2L).build()));
        mockMvc.perform(get("/projects")
                .param("divisionId", "1")
                .param("departmentId", "2"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.length()").value(1));
    }

    @Test
    void findAll_serverError() throws Exception {
        when(projectController.findAllProjects(any(), any(), any(), any())).thenThrow(new ServerErrorException(-1, "err"));
        mockMvc.perform(get("/projects")).andExpect(status().isInternalServerError());
    }

    @Test
    void pages_withAllFilters() throws Exception {
        when(projectController.countProjects(1L, 2L)).thenReturn(15L);
        mockMvc.perform(get("/projects/pages")
                .param("divisionId", "1")
                .param("departmentId", "2")
                .param("count", "10"))
            .andExpect(jsonPath("$.data.totalCount").value(15))
            .andExpect(jsonPath("$.data.totalPages").value(2));
    }

    @Test
    void pages_singlePage() throws Exception {
        when(projectController.countProjects(null, null)).thenReturn(3L);
        mockMvc.perform(get("/projects/pages").param("count", "10"))
            .andExpect(jsonPath("$.data.totalPages").value(1));
    }

    @Test
    void create_valid() throws Exception {
        when(projectController.createProject(any())).thenReturn(
            ProjectVO.builder().id(1L).name("P").departmentId(1L).build());
        mockMvc.perform(post("/projects")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"P\",\"departmentId\":1}"))
            .andExpect(status().isOk());
    }

    @Test
    void create_noName() throws Exception {
        mockMvc.perform(post("/projects")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"departmentId\":1}"))
            .andExpect(status().isBadRequest());
    }

    @Test
    void update_valid() throws Exception {
        when(projectController.updateProject(any())).thenReturn(
            ProjectVO.builder().id(1L).name("Upd").departmentId(1L).build());
        mockMvc.perform(put("/projects/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Upd\",\"departmentId\":1}"))
            .andExpect(status().isOk());
    }

    @Test
    void getById_exists() throws Exception {
        when(projectController.getProjectById(any())).thenReturn(
            ProjectVO.builder().id(1L).name("P").departmentId(1L).build());
        mockMvc.perform(get("/projects/1")).andExpect(status().isOk());
    }

    @Test
    void getById_notFound() throws Exception {
        when(projectController.getProjectById(any())).thenThrow(new InvalidDataException("not found"));
        mockMvc.perform(get("/projects/99")).andExpect(status().isBadRequest());
    }

    @Test
    void delete_valid() throws Exception {
        doNothing().when(projectController).deleteProject(any());
        mockMvc.perform(delete("/projects/1")).andExpect(status().isOk());
    }

    @Test
    void delete_notFound() throws Exception {
        doThrow(new InvalidDataException("not found")).when(projectController).deleteProject(any());
        mockMvc.perform(delete("/projects/99")).andExpect(status().isBadRequest());
    }

    @Test
    void findAll_byDepartmentOnly() throws Exception {
        when(projectController.findAllProjects(null, 1L, null, null)).thenReturn(List.of(
            ProjectVO.builder().id(1L).name("P").departmentId(1L).build()));
        mockMvc.perform(get("/projects").param("departmentId", "1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.length()").value(1));
    }

    @Test
    void findAll_byDivisionOnly() throws Exception {
        when(projectController.findAllProjects(1L, null, null, null)).thenReturn(List.of());
        mockMvc.perform(get("/projects").param("divisionId", "1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.length()").value(0));
    }

    @Test
    void findAll_withPagination() throws Exception {
        when(projectController.findAllProjects(null, null, 10, 0)).thenReturn(List.of());
        mockMvc.perform(get("/projects").param("count", "10").param("page", "0"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.length()").value(0));
    }

    @Test
    void findById_exists_fieldAssertions() throws Exception {
        when(projectController.getProjectById(any())).thenReturn(
            ProjectVO.builder().id(1L).name("Test Project").departmentId(2L).build());
        mockMvc.perform(get("/projects/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.id").value(1))
            .andExpect(jsonPath("$.data.name").value("Test Project"))
            .andExpect(jsonPath("$.data.departmentId").value(2));
    }

    @Test
    void findById_serverError() throws Exception {
        when(projectController.getProjectById(any())).thenThrow(new ServerErrorException(-1, "err"));
        mockMvc.perform(get("/projects/1"))
            .andExpect(status().isInternalServerError());
    }

    @Test
    void create_noDepartmentId() throws Exception {
        when(projectController.createProject(any())).thenThrow(new InvalidDataException("Department ID is required"));
        mockMvc.perform(post("/projects")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Proj\"}"))
            .andExpect(status().isBadRequest());
    }

    @Test
    void create_blankName() throws Exception {
        mockMvc.perform(post("/projects")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"\",\"departmentId\":1}"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value(1));
    }

    @Test
    void create_serverError() throws Exception {
        when(projectController.createProject(any())).thenThrow(new ServerErrorException(-1, "err"));
        mockMvc.perform(post("/projects")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Proj\",\"departmentId\":1}"))
            .andExpect(status().isInternalServerError());
    }

    @Test
    void create_departmentNotFound() throws Exception {
        when(projectController.createProject(any())).thenThrow(new InvalidDataException("Department not found"));
        mockMvc.perform(post("/projects")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Proj\",\"departmentId\":99}"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value(1));
    }

    @Test
    void update_blankName() throws Exception {
        mockMvc.perform(put("/projects/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"\",\"departmentId\":1}"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value(1));
    }

    @Test
    void update_notFound() throws Exception {
        when(projectController.updateProject(any())).thenThrow(new InvalidDataException("Project not found"));
        mockMvc.perform(put("/projects/99")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Proj\",\"departmentId\":1}"))
            .andExpect(status().isBadRequest());
    }

    @Test
    void update_serverError() throws Exception {
        when(projectController.updateProject(any())).thenThrow(new ServerErrorException(-1, "err"));
        mockMvc.perform(put("/projects/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Proj\",\"departmentId\":1}"))
            .andExpect(status().isInternalServerError());
    }

    @Test
    void delete_serverError() throws Exception {
        doThrow(new ServerErrorException(-1, "err")).when(projectController).deleteProject(any());
        mockMvc.perform(delete("/projects/1"))
            .andExpect(status().isInternalServerError());
    }

    @Test
    void pages_byDepartmentOnly() throws Exception {
        when(projectController.countProjects(null, 2L)).thenReturn(12L);
        mockMvc.perform(get("/projects/pages")
                .param("departmentId", "2")
                .param("count", "10"))
            .andExpect(jsonPath("$.data.totalCount").value(12))
            .andExpect(jsonPath("$.data.totalPages").value(2));
    }

    @Test
    void pages_byDivisionOnly() throws Exception {
        when(projectController.countProjects(1L, null)).thenReturn(5L);
        mockMvc.perform(get("/projects/pages")
                .param("divisionId", "1")
                .param("count", "10"))
            .andExpect(jsonPath("$.data.totalCount").value(5))
            .andExpect(jsonPath("$.data.totalPages").value(1));
    }

    @Test
    void pages_serverError() throws Exception {
        when(projectController.countProjects(any(), any())).thenThrow(new ServerErrorException(-1, "err"));
        mockMvc.perform(get("/projects/pages"))
            .andExpect(status().isInternalServerError())
            .andExpect(jsonPath("$.code").value(-1));
    }

    @Test
    void pages_zeroRecords() throws Exception {
        when(projectController.countProjects(null, null)).thenReturn(0L);
        mockMvc.perform(get("/projects/pages").param("count", "10"))
            .andExpect(jsonPath("$.data.totalCount").value(0))
            .andExpect(jsonPath("$.data.totalPages").value(0));
    }

    @Test
    void findAll_countOnly() throws Exception {
        when(projectController.findAllProjects(null, null, 5, null)).thenReturn(List.of(
            ProjectVO.builder().id(1L).name("P").departmentId(1L).build()));
        mockMvc.perform(get("/projects").param("count", "5"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.length()").value(1));
    }

    @Test
    void findAll_pageOnly() throws Exception {
        when(projectController.findAllProjects(null, null, null, 1)).thenReturn(List.of());
        mockMvc.perform(get("/projects").param("page", "1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.length()").value(0));
    }

    @Test
    void findAll_byBothFiltersWithPagination() throws Exception {
        when(projectController.findAllProjects(1L, 2L, 10, 0)).thenReturn(List.of(
            ProjectVO.builder().id(1L).name("P").departmentId(2L).build()));
        mockMvc.perform(get("/projects")
                .param("divisionId", "1")
                .param("departmentId", "2")
                .param("count", "10")
                .param("page", "0"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.length()").value(1));
    }

    @Test
    void findAll_invalidCount() throws Exception {
        mockMvc.perform(get("/projects").param("count", "abc"))
            .andExpect(status().isBadRequest());
    }

    @Test
    void findAll_invalidPage() throws Exception {
        mockMvc.perform(get("/projects").param("page", "abc"))
            .andExpect(status().isBadRequest());
    }

    @Test
    void pages_countFive() throws Exception {
        when(projectController.countProjects(null, null)).thenReturn(12L);
        mockMvc.perform(get("/projects/pages").param("count", "5"))
            .andExpect(jsonPath("$.data.totalCount").value(12))
            .andExpect(jsonPath("$.data.totalPages").value(3));
    }

    @Test
    void pages_invalidCount() throws Exception {
        mockMvc.perform(get("/projects/pages").param("count", "abc"))
            .andExpect(status().isBadRequest());
    }

    @Test
    void pages_countZero() throws Exception {
        when(projectController.countProjects(null, null)).thenReturn(5L);
        mockMvc.perform(get("/projects/pages").param("count", "0"))
            .andExpect(jsonPath("$.data.totalPages").value(1));
    }

    @Test
    void findById_nonNumericId() throws Exception {
        mockMvc.perform(get("/projects/abc"))
            .andExpect(status().isBadRequest());
    }

    @Test
    void findById_negativeId() throws Exception {
        when(projectController.getProjectById(any())).thenThrow(new InvalidDataException("Invalid ID"));
        mockMvc.perform(get("/projects/-1"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value(1));
    }

    @Test
    void create_emptyBody() throws Exception {
        mockMvc.perform(post("/projects")
                .contentType(MediaType.APPLICATION_JSON)
                .content(""))
            .andExpect(status().isBadRequest());
    }

    @Test
    void create_nonJsonBody() throws Exception {
        mockMvc.perform(post("/projects")
                .contentType(MediaType.APPLICATION_JSON)
                .content("not-json"))
            .andExpect(status().isBadRequest());
    }

    @Test
    void create_malformedJson() throws Exception {
        mockMvc.perform(post("/projects")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{malformed"))
            .andExpect(status().isBadRequest());
    }

    @Test
    void create_extraFields() throws Exception {
        when(projectController.createProject(any())).thenReturn(
            ProjectVO.builder().id(1L).name("Proj").departmentId(1L).build());
        mockMvc.perform(post("/projects")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Proj\",\"departmentId\":1,\"extra\":\"val\"}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.id").value(1));
    }

    @Test
    void update_nullName() throws Exception {
        mockMvc.perform(put("/projects/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value(1));
    }

    @Test
    void update_emptyBody() throws Exception {
        mockMvc.perform(put("/projects/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(""))
            .andExpect(status().isBadRequest());
    }

    @Test
    void update_nonJsonBody() throws Exception {
        mockMvc.perform(put("/projects/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("not-json"))
            .andExpect(status().isBadRequest());
    }

    @Test
    void update_malformedJson() throws Exception {
        mockMvc.perform(put("/projects/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{broken"))
            .andExpect(status().isBadRequest());
    }

    @Test
    void update_invalidPathId() throws Exception {
        mockMvc.perform(put("/projects/abc")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Proj\",\"departmentId\":1}"))
            .andExpect(status().isBadRequest());
    }

    @Test
    void delete_invalidPathId() throws Exception {
        mockMvc.perform(delete("/projects/abc"))
            .andExpect(status().isBadRequest());
    }

    @Test
    void delete_emptyIdPath() throws Exception {
        mockMvc.perform(delete("/projects/"))
            .andExpect(status().isNotFound());
    }
}
