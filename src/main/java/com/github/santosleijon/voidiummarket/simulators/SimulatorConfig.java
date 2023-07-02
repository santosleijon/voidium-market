package com.github.santosleijon.voidiummarket.simulators;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

import java.math.BigDecimal;

@ConfigurationProperties(prefix = "simulator")
@ConfigurationPropertiesScan
public class SimulatorConfig {

    private int sellerMinUnitsCount = 1;
    private int sellerMaxUnitsCount = 4;
    private BigDecimal sellerMinPricePerUnit = BigDecimal.valueOf(10.00);
    private BigDecimal sellerMaxPricePerUnit = BigDecimal.valueOf(20.00);

    private int buyerMinUnitsCount = 1;
    private int buyerMaxUnitsCount = 4;
    private BigDecimal buyerMinPricePerUnit = BigDecimal.valueOf(5.00);
    private BigDecimal buyerMaxPricePerUnit = BigDecimal.valueOf(15.00);

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

    public int getBuyerMinUnitsCount() {
        return buyerMinUnitsCount;
    }

    public void setBuyerMinUnitsCount(int buyerMinUnitsCount) {
        this.buyerMinUnitsCount = buyerMinUnitsCount;
    }

    public int getBuyerMaxUnitsCount() {
        return buyerMaxUnitsCount;
    }

    public void setBuyerMaxUnitsCount(int buyerMaxUnitsCount) {
        this.buyerMaxUnitsCount = buyerMaxUnitsCount;
    }

    public BigDecimal getBuyerMinPricePerUnit() {
        return buyerMinPricePerUnit;
    }

    public void setBuyerMinPricePerUnit(BigDecimal buyerMinPricePerUnit) {
        this.buyerMinPricePerUnit = buyerMinPricePerUnit;
    }

    public BigDecimal getBuyerMaxPricePerUnit() {
        return buyerMaxPricePerUnit;
    }

    public void setBuyerMaxPricePerUnit(BigDecimal buyerMaxPricePerUnit) {
        this.buyerMaxPricePerUnit = buyerMaxPricePerUnit;
    }
}
