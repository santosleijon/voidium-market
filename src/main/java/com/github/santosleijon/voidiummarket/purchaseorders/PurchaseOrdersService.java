package com.github.santosleijon.voidiummarket.purchaseorders;

import com.github.santosleijon.voidiummarket.purchaseorders.errors.PurchaseOrderNotDeleted;
import jakarta.annotation.Nullable;
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

    @Nullable
    public PurchaseOrder get(UUID id) {
        return purchaseOrdersRepository.get(id);
    }

    public void place(PurchaseOrder purchaseOrder) {
        if (purchaseOrdersRepository.exists(purchaseOrder.id)) {
            return;
        }

        purchaseOrdersRepository.save(purchaseOrder);
    }

    public void delete(UUID id) {
        var purchaseOrder = purchaseOrdersRepository.get(id);

        if (purchaseOrder == null) {
            return;
        }

        try {
            purchaseOrder.delete();
            purchaseOrdersRepository.save(purchaseOrder);
        } catch (Exception e) {
            throw new PurchaseOrderNotDeleted(id, e);
        }
    }
}
