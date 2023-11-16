package com.github.santosleijon.voidiummarket.simulators;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Random;

@Component
public class RandomUtil {

    private static final Random random = new Random();

    @Autowired
    public RandomUtil() {
    }

    public int getRandomInt(int min, int max) {
        if (min > max) {
            throw new IllegalArgumentException("min must be less than or equal to max");
        }

        return random.nextInt(max - min + 1) + min;
    }

    public BigDecimal getRandomBigDecimal(BigDecimal min, BigDecimal max) {
        if (min.compareTo(max) > 0) {
            throw new IllegalArgumentException("min must be less than or equal to max");
        }

        double randomWithinBounds = random.nextDouble() * (max.doubleValue() - min.doubleValue()) + min.doubleValue();

        return BigDecimal.valueOf(randomWithinBounds).setScale(2, RoundingMode.HALF_UP);
    }
}
