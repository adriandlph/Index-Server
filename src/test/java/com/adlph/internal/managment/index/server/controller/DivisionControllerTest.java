package com.adlph.internal.managment.index.server.controller;

import com.adlph.internal.managment.index.server.data.entity.Division;
import com.adlph.internal.managment.index.server.data.vo.DivisionVO;
import com.adlph.internal.managment.index.server.exception.InvalidDataException;
import com.adlph.internal.managment.index.server.exception.ServerErrorException;
import com.adlph.internal.managment.index.server.repository.DivisionRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DivisionControllerTest {

    @Mock private DivisionRepository divisionRepository;
    @InjectMocks private DivisionController controller;

    private Division division;

    @BeforeEach
    void setUp() {
        division = Division.builder().id(1L).name("Test Division").build();
    }

    @Nested
    class FindAll {
        @Test
        void noPagination() throws Exception {
            when(divisionRepository.findAll(null, null)).thenReturn(List.of(division));
            var result = controller.findAllDivisions(null, null);
            assertEquals(1, result.size());
            assertEquals("Test Division", result.getFirst().getName());
        }

        @Test
        void withPagination() throws Exception {
            when(divisionRepository.findAll(10, 0)).thenReturn(List.of(division));
            var result = controller.findAllDivisions(10, 0);
            assertEquals(1, result.size());
        }

        @Test
        void negativeCount() throws Exception {
            when(divisionRepository.findAll(-1, 0)).thenReturn(List.of());
            var result = controller.findAllDivisions(-1, 0);
            assertTrue(result.isEmpty());
        }

        @Test
        void nullPage() throws Exception {
            when(divisionRepository.findAll(10, null)).thenReturn(List.of(division));
            var result = controller.findAllDivisions(10, null);
            assertEquals(1, result.size());
        }

        @Test
        void repositoryThrows() {
            when(divisionRepository.findAll(any(), any())).thenThrow(RuntimeException.class);
            assertThrows(ServerErrorException.class, () -> controller.findAllDivisions(1, 0));
        }

        @Test
        void emptyResults() throws Exception {
            when(divisionRepository.findAll(10, 0)).thenReturn(List.of());
            var result = controller.findAllDivisions(10, 0);
            assertTrue(result.isEmpty());
        }

        @Test
        void negativePage() throws Exception {
            when(divisionRepository.findAll(10, -1)).thenReturn(List.of());
            var result = controller.findAllDivisions(10, -1);
            assertTrue(result.isEmpty());
        }

        @Test
        void zeroCount() throws Exception {
            when(divisionRepository.findAll(0, 0)).thenReturn(List.of());
            var result = controller.findAllDivisions(0, 0);
            assertTrue(result.isEmpty());
        }

        @Test
        void pageWithoutCount() throws Exception {
            when(divisionRepository.findAll(null, 2)).thenReturn(List.of(division));
            var result = controller.findAllDivisions(null, 2);
            assertEquals(1, result.size());
        }

        @Test
        void zeroPage() throws Exception {
            when(divisionRepository.findAll(5, 0)).thenReturn(List.of(division));
            var result = controller.findAllDivisions(5, 0);
            assertEquals(1, result.size());
        }

        @Test
        void multipleResults() throws Exception {
            var d2 = Division.builder().id(2L).name("Second").build();
            when(divisionRepository.findAll(10, 0)).thenReturn(List.of(division, d2));
            var result = controller.findAllDivisions(10, 0);
            assertEquals(2, result.size());
        }

        @Test
        void countOnly() throws Exception {
            when(divisionRepository.findAll(5, null)).thenReturn(List.of(division));
            var result = controller.findAllDivisions(5, null);
            assertEquals(1, result.size());
        }
    }

    @Nested
    class Count {
        @Test
        void countDivisions() throws Exception {
            when(divisionRepository.count()).thenReturn(42L);
            assertEquals(42L, controller.countDivisions());
        }

        @Test
        void countZero() throws Exception {
            when(divisionRepository.count()).thenReturn(0L);
            assertEquals(0L, controller.countDivisions());
        }

        @Test
        void repositoryThrows() {
            when(divisionRepository.count()).thenThrow(RuntimeException.class);
            assertThrows(ServerErrorException.class, () -> controller.countDivisions());
        }

        @Test
        void countFive() throws Exception {
            when(divisionRepository.count()).thenReturn(5L);
            assertEquals(5L, controller.countDivisions());
        }

        @Test
        void countHundred() throws Exception {
            when(divisionRepository.count()).thenReturn(100L);
            assertEquals(100L, controller.countDivisions());
        }
    }

    @Nested
    class GetById {
        @Test
        void exists() throws Exception {
            when(divisionRepository.findById(1L)).thenReturn(Optional.of(division));
            var result = controller.getDivisionById(DivisionVO.builder().id(1L).build());
            assertEquals(1L, result.getId());
        }

        @Test
        void notExists() {
            when(divisionRepository.findById(99L)).thenReturn(Optional.empty());
            assertThrows(InvalidDataException.class,
                () -> controller.getDivisionById(DivisionVO.builder().id(99L).build()));
        }

        @Test
        void negativeId() {
            assertThrows(InvalidDataException.class,
                () -> controller.getDivisionById(DivisionVO.builder().id(-1L).build()));
        }

        @Test
        void repositoryThrows() {
            when(divisionRepository.findById(any())).thenThrow(RuntimeException.class);
            assertThrows(ServerErrorException.class,
                () -> controller.getDivisionById(DivisionVO.builder().id(1L).build()));
        }

        @Test
        void nullId() {
            assertThrows(InvalidDataException.class,
                () -> controller.getDivisionById(DivisionVO.builder().id(null).build()));
        }

        @Test
        void zeroId() {
            assertThrows(InvalidDataException.class,
                () -> controller.getDivisionById(DivisionVO.builder().id(0L).build()));
        }

        @Test
        void largeId() {
            when(divisionRepository.findById(999999L)).thenReturn(Optional.of(division));
            assertDoesNotThrow(() -> controller.getDivisionById(DivisionVO.builder().id(999999L).build()));
        }

        @Test
        void existsReturnsCorrectFields() throws Exception {
            when(divisionRepository.findById(1L)).thenReturn(Optional.of(division));
            var result = controller.getDivisionById(DivisionVO.builder().id(1L).build());
            assertEquals("Test Division", result.getName());
            assertEquals(1L, result.getId());
        }
    }

    @Nested
    class Create {
        @Test
        void valid() throws Exception {
            var newDiv = Division.builder().id(2L).name("New").build();
            when(divisionRepository.save(any())).thenReturn(newDiv);
            var result = controller.createDivision(DivisionVO.builder().name("New").build());
            assertEquals("New", result.getName());
        }

        @Test
        void blankName() {
            assertThrows(InvalidDataException.class,
                () -> controller.createDivision(DivisionVO.builder().name("").build()));
        }

        @Test
        void nullName() {
            assertThrows(InvalidDataException.class,
                () -> controller.createDivision(DivisionVO.builder().name(null).build()));
        }

        @Test
        void repositoryThrowsOnSave() {
            when(divisionRepository.save(any())).thenThrow(RuntimeException.class);
            assertThrows(ServerErrorException.class,
                () -> controller.createDivision(DivisionVO.builder().name("Valid").build()));
        }

        @Test
        void createValidCheckFields() throws Exception {
            var saved = Division.builder().id(5L).name("Created").build();
            when(divisionRepository.save(any())).thenReturn(saved);
            var result = controller.createDivision(DivisionVO.builder().name("Created").build());
            assertEquals(5L, result.getId());
            assertEquals("Created", result.getName());
        }

        @Test
        void repositoryThrowsIllegalState() {
            when(divisionRepository.save(any())).thenThrow(IllegalStateException.class);
            assertThrows(ServerErrorException.class,
                () -> controller.createDivision(DivisionVO.builder().name("Valid").build()));
        }

        @Test
        void createCallsSave() throws Exception {
            var saved = Division.builder().id(3L).name("Saved").build();
            when(divisionRepository.save(any())).thenReturn(saved);
            controller.createDivision(DivisionVO.builder().name("Saved").build());
            verify(divisionRepository).save(any(Division.class));
        }

        @Test
        void createWithNameWithSpaces() throws Exception {
            var saved = Division.builder().id(6L).name("  Spaces  ").build();
            when(divisionRepository.save(any())).thenReturn(saved);
            var result = controller.createDivision(DivisionVO.builder().name("  Spaces  ").build());
            assertEquals("  Spaces  ", result.getName());
        }

        @Test
        void createValidReturnsId() throws Exception {
            var saved = Division.builder().id(10L).name("WithId").build();
            when(divisionRepository.save(any())).thenReturn(saved);
            var result = controller.createDivision(DivisionVO.builder().name("WithId").build());
            assertNotNull(result.getId());
            assertEquals(10L, result.getId());
        }
    }

    @Nested
    class Update {
        @Test
        void valid() throws Exception {
            var existing = Division.builder().id(1L).name("Original").build();
            var saved = Division.builder().id(1L).name("Updated").build();
            when(divisionRepository.findById(1L)).thenReturn(Optional.of(existing));
            when(divisionRepository.save(any())).thenReturn(saved);
            var result = controller.updateDivision(DivisionVO.builder().id(1L).name("Updated").build());
            assertEquals("Updated", result.getName());
        }

        @Test
        void notExists() {
            when(divisionRepository.findById(99L)).thenReturn(Optional.empty());
            assertThrows(InvalidDataException.class,
                () -> controller.updateDivision(DivisionVO.builder().id(99L).name("X").build()));
        }

        @Test
        void blankName() {
            assertThrows(InvalidDataException.class,
                () -> controller.updateDivision(DivisionVO.builder().id(1L).name("").build()));
        }

        @Test
        void nullName() {
            assertThrows(InvalidDataException.class,
                () -> controller.updateDivision(DivisionVO.builder().id(1L).name(null).build()));
        }

        @Test
        void repositoryThrowsOnFind() {
            when(divisionRepository.findById(any())).thenThrow(RuntimeException.class);
            assertThrows(ServerErrorException.class,
                () -> controller.updateDivision(DivisionVO.builder().id(1L).name("X").build()));
        }

        @Test
        void repositoryThrowsOnSave() {
            var existing = Division.builder().id(1L).name("Original").build();
            when(divisionRepository.findById(1L)).thenReturn(Optional.of(existing));
            when(divisionRepository.save(any())).thenThrow(RuntimeException.class);
            assertThrows(ServerErrorException.class,
                () -> controller.updateDivision(DivisionVO.builder().id(1L).name("X").build()));
        }

        @Test
        void nullId() {
            assertThrows(InvalidDataException.class,
                () -> controller.updateDivision(DivisionVO.builder().id(null).name("X").build()));
        }

        @Test
        void zeroId() {
            assertThrows(InvalidDataException.class,
                () -> controller.updateDivision(DivisionVO.builder().id(0L).name("X").build()));
        }

        @Test
        void negativeId() {
            assertThrows(InvalidDataException.class,
                () -> controller.updateDivision(DivisionVO.builder().id(-1L).name("X").build()));
        }

        @Test
        void updateValidCheckName() throws Exception {
            var existing = Division.builder().id(1L).name("Original").build();
            var saved = Division.builder().id(1L).name("Changed").build();
            when(divisionRepository.findById(1L)).thenReturn(Optional.of(existing));
            when(divisionRepository.save(any())).thenReturn(saved);
            var result = controller.updateDivision(DivisionVO.builder().id(1L).name("Changed").build());
            assertEquals("Changed", result.getName());
            assertNotEquals("Original", result.getName());
        }
    }

    @Nested
    class Delete {
        @Test
        void exists() throws Exception {
            when(divisionRepository.findById(1L)).thenReturn(Optional.of(division));
            doNothing().when(divisionRepository).delete(any());
            assertDoesNotThrow(() -> controller.deleteDivision(DivisionVO.builder().id(1L).build()));
            verify(divisionRepository).delete(division);
        }

        @Test
        void notExists() {
            when(divisionRepository.findById(99L)).thenReturn(Optional.empty());
            assertThrows(InvalidDataException.class,
                () -> controller.deleteDivision(DivisionVO.builder().id(99L).build()));
        }

        @Test
        void repositoryThrowsOnDelete() {
            when(divisionRepository.findById(1L)).thenReturn(Optional.of(division));
            doThrow(RuntimeException.class).when(divisionRepository).delete(any());
            assertThrows(ServerErrorException.class,
                () -> controller.deleteDivision(DivisionVO.builder().id(1L).build()));
        }

        @Test
        void repositoryThrowsOnFind() {
            when(divisionRepository.findById(any())).thenThrow(RuntimeException.class);
            assertThrows(ServerErrorException.class,
                () -> controller.deleteDivision(DivisionVO.builder().id(1L).build()));
        }

        @Test
        void nullId() {
            assertThrows(InvalidDataException.class,
                () -> controller.deleteDivision(DivisionVO.builder().id(null).build()));
        }

        @Test
        void zeroId() {
            assertThrows(InvalidDataException.class,
                () -> controller.deleteDivision(DivisionVO.builder().id(0L).build()));
        }

        @Test
        void negativeId() {
            assertThrows(InvalidDataException.class,
                () -> controller.deleteDivision(DivisionVO.builder().id(-1L).build()));
        }

        @Test
        void deleteVerifiesCorrectEntity() throws Exception {
            when(divisionRepository.findById(1L)).thenReturn(Optional.of(division));
            doNothing().when(divisionRepository).delete(any());
            controller.deleteDivision(DivisionVO.builder().id(1L).build());
            verify(divisionRepository).delete(division);
            verify(divisionRepository, times(1)).delete(any(Division.class));
        }
    }
}
