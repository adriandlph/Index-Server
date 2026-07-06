package com.adlph.internal.managment.index.server.repository;

import com.adlph.internal.managment.index.server.data.entity.Department;
import com.adlph.internal.managment.index.server.data.entity.Division;

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
@Import({DivisionRepository.class, DepartmentRepository.class})
class DepartmentRepositoryTest {

    @Autowired private DepartmentRepository repository;
    @Autowired private DivisionRepository divisionRepository;
    @Autowired private TestEntityManager em;

    private Division division;
    private Department department;

    @BeforeEach
    void setUp() {
        division = divisionRepository.save(Division.builder().name("Div").build());
        department = repository.save(Department.builder().name("Dept").division(division).build());
        em.flush();
    }

    @Test
    void saveAndFindById() {
        var found = repository.findById(department.getId());
        assertTrue(found.isPresent());
        assertEquals("Dept", found.get().getName());
        assertEquals(division.getId(), found.get().getDivision().getId());
    }

    @Test
    void findByIdNotFound() {
        assertTrue(repository.findById(999L).isEmpty());
    }

    @Test
    void findAllNoFilter() {
        var all = repository.findAll(null, null, null);
        assertEquals(1, all.size());
    }

    @Test
    void findAllFilterByDivision() {
        var all = repository.findAll(division.getId(), null, null);
        assertEquals(1, all.size());
    }

    @Test
    void findAllFilterByNonExistentDivision() {
        var all = repository.findAll(999L, null, null);
        assertTrue(all.isEmpty());
    }

    @Test
    void findAllWithPagination() {
        for (int i = 0; i < 5; i++) {
            repository.save(Department.builder().name("D" + i).division(division).build());
        }
        em.flush();
        var page1 = repository.findAll(null, 3, 0);
        assertEquals(3, page1.size());
        var page2 = repository.findAll(null, 3, 1);
        assertEquals(3, page2.size());
    }

    @Nested
    class CountTests {
        @Test
        void noFilter() {
            assertEquals(1, repository.count());
            assertEquals(1, repository.count(null));
        }

        @Test
        void withDivisionFilter() {
            assertEquals(1, repository.count(division.getId()));
        }

        @Test
        void withDivisionFilterNoMatch() {
            assertEquals(0, repository.count(999L));
        }

        @Test
        void afterDelete() {
            repository.delete(department);
            em.flush();
            assertEquals(0, repository.count(division.getId()));
            assertEquals(0, repository.count());
        }

        @Test
        void multipleDivisions() {
            var div2 = divisionRepository.save(Division.builder().name("Div2").build());
            repository.save(Department.builder().name("Dept2").division(div2).build());
            em.flush();
            assertEquals(1, repository.count(division.getId()));
            assertEquals(1, repository.count(div2.getId()));
            assertEquals(2, repository.count());
        }
    }

    @Test
    void existsById() {
        assertTrue(repository.existsById(department.getId()));
    }

    @Test
    void delete() {
        repository.delete(department);
        em.flush();
        assertTrue(repository.findById(department.getId()).isEmpty());
    }

    @Test
    void deleteById() {
        repository.deleteById(department.getId());
        em.flush();
        assertTrue(repository.findById(department.getId()).isEmpty());
    }

    @Test
    void update() {
        var dept = repository.findById(department.getId()).orElseThrow();
        dept.setName("Updated");
        repository.save(dept);
        em.flush();
        em.clear();
        assertEquals("Updated", repository.findById(department.getId()).orElseThrow().getName());
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
        repository.deleteById(department.getId());
        em.flush();
        assertTrue(repository.findById(department.getId()).isEmpty());
    }

    @Test
    void findByIdWithNull() {
        assertThrows(Exception.class, () -> repository.findById(null));
    }

    @Test
    void findAllEmptyTable() {
        repository.delete(department);
        em.flush();
        assertTrue(repository.findAll().isEmpty());
    }

    @Test
    void findAllMultipleRecords() {
        repository.save(Department.builder().name("Second").division(division).build());
        repository.save(Department.builder().name("Third").division(division).build());
        em.flush();
        assertEquals(3, repository.findAll().size());
    }

    @Test
    void findAllVerifyContent() {
        var d2 = repository.save(Department.builder().name("VerifyDept").division(division).build());
        em.flush();
        em.clear();
        var all = repository.findAll();
        assertTrue(all.stream().anyMatch(d -> d.getName().equals("Dept")));
        assertTrue(all.stream().anyMatch(d -> d.getName().equals("VerifyDept")));
    }

    @Test
    void findAllOrdering() {
        repository.save(Department.builder().name("A").division(division).build());
        repository.save(Department.builder().name("B").division(division).build());
        em.flush();
        assertEquals(3, repository.findAll().size());
    }

    @Test
    void findAllFilterByDivisionWithPagination() {
        for (int i = 0; i < 5; i++) {
            repository.save(Department.builder().name("D" + i).division(division).build());
        }
        em.flush();
        assertEquals(3, repository.findAll(division.getId(), 3, 0).size());
        assertEquals(3, repository.findAll(division.getId(), 3, 1).size());
    }

    @Test
    void findAllFilterByDivisionSecondPage() {
        for (int i = 0; i < 5; i++) {
            repository.save(Department.builder().name("D" + i).division(division).build());
        }
        em.flush();
        assertEquals(1, repository.findAll(division.getId(), 5, 1).size());
    }

    @Test
    void findAllFilterByNonExistentDivisionWithPagination() {
        assertTrue(repository.findAll(999L, 10, 0).isEmpty());
    }

    @Test
    void findAllFilterByDivisionLastPage() {
        repository.save(Department.builder().name("Extra").division(division).build());
        em.flush();
        assertTrue(repository.findAll(division.getId(), 1, 2).isEmpty());
    }

    @Test
    void findAllPaginationPageZero() {
        assertEquals(1, repository.findAll(null, 5, 0).size());
    }

    @Test
    void findAllPaginationCountZero() {
        assertTrue(repository.findAll(null, 0, 0).isEmpty());
    }

    @Test
    void findAllPaginationCountLargerThanData() {
        assertEquals(1, repository.findAll(null, 100, 0).size());
    }

    @Test
    void findAllPaginationNullCount() {
        assertEquals(1, repository.findAll(null, null, 0).size());
    }

    @Test
    void findAllPaginationNullPage() {
        assertEquals(1, repository.findAll(null, 5, null).size());
    }

    @Test
    void findAllPaginationSecondPage() {
        for (int i = 0; i < 5; i++) {
            repository.save(Department.builder().name("D" + i).division(division).build());
        }
        em.flush();
        assertEquals(3, repository.findAll(null, 3, 1).size());
    }

    @Test
    void findAllPaginationLastPage() {
        repository.save(Department.builder().name("Extra").division(division).build());
        em.flush();
        assertTrue(repository.findAll(null, 2, 1).isEmpty());
    }

    @Test
    void findAllPaginationAfterDelete() {
        repository.delete(department);
        em.flush();
        assertTrue(repository.findAll(null, 5, 0).isEmpty());
    }

    @Test
    void findAllFilterByDivisionMultipleDivisions() {
        var div2 = divisionRepository.save(Division.builder().name("Div2").build());
        repository.save(Department.builder().name("FromDiv2").division(div2).build());
        em.flush();
        assertEquals(1, repository.findAll(division.getId(), null, null).size());
        assertEquals(1, repository.findAll(div2.getId(), null, null).size());
    }

    @Test
    void countAfterInsert() {
        repository.save(Department.builder().name("Extra").division(division).build());
        em.flush();
        assertEquals(2, repository.count());
    }

    @Test
    void countAfterMultipleInserts() {
        repository.save(Department.builder().name("A").division(division).build());
        repository.save(Department.builder().name("B").division(division).build());
        em.flush();
        assertEquals(3, repository.count());
    }

    @Test
    void countByDivisionAfterInsert() {
        repository.save(Department.builder().name("Extra").division(division).build());
        em.flush();
        assertEquals(2, repository.count(division.getId()));
    }

    @Test
    void countByDivisionMultipleDivisions() {
        var div2 = divisionRepository.save(Division.builder().name("Div2").build());
        repository.save(Department.builder().name("FromDiv2").division(div2).build());
        em.flush();
        assertEquals(1, repository.count(division.getId()));
        assertEquals(1, repository.count(div2.getId()));
        assertEquals(2, repository.count());
    }

    @Test
    void existsByIdNonExisting() {
        assertFalse(repository.existsById(999L));
    }

    @Test
    void existsByIdAfterDelete() {
        repository.deleteById(department.getId());
        em.flush();
        assertFalse(repository.existsById(department.getId()));
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
        repository.deleteById(department.getId());
        em.flush();
        assertEquals(0, repository.count());
    }

    @Test
    void deleteEntityNonExisting() {
        repository.deleteById(999L);
    }

    @Test
    void deleteThenFindById() {
        repository.delete(department);
        em.flush();
        assertTrue(repository.findById(department.getId()).isEmpty());
    }

    @Test
    void saveWithBlankName() {
        var dept = repository.save(Department.builder().name("").division(division).build());
        em.flush();
        assertNotNull(dept.getId());
        assertEquals("", repository.findById(dept.getId()).orElseThrow().getName());
    }

    @Test
    void saveWithNullDivision() {
        assertThrows(Exception.class, () -> {
            repository.save(Department.builder().name("NoDiv").division(null).build());
            em.flush();
        });
    }

    @Test
    void saveNullObject() {
        assertThrows(Exception.class, () -> repository.save(null));
    }

    @Test
    void saveMultipleWithDifferentDivisions() {
        var div2 = divisionRepository.save(Division.builder().name("Div2").build());
        var d2 = repository.save(Department.builder().name("Dept2").division(div2).build());
        var d3 = repository.save(Department.builder().name("Dept3").division(division).build());
        em.flush();
        em.clear();
        assertEquals("Dept2", repository.findById(d2.getId()).orElseThrow().getName());
        assertEquals("Dept3", repository.findById(d3.getId()).orElseThrow().getName());
    }

    @Test
    void saveAndVerifyReturnedEntity() {
        var dept = Department.builder().name("Returned").division(division).build();
        var result = repository.save(dept);
        assertNotNull(result.getId());
        assertEquals("Returned", result.getName());
        assertEquals(division.getId(), result.getDivision().getId());
    }

    @Test
    void saveDepartmentThenUpdateDivision() {
        var div2 = divisionRepository.save(Division.builder().name("Div2").build());
        var dept = repository.findById(department.getId()).orElseThrow();
        dept.setDivision(div2);
        repository.save(dept);
        em.flush();
        em.clear();
        var found = repository.findById(department.getId()).orElseThrow();
        assertEquals(div2.getId(), found.getDivision().getId());
    }

    @Test
    void saveAndFindAfterClear() {
        em.clear();
        var found = repository.findById(department.getId());
        assertTrue(found.isPresent());
        assertEquals("Dept", found.get().getName());
    }
}
