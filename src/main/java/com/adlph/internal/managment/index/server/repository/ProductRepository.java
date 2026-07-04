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
        return em.createQuery("SELECT p FROM Product p", Product.class).getResultList();
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

    public boolean existsById(Long id) {
        return findById(id).isPresent();
    }
}
