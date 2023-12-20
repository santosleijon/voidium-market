package com.github.santosleijon.voidiummarket.ui;

import com.github.santosleijon.voidiummarket.common.eventstore.DomainEventDTO;
import com.github.santosleijon.voidiummarket.common.eventstore.EventStore;
import com.github.santosleijon.voidiummarket.saleorders.SaleOrderDTO;
import com.github.santosleijon.voidiummarket.saleorders.SaleOrderRepository;
import com.github.santosleijon.voidiummarket.transactions.Transaction;
import com.github.santosleijon.voidiummarket.transactions.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.stream.Collectors;

@Controller
public class DashboardController {

    private final TransactionRepository transactionRepository;
    private final SaleOrderRepository saleOrderRepository;
    private final EventStore eventStore;

    @Autowired
    public DashboardController(TransactionRepository transactionRepository, SaleOrderRepository saleOrderRepository, EventStore eventStore) {
        this.transactionRepository = transactionRepository;
        this.saleOrderRepository = saleOrderRepository;
        this.eventStore = eventStore;
    }

    @GetMapping("/")
    public String dashboard(
            Model model,
            @RequestParam(defaultValue = "1") String transactionsPage,
            @RequestParam(defaultValue = "1") String saleOrdersPage
    ) {
        int currentTransactionsPage = Integer.parseInt(transactionsPage);

        setTransactionsAttributes(model, currentTransactionsPage);

        int currentSaleOrdersPage = Integer.parseInt(saleOrdersPage);

        setSaleOrdersAttributes(model, currentSaleOrdersPage);

        return "dashboard";
    }

    @GetMapping("event-store")
    public String eventStore(Model model, @RequestParam(defaultValue = "1") String page) {
        int currentPage = Integer.parseInt(page);
        var eventsPerPage = 10;

        var events = eventStore.getPaginatedEventsWithData(currentPage, eventsPerPage)
                .stream()
                .map(e -> new DomainEventDTO(e.getId(), e.getType(), e.getDate(), e.getAggregateName(), e.getAggregateId(), e.getPublished(), e.getData()))
                .collect(Collectors.toList());

        model.addAttribute("events", events);

        var eventsCount = eventStore.getEventsCount();

        model.addAttribute("eventsCount", eventsCount);

        setEventsPaginationAttributes(model, currentPage, eventsPerPage, eventsCount);

        return "eventStore";
    }

    private void setTransactionsAttributes(Model model, int currentTransactionsPage) {
        var transactionsPerPage = 10;

        var transactionsCount = transactionRepository.getTransactionsCount();

        model.addAttribute("transactionsCount", transactionsCount);

        var transactions = transactionRepository.getAllPaginated(currentTransactionsPage, transactionsPerPage)
                .stream()
                .map(Transaction::toDTO)
                .collect(Collectors.toList());

        model.addAttribute("transactions", transactions);

        int totalPages =  (int) Math.ceil((double) transactionsCount / transactionsPerPage);
        model.addAttribute("totalTransactionsPages", totalPages);

        model.addAttribute("currentTransactionsPage", currentTransactionsPage);

        Integer previousPage = (currentTransactionsPage > 1) ? (currentTransactionsPage - 1) : null;
        model.addAttribute("previousTransactionsPage", previousPage);

        Integer nextPage = (currentTransactionsPage * transactionsPerPage < transactionsCount) ? (currentTransactionsPage + 1) : null;
        model.addAttribute("nextTransactionsPage", nextPage);
    }

    private void setSaleOrdersAttributes(Model model, int currentSaleOrdersPage) {
        int saleOrdersPerPage = 10;

        var saleOrdersCount = saleOrderRepository.getSaleOrdersCount();

        model.addAttribute("saleOrdersCount", saleOrdersCount);

        var transactions = saleOrderRepository.getPaginatedProjections(currentSaleOrdersPage, saleOrdersPerPage)
                .stream()
                .map(SaleOrderDTO::new)
                .collect(Collectors.toList());

        model.addAttribute("saleOrders", transactions);

        int totalPages =  (int) Math.ceil((double) saleOrdersCount / saleOrdersPerPage);
        model.addAttribute("totalSaleOrdersPages", totalPages);

        model.addAttribute("currentSaleOrdersPage", currentSaleOrdersPage);

        Integer previousPage = (currentSaleOrdersPage > 1) ? (currentSaleOrdersPage - 1) : null;
        model.addAttribute("previousSaleOrdersPage", previousPage);

        Integer nextPage = (currentSaleOrdersPage * saleOrdersPerPage < saleOrdersCount) ? (currentSaleOrdersPage + 1) : null;
        model.addAttribute("nextSaleOrdersPage", nextPage);
    }

    private static void setEventsPaginationAttributes(Model model, int currentPage, int eventsPerPage, int eventsCount) {
        int totalPages =  (int) Math.ceil((double) eventsCount / eventsPerPage);
        model.addAttribute("totalPages", totalPages);

        model.addAttribute("currentPage", currentPage);

        Integer previousPage = (currentPage > 1) ? (currentPage - 1) : null;
        model.addAttribute("previousPage", previousPage);

        Integer nextPage = (currentPage * eventsPerPage < eventsCount) ? (currentPage + 1) : null;
        model.addAttribute("nextPage", nextPage);
    }
}
