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
        return em.createQuery("SELECT p FROM Project p", Project.class).getResultList();
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
