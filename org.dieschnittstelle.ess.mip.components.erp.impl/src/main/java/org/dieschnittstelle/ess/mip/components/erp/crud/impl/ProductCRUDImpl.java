package org.dieschnittstelle.ess.mip.components.erp.crud.impl;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.RequestScoped;
import org.dieschnittstelle.ess.entities.erp.AbstractProduct;
import org.dieschnittstelle.ess.entities.erp.Campaign;
import org.dieschnittstelle.ess.mip.components.erp.crud.api.ProductCRUD;

import java.util.List;

import static org.dieschnittstelle.ess.utils.Utils.show;

@ApplicationScoped //(Eine Instanz für die Laufzeit der Anwendung, die beim Start erzeigt wird -> Bei RequestScoped, wäre bei jeder Anfrage eine neue Instant)
public class ProductCRUDImpl implements ProductCRUD {
    public ProductCRUDImpl() {
        show("constructor(): ProductCRUDImpl");
    }

    @Override
    public AbstractProduct createProduct(AbstractProduct prod) {
        show("createProduct(): " + prod);
        return prod;
    }

    @Override
    public List<AbstractProduct> readAllProducts() {
        show("readAllProducts()");
        return List.of();
    }

    @Override
    public AbstractProduct updateProduct(AbstractProduct update) {
        return null;
    }

    @Override
    public AbstractProduct readProduct(long productID) {
        return null;
    }

    @Override
    public boolean deleteProduct(long productID) {
        return false;
    }

    @Override
    public List<Campaign> getCampaignsForProduct(long productID) {
        return List.of();
    }
}
