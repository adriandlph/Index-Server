package com.adlph.internal.managment.index.server.repository;

import com.adlph.internal.managment.index.server.data.entity.Product;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public class ProductRepository {

    @PersistenceContext
    private EntityManager em;

    @Transactional
    public Product save(Product product) {
        if (product.getId() == null) {
            em.persist(product);
            return product;
        } else {
            return em.merge(product);
        }
    }

    public Optional<Product> findById(Long id) {
        return Optional.ofNullable(em.find(Product.class, id));
    }

    public List<Product> findAll() {
        return findAll(null, null, null, null, null);
    }

    public List<Product> findAll(Long divisionId, Long departmentId, Long projectId, Integer count, Integer page) {
        StringBuilder jpql = new StringBuilder("SELECT pr FROM Product pr WHERE 1=1");
        if (projectId != null) {
            jpql.append(" AND pr.project.id = :projectId");
        } else if (departmentId != null) {
            jpql.append(" AND pr.project.department.id = :departmentId");
        } else if (divisionId != null) {
            jpql.append(" AND pr.project.department.division.id = :divisionId");
        }
        var query = em.createQuery(jpql.toString(), Product.class);
        if (projectId != null) {
            query.setParameter("projectId", projectId);
        } else if (departmentId != null) {
            query.setParameter("departmentId", departmentId);
        } else if (divisionId != null) {
            query.setParameter("divisionId", divisionId);
        }
        if (count != null) {
            query.setFirstResult(page != null ? count * page : 0);
            query.setMaxResults(count);
        }
        return query.getResultList();
    }

    @Transactional
    public void deleteById(Long id) {
        findById(id).ifPresent(em::remove);
    }

    @Transactional
    public void delete(Product product) {
        em.remove(em.contains(product) ? product : em.merge(product));
    }

    public long count() {
        return em.createQuery("SELECT COUNT(p) FROM Product p", Long.class).getSingleResult();
    }

    public long count(Long divisionId, Long departmentId, Long projectId) {
        if (divisionId == null && departmentId == null && projectId == null) return count();
        StringBuilder jpql = new StringBuilder("SELECT COUNT(p) FROM Product p WHERE 1=1");
        if (projectId != null) {
            jpql.append(" AND p.project.id = :projectId");
        } else if (departmentId != null) {
            jpql.append(" AND p.project.department.id = :departmentId");
        } else if (divisionId != null) {
            jpql.append(" AND p.project.department.division.id = :divisionId");
        }
        var query = em.createQuery(jpql.toString(), Long.class);
        if (projectId != null) {
            query.setParameter("projectId", projectId);
        } else if (departmentId != null) {
            query.setParameter("departmentId", departmentId);
        } else if (divisionId != null) {
            query.setParameter("divisionId", divisionId);
        }
        return query.getSingleResult();
    }

    public boolean existsById(Long id) {
        return findById(id).isPresent();
    }
}
