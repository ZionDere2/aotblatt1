# AOT Aufgabenblatt 1 – Ameisenkolonie-Simulation

Diese README ist die Projektdokumentation für die Abgabe. Sie beschreibt das Systemdesign, die vorgesehenen Experimente, die Interpretation der Simulationsergebnisse sowie eine geeignete Brokering-Variante zur effizienteren Ausbeutung von Nahrungsquellen.

## Ausführen

```bash
cd aot_aufgabe01-master
./gradlew run
```

Falls das Ausführungsbit der Gradle-Datei auf einem System verloren gegangen ist, kann alternativ `bash ./gradlew run` verwendet werden.

## Kurzbeschreibung des Systemdesigns

Das Projekt implementiert eine agentenbasierte Ameisensimulation in Kotlin mit einer Compose-Desktop-Oberfläche. Die Umwelt ist ein zweidimensionales Raster (`Grid`), dessen Zellen Nahrung, Hindernisse, Nestmarkierung, Kapazitätsgrenzen und Pheromonwerte speichern. Ameisen (`AntAgent`) bewegen sich lokal über dieses Raster und treffen ihre Entscheidungen ausschließlich aus ihrer aktuellen Wahrnehmung: aktuelles Feld, direkt benachbarte Felder, Nahrungsmenge, Zugänglichkeit und Pheromonspuren.

Jede Ameise besitzt zwei Hauptzustände. Im Zustand `SEARCHING` sucht sie nach Nahrung. Findet sie Nahrung auf dem aktuellen Feld, nimmt sie Nahrung bis zur eigenen Kapazitätsgrenze auf. Liegt Nahrung in einer Nachbarzelle, bewegt sie sich bevorzugt dorthin. Andernfalls folgt sie vorhandenen Food-Pheromonen mit einem Explorationsanteil. Im Zustand `RETURNING` versucht sie, zum Nest zurückzukehren, um die transportierte Nahrung abzugeben. Dabei folgt sie bevorzugt Home-Pheromonen. Beim Laufen legt die Ameise zustandsabhängig Pheromone: suchende Ameisen markieren den Weg zum Nest mit Home-Pheromonen, zurückkehrende Ameisen markieren den Weg zur Nahrung mit Food-Pheromonen. Dadurch entsteht eine indirekte Koordination über Stigmergie.

Der `SimulationManager` koordiniert die diskrete Zeitschrittlogik. Pro Tick erzeugt er für jede Ameise eine lokale Wahrnehmung, sammelt die Aktionen und führt sie anschließend nach dem First-Come-First-Served-Prinzip aus. Danach sinkt die Energie aller Ameisen, tote Ameisen werden entfernt und aktive Pheromonfelder verdunsten mit der konfigurierten Evaporationsrate. Die Oberfläche erlaubt das Starten/Stoppen der Simulation, Einzelschritte, Geschwindigkeitsänderungen, Editieren von Ameisen/Nest/Nahrung/Hindernissen/Pheromonen und das Anpassen der Evaporationsrate. Damit können Experimente interaktiv reproduziert und gezielt variiert werden.

## Forschungsfrage

Die zentrale Forschungsfrage lautet: **Wie beeinflussen Pheromonverdunstung, Koloniegröße, Hindernisse und Transportkapazität die Effizienz, mit der eine Ameisenkolonie verteilte Nahrungsquellen findet, ausbeutet und zum Nest transportiert?** Effizienz wird dabei qualitativ über die Zeit bis zur stabilen Pfadbildung, die Menge der ins Nest transportierten Nahrung, die Anzahl überlebender Ameisen und die Robustheit bei veränderten Umweltbedingungen beurteilt.

## Experiment 1: Baseline mit einer entfernten Nahrungsquelle

**Motivation.** Das Baseline-Experiment dient als Referenz: Eine Kolonie startet an einem Nest, eine größere Nahrungsquelle liegt weit entfernt. Es soll sichtbar werden, ob die lokale Agentenlogik ohne zentrale Steuerung einen nutzbaren Transportpfad erzeugt.

**Aufbau.** Die Standardkonfiguration startet mit einem 250×250-Raster, 100 Ameisen am Nest bei `(25, 225)` und einer Nahrungsquelle mit 1000 Einheiten bei `(225, 25)`. Die Evaporationsrate beträgt `0.01`. Hindernisse sind in der Startkonfiguration nur lokal gesetzt und beeinflussen die lange Diagonalsuche kaum.

**Verlauf der Simulation.** Zu Beginn bewegen sich die Ameisen überwiegend zufällig bzw. trägheitsbasiert in die zuletzt erfolgreiche Richtung, weil noch keine relevanten Food-Pheromone existieren. Sobald einzelne Ameisen Nahrung finden, wechseln sie in den Rückkehrzustand und legen auf dem Rückweg Food-Pheromone ab. Andere suchende Ameisen können diese Spur aufnehmen, wodurch mehr Ameisen zur Nahrungsquelle geführt werden. Gleichzeitig legen suchende Ameisen Home-Pheromone, die zurückkehrenden Ameisen Orientierung zum Nest geben.

**Ergebnisse und Interpretation.** Die Simulation zeigt das erwartete selbstorganisierte Verhalten: Anfangs dominiert Exploration, später entsteht eine verstärkte Route zwischen Nest und Nahrungsquelle. Das System benötigt keine globale Karte und keinen zentralen Planer; die Pfadbildung entsteht aus lokaler Wahrnehmung und Pheromonverstärkung. Die Effizienz hängt jedoch stark davon ab, ob mindestens einige Ameisen die Quelle zufällig finden. Bei großer Distanz bleibt die Anlaufphase deshalb relativ lang. Für die Forschungsfrage bedeutet das: Pheromonbasierte Stigmergie eignet sich gut zur Ausbeutung bereits entdeckter Quellen, löst aber das initiale Suchproblem nur probabilistisch.

## Experiment 2: Einfluss der Pheromonverdunstung

**Motivation.** Pheromonverdunstung steuert den Trade-off zwischen Stabilität und Anpassungsfähigkeit. Zu niedrige Verdunstung kann alte Pfade dauerhaft konservieren; zu hohe Verdunstung verhindert stabile Rekrutierung.

**Aufbau.** Ausgehend von der Baseline wird die Evaporationsrate über die UI variiert, z. B. `0.00`, `0.01`, `0.05` und `0.10`. Die übrigen Parameter bleiben unverändert, damit die Wirkung der Verdunstung isoliert beobachtet werden kann.

**Verlauf der Simulationen.** Bei `0.00` bleiben frühe Pheromonspuren dauerhaft bestehen. Dadurch wird die erste erfolgreiche Route sehr stabil, auch wenn sie nicht die kürzeste oder effizienteste Route ist. Bei `0.01` entsteht ein Kompromiss: erfolgreiche Routen bleiben lange genug erhalten, schwächere Spuren verschwinden aber. Bei höheren Werten lösen sich Spuren schneller auf; die Kolonie bleibt explorativer, bildet aber schwerer einen dauerhaft stark genutzten Pfad.

**Ergebnisse und Interpretation.** Eine mittlere Verdunstung ist für diese Simulation am sinnvollsten. Ohne Verdunstung steigt die Gefahr von Pfad-Lock-in: zufällige frühe Entscheidungen dominieren dauerhaft. Mit zu hoher Verdunstung wird der Informationswert der Pheromone zu kurzlebig, sodass Ameisen häufig wieder in unkoordiniertes Suchen zurückfallen. Hinsichtlich der Forschungsfrage zeigt das Experiment, dass Verdunstung nicht nur ein Realismusdetail ist, sondern eine zentrale Regel zur Balance von Exploitation und Exploration darstellt.

## Experiment 3: Koloniegröße und individuelle Transportkapazität

**Motivation.** Koloniegröße und Transportkapazität bestimmen, wie viel parallele Exploration und wie viel Transportleistung pro erfolgreichem Fund möglich sind. Gleichzeitig erzeugen mehr Ameisen potenziell mehr Kollisionen und konkurrierende Spuren.

**Aufbau.** Die Baseline kann durch Anpassung der Startinitialisierung oder über die UI variiert werden: kleine Kolonie (z. B. 25 Ameisen), mittlere Kolonie (100 Ameisen) und große Kolonie (200 Ameisen). Zusätzlich kann die individuelle Kapazität von 1, 5 und 10 Einheiten betrachtet werden.

**Verlauf der Simulationen.** Kleine Kolonien erzeugen weniger parallele Suchbewegungen; dadurch dauert es länger, bis eine entfernte Quelle entdeckt wird. Große Kolonien decken das Raster schneller ab und verstärken gefundene Pfade schneller, allerdings steigt die lokale Dichte an stark genutzten Feldern. Höhere Transportkapazität reduziert die notwendige Anzahl erfolgreicher Hin- und Rückwege, erhöht aber nicht die Wahrscheinlichkeit, die Quelle initial zu finden.

**Ergebnisse und Interpretation.** Eine größere Kolonie verbessert vor allem die Entdeckungswahrscheinlichkeit und beschleunigt die frühe Verstärkung erfolgreicher Spuren. Eine höhere Kapazität verbessert dagegen die Ausbeutung einer bereits bekannten Quelle. Beide Parameter wirken also auf unterschiedliche Phasen des Problems: Koloniegröße hilft bei Exploration und Rekrutierung, Kapazität hilft bei der späteren Exploitation. Für die Forschungsfrage ist relevant, dass reine Erhöhung der Transportkapazität ineffektiv bleibt, wenn die Informationsverteilung über Nahrungsorte schlecht ist.

## Experiment 4: Hindernisse und Pfadrobustheit

**Motivation.** Reale Umgebungen enthalten blockierte oder schwer passierbare Bereiche. Dieses Experiment prüft, ob die lokale Pheromonlogik alternative Wege findet und ob bestehende Pfade nach Umweltänderungen angepasst werden können.

**Aufbau.** Zwischen Nest und Nahrungsquelle werden Hindernisbarrieren gesetzt. Zunächst bleibt eine Lücke offen, anschließend wird die Barriere verändert oder ein bereits etablierter Pfad blockiert. Die Evaporationsrate wird auf dem Baseline-Wert `0.01` gehalten.

**Verlauf der Simulationen.** Bei einer festen Barriere suchen Ameisen zunächst zufällig an den Hindernissen entlang, bis einzelne Agenten eine passierbare Lücke finden. Sobald darüber Nahrung transportiert wird, verstärken sich die Pheromone entlang der Umgehungsroute. Wird ein etablierter Pfad nachträglich blockiert, laufen kurzfristig noch viele Ameisen in Richtung der alten Spur. Durch Verdunstung verliert diese Spur aber allmählich an Bedeutung, während neue erfolgreiche Umgehungen verstärkt werden.

**Ergebnisse und Interpretation.** Die Simulation ist grundsätzlich robust gegenüber Hindernissen, solange alternative Wege existieren und die Verdunstung nicht zu niedrig ist. Hindernisse erhöhen die Suchzeit und machen Pfade länger, aber sie zerstören die Funktionsfähigkeit des Systems nicht. Dieses Ergebnis unterstützt die Annahme, dass stigmergische Koordination besonders in dynamischen oder partiell unbekannten Umgebungen geeignet ist, weil keine globale Neuplanung nötig ist. Allerdings reagiert das System nur verzögert, wenn alte Pheromonspuren sehr stark oder dauerhaft sind.

## Zusammenfassung und Ausblick

Die Experimente zeigen, dass die Ameisenkolonie Nahrungsquellen durch dezentrale Regeln finden und zunehmend effizient ausbeuten kann. Die wichtigste Erkenntnis ist die Trennung von Such- und Ausbeutungsphase: Pheromone sind sehr wirksam, sobald Informationen über eine Quelle existieren, aber die erste Entdeckung bleibt stark zufallsabhängig. Sinnvolle Anschlussaktivitäten wären quantitative Messläufe mit automatischem Logging, mehrere gleichzeitige Nahrungsquellen mit unterschiedlicher Ergiebigkeit, begrenzte Lebensdauer/Energiekosten pro Weglänge, statistische Wiederholungen mit verschiedenen Seeds und eine explizite Brokering-Erweiterung, die entdeckte Informationen strukturierter verteilt.

## Brokering-Variante zur effizienteren Ausbeutung

Eine sinnvolle Erweiterung ist ein **Informationsbroker am Nest** in Form eines einfachen Blackboard- bzw. Matchmaking-Brokers. Ameisen, die Nahrung gefunden und erfolgreich zum Nest zurückgebracht haben, melden dort Quelle, geschätzte Entfernung bzw. Pfadlänge, Restmenge und Aktualität der Information. Suchende Ameisen fragen beim Verlassen des Nests den Broker ab und erhalten eine Empfehlung für die aktuell attraktivste bekannte Quelle, z. B. gewichtet nach hoher Restmenge, kurzer Entfernung und frischer Meldung. Die konkrete Bewegung bleibt weiterhin dezentral und pheromonbasiert; der Broker gibt nur eine priorisierende Startinformation bzw. Zielhypothese.

Diese Variante ist geeignet, weil im aktuellen System eine deutliche Informationsasymmetrie besteht: einzelne erfolgreiche Ameisen besitzen wertvolles Wissen über eine Nahrungsquelle, während andere Ameisen dieses Wissen nur indirekt und verzögert über lokale Pheromone aufnehmen können. Besonders bei langen Wegen, hoher Verdunstung oder mehreren Quellen erreicht die Information nicht alle relevanten Agenten gleich schnell. Ein Nest-Broker reduziert diese Asymmetrie genau an dem Ort, den alle erfolgreichen Transporte regelmäßig passieren. Im Vergleich zu einem vollständig zentralen Steuerungsbroker bleibt das System robust und skalierbar, weil der Broker keine Einzelbewegungen plant und keine globale Kontrolle über alle Agenten benötigt. Im Vergleich zu reinem Pheromon-Brokering ist die Informationsweitergabe direkter: neue oder ergiebige Quellen können gezielt an ausrückende Ameisen vermittelt werden, statt nur zufällig durch Spurkreuzungen entdeckt zu werden. Erwartet wird daher eine kürzere Rekrutierungszeit, eine bessere Verteilung der Ameisen auf ergiebige Quellen und eine schnellere Aufgabe veralteter Quellen, sofern Broker-Meldungen mit Zeitstempeln altern oder durch spätere Rückmeldungen korrigiert werden.
