package server.model.network.state;

import server.model.network.ClientHandler;
import shared.protocol.Message;
import shared.protocol.MessageType;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Classe astratta che definisce il concetto di Stato per i Client (State Pattern).
 * Sfrutta la reflection per mappare in maniera dichiarativa (tramite annotazione {@link MessageHandler})
 * i metodi che devono essere invocati per specifici tipi di messaggio ({@link MessageType}).
 */
public abstract class ClientState {

    private static final Map<Class<? extends ClientState>, Map<MessageType, Method>> globalRoutingCache = new HashMap<>();


    // TODO: da mettere l'inizializzazione della mappa all'avvio del server
    /**
     * Esegue la scansione iniziale (boot) delle classi di stato registrando in cache
     * la corrispondenza tra i {@link MessageType} e i metodi annotati.
     */
    public static void inizializzaRoutingServer() {
        System.out.println("[SERVER BOOT] Inizializzazione della tabella di routing...");

        Class<?>[] classiStato = {
                AuthState.class,
                DashboardState.class,
                GameState.class
        };

        for (Class<?> classe : classiStato) {
            // Un piccolo cast di sicurezza per Java
            Class<? extends ClientState> statoClass = classe.asSubclass(ClientState.class);
            helperScansionaAnnotazioni(statoClass);
            System.out.println("Mappato stato " + statoClass.getSimpleName() + ": DONE");
        }
        System.out.println("[SERVER BOOT] Routing configurato con successo.\n");
    }

    /**
     * Metodo helper che usa la reflection per estrarre tutti i metodi annotati
     * con {@link MessageHandler} da una classe e li registra in mappa.
     *
     * @param classe La sottoclasse di {@link ClientState} da analizzare.
     */
    private static void helperScansionaAnnotazioni(Class<? extends ClientState> classe) {
        Method[] metodi = classe.getDeclaredMethods();
        Map<MessageType, Method> localMap = new HashMap<>();

        for (Method metodo : metodi) {
            if (metodo.isAnnotationPresent(MessageHandler.class)) {
                MessageHandler annotazione = metodo.getAnnotation(MessageHandler.class);
                MessageType tipoMessaggio = annotazione.value();

                if (localMap.containsKey(tipoMessaggio)) {
                    throw new IllegalStateException("Errore: Il tipo messaggio " + tipoMessaggio +
                            " è già mappato su un metodo nella classe " + classe.getSimpleName());
                }
                
                metodo.setAccessible(true);
                localMap.put(tipoMessaggio, metodo);
            }
        }
        ClientState.globalRoutingCache.put(classe, localMap);
    }

    /**
     * Gestisce (smista) il messaggio in ingresso invocando il metodo corrispondente tramite reflection.
     *
     * @param message Il messaggio ricevuto dal client.
     * @param clientHandler L'handler responsabile del client mittente.
     */
    public final void handleMessage(Message message, ClientHandler clientHandler) {
        // 'this' a runtime sarà la sottoclasse concreta (es. DashboardState)
        Method metodoDestinatario = globalRoutingCache.get(this.getClass()).get(message.getMsgType());

        if(metodoDestinatario == null){
            System.err.println("[ROUTING] Nessun handler trovato per il messaggio: " + 
                    message.getMsgType() + " nello stato " + this.getClass().getSimpleName());
            return;
        }

        try {
            metodoDestinatario.invoke(this, message, clientHandler);
        } catch (java.lang.reflect.InvocationTargetException e) {
            // Estrae la VERA eccezione lanciata dal metodo invocato
            System.err.println("[ROUTING ERROR] Eccezione nell'handler di " + message.getMsgType() + ":");
            e.getCause().printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Hook invocato durante una disconnessione improvvisa (socket caduto)
     * per permettere allo stato corrente di liberare eventuali risorse in sospeso (es. code, partite).
     *
     * @param clientHandler L'handler del client che si è disconnesso.
     */
    public void onDisconnect(ClientHandler clientHandler) {
        // Implementazione di default vuota.
        // Le classi figlie (es. DashboardState, GameState) faranno l'override di questo
        // metodo per rimuovere il client dalle code di ricerca o terminare partite.
    }

}
