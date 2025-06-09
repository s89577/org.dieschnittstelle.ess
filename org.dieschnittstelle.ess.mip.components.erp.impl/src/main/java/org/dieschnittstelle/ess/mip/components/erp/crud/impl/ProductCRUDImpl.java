package org.dieschnittstelle.ess.mip.components.erp.crud.impl;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import org.dieschnittstelle.ess.entities.erp.AbstractProduct;
import org.dieschnittstelle.ess.entities.erp.Campaign;
import org.dieschnittstelle.ess.mip.components.erp.crud.api.ProductCRUD;
import org.dieschnittstelle.ess.utils.interceptors.Logged;

import java.util.List;

import static org.dieschnittstelle.ess.utils.Utils.show;

@ApplicationScoped //(Eine Instanz für die Laufzeit der Anwendung, die beim Start erzeigt wird -> Bei RequestScoped, wäre bei jeder Anfrage eine neue Instant)
@Transactional //Nötig für DB Transaktion
@Logged //Für Debugging
public class ProductCRUDImpl implements ProductCRUD {
    public ProductCRUDImpl() {
        show("constructor(): ProductCRUDImpl");
    }

    @Inject
    @EntityManagerProvider.ERPDataAccessor
    private EntityManager entityManager;

    @Override
    public AbstractProduct createProduct(AbstractProduct prod) {
        show("createProduct(): " + prod);
        entityManager.persist(prod);
        return prod;
    }

    @Override
    public List<AbstractProduct> readAllProducts() {
        show("readAllProducts()");
        Query query=entityManager.createQuery("select p from AbstractProduct p");
        return query.getResultList();
    }

    @Override
    public AbstractProduct updateProduct(AbstractProduct update) {
        update=entityManager.merge(update);
        return update;
    }

    @Override
    public AbstractProduct readProduct(long productID) {
        AbstractProduct prod = entityManager.find(AbstractProduct.class, productID);
        return prod;
    }

    @Override
    public boolean deleteProduct(long productID) {
        AbstractProduct product=entityManager.find(AbstractProduct.class, productID);
        if(product!=null) {
            entityManager.remove(product);
            return true;
        }
        return false;
    }

    @Override
    public List<Campaign> getCampaignsForProduct(long productID) {
        Query q= entityManager.createQuery("SELECT c FROM Campaign c JOIN c.bundles b WHERE b.product.id= :productID");
        q.setParameter("productID", productID);
        return q.getResultList();
    }
}
