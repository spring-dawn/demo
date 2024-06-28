package com.example.demo.domain.system.menu;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MenuRepository extends JpaRepository<Menu, Long> {
//    private final EntityManager em;
//
//    public List<Menu> findAll() {
//        return em.createQuery("select c from Menu c where c.parent is NULL", Menu.class).getResultList();
//    }

    Optional<Menu> findByUrl(String url);

    List<Menu> findByParentIsNull();
}