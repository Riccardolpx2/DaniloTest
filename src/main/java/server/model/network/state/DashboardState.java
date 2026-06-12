package server.model.network.state;

import server.model.network.MatchmakingManager;
import server.model.network.ClientHandler;
import server.model.network.SessionManager;
import server.model.service.DashboardService;
import shared.protocol.DTO.GameSearchDTO;
import shared.protocol.DTO.StatDTO;
import shared.protocol.Message;
import shared.protocol.MessageType;

import java.io.IOException;
import java.sql.SQLException;

public class DashboardState extends ClientState{

    private DashboardService dashboardService;

    public DashboardState() {
        this.dashboardService = new DashboardService();
    }

    @MessageHandler(MessageType.stats)
    private void stats(Message message, ClientHandler clientHandler) {

        String username = clientHandler.getLoggedUser().getUsername();
        try {
            StatDTO statDTO = dashboardService.getStatistiche(username);
            if(statDTO!=null){
                System.out.println("statistiche trovate" + statDTO.toString());
                // lato client gestire se non ci sono statistiche
                clientHandler.getOut().writeObject(new Message(MessageType.statsInfo, statDTO));
            }else{
                System.out.println("statistiche non trovate");
                clientHandler.getOut().writeObject(new Message(MessageType.statsInfo, null));
            }



        } catch (SQLException e) {
            try {
                clientHandler.getOut().writeObject(new Message(MessageType.generalError, "Errore lato server, riprova riprova più tardi"));
            } catch (IOException ex) {
                System.out.println("Client disconnesso");
            }
        } catch (IOException e) {
            System.out.println("Client disconnesso");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @MessageHandler(MessageType.logout)
    private void logout(Message message, ClientHandler clientHandler) {
        try {
            if (clientHandler.getLoggedUser() != null) {
                SessionManager.getInstance().logout(clientHandler.getLoggedUser().getUsername());
            }
            clientHandler.setLoggedUser(null);
            clientHandler.setCurrentState(new AuthState());
            MatchmakingManager.exitLobby(clientHandler); // per assicurarci di uscire dalla lobby (il metodo gestisce il fatto che non fosse in lobby)
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @MessageHandler(MessageType.gameSearch)
    private void searchGame(Message message, ClientHandler clientHandler){
        MatchmakingManager.enterLobby(clientHandler, ((GameSearchDTO) message.getPayload()).getDifficoltaPartita());
    }

    @MessageHandler(MessageType.gameSearchCancel)
    private void cancelSearchGame(Message message, ClientHandler clientHandler){
        MatchmakingManager.exitLobby(clientHandler);
    }

}
