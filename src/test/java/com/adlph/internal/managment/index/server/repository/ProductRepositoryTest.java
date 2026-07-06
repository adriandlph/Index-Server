package com.adlph.internal.managment.index.server.repository;

import com.adlph.internal.managment.index.server.data.entity.Department;
import com.adlph.internal.managment.index.server.data.entity.Division;
import com.adlph.internal.managment.index.server.data.entity.Product;
import com.adlph.internal.managment.index.server.data.entity.Project;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({DivisionRepository.class, DepartmentRepository.class, ProjectRepository.class, ProductRepository.class})
class ProductRepositoryTest {

    @Autowired private ProductRepository repository;
    @Autowired private DivisionRepository divisionRepository;
    @Autowired private DepartmentRepository departmentRepository;
    @Autowired private ProjectRepository projectRepository;
    @Autowired private TestEntityManager em;

    private Division division;
    private Department department;
    private Project project;
    private Product product;

    @BeforeEach
    void setUp() {
        division = divisionRepository.save(Division.builder().name("Div").build());
        department = departmentRepository.save(Department.builder().name("Dept").division(division).build());
        project = projectRepository.save(Project.builder().name("Proj").department(department).build());
        product = repository.save(Product.builder().name("Prod").version("v01.02.003")
            .publishDate(LocalDateTime.of(2025, 1, 1, 0, 0))
            .description("Description").project(project).build());
        em.flush();
    }

    @Test
    void saveAndFindById() {
        var found = repository.findById(product.getId());
        assertTrue(found.isPresent());
        assertEquals("Prod", found.get().getName());
        assertEquals("v01.02.003", found.get().getVersion());
        assertEquals("Description", found.get().getDescription());
        assertNotNull(found.get().getPublishDate());
    }

    @Test
    void findByIdNotFound() {
        assertTrue(repository.findById(999L).isEmpty());
    }

    @Test
    void findAllNoFilter() {
        assertEquals(1, repository.findAll(null, null, null, null, null).size());
    }

    @Test
    void findAllFilterByProject() {
        assertEquals(1, repository.findAll(null, null, project.getId(), null, null).size());
    }

    @Test
    void findAllFilterByDepartment() {
        assertEquals(1, repository.findAll(null, department.getId(), null, null, null).size());
    }

    @Test
    void findAllFilterByDivision() {
        assertEquals(1, repository.findAll(division.getId(), null, null, null, null).size());
    }

    @Test
    void findAllFilterNoMatch() {
        assertTrue(repository.findAll(999L, null, null, null, null).isEmpty());
    }

    @Test
    void saveWithNullOptionals() {
        var p = repository.save(Product.builder().name("NoOpts").version("v99.99.999")
            .publishDate(null).description(null).project(project).build());
        em.flush();
        var found = repository.findById(p.getId()).orElseThrow();
        assertNull(found.getPublishDate());
        assertNull(found.getDescription());
    }

    @Nested
    class CountTests {
        @Test
        void noFilter() {
            assertEquals(1, repository.count(null, null, null));
            assertEquals(1, repository.count());
        }

        @Test
        void byProject() {
            assertEquals(1, repository.count(null, null, project.getId()));
        }

        @Test
        void byDepartment() {
            assertEquals(1, repository.count(null, department.getId(), null));
        }

        @Test
        void byDivision() {
            assertEquals(1, repository.count(division.getId(), null, null));
        }

        @Test
        void noMatch() {
            assertEquals(0, repository.count(999L, null, null));
        }

        @Test
        void multipleProducts() {
            repository.save(Product.builder().name("P2").version("v02.00.000").project(project).build());
            em.flush();
            assertEquals(2, repository.count(null, null, null));
            assertEquals(2, repository.count(null, null, project.getId()));
        }
    }

    @Test
    void delete() {
        repository.delete(product);
        em.flush();
        assertTrue(repository.findById(product.getId()).isEmpty());
    }

    @Test
    void deleteById() {
        repository.deleteById(product.getId());
        em.flush();
        assertTrue(repository.findById(product.getId()).isEmpty());
    }

    @Test
    void update() {
        var p = repository.findById(product.getId()).orElseThrow();
        p.setName("Updated");
        p.setVersion("v99.99.999");
        repository.save(p);
        em.flush();
        em.clear();
        var found = repository.findById(product.getId()).orElseThrow();
        assertEquals("Updated", found.getName());
        assertEquals("v99.99.999", found.getVersion());
    }

    @Test
    void existsById() {
        assertTrue(repository.existsById(product.getId()));
    }

    @Test
    void findAllWithPagination() {
        for (int i = 0; i < 9; i++) {
            repository.save(Product.builder().name("P" + i).version("v01.02.00" + i).project(project).build());
        }
        em.flush();
        assertEquals(5, repository.findAll(null, null, null, 5, 0).size());
        assertEquals(5, repository.findAll(null, null, null, 5, 1).size());
    }

    @Test
    void findAllDefault() {
        assertEquals(1, repository.findAll().size());
    }

    @Test
    void findByIdNegativeId() {
        assertTrue(repository.findById(-1L).isEmpty());
    }

    @Test
    void findByIdAfterDelete() {
        repository.deleteById(product.getId());
        em.flush();
        assertTrue(repository.findById(product.getId()).isEmpty());
    }

    @Test
    void findByIdWithNull() {
        assertThrows(Exception.class, () -> repository.findById(null));
    }

    @Test
    void findAllEmptyTable() {
        repository.delete(product);
        em.flush();
        assertTrue(repository.findAll().isEmpty());
    }

    @Test
    void findAllMultipleRecords() {
        repository.save(Product.builder().name("Second").version("v02.00.000").project(project).build());
        repository.save(Product.builder().name("Third").version("v03.00.000").project(project).build());
        em.flush();
        assertEquals(3, repository.findAll().size());
    }

    @Test
    void findAllVerifyContent() {
        var p2 = repository.save(Product.builder().name("VerifyProd").version("v99.00.000").project(project).build());
        em.flush();
        em.clear();
        var all = repository.findAll();
        assertTrue(all.stream().anyMatch(p -> p.getName().equals("Prod")));
        assertTrue(all.stream().anyMatch(p -> p.getName().equals("VerifyProd")));
    }

    @Test
    void findAllOrdering() {
        repository.save(Product.builder().name("A").version("v01.00.000").project(project).build());
        repository.save(Product.builder().name("B").version("v02.00.000").project(project).build());
        em.flush();
        assertEquals(3, repository.findAll().size());
    }

    @Test
    void findAllFilterByAllThree() {
        assertEquals(1, repository.findAll(division.getId(), department.getId(), project.getId(), null, null).size());
    }

    @Test
    void findAllFilterByDivisionAndDepartment() {
        assertEquals(1, repository.findAll(division.getId(), department.getId(), null, null, null).size());
    }

    @Test
    void findAllFilterByDivisionAndProject() {
        assertEquals(1, repository.findAll(division.getId(), null, project.getId(), null, null).size());
    }

    @Test
    void findAllFilterByDepartmentAndProject() {
        assertEquals(1, repository.findAll(null, department.getId(), project.getId(), null, null).size());
    }

    @Test
    void findAllFilterByProjectNoMatch() {
        assertTrue(repository.findAll(null, null, 999L, null, null).isEmpty());
    }

    @Test
    void findAllFilterByDepartmentNoMatch() {
        assertTrue(repository.findAll(null, 999L, null, null, null).isEmpty());
    }

    @Test
    void findAllFilterMultipleProjects() {
        var proj2 = projectRepository.save(Project.builder().name("Proj2").department(department).build());
        repository.save(Product.builder().name("Prod2").version("v02.00.000").project(proj2).build());
        em.flush();
        assertEquals(1, repository.findAll(null, null, project.getId(), null, null).size());
        assertEquals(1, repository.findAll(null, null, proj2.getId(), null, null).size());
    }

    @Test
    void findAllPaginationPageZero() {
        assertEquals(1, repository.findAll(null, null, null, 5, 0).size());
    }

    @Test
    void findAllPaginationCountZero() {
        assertTrue(repository.findAll(null, null, null, 0, 0).isEmpty());
    }

    @Test
    void findAllPaginationCountLargerThanData() {
        assertEquals(1, repository.findAll(null, null, null, 100, 0).size());
    }

    @Test
    void findAllPaginationNullCount() {
        assertEquals(1, repository.findAll(null, null, null, null, 0).size());
    }

    @Test
    void findAllPaginationNullPage() {
        assertEquals(1, repository.findAll(null, null, null, 5, null).size());
    }

    @Test
    void findAllPaginationSecondPage() {
        for (int i = 0; i < 5; i++) {
            repository.save(Product.builder().name("P" + i).version("v01.00.00" + i).project(project).build());
        }
        em.flush();
        assertEquals(3, repository.findAll(null, null, null, 3, 1).size());
    }

    @Test
    void findAllPaginationLastPage() {
        repository.save(Product.builder().name("Extra").version("v99.00.000").project(project).build());
        em.flush();
        assertTrue(repository.findAll(null, null, null, 2, 1).isEmpty());
    }

    @Test
    void findAllPaginationAfterDelete() {
        repository.delete(product);
        em.flush();
        assertTrue(repository.findAll(null, null, null, 5, 0).isEmpty());
    }

    @Test
    void countAfterInsert() {
        repository.save(Product.builder().name("Extra").version("v99.00.000").project(project).build());
        em.flush();
        assertEquals(2, repository.count());
    }

    @Test
    void countByAllThree() {
        assertEquals(1, repository.count(division.getId(), department.getId(), project.getId()));
    }

    @Test
    void countByDivisionNoMatch() {
        assertEquals(0, repository.count(999L, null, null));
    }

    @Test
    void countByDepartmentNoMatch() {
        assertEquals(0, repository.count(null, 999L, null));
    }

    @Test
    void countByProjectNoMatch() {
        assertEquals(0, repository.count(null, null, 999L));
    }

    @Test
    void countByDivisionAfterInsert() {
        repository.save(Product.builder().name("Extra").version("v99.00.000").project(project).build());
        em.flush();
        assertEquals(2, repository.count(division.getId(), null, null));
    }

    @Test
    void existsByIdNonExisting() {
        assertFalse(repository.existsById(999L));
    }

    @Test
    void existsByIdAfterDelete() {
        repository.deleteById(product.getId());
        em.flush();
        assertFalse(repository.existsById(product.getId()));
    }

    @Test
    void existsByIdNegative() {
        assertFalse(repository.existsById(-1L));
    }

    @Test
    void deleteByIdNonExisting() {
        repository.deleteById(999L);
    }

    @Test
    void deleteByIdThenVerifyCount() {
        repository.deleteById(product.getId());
        em.flush();
        assertEquals(0, repository.count());
    }

    @Test
    void deleteEntityNonExisting() {
        repository.deleteById(999L);
    }

    @Test
    void deleteThenFindById() {
        repository.delete(product);
        em.flush();
        assertTrue(repository.findById(product.getId()).isEmpty());
    }

    @Test
    void saveWithBlankName() {
        var prod = repository.save(Product.builder().name("").version("v01.00.000").project(project).build());
        em.flush();
        assertNotNull(prod.getId());
        assertEquals("", repository.findById(prod.getId()).orElseThrow().getName());
    }

    @Test
    void saveWithInvalidVersion() {
        var prod = repository.save(Product.builder().name("BadVer").version("invalid").project(project).build());
        em.flush();
        assertNotNull(prod.getId());
        assertEquals("invalid", repository.findById(prod.getId()).orElseThrow().getVersion());
    }

    @Test
    void saveWithNullProject() {
        assertThrows(Exception.class, () -> {
            repository.save(Product.builder().name("NoProj").version("v01.00.000").project(null).build());
            em.flush();
        });
    }

    @Test
    void saveNullObject() {
        assertThrows(Exception.class, () -> repository.save(null));
    }

    @Test
    void saveMultipleWithDifferentProjects() {
        var proj2 = projectRepository.save(Project.builder().name("Proj2").department(department).build());
        var p2 = repository.save(Product.builder().name("Prod2").version("v02.00.000").project(proj2).build());
        var p3 = repository.save(Product.builder().name("Prod3").version("v03.00.000").project(project).build());
        em.flush();
        em.clear();
        assertEquals("Prod2", repository.findById(p2.getId()).orElseThrow().getName());
        assertEquals("Prod3", repository.findById(p3.getId()).orElseThrow().getName());
    }

    @Test
    void saveAndVerifyReturnedEntity() {
        var prod = Product.builder().name("Returned").version("v99.99.999").project(project).build();
        var result = repository.save(prod);
        assertNotNull(result.getId());
        assertEquals("Returned", result.getName());
        assertEquals("v99.99.999", result.getVersion());
        assertEquals(project.getId(), result.getProject().getId());
    }

    @Test
    void saveAndFindAfterClear() {
        em.clear();
        var found = repository.findById(product.getId());
        assertTrue(found.isPresent());
        assertEquals("Prod", found.get().getName());
    }

    @Test
    void updateVersionThenVerify() {
        var p = repository.findById(product.getId()).orElseThrow();
        p.setVersion("v99.99.999");
        repository.save(p);
        em.flush();
        em.clear();
        assertEquals("v99.99.999", repository.findById(product.getId()).orElseThrow().getVersion());
    }
}
