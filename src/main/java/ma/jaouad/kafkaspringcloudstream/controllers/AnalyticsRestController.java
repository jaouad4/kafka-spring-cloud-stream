package ma.jaouad.kafkaspringcloudstream.controllers;

import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.Windowed;
import org.apache.kafka.streams.state.KeyValueIterator;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyWindowStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.binder.kafka.streams.InteractiveQueryService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestController
public class AnalyticsRestController {

    @Autowired
    private InteractiveQueryService interactiveQueryService;

    @GetMapping(path = "/analytics", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Map<String, Long>> analytics() {
        return Flux.interval(Duration.ofSeconds(1))
                .map(sequence -> {
                    Map<String, Long> stringLongMap = new HashMap<>();

                    try {
                        ReadOnlyWindowStore<String, Long> windowStore =
                                interactiveQueryService.getQueryableStore(
                                        "page-count-store",
                                        QueryableStoreTypes.windowStore()
                                );

                        Instant now = Instant.now();
                        Instant fromTime = now.minusSeconds(5);

                        long p1Count = 0;
                        long p2Count = 0;

                        try (KeyValueIterator<Windowed<String>, Long> iterator =
                                     windowStore.fetchAll(fromTime, now)) {

                            while (iterator.hasNext()) {
                                KeyValue<Windowed<String>, Long> next = iterator.next();
                                String key = next.key.key();
                                Long value = next.value;

                                if ("P1".equals(key)) {
                                    p1Count = Math.max(p1Count, value);
                                } else if ("P2".equals(key)) {
                                    p2Count = Math.max(p2Count, value);
                                }
                            }
                        }

                        stringLongMap.put("P1", p1Count);
                        stringLongMap.put("P2", p2Count);

                        System.out.println("📊 Analytics: P1=" + p1Count + ", P2=" + p2Count);
                    } catch (Exception e) {
                        System.err.println("⚠️ Erreur: " + e.getMessage());
                        stringLongMap.put("P1", 0L);
                        stringLongMap.put("P2", 0L);
                    }

                    return stringLongMap;
                })
                .doOnSubscribe(sub -> System.out.println("✅ Client SSE connecté"))
                .doOnCancel(() -> System.out.println("❌ Client SSE déconnecté"));
    }
}
