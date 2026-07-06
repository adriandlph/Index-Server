package com.adlph.internal.managment.index.server.controller;

import com.adlph.internal.managment.index.server.data.entity.Department;
import com.adlph.internal.managment.index.server.data.entity.Division;
import com.adlph.internal.managment.index.server.data.entity.Product;
import com.adlph.internal.managment.index.server.data.entity.Project;
import com.adlph.internal.managment.index.server.data.vo.ProductVO;
import com.adlph.internal.managment.index.server.exception.InvalidDataException;
import com.adlph.internal.managment.index.server.exception.ServerErrorException;
import com.adlph.internal.managment.index.server.repository.ProductRepository;
import com.adlph.internal.managment.index.server.repository.ProjectRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductControllerTest {

    @Mock private ProductRepository productRepository;
    @Mock private ProjectRepository projectRepository;
    @InjectMocks private ProductController controller;

    private Division division;
    private Department department;
    private Project project;
    private Product product;

    @BeforeEach
    void setUp() {
        division = Division.builder().id(1L).name("Div").build();
        department = Department.builder().id(1L).name("Dept").division(division).build();
        project = Project.builder().id(1L).name("Proj").department(department).build();
        product = Product.builder().id(1L).name("Prod").version("v01.02.003")
            .publishDate(LocalDateTime.of(2025, 1, 1, 0, 0))
            .description("Desc").project(project).build();
    }

    @Nested
    class FindAll {
        @Test
        void noFilters() throws Exception {
            when(productRepository.findAll(null, null, null, null, null)).thenReturn(List.of(product));
            var r = controller.findAllProducts(null, null, null, null, null);
            assertEquals(1, r.size());
        }

        @Test
        void filterByProject() throws Exception {
            when(productRepository.findAll(null, null, 1L, null, null)).thenReturn(List.of(product));
            var r = controller.findAllProducts(null, null, 1L, null, null);
            assertEquals(1, r.size());
        }

        @Test
        void filterByDepartment() throws Exception {
            when(productRepository.findAll(null, 1L, null, null, null)).thenReturn(List.of(product));
            var r = controller.findAllProducts(null, 1L, null, null, null);
            assertEquals(1, r.size());
        }

        @Test
        void filterByDivision() throws Exception {
            when(productRepository.findAll(1L, null, null, null, null)).thenReturn(List.of(product));
            var r = controller.findAllProducts(1L, null, null, null, null);
            assertEquals(1, r.size());
        }

        @Test
        void filterByProjectWithPagination() throws Exception {
            when(productRepository.findAll(null, null, 1L, 10, 0)).thenReturn(List.of(product));
            var r = controller.findAllProducts(null, null, 1L, 10, 0);
            assertEquals(1, r.size());
        }

        @Test
        void filterByAllThree() throws Exception {
            when(productRepository.findAll(1L, 1L, 1L, null, null)).thenReturn(List.of(product));
            var r = controller.findAllProducts(1L, 1L, 1L, null, null);
            assertEquals(1, r.size());
        }

        @Test
        void negativeCount() throws Exception {
            when(productRepository.findAll(null, null, null, -1, 0)).thenReturn(List.of());
            var r = controller.findAllProducts(null, null, null, -1, 0);
            assertTrue(r.isEmpty());
        }

        @Test
        void nullPage() throws Exception {
            when(productRepository.findAll(null, null, null, 10, null)).thenReturn(List.of(product));
            var r = controller.findAllProducts(null, null, null, 10, null);
            assertEquals(1, r.size());
        }

        @Test
        void empty() throws Exception {
            when(productRepository.findAll(any(), any(), any(), any(), any())).thenReturn(List.of());
            var r = controller.findAllProducts(99L, null, null, null, null);
            assertTrue(r.isEmpty());
        }

        @Test
        void repositoryThrows() {
            when(productRepository.findAll(any(), any(), any(), any(), any())).thenThrow(RuntimeException.class);
            assertThrows(ServerErrorException.class,
                () -> controller.findAllProducts(1L, 1L, 1L, 10, 0));
        }

        @Test
        void multipleResults() throws Exception {
            var p2 = Product.builder().id(2L).name("Second").version("v02.00.000")
                .project(project).build();
            when(productRepository.findAll(null, null, null, 10, 0)).thenReturn(List.of(product, p2));
            var r = controller.findAllProducts(null, null, null, 10, 0);
            assertEquals(2, r.size());
        }

        @Test
        void zeroCount() throws Exception {
            when(productRepository.findAll(null, null, null, 0, 0)).thenReturn(List.of());
            var r = controller.findAllProducts(null, null, null, 0, 0);
            assertTrue(r.isEmpty());
        }

        @Test
        void negativePage() throws Exception {
            when(productRepository.findAll(null, null, null, 10, -1)).thenReturn(List.of());
            var r = controller.findAllProducts(null, null, null, 10, -1);
            assertTrue(r.isEmpty());
        }

        @Test
        void pageWithoutCount() throws Exception {
            when(productRepository.findAll(null, null, null, null, 2)).thenReturn(List.of(product));
            var r = controller.findAllProducts(null, null, null, null, 2);
            assertEquals(1, r.size());
        }

        @Test
        void filterByProjectEmpty() throws Exception {
            when(productRepository.findAll(null, null, 99L, null, null)).thenReturn(List.of());
            var r = controller.findAllProducts(null, null, 99L, null, null);
            assertTrue(r.isEmpty());
        }
    }

    @Nested
    class Count {
        @Test
        void noFilter() throws Exception {
            when(productRepository.count(null, null, null)).thenReturn(10L);
            assertEquals(10L, controller.countProducts(null, null, null));
        }

        @Test
        void withProjectFilter() throws Exception {
            when(productRepository.count(null, null, 1L)).thenReturn(5L);
            assertEquals(5L, controller.countProducts(null, null, 1L));
        }

        @Test
        void withAllFilters() throws Exception {
            when(productRepository.count(1L, 2L, 3L)).thenReturn(1L);
            assertEquals(1L, controller.countProducts(1L, 2L, 3L));
        }

        @Test
        void countDivisionFilter() throws Exception {
            when(productRepository.count(1L, null, null)).thenReturn(3L);
            assertEquals(3L, controller.countProducts(1L, null, null));
        }

        @Test
        void countDepartmentFilter() throws Exception {
            when(productRepository.count(null, 1L, null)).thenReturn(4L);
            assertEquals(4L, controller.countProducts(null, 1L, null));
        }

        @Test
        void countZero() throws Exception {
            when(productRepository.count(99L, null, null)).thenReturn(0L);
            assertEquals(0L, controller.countProducts(99L, null, null));
        }

        @Test
        void repositoryThrows() {
            when(productRepository.count(any(), any(), any())).thenThrow(RuntimeException.class);
            assertThrows(ServerErrorException.class,
                () -> controller.countProducts(1L, 1L, 1L));
        }

        @Test
        void countReturnsTwenty() throws Exception {
            when(productRepository.count(null, null, null)).thenReturn(20L);
            assertEquals(20L, controller.countProducts(null, null, null));
        }

        @Test
        void countProjectFilterZero() throws Exception {
            when(productRepository.count(null, null, 99L)).thenReturn(0L);
            assertEquals(0L, controller.countProducts(null, null, 99L));
        }
    }

    @Nested
    class GetById {
        @Test
        void exists() throws Exception {
            when(productRepository.findById(1L)).thenReturn(Optional.of(product));
            var r = controller.getProductById(ProductVO.builder().id(1L).build());
            assertEquals(1L, r.getId());
            assertEquals("v01.02.003", r.getVersion());
        }

        @Test
        void notExists() {
            when(productRepository.findById(99L)).thenReturn(Optional.empty());
            assertThrows(InvalidDataException.class,
                () -> controller.getProductById(ProductVO.builder().id(99L).build()));
        }

        @Test
        void negativeId() {
            assertThrows(InvalidDataException.class,
                () -> controller.getProductById(ProductVO.builder().id(-1L).build()));
        }

        @Test
        void zeroId() {
            assertThrows(InvalidDataException.class,
                () -> controller.getProductById(ProductVO.builder().id(0L).build()));
        }

        @Test
        void nullId() {
            assertThrows(InvalidDataException.class,
                () -> controller.getProductById(ProductVO.builder().id(null).build()));
        }

        @Test
        void repositoryThrows() {
            when(productRepository.findById(any())).thenThrow(RuntimeException.class);
            assertThrows(ServerErrorException.class,
                () -> controller.getProductById(ProductVO.builder().id(1L).build()));
        }

        @Test
        void existsReturnsCorrectFields() throws Exception {
            when(productRepository.findById(1L)).thenReturn(Optional.of(product));
            var r = controller.getProductById(ProductVO.builder().id(1L).build());
            assertEquals("Prod", r.getName());
            assertEquals("v01.02.003", r.getVersion());
            assertEquals("Desc", r.getDescription());
            assertNotNull(r.getPublishDate());
            assertEquals(1L, r.getProjectId());
        }

        @Test
        void largeId() {
            when(productRepository.findById(999999L)).thenReturn(Optional.of(product));
            assertDoesNotThrow(() -> controller.getProductById(ProductVO.builder().id(999999L).build()));
        }
    }

    @Nested
    class Create {
        @Test
        void valid() throws Exception {
            when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
            when(productRepository.save(any())).thenReturn(product);
            var r = controller.createProduct(ProductVO.builder().name("Prod").version("v01.02.003").projectId(1L).build());
            assertNotNull(r);
        }

        @Test
        void blankName() {
            assertThrows(InvalidDataException.class,
                () -> controller.createProduct(ProductVO.builder().name("").version("v01.02.003").projectId(1L).build()));
        }

        @Test
        void nullProject() {
            assertThrows(InvalidDataException.class,
                () -> controller.createProduct(ProductVO.builder().name("Prod").version("v01.02.003").projectId(null).build()));
        }

        @Test
        void invalidVersion() {
            assertThrows(InvalidDataException.class,
                () -> controller.createProduct(ProductVO.builder().name("Prod").version("bad").projectId(1L).build()));
        }

        @Test
        void projectNotExists() {
            when(projectRepository.findById(99L)).thenReturn(Optional.empty());
            assertThrows(InvalidDataException.class,
                () -> controller.createProduct(ProductVO.builder().name("Prod").version("v01.02.003").projectId(99L).build()));
        }

        @Test
        void nullName() {
            assertThrows(InvalidDataException.class,
                () -> controller.createProduct(ProductVO.builder().name(null).version("v01.02.003").projectId(1L).build()));
        }

        @Test
        void nullVersion() {
            assertThrows(InvalidDataException.class,
                () -> controller.createProduct(ProductVO.builder().name("Prod").version(null).projectId(1L).build()));
        }

        @Test
        void blankVersion() {
            assertThrows(InvalidDataException.class,
                () -> controller.createProduct(ProductVO.builder().name("Prod").version("").projectId(1L).build()));
        }

        @Test
        void repositoryThrowsOnSave() {
            when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
            when(productRepository.save(any())).thenThrow(RuntimeException.class);
            assertThrows(ServerErrorException.class,
                () -> controller.createProduct(ProductVO.builder().name("Prod").version("v01.02.003").projectId(1L).build()));
        }

        @Test
        void repositoryThrowsOnFindParent() {
            when(projectRepository.findById(any())).thenThrow(RuntimeException.class);
            assertThrows(ServerErrorException.class,
                () -> controller.createProduct(ProductVO.builder().name("Prod").version("v01.02.003").projectId(1L).build()));
        }

        @Test
        void createCallsSave() throws Exception {
            when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
            when(productRepository.save(any())).thenReturn(product);
            controller.createProduct(ProductVO.builder().name("Prod").version("v01.02.003").projectId(1L).build());
            verify(productRepository).save(any(Product.class));
        }

        @Test
        void createValidCheckFields() throws Exception {
            var saved = Product.builder().id(5L).name("NewProd").version("v02.00.000")
                .publishDate(LocalDateTime.of(2025, 6, 1, 0, 0))
                .description("New desc").project(project).build();
            when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
            when(productRepository.save(any())).thenReturn(saved);
            var r = controller.createProduct(ProductVO.builder().name("NewProd").version("v02.00.000")
                .publishDate(LocalDateTime.of(2025, 6, 1, 0, 0))
                .description("New desc").projectId(1L).build());
            assertEquals(5L, r.getId());
            assertEquals("NewProd", r.getName());
            assertEquals("v02.00.000", r.getVersion());
            assertEquals(1L, r.getProjectId());
        }

        @Test
        void invalidVersionFormatMissingV() {
            assertThrows(InvalidDataException.class,
                () -> controller.createProduct(ProductVO.builder().name("Prod").version("01.02.003").projectId(1L).build()));
        }

        @Test
        void invalidVersionFormatTooLong() {
            assertThrows(InvalidDataException.class,
                () -> controller.createProduct(ProductVO.builder().name("Prod").version("v001.02.003").projectId(1L).build()));
        }
    }

    @Nested
    class Update {
        @Test
        void valid() throws Exception {
            when(productRepository.findById(1L)).thenReturn(Optional.of(product));
            when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
            when(productRepository.save(any())).thenReturn(product);
            var r = controller.updateProduct(ProductVO.builder().id(1L).name("Updated").version("v01.02.003").projectId(1L).build());
            assertNotNull(r);
        }

        @Test
        void notExists() {
            when(productRepository.findById(99L)).thenReturn(Optional.empty());
            assertThrows(InvalidDataException.class,
                () -> controller.updateProduct(ProductVO.builder().id(99L).name("X").version("v01.02.003").projectId(1L).build()));
        }

        @Test
        void blankName() {
            when(productRepository.findById(1L)).thenReturn(Optional.of(product));
            assertThrows(InvalidDataException.class,
                () -> controller.updateProduct(ProductVO.builder().id(1L).name("").version("v01.02.003").projectId(1L).build()));
        }

        @Test
        void nullName() {
            when(productRepository.findById(1L)).thenReturn(Optional.of(product));
            assertThrows(InvalidDataException.class,
                () -> controller.updateProduct(ProductVO.builder().id(1L).name(null).version("v01.02.003").projectId(1L).build()));
        }

        @Test
        void nullVersion() {
            when(productRepository.findById(1L)).thenReturn(Optional.of(product));
            assertThrows(InvalidDataException.class,
                () -> controller.updateProduct(ProductVO.builder().id(1L).name("X").version(null).projectId(1L).build()));
        }

        @Test
        void blankVersion() {
            when(productRepository.findById(1L)).thenReturn(Optional.of(product));
            assertThrows(InvalidDataException.class,
                () -> controller.updateProduct(ProductVO.builder().id(1L).name("X").version("").projectId(1L).build()));
        }

        @Test
        void invalidVersion() {
            when(productRepository.findById(1L)).thenReturn(Optional.of(product));
            assertThrows(InvalidDataException.class,
                () -> controller.updateProduct(ProductVO.builder().id(1L).name("X").version("bad").projectId(1L).build()));
        }

        @Test
        void nullProjectId() throws Exception {
            when(productRepository.findById(1L)).thenReturn(Optional.of(product));
            when(productRepository.save(any())).thenReturn(product);
            var r = controller.updateProduct(ProductVO.builder().id(1L).name("Updated").version("v01.02.003").projectId(null).build());
            assertNotNull(r);
        }

        @Test
        void projectNotExists() {
            when(productRepository.findById(1L)).thenReturn(Optional.of(product));
            when(projectRepository.findById(99L)).thenReturn(Optional.empty());
            assertThrows(InvalidDataException.class,
                () -> controller.updateProduct(ProductVO.builder().id(1L).name("X").version("v01.02.003").projectId(99L).build()));
        }

        @Test
        void repositoryThrowsOnFind() {
            when(productRepository.findById(any())).thenThrow(RuntimeException.class);
            assertThrows(ServerErrorException.class,
                () -> controller.updateProduct(ProductVO.builder().id(1L).name("X").version("v01.02.003").projectId(1L).build()));
        }

        @Test
        void repositoryThrowsOnSave() {
            when(productRepository.findById(1L)).thenReturn(Optional.of(product));
            when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
            when(productRepository.save(any())).thenThrow(RuntimeException.class);
            assertThrows(ServerErrorException.class,
                () -> controller.updateProduct(ProductVO.builder().id(1L).name("X").version("v01.02.003").projectId(1L).build()));
        }

        @Test
        void updateValidCheckChangedFields() throws Exception {
            var updated = Product.builder().id(1L).name("Changed").version("v99.99.999")
                .description("New desc").project(project).build();
            when(productRepository.findById(1L)).thenReturn(Optional.of(product));
            when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
            when(productRepository.save(any())).thenReturn(updated);
            var r = controller.updateProduct(ProductVO.builder().id(1L).name("Changed").version("v99.99.999")
                .description("New desc").projectId(1L).build());
            assertEquals("Changed", r.getName());
            assertEquals("v99.99.999", r.getVersion());
        }

        @Test
        void negativeId() {
            assertThrows(InvalidDataException.class,
                () -> controller.updateProduct(ProductVO.builder().id(-1L).name("X").version("v01.02.003").projectId(1L).build()));
        }

        @Test
        void zeroId() {
            assertThrows(InvalidDataException.class,
                () -> controller.updateProduct(ProductVO.builder().id(0L).name("X").version("v01.02.003").projectId(1L).build()));
        }

        @Test
        void nullId() {
            assertThrows(InvalidDataException.class,
                () -> controller.updateProduct(ProductVO.builder().id(null).name("X").version("v01.02.003").projectId(1L).build()));
        }

        @Test
        void invalidVersionFormatShort() {
            when(productRepository.findById(1L)).thenReturn(Optional.of(product));
            assertThrows(InvalidDataException.class,
                () -> controller.updateProduct(ProductVO.builder().id(1L).name("X").version("v01.02").projectId(1L).build()));
        }
    }

    @Nested
    class Delete {
        @Test
        void exists() throws Exception {
            when(productRepository.findById(1L)).thenReturn(Optional.of(product));
            assertDoesNotThrow(() -> controller.deleteProduct(ProductVO.builder().id(1L).build()));
            verify(productRepository).delete(product);
        }

        @Test
        void notExists() {
            when(productRepository.findById(99L)).thenReturn(Optional.empty());
            assertThrows(InvalidDataException.class,
                () -> controller.deleteProduct(ProductVO.builder().id(99L).build()));
        }

        @Test
        void repositoryThrowsOnFind() {
            when(productRepository.findById(any())).thenThrow(RuntimeException.class);
            assertThrows(ServerErrorException.class,
                () -> controller.deleteProduct(ProductVO.builder().id(1L).build()));
        }

        @Test
        void repositoryThrowsOnDelete() {
            when(productRepository.findById(1L)).thenReturn(Optional.of(product));
            doThrow(RuntimeException.class).when(productRepository).delete(any());
            assertThrows(ServerErrorException.class,
                () -> controller.deleteProduct(ProductVO.builder().id(1L).build()));
        }

        @Test
        void nullId() {
            assertThrows(InvalidDataException.class,
                () -> controller.deleteProduct(ProductVO.builder().id(null).build()));
        }

        @Test
        void zeroId() {
            assertThrows(InvalidDataException.class,
                () -> controller.deleteProduct(ProductVO.builder().id(0L).build()));
        }

        @Test
        void negativeId() {
            assertThrows(InvalidDataException.class,
                () -> controller.deleteProduct(ProductVO.builder().id(-1L).build()));
        }

        @Test
        void deleteVerifiesCorrectEntity() throws Exception {
            when(productRepository.findById(1L)).thenReturn(Optional.of(product));
            doNothing().when(productRepository).delete(any());
            controller.deleteProduct(ProductVO.builder().id(1L).build());
            verify(productRepository).delete(product);
            verify(productRepository, times(1)).delete(any(Product.class));
        }
    }
}
