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

Die zentrale Forschungsfrage dieser Abgabe lautet: **Welche Pheromon-Verdunstungsrate maximiert die Sammeleffizienz des Schwarms?** Sammeleffizienz bedeutet hier, wie schnell und zuverlässig Nahrung von einer entfernten Quelle ins Nest transportiert wird. Als beobachtbare Größen eignen sich insbesondere die Nahrungsmenge im Nest nach einer festen Tickzahl, die Zeit bis zur ersten erfolgreichen Rückkehr, die Stabilität der entstehenden Pheromonroute und die Anzahl der noch aktiven Ameisen. Die folgenden Experimente sind deshalb nicht als unabhängige Themen gedacht, sondern als Varianten derselben Fragestellung: Die Verdunstungsrate wird systematisch verändert, während Aufbau, Nestposition, Nahrungsquelle und Kolonie möglichst konstant bleiben.

## Experiment 1: Baseline mit Standard-Verdunstungsrate `0.01`

**Motivation.** Das Baseline-Experiment liefert den Referenzwert für alle weiteren Vergleiche. Es zeigt, wie sich der Schwarm mit der im Code voreingestellten Verdunstungsrate verhält und ob daraus bereits eine brauchbare Balance zwischen Spuraufbau und Spurabbau entsteht.

**Aufbau.** Die Standardkonfiguration startet mit einem 250×250-Raster, 100 Ameisen am Nest bei `(25, 225)` und einer Nahrungsquelle mit 1000 Einheiten bei `(225, 25)`. Die Evaporationsrate beträgt `0.01`. Damit bleiben erfolgreiche Pheromonspuren über mehrere Ticks sichtbar, werden aber langfristig wieder schwächer, wenn sie nicht weiter benutzt werden.

**Verlauf der Simulation.** Zu Beginn bewegen sich die Ameisen überwiegend explorativ, weil noch keine Food-Pheromone existieren. Sobald einzelne Ameisen Nahrung finden, wechseln sie in den Rückkehrzustand und markieren ihren Weg mit Food-Pheromonen. Andere suchende Ameisen können diese Spur anschließend aufnehmen, wodurch sich der Verkehr zwischen Nest und Quelle zunehmend bündelt. Gleichzeitig verdunstet nicht weiter verstärkte Pheromoninformation langsam.

**Ergebnisse und Interpretation.** Für die Forschungsfrage ist diese Variante der erwartete Ausgangspunkt: `0.01` sollte eine mittlere Sammeleffizienz erzeugen, weil die Spur lange genug bestehen bleibt, um weitere Ameisen zu rekrutieren, aber nicht dauerhaft jede frühe Zufallsspur konserviert. Die Baseline ist deshalb besonders geeignet, um spätere Varianten daran zu messen, ob sie schneller Nahrung ins Nest bringen oder stabilere Pfade erzeugen.

## Experiment 2: Keine bzw. sehr geringe Verdunstung (`0.00`)

**Motivation.** Dieses Experiment prüft, ob maximale Pheromonstabilität automatisch zu maximaler Sammeleffizienz führt. Intuitiv könnte man erwarten, dass dauerhafte Spuren gut sind, weil keine Information verloren geht. Gleichzeitig besteht aber die Gefahr, dass schlechte frühe Wege zu lange erhalten bleiben.

**Aufbau.** Der Aufbau bleibt wie in der Baseline, nur die Verdunstungsrate wird auf `0.00` gesetzt. Dadurch verschwinden Pheromone praktisch nicht mehr von selbst. Jede einmal gelegte Spur bleibt als Information im System erhalten, unabhängig davon, ob sie später noch nützlich ist.

**Verlauf der Simulation.** In der Anfangsphase sammeln sich viele zufällige Home- und Food-Pheromonspuren an. Sobald eine Ameise eine Nahrungsquelle findet, kann ihr Rückweg dauerhaft attraktiv bleiben. Problematisch ist jedoch, dass auch Umwege, Sackgassen oder alte Suchrichtungen im System bleiben. Der Schwarm kann dadurch stärker an frühe Zufallsentscheidungen gebunden werden.

**Ergebnisse und Interpretation.** Bezogen auf die Forschungsfrage ist eine Verdunstungsrate von `0.00` wahrscheinlich nicht optimal. Zwar wird gefundene Information nicht gelöscht, aber der Schwarm verliert Anpassungsfähigkeit. Die Sammeleffizienz kann dadurch sinken, wenn viele Ameisen alten oder ungünstigen Spuren folgen. Das Experiment zeigt, dass maximale Informationsspeicherung nicht automatisch maximale Effizienz bedeutet; ein gewisser Informationsabbau ist notwendig, damit sich bessere Routen durchsetzen können.

## Experiment 3: Erhöhte Verdunstung (`0.05`)

**Motivation.** Dieses Experiment untersucht den Gegenpol zur stabilen Pheromonspur: Was passiert, wenn Pheromone deutlich schneller verschwinden? Ziel ist zu prüfen, ob schnellere Aktualisierung des Systems die Sammeleffizienz erhöht oder ob die Rekrutierung dadurch zu schwach wird.

**Aufbau.** Der Baseline-Aufbau bleibt erhalten, aber die Evaporationsrate wird auf `0.05` erhöht. Erfolgreiche Wege müssen dadurch häufiger durch neue Ameisen bestätigt werden, damit die Spur sichtbar bleibt. Alte oder selten genutzte Spuren verschwinden deutlich schneller als bei `0.01`.

**Verlauf der Simulation.** In der Suchphase bleibt der Schwarm länger explorativ, weil schwache Spuren rasch verdunsten. Nach einem Nahrungsfund kann eine Route entstehen, sie muss aber kontinuierlich von zurückkehrenden und suchenden Ameisen verstärkt werden. Wenn die Quelle weit entfernt ist oder nur wenige Ameisen gleichzeitig auf der Spur laufen, bricht die Rekrutierung leichter wieder ab.

**Ergebnisse und Interpretation.** Eine Verdunstungsrate von `0.05` kann in dynamischen Umgebungen hilfreich sein, weil veraltete Information schnell verschwindet. Für die hier betrachtete einzelne entfernte Nahrungsquelle ist sie aber voraussichtlich weniger effizient als die Baseline, wenn die Spur nicht stabil genug bleibt. Hinsichtlich der Forschungsfrage deutet dieses Experiment darauf hin, dass die optimale Rate nicht zu hoch liegen sollte: Zu schnelle Verdunstung reduziert den Nutzen der Pheromonkommunikation und macht die Nahrungssammlung stärker zufallsabhängig.

## Experiment 4: Sehr hohe Verdunstung (`0.10`) als Grenzfall

**Motivation.** Das vierte Experiment betrachtet eine sehr hohe Verdunstungsrate als Grenzfall. Damit lässt sich prüfen, ab wann die Pheromonkommunikation für den Schwarm praktisch zu kurzlebig wird, um die Sammeleffizienz noch zu verbessern.

**Aufbau.** Wieder bleibt die Startumgebung identisch, nur die Evaporationsrate wird auf `0.10` gesetzt. Pheromone verlieren dadurch sehr schnell ihre Stärke. Das Experiment dient vor allem als Vergleich zu `0.00`, `0.01` und `0.05`.

**Verlauf der Simulation.** Die Ameisen bewegen sich über lange Strecken fast so, als gäbe es nur schwache oder kurzfristige Pheromonhinweise. Einzelne erfolgreiche Ameisen können zwar kurzzeitig eine Spur erzeugen, diese Spur hat aber wenig Zeit, weitere Ameisen zu erreichen und zu rekrutieren. Dadurch entsteht weniger kollektive Verstärkung zwischen Nest und Nahrungsquelle.

**Ergebnisse und Interpretation.** Für die Forschungsfrage ist `0.10` voraussichtlich zu hoch, weil die Pheromoninformation schneller verschwindet, als der Schwarm sie zuverlässig ausnutzen kann. Die Sammeleffizienz sollte dadurch sinken: Es wird mehr Zeit mit Exploration verbracht, während weniger Ameisen stabil entlang einer erfolgreichen Route pendeln. Der Vergleich der vier Experimente legt qualitativ nahe, dass eine mittlere Verdunstungsrate, insbesondere nahe der Baseline `0.01`, die beste Balance zwischen Stabilität und Anpassungsfähigkeit bietet. Für eine endgültige Aussage müssten die Läufe jedoch quantitativ protokolliert und mehrfach mit verschiedenen Zufallsverläufen wiederholt werden.

## Zusammenfassung und Ausblick

Die Experimente sind auf die Forschungsfrage nach der optimalen Verdunstungsrate ausgerichtet. Qualitativ ergibt sich: Ohne Verdunstung bleiben zu viele alte oder zufällige Spuren erhalten; bei zu hoher Verdunstung verschwindet nützliche Information zu schnell; eine mittlere Rate wie `0.01` ist daher der plausibelste Kandidat für hohe Sammeleffizienz des Schwarms. Als Anschlussaktivität sollte ein automatisches Logging ergänzt werden, das pro Tick die Nahrungsmenge im Nest, Restnahrung an der Quelle, aktive Ameisen und Pheromonstärken speichert. Danach könnten mehrere Wiederholungen pro Verdunstungsrate durchgeführt und die Sammeleffizienz als „Nahrung im Nest pro Tick“ quantitativ verglichen werden.

## Brokering-Variante zur effizienteren Ausbeutung

Eine sinnvolle Erweiterung ist ein **Informationsbroker am Nest** in Form eines einfachen Blackboard- bzw. Matchmaking-Brokers. Ameisen, die Nahrung gefunden und erfolgreich zum Nest zurückgebracht haben, melden dort Quelle, geschätzte Entfernung bzw. Pfadlänge, Restmenge und Aktualität der Information. Suchende Ameisen fragen beim Verlassen des Nests den Broker ab und erhalten eine Empfehlung für die aktuell attraktivste bekannte Quelle, z. B. gewichtet nach hoher Restmenge, kurzer Entfernung und frischer Meldung. Die konkrete Bewegung bleibt weiterhin dezentral und pheromonbasiert; der Broker gibt nur eine priorisierende Startinformation bzw. Zielhypothese.

Diese Variante ist geeignet, weil im aktuellen System eine deutliche Informationsasymmetrie besteht: einzelne erfolgreiche Ameisen besitzen wertvolles Wissen über eine Nahrungsquelle, während andere Ameisen dieses Wissen nur indirekt und verzögert über lokale Pheromone aufnehmen können. Besonders bei langen Wegen, hoher Verdunstung oder mehreren Quellen erreicht die Information nicht alle relevanten Agenten gleich schnell. Ein Nest-Broker reduziert diese Asymmetrie genau an dem Ort, den alle erfolgreichen Transporte regelmäßig passieren. Im Vergleich zu einem vollständig zentralen Steuerungsbroker bleibt das System robust und skalierbar, weil der Broker keine Einzelbewegungen plant und keine globale Kontrolle über alle Agenten benötigt. Im Vergleich zu reinem Pheromon-Brokering ist die Informationsweitergabe direkter: neue oder ergiebige Quellen können gezielt an ausrückende Ameisen vermittelt werden, statt nur zufällig durch Spurkreuzungen entdeckt zu werden. Erwartet wird daher eine kürzere Rekrutierungszeit, eine bessere Verteilung der Ameisen auf ergiebige Quellen und eine schnellere Aufgabe veralteter Quellen, sofern Broker-Meldungen mit Zeitstempeln altern oder durch spätere Rückmeldungen korrigiert werden.
