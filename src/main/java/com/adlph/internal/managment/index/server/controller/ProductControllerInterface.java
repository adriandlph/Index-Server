package com.adlph.internal.managment.index.server.controller;

import com.adlph.internal.managment.index.server.data.vo.ProductVO;
import com.adlph.internal.managment.index.server.exception.InvalidDataException;
import com.adlph.internal.managment.index.server.exception.ServerErrorException;

import java.util.List;

public interface ProductControllerInterface {

    List<ProductVO> findAllProducts(Long divisionId, Long departmentId, Long projectId, Integer count, Integer page) throws ServerErrorException;

    ProductVO getProductById(ProductVO productVO) throws ServerErrorException, InvalidDataException;

    ProductVO createProduct(ProductVO productVO) throws ServerErrorException, InvalidDataException;

    ProductVO updateProduct(ProductVO productVO) throws ServerErrorException, InvalidDataException;

    void deleteProduct(ProductVO productVO) throws ServerErrorException, InvalidDataException;
}
