package com.github.santosleijon.voidiummarket.purchaseorders;

import com.github.santosleijon.voidiummarket.purchaseorders.errors.PurchaseOrderNotSavedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class PurchaseOrdersService {

    private final PurchaseOrdersRepository purchaseOrdersRepository;

    @Autowired
    public PurchaseOrdersService(PurchaseOrdersRepository purchaseOrdersRepository) {
        this.purchaseOrdersRepository = purchaseOrdersRepository;
    }

    public List<PurchaseOrder> getAll() {
        return purchaseOrdersRepository.getAll();
    }

    public PurchaseOrder get(UUID id) {
        return purchaseOrdersRepository.get(id);
    }

    public void add(PurchaseOrder purchaseOrder) throws PurchaseOrderNotSavedException {
        purchaseOrdersRepository.save(purchaseOrder);
    }
}
