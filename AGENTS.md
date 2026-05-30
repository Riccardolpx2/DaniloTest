Overview
Il progetto prevede la realizzazione di un gioco client–server sviluppato in Java con interfaccia grafica
JavaFX, in cui due utenti si sfidano contemporaneamente nel decifrare una parola nascosta all’interno di
un testo. Il sistema è composto da un server centrale e da due client, che comunicano tra loro tramite
socket.
Il server ha il ruolo di coordinare l’intera applicazione: gestisce le connessioni dei client, l’autenticazione
degli utenti, la preparazione delle sfide e la registrazione degli esiti. Tramite un’interfaccia grafica riservata
all’amministratore, il server consente il caricamento di documenti testuali che vengono analizzati per
estrarre informazioni statistiche sulle parole. I risultati di queste analisi possono essere salvati e riutilizzati,
così da evitare nuove elaborazioni a ogni avvio.
Durante una partita, il server seleziona un estratto di testo da uno dei documenti analizzati e sostituisce
una o più parole con la loro codifica secondo un cifrario di Cesare, applicato con uno spostamento scelto
in modo casuale. Il testo modificato viene inviato ai due client, che lo visualizzano a schermo e partecipano
alla sfida in tempo reale.
Ogni client fornisce un’interfaccia grafica che permette all’utente di effettuare il login o la registrazione,
attendere la connessione dell’altro sfidante e partecipare alla partita. Quando la sfida ha inizio, i giocatori
devono individuare la parola originale e inviarla al server entro il tempo previsto. Il primo utente che
risponde correttamente viene dichiarato vincitore.
Il sistema mantiene in modo persistente le credenziali degli utenti, incluse quelle dell’amministratore, e
lo storico delle sfide, con riferimento temporale, permettendo così la consultazione delle classifiche. Tutta
l’applicazione è configurata tramite file di proprietà e può essere avviata tramite file eseguibili JAR,
rendendo possibile il test completo del gioco in modo immediato.
Il sistema dunque prevede:
• un Server JavaFX con interfaccia di amministrazione;
• due Client JavaFX per gli sfidanti;
• comunicazione tramite Socket;
• persistenza dei dati tramite SQLite;
• utilizzo di Stream API, JavaFX Service/Task, FXML., JDBC
1
Architettura generale
Componenti principali
• Server
o Gestione connessioni client
o Autenticazione utenti
o Selezione e preparazione della sfida
o Gestione risultati e classifiche
• Client
o Autenticazione (login/registrazione)
o Visualizzazione del testo cifrato
o Inserimento risposta
o Visualizzazione esito della sfida
o Visualizzazione storico partite precedenti
• Database locale (SQLite)
o Utenti
o Sfide
o Risultati
• File di configurazione (properties)
o Server: porta di ascolto
o Client: IP e porta del server
Server
Configurazione
• La porta del server è letta da un file server.properties
.properties
server.port=5000
• Il server apre un ServerSocket sulla porta configurata.
Interfaccia Amministratore (JavaFX)
L’amministratore ha accesso a un’interfaccia grafica che consente di:
• selezionare uno o più documenti testuali;
• avviare l’analisi del contenuto;
• salvare e caricare i risultati dell’analisi.
L’amministratore è un utente speciale, con credenziali già presenti nel database.
2
Analisi dei documenti
• I documenti sono file di testo semplice (TXT)
• L’analisi consiste nel calcolo della Term Frequency delle parole.
• Prediligere l’uso di:
o Java Stream API per l’elaborazione del testo;
o JavaFX Service / Task per l’esecuzione asincrona (UI non bloccante).
Output dell’analisi (esempio):
• mappa parola → frequenza
• struttura dati
Persistenza:
• i risultati possono essere salvati in maniera serializzata
• all’avvio del server è possibile ricaricarli evitando di rifare l’analisi
Preparazione della sfida
• Il server:
1. seleziona un documento analizzato;
2. estrae un breve estratto testuale;
3. sceglie una o più parole da nascondere*;
4. applica un cifrario di Cesare con shift casuale;
5. sostituisce le parole originali con le versioni cifrate.
   *Livelli di difficoltà (opzionale)
   La difficoltà può dipendere da:
   • frequenza della parola (più rara = più difficile);
   • lunghezza della parola;
   • numero di parole cifrate;
   • valore dello shift di Cesare.
   Gestione della sfida
   Connessione dei client
   La sfida parte solo quando entrambi i client sono connessi e autenticati
   3
   Avvio della partita
   • Il server invia ai client:
   o testo con parola/e cifrata/e
   o durata del conto alla rovescia
   • I client visualizzano il testo e parte il timer.
   Conclusione
   • I client inviano la parola proposta al server.
   • Il server:
   o valida la risposta;
   o determina il vincitore (prima risposta corretta);
   o notifica entrambi i client del risultato;
   o registra l’esito nel database.
   Client
   Configurazione
   • IP e porta del server sono letti da client.properties:
   .properties
   server.ip=127.0.0.1
   server.port=5000
   Interfaccia di avvio (esempio)
   • Finestra iniziale con:
   o campo username
   o campo password
   o pulsanti Login / Registrazione
   • Validazione delle credenziali tramite server.
   Finestra di gioco (esempio)
   • Testo con parola/e cifrata/e evidenziate
   • Campo di input per la risposta
   • Pulsante di invio
   • Timer visibile
   • Stato della partita (in attesa / in gioco / finita)
   4
   Persistenza dei dati e classifiche
   Il sistema deve prevedere l’utilizzo di un database locale SQLite per la gestione persistente delle
   informazioni necessarie al funzionamento dell’applicazione.
   La struttura del database è lasciata alla progettazione del gruppo, che dovrà definire in autonomia il
   modello dei dati più adeguato.
   Gestione dei dati
   Il database deve consentire di:
   • memorizzare le credenziali degli utenti registrati, inclusi almeno un account amministratore e
   gli account dei giocatori;
   • distinguere tra ruoli differenti (amministratore e utente);
   • salvare le sfide disputate, includendo informazioni temporali;
   • registrare gli esiti delle partite, associando ogni risultato agli utenti coinvolti.
   Classifiche e statistiche
   Il sistema deve permettere l’estrazione di statistiche sulle partite giocate, in particolare:
   • numero di vittorie per utente;
   • numero di partite disputate;
   • tempo medio di risposta.
   Le classifiche possono essere visualizzate:
   • lato server (ad esempio tramite l’interfaccia amministratore),
   • oppure lato client (ogni client visualizza solo lo storico delle proprie sfide con gli esiti)
   Tecnologie e vincoli richiesti
   Obbligatori:
   • Java
   • JavaFX + FXML
   • Socket
   • Stream API
   • JavaFX Service / Task
   • SQLite
   • File .properties
   5
   • Serializzazione
   Facoltativi:
   Styling CSS
   Modalità di Consegna
   La consegna del progetto finale deve includere obbligatoriamente il seguente materiale:
1. Codice sorgente completo del progetto (server e client) con Javadoc.
2. Relazione sintetica che descriva:
   o architettura del sistema;
   o principali scelte progettuali;
   o Specifica requisiti (Use Cases, Mockup)
   o Progettazione (Class Diagram, Sequence Diagram, E-R)
   o tecnologie utilizzate
3. Presentazione sintetica (PowerPoint) del progetto.
   La consegna è fissata 7 giorni prima dell’appello.
   Archivio degli eseguibili
   Oltre ai materiali sopra elencati, è obbligatorio consegnare un archivio ZIP denominato:
   eseguibili.zip
   Contenuto dell’archivio eseguibili.zip
   L’archivio deve contenere:
   • server.jar
   • client.jar
   oltre a tutte le eventuali dipendenze necessarie all’esecuzione, organizzate in apposite cartelle, ad
   esempio:
   eseguibili/
   ├── server.jar
   ├── client.jar
   ├── readme.txt
   ├── properties/
   │ ├── server.properties
   │ └── client.properties
   ├── db/
   │ └── database.db
   6
   ├── documents/
   ├── data/
   La struttura può variare, ma tutti i file necessari all’esecuzione devono essere inclusi.
   Obiettivo dell’archivio eseguibili
   L’obiettivo è che il revisore, estraendo l’archivio ZIP, possa:
1. avviare il server tramite:
   java -jar server.jar
2. avviare i due client tramite:
   java -jar client.jar
   java -jar client.jar
3. eseguire e testare immediatamente il gioco senza ulteriori configurazioni manuali.
   Account predefiniti per il testing
   Per facilitare la fase di valutazione, il sistema deve prevedere account già presenti nel database,
   utilizzabili immediatamente:
   • 1 account amministratore (per l’accesso all’interfaccia di gestione server)
   • 2 account utente (per testare subito una sfida completa)
   Le credenziali di tali account devono essere chiaramente indicate nella relazione. E in un file
   readme.txt consegnato con l’archivio degli eseguibili.
   Vincoli tecnici
   • Tutti i file .jar devono essere compatibili con JDK 8
   • Non devono essere richieste dipendenze esterne non incluse nell’archivio
   • I percorsi di file e database devono essere relativi, non assoluti
   7
   La consegna di archivi incompleti o non eseguibili influisce sulla valutazione.