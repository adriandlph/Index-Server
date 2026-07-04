package com.adlph.internal.managment.index.server.repository;

import com.adlph.internal.managment.index.server.data.entity.Department;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public class DepartmentRepository {

    @PersistenceContext
    private EntityManager em;

    @Transactional
    public Department save(Department department) {
        if (department.getId() == null) {
            em.persist(department);
            return department;
        } else {
            return em.merge(department);
        }
    }

    public Optional<Department> findById(Long id) {
        return Optional.ofNullable(em.find(Department.class, id));
    }

    public List<Department> findAll() {
        return em.createQuery("SELECT d FROM Department d", Department.class).getResultList();
    }

    @Transactional
    public void deleteById(Long id) {
        findById(id).ifPresent(em::remove);
    }

    @Transactional
    public void delete(Department department) {
        em.remove(em.contains(department) ? department : em.merge(department));
    }

    public long count() {
        return em.createQuery("SELECT COUNT(d) FROM Department d", Long.class).getSingleResult();
    }

    public boolean existsById(Long id) {
        return findById(id).isPresent();
    }
}
