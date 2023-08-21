package com.github.santosleijon.voidiummarket.common.eventstreaming;

import com.github.santosleijon.voidiummarket.purchaseorders.PurchaseOrder;
import com.github.santosleijon.voidiummarket.saleorders.SaleOrder;
import com.github.santosleijon.voidiummarket.transactions.Transaction;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaTopicConfig {

    @Value(value = "${spring.kafka.bootstrap-servers}")
    private String bootstrapAddress;

    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        return new KafkaAdmin(configs);
    }

    @Bean
    public NewTopic purchaseOrders() {
        return new NewTopic(PurchaseOrder.aggregateName, 1, (short) 1);
    }

    @Bean
    public NewTopic saleOrders() {
        return new NewTopic(SaleOrder.aggregateName, 1, (short) 1);
    }

    @Bean
    public NewTopic transactions() {
        return new NewTopic(Transaction.aggregateName, 1, (short) 1);
    }
}
