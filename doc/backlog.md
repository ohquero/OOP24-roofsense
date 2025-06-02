# Backlog di progetto

## EPIC: Analisi tecnica e setup del progetto

- US: **Come** team di sviluppo **vogliamo** definire l'architettura a package del monolita Java **per** organizzare il codice in modo manutenibile e scalabile
  - [ ] È stata definita l'architettura implementativa dell'applicativo
  - [ ] È stata definita la struttura dei package (es. presentation, business, data, integration)
  - [ ] Sono state definite le regole di dipendenza tra i package
  - [ ] È stata documentata l'architettura con diagrammi dei package

- US: **Come** team di sviluppo **vogliamo** scegliere la strategia di persistenza **per** mantenere i dati tra i riavvii dell'applicazione
  - [ ] È stato scelto il tipo di database (relazionale, NoSQL, embedded)
  - [ ] È stato scelto il prodotto specifico (PostgreSQL, H2, SQLite, etc.)
  - [ ] È stata definita la strategia di connessione e configurazione
  - [ ] Sono stati definiti i requisiti di backup e ripristino
  - [ ] È stato validato il setup con un database di test

- US: **Come** team di sviluppo **vogliamo** scegliere il framework Java **per** accelerare lo sviluppo e standardizzare l'implementazione
  - [ ] È stato scelto il framework principale (Spring Boot, Quarkus, plain Java)
  - [ ] Sono state scelte le librerie per persistenza (JPA/Hibernate, JDBC, etc.)
  - [ ] È stata definita la strategia di dependency injection se applicabile
  - [ ] È stata configurata la struttura base del progetto
  - [ ] È stato creato un "Hello World" funzionante

- US: **Come** team di sviluppo **vogliamo** scegliere la tecnologia per l'interfaccia utente **per** soddisfare i requisiti di usabilità e deployment
  - [ ] È stata scelta la tipologia di interfaccia (web, desktop, mista)
  - [ ] È stata scelta la tecnologia specifica (Thymeleaf, JSF, JavaFX, React+API, etc.)
  - [ ] È stata scelta la soluzione per la visualizzazione delle mappe
  - [ ] È stata definita la strategia di packaging dell'interfaccia nel jar
  - [ ] È stato creato un prototipo base dell'interfaccia

- US: **Come** team di sviluppo **vogliamo** configurare l'ambiente di sviluppo **per** iniziare l'implementazione
  - [ ] È stato configurato il repository di codice con struttura del progetto
  - [ ] Sono stati definiti gli standard di codifica e documentazione
  - [ ] È stata configurata la build (Maven/Gradle) per generare il jar
  - [ ] È stato configurato l'IDE con i plugin necessari
  - [ ] Sono stati definiti gli standard per i test

## EPIC: Gestione anagrafica punti di misura

- US: **Come** ricercatore **voglio** creare un nuovo tetto **per** poter definire i tetti da monitorare
  - [ ] Posso inserire un codice univoco per il tetto
  - [ ] Posso specificare l'area geografica occupata dal tetto come una lista di coordinate geografiche (latitudine, longitudine) che definiscono un poligono
  - [ ] Posso specificare l'indirizzo dell'edificio al quale il tetto appartiene
  - [ ] Posso specificare il colore con il quale il tetto e i suoi punti di misura verranno visualizzati sulla mappa
  - [ ] Il sistema valida l'unicità del codice
  - [ ] Il sistema valida che l'area geografica sia un poligono valido e che non si sovrapponga con altre aree già definite
  - [ ] Il tetto viene salvato nel database

- US: **Come** ricercatore **voglio** creare un nuovo punto di misura **per** poter definire i punti in cui un tetto è monitorato
  - [ ] Posso inserire un codice univoco per il punto di misura
  - [ ] Posso specificarne le coordinate geografiche (latitudine, longitudine)
  - [ ] Posso specificarne l'orientamento tra punti cardinali e intercardinali
  - [ ] Il sistema valida l'unicità del codice
  - [ ] Il sistema valida che le coordinate siano all'interno dell'area geografica di un tetto già definito e che non si sovrappongano con altri punti di misura già definiti
  - [ ] Il punto di misura viene salvato nel database

- US: **Come** ricercatore **voglio** visualizzare tutti i tetti in formato tabellare **per** averne una panoramica completa
  - [ ] È possibile consultare la tabella
  - [ ] La tabella mostra per ogni tetto: codice, dimensione dell'area geografica, punti di misura associati
  - [ ] Posso ordinare per ogni colonna
  - [ ] Posso filtrare per ogni colonna

- US: **Come** ricercatore **voglio** visualizzare tutti i punti di misura in formato tabellare **per** averne una panoramica completa
  - [ ] È possibile consultare la tabella
  - [ ] La tabella mostra per ogni punto di misura: codice, coordinate, orientamento, tetto associato
  - [ ] Posso ordinare per ogni colonna
  - [ ] Posso filtrare per ogni colonna

- US: **Come** ricercatore **voglio** visualizzare i tetti e i relativi punti di misura su una mappa geografica **per** averne una vista spaziale
  - [ ] Vedo una mappa interattiva con aree colorate per ogni tetto e marker per ogni punto di misura
  - [ ] I marker mostrano visivamente l'orientamento del punto
  - [ ] Per ogni tetto vedo il suo codice
  - [ ] Per ogni punto di misura vedo il suo codice
  - [ ] Posso zoomare e muovere la mappa

- US: **Come** ricercatore **voglio** eliminare un punto di misura **per** rimuovere punti creati per errore
  - [ ] Posso selezionare un punto di misura per l'eliminazione
  - [ ] Il sistema nega l'eliminazione se il punto ha delle misure associate

## EPIC: Gestione anagrafica sensori

- US: **Come** ricercatore **voglio** creare un nuovo sensore **per** poter tracciare tutti i dispositivi utilizzati nel progetto
  - [ ] Posso inserire un codice univoco per il sensore
  - [ ] Posso specificare il tipo di sensore (Lastem, LoRa, etc.)
  - [ ] Posso inserire il modello specifico del sensore
  - [ ] Il sistema valida l'unicità del codice sensore

- US: **Come** ricercatore **voglio** visualizzare l'elenco dei sensori **per** avere una panoramica dei dispositivi utilizzati nel progetto
  - [ ] Posso visualizzare una tabella con i sensori registrati
  - [ ] La tabella mostra per ogni sensore: codice, tipo, modello, punto di misura associato (se presente), tempo trascorso dall'ultimo aggiornamento dati
  - [ ] Posso ordinare per ogni colonna
  - [ ] Posso filtrare per ogni colonna

- US: **Come** ricercatore **voglio** associare un sensore a un punto di misura **per** tracciare la configurazione di monitoraggio
  - [ ] Posso scegliere uno o più sensori da associare ad un punto di misura
  - [ ] Posso selezionare il punto di misura al quale associarli
  - [ ] Posso specificare data e ora dal quale l'associazione sarà da considerarsi valida
  - [ ] Posso indicare come gli indici misurati dai sensori saranno mappati agli indici del punto di misura
  - [ ] Il sistema registra la configurazione nel database

- US: **Come** ricercatore **voglio** dissociare un sensore a un punto di misura **per** tracciare la configurazione di monitoraggio
  - [ ] Posso scegliere uno o più sensori da dissociare
  - [ ] Posso specificare data e ora dal quale la dissociazione sarà da considerarsi valida
  - [ ] Il sistema registra la configurazione nel database

- US: **Come** ricercatore **voglio** visualizzare i sensori associati ad un punto di misura **per** avere informazioni complete sul punto di misura

- US: **Come** ricercatore **voglio** visualizzare lo storico dei sensori di un punto di misura **per** vedere l'evoluzione nel tempo dei sensori installati
