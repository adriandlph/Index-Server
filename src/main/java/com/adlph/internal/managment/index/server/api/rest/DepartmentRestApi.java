package com.adlph.internal.managment.index.server.api.rest;

import com.adlph.internal.managment.index.server.api.rest.data.ApiResponse;
import com.adlph.internal.managment.index.server.api.rest.data.CreateDepartmentRequest;
import com.adlph.internal.managment.index.server.api.rest.data.DepartmentResponse;
import com.adlph.internal.managment.index.server.api.rest.data.PageCountResponse;
import com.adlph.internal.managment.index.server.api.rest.data.UpdateDepartmentRequest;
import com.adlph.internal.managment.index.server.controller.DepartmentControllerInterface;
import com.adlph.internal.managment.index.server.data.vo.DepartmentVO;
import com.adlph.internal.managment.index.server.exception.InvalidDataException;
import com.adlph.internal.managment.index.server.exception.ServerErrorException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/departments")
public class DepartmentRestApi {

    private static final Logger LOG = LoggerFactory.getLogger(DepartmentRestApi.class);

    @Autowired
    private DepartmentControllerInterface departmentController;

    @GetMapping
    public ResponseEntity<ApiResponse<List<DepartmentResponse>>> findAllDepartments(
            @RequestParam(required = false) Long divisionId,
            @RequestParam(required = false) Integer count,
            @RequestParam(required = false) Integer page) {
        LOG.trace("---> findAllDepartments()");
        try {
            List<DepartmentResponse> departments = departmentController.findAllDepartments(divisionId, count, page).stream()
                .map(DepartmentRestApi::toResponse).toList();
            LOG.trace("<--- findAllDepartments()");
            return ResponseEntity.ok(ApiResponse.ok(departments));
        } catch (ServerErrorException ex) {
            LOG.trace("<--- findAllDepartments()");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(-1, "Server error"));
        }
    }

    @GetMapping("/pages")
    public ResponseEntity<ApiResponse<PageCountResponse>> getDepartmentPages(
            @RequestParam(required = false) Long divisionId,
            @RequestParam(required = false) Integer count) {
        LOG.trace("---> getDepartmentPages()");
        try {
            long totalCount = departmentController.countDepartments(divisionId);
            int totalPages = count != null && count > 0
                ? (int) Math.ceil((double) totalCount / count)
                : (totalCount > 0 ? 1 : 0);
            LOG.trace("<--- getDepartmentPages()");
            return ResponseEntity.ok(ApiResponse.ok(
                PageCountResponse.builder().totalCount(totalCount).totalPages(totalPages).build()));
        } catch (ServerErrorException ex) {
            LOG.trace("<--- getDepartmentPages()");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(-1, "Server error"));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<DepartmentResponse>> findDepartmentById(@PathVariable Long id) {
        LOG.trace("---> findDepartmentById()");
        try {
            DepartmentVO vo = departmentController.getDepartmentById(DepartmentVO.builder().id(id).build());
            if (vo == null) {
                LOG.trace("<--- findDepartmentById()");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(1, "Department does not exists"));
            }
            LOG.trace("<--- findDepartmentById()");
            return ResponseEntity.ok(ApiResponse.ok(toResponse(vo)));
        } catch (ServerErrorException ex) {
            LOG.trace("<--- findDepartmentById()");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(-1, "Server error"));
        } catch (InvalidDataException ex) {
            LOG.trace("<--- findDepartmentById()");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(1, ex.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<ApiResponse<DepartmentResponse>> createDepartment(@RequestBody CreateDepartmentRequest request) {
        LOG.trace("---> createDepartment()");
        try {
            if (request.getName() == null || request.getName().isBlank()) {
                LOG.trace("<--- createDepartment()");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(1, "Department name is required"));
            }
            DepartmentVO vo = departmentController.createDepartment(
                DepartmentVO.builder().name(request.getName()).divisionId(request.getDivisionId()).build());
            LOG.trace("<--- createDepartment()");
            return ResponseEntity.ok(ApiResponse.ok(toResponse(vo)));
        } catch (ServerErrorException ex) {
            LOG.trace("<--- createDepartment()");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(-1, "Server error"));
        } catch (InvalidDataException ex) {
            LOG.trace("<--- createDepartment()");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(1, ex.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<DepartmentResponse>> updateDepartment(
            @PathVariable Long id, @RequestBody UpdateDepartmentRequest request) {
        LOG.trace("---> updateDepartment()");
        try {
            if (request.getName() == null || request.getName().isBlank()) {
                LOG.trace("<--- updateDepartment()");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(1, "Department name is required"));
            }
            DepartmentVO vo = departmentController.updateDepartment(
                DepartmentVO.builder().id(id).name(request.getName()).divisionId(request.getDivisionId()).build());
            LOG.trace("<--- updateDepartment()");
            return ResponseEntity.ok(ApiResponse.ok(toResponse(vo)));
        } catch (ServerErrorException ex) {
            LOG.trace("<--- updateDepartment()");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(-1, "Server error"));
        } catch (InvalidDataException ex) {
            LOG.trace("<--- updateDepartment()");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(1, ex.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteDepartment(@PathVariable Long id) {
        LOG.trace("---> deleteDepartment()");
        try {
            departmentController.deleteDepartment(DepartmentVO.builder().id(id).build());
            LOG.trace("<--- deleteDepartment()");
            return ResponseEntity.ok(ApiResponse.ok(null));
        } catch (ServerErrorException ex) {
            LOG.trace("<--- deleteDepartment()");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(-1, "Server error"));
        } catch (InvalidDataException ex) {
            LOG.trace("<--- deleteDepartment()");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(1, ex.getMessage()));
        }
    }

    private static DepartmentResponse toResponse(DepartmentVO vo) {
        return DepartmentResponse.builder()
            .id(vo.getId()).name(vo.getName()).divisionId(vo.getDivisionId())
            .build();
    }
}
