# Backlog di progetto

## EPIC: Analisi tecnica e setup del progetto

- US#1: **Come** team di sviluppo **vogliamo** definire l'architettura dell'applicativo **per** organizzare il codice in
  modo manutenibile e scalabile
  - [x] È stata definita l'architettura implementativa dell'applicativo
  - [x] È stata definita la struttura dei package (es. presentation, business, data, integration)
  - [x] Sono state definite le regole di dipendenza tra i package

- US#2: **Come** team di sviluppo **vogliamo** scegliere la strategia di persistenza **per** mantenere i dati tra i
  riavvii dell'applicazione
  - [x] È stato scelto il tipo di database (OO, relazionale, NoSQL, misto, ...)
  - [x] È stato scelto il prodotto specifico
  - [x] È stata definita la strategia di connessione e configurazione

- US#3: **Come** team di sviluppo **vogliamo** scegliere la tecnologia per l'interfaccia utente **per** soddisfare i
  requisiti di usabilità e deployment
  - [x] È stata scelta la tipologia di interfaccia (web, desktop, mista)
  - [x] È stata scelta la tecnologia specifica
  - [x] È stata definita la strategia di packaging dell'interfaccia nel jar

- US#4: **Come** team di sviluppo **vogliamo** configurare l'ambiente di sviluppo **per** iniziare l'implementazione
  - [x] È stato configurato il repository di codice con struttura del progetto
  - [x] È stato creato un progetto Gradle
  - [x] È stato aggiunto il plugin per il download automatico della JRE sul quale eseguire l'applicativo
  - [x] Sono stati aggiunti i plugin di quality assurance
  - [x] Sono state aggiunte le dipendenze necessarie allo sviluppo di interfacce grafiche JavaFX

## EPIC: Gestione anagrafica punti di misura

- US#5: **Come** ricercatore **voglio** aggiungere un nuovo tetto **per** poter definire i tetti da monitorare
  - [x] Sono obbligato a specificare il codice identificativo
  - [x] Sono obbligato a specificare l'indirizzo dell'edificio al quale appartiene
  - [x] L'unicità è garantita dal solo codice identificativo
  - [ ] Posso eseguire l'operazione tramite interfaccia grafica
  - [ ] L'interfaccia grafica fornisce feedback in caso di compilazione errata dei campi

- US: **Come** ricercatore **voglio** creare un nuovo punto di misura **per** poter definire i punti in cui un tetto è
  monitorato
  - [ ] Sono obbligato a specificare il codice identificativo
  - [ ] Sono obbligato a specificare l'edificio al quale appartiene
  - [ ] Sono obbligato a specificare l'orientamento utilizzando punti cardinali e intercardinali
  - [ ] La creazione include la persistenza dello stesso
  - [ ] L'unicità è garantita dal solo codice identificativo
  - [ ] Posso eseguire l'operazione tramite interfaccia grafica
  - [ ] L'interfaccia grafica fornisce feedback in caso di compilazione errata dei campi

- US: **Come** ricercatore **voglio** visualizzare tutti i tetti in formato tabellare **per** averne una panoramica
  completa
  - [ ] Posso consultare la tabella tramite interfaccia grafica
  - [ ] La tabella mostra per ogni tetto: codice, dimensione dell'area geografica, punti di misura associati
  - [ ] Posso ordinare per ogni colonna
  - [ ] Posso filtrare per ogni colonna

- US: **Come** ricercatore **voglio** visualizzare tutti i punti di misura in formato tabellare **per** averne una
  panoramica completa
  - [ ] Posso consultare la tabella tramite interfaccia grafica
  - [ ] La tabella mostra per ogni punto di misura: codice, coordinate, orientamento, tetto associato
  - [ ] Posso ordinare per ogni colonna
  - [ ] Posso filtrare per ogni colonna

- US: **Come** ricercatore **voglio** eliminare un tetto **per** rimuovere quelli creati per errore
  - [ ] Posso selezionare il tetto da eliminare tramite l'interfaccia grafica
  - [ ] Il sistema nega l'eliminazione se il tetto ha dei punti di misura

- US: **Come** ricercatore **voglio** eliminare un punto di misura **per** rimuovere quelli creati per errore
  - [ ] Posso selezionare il punto di misura da eliminare tramite l'interfaccia grafica
  - [ ] Il sistema nega l'eliminazione se il punto ha delle misure associate

## EPIC: Gestione anagrafica sensori

- US: **Come** ricercatore **voglio** creare un nuovo sensore **per** poter tracciare tutti i dispositivi utilizzati nel
  progetto
  - [ ] Posso inserire un codice univoco per il sensore
  - [ ] Posso specificare il tipo di sensore (Lastem, LoRa, etc.)
  - [ ] Posso inserire il modello specifico del sensore
  - [ ] Il sistema valida l'unicità del codice sensore

- US: **Come** ricercatore **voglio** visualizzare l'elenco dei sensori **per** avere una panoramica dei dispositivi
  utilizzati nel progetto
  - [ ] Posso visualizzare una tabella con i sensori registrati
  - [ ] La tabella mostra per ogni sensore: codice, tipo, modello, punto di misura associato (se presente), tempo
    trascorso dall'ultimo aggiornamento dati
  - [ ] Posso ordinare per ogni colonna
  - [ ] Posso filtrare per ogni colonna

- US: **Come** ricercatore **voglio** associare un sensore a un punto di misura **per** tracciare la configurazione di
  monitoraggio
  - [ ] Posso scegliere uno o più sensori da associare ad un punto di misura
  - [ ] Posso selezionare il punto di misura al quale associarli
  - [ ] Posso specificare data e ora dal quale l'associazione sarà da considerarsi valida
  - [ ] Posso indicare come gli indici misurati dai sensori saranno mappati agli indici del punto di misura
  - [ ] Il sistema registra la configurazione nel database

- US: **Come** ricercatore **voglio** dissociare un sensore a un punto di misura **per** tracciare la configurazione di
  monitoraggio
  - [ ] Posso scegliere uno o più sensori da dissociare
  - [ ] Posso specificare data e ora dal quale la dissociazione sarà da considerarsi valida
  - [ ] Il sistema registra la configurazione nel database

- US: **Come** ricercatore **voglio** visualizzare i sensori associati ad un punto di misura **per** avere informazioni
  complete sul punto di misura

- US: **Come** ricercatore **voglio** visualizzare lo storico dei sensori adottati in un punto di misura **per** vedere
  l'evoluzione nel tempo dei sensori installati

## EPIC: Acquisizione dati sensori

## EPIC: API REST per applicativi terzi

## EPIC: Packaging e redistribuzione

- US: **Come** team di sviluppo **voglio** pacchettizzare l'applicativo in un unico JAR **per** poterlo redistribuire
  secondo requisiti di progetto
