# Elevator Simulator

En heissimulator bygget med JavaFX, basert på MVC-arkitektur og multitråding.

## Forutsetninger

- Java 21 eller nyere
- Maven 3.6 eller nyere

## Project Structure

```
src/main/java/demo/elevatorsimulator/
├── App.java                        # Startpunkt for applikasjonen
├── model/
│   ├── Elevator.java               # Håndterer kølogikk og etasjetilstand
│   └── FloorButton.java            # Logikk for etasjeknapper
├── controller/
│   └── ElevatorController.java     # Kobler knappene til heismodellen
└── view/
    └── ElevatorView.java           # JavaFX-grensesnitt og knapper
```

## Kjøre applikasjonen

For å starte applikasjonen, kjør følgende kommando i terminalen:

```bash
mvn clean javafx:run
```
