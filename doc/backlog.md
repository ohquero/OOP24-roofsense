# Backlog di progetto

## EPIC: Analisi tecnica e setup del progetto

- US#1: **Come** team di sviluppo **vogliamo** definire l'architettura dell'applicativo **per** organizzare il codice in modo manutenibile e scalabile
  - [x] È stata definita l'architettura implementativa dell'applicativo
  - [x] È stata definita la struttura dei package (es. presentation, business, data, integration)
  - [x] Sono state definite le regole di dipendenza tra i package

- US#2: **Come** team di sviluppo **vogliamo** scegliere la strategia di persistenza **per** mantenere i dati tra i riavvii dell'applicazione
  - [x] È stato scelto il tipo di database (OO, relazionale, NoSQL, misto, ...)
  - [x] È stato scelto il prodotto specifico
  - [x] È stata definita la strategia di connessione e configurazione

- US#3: **Come** team di sviluppo **vogliamo** scegliere la tecnologia per l'interfaccia utente **per** soddisfare i requisiti di usabilità e deployment
  - [x] È stata scelta la tipologia di interfaccia (web, desktop, mista)
  - [x] È stata scelta la tecnologia specifica
  - [x] È stata definita la strategia di packaging dell'interfaccia nel jar

- US#4: **Come** team di sviluppo **vogliamo** configurare l'ambiente di sviluppo **per** iniziare l'implementazione
  - [x] È stato configurato il repository di codice con struttura del progetto
  - [x] È stato creato un progetto Gradle
  - [x] È stato aggiunto il plugin per il download automatico della JRE sul quale eseguire l'applicativo
  - [x] Sono stati aggiunti i plugin di quality assurance
  - [x] Sono state aggiunte le dipendenze necessarie allo sviluppo di interfacce grafiche JavaFX

## EPIC: Gestione anagrafica tetti

### FASE 1: Progettazione UI Mock

- US: **Come** UX designer **voglio** creare un mock della UI di gestione dell'anagrafica tetti **per** permettere ai ricercatori di visualizzare tutte le funzioni previste
  - [ ] È stata prevista una vista di tutti i tetti con layout tabellare
  - [ ] È possibile inserire un nuovo tetto
  - [ ] È possibile modificare i dettagli di un tetto
  - [ ] È possibile eliminare un tetto
  - [ ] È possibile cercare tra i tetti registrati
  - [ ] È possibile ordinare i tetti mostrati sulla vista

- US: **Come** ricercatore **voglio** visualizzare tutti i tetti **per** averne una panoramica completa
  - [ ] Il sistema recupera tutti i tetti dal database
  - [ ] Il sistema supporta ordinamento per ogni attributo del tetto
  - [ ] Il sistema registra l'operazione nei log

- US: **Come** ricercatore **voglio** cercare tra i tetti tramite ricerca testuale **per** visualizzare soltanto alcuni tetti
  - [ ] Il sistema supporta ricerca testuale sui valori degli attributi
  - [ ] Il sistema supporta ordinamento per ogni attributo del tetto
  - [ ] Il sistema registra l'operazione nei log

- US#5: **Come** ricercatore **voglio** aggiungere un nuovo tetto **per** poter definire i tetti da monitorare
  - [x] Sono obbligato a specificare il codice identificativo
  - [x] Sono obbligato a specificare l'indirizzo dell'edificio al quale appartiene
  - [x] L'unicità è garantita dal solo codice identificativo
  - [ ] L'aggiunta di un tetto include la persistenza nel database
  - [ ] La form di aggiunta del nuovo tetto non permette di effettuare l'operazione se gli attributi del tetto non sono correttamente compilati o esistono tetti con lo stesso codice identificativo
  - [ ] Dopo l'aggiunta del nuovo tetto la schermata la vista tabellare viene aggiornata
  - [ ] Il sistema registra l'esito delle operazioni effettuate nei log

- US: **Come** ricercatore **voglio** visualizzare e modificare i dettagli di un tetto **per** aggiornarne le informazioni
  - [ ] Il sistema permette la modifica di tutti gli attributi eccetto il codice identificativo
  - [ ] Il sistema valida i dati modificati prima del salvataggio
  - [ ] Il sistema persiste le modifiche nel database
  - [ ] Il sistema registra l'operazione nei log

- US: **Come** ricercatore **voglio** eliminare un tetto **per** rimuovere quelli non più utili
  - [ ] Il sistema elimina il tetto dal database
  - [ ] Il sistema registra l'operazione nei log

## EPIC: Gestione anagrafica punti di misura

- US: **Come** ricercatore **voglio** che il sistema mi impedisca di eliminare un tetto se ha punti di misura associati **per** non avere punti di misura orfani

## EPIC: Gestione anagrafica sensori

## EPIC: Acquisizione dati sensori

- US: **Come** ricercatore **voglio** vedere il tempo passato dall'ultimo aggiornamento ricevuto da un punto di misura **per** capire velocemente se ci sono problemi di comunicazione relativi al singolo punto di misura

- US: **Come** ricercatore **voglio** vedere il tempo passato dall'ultimo aggiornamento ricevuto da un sensore **per** capire velocemente se ci sono problemi di comunicazione relativi al singolo punto di misura

## EPIC: API REST per applicativi terzi

## EPIC: Packaging e redistribuzione

- US: **Come** team di sviluppo **voglio** pacchettizzare l'applicativo in un unico JAR **per** poterlo redistribuire
  secondo requisiti di progetto
