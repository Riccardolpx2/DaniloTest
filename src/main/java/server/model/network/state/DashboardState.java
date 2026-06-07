package server.model.network.state;

import server.model.network.ClientHandler;
import server.model.service.DashService;
import shared.game.Statistica;
import shared.protocol.DTO.StatDTO;
import shared.protocol.Message;
import shared.protocol.MessageType;

import java.io.IOException;
import java.sql.SQLException;

public class DashboardState extends ClientState{

    private DashService ds;

    public DashboardState() {
        this.ds = new DashService();
    }

    @MessageHandler(MessageType.stats)
    private void stats(Message message, ClientHandler clientHandler) {
        String username = message.getPlayerID();
        try {
            StatDTO statDTO = ds.getStatistiche(username);
            if (statDTO != null) { //restituisce la statistica se non e null
                clientHandler.getOut().writeObject(new Message(MessageType.statsInfo, statDTO));
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
            clientHandler.setCurrentState(new AuthState());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @MessageHandler(MessageType.gameSearch)
    private void cercaGame(Message message, ClientHandler clientHandler){

    }


    @MessageHandler(MessageType.gameStart)
    private void startGame(Message message, ClientHandler clientHandler){

    }

    @MessageHandler(MessageType.gameSearchError)
    private void erroreGame(Message message, ClientHandler clientHandler){

    }





}
