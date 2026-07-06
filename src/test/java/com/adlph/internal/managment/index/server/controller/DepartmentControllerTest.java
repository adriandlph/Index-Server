package com.adlph.internal.managment.index.server.controller;

import com.adlph.internal.managment.index.server.data.entity.Department;
import com.adlph.internal.managment.index.server.data.entity.Division;
import com.adlph.internal.managment.index.server.data.vo.DepartmentVO;
import com.adlph.internal.managment.index.server.exception.InvalidDataException;
import com.adlph.internal.managment.index.server.exception.ServerErrorException;
import com.adlph.internal.managment.index.server.repository.DepartmentRepository;
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
class DepartmentControllerTest {

    @Mock private DepartmentRepository departmentRepository;
    @Mock private DivisionRepository divisionRepository;
    @InjectMocks private DepartmentController controller;

    private Division division;
    private Department department;

    @BeforeEach
    void setUp() {
        division = Division.builder().id(1L).name("Div").build();
        department = Department.builder().id(1L).name("Dept").division(division).build();
    }

    @Nested
    class FindAll {
        @Test
        void noFilters() throws Exception {
            when(departmentRepository.findAll(null, null, null)).thenReturn(List.of(department));
            var r = controller.findAllDepartments(null, null, null);
            assertEquals(1, r.size());
        }

        @Test
        void filterByDivision() throws Exception {
            when(departmentRepository.findAll(1L, null, null)).thenReturn(List.of(department));
            var r = controller.findAllDepartments(1L, null, null);
            assertEquals(1, r.size());
        }

        @Test
        void withPagination() throws Exception {
            when(departmentRepository.findAll(null, 10, 0)).thenReturn(List.of(department));
            var r = controller.findAllDepartments(null, 10, 0);
            assertEquals(1, r.size());
        }

        @Test
        void empty() throws Exception {
            when(departmentRepository.findAll(any(), any(), any())).thenReturn(List.of());
            var r = controller.findAllDepartments(99L, null, null);
            assertTrue(r.isEmpty());
        }

        @Test
        void filterByDivisionWithPagination() throws Exception {
            when(departmentRepository.findAll(1L, 10, 0)).thenReturn(List.of(department));
            var r = controller.findAllDepartments(1L, 10, 0);
            assertEquals(1, r.size());
        }

        @Test
        void negativeCount() throws Exception {
            when(departmentRepository.findAll(null, -1, 0)).thenReturn(List.of());
            var r = controller.findAllDepartments(null, -1, 0);
            assertTrue(r.isEmpty());
        }

        @Test
        void nullPage() throws Exception {
            when(departmentRepository.findAll(null, 10, null)).thenReturn(List.of(department));
            var r = controller.findAllDepartments(null, 10, null);
            assertEquals(1, r.size());
        }

        @Test
        void repositoryThrows() {
            when(departmentRepository.findAll(any(), any(), any())).thenThrow(RuntimeException.class);
            assertThrows(ServerErrorException.class,
                () -> controller.findAllDepartments(1L, 10, 0));
        }

        @Test
        void negativeDivisionId() throws Exception {
            when(departmentRepository.findAll(-1L, null, null)).thenReturn(List.of());
            var r = controller.findAllDepartments(-1L, null, null);
            assertTrue(r.isEmpty());
        }

        @Test
        void multipleResults() throws Exception {
            var d2 = Department.builder().id(2L).name("Second").division(division).build();
            when(departmentRepository.findAll(null, 10, 0)).thenReturn(List.of(department, d2));
            var r = controller.findAllDepartments(null, 10, 0);
            assertEquals(2, r.size());
        }

        @Test
        void zeroCount() throws Exception {
            when(departmentRepository.findAll(null, 0, 0)).thenReturn(List.of());
            var r = controller.findAllDepartments(null, 0, 0);
            assertTrue(r.isEmpty());
        }

        @Test
        void negativePage() throws Exception {
            when(departmentRepository.findAll(null, 10, -1)).thenReturn(List.of());
            var r = controller.findAllDepartments(null, 10, -1);
            assertTrue(r.isEmpty());
        }

        @Test
        void pageWithoutCount() throws Exception {
            when(departmentRepository.findAll(null, null, 2)).thenReturn(List.of(department));
            var r = controller.findAllDepartments(null, null, 2);
            assertEquals(1, r.size());
        }

        @Test
        void filterByDivisionEmpty() throws Exception {
            when(departmentRepository.findAll(99L, null, null)).thenReturn(List.of());
            var r = controller.findAllDepartments(99L, null, null);
            assertTrue(r.isEmpty());
        }
    }

    @Nested
    class Count {
        @Test
        void noFilter() throws Exception {
            when(departmentRepository.count(null)).thenReturn(5L);
            assertEquals(5L, controller.countDepartments(null));
        }

        @Test
        void withDivisionFilter() throws Exception {
            when(departmentRepository.count(1L)).thenReturn(3L);
            assertEquals(3L, controller.countDepartments(1L));
        }

        @Test
        void zero() throws Exception {
            when(departmentRepository.count(99L)).thenReturn(0L);
            assertEquals(0L, controller.countDepartments(99L));
        }

        @Test
        void repositoryThrows() {
            when(departmentRepository.count(any())).thenThrow(RuntimeException.class);
            assertThrows(ServerErrorException.class,
                () -> controller.countDepartments(1L));
        }

        @Test
        void countAllReturnsTen() throws Exception {
            when(departmentRepository.count(null)).thenReturn(10L);
            assertEquals(10L, controller.countDepartments(null));
        }

        @Test
        void countWithDivisionZero() throws Exception {
            when(departmentRepository.count(99L)).thenReturn(0L);
            assertEquals(0L, controller.countDepartments(99L));
        }

        @Test
        void countNullDivision() throws Exception {
            when(departmentRepository.count(null)).thenReturn(7L);
            assertEquals(7L, controller.countDepartments(null));
        }
    }

    @Nested
    class GetById {
        @Test
        void exists() throws Exception {
            when(departmentRepository.findById(1L)).thenReturn(Optional.of(department));
            var r = controller.getDepartmentById(DepartmentVO.builder().id(1L).build());
            assertEquals(1L, r.getId());
        }

        @Test
        void notExists() {
            when(departmentRepository.findById(99L)).thenReturn(Optional.empty());
            assertThrows(InvalidDataException.class,
                () -> controller.getDepartmentById(DepartmentVO.builder().id(99L).build()));
        }

        @Test
        void negativeId() {
            assertThrows(InvalidDataException.class,
                () -> controller.getDepartmentById(DepartmentVO.builder().id(-1L).build()));
        }

        @Test
        void zeroId() {
            assertThrows(InvalidDataException.class,
                () -> controller.getDepartmentById(DepartmentVO.builder().id(0L).build()));
        }

        @Test
        void nullId() {
            assertThrows(InvalidDataException.class,
                () -> controller.getDepartmentById(DepartmentVO.builder().id(null).build()));
        }

        @Test
        void repositoryThrows() {
            when(departmentRepository.findById(any())).thenThrow(RuntimeException.class);
            assertThrows(ServerErrorException.class,
                () -> controller.getDepartmentById(DepartmentVO.builder().id(1L).build()));
        }

        @Test
        void existsReturnsCorrectFields() throws Exception {
            when(departmentRepository.findById(1L)).thenReturn(Optional.of(department));
            var r = controller.getDepartmentById(DepartmentVO.builder().id(1L).build());
            assertEquals("Dept", r.getName());
            assertEquals(1L, r.getId());
            assertEquals(1L, r.getDivisionId());
        }

        @Test
        void largeId() {
            when(departmentRepository.findById(999999L)).thenReturn(Optional.of(department));
            assertDoesNotThrow(() -> controller.getDepartmentById(DepartmentVO.builder().id(999999L).build()));
        }
    }

    @Nested
    class Create {
        @Test
        void valid() throws Exception {
            when(divisionRepository.findById(1L)).thenReturn(Optional.of(division));
            when(departmentRepository.save(any())).thenReturn(department);
            var r = controller.createDepartment(DepartmentVO.builder().name("Dept").divisionId(1L).build());
            assertEquals("Dept", r.getName());
        }

        @Test
        void blankName() {
            assertThrows(InvalidDataException.class,
                () -> controller.createDepartment(DepartmentVO.builder().name("").divisionId(1L).build()));
        }

        @Test
        void nullDivision() {
            assertThrows(InvalidDataException.class,
                () -> controller.createDepartment(DepartmentVO.builder().name("Dept").divisionId(null).build()));
        }

        @Test
        void divisionNotExists() {
            when(divisionRepository.findById(99L)).thenReturn(Optional.empty());
            assertThrows(InvalidDataException.class,
                () -> controller.createDepartment(DepartmentVO.builder().name("Dept").divisionId(99L).build()));
        }

        @Test
        void nullName() {
            assertThrows(InvalidDataException.class,
                () -> controller.createDepartment(DepartmentVO.builder().name(null).divisionId(1L).build()));
        }

        @Test
        void repositoryThrowsOnSave() {
            when(divisionRepository.findById(1L)).thenReturn(Optional.of(division));
            when(departmentRepository.save(any())).thenThrow(RuntimeException.class);
            assertThrows(ServerErrorException.class,
                () -> controller.createDepartment(DepartmentVO.builder().name("Dept").divisionId(1L).build()));
        }

        @Test
        void repositoryThrowsOnFindParent() {
            when(divisionRepository.findById(any())).thenThrow(RuntimeException.class);
            assertThrows(ServerErrorException.class,
                () -> controller.createDepartment(DepartmentVO.builder().name("Dept").divisionId(1L).build()));
        }

        @Test
        void createCallsSave() throws Exception {
            when(divisionRepository.findById(1L)).thenReturn(Optional.of(division));
            when(departmentRepository.save(any())).thenReturn(department);
            controller.createDepartment(DepartmentVO.builder().name("Dept").divisionId(1L).build());
            verify(departmentRepository).save(any(Department.class));
        }

        @Test
        void createValidCheckFields() throws Exception {
            var saved = Department.builder().id(5L).name("NewDept").division(division).build();
            when(divisionRepository.findById(1L)).thenReturn(Optional.of(division));
            when(departmentRepository.save(any())).thenReturn(saved);
            var r = controller.createDepartment(DepartmentVO.builder().name("NewDept").divisionId(1L).build());
            assertEquals(5L, r.getId());
            assertEquals("NewDept", r.getName());
            assertEquals(1L, r.getDivisionId());
        }
    }

    @Nested
    class Update {
        @Test
        void valid() throws Exception {
            when(departmentRepository.findById(1L)).thenReturn(Optional.of(department));
            when(divisionRepository.findById(1L)).thenReturn(Optional.of(division));
            when(departmentRepository.save(any())).thenReturn(department);
            var r = controller.updateDepartment(DepartmentVO.builder().id(1L).name("Updated").divisionId(1L).build());
            assertNotNull(r);
        }

        @Test
        void notExists() {
            when(departmentRepository.findById(99L)).thenReturn(Optional.empty());
            assertThrows(InvalidDataException.class,
                () -> controller.updateDepartment(DepartmentVO.builder().id(99L).name("X").divisionId(1L).build()));
        }

        @Test
        void blankName() {
            when(departmentRepository.findById(1L)).thenReturn(Optional.of(department));
            assertThrows(InvalidDataException.class,
                () -> controller.updateDepartment(DepartmentVO.builder().id(1L).name("").divisionId(1L).build()));
        }

        @Test
        void nullName() {
            when(departmentRepository.findById(1L)).thenReturn(Optional.of(department));
            assertThrows(InvalidDataException.class,
                () -> controller.updateDepartment(DepartmentVO.builder().id(1L).name(null).divisionId(1L).build()));
        }

        @Test
        void nullDivisionId() throws Exception {
            when(departmentRepository.findById(1L)).thenReturn(Optional.of(department));
            when(departmentRepository.save(any())).thenReturn(department);
            var r = controller.updateDepartment(DepartmentVO.builder().id(1L).name("Updated").divisionId(null).build());
            assertNotNull(r);
        }

        @Test
        void divisionNotExists() {
            when(departmentRepository.findById(1L)).thenReturn(Optional.of(department));
            when(divisionRepository.findById(99L)).thenReturn(Optional.empty());
            assertThrows(InvalidDataException.class,
                () -> controller.updateDepartment(DepartmentVO.builder().id(1L).name("X").divisionId(99L).build()));
        }

        @Test
        void repositoryThrowsOnFind() {
            when(departmentRepository.findById(any())).thenThrow(RuntimeException.class);
            assertThrows(ServerErrorException.class,
                () -> controller.updateDepartment(DepartmentVO.builder().id(1L).name("X").divisionId(1L).build()));
        }

        @Test
        void repositoryThrowsOnSave() {
            when(departmentRepository.findById(1L)).thenReturn(Optional.of(department));
            when(divisionRepository.findById(1L)).thenReturn(Optional.of(division));
            when(departmentRepository.save(any())).thenThrow(RuntimeException.class);
            assertThrows(ServerErrorException.class,
                () -> controller.updateDepartment(DepartmentVO.builder().id(1L).name("X").divisionId(1L).build()));
        }

        @Test
        void updateValidCheckChangedFields() throws Exception {
            var updated = Department.builder().id(1L).name("Changed").division(division).build();
            when(departmentRepository.findById(1L)).thenReturn(Optional.of(department));
            when(divisionRepository.findById(1L)).thenReturn(Optional.of(division));
            when(departmentRepository.save(any())).thenReturn(updated);
            var r = controller.updateDepartment(DepartmentVO.builder().id(1L).name("Changed").divisionId(1L).build());
            assertEquals("Changed", r.getName());
        }

        @Test
        void negativeId() {
            assertThrows(InvalidDataException.class,
                () -> controller.updateDepartment(DepartmentVO.builder().id(-1L).name("X").divisionId(1L).build()));
        }

        @Test
        void zeroId() {
            assertThrows(InvalidDataException.class,
                () -> controller.updateDepartment(DepartmentVO.builder().id(0L).name("X").divisionId(1L).build()));
        }

        @Test
        void nullId() {
            assertThrows(InvalidDataException.class,
                () -> controller.updateDepartment(DepartmentVO.builder().id(null).name("X").divisionId(1L).build()));
        }
    }

    @Nested
    class Delete {
        @Test
        void exists() throws Exception {
            when(departmentRepository.findById(1L)).thenReturn(Optional.of(department));
            assertDoesNotThrow(() -> controller.deleteDepartment(DepartmentVO.builder().id(1L).build()));
            verify(departmentRepository).delete(department);
        }

        @Test
        void notExists() {
            when(departmentRepository.findById(99L)).thenReturn(Optional.empty());
            assertThrows(InvalidDataException.class,
                () -> controller.deleteDepartment(DepartmentVO.builder().id(99L).build()));
        }

        @Test
        void repositoryThrowsOnFind() {
            when(departmentRepository.findById(any())).thenThrow(RuntimeException.class);
            assertThrows(ServerErrorException.class,
                () -> controller.deleteDepartment(DepartmentVO.builder().id(1L).build()));
        }

        @Test
        void repositoryThrowsOnDelete() {
            when(departmentRepository.findById(1L)).thenReturn(Optional.of(department));
            doThrow(RuntimeException.class).when(departmentRepository).delete(any());
            assertThrows(ServerErrorException.class,
                () -> controller.deleteDepartment(DepartmentVO.builder().id(1L).build()));
        }

        @Test
        void nullId() {
            assertThrows(InvalidDataException.class,
                () -> controller.deleteDepartment(DepartmentVO.builder().id(null).build()));
        }

        @Test
        void zeroId() {
            assertThrows(InvalidDataException.class,
                () -> controller.deleteDepartment(DepartmentVO.builder().id(0L).build()));
        }

        @Test
        void negativeId() {
            assertThrows(InvalidDataException.class,
                () -> controller.deleteDepartment(DepartmentVO.builder().id(-1L).build()));
        }

        @Test
        void deleteVerifiesCorrectEntity() throws Exception {
            when(departmentRepository.findById(1L)).thenReturn(Optional.of(department));
            doNothing().when(departmentRepository).delete(any());
            controller.deleteDepartment(DepartmentVO.builder().id(1L).build());
            verify(departmentRepository).delete(department);
            verify(departmentRepository, times(1)).delete(any(Department.class));
        }
    }
}
