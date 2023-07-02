package com.github.santosleijon.voidiummarket.saleorders;

import com.github.santosleijon.voidiummarket.saleorders.errors.SaleOrderNotDeleted;
import jakarta.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class SaleOrdersService {

    private static final Logger log = LoggerFactory.getLogger(SaleOrdersService.class);

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
        if (saleOrdersRepository.exists(saleOrder.getId())) {
            return;
        }

        saleOrdersRepository.save(saleOrder);

        log.info("SaleOrder {}: Sell {} units to unit price of {} CU", saleOrder.getId(), saleOrder.getUnitsCount(), saleOrder.getPricePerUnit());
    }

    public void delete(UUID id) {
        var saleOrder = saleOrdersRepository.get(id);

        if (saleOrder == null) {
            return;
        }

        try {
            saleOrder.delete();
            saleOrdersRepository.save(saleOrder);

            log.info("SaleOrder {}: Delete order of {} units to unit price of {} CU", saleOrder.getId(), saleOrder.getUnitsCount(), saleOrder.getPricePerUnit());
        } catch (Exception e) {
            throw new SaleOrderNotDeleted(id, e);
        }
    }
}
