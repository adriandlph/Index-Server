package com.adlph.internal.managment.index.server.api.rest;

import com.adlph.internal.managment.index.server.api.rest.data.ApiResponse;
import com.adlph.internal.managment.index.server.api.rest.data.CreateProjectRequest;
import com.adlph.internal.managment.index.server.api.rest.data.PageCountResponse;
import com.adlph.internal.managment.index.server.api.rest.data.ProjectResponse;
import com.adlph.internal.managment.index.server.api.rest.data.UpdateProjectRequest;
import com.adlph.internal.managment.index.server.controller.ProjectControllerInterface;
import com.adlph.internal.managment.index.server.data.vo.ProjectVO;
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
@RequestMapping("/projects")
public class ProjectRestApi {

    private static final Logger LOG = LoggerFactory.getLogger(ProjectRestApi.class);

    @Autowired
    private ProjectControllerInterface projectController;

    @GetMapping
    public ResponseEntity<ApiResponse<List<ProjectResponse>>> findAllProjects(
            @RequestParam(required = false) Long divisionId,
            @RequestParam(required = false) Long departmentId,
            @RequestParam(required = false) Integer count,
            @RequestParam(required = false) Integer page) {
        LOG.trace("---> findAllProjects()");
        try {
            List<ProjectResponse> projects = projectController.findAllProjects(divisionId, departmentId, count, page).stream()
                .map(ProjectRestApi::toResponse).toList();
            LOG.trace("<--- findAllProjects()");
            return ResponseEntity.ok(ApiResponse.ok(projects));
        } catch (ServerErrorException ex) {
            LOG.trace("<--- findAllProjects()");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(-1, "Server error"));
        }
    }

    @GetMapping("/pages")
    public ResponseEntity<ApiResponse<PageCountResponse>> getProjectPages(
            @RequestParam(required = false) Long divisionId,
            @RequestParam(required = false) Long departmentId,
            @RequestParam(required = false) Integer count) {
        LOG.trace("---> getProjectPages()");
        try {
            long totalCount = projectController.countProjects(divisionId, departmentId);
            int totalPages = count != null && count > 0
                ? (int) Math.ceil((double) totalCount / count)
                : (totalCount > 0 ? 1 : 0);
            LOG.trace("<--- getProjectPages()");
            return ResponseEntity.ok(ApiResponse.ok(
                PageCountResponse.builder().totalCount(totalCount).totalPages(totalPages).build()));
        } catch (ServerErrorException ex) {
            LOG.trace("<--- getProjectPages()");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(-1, "Server error"));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProjectResponse>> findProjectById(@PathVariable Long id) {
        LOG.trace("---> findProjectById()");
        try {
            ProjectVO vo = projectController.getProjectById(ProjectVO.builder().id(id).build());
            if (vo == null) {
                LOG.trace("<--- findProjectById()");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(1, "Project does not exists"));
            }
            LOG.trace("<--- findProjectById()");
            return ResponseEntity.ok(ApiResponse.ok(toResponse(vo)));
        } catch (ServerErrorException ex) {
            LOG.trace("<--- findProjectById()");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(-1, "Server error"));
        } catch (InvalidDataException ex) {
            LOG.trace("<--- findProjectById()");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(1, ex.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ProjectResponse>> createProject(@RequestBody CreateProjectRequest request) {
        LOG.trace("---> createProject()");
        try {
            if (request.getName() == null || request.getName().isBlank()) {
                LOG.trace("<--- createProject()");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(1, "Project name is required"));
            }
            ProjectVO vo = projectController.createProject(
                ProjectVO.builder().name(request.getName()).departmentId(request.getDepartmentId()).build());
            LOG.trace("<--- createProject()");
            return ResponseEntity.ok(ApiResponse.ok(toResponse(vo)));
        } catch (ServerErrorException ex) {
            LOG.trace("<--- createProject()");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(-1, "Server error"));
        } catch (InvalidDataException ex) {
            LOG.trace("<--- createProject()");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(1, ex.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProjectResponse>> updateProject(
            @PathVariable Long id, @RequestBody UpdateProjectRequest request) {
        LOG.trace("---> updateProject()");
        try {
            if (request.getName() == null || request.getName().isBlank()) {
                LOG.trace("<--- updateProject()");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(1, "Project name is required"));
            }
            ProjectVO vo = projectController.updateProject(
                ProjectVO.builder().id(id).name(request.getName()).departmentId(request.getDepartmentId()).build());
            LOG.trace("<--- updateProject()");
            return ResponseEntity.ok(ApiResponse.ok(toResponse(vo)));
        } catch (ServerErrorException ex) {
            LOG.trace("<--- updateProject()");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(-1, "Server error"));
        } catch (InvalidDataException ex) {
            LOG.trace("<--- updateProject()");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(1, ex.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteProject(@PathVariable Long id) {
        LOG.trace("---> deleteProject()");
        try {
            projectController.deleteProject(ProjectVO.builder().id(id).build());
            LOG.trace("<--- deleteProject()");
            return ResponseEntity.ok(ApiResponse.ok(null));
        } catch (ServerErrorException ex) {
            LOG.trace("<--- deleteProject()");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(-1, "Server error"));
        } catch (InvalidDataException ex) {
            LOG.trace("<--- deleteProject()");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(1, ex.getMessage()));
        }
    }

    private static ProjectResponse toResponse(ProjectVO vo) {
        return ProjectResponse.builder()
            .id(vo.getId()).name(vo.getName()).departmentId(vo.getDepartmentId())
            .build();
    }
}
