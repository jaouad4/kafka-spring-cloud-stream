package ma.jaouad.controllers;

import ma.jaouad.events.PageEvent;

import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.Random;

@RestController
public class PageEventController {

    private final StreamBridge streamBridge;

    public PageEventController(StreamBridge streamBridge) {
        this.streamBridge = streamBridge;
    }

    @GetMapping("/publish")
    public PageEvent publish(
            @RequestParam String name,
            @RequestParam String topic) {

        PageEvent event = new PageEvent(
                name,
                Math.random() > 0.5 ? "U1" : "U2",
                new Date(),
                new Random().nextInt(10000) + 10
        );

        streamBridge.send(topic, event);
        return event;
    }
}
