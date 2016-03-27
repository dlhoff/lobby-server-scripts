/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.request.convergegame;

import convergegame.match.*;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import net.request.GameRequest;
import net.response.convergegame.ResponseConvergeGetNames;
import util.Log;

/**
 *
 * @author justinacotter
 */
public class RequestConvergeGetNames extends GameRequest {
    
    private HashMap<Integer, String> playerNames = new HashMap<Integer, String>();
    
    @Override
    public void parse(DataInputStream dataInput) throws IOException {
    	Log.println("Parsing RequestConvergeGetNames");
    }

    @Override
    public void process() throws Exception {
        ResponseConvergeGetNames response = new ResponseConvergeGetNames();
        int player_id = client.getPlayerID();
        Log.println("RequestConvergeGetNames for player ID: " + player_id);
        MCMatchManager manager = MCMatchManager.getInstance();
        MCMatch match = manager.getMatchByPlayer(player_id);
        Map<Integer, MCMatchPlayer> playersList = match.getPlayers();
        Log.println("RequestConvergeGetNames, player id/name");
        for (Map.Entry<Integer, MCMatchPlayer> entry : playersList.entrySet()) {
            Integer key = entry.getKey();
            if (key != player_id) {
                MCMatchPlayer value = entry.getValue();
                playerNames.put(key, value.getPlayerName());
                Log.println(" " + key + " " + value.getPlayerName());
            }
        }   
        // Make sure the size is 4. That complies with the protocol
        while (playerNames.size() < 4) {
            playerNames.put(-playerNames.size(), "");
        }
        
        response.setPlayers(playerNames);
        client.add(response);
        Log.println("Processed RequestConvergeGetNames");
    }
}
