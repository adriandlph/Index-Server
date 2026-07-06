package com.adlph.internal.managment.index.server.controller;

import com.adlph.internal.managment.index.server.data.entity.Product;
import com.adlph.internal.managment.index.server.data.entity.Project;
import com.adlph.internal.managment.index.server.data.vo.ProductVO;
import com.adlph.internal.managment.index.server.exception.InvalidDataException;
import com.adlph.internal.managment.index.server.exception.ServerErrorException;
import com.adlph.internal.managment.index.server.repository.ProductRepository;
import com.adlph.internal.managment.index.server.repository.ProjectRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class ProductController implements ProductControllerInterface {

    private static final Logger LOG = LoggerFactory.getLogger(ProductController.class);

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Override
    public List<ProductVO> findAllProducts(Long divisionId, Long departmentId, Long projectId, Integer count, Integer page) throws ServerErrorException {
        LOG.debug("Finding all products");
        try {
            return productRepository.findAll(divisionId, departmentId, projectId, count, page).stream()
                .map(ProductController::Product2ProductVO)
                .toList();
        } catch (Exception e) {
            LOG.error("Error finding all products", e);
            throw new ServerErrorException(-1, "Server error");
        }
    }

    @Override
    public long countProducts(Long divisionId, Long departmentId, Long projectId) throws ServerErrorException {
        LOG.debug("Counting all products");
        try {
            return productRepository.count(divisionId, departmentId, projectId);
        } catch (Exception e) {
            LOG.error("Error counting products", e);
            throw new ServerErrorException(-1, "Server error");
        }
    }

    @Override
    public ProductVO getProductById(ProductVO productVO) throws ServerErrorException, InvalidDataException {
        LOG.debug("Finding product by id: {}", productVO.getId());
        try {
            Product product = productRepository.findById(productVO.getId()).orElse(null);
            if (product == null) {
                throw new InvalidDataException("Product does not exists");
            }
            return Product2ProductVO(product, null);
        } catch (InvalidDataException e) {
            throw e;
        } catch (Exception e) {
            LOG.error("Error finding product by id", e);
            throw new ServerErrorException(-1, "Server error");
        }
    }

    @Override
    public ProductVO createProduct(ProductVO productVO) throws ServerErrorException, InvalidDataException {
        LOG.debug("Creating product");
        try {
            Product product = ProductVO2Product(productVO, null);
            if (productVO.getProjectId() != null) {
                Project project = projectRepository.findById(productVO.getProjectId()).orElse(null);
                if (project == null) {
                    throw new InvalidDataException("Project does not exists");
                }
                product.setProject(project);
            }
            product.validateData();
            product = productRepository.save(product);
            return Product2ProductVO(product, null);
        } catch (InvalidDataException e) {
            throw e;
        } catch (Exception e) {
            LOG.error("Error creating product", e);
            throw new ServerErrorException(-1, "Server error");
        }
    }

    @Override
    public ProductVO updateProduct(ProductVO productVO) throws ServerErrorException, InvalidDataException {
        LOG.debug("Updating product: {}", productVO.getId());
        try {
            Product product = productRepository.findById(productVO.getId()).orElse(null);
            if (product == null) {
                throw new InvalidDataException("Product does not exists");
            }
            product = ProductVO2Product(productVO, product);
            if (productVO.getProjectId() != null) {
                Project project = projectRepository.findById(productVO.getProjectId()).orElse(null);
                if (project == null) {
                    throw new InvalidDataException("Project does not exists");
                }
                product.setProject(project);
            }
            product.validateData();
            product = productRepository.save(product);
            return Product2ProductVO(product, null);
        } catch (InvalidDataException e) {
            throw e;
        } catch (Exception e) {
            LOG.error("Error updating product", e);
            throw new ServerErrorException(-1, "Server error");
        }
    }

    @Override
    public void deleteProduct(ProductVO productVO) throws ServerErrorException, InvalidDataException {
        LOG.debug("Deleting product: {}", productVO.getId());
        try {
            Product product = productRepository.findById(productVO.getId()).orElse(null);
            if (product == null) {
                throw new InvalidDataException("Product does not exists");
            }
            productRepository.delete(product);
        } catch (InvalidDataException e) {
            throw e;
        } catch (Exception e) {
            LOG.error("Error deleting product", e);
            throw new ServerErrorException(-1, "Server error");
        }
    }

    private static ProductVO Product2ProductVO(Product product) {
        return Product2ProductVO(product, null);
    }

    private static ProductVO Product2ProductVO(Product product, ProductVO productVO) {
        if (productVO == null) productVO = new ProductVO();
        productVO.setId(product.getId());
        productVO.setName(product.getName());
        productVO.setVersion(product.getVersion());
        productVO.setPublishDate(product.getPublishDate());
        productVO.setDescription(product.getDescription());
        productVO.setProjectId(product.getProject() != null ? product.getProject().getId() : null);
        return productVO;
    }

    private static Product ProductVO2Product(ProductVO productVO) {
        return ProductVO2Product(productVO, null);
    }

    private static Product ProductVO2Product(ProductVO productVO, Product product) {
        if (product == null) product = new Product();
        product.setId(productVO.getId());
        product.setName(productVO.getName());
        product.setVersion(productVO.getVersion());
        product.setPublishDate(productVO.getPublishDate());
        product.setDescription(productVO.getDescription());
        return product;
    }
}
