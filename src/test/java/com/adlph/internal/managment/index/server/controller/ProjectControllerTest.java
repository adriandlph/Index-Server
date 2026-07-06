package com.adlph.internal.managment.index.server.controller;

import com.adlph.internal.managment.index.server.data.entity.Department;
import com.adlph.internal.managment.index.server.data.entity.Division;
import com.adlph.internal.managment.index.server.data.entity.Project;
import com.adlph.internal.managment.index.server.data.vo.ProjectVO;
import com.adlph.internal.managment.index.server.exception.InvalidDataException;
import com.adlph.internal.managment.index.server.exception.ServerErrorException;
import com.adlph.internal.managment.index.server.repository.DepartmentRepository;
import com.adlph.internal.managment.index.server.repository.ProjectRepository;

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
class ProjectControllerTest {

    @Mock private ProjectRepository projectRepository;
    @Mock private DepartmentRepository departmentRepository;
    @InjectMocks private ProjectController controller;

    private Division division;
    private Department department;
    private Project project;

    @BeforeEach
    void setUp() {
        division = Division.builder().id(1L).name("Div").build();
        department = Department.builder().id(1L).name("Dept").division(division).build();
        project = Project.builder().id(1L).name("Proj").department(department).build();
    }

    @Nested
    class FindAll {
        @Test
        void noFilters() throws Exception {
            when(projectRepository.findAll(null, null, null, null)).thenReturn(List.of(project));
            var r = controller.findAllProjects(null, null, null, null);
            assertEquals(1, r.size());
        }

        @Test
        void filterByDepartment() throws Exception {
            when(projectRepository.findAll(null, 1L, null, null)).thenReturn(List.of(project));
            var r = controller.findAllProjects(null, 1L, null, null);
            assertEquals(1, r.size());
        }

        @Test
        void filterByDivision() throws Exception {
            when(projectRepository.findAll(1L, null, null, null)).thenReturn(List.of(project));
            var r = controller.findAllProjects(1L, null, null, null);
            assertEquals(1, r.size());
        }

        @Test
        void empty() throws Exception {
            when(projectRepository.findAll(any(), any(), any(), any())).thenReturn(List.of());
            var r = controller.findAllProjects(99L, null, null, null);
            assertTrue(r.isEmpty());
        }

        @Test
        void filterByDepartmentWithPagination() throws Exception {
            when(projectRepository.findAll(null, 1L, 10, 0)).thenReturn(List.of(project));
            var r = controller.findAllProjects(null, 1L, 10, 0);
            assertEquals(1, r.size());
        }

        @Test
        void filterByDivisionWithPagination() throws Exception {
            when(projectRepository.findAll(1L, null, 10, 0)).thenReturn(List.of(project));
            var r = controller.findAllProjects(1L, null, 10, 0);
            assertEquals(1, r.size());
        }

        @Test
        void filterByBoth() throws Exception {
            when(projectRepository.findAll(1L, 1L, null, null)).thenReturn(List.of(project));
            var r = controller.findAllProjects(1L, 1L, null, null);
            assertEquals(1, r.size());
        }

        @Test
        void negativeCount() throws Exception {
            when(projectRepository.findAll(null, null, -1, 0)).thenReturn(List.of());
            var r = controller.findAllProjects(null, null, -1, 0);
            assertTrue(r.isEmpty());
        }

        @Test
        void nullPage() throws Exception {
            when(projectRepository.findAll(null, null, 10, null)).thenReturn(List.of(project));
            var r = controller.findAllProjects(null, null, 10, null);
            assertEquals(1, r.size());
        }

        @Test
        void repositoryThrows() {
            when(projectRepository.findAll(any(), any(), any(), any())).thenThrow(RuntimeException.class);
            assertThrows(ServerErrorException.class,
                () -> controller.findAllProjects(1L, 1L, 10, 0));
        }

        @Test
        void multipleResults() throws Exception {
            var p2 = Project.builder().id(2L).name("Second").department(department).build();
            when(projectRepository.findAll(null, null, 10, 0)).thenReturn(List.of(project, p2));
            var r = controller.findAllProjects(null, null, 10, 0);
            assertEquals(2, r.size());
        }

        @Test
        void zeroCount() throws Exception {
            when(projectRepository.findAll(null, null, 0, 0)).thenReturn(List.of());
            var r = controller.findAllProjects(null, null, 0, 0);
            assertTrue(r.isEmpty());
        }

        @Test
        void negativePage() throws Exception {
            when(projectRepository.findAll(null, null, 10, -1)).thenReturn(List.of());
            var r = controller.findAllProjects(null, null, 10, -1);
            assertTrue(r.isEmpty());
        }

        @Test
        void pageWithoutCount() throws Exception {
            when(projectRepository.findAll(null, null, null, 2)).thenReturn(List.of(project));
            var r = controller.findAllProjects(null, null, null, 2);
            assertEquals(1, r.size());
        }

        @Test
        void filterByDepartmentEmpty() throws Exception {
            when(projectRepository.findAll(null, 99L, null, null)).thenReturn(List.of());
            var r = controller.findAllProjects(null, 99L, null, null);
            assertTrue(r.isEmpty());
        }
    }

    @Nested
    class Count {
        @Test
        void noFilter() throws Exception {
            when(projectRepository.count(null, null)).thenReturn(5L);
            assertEquals(5L, controller.countProjects(null, null));
        }

        @Test
        void withDepartmentFilter() throws Exception {
            when(projectRepository.count(null, 1L)).thenReturn(3L);
            assertEquals(3L, controller.countProjects(null, 1L));
        }

        @Test
        void withDivisionFilter() throws Exception {
            when(projectRepository.count(1L, null)).thenReturn(2L);
            assertEquals(2L, controller.countProjects(1L, null));
        }

        @Test
        void countBothFilters() throws Exception {
            when(projectRepository.count(1L, 1L)).thenReturn(3L);
            assertEquals(3L, controller.countProjects(1L, 1L));
        }

        @Test
        void countZero() throws Exception {
            when(projectRepository.count(99L, null)).thenReturn(0L);
            assertEquals(0L, controller.countProjects(99L, null));
        }

        @Test
        void repositoryThrows() {
            when(projectRepository.count(any(), any())).thenThrow(RuntimeException.class);
            assertThrows(ServerErrorException.class,
                () -> controller.countProjects(1L, 1L));
        }

        @Test
        void countReturnsTen() throws Exception {
            when(projectRepository.count(null, null)).thenReturn(10L);
            assertEquals(10L, controller.countProjects(null, null));
        }

        @Test
        void countWithDepartmentZero() throws Exception {
            when(projectRepository.count(null, 99L)).thenReturn(0L);
            assertEquals(0L, controller.countProjects(null, 99L));
        }

        @Test
        void countWithDivisionReturnsFive() throws Exception {
            when(projectRepository.count(1L, null)).thenReturn(5L);
            assertEquals(5L, controller.countProjects(1L, null));
        }
    }

    @Nested
    class GetById {
        @Test
        void exists() throws Exception {
            when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
            var r = controller.getProjectById(ProjectVO.builder().id(1L).build());
            assertEquals(1L, r.getId());
        }

        @Test
        void notExists() {
            when(projectRepository.findById(99L)).thenReturn(Optional.empty());
            assertThrows(InvalidDataException.class,
                () -> controller.getProjectById(ProjectVO.builder().id(99L).build()));
        }

        @Test
        void negativeId() {
            assertThrows(InvalidDataException.class,
                () -> controller.getProjectById(ProjectVO.builder().id(-1L).build()));
        }

        @Test
        void zeroId() {
            assertThrows(InvalidDataException.class,
                () -> controller.getProjectById(ProjectVO.builder().id(0L).build()));
        }

        @Test
        void nullId() {
            assertThrows(InvalidDataException.class,
                () -> controller.getProjectById(ProjectVO.builder().id(null).build()));
        }

        @Test
        void repositoryThrows() {
            when(projectRepository.findById(any())).thenThrow(RuntimeException.class);
            assertThrows(ServerErrorException.class,
                () -> controller.getProjectById(ProjectVO.builder().id(1L).build()));
        }

        @Test
        void existsReturnsCorrectFields() throws Exception {
            when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
            var r = controller.getProjectById(ProjectVO.builder().id(1L).build());
            assertEquals("Proj", r.getName());
            assertEquals(1L, r.getId());
            assertEquals(1L, r.getDepartmentId());
        }

        @Test
        void largeId() {
            when(projectRepository.findById(999999L)).thenReturn(Optional.of(project));
            assertDoesNotThrow(() -> controller.getProjectById(ProjectVO.builder().id(999999L).build()));
        }
    }

    @Nested
    class Create {
        @Test
        void valid() throws Exception {
            when(departmentRepository.findById(1L)).thenReturn(Optional.of(department));
            when(projectRepository.save(any())).thenReturn(project);
            var r = controller.createProject(ProjectVO.builder().name("Proj").departmentId(1L).build());
            assertNotNull(r);
        }

        @Test
        void blankName() {
            assertThrows(InvalidDataException.class,
                () -> controller.createProject(ProjectVO.builder().name("").departmentId(1L).build()));
        }

        @Test
        void nullDepartment() {
            assertThrows(InvalidDataException.class,
                () -> controller.createProject(ProjectVO.builder().name("Proj").departmentId(null).build()));
        }

        @Test
        void departmentNotExists() {
            when(departmentRepository.findById(99L)).thenReturn(Optional.empty());
            assertThrows(InvalidDataException.class,
                () -> controller.createProject(ProjectVO.builder().name("Proj").departmentId(99L).build()));
        }

        @Test
        void nullName() {
            assertThrows(InvalidDataException.class,
                () -> controller.createProject(ProjectVO.builder().name(null).departmentId(1L).build()));
        }

        @Test
        void repositoryThrowsOnSave() {
            when(departmentRepository.findById(1L)).thenReturn(Optional.of(department));
            when(projectRepository.save(any())).thenThrow(RuntimeException.class);
            assertThrows(ServerErrorException.class,
                () -> controller.createProject(ProjectVO.builder().name("Proj").departmentId(1L).build()));
        }

        @Test
        void repositoryThrowsOnFindParent() {
            when(departmentRepository.findById(any())).thenThrow(RuntimeException.class);
            assertThrows(ServerErrorException.class,
                () -> controller.createProject(ProjectVO.builder().name("Proj").departmentId(1L).build()));
        }

        @Test
        void createCallsSave() throws Exception {
            when(departmentRepository.findById(1L)).thenReturn(Optional.of(department));
            when(projectRepository.save(any())).thenReturn(project);
            controller.createProject(ProjectVO.builder().name("Proj").departmentId(1L).build());
            verify(projectRepository).save(any(Project.class));
        }

        @Test
        void createValidCheckFields() throws Exception {
            var saved = Project.builder().id(5L).name("NewProj").department(department).build();
            when(departmentRepository.findById(1L)).thenReturn(Optional.of(department));
            when(projectRepository.save(any())).thenReturn(saved);
            var r = controller.createProject(ProjectVO.builder().name("NewProj").departmentId(1L).build());
            assertEquals(5L, r.getId());
            assertEquals("NewProj", r.getName());
            assertEquals(1L, r.getDepartmentId());
        }
    }

    @Nested
    class Update {
        @Test
        void valid() throws Exception {
            when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
            when(departmentRepository.findById(1L)).thenReturn(Optional.of(department));
            when(projectRepository.save(any())).thenReturn(project);
            var r = controller.updateProject(ProjectVO.builder().id(1L).name("Updated").departmentId(1L).build());
            assertNotNull(r);
        }

        @Test
        void notExists() {
            when(projectRepository.findById(99L)).thenReturn(Optional.empty());
            assertThrows(InvalidDataException.class,
                () -> controller.updateProject(ProjectVO.builder().id(99L).name("X").departmentId(1L).build()));
        }

        @Test
        void blankName() {
            when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
            assertThrows(InvalidDataException.class,
                () -> controller.updateProject(ProjectVO.builder().id(1L).name("").departmentId(1L).build()));
        }

        @Test
        void nullName() {
            when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
            assertThrows(InvalidDataException.class,
                () -> controller.updateProject(ProjectVO.builder().id(1L).name(null).departmentId(1L).build()));
        }

        @Test
        void nullDepartmentId() throws Exception {
            when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
            when(projectRepository.save(any())).thenReturn(project);
            var r = controller.updateProject(ProjectVO.builder().id(1L).name("Updated").departmentId(null).build());
            assertNotNull(r);
        }

        @Test
        void departmentNotExists() {
            when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
            when(departmentRepository.findById(99L)).thenReturn(Optional.empty());
            assertThrows(InvalidDataException.class,
                () -> controller.updateProject(ProjectVO.builder().id(1L).name("X").departmentId(99L).build()));
        }

        @Test
        void repositoryThrowsOnFind() {
            when(projectRepository.findById(any())).thenThrow(RuntimeException.class);
            assertThrows(ServerErrorException.class,
                () -> controller.updateProject(ProjectVO.builder().id(1L).name("X").departmentId(1L).build()));
        }

        @Test
        void repositoryThrowsOnSave() {
            when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
            when(departmentRepository.findById(1L)).thenReturn(Optional.of(department));
            when(projectRepository.save(any())).thenThrow(RuntimeException.class);
            assertThrows(ServerErrorException.class,
                () -> controller.updateProject(ProjectVO.builder().id(1L).name("X").departmentId(1L).build()));
        }

        @Test
        void updateValidCheckChangedFields() throws Exception {
            var updated = Project.builder().id(1L).name("Changed").department(department).build();
            when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
            when(departmentRepository.findById(1L)).thenReturn(Optional.of(department));
            when(projectRepository.save(any())).thenReturn(updated);
            var r = controller.updateProject(ProjectVO.builder().id(1L).name("Changed").departmentId(1L).build());
            assertEquals("Changed", r.getName());
        }

        @Test
        void negativeId() {
            assertThrows(InvalidDataException.class,
                () -> controller.updateProject(ProjectVO.builder().id(-1L).name("X").departmentId(1L).build()));
        }

        @Test
        void zeroId() {
            assertThrows(InvalidDataException.class,
                () -> controller.updateProject(ProjectVO.builder().id(0L).name("X").departmentId(1L).build()));
        }

        @Test
        void nullId() {
            assertThrows(InvalidDataException.class,
                () -> controller.updateProject(ProjectVO.builder().id(null).name("X").departmentId(1L).build()));
        }
    }

    @Nested
    class Delete {
        @Test
        void exists() throws Exception {
            when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
            assertDoesNotThrow(() -> controller.deleteProject(ProjectVO.builder().id(1L).build()));
            verify(projectRepository).delete(project);
        }

        @Test
        void notExists() {
            when(projectRepository.findById(99L)).thenReturn(Optional.empty());
            assertThrows(InvalidDataException.class,
                () -> controller.deleteProject(ProjectVO.builder().id(99L).build()));
        }

        @Test
        void repositoryThrowsOnFind() {
            when(projectRepository.findById(any())).thenThrow(RuntimeException.class);
            assertThrows(ServerErrorException.class,
                () -> controller.deleteProject(ProjectVO.builder().id(1L).build()));
        }

        @Test
        void repositoryThrowsOnDelete() {
            when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
            doThrow(RuntimeException.class).when(projectRepository).delete(any());
            assertThrows(ServerErrorException.class,
                () -> controller.deleteProject(ProjectVO.builder().id(1L).build()));
        }

        @Test
        void nullId() {
            assertThrows(InvalidDataException.class,
                () -> controller.deleteProject(ProjectVO.builder().id(null).build()));
        }

        @Test
        void zeroId() {
            assertThrows(InvalidDataException.class,
                () -> controller.deleteProject(ProjectVO.builder().id(0L).build()));
        }

        @Test
        void negativeId() {
            assertThrows(InvalidDataException.class,
                () -> controller.deleteProject(ProjectVO.builder().id(-1L).build()));
        }

        @Test
        void deleteVerifiesCorrectEntity() throws Exception {
            when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
            doNothing().when(projectRepository).delete(any());
            controller.deleteProject(ProjectVO.builder().id(1L).build());
            verify(projectRepository).delete(project);
            verify(projectRepository, times(1)).delete(any(Project.class));
        }
    }
}
