package com.adlph.internal.managment.index.server.api.rest;

import com.adlph.internal.managment.index.server.api.rest.data.ApiResponse;
import com.adlph.internal.managment.index.server.api.rest.data.CreateDivisionRequest;
import com.adlph.internal.managment.index.server.api.rest.data.DivisionResponse;
import com.adlph.internal.managment.index.server.api.rest.data.UpdateDivisionRequest;
import com.adlph.internal.managment.index.server.controller.DivisionControllerInterface;
import com.adlph.internal.managment.index.server.data.vo.DivisionVO;
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
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/divisions")
public class DivisionRestApi {

    private static final Logger LOG = LoggerFactory.getLogger(DivisionRestApi.class);

    @Autowired
    private DivisionControllerInterface divisionController;

    @GetMapping
    public ResponseEntity<ApiResponse<List<DivisionResponse>>> findAllDivisions() {
        LOG.trace("---> findAllDivisions()");
        try {
            List<DivisionResponse> divisions = divisionController.findAllDivisions().stream()
                .map(DivisionRestApi::toResponse).toList();
            LOG.trace("<--- findAllDivisions()");
            return ResponseEntity.ok(ApiResponse.ok(divisions));
        } catch (ServerErrorException ex) {
            LOG.trace("<--- findAllDivisions()");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(-1, "Server error"));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<DivisionResponse>> findDivisionById(@PathVariable Long id) {
        LOG.trace("---> findDivisionById()");
        try {
            DivisionVO vo = divisionController.getDivisionById(DivisionVO.builder().id(id).build());
            if (vo == null) {
                LOG.trace("<--- findDivisionById()");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(1, "Division does not exists"));
            }
            LOG.trace("<--- findDivisionById()");
            return ResponseEntity.ok(ApiResponse.ok(toResponse(vo)));
        } catch (ServerErrorException ex) {
            LOG.trace("<--- findDivisionById()");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(-1, "Server error"));
        } catch (InvalidDataException ex) {
            LOG.trace("<--- findDivisionById()");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(1, ex.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<ApiResponse<DivisionResponse>> createDivision(@RequestBody CreateDivisionRequest request) {
        LOG.trace("---> createDivision()");
        try {
            if (request.getName() == null || request.getName().isBlank()) {
                LOG.trace("<--- createDivision()");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(1, "Division name is required"));
            }
            DivisionVO vo = divisionController.createDivision(
                DivisionVO.builder().name(request.getName()).build());
            LOG.trace("<--- createDivision()");
            return ResponseEntity.ok(ApiResponse.ok(toResponse(vo)));
        } catch (ServerErrorException ex) {
            LOG.trace("<--- createDivision()");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(-1, "Server error"));
        } catch (InvalidDataException ex) {
            LOG.trace("<--- createDivision()");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(1, ex.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<DivisionResponse>> updateDivision(
            @PathVariable Long id, @RequestBody UpdateDivisionRequest request) {
        LOG.trace("---> updateDivision()");
        try {
            if (request.getName() == null || request.getName().isBlank()) {
                LOG.trace("<--- updateDivision()");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(1, "Division name is required"));
            }
            DivisionVO vo = divisionController.updateDivision(
                DivisionVO.builder().id(id).name(request.getName()).build());
            LOG.trace("<--- updateDivision()");
            return ResponseEntity.ok(ApiResponse.ok(toResponse(vo)));
        } catch (ServerErrorException ex) {
            LOG.trace("<--- updateDivision()");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(-1, "Server error"));
        } catch (InvalidDataException ex) {
            LOG.trace("<--- updateDivision()");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(1, ex.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteDivision(@PathVariable Long id) {
        LOG.trace("---> deleteDivision()");
        try {
            divisionController.deleteDivision(DivisionVO.builder().id(id).build());
            LOG.trace("<--- deleteDivision()");
            return ResponseEntity.ok(ApiResponse.ok(null));
        } catch (ServerErrorException ex) {
            LOG.trace("<--- deleteDivision()");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(-1, "Server error"));
        } catch (InvalidDataException ex) {
            LOG.trace("<--- deleteDivision()");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(1, ex.getMessage()));
        }
    }

    private static DivisionResponse toResponse(DivisionVO vo) {
        return DivisionResponse.builder()
            .id(vo.getId()).name(vo.getName())
            .build();
    }
}
