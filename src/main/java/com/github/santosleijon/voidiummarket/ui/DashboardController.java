package com.github.santosleijon.voidiummarket.ui;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.santosleijon.voidiummarket.admin.StateClearer;
import com.github.santosleijon.voidiummarket.common.eventstore.DomainEventDTO;
import com.github.santosleijon.voidiummarket.common.eventstore.EventStore;
import com.github.santosleijon.voidiummarket.purchaseorders.PurchaseOrderDTO;
import com.github.santosleijon.voidiummarket.purchaseorders.PurchaseOrderRepository;
import com.github.santosleijon.voidiummarket.saleorders.SaleOrderDTO;
import com.github.santosleijon.voidiummarket.saleorders.SaleOrderRepository;
import com.github.santosleijon.voidiummarket.transactions.Transaction;
import com.github.santosleijon.voidiummarket.transactions.TransactionRepository;
import com.github.santosleijon.voidiummarket.transactions.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

import static org.springframework.web.bind.annotation.RequestMethod.DELETE;

@Controller
public class DashboardController {

    private final TransactionRepository transactionRepository;
    private final SaleOrderRepository saleOrderRepository;
    private final PurchaseOrderRepository purchaseOrderRepository;
    private final TransactionService transactionService;
    private final EventStore eventStore;
    private final StateClearer stateClearer;
    private final ObjectMapper objectMapper;

    @Autowired
    public DashboardController(TransactionRepository transactionRepository, SaleOrderRepository saleOrderRepository, PurchaseOrderRepository purchaseOrderRepository, TransactionService transactionService, EventStore eventStore, StateClearer stateClearer, ObjectMapper objectMapper) {
        this.transactionRepository = transactionRepository;
        this.saleOrderRepository = saleOrderRepository;
        this.purchaseOrderRepository = purchaseOrderRepository;
        this.transactionService = transactionService;
        this.eventStore = eventStore;
        this.stateClearer = stateClearer;
        this.objectMapper = objectMapper;
    }

    @GetMapping("/")
    public String priceChart(Model model) {
        var priceDetails = transactionService.getPriceDetailsPerMinute();

        model.addAttribute("priceDetails", priceDetails);

        try {
            model.addAttribute("priceDetailsJson", objectMapper.writeValueAsString(priceDetails));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        return "priceChart";
    }

    @GetMapping("admin")
    public String admin(
            Model model,
            @RequestParam(defaultValue = "1") String transactionsPage,
            @RequestParam(defaultValue = "1") String saleOrdersPage,
            @RequestParam(defaultValue = "1") String purchaseOrdersPage
    ) {
        int currentTransactionsPage = Integer.parseInt(transactionsPage);

        setTransactionsAttributes(model, currentTransactionsPage);

        int currentSaleOrdersPage = Integer.parseInt(saleOrdersPage);

        setSaleOrdersAttributes(model, currentSaleOrdersPage);

        int currentPurchaseOrdersPage = Integer.parseInt(purchaseOrdersPage);

        setPurchaseOrdersAttributes(model, currentPurchaseOrdersPage);

        return "admin";
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

    @RequestMapping(value = "/", method = DELETE)
    @ResponseBody
    public void deleteAll() {
        stateClearer.deleteAllData();
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

        var saleOrders = saleOrderRepository.getPaginatedProjections(currentSaleOrdersPage, saleOrdersPerPage)
                .stream()
                .map(SaleOrderDTO::new)
                .toList();

        model.addAttribute("saleOrders", saleOrders);

        int totalPages =  (int) Math.ceil((double) saleOrdersCount / saleOrdersPerPage);
        model.addAttribute("totalSaleOrdersPages", totalPages);

        model.addAttribute("currentSaleOrdersPage", currentSaleOrdersPage);

        Integer previousPage = (currentSaleOrdersPage > 1) ? (currentSaleOrdersPage - 1) : null;
        model.addAttribute("previousSaleOrdersPage", previousPage);

        Integer nextPage = (currentSaleOrdersPage * saleOrdersPerPage < saleOrdersCount) ? (currentSaleOrdersPage + 1) : null;
        model.addAttribute("nextSaleOrdersPage", nextPage);
    }

    private void setPurchaseOrdersAttributes(Model model, int currentPurchaseOrdersPage) {
        int purchaseOrdersPerPage = 10;

        var purchaseOrdersCount = purchaseOrderRepository.getPurchaseOrdersCount();

        model.addAttribute("purchaseOrdersCount", purchaseOrdersCount);

        var purchaseOrders = purchaseOrderRepository.getPaginatedProjections(currentPurchaseOrdersPage, purchaseOrdersPerPage)
                .stream()
                .map(PurchaseOrderDTO::new)
                .toList();

        model.addAttribute("purchaseOrders", purchaseOrders);

        int totalPages =  (int) Math.ceil((double) purchaseOrdersCount / purchaseOrdersPerPage);
        model.addAttribute("totalPurchaseOrdersPages", totalPages);

        model.addAttribute("currentPurchaseOrdersPage", currentPurchaseOrdersPage);

        Integer previousPage = (currentPurchaseOrdersPage > 1) ? (currentPurchaseOrdersPage - 1) : null;
        model.addAttribute("previousPurchaseOrdersPage", previousPage);

        Integer nextPage = (currentPurchaseOrdersPage * purchaseOrdersPerPage < purchaseOrdersCount) ? (currentPurchaseOrdersPage + 1) : null;
        model.addAttribute("nextPurchaseOrdersPage", nextPage);
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
