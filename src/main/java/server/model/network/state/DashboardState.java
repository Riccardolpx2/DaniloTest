package server.model.network.state;

import server.logica.MatchmakingManager;
import server.model.network.ClientHandler;
import server.model.network.SessionManager;
import server.model.service.DashboardService;
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
        System.out.println("santa loia abate");
        String username = clientHandler.getLoggedUser().getUsername();
        try {
            StatDTO statDTO = dashboardService.getStatistiche(username);
            System.out.println("statistiche trovate" + statDTO.toString());
            // lato client gestire se non ci sono statistiche
            clientHandler.getOut().writeObject(new Message(MessageType.statsInfo, statDTO));


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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @MessageHandler(MessageType.gameSearch)
    private void searchGame(Message message, ClientHandler clientHandler){
        MatchmakingManager.enterLobby(clientHandler);
    }

}
