# Event Driven Architecture avec Apache Kafka et Spring Cloud Streams

[![Java](https://img.shields.io/badge/Java-25-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Kafka](https://img.shields.io/badge/Apache%20Kafka-Latest-black.svg)](https://kafka.apache.org/)

## 📋 Description

Activité pratique démontrant l'implémentation d'une architecture événementielle (Event-Driven Architecture) utilisant **Apache Kafka** et **Spring Cloud Streams**. Cette activité illustre les concepts de streaming de données en temps réel, de traitement d'événements et d'analyse de données avec Kafka Streams.

## 🎯 Objectifs du Projet

- Comprendre les fondements de l'architecture événementielle
- Maîtriser Apache Kafka (Producer, Consumer, Topics)
- Utiliser Spring Cloud Streams pour l'abstraction de messaging
- Implémenter du traitement de flux en temps réel avec Kafka Streams
- Visualiser des analytics en temps réel dans une interface web

## 🏗️ Architecture
![Architecture](assets/1-Architecture.svg)

## 🚀 Technologies Utilisées

- **Java 25**
- **Spring Boot 3.x**
- **Spring Cloud Streams** - Abstraction pour le messaging
- **Apache Kafka** - Plateforme de streaming distribué
- **Kafka Streams** - Bibliothèque de traitement de flux
- **Docker & Docker Compose** - Conteneurisation
- **Chart.js** - Visualisation des données

## 📦 Prérequis

- Java JDK 25 ou supérieur
- Docker Desktop installé et démarré
- IntelliJ IDEA (recommandé) ou tout autre IDE Java
- Maven (intégré avec IntelliJ)

## 🔧 Installation et Démarrage

### 1. Cloner le repository

```bash
git clone https://github.com/jaouad4/kafka-spring-cloud-stream
cd kafka-spring-cloud-stream
```

### 2. Démarrer Kafka et Zookeeper avec Docker

```bash
docker-compose up -d
```
![docker-compose up -d](assets/2-Docker_compose_command.png)
![docker-compose up -d results](assets/2.5-Docker_compose_results.png)
Vérifier que les conteneurs sont actifs :
```bash
docker ps
```
![docker ps](assets/3-Docker_ps.png)
![docker ps result 1](assets/3.5.1-Docker_ps_result1.png)
![docker ps result 2](assets/3.5.2-Docker_ps_result2.png)

### 3. Démarrer l'application Spring Boot

Avec Maven :
```bash
mvn spring-boot:run
```

Ou depuis IntelliJ : *Run → Run 'Application'*

<!-- [Screenshot : IntelliJ IDEA avec l'application démarrée, console montrant "Started KafkaSpringCloudStreamApplication"] -->

### 4. Tester l'application

L'application sera disponible sur `http://localhost:8080`

## 📝 Fonctionnalités Implémentées

### 1. Producer REST (Topic T2)

Envoyer un événement via HTTP :
```
GET http://localhost:8080/publish?name=P1&topic=T2
```

**Paramètres :**
- `name` : Nom de la page (P1, P2, etc.)
- `topic` : Topic Kafka de destination
<!-- [Screenshot : Navigateur web avec l'URL http://localhost:8080/publish?name=P1&topic=T2 et la réponse JSON affichée] -->

<!-- [Screenshot : Postman ou navigateur montrant la requête et la réponse avec les détails de PageEvent] -->

### 2. Consumer Kafka

Consomme automatiquement les messages du topic T2 et les affiche dans la console.

<!-- [Screenshot : Console IntelliJ montrant les logs du Consumer avec les étoiles et les messages PageEvent reçus] -->

**Exemple de sortie console :**
```
Consuming: PageEvent[name=P1, user=U2, date=Sun Oct 05 18:30:45 CET 2025, duration=1567]
```

### 3. Supplier (Auto-Producer vers T3)

Génère automatiquement des événements PageEvent toutes les 200ms vers le topic T3.

**Configuration dans `application.properties` :**
```properties
spring.cloud.stream.bindings.pageEventSupplier-out-0.producer.poller.fixed-delay=200
```

<!-- [Screenshot : Fichier application.properties ouvert dans IntelliJ avec les configurations en surbrillance] -->

### 4. Kafka Streams - Analytics en Temps Réel

Traite le flux d'événements du topic T3 :
- **Filtrage** : Durée de visite > 100ms
- **Groupement** : Par nom de page (P1, P2)
- **Fenêtrage** : Fenêtre glissante de 5 secondes
- **Agrégation** : Comptage du nombre de visites
- **Output** : Résultats publiés sur topic T4

<!-- [Screenshot : Code source de la fonction kstreamFunction dans IntelliJ] -->

### 5. Interface Web de Visualisation

Accéder à : `http://localhost:8080/index.html`

Affiche en temps réel :
- Graphique à barres du nombre de visites par page
- Mise à jour toutes les secondes

<!-- [Screenshot : Page web avec le graphique Chart.js montrant les barres P1 et P2 avec des valeurs] -->

<!-- [Screenshot : Animation ou GIF montrant le graphique se mettre à jour en temps réel] -->

## 🧪 Tests avec Kafka Console

### Tester le Producer Console

```bash
docker exec -it bdcc-kafka-broker kafka-console-producer \
  --bootstrap-server localhost:9092 \
  --topic T1
```

<!-- [Screenshot : Terminal avec kafka-console-producer actif, prêt à recevoir des messages] -->

### Tester le Consumer Console

```bash
docker exec -it bdcc-kafka-broker kafka-console-consumer \
  --bootstrap-server localhost:9092 \
  --topic T1 \
  --from-beginning
```

<!-- [Screenshot : Deux terminaux côte à côte - Producer à gauche envoyant "Hello", Consumer à droite recevant "Hello"] -->

### Consommer les messages du Topic T2

```bash
docker exec -it bdcc-kafka-broker kafka-console-consumer \
  --bootstrap-server localhost:9092 \
  --topic T2
```

<!-- [Screenshot : Terminal montrant les messages JSON PageEvent reçus sur le topic T2] -->

### Consommer les messages du Topic T3 (Supplier)

```bash
docker exec -it bdcc-kafka-broker kafka-console-consumer \
  --bootstrap-server localhost:9092 \
  --topic T3
```

<!-- [Screenshot : Terminal montrant le flux continu de messages générés automatiquement toutes les 200ms] -->

### Visualiser les résultats Kafka Streams (T4)

```bash
docker exec -it bdcc-kafka-broker kafka-console-consumer \
  --bootstrap-server localhost:9092 \
  --topic T4 \
  --property print.key=true \
  --property print.value=true \
  --property key.deserializer=org.apache.kafka.common.serialization.StringDeserializer \
  --property value.deserializer=org.apache.kafka.common.serialization.LongDeserializer
```

<!-- [Screenshot : Terminal montrant les résultats du Kafka Streams avec format "P1	150" et "P2	120"] -->

**Exemple de sortie :**
```
P1	145
P2	138
P1	150
P2	142
```

## 📊 Structure du Projet

```
kafka-spring-cloud-stream/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── ma/youssfi/
│   │   │       ├── KafkaSpringCloudStreamApplication.java
│   │   │       ├── controllers/
│   │   │       │   └── PageEventController.java
│   │   │       ├── events/
│   │   │       │   └── PageEvent.java
│   │   │       └── handlers/
│   │   │           └── PageEventHandler.java
│   │   └── resources/
│   │       ├── application.properties
│   │       └── static/
│   │           └── index.html
├── docker-compose.yml
├── pom.xml
└── README.md
```

<!-- [Screenshot : Explorateur de fichiers IntelliJ montrant l'arborescence complète du projet] -->

## 🔍 Concepts Clés

### PageEvent

Record Java représentant un événement de visite de page :
```java
public record PageEvent(
    String name,      // Nom de la page (P1, P2)
    String user,      // Utilisateur (U1, U2)
    Date date,        // Date de la visite
    long duration     // Durée de visite en ms
) {}
```

<!-- [Screenshot : Code source de PageEvent.java dans IntelliJ] -->

### Spring Cloud Stream Bindings

- **Consumer** : `<functionName>-in-<index>`
- **Producer/Supplier** : `<functionName>-out-<index>`
- **Function** : Input et Output définis

### Kafka Streams Operations

- **filter()** : Filtrer les événements selon une condition
- **map()** : Transformer les événements
- **groupByKey()** : Grouper par clé
- **windowedBy()** : Définir une fenêtre temporelle
- **count()** : Compter les événements dans chaque groupe/fenêtre

<!-- [Screenshot : Code annoté montrant chaque opération Kafka Streams avec des commentaires] -->

## 📈 Démonstration Complète

### Scénario de Test Complet

1. **Démarrage des services**

<!-- [Screenshot : Docker Desktop avec tous les conteneurs démarrés] -->

2. **Publication d'événements via REST**

<!-- [Screenshot : Multiples onglets de navigateur envoyant des requêtes publish] -->

3. **Consommation en temps réel**

<!-- [Screenshot : 3 terminaux affichant simultanément T2, T3 et T4] -->

4. **Visualisation Web**

<!-- [Screenshot : Page web finale avec le graphique animé montrant les statistiques en temps réel] -->

## 🐛 Troubleshooting

### Docker ne démarre pas
```bash
# Vérifier que Docker Desktop est lancé
docker info

# Redémarrer les conteneurs
docker-compose down
docker-compose up -d
```

<!-- [Screenshot : Docker Desktop Settings ou message d'erreur résolu] -->

### Port 9092 déjà utilisé
Modifiez le port dans `docker-compose.yml` et `application.properties`.

<!-- [Screenshot : Modification du fichier docker-compose.yml avec le nouveau port] -->

### Les messages n'arrivent pas
Vérifiez que les topics et bindings correspondent dans la configuration.

<!-- [Screenshot : Fichier application.properties avec les bindings en surbrillance] -->

### Erreur au démarrage de l'application
```bash
# Nettoyer et recompiler
mvn clean install
```

<!-- [Screenshot : Console Maven montrant "BUILD SUCCESS"] -->

## 📚 Ressources

- [Vidéo du cours - Prof. Mohamed YOUSSFI](https://www.youtube.com/watch?v=8uY7JE_X_Fw)
- [Apache Kafka Documentation](https://kafka.apache.org/documentation/)
- [Spring Cloud Stream](https://spring.io/projects/spring-cloud-stream)
- [Kafka Streams Documentation](https://kafka.apache.org/documentation/streams/)

## 🎥 Vidéo de Démonstration

<!-- [Screenshot ou lien : Vidéo de démonstration du projet en action (si vous en créez une)] -->

## 📸 Galerie

### Configuration et Setup

<!-- [Screenshot : docker-compose.yml ouvert dans l'éditeur] -->

<!-- [Screenshot : pom.xml montrant les dépendances Spring Cloud Stream et Kafka] -->

### Code Principal

<!-- [Screenshot : PageEventController.java - méthode publish()] -->

<!-- [Screenshot : PageEventHandler.java - les trois beans (Consumer, Supplier, Function)] -->

### Résultats et Logs

<!-- [Screenshot : Console complète montrant tous les logs lors de l'exécution] -->

<!-- [Screenshot : Kafka console consumer avec un flux de données] -->

### Interface Utilisateur

<!-- [Screenshot : Code HTML/JavaScript de index.html] -->

<!-- [Screenshot : Inspecteur de navigateur montrant les requêtes en temps réel] -->

## 👨‍🎓 Auteur

**Votre Nom**  
Activité Pratique N°1 - Event Driven Architecture  
Professeur : Mohamed YOUSSFI  
Date : Octobre 2025

<!-- [Screenshot : Photo de profil ou avatar (optionnel)] -->

## 📄 Licence

Ce projet est réalisé dans un cadre académique.

---

## ✅ Checklist de Validation

- [x] Docker Desktop installé et démarré
- [x] Kafka et Zookeeper lancés avec `docker-compose up -d`
- [x] Application Spring Boot démarre sans erreur
- [x] Test du REST Producer fonctionnel
- [x] Consumer affiche les messages dans la console
- [x] Supplier génère des événements automatiquement
- [x] Kafka Streams traite et compte les événements
- [x] Interface web accessible et affiche les graphiques
- [x] Tous les fichiers commités sur GitHub
- [x] README.md complet et formaté
- [x] Screenshots ajoutés dans le README

<!-- [Screenshot : Repository GitHub montrant tous les fichiers commités avec un beau README] -->

---

⭐ Si ce projet vous a été utile, n'oubliez pas de mettre une étoile !

<!-- [Screenshot : Page GitHub du projet avec le bouton Star en évidence] -->
