package com.github.santosleijon.voidiummarket.saleorders;

import com.github.santosleijon.voidiummarket.saleorders.errors.SaleOrderNotDeleted;
import com.github.santosleijon.voidiummarket.transactions.TransactionService;
import jakarta.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class SaleOrderService {

    private static final Logger log = LoggerFactory.getLogger(SaleOrderService.class);

    private final SaleOrderRepository saleOrderRepository;
    private final TransactionService transactionService;

    @Autowired
    public SaleOrderService(SaleOrderRepository saleOrderRepository, TransactionService transactionService) {
        this.saleOrderRepository = saleOrderRepository;
        this.transactionService = transactionService;
    }

    public List<SaleOrder> getAll() {
        return saleOrderRepository.getAll();
    }

    public List<SaleOrder> getUnfulfilled() {
        return saleOrderRepository.getUnfulfilled();
    }

    @Nullable
    public SaleOrder get(UUID id) {
        var saleOrder = saleOrderRepository.get(id);

        if (saleOrder == null) {
            return null;
        }

        var transactions = transactionService.getForSaleOrder(saleOrder.getId());

        return saleOrder.setTransactions(transactions);
    }

    public void place(SaleOrder saleOrder) {
        if (saleOrderRepository.exists(saleOrder.getId())) {
            return;
        }

        saleOrderRepository.save(saleOrder);

        log.info("SaleOrder\t\t\t{}: Sell {} units to unit price of {} CU", saleOrder.getId(), saleOrder.getUnitsCount(), saleOrder.getPricePerUnit());
    }

    public void delete(UUID id) {
        var saleOrder = saleOrderRepository.get(id);

        if (saleOrder == null) {
            return;
        }

        try {
            saleOrder.delete();
            saleOrderRepository.save(saleOrder);

            log.info("SaleOrder\t\t{}: Delete order of {} units to unit price of {} CU", saleOrder.getId(), saleOrder.getUnitsCount(), saleOrder.getPricePerUnit());
        } catch (Exception e) {
            throw new SaleOrderNotDeleted(id, e);
        }
    }

    private SaleOrder getSaleOrderWithTransactions(SaleOrder saleOrder) {
        var transactions = transactionService.getForSaleOrder(saleOrder.getId());

        return saleOrder.setTransactions(transactions);
    }
}
