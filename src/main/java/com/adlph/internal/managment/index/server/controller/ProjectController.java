package com.adlph.internal.managment.index.server.controller;

import com.adlph.internal.managment.index.server.data.entity.Department;
import com.adlph.internal.managment.index.server.data.entity.Project;
import com.adlph.internal.managment.index.server.data.vo.ProjectVO;
import com.adlph.internal.managment.index.server.exception.InvalidDataException;
import com.adlph.internal.managment.index.server.exception.ServerErrorException;
import com.adlph.internal.managment.index.server.repository.DepartmentRepository;
import com.adlph.internal.managment.index.server.repository.ProjectRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class ProjectController implements ProjectControllerInterface {

    private static final Logger LOG = LoggerFactory.getLogger(ProjectController.class);

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Override
    public List<ProjectVO> findAllProjects(Long divisionId, Long departmentId, Integer count, Integer page) throws ServerErrorException {
        LOG.debug("Finding all projects");
        try {
            return projectRepository.findAll(divisionId, departmentId, count, page).stream()
                .map(ProjectController::Project2ProjectVO)
                .toList();
        } catch (Exception e) {
            LOG.error("Error finding all projects", e);
            throw new ServerErrorException(-1, "Server error");
        }
    }

    @Override
    public long countProjects(Long divisionId, Long departmentId) throws ServerErrorException {
        LOG.debug("Counting all projects");
        try {
            return projectRepository.count(divisionId, departmentId);
        } catch (Exception e) {
            LOG.error("Error counting projects", e);
            throw new ServerErrorException(-1, "Server error");
        }
    }

    @Override
    public ProjectVO getProjectById(ProjectVO projectVO) throws ServerErrorException, InvalidDataException {
        LOG.debug("Finding project by id: {}", projectVO.getId());
        try {
            Project project = projectRepository.findById(projectVO.getId()).orElse(null);
            if (project == null) {
                throw new InvalidDataException("Project does not exists");
            }
            return Project2ProjectVO(project, null);
        } catch (InvalidDataException e) {
            throw e;
        } catch (Exception e) {
            LOG.error("Error finding project by id", e);
            throw new ServerErrorException(-1, "Server error");
        }
    }

    @Override
    public ProjectVO createProject(ProjectVO projectVO) throws ServerErrorException, InvalidDataException {
        LOG.debug("Creating project");
        try {
            Project project = ProjectVO2Project(projectVO, null);
            if (projectVO.getDepartmentId() != null) {
                Department department = departmentRepository.findById(projectVO.getDepartmentId()).orElse(null);
                if (department == null) {
                    throw new InvalidDataException("Department does not exists");
                }
                project.setDepartment(department);
            }
            project.validateData();
            project = projectRepository.save(project);
            return Project2ProjectVO(project, null);
        } catch (InvalidDataException e) {
            throw e;
        } catch (Exception e) {
            LOG.error("Error creating project", e);
            throw new ServerErrorException(-1, "Server error");
        }
    }

    @Override
    public ProjectVO updateProject(ProjectVO projectVO) throws ServerErrorException, InvalidDataException {
        LOG.debug("Updating project: {}", projectVO.getId());
        try {
            Project project = projectRepository.findById(projectVO.getId()).orElse(null);
            if (project == null) {
                throw new InvalidDataException("Project does not exists");
            }
            project = ProjectVO2Project(projectVO, project);
            if (projectVO.getDepartmentId() != null) {
                Department department = departmentRepository.findById(projectVO.getDepartmentId()).orElse(null);
                if (department == null) {
                    throw new InvalidDataException("Department does not exists");
                }
                project.setDepartment(department);
            }
            project.validateData();
            project = projectRepository.save(project);
            return Project2ProjectVO(project, null);
        } catch (InvalidDataException e) {
            throw e;
        } catch (Exception e) {
            LOG.error("Error updating project", e);
            throw new ServerErrorException(-1, "Server error");
        }
    }

    @Override
    public void deleteProject(ProjectVO projectVO) throws ServerErrorException, InvalidDataException {
        LOG.debug("Deleting project: {}", projectVO.getId());
        try {
            Project project = projectRepository.findById(projectVO.getId()).orElse(null);
            if (project == null) {
                throw new InvalidDataException("Project does not exists");
            }
            projectRepository.delete(project);
        } catch (InvalidDataException e) {
            throw e;
        } catch (Exception e) {
            LOG.error("Error deleting project", e);
            throw new ServerErrorException(-1, "Server error");
        }
    }

    private static ProjectVO Project2ProjectVO(Project project) {
        return Project2ProjectVO(project, null);
    }

    private static ProjectVO Project2ProjectVO(Project project, ProjectVO projectVO) {
        if (projectVO == null) projectVO = new ProjectVO();
        projectVO.setId(project.getId());
        projectVO.setName(project.getName());
        projectVO.setDepartmentId(project.getDepartment() != null ? project.getDepartment().getId() : null);
        return projectVO;
    }

    private static Project ProjectVO2Project(ProjectVO projectVO) {
        return ProjectVO2Project(projectVO, null);
    }

    private static Project ProjectVO2Project(ProjectVO projectVO, Project project) {
        if (project == null) project = new Project();
        project.setId(projectVO.getId());
        project.setName(projectVO.getName());
        return project;
    }
}
