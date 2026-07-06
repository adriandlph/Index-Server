package com.adlph.internal.managment.index.server.repository;

import com.adlph.internal.managment.index.server.data.entity.Division;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public class DivisionRepository {

    @PersistenceContext
    private EntityManager em;

    @Transactional
    public Division save(Division division) {
        if (division.getId() == null) {
            em.persist(division);
            return division;
        } else {
            return em.merge(division);
        }
    }

    public Optional<Division> findById(Long id) {
        return Optional.ofNullable(em.find(Division.class, id));
    }

    public List<Division> findAll() {
        return findAll(null, null);
    }

    public List<Division> findAll(Integer count, Integer page) {
        var query = em.createQuery("SELECT d FROM Division d", Division.class);
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
    public void delete(Division division) {
        em.remove(em.contains(division) ? division : em.merge(division));
    }

    public long count() {
        return em.createQuery("SELECT COUNT(d) FROM Division d", Long.class).getSingleResult();
    }

    public boolean existsById(Long id) {
        return findById(id).isPresent();
    }
}
