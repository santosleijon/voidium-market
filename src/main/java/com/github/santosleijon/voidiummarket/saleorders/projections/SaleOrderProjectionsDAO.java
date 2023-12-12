package com.github.santosleijon.voidiummarket.saleorders.projections;

import java.util.List;
import java.util.UUID;

public interface SaleOrderProjectionsDAO {
    void upsert(SaleOrderProjection saleOrder);
    SaleOrderProjection get(UUID saleOrderId);
    List<SaleOrderProjection> getNonDeleted();
    List<SaleOrderProjection> getUnfulfilled();
    void deleteAll();
}
