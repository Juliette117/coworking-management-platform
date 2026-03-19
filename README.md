


# Platforme de gestion de Coworking

*Projet réalisé dans le cadre d'un TP d'Architecture Logicielle.*

Une plateforme de réservation de salles de coworking basée sur une architecture microservices.



##  Services

### Room Service (Port: 8082)
Gestion des salles de coworking
- CRUD complet sur les salles
- Types de salles : OPEN_SPACE, MEETING_ROOM, PRIVATE_OFFICE
- Gestion de la disponibilité en temps réel
- Capacité et tarification horaire

### Member Service (Port: 8083)
Gestion des membres et abonnements
- CRUD complet sur les membres
- Types d'abonnement : BASIC (2 réservations), PRO (5 réservations), ENTERPRISE (10 réservations)
- Suspension automatique lors du dépassement de quota
- Base de données H2 avec console accessible

### Reservation Service (Port: 8084)
Gestion des réservations
- Création de réservations avec validation cross-service
- États : CONFIRMED, CANCELLED, COMPLETED
- Intégration Kafka pour les événements asynchrones
- Design Patterns : Builder (création) et State (gestion du cycle de vie)

### API Gateway (Port: 8090)
Point d'entrée unique de l'application
- Routage vers les différents services
- Load balancing avec Eureka
- Configuration des routes :
  - `/members/**` → member-service
  - `/rooms/**` → room-service
  - `/reservations/**` → reservation-service

### Discovery Server - Eureka (Port: 8761)
Registre de services
- Découverte automatique des microservices
- Health monitoring des services

### Config Server (Port: 8888)
Configuration centralisée
- Fichiers de configuration pour chaque service
- Gestion des bases de données et configurations Kafka

## Technologies

- **Java 21**
- **Spring Boot**
- **Spring Cloud**
- **Apache Kafka** - Système de messagerie
- **Eureka** - Service Discovery
- **Spring Cloud Gateway** - API Gateway
- **H2 Database** - Base de données en mémoire
- **Maven** - Gestion des dépendances
- **Swagger/OpenAPI** - Documentation API

<br>

## 🚀 Lancer l'application

### Prérequis
- Java 21 ou supérieur
- Maven 3.6+
- Apache Kafka

### 1. Démarrer l'infrastructure

```bash

# Démarrer Config Server
# Se positiomnner dans config-server

mvn spring-boot:run

# Démarrer Discovery Server (Eureka)
# Se positionner dans discovery-server

mvn spring-boot:run
```

### 2. Démarrer les microservices

```bash
# Démarrer les services
#Se positionner dans le service

mvn spring-boot:run
```

WIP : Terminer l'implémentation de Kafka

### 3. Vérification

- **Eureka** : http://localhost:8761
- **API Gateway** : http://localhost:8090

<br>

## Endpoints API

### Room Service
- `GET /api/rooms` - Lister toutes les salles
- `GET /api/rooms/{id}` - Obtenir une salle
- `POST /api/rooms` - Créer une salle
- `PUT /api/rooms/{id}` - Modifier une salle
- `DELETE /api/rooms/{id}` - Supprimer une salle
- `PATCH /api/rooms/{id}/availability` - Modifier la disponibilité

### Member Service
- `GET /api/members` - Lister tous les membres
- `GET /api/members/{id}` - Obtenir un membre
- `POST /api/members` - Créer un membre
- `PUT /api/members/{id}` - Modifier un membre
- `DELETE /api/members/{id}` - Supprimer un membre
- `PATCH /api/members/{id}/status` - Modifier le statut de suspension

### Reservation Service
- `GET /api/reservations` - Lister toutes les réservations
- `GET /api/reservations/{id}` - Obtenir une réservation
- `POST /api/reservations` - Créer une réservation
- `POST /api/reservations/{id}/cancel` - Annuler une réservation
- `POST /api/reservations/{id}/complete` - Compléter une réservation



## Base de données

### H2 Console Access
- **URL** : `jdbc:h2:mem:member_db`
- **Username** : `sa`
- Pas de **Password**

<br>

## Configuration

### Ports par défaut
- API Gateway : 8090
- Discovery Server : 8761
- Config Server : 8888
- Room Service : 8081
- Member Service : 8083
- Reservation Service : 8082
- Kafka : 9092

<br>

- L'ordre de démarrage recommandé : Config Server → Discovery Server → Services → API Gateway


