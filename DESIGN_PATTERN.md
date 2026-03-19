# Choix des Design Patterns : Builder et State

J'ai choisi d'implémenter deux Design Patterns distincts qui répondent à deux problématiques métiers différentes liées à l'entité `Reservation`.

## Le pattern créationnel : Builder

### Problématique
La création d'une réservation (`Reservation`) nécessite plusieurs informations (l'ID de la salle, l'ID du membre, les dates de début et de fin) ainsi que la validation de certaines règles (les dates ne doivent pas être nulles, le statut initial doit être défini par défaut sur `CONFIRMED`).
L'utilisation d'un constructeur classique ou de multiples "setters" rend le code de création confus, sujet à l'oubli de paramètres, et peu lisible au sein du service.

### Solution : Le Builder
J'ai implémenté une classe statique interne `Builder` dans l'entité `Reservation` (cf. `Reservation.java`).
- Il permet de construire une réservation de manière "fluente" (Fluent API) étape par étape (`.withRoom()`, `.withMember()`, etc.).
- Il centralise la logique d'initialisation : la méthode `build()` valide la présence des identifiants et assigne par défaut la date de création actuelle si aucune date de début n'est fournie.
- Cela rend le code dans `ReservationService.createReservation()` beaucoup plus élégant et robuste.

---

## Le pattern comportemental : State

### Problématique
Une réservation possède un cycle de vie précis représenté par un statut (`ReservationStatus`) : `CONFIRMED`, `CANCELLED`, ou `COMPLETED`.
Les règles métiers dictent que certaines transitions sont impossibles. Par exemple, on ne peut pas "compléter" une réservation déjà "annulée", ni "annuler" une réservation déjà "complétée". Gérer cela avec des séries de `if/else` ou des `switch` dans le service alourdit considérablement le code métier.

### Solution : Le pattern State
J'ai créé une interface `ReservationState` définissant les actions possibles (`cancel()` et `complete()`).
- J'ai ensuite créé une classe concrète pour chaque état existant : `ConfirmedState`, `CancelledState`, et `CompletedState`.
- Lorsqu'une action est appelée sur l'état `ConfirmedState`, elle modifie le statut de la réservation.
- Si une action interdite est appelée (ex: `cancel()` sur l'état `CompletedState`), la classe d'état lève une `IllegalStateException`.
- Une `ReservationStateFactory` permet d'instancier dynamiquement la bonne classe d'état en fonction du statut de la réservation stockée en base.

Le Builder masque la complexité de création, et le State masque la complexité des validations de cycle de vie.
