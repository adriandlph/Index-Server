package com.adlph.internal.managment.index.server.api.rest;

import com.adlph.internal.managment.index.server.api.rest.data.ApiResponse;
import com.adlph.internal.managment.index.server.api.rest.data.CreateProductRequest;
import com.adlph.internal.managment.index.server.api.rest.data.PageCountResponse;
import com.adlph.internal.managment.index.server.api.rest.data.ProductResponse;
import com.adlph.internal.managment.index.server.api.rest.data.UpdateProductRequest;
import com.adlph.internal.managment.index.server.controller.ProductControllerInterface;
import com.adlph.internal.managment.index.server.data.vo.ProductVO;
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
@RequestMapping("/products")
public class ProductRestApi {

    private static final Logger LOG = LoggerFactory.getLogger(ProductRestApi.class);

    @Autowired
    private ProductControllerInterface productController;

    @GetMapping
    public ResponseEntity<ApiResponse<List<ProductResponse>>> findAllProducts(
            @RequestParam(required = false) Long divisionId,
            @RequestParam(required = false) Long departmentId,
            @RequestParam(required = false) Long projectId,
            @RequestParam(required = false) Integer count,
            @RequestParam(required = false) Integer page) {
        LOG.trace("---> findAllProducts()");
        try {
            List<ProductResponse> products = productController.findAllProducts(divisionId, departmentId, projectId, count, page).stream()
                .map(ProductRestApi::toResponse).toList();
            LOG.trace("<--- findAllProducts()");
            return ResponseEntity.ok(ApiResponse.ok(products));
        } catch (ServerErrorException ex) {
            LOG.trace("<--- findAllProducts()");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(-1, "Server error"));
        }
    }

    @GetMapping("/pages")
    public ResponseEntity<ApiResponse<PageCountResponse>> getProductPages(
            @RequestParam(required = false) Long divisionId,
            @RequestParam(required = false) Long departmentId,
            @RequestParam(required = false) Long projectId,
            @RequestParam(required = false) Integer count) {
        LOG.trace("---> getProductPages()");
        try {
            long totalCount = productController.countProducts(divisionId, departmentId, projectId);
            int totalPages = count != null && count > 0
                ? (int) Math.ceil((double) totalCount / count)
                : (totalCount > 0 ? 1 : 0);
            LOG.trace("<--- getProductPages()");
            return ResponseEntity.ok(ApiResponse.ok(
                PageCountResponse.builder().totalCount(totalCount).totalPages(totalPages).build()));
        } catch (ServerErrorException ex) {
            LOG.trace("<--- getProductPages()");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(-1, "Server error"));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> findProductById(@PathVariable Long id) {
        LOG.trace("---> findProductById()");
        try {
            ProductVO vo = productController.getProductById(ProductVO.builder().id(id).build());
            if (vo == null) {
                LOG.trace("<--- findProductById()");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(1, "Product does not exists"));
            }
            LOG.trace("<--- findProductById()");
            return ResponseEntity.ok(ApiResponse.ok(toResponse(vo)));
        } catch (ServerErrorException ex) {
            LOG.trace("<--- findProductById()");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(-1, "Server error"));
        } catch (InvalidDataException ex) {
            LOG.trace("<--- findProductById()");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(1, ex.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ProductResponse>> createProduct(@RequestBody CreateProductRequest request) {
        LOG.trace("---> createProduct()");
        try {
            if (request.getName() == null || request.getName().isBlank()) {
                LOG.trace("<--- createProduct()");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(1, "Product name is required"));
            }
            ProductVO vo = productController.createProduct(
                ProductVO.builder().name(request.getName()).version(request.getVersion())
                    .publishDate(request.getPublishDate()).description(request.getDescription())
                    .projectId(request.getProjectId()).build());
            LOG.trace("<--- createProduct()");
            return ResponseEntity.ok(ApiResponse.ok(toResponse(vo)));
        } catch (ServerErrorException ex) {
            LOG.trace("<--- createProduct()");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(-1, "Server error"));
        } catch (InvalidDataException ex) {
            LOG.trace("<--- createProduct()");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(1, ex.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> updateProduct(
            @PathVariable Long id, @RequestBody UpdateProductRequest request) {
        LOG.trace("---> updateProduct()");
        try {
            if (request.getName() == null || request.getName().isBlank()) {
                LOG.trace("<--- updateProduct()");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(1, "Product name is required"));
            }
            ProductVO vo = productController.updateProduct(
                ProductVO.builder().id(id).name(request.getName()).version(request.getVersion())
                    .publishDate(request.getPublishDate()).description(request.getDescription())
                    .projectId(request.getProjectId()).build());
            LOG.trace("<--- updateProduct()");
            return ResponseEntity.ok(ApiResponse.ok(toResponse(vo)));
        } catch (ServerErrorException ex) {
            LOG.trace("<--- updateProduct()");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(-1, "Server error"));
        } catch (InvalidDataException ex) {
            LOG.trace("<--- updateProduct()");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(1, ex.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(@PathVariable Long id) {
        LOG.trace("---> deleteProduct()");
        try {
            productController.deleteProduct(ProductVO.builder().id(id).build());
            LOG.trace("<--- deleteProduct()");
            return ResponseEntity.ok(ApiResponse.ok(null));
        } catch (ServerErrorException ex) {
            LOG.trace("<--- deleteProduct()");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(-1, "Server error"));
        } catch (InvalidDataException ex) {
            LOG.trace("<--- deleteProduct()");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(1, ex.getMessage()));
        }
    }

    private static ProductResponse toResponse(ProductVO vo) {
        return ProductResponse.builder()
            .id(vo.getId()).name(vo.getName()).version(vo.getVersion())
            .publishDate(vo.getPublishDate()).description(vo.getDescription())
            .projectId(vo.getProjectId())
            .build();
    }
}
