package com.adlph.internal.managment.index.server.rest;

import com.adlph.internal.managment.index.server.api.rest.DivisionRestApi;
import com.adlph.internal.managment.index.server.controller.DivisionControllerInterface;
import com.adlph.internal.managment.index.server.data.vo.DivisionVO;
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
class DivisionRestApiTest {

    private MockMvc mockMvc;

    @Mock
    private DivisionControllerInterface divisionController;

    @BeforeEach
    void setUp() throws Exception {
        var restApi = new DivisionRestApi();
        var field = DivisionRestApi.class.getDeclaredField("divisionController");
        field.setAccessible(true);
        field.set(restApi, divisionController);
        mockMvc = MockMvcBuilders.standaloneSetup(restApi).build();
    }

    @Test
    void findAll_noParams() throws Exception {
        when(divisionController.findAllDivisions(null, null)).thenReturn(List.of(
            DivisionVO.builder().id(1L).name("D1").build(),
            DivisionVO.builder().id(2L).name("D2").build()));
        mockMvc.perform(get("/divisions"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(0))
            .andExpect(jsonPath("$.data.length()").value(2));
    }

    @Test
    void findAll_withPagination() throws Exception {
        when(divisionController.findAllDivisions(10, 0)).thenReturn(List.of());
        mockMvc.perform(get("/divisions").param("count", "10").param("page", "0"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.length()").value(0));
    }

    @Test
    void findAll_serverError() throws Exception {
        when(divisionController.findAllDivisions(any(), any())).thenThrow(new ServerErrorException(-1, "err"));
        mockMvc.perform(get("/divisions"))
            .andExpect(status().isInternalServerError())
            .andExpect(jsonPath("$.code").value(-1));
    }

    @Test
    void pages_withCount() throws Exception {
        when(divisionController.countDivisions()).thenReturn(25L);
        mockMvc.perform(get("/divisions/pages").param("count", "10"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.totalCount").value(25))
            .andExpect(jsonPath("$.data.totalPages").value(3));
    }

    @Test
    void pages_exactDivision() throws Exception {
        when(divisionController.countDivisions()).thenReturn(20L);
        mockMvc.perform(get("/divisions/pages").param("count", "10"))
            .andExpect(jsonPath("$.data.totalPages").value(2));
    }

    @Test
    void pages_noCount() throws Exception {
        when(divisionController.countDivisions()).thenReturn(5L);
        mockMvc.perform(get("/divisions/pages"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.totalPages").value(1));
    }

    @Test
    void pages_zeroRecords() throws Exception {
        when(divisionController.countDivisions()).thenReturn(0L);
        mockMvc.perform(get("/divisions/pages").param("count", "10"))
            .andExpect(jsonPath("$.data.totalCount").value(0))
            .andExpect(jsonPath("$.data.totalPages").value(0));
    }

    @Test
    void pages_serverError() throws Exception {
        when(divisionController.countDivisions()).thenThrow(new ServerErrorException(-1, "err"));
        mockMvc.perform(get("/divisions/pages"))
            .andExpect(status().isInternalServerError())
            .andExpect(jsonPath("$.code").value(-1));
    }

    @Test
    void findById_exists() throws Exception {
        when(divisionController.getDivisionById(any())).thenReturn(DivisionVO.builder().id(1L).name("Div").build());
        mockMvc.perform(get("/divisions/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.id").value(1));
    }

    @Test
    void findById_notExists() throws Exception {
        when(divisionController.getDivisionById(any())).thenThrow(new InvalidDataException("Division does not exists"));
        mockMvc.perform(get("/divisions/99"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value(1));
    }

    @Test
    void findById_serverError() throws Exception {
        when(divisionController.getDivisionById(any())).thenThrow(new ServerErrorException(-1, "err"));
        mockMvc.perform(get("/divisions/1"))
            .andExpect(status().isInternalServerError());
    }

    @Test
    void create_valid() throws Exception {
        when(divisionController.createDivision(any())).thenReturn(DivisionVO.builder().id(1L).name("New Div").build());
        mockMvc.perform(post("/divisions")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"New Div\"}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.id").value(1));
    }

    @Test
    void create_blankName() throws Exception {
        mockMvc.perform(post("/divisions")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"\"}"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value(1));
    }

    @Test
    void create_nullName() throws Exception {
        mockMvc.perform(post("/divisions")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value(1));
    }

    @Test
    void create_serverError() throws Exception {
        when(divisionController.createDivision(any())).thenThrow(new ServerErrorException(-1, "err"));
        mockMvc.perform(post("/divisions")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Valid\"}"))
            .andExpect(status().isInternalServerError());
    }

    @Test
    void update_valid() throws Exception {
        when(divisionController.updateDivision(any())).thenReturn(DivisionVO.builder().id(1L).name("Updated").build());
        mockMvc.perform(put("/divisions/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Updated\"}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.name").value("Updated"));
    }

    @Test
    void update_blankName() throws Exception {
        mockMvc.perform(put("/divisions/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"\"}"))
            .andExpect(status().isBadRequest());
    }

    @Test
    void delete_valid() throws Exception {
        doNothing().when(divisionController).deleteDivision(any());
        mockMvc.perform(delete("/divisions/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(0));
    }

    @Test
    void delete_notExists() throws Exception {
        doThrow(new InvalidDataException("Division does not exists")).when(divisionController).deleteDivision(any());
        mockMvc.perform(delete("/divisions/99"))
            .andExpect(status().isBadRequest());
    }

    @Test
    void findById_negativeId() throws Exception {
        when(divisionController.getDivisionById(any())).thenThrow(new InvalidDataException("Invalid ID"));
        mockMvc.perform(get("/divisions/-1"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value(1));
    }

    @Test
    void create_invalidData() throws Exception {
        when(divisionController.createDivision(any())).thenThrow(new InvalidDataException("Division name already exists"));
        mockMvc.perform(post("/divisions")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Existing\"}"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value(1));
    }

    @Test
    void update_notFound() throws Exception {
        when(divisionController.updateDivision(any())).thenThrow(new InvalidDataException("Division does not exists"));
        mockMvc.perform(put("/divisions/99")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Updated\"}"))
            .andExpect(status().isBadRequest());
    }

    @Test
    void update_serverError() throws Exception {
        when(divisionController.updateDivision(any())).thenThrow(new ServerErrorException(-1, "err"));
        mockMvc.perform(put("/divisions/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Updated\"}"))
            .andExpect(status().isInternalServerError());
    }

    @Test
    void delete_serverError() throws Exception {
        doThrow(new ServerErrorException(-1, "err")).when(divisionController).deleteDivision(any());
        mockMvc.perform(delete("/divisions/1"))
            .andExpect(status().isInternalServerError());
    }

    @Test
    void findAll_countOnly() throws Exception {
        when(divisionController.findAllDivisions(5, null)).thenReturn(List.of(
            DivisionVO.builder().id(1L).name("D1").build()));
        mockMvc.perform(get("/divisions").param("count", "5"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.length()").value(1));
    }

    @Test
    void findAll_pageOnly() throws Exception {
        when(divisionController.findAllDivisions(null, 1)).thenReturn(List.of());
        mockMvc.perform(get("/divisions").param("page", "1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.length()").value(0));
    }

    @Test
    void findAll_emptyResult() throws Exception {
        when(divisionController.findAllDivisions(any(), any())).thenReturn(List.of());
        mockMvc.perform(get("/divisions"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.length()").value(0));
    }

    @Test
    void findAll_invalidCount() throws Exception {
        mockMvc.perform(get("/divisions").param("count", "abc"))
            .andExpect(status().isBadRequest());
    }

    @Test
    void findAll_invalidPage() throws Exception {
        mockMvc.perform(get("/divisions").param("page", "abc"))
            .andExpect(status().isBadRequest());
    }

    @Test
    void findAll_invalidParamsBoth() throws Exception {
        mockMvc.perform(get("/divisions").param("count", "x").param("page", "y"))
            .andExpect(status().isBadRequest());
    }

    @Test
    void pages_countFive() throws Exception {
        when(divisionController.countDivisions()).thenReturn(13L);
        mockMvc.perform(get("/divisions/pages").param("count", "5"))
            .andExpect(jsonPath("$.data.totalCount").value(13))
            .andExpect(jsonPath("$.data.totalPages").value(3));
    }

    @Test
    void pages_countOne() throws Exception {
        when(divisionController.countDivisions()).thenReturn(3L);
        mockMvc.perform(get("/divisions/pages").param("count", "1"))
            .andExpect(jsonPath("$.data.totalPages").value(3));
    }

    @Test
    void pages_countTwenty() throws Exception {
        when(divisionController.countDivisions()).thenReturn(40L);
        mockMvc.perform(get("/divisions/pages").param("count", "20"))
            .andExpect(jsonPath("$.data.totalPages").value(2));
    }

    @Test
    void pages_countZero() throws Exception {
        when(divisionController.countDivisions()).thenReturn(5L);
        mockMvc.perform(get("/divisions/pages").param("count", "0"))
            .andExpect(jsonPath("$.data.totalPages").value(1));
    }

    @Test
    void pages_countLarge() throws Exception {
        when(divisionController.countDivisions()).thenReturn(5L);
        mockMvc.perform(get("/divisions/pages").param("count", "100"))
            .andExpect(jsonPath("$.data.totalPages").value(1));
    }

    @Test
    void pages_invalidCount() throws Exception {
        mockMvc.perform(get("/divisions/pages").param("count", "abc"))
            .andExpect(status().isBadRequest());
    }

    @Test
    void findById_nonNumericId() throws Exception {
        mockMvc.perform(get("/divisions/abc"))
            .andExpect(status().isBadRequest());
    }

    @Test
    void findById_fieldsCheck() throws Exception {
        when(divisionController.getDivisionById(any())).thenReturn(
            DivisionVO.builder().id(5L).name("MyDivision").build());
        mockMvc.perform(get("/divisions/5"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.id").value(5))
            .andExpect(jsonPath("$.data.name").value("MyDivision"));
    }

    @Test
    void findById_emptyIdPath() throws Exception {
        mockMvc.perform(get("/divisions/"))
            .andExpect(status().isNotFound());
    }

    @Test
    void create_emptyBody() throws Exception {
        mockMvc.perform(post("/divisions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(""))
            .andExpect(status().isBadRequest());
    }

    @Test
    void create_nonJsonBody() throws Exception {
        mockMvc.perform(post("/divisions")
                .contentType(MediaType.APPLICATION_JSON)
                .content("not-json"))
            .andExpect(status().isBadRequest());
    }

    @Test
    void create_malformedJson() throws Exception {
        mockMvc.perform(post("/divisions")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{malformed"))
            .andExpect(status().isBadRequest());
    }

    @Test
    void create_extraFields() throws Exception {
        when(divisionController.createDivision(any())).thenReturn(
            DivisionVO.builder().id(1L).name("Div").build());
        mockMvc.perform(post("/divisions")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Div\",\"unknown\":\"val\"}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.id").value(1));
    }

    @Test
    void update_nullName() throws Exception {
        mockMvc.perform(put("/divisions/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value(1));
    }

    @Test
    void update_emptyBody() throws Exception {
        mockMvc.perform(put("/divisions/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(""))
            .andExpect(status().isBadRequest());
    }

    @Test
    void update_nonJsonBody() throws Exception {
        mockMvc.perform(put("/divisions/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("not-json"))
            .andExpect(status().isBadRequest());
    }

    @Test
    void update_malformedJson() throws Exception {
        mockMvc.perform(put("/divisions/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{broken"))
            .andExpect(status().isBadRequest());
    }

    @Test
    void update_invalidPathId() throws Exception {
        mockMvc.perform(put("/divisions/abc")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Upd\"}"))
            .andExpect(status().isBadRequest());
    }

    @Test
    void delete_invalidPathId() throws Exception {
        mockMvc.perform(delete("/divisions/abc"))
            .andExpect(status().isBadRequest());
    }

    @Test
    void delete_emptyIdPath() throws Exception {
        mockMvc.perform(delete("/divisions/"))
            .andExpect(status().isNotFound());
    }
}
