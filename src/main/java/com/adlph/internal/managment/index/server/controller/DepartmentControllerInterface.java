package com.adlph.internal.managment.index.server.controller;

import com.adlph.internal.managment.index.server.data.vo.DepartmentVO;
import com.adlph.internal.managment.index.server.exception.InvalidDataException;
import com.adlph.internal.managment.index.server.exception.ServerErrorException;

import java.util.List;

public interface DepartmentControllerInterface {

    List<DepartmentVO> findAllDepartments() throws ServerErrorException;

    DepartmentVO getDepartmentById(DepartmentVO departmentVO) throws ServerErrorException, InvalidDataException;

    DepartmentVO createDepartment(DepartmentVO departmentVO) throws ServerErrorException, InvalidDataException;

    DepartmentVO updateDepartment(DepartmentVO departmentVO) throws ServerErrorException, InvalidDataException;

    void deleteDepartment(DepartmentVO departmentVO) throws ServerErrorException, InvalidDataException;
}
