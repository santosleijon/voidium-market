package com.github.santosleijon.voidiummarket.saleorders;

import com.github.santosleijon.voidiummarket.saleorders.errors.SaleOrderNotDeleted;
import jakarta.annotation.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class SaleOrdersService {

    private final SaleOrdersRepository saleOrdersRepository;

    @Autowired
    public SaleOrdersService(SaleOrdersRepository saleOrdersRepository) {
        this.saleOrdersRepository = saleOrdersRepository;
    }

    public List<SaleOrder> getAll() {
        return saleOrdersRepository.getAll();
    }

    @Nullable
    public SaleOrder get(UUID id) {
        return saleOrdersRepository.get(id);
    }

    public void place(SaleOrder saleOrder) {
        if (saleOrdersRepository.exists(saleOrder.id)) {
            return;
        }

        saleOrdersRepository.save(saleOrder);
    }

    public void delete(UUID id) {
        var saleOrder = saleOrdersRepository.get(id);

        if (saleOrder == null) {
            return;
        }

        try {
            saleOrder.delete();
            saleOrdersRepository.save(saleOrder);
        } catch (Exception e) {
            throw new SaleOrderNotDeleted(id, e);
        }
    }
}
