package com.github.santosleijon.voidiummarket.mocks;

import com.github.santosleijon.voidiummarket.purchaseorders.PurchaseOrder;
import com.github.santosleijon.voidiummarket.purchaseorders.projections.PurchaseOrderProjectionsDAO;
import scala.NotImplementedError;

import java.util.UUID;

public class PurchaseOrderProjectionsDAOMock implements PurchaseOrderProjectionsDAO {

    @Override
    public void upsert(PurchaseOrder purchaseOrder) {
        throw new NotImplementedError();
    }

    @Override
    public void delete(UUID purchaseOrderId) {
        throw new NotImplementedError();
    }
}
