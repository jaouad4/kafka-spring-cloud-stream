package ma.jaouad.kafkaspringcloudstream;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {
        "ma.jaouad.kafkaspringcloudstream",
        "ma.jaouad.controllers",
        "ma.jaouad.handlers",
        "ma.jaouad.events"
})
public class KafkaSpringCloudStreamApplication {

    public static void main(String[] args) {
        SpringApplication.run(KafkaSpringCloudStreamApplication.class, args);
    }
}
