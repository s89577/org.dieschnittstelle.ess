package org.dieschnittstelle.ess.mip.components.erp.crud.impl;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;
import jakarta.inject.Qualifier;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@ApplicationScoped
public class EntityManagerProvider {

    @Qualifier
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public static @interface ERPDataAccessor {

    }

    //Annotationen, die das Objekt nach außen hin zur Verfügung stellen, damit es genutzt werden kann
    @Produces
    @ERPDataAccessor
    @PersistenceContext(unitName = "erp_PU")
    private EntityManager em;

}