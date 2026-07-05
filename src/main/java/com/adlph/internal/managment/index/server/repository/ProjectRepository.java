package com.adlph.internal.managment.index.server.repository;

import com.adlph.internal.managment.index.server.data.entity.Project;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public class ProjectRepository {

    @PersistenceContext
    private EntityManager em;

    @Transactional
    public Project save(Project project) {
        if (project.getId() == null) {
            em.persist(project);
            return project;
        } else {
            return em.merge(project);
        }
    }

    public Optional<Project> findById(Long id) {
        return Optional.ofNullable(em.find(Project.class, id));
    }

    public List<Project> findAll() {
        return findAll(null, null, null, null);
    }

    public List<Project> findAll(Long divisionId, Long departmentId, Integer count, Integer page) {
        StringBuilder jpql = new StringBuilder("SELECT p FROM Project p WHERE 1=1");
        if (departmentId != null) {
            jpql.append(" AND p.department.id = :departmentId");
        } else if (divisionId != null) {
            jpql.append(" AND p.department.division.id = :divisionId");
        }
        var query = em.createQuery(jpql.toString(), Project.class);
        if (departmentId != null) {
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
    public void delete(Project project) {
        em.remove(em.contains(project) ? project : em.merge(project));
    }

    public long count() {
        return em.createQuery("SELECT COUNT(p) FROM Project p", Long.class).getSingleResult();
    }

    public boolean existsById(Long id) {
        return findById(id).isPresent();
    }
}
