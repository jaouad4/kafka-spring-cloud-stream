package ma.jaouad.handlers;

import ma.jaouad.events.PageEvent;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.Grouped;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.TimeWindows;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Date;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

@Component
public class PageEventHandler {

    // Supplier: Produit des événements périodiquement
    @Bean
    public Supplier<PageEvent> pageEventSupplier() {
        return () -> new PageEvent(
                Math.random() > 0.5 ? "P1" : "P2",
                Math.random() > 0.5 ? "U1" : "U2",
                new Date(),
                new Random().nextInt(9000) + 100  // Entre 100 et 9100 ms
        );
    }

    // Consumer: Consomme et affiche les événements
    @Bean
    public Consumer<PageEvent> pageEventConsumer() {
        return (input) -> {
            System.out.println("***********************");
            System.out.println("📄 Page: " + input.name());
            System.out.println("👤 User: " + input.user());
            System.out.println("⏱️  Duration: " + input.duration() + " ms");
            System.out.println("📅 Date: " + input.date());
            System.out.println("***********************");
        };
    }

    // Function: Traitement avec Kafka Streams
    @Bean
    public Function<KStream<String, PageEvent>, KStream<String, Long>> kstreamFunction() {
        return (input) -> input
                // Filtrer les événements avec durée > 100ms
                .filter((k, v) -> v.duration() > 100)

                // Transformer en (nom_page, durée)
                .map((k, v) -> new KeyValue<>(v.name(), v.duration()))

                // Grouper par nom de page avec les Serdes appropriés
                .groupByKey(Grouped.with(Serdes.String(), Serdes.Long()))

                // Fenêtre glissante de 5 secondes
                .windowedBy(TimeWindows.ofSizeWithNoGrace(Duration.ofSeconds(5)))

                // Compter le nombre d'événements dans chaque fenêtre
                .count(Materialized.as("page-count-store"))

                // Convertir en Stream
                .toStream()

                // Extraire uniquement la clé (nom de page) sans les informations de fenêtre
                .map((windowedKey, count) -> new KeyValue<>(windowedKey.key(), count));
    }
}
