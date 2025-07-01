package org.dieschnittstelle.ess.mip.components.erp.crud.impl;

import jakarta.annotation.Priority;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Alternative;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptor;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import org.dieschnittstelle.ess.entities.erp.IndividualisedProductItem;
import org.dieschnittstelle.ess.entities.erp.PointOfSale;
import org.dieschnittstelle.ess.entities.erp.StockItem;
import org.dieschnittstelle.ess.utils.interceptors.Logged;

import java.util.List;

@ApplicationScoped
@Transactional
@Logged
public class StockItemCRUDImpl implements StockItemCRUD{

    @Inject
    @EntityManagerProvider.ERPDataAccessor  //Qualifier Identifikation, da mehr als 1 EntityManager existiert zum Identifizieren
    private EntityManager em;

    @Override
    public StockItem createStockItem(StockItem item) {
       em.persist(item);
       return item;
    }

    @Override
    public StockItem readStockItem(IndividualisedProductItem prod, PointOfSale pos) {
        Query query=em.createQuery("select s from StockItem s where s.product.id=:prodID and s.pos.id=:posID");
        query.setParameter("prodID", prod.getId());
        query.setParameter("posID", pos.getId());
        try {
           return (StockItem) query.getSingleResult();
        }catch (NoResultException e){
            return null;
        }
    }

    @Override
    public StockItem updateStockItem(StockItem item) {
        return em.merge(item);
    }

    @Override
    public List<StockItem> readStockItemsForProduct(IndividualisedProductItem prod) {
        Query q= em.createQuery("SELECT s FROM StockItem s WHERE s.product.id= :productID");
        q.setParameter("productID", prod.getId());
        return q.getResultList();
    }

    @Override
    public List<StockItem> readStockItemsForPointOfSale(PointOfSale pos) {
        Query q= em.createQuery("SELECT s FROM StockItem s WHERE s.pos.id= :posID");
        q.setParameter("posID", pos.getId());
        return q.getResultList();
    }
}
