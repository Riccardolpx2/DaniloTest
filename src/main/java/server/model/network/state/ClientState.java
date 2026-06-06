package server.model.network.state;

import server.model.network.ClientHandler;
import shared.protocol.Message;
import shared.protocol.MessageType;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public abstract class ClientState {

    private static final Map<Class<? extends ClientState>, Map<MessageType, Method>> globalRoutingCache = new HashMap<>();


    // TODO: da mettere l'inizializzazione della mappa all'avvio del server
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
                localMap.put(tipoMessaggio, metodo);
            }
        }
        ClientState.globalRoutingCache.put(classe, localMap);
    }

    public final void handleMessage(Message message, ClientHandler clientHandler) {
        // 'this' a runtime sarà la sottoclasse concreta (es. DashboardState)
        Method metodoDestinatario = globalRoutingCache.get(this.getClass()).get(message.getMsgType());

        if(metodoDestinatario == null){
            throw new RuntimeException("Metodo non trovato!");
        }

        try {
            metodoDestinatario.invoke(this, message, clientHandler);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
