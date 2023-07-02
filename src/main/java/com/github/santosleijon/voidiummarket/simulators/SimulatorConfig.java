package com.github.santosleijon.voidiummarket.simulators;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

import java.math.BigDecimal;
import java.util.Currency;

@ConfigurationProperties(prefix = "simulator")
@ConfigurationPropertiesScan
public class SimulatorConfig {

    private int sellerExecutionIntervalSeconds = 2;
    private int sellerMinUnitsCount = 1;
    private int sellerMaxUnitsCount = 5;
    private BigDecimal sellerMinPricePerUnit = BigDecimal.valueOf(10.00);
    private BigDecimal sellerMaxPricePerUnit = BigDecimal.valueOf(20.00);

    private Currency currency = Currency.getInstance("SEK");

    public int getSellerExecutionIntervalSeconds() {
        return sellerExecutionIntervalSeconds;
    }

    public void setSellerExecutionIntervalSeconds(int sellerExecutionIntervalSeconds) {
        this.sellerExecutionIntervalSeconds = sellerExecutionIntervalSeconds;
    }

    public int getSellerMinUnitsCount() {
        return sellerMinUnitsCount;
    }

    public void setSellerMinUnitsCount(int sellerMinUnitsCount) {
        this.sellerMinUnitsCount = sellerMinUnitsCount;
    }

    public int getSellerMaxUnitsCount() {
        return sellerMaxUnitsCount;
    }

    public void setSellerMaxUnitsCount(int sellerMaxUnitsCount) {
        this.sellerMaxUnitsCount = sellerMaxUnitsCount;
    }

    public BigDecimal getSellerMinPricePerUnit() {
        return sellerMinPricePerUnit;
    }

    public void setSellerMinPricePerUnit(BigDecimal sellerMinPricePerUnit) {
        this.sellerMinPricePerUnit = sellerMinPricePerUnit;
    }

    public BigDecimal getSellerMaxPricePerUnit() {
        return sellerMaxPricePerUnit;
    }

    public void setSellerMaxPricePerUnit(BigDecimal sellerMaxPricePerUnit) {
        this.sellerMaxPricePerUnit = sellerMaxPricePerUnit;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }
}
