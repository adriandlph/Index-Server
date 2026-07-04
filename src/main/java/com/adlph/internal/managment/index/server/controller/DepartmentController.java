package com.adlph.internal.managment.index.server.controller;

import com.adlph.internal.managment.index.server.data.entity.Department;
import com.adlph.internal.managment.index.server.data.entity.Division;
import com.adlph.internal.managment.index.server.data.vo.DepartmentVO;
import com.adlph.internal.managment.index.server.exception.InvalidDataException;
import com.adlph.internal.managment.index.server.exception.ServerErrorException;
import com.adlph.internal.managment.index.server.repository.DepartmentRepository;
import com.adlph.internal.managment.index.server.repository.DivisionRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class DepartmentController implements DepartmentControllerInterface {

    private static final Logger LOG = LoggerFactory.getLogger(DepartmentController.class);

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private DivisionRepository divisionRepository;

    @Override
    public List<DepartmentVO> findAllDepartments() throws ServerErrorException {
        LOG.debug("Finding all departments");
        try {
            return departmentRepository.findAll().stream()
                .map(DepartmentController::Department2DepartmentVO)
                .toList();
        } catch (Exception e) {
            LOG.error("Error finding all departments", e);
            throw new ServerErrorException(-1, "Server error");
        }
    }

    @Override
    public DepartmentVO getDepartmentById(DepartmentVO departmentVO) throws ServerErrorException, InvalidDataException {
        LOG.debug("Finding department by id: {}", departmentVO.getId());
        try {
            Department department = departmentRepository.findById(departmentVO.getId()).orElse(null);
            if (department == null) {
                throw new InvalidDataException("Department does not exists");
            }
            return Department2DepartmentVO(department, null);
        } catch (InvalidDataException e) {
            throw e;
        } catch (Exception e) {
            LOG.error("Error finding department by id", e);
            throw new ServerErrorException(-1, "Server error");
        }
    }

    @Override
    public DepartmentVO createDepartment(DepartmentVO departmentVO) throws ServerErrorException, InvalidDataException {
        LOG.debug("Creating department");
        try {
            Department department = DepartmentVO2Department(departmentVO, null);
            if (departmentVO.getDivisionId() != null) {
                Division division = divisionRepository.findById(departmentVO.getDivisionId()).orElse(null);
                if (division == null) {
                    throw new InvalidDataException("Division does not exists");
                }
                department.setDivision(division);
            }
            department.validateData();
            department = departmentRepository.save(department);
            return Department2DepartmentVO(department, null);
        } catch (InvalidDataException e) {
            throw e;
        } catch (Exception e) {
            LOG.error("Error creating department", e);
            throw new ServerErrorException(-1, "Server error");
        }
    }

    @Override
    public DepartmentVO updateDepartment(DepartmentVO departmentVO) throws ServerErrorException, InvalidDataException {
        LOG.debug("Updating department: {}", departmentVO.getId());
        try {
            Department department = departmentRepository.findById(departmentVO.getId()).orElse(null);
            if (department == null) {
                throw new InvalidDataException("Department does not exists");
            }
            department = DepartmentVO2Department(departmentVO, department);
            if (departmentVO.getDivisionId() != null) {
                Division division = divisionRepository.findById(departmentVO.getDivisionId()).orElse(null);
                if (division == null) {
                    throw new InvalidDataException("Division does not exists");
                }
                department.setDivision(division);
            }
            department.validateData();
            department = departmentRepository.save(department);
            return Department2DepartmentVO(department, null);
        } catch (InvalidDataException e) {
            throw e;
        } catch (Exception e) {
            LOG.error("Error updating department", e);
            throw new ServerErrorException(-1, "Server error");
        }
    }

    @Override
    public void deleteDepartment(DepartmentVO departmentVO) throws ServerErrorException, InvalidDataException {
        LOG.debug("Deleting department: {}", departmentVO.getId());
        try {
            Department department = departmentRepository.findById(departmentVO.getId()).orElse(null);
            if (department == null) {
                throw new InvalidDataException("Department does not exists");
            }
            departmentRepository.delete(department);
        } catch (InvalidDataException e) {
            throw e;
        } catch (Exception e) {
            LOG.error("Error deleting department", e);
            throw new ServerErrorException(-1, "Server error");
        }
    }

    private static DepartmentVO Department2DepartmentVO(Department department) {
        return Department2DepartmentVO(department, null);
    }

    private static DepartmentVO Department2DepartmentVO(Department department, DepartmentVO departmentVO) {
        if (departmentVO == null) departmentVO = new DepartmentVO();
        departmentVO.setId(department.getId());
        departmentVO.setName(department.getName());
        departmentVO.setDivisionId(department.getDivision() != null ? department.getDivision().getId() : null);
        return departmentVO;
    }

    private static Department DepartmentVO2Department(DepartmentVO departmentVO) {
        return DepartmentVO2Department(departmentVO, null);
    }

    private static Department DepartmentVO2Department(DepartmentVO departmentVO, Department department) {
        if (department == null) department = new Department();
        department.setId(departmentVO.getId());
        department.setName(departmentVO.getName());
        return department;
    }
}
