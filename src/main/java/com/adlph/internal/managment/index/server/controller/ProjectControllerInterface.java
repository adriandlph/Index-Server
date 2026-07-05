package com.adlph.internal.managment.index.server.controller;

import com.adlph.internal.managment.index.server.data.vo.ProjectVO;
import com.adlph.internal.managment.index.server.exception.InvalidDataException;
import com.adlph.internal.managment.index.server.exception.ServerErrorException;

import java.util.List;

public interface ProjectControllerInterface {

    List<ProjectVO> findAllProjects(Long divisionId, Long departmentId, Integer count, Integer page) throws ServerErrorException;

    ProjectVO getProjectById(ProjectVO projectVO) throws ServerErrorException, InvalidDataException;

    ProjectVO createProject(ProjectVO projectVO) throws ServerErrorException, InvalidDataException;

    ProjectVO updateProject(ProjectVO projectVO) throws ServerErrorException, InvalidDataException;

    void deleteProject(ProjectVO projectVO) throws ServerErrorException, InvalidDataException;
}
