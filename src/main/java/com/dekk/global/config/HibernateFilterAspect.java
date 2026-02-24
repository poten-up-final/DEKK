package com.dekk.global.config;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.hibernate.Session;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class HibernateFilterAspect {

    @PersistenceContext
    private EntityManager entityManager;

    @Before("execution(* com.dekk..*Service.*(..))")
    public void enableDeletedFilter() {
        Session session = entityManager.unwrap(Session.class);
        session.enableFilter("deletedFilter");
    }
}
