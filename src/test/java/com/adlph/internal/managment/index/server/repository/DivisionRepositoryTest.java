package com.adlph.internal.managment.index.server.repository;

import com.adlph.internal.managment.index.server.data.entity.Division;

import jakarta.persistence.EntityManager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(DivisionRepository.class)
class DivisionRepositoryTest {

    @Autowired private DivisionRepository repository;
    @Autowired private TestEntityManager em;

    private Division saved;

    @BeforeEach
    void setUp() {
        saved = repository.save(Division.builder().name("Test Division").build());
        em.flush();
    }

    @Test
    void saveAndFindById() {
        var found = repository.findById(saved.getId());
        assertTrue(found.isPresent());
        assertEquals("Test Division", found.get().getName());
    }

    @Test
    void findByIdNotFound() {
        assertTrue(repository.findById(999L).isEmpty());
    }

    @Test
    void findAllNoPagination() {
        repository.save(Division.builder().name("Second").build());
        em.flush();
        var all = repository.findAll(null, null);
        assertEquals(2, all.size());
    }

    @Test
    void findAllWithPagination() {
        for (int i = 0; i < 10; i++) {
            repository.save(Division.builder().name("D" + i).build());
        }
        em.flush();
        var page1 = repository.findAll(5, 0);
        assertEquals(5, page1.size());
        var page2 = repository.findAll(5, 1);
        assertEquals(5, page2.size());
        var page3 = repository.findAll(5, 2);
        assertEquals(1, page3.size());
    }

    @Test
    void findAllNegativePage() {
        assertThrows(IllegalArgumentException.class, () -> repository.findAll(10, -1));
    }

    @Test
    void count() {
        assertEquals(1, repository.count());
        repository.save(Division.builder().name("Another").build());
        em.flush();
        assertEquals(2, repository.count());
    }

    @Test
    void countEmpty() {
        repository.delete(saved);
        em.flush();
        assertEquals(0, repository.count());
    }

    @Test
    void existsById() {
        assertTrue(repository.existsById(saved.getId()));
        assertFalse(repository.existsById(999L));
    }

    @Test
    void deleteById() {
        repository.deleteById(saved.getId());
        em.flush();
        assertTrue(repository.findById(saved.getId()).isEmpty());
    }

    @Test
    void deleteEntity() {
        repository.delete(saved);
        em.flush();
        assertEquals(0, repository.count());
    }

    @Test
    void update() {
        var div = repository.findById(saved.getId()).orElseThrow();
        div.setName("Updated");
        repository.save(div);
        em.flush();
        em.clear();
        var found = repository.findById(saved.getId()).orElseThrow();
        assertEquals("Updated", found.getName());
    }

    @Test
    void saveNullIdCreatesNew() {
        var d = repository.save(Division.builder().name("New").build());
        assertNotNull(d.getId());
    }

    @Test
    void findAllDefault() {
        var all = repository.findAll();
        assertEquals(1, all.size());
    }

    @Test
    void findByIdNegativeId() {
        assertTrue(repository.findById(-1L).isEmpty());
    }

    @Test
    void findByIdAfterDelete() {
        repository.deleteById(saved.getId());
        em.flush();
        assertTrue(repository.findById(saved.getId()).isEmpty());
    }

    @Test
    void findAllEmptyTable() {
        repository.delete(saved);
        em.flush();
        assertTrue(repository.findAll().isEmpty());
    }

    @Test
    void findAllMultipleRecords() {
        repository.save(Division.builder().name("Second").build());
        repository.save(Division.builder().name("Third").build());
        em.flush();
        assertEquals(3, repository.findAll().size());
    }

    @Test
    void findAllVerifyContent() {
        var d2 = repository.save(Division.builder().name("Verify").build());
        em.flush();
        em.clear();
        var all = repository.findAll();
        assertTrue(all.stream().anyMatch(d -> d.getName().equals("Test Division")));
        assertTrue(all.stream().anyMatch(d -> d.getName().equals("Verify")));
    }

    @Test
    void findAllOrdering() {
        repository.save(Division.builder().name("A").build());
        repository.save(Division.builder().name("B").build());
        em.flush();
        assertEquals(3, repository.findAll().size());
    }

    @Test
    void findAllPaginationPageZero() {
        assertEquals(1, repository.findAll(5, 0).size());
    }

    @Test
    void findAllPaginationCountZero() {
        assertTrue(repository.findAll(0, 0).isEmpty());
    }

    @Test
    void findAllPaginationCountLargerThanData() {
        assertEquals(1, repository.findAll(100, 0).size());
    }

    @Test
    void findAllPaginationNullCount() {
        assertEquals(1, repository.findAll(null, 0).size());
    }

    @Test
    void findAllPaginationNullPage() {
        assertEquals(1, repository.findAll(5, null).size());
    }

    @Test
    void findAllPaginationSecondPage() {
        for (int i = 0; i < 10; i++) {
            repository.save(Division.builder().name("D" + i).build());
        }
        em.flush();
        assertEquals(3, repository.findAll(3, 1).size());
    }

    @Test
    void findAllPaginationLastPage() {
        for (int i = 0; i < 3; i++) {
            repository.save(Division.builder().name("D" + i).build());
        }
        em.flush();
        assertTrue(repository.findAll(2, 2).isEmpty());
    }

    @Test
    void findAllPaginationAfterDelete() {
        repository.delete(saved);
        em.flush();
        assertTrue(repository.findAll(5, 0).isEmpty());
    }

    @Test
    void countAfterMultipleInserts() {
        repository.save(Division.builder().name("A").build());
        repository.save(Division.builder().name("B").build());
        em.flush();
        assertEquals(3, repository.count());
    }

    @Test
    void countAfterMultipleDeletes() {
        var d2 = repository.save(Division.builder().name("ToDelete").build());
        em.flush();
        repository.delete(d2);
        repository.delete(saved);
        em.flush();
        assertEquals(0, repository.count());
    }

    @Test
    void countExactMatch() {
        repository.save(Division.builder().name("Exact").build());
        em.flush();
        assertEquals(2, repository.count());
    }

    @Test
    void countAfterDeleteAll() {
        repository.delete(saved);
        em.flush();
        assertEquals(0, repository.count());
    }

    @Test
    void existsByIdNonExisting() {
        assertFalse(repository.existsById(999L));
    }

    @Test
    void existsByIdAfterDelete() {
        repository.deleteById(saved.getId());
        em.flush();
        assertFalse(repository.existsById(saved.getId()));
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
        repository.deleteById(saved.getId());
        em.flush();
        assertEquals(0, repository.count());
    }

    @Test
    void deleteByIdNegative() {
        repository.deleteById(-1L);
    }

    @Test
    void deleteEntityNonExisting() {
        repository.deleteById(999L);
    }

    @Test
    void deleteThenFindById() {
        repository.delete(saved);
        em.flush();
        assertTrue(repository.findById(saved.getId()).isEmpty());
    }

    @Test
    void deleteEntityAfterUpdate() {
        var div = repository.findById(saved.getId()).orElseThrow();
        div.setName("Updated");
        repository.save(div);
        em.flush();
        repository.delete(div);
        em.flush();
        assertTrue(repository.findById(saved.getId()).isEmpty());
    }

    @Test
    void deleteMultipleAndVerifyCount() {
        var d2 = repository.save(Division.builder().name("D2").build());
        var d3 = repository.save(Division.builder().name("D3").build());
        em.flush();
        repository.delete(d2);
        repository.delete(d3);
        repository.delete(saved);
        em.flush();
        assertEquals(0, repository.count());
    }

    @Test
    void saveNullObject() {
        assertThrows(Exception.class, () -> repository.save(null));
    }

    @Test
    void saveWithBlankName() {
        var div = repository.save(Division.builder().name("").build());
        em.flush();
        assertNotNull(div.getId());
        assertEquals("", repository.findById(div.getId()).orElseThrow().getName());
    }

    @Test
    void saveMultipleAndVerifyEachField() {
        var d2 = repository.save(Division.builder().name("Second").build());
        var d3 = repository.save(Division.builder().name("Third").build());
        em.flush();
        em.clear();
        assertEquals("Second", repository.findById(d2.getId()).orElseThrow().getName());
        assertEquals("Third", repository.findById(d3.getId()).orElseThrow().getName());
    }

    @Test
    void saveAndVerifyReturnedEntity() {
        var div = Division.builder().name("Returned").build();
        var result = repository.save(div);
        assertNotNull(result.getId());
        assertEquals("Returned", result.getName());
    }

    @Test
    void saveUpdateThenCount() {
        repository.save(Division.builder().name("Extra").build());
        em.flush();
        assertEquals(2, repository.count());
    }

    @Test
    void saveThenFindAllCheckExactCount() {
        repository.save(Division.builder().name("ExactCount").build());
        em.flush();
        assertEquals(2, repository.findAll().size());
    }

    @Test
    void findByIdWithNull() {
        assertThrows(Exception.class, () -> repository.findById(null));
    }

    @Test
    void saveAndFindAfterClear() {
        em.clear();
        var found = repository.findById(saved.getId());
        assertTrue(found.isPresent());
        assertEquals("Test Division", found.get().getName());
    }

    @Test
    void updateThenFindAllCount() {
        var div = repository.findById(saved.getId()).orElseThrow();
        div.setName("Changed");
        repository.save(div);
        em.flush();
        assertEquals(1, repository.count());
    }
}
