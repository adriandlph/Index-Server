package com.adlph.internal.managment.index.server.controller;

import com.adlph.internal.managment.index.server.data.entity.Division;
import com.adlph.internal.managment.index.server.data.vo.DivisionVO;
import com.adlph.internal.managment.index.server.exception.InvalidDataException;
import com.adlph.internal.managment.index.server.exception.ServerErrorException;
import com.adlph.internal.managment.index.server.repository.DivisionRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class DivisionController implements DivisionControllerInterface {

    private static final Logger LOG = LoggerFactory.getLogger(DivisionController.class);

    @Autowired
    private DivisionRepository divisionRepository;

    @Override
    public List<DivisionVO> findAllDivisions() throws ServerErrorException {
        LOG.debug("Finding all divisions");
        try {
            return divisionRepository.findAll().stream()
                .map(DivisionController::Division2DivisionVO)
                .toList();
        } catch (Exception e) {
            LOG.error("Error finding all divisions", e);
            throw new ServerErrorException(-1, "Server error");
        }
    }

    @Override
    public DivisionVO getDivisionById(DivisionVO divisionVO) throws ServerErrorException, InvalidDataException {
        LOG.debug("Finding division by id: {}", divisionVO.getId());
        try {
            Division division = divisionRepository.findById(divisionVO.getId()).orElse(null);
            if (division == null) {
                throw new InvalidDataException("Division does not exists");
            }
            return Division2DivisionVO(division, null);
        } catch (InvalidDataException e) {
            throw e;
        } catch (Exception e) {
            LOG.error("Error finding division by id", e);
            throw new ServerErrorException(-1, "Server error");
        }
    }

    @Override
    public DivisionVO createDivision(DivisionVO divisionVO) throws ServerErrorException, InvalidDataException {
        LOG.debug("Creating division");
        try {
            Division division = DivisionVO2Division(divisionVO, null);
            division.validateData();
            division = divisionRepository.save(division);
            return Division2DivisionVO(division, null);
        } catch (InvalidDataException e) {
            throw e;
        } catch (Exception e) {
            LOG.error("Error creating division", e);
            throw new ServerErrorException(-1, "Server error");
        }
    }

    @Override
    public DivisionVO updateDivision(DivisionVO divisionVO) throws ServerErrorException, InvalidDataException {
        LOG.debug("Updating division: {}", divisionVO.getId());
        try {
            Division division = divisionRepository.findById(divisionVO.getId()).orElse(null);
            if (division == null) {
                throw new InvalidDataException("Division does not exists");
            }
            division = DivisionVO2Division(divisionVO, division);
            division.validateData();
            division = divisionRepository.save(division);
            return Division2DivisionVO(division, null);
        } catch (InvalidDataException e) {
            throw e;
        } catch (Exception e) {
            LOG.error("Error updating division", e);
            throw new ServerErrorException(-1, "Server error");
        }
    }

    @Override
    public void deleteDivision(DivisionVO divisionVO) throws ServerErrorException, InvalidDataException {
        LOG.debug("Deleting division: {}", divisionVO.getId());
        try {
            Division division = divisionRepository.findById(divisionVO.getId()).orElse(null);
            if (division == null) {
                throw new InvalidDataException("Division does not exists");
            }
            divisionRepository.delete(division);
        } catch (InvalidDataException e) {
            throw e;
        } catch (Exception e) {
            LOG.error("Error deleting division", e);
            throw new ServerErrorException(-1, "Server error");
        }
    }

    private static DivisionVO Division2DivisionVO(Division division) {
        return Division2DivisionVO(division, null);
    }

    private static DivisionVO Division2DivisionVO(Division division, DivisionVO divisionVO) {
        if (divisionVO == null) divisionVO = new DivisionVO();
        divisionVO.setId(division.getId());
        divisionVO.setName(division.getName());
        return divisionVO;
    }

    private static Division DivisionVO2Division(DivisionVO divisionVO) {
        return DivisionVO2Division(divisionVO, null);
    }

    private static Division DivisionVO2Division(DivisionVO divisionVO, Division division) {
        if (division == null) division = new Division();
        division.setId(divisionVO.getId());
        division.setName(divisionVO.getName());
        return division;
    }
}
