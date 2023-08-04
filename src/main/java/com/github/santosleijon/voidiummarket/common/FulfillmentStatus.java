package com.github.santosleijon.voidiummarket.common;

import com.github.santosleijon.voidiummarket.transactions.Transaction;

import java.util.List;

public enum FulfillmentStatus {
    FULFILLED,
    PARTIALLY_FULFILLED,
    UNFULFILLED,
    UNKNOWN;

    public static FulfillmentStatus fromOrderTransactions(List<Transaction> orderTransactions, int orderedUnitsCount) {
        if (orderTransactions == null) {
            return FulfillmentStatus.UNKNOWN;
        }

        if (orderTransactions.isEmpty()) {
            return FulfillmentStatus.UNFULFILLED;
        }

        int fulfilledUnitsCount = orderTransactions.stream().reduce(0, (partialUnitsCountResult, transaction) -> partialUnitsCountResult + transaction.getUnitsCount(), Integer::sum);

        if (fulfilledUnitsCount < orderedUnitsCount) {
            return FulfillmentStatus.PARTIALLY_FULFILLED;
        }

        return FulfillmentStatus.FULFILLED;
    }
}
