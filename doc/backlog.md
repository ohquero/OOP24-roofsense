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

- US: **Come** UX designer **voglio** progettare la porzione di UI relativa all'anagrafica tetti **per** permettere ai ricercatori l'accesso a tali funzioni
  - [ ] È possibile inserire un nuovo tetto
  - [ ] È possibile visualizzazione e modificare i dettagli di un tetto
  - [ ] È possibile rimuovere un tetto
  - [ ] È possibile visualizzare tutti i tetti, con possibilità di ordinarli secondo i valori dei loro attributi
  - [ ] È possibile filtrare tetti per i loro attributi
  - [ ] La UI può mostrare hint relativi alla validazione degli attributi di tetti e punti di misura
  - [ ] La UI può mostrare messaggi di successo ed errore relativi all'esito delle operazioni richieste

- US#9: **Come** ricercatore **voglio** aggiungere un nuovo tetto **per** poter definire i tetti da monitorare
  - [x] Sono obbligato a specificare il codice identificativo
  - [x] Sono obbligato a specificare l'indirizzo dell'edificio al quale appartiene
  - [x] L'unicità è garantita dal solo codice identificativo
  - [ ] Posso inserire le coordinate geografiche del tetto
  - [ ] Posso specificare l'area approssimativa del tetto
  - [ ] Posso eseguire l'operazione tramite l'interfaccia grafica
  - [ ] L'interfaccia grafica fornisce feedback in caso di compilazione errata dei campi
  - [ ] Il sistema registra l'operazione nei log

- US: **Come** ricercatore **voglio** visualizzare tutti i tetti in formato tabellare **per** averne un panoramica completa
  - [ ] Posso consultare la tabella tramite interfaccia grafica
  - [ ] La tabella mostra per ogni tetto: codice, indirizzo
  - [ ] Posso ordinare per ogni colonna
  - [ ] Posso effettuare una ricerca testuale per valori di ogni colonna

- US: **Come** ricercatore **voglio** eliminare un tetto **per** rimuovere quelli non più utili
  - [ ] Posso selezionare il tetto da eliminare tramite l'interfaccia grafica
  - [ ] Il sistema mostra un dialog di conferma con dettagli dell'operazione
  - [ ] Il sistema nega l'eliminazione se il tetto ha dei punti di misura e mostra messaggio esplicativo
  - [ ] Il sistema registra l'operazione nei log

## EPIC: Gestione anagrafica punti di misura

- US: **Come** UX designer **voglio** progettare la porzione di UI relativa all'anagrafica punti di misura **per** permettere ai ricercatori l'accesso a tali funzioni
  - [ ] È possibile inserire un nuovo punto di misura
  - [ ] È possibile visualizzare e modificare dei dettagli di un punto di misura
  - [ ] È possibile rimuovere un punto di misura
  - [ ] È possibile visualizzare tutti i tetti, con possibilità di ordinarli secondo i valori dei loro attributi
  - [ ] È possibile filtrarli per i loro attributi
  - [ ] La UI può mostrare hint relativi alla validazione degli attributi
  - [ ] La UI può mostrare messaggi di successo ed errore relativi all'esito delle operazioni richieste

- US: **Come** ricercatore **voglio** creare un nuovo punto di misura **per** poter definire i punti in cui un tetto è monitorato
  - [ ] Sono obbligato a specificare il codice identificativo
  - [ ] Sono obbligato a specificare il tetto al quale appartiene
  - [ ] Sono obbligato a specificare l'orientamento utilizzando punti cardinali e intercardinali
  - [ ] Posso specificare le coordinate geografiche precise del punto
  - [ ] La creazione include la persistenza dello stesso
  - [ ] L'unicità è garantita dal solo codice identificativo
  - [ ] Posso eseguire l'operazione tramite interfaccia grafica con mappa interattiva
  - [ ] L'interfaccia grafica fornisce feedback in caso di compilazione errata dei campi

- US: **Come** ricercatore **voglio** visualizzare tutti i punti di misura in formato tabellare **per** averne una panoramica completa
  - [ ] Posso consultare la tabella tramite interfaccia grafica
  - [ ] La tabella mostra per ogni punto di misura: codice, coordinate, orientamento, tetto associato, stato sensori
  - [ ] Posso ordinare per ogni colonna
  - [ ] Posso effettuare una ricerca testuale per valori di ogni colonna

- US: **Come** ricercatore **voglio** eliminare un punto di misura **per** rimuovere quelli non più utili
  - [ ] Posso selezionare il punto di misura da eliminare tramite l'interfaccia grafica
  - [ ] Il sistema mostra un dialog di conferma con dettagli dell'operazione

- US: **Come** ricercatore **voglio** che l'applicativo impedisca l'eliminazione di tetti con punti di misura **per** rispettare i vincoli di dominio

## EPIC: Gestione anagrafica sensori

- US: **Come** UX designer **voglio** progettare la porzione di UI relativa all'anagrafica sensori **per** permettere ai ricercatori l'accesso a tali funzioni
  - [ ] È possibile inserire un nuovo sensore, indicando il punto di misura in cui è montato
  - [ ] È possibile visualizare e modificare i dettagli di un sensore
  - [ ] È possibile rimuovere un punto di misura
  - [ ] È possibile visualizzare tutti i sensori, con possiblità di ordinarli secondo i valori dei loro attributi
  - [ ] È possibile filtrare tetti e punti di misura per i loro attributi
  - [ ] La UI può mostrare hint relativi alla validazione degli attributi di tetti e punti di misura
  - [ ] La UI può mostrare messaggi di successo ed errore relativi all'esito delle operazioni richieste

- US: **Come** ricercatore **voglio** creare un nuovo sensore **per** poter tracciare tutti i dispositivi utilizzati nel progetto
  - [ ] Posso inserire un codice univoco per il sensore
  - [ ] Posso specificare il tipo di sensore (Lastem, LoRa, etc.)
  - [ ] Posso inserire il modello specifico del sensore (Omega PR-10, HFP01, LHT65, EM300-TH)
  - [ ] Posso inserire note e descrizione del sensore
  - [ ] Il sistema valida l'unicità del codice sensore
  - [ ] L'interfaccia adatta i campi disponibili in base al tipo di sensore selezionato

- US: **Come** ricercatore **voglio** visualizzare l'elenco dei sensori **per** avere una panoramica dei dispositivi utilizzati nel progetto
  - [ ] Posso visualizzare una tabella con i sensori registrati
  - [ ] La tabella mostra per ogni sensore: codice, tipo, modello, punto di misura associato (se presente), stato connessione, ultimo aggiornamento dati
  - [ ] Posso ordinare per ogni colonna
  - [ ] Posso filtrare per tipo di sensore, stato di connessione, e punto di misura
  - [ ] Posso visualizzare indicatori colorati per lo stato di funzionamento

- US: **Come** ricercatore **voglio** associare un sensore a un punto di misura **per** tracciare la configurazione di monitoraggio
  - [ ] Posso utilizzare un'interfaccia drag-and-drop per associare sensori ai punti
  - [ ] Posso scegliere uno o più sensori da associare ad un punto di misura
  - [ ] Posso selezionare il punto di misura dalla mappa o dalla lista
  - [ ] Posso specificare data e ora dal quale l'associazione sarà da considerarsi valida
  - [ ] Posso configurare la mappatura degli indici misurati (temperatura aria, superficie, flusso calore)
  - [ ] Il sistema registra la configurazione nel database e notifica il cambiamento

- US: **Come** ricercatore **voglio** che il programma impedisca l'eliminazione di un punto di misura se ha dei sensori associati **per** evitare l'erronea rimozione di un punto di misura

- US: **Come** ricercatore **voglio** dissociare un sensore da un punto di misura **per** tracciare la configurazione di monitoraggio
  - [ ] Posso scegliere uno o più sensori da dissociare
  - [ ] Posso specificare data e ora dal quale la dissociazione sarà da considerarsi valida
  - [ ] Il sistema conferma l'operazione e mostra l'impatto sulla raccolta dati
  - [ ] Il sistema registra la configurazione nel database

- US: **Come** ricercatore **voglio** visualizzare i sensori associati ad un punto di misura **per** avere informazioni complete sul punto di misura
  - [ ] Posso visualizzare una vista di dettaglio con tutti i sensori attivi
  - [ ] Posso visualizzare lo stato di funzionamento di ogni sensore
  - [ ] Posso accedere rapidamente alle configurazioni di mappatura
  - [ ] Posso visualizzare i grafici delle ultime misure per verifica funzionamento

- US: **Come** ricercatore **voglio** visualizzare lo storico dei sensori adottati in un punto di misura **per** vedere l'evoluzione nel tempo dei sensori installati
  - [ ] Posso visualizzare una timeline delle configurazioni nel tempo
  - [ ] Posso vedere i periodi di attività di ogni sensore
  - [ ] Posso identificare i gap temporali nella raccolta dati
  - [ ] Posso esportare lo storico configurazioni per report

## EPIC: Acquisizione dati sensori

- US: **Come** ricercatore **voglio** vedere il tempo passato dall'ultimo aggiornamento ricevuto da un punto di misura **per** capire velocemente se ci sono problemi di comunicazione relativi al singolo punto di misura

- US: **Come** ricercatore **voglio** vedere il tempo passato dall'ultimo aggiornamento ricevuto da un sensore **per** capire velocemente se ci sono problemi di comunicazione relativi al singolo punto di misura

## EPIC: API REST per applicativi terzi

## EPIC: Packaging e redistribuzione

- US: **Come** team di sviluppo **voglio** pacchettizzare l'applicativo in un unico JAR **per** poterlo redistribuire
  secondo requisiti di progetto
