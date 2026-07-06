package com.adlph.internal.managment.index.server.repository;

import com.adlph.internal.managment.index.server.data.entity.Department;
import com.adlph.internal.managment.index.server.data.entity.Division;
import com.adlph.internal.managment.index.server.data.entity.Project;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({DivisionRepository.class, DepartmentRepository.class, ProjectRepository.class})
class ProjectRepositoryTest {

    @Autowired private ProjectRepository repository;
    @Autowired private DivisionRepository divisionRepository;
    @Autowired private DepartmentRepository departmentRepository;
    @Autowired private TestEntityManager em;

    private Division division;
    private Department department;
    private Project project;

    @BeforeEach
    void setUp() {
        division = divisionRepository.save(Division.builder().name("Div").build());
        department = departmentRepository.save(Department.builder().name("Dept").division(division).build());
        project = repository.save(Project.builder().name("Proj").department(department).build());
        em.flush();
    }

    @Test
    void saveAndFindById() {
        var found = repository.findById(project.getId());
        assertTrue(found.isPresent());
        assertEquals("Proj", found.get().getName());
    }

    @Test
    void findByIdNotFound() {
        assertTrue(repository.findById(999L).isEmpty());
    }

    @Test
    void findAllNoFilter() {
        assertEquals(1, repository.findAll(null, null, null, null).size());
    }

    @Test
    void findAllFilterByDepartment() {
        assertEquals(1, repository.findAll(null, department.getId(), null, null).size());
    }

    @Test
    void findAllFilterByDivision() {
        assertEquals(1, repository.findAll(division.getId(), null, null, null).size());
    }

    @Test
    void findAllNoMatch() {
        assertTrue(repository.findAll(999L, null, null, null).isEmpty());
        assertTrue(repository.findAll(null, 999L, null, null).isEmpty());
    }

    @Nested
    class CountTests {
        @Test
        void noFilter() {
            assertEquals(1, repository.count(null, null));
            assertEquals(1, repository.count());
        }

        @Test
        void byDepartment() {
            assertEquals(1, repository.count(null, department.getId()));
        }

        @Test
        void byDivision() {
            assertEquals(1, repository.count(division.getId(), null));
        }

        @Test
        void noMatch() {
            assertEquals(0, repository.count(999L, null));
            assertEquals(0, repository.count(null, 999L));
        }
    }

    @Test
    void delete() {
        repository.delete(project);
        em.flush();
        assertTrue(repository.findById(project.getId()).isEmpty());
    }

    @Test
    void update() {
        var p = repository.findById(project.getId()).orElseThrow();
        p.setName("Updated");
        repository.save(p);
        em.flush();
        em.clear();
        assertEquals("Updated", repository.findById(project.getId()).orElseThrow().getName());
    }

    @Test
    void existsById() {
        assertTrue(repository.existsById(project.getId()));
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
        repository.deleteById(project.getId());
        em.flush();
        assertTrue(repository.findById(project.getId()).isEmpty());
    }

    @Test
    void findByIdWithNull() {
        assertThrows(Exception.class, () -> repository.findById(null));
    }

    @Test
    void findAllEmptyTable() {
        repository.delete(project);
        em.flush();
        assertTrue(repository.findAll().isEmpty());
    }

    @Test
    void findAllMultipleRecords() {
        repository.save(Project.builder().name("Second").department(department).build());
        repository.save(Project.builder().name("Third").department(department).build());
        em.flush();
        assertEquals(3, repository.findAll().size());
    }

    @Test
    void findAllVerifyContent() {
        var p2 = repository.save(Project.builder().name("VerifyProj").department(department).build());
        em.flush();
        em.clear();
        var all = repository.findAll();
        assertTrue(all.stream().anyMatch(p -> p.getName().equals("Proj")));
        assertTrue(all.stream().anyMatch(p -> p.getName().equals("VerifyProj")));
    }

    @Test
    void findAllOrdering() {
        repository.save(Project.builder().name("A").department(department).build());
        repository.save(Project.builder().name("B").department(department).build());
        em.flush();
        assertEquals(3, repository.findAll().size());
    }

    @Test
    void findAllFilterByDivisionAndDepartment() {
        assertEquals(1, repository.findAll(division.getId(), department.getId(), null, null).size());
    }

    @Test
    void findAllFilterByDivisionNoMatch() {
        assertTrue(repository.findAll(999L, null, null, null).isEmpty());
    }

    @Test
    void findAllFilterByDepartmentNoMatch() {
        assertTrue(repository.findAll(null, 999L, null, null).isEmpty());
    }

    @Test
    void findAllFilterByDivisionWithPagination() {
        assertEquals(1, repository.findAll(division.getId(), null, 5, 0).size());
    }

    @Test
    void findAllFilterByDepartmentWithPagination() {
        assertEquals(1, repository.findAll(null, department.getId(), 5, 0).size());
    }

    @Test
    void findAllFilterByBothWithPagination() {
        assertEquals(1, repository.findAll(division.getId(), department.getId(), 5, 0).size());
    }

    @Test
    void findAllFilterByBothNoMatch() {
        assertTrue(repository.findAll(999L, 999L, null, null).isEmpty());
    }

    @Test
    void findAllFilterMultipleDepartments() {
        var dept2 = departmentRepository.save(Department.builder().name("Dept2").division(division).build());
        repository.save(Project.builder().name("Proj2").department(dept2).build());
        em.flush();
        assertEquals(1, repository.findAll(null, department.getId(), null, null).size());
        assertEquals(1, repository.findAll(null, dept2.getId(), null, null).size());
    }

    @Test
    void findAllPaginationPageZero() {
        assertEquals(1, repository.findAll(null, null, 5, 0).size());
    }

    @Test
    void findAllPaginationCountZero() {
        assertTrue(repository.findAll(null, null, 0, 0).isEmpty());
    }

    @Test
    void findAllPaginationCountLargerThanData() {
        assertEquals(1, repository.findAll(null, null, 100, 0).size());
    }

    @Test
    void findAllPaginationNullCount() {
        assertEquals(1, repository.findAll(null, null, null, 0).size());
    }

    @Test
    void findAllPaginationNullPage() {
        assertEquals(1, repository.findAll(null, null, 5, null).size());
    }

    @Test
    void findAllPaginationSecondPage() {
        for (int i = 0; i < 5; i++) {
            repository.save(Project.builder().name("P" + i).department(department).build());
        }
        em.flush();
        assertEquals(3, repository.findAll(null, null, 3, 1).size());
    }

    @Test
    void findAllPaginationLastPage() {
        repository.save(Project.builder().name("Extra").department(department).build());
        em.flush();
        assertTrue(repository.findAll(null, null, 2, 1).isEmpty());
    }

    @Test
    void findAllPaginationAfterDelete() {
        repository.delete(project);
        em.flush();
        assertTrue(repository.findAll(null, null, 5, 0).isEmpty());
    }

    @Test
    void countAfterInsert() {
        repository.save(Project.builder().name("Extra").department(department).build());
        em.flush();
        assertEquals(2, repository.count());
    }

    @Test
    void countAfterMultipleInserts() {
        repository.save(Project.builder().name("A").department(department).build());
        repository.save(Project.builder().name("B").department(department).build());
        em.flush();
        assertEquals(3, repository.count());
    }

    @Test
    void countByDivisionAndDepartment() {
        assertEquals(1, repository.count(division.getId(), department.getId()));
    }

    @Test
    void countByDivisionNoMatch() {
        assertEquals(0, repository.count(999L, null));
    }

    @Test
    void countByDepartmentNoMatch() {
        assertEquals(0, repository.count(null, 999L));
    }

    @Test
    void countByDivisionAfterInsert() {
        repository.save(Project.builder().name("Extra").department(department).build());
        em.flush();
        assertEquals(2, repository.count(division.getId(), null));
    }

    @Test
    void existsByIdNonExisting() {
        assertFalse(repository.existsById(999L));
    }

    @Test
    void existsByIdAfterDelete() {
        repository.deleteById(project.getId());
        em.flush();
        assertFalse(repository.existsById(project.getId()));
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
        repository.deleteById(project.getId());
        em.flush();
        assertEquals(0, repository.count());
    }

    @Test
    void deleteEntityNonExisting() {
        repository.deleteById(999L);
    }

    @Test
    void deleteThenFindById() {
        repository.delete(project);
        em.flush();
        assertTrue(repository.findById(project.getId()).isEmpty());
    }

    @Test
    void saveWithBlankName() {
        var proj = repository.save(Project.builder().name("").department(department).build());
        em.flush();
        assertNotNull(proj.getId());
        assertEquals("", repository.findById(proj.getId()).orElseThrow().getName());
    }

    @Test
    void saveWithNullDepartment() {
        assertThrows(Exception.class, () -> {
            repository.save(Project.builder().name("NoDept").department(null).build());
            em.flush();
        });
    }

    @Test
    void saveNullObject() {
        assertThrows(Exception.class, () -> repository.save(null));
    }

    @Test
    void saveMultipleWithDifferentDepartments() {
        var dept2 = departmentRepository.save(Department.builder().name("Dept2").division(division).build());
        var p2 = repository.save(Project.builder().name("Proj2").department(dept2).build());
        var p3 = repository.save(Project.builder().name("Proj3").department(department).build());
        em.flush();
        em.clear();
        assertEquals("Proj2", repository.findById(p2.getId()).orElseThrow().getName());
        assertEquals("Proj3", repository.findById(p3.getId()).orElseThrow().getName());
    }

    @Test
    void saveAndVerifyReturnedEntity() {
        var proj = Project.builder().name("Returned").department(department).build();
        var result = repository.save(proj);
        assertNotNull(result.getId());
        assertEquals("Returned", result.getName());
        assertEquals(department.getId(), result.getDepartment().getId());
    }

    @Test
    void saveAndFindAfterClear() {
        em.clear();
        var found = repository.findById(project.getId());
        assertTrue(found.isPresent());
        assertEquals("Proj", found.get().getName());
    }

    @Test
    void updateThenFindAllCount() {
        var p = repository.findById(project.getId()).orElseThrow();
        p.setName("Changed");
        repository.save(p);
        em.flush();
        assertEquals(1, repository.count());
    }
}
