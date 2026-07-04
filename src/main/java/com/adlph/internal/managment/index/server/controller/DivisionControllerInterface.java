package com.adlph.internal.managment.index.server.controller;

import com.adlph.internal.managment.index.server.data.vo.DivisionVO;
import com.adlph.internal.managment.index.server.exception.InvalidDataException;
import com.adlph.internal.managment.index.server.exception.ServerErrorException;

import java.util.List;

public interface DivisionControllerInterface {

    List<DivisionVO> findAllDivisions() throws ServerErrorException;

    DivisionVO getDivisionById(DivisionVO divisionVO) throws ServerErrorException, InvalidDataException;

    DivisionVO createDivision(DivisionVO divisionVO) throws ServerErrorException, InvalidDataException;

    DivisionVO updateDivision(DivisionVO divisionVO) throws ServerErrorException, InvalidDataException;

    void deleteDivision(DivisionVO divisionVO) throws ServerErrorException, InvalidDataException;
}
