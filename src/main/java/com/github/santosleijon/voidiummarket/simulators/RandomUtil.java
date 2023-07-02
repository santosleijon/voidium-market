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
        return random.nextInt(max - min + 1) + min;
    }

    public BigDecimal getRandomBigDecimal(BigDecimal min, BigDecimal max) {
        var randomDouble = random.nextDouble(max.doubleValue() - min.doubleValue() + 1) + min.doubleValue();
        return BigDecimal.valueOf(randomDouble).setScale(2, RoundingMode.HALF_UP);
    }
}
