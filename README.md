# Elevator Simulator

En heissimulator bygget med JavaFX, basert på MVC-arkitektur og multitråding.

## Forutsetninger

- Java 24 eller nyere
- Maven 3.6 eller nyere

## Prosjektstruktur

```
src/main/java/demo/elevatorsimulator/
├── App.java                          # Startpunkt for applikasjonen
├── model/
│   ├── Elevator.java                 # Heislogikk, kjører som egen tråd (Runnable)
│   ├── RequestManager.java           # Trådsikker håndtering av etasjeforespørsler
│   ├── FloorButton.java              # Logikk for etasjeknapper
│   ├── ElevatorState.java            # Enum: heisens tilstand (IDLE, MOVING, DOOR_OPERATION)
│   ├── DoorState.java                # Enum: dørens tilstand (OPEN, OPENING, CLOSED, CLOSING)
│   └── Direction.java                # Enum: heisens retning (UP, DOWN, IDLE)
├── controller/
│   └── ElevatorController.java       # Kobler knappene til heismodellen
└── view/
    └── ElevatorView.java             # JavaFX-grensesnitt og knapper
```

## Arkitektur

Prosjektet følger MVC-arkitektur og bruker multitråding:

- **Model** — `Elevator` implementerer `Runnable` og kjører på en egen tråd. `RequestManager` håndterer køen trådsikkert
  med `ReentrantLock` og `Condition`.
- **Controller** — Oppretter knappene og kobler dem til `Elevator.addRequest()`.
- **View** — Bygger JavaFX-grensesnittet og binder knappene til kontrolleren.

## Kjøre applikasjonen

```bash
mvn clean javafx:run
```
