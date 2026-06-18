package ru.yandex.practicum.commerce.delivery;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;
import ru.yandex.practicum.commerce.delivery.config.DeliveryProperties;

@SpringBootApplication(scanBasePackages = "ru.yandex.practicum.commerce")
@EnableFeignClients(basePackages = "ru.yandex.practicum.commerce.client")
@EnableConfigurationProperties(DeliveryProperties.class)
public class DeliveryApplication {

    public static void main(String[] args) {
        SpringApplication.run(DeliveryApplication.class, args);
    }
}