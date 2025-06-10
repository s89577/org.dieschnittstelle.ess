package org.dieschnittstelle.ess.mip.components.erp.impl;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.dieschnittstelle.ess.entities.erp.IndividualisedProductItem;
import org.dieschnittstelle.ess.entities.erp.PointOfSale;
import org.dieschnittstelle.ess.entities.erp.StockItem;
import org.dieschnittstelle.ess.mip.components.erp.api.StockSystem;
import org.dieschnittstelle.ess.mip.components.erp.crud.api.PointOfSaleCRUD;
import org.dieschnittstelle.ess.mip.components.erp.crud.impl.StockItemCRUD;
import org.dieschnittstelle.ess.utils.interceptors.Logged;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
@Transactional
@Logged
public class StockSystemImpl implements StockSystem {

    @Inject
    private PointOfSaleCRUD posCRUD;

    @Inject
    private StockItemCRUD stockItemCRUD;

    @Override
    public void addToStock(IndividualisedProductItem product, long pointOfSaleId, int units) {
        PointOfSale pos = posCRUD.readPointOfSale(pointOfSaleId);
        StockItem item = stockItemCRUD.readStockItem(product, pos);
        if(item!=null) {
            item.setUnits(item.getUnits()+units);
            stockItemCRUD.updateStockItem(item);
        }else{
            item =new StockItem(product, pos, units);
            stockItemCRUD.createStockItem(item);
        }
    }

    @Override
    public void removeFromStock(IndividualisedProductItem product, long pointOfSaleId, int units) {
       this.addToStock(product,pointOfSaleId,-units);
    }

    @Override
    public List<IndividualisedProductItem> getProductsOnStock(long pointOfSaleId) {
        PointOfSale pos = posCRUD.readPointOfSale(pointOfSaleId);
        if(pos!=null) {
            return stockItemCRUD.readStockItemsForPointOfSale(pos).stream().map(StockItem::getProduct).toList();
        }else{
            return List.of();
        }
    }

    @Override
    public List<IndividualisedProductItem> getAllProductsOnStock() {
        List<PointOfSale> posList=posCRUD.readAllPointsOfSale();
        HashSet<IndividualisedProductItem> productSet=new HashSet<>(); //vermeidet aitomatisch Duplikate
        for(PointOfSale pos:posList) {
            List<IndividualisedProductItem> prodListForPos = stockItemCRUD.readStockItemsForPointOfSale(pos).stream().map(StockItem::getProduct).toList();
            productSet.addAll(prodListForPos);
        };
        return new ArrayList<>(productSet);
    }

    @Override
    public int getUnitsOnStock(IndividualisedProductItem product, long pointOfSaleId) {
        PointOfSale pos = posCRUD.readPointOfSale(pointOfSaleId);
        if(pos!=null) {
            StockItem item = stockItemCRUD.readStockItem(product, pos);
            if (item != null) {
                return item.getUnits();
            } else {
                throw new IllegalArgumentException("No stock item found for product " + product.getId() + " and point of sale " + pointOfSaleId);
            }
        }else{
            throw new IllegalArgumentException("No point of sale found for id "+pointOfSaleId);
        }
    }

    @Override
    public int getTotalUnitsOnStock(IndividualisedProductItem product) {
        return stockItemCRUD.readStockItemsForProduct(product).stream().mapToInt(StockItem::getUnits).sum();
    }

    @Override
    public List<Long> getPointsOfSale(IndividualisedProductItem product) {
        return stockItemCRUD.readStockItemsForProduct(product).stream().map(si->si.getPos().getId()).toList();
    }
}