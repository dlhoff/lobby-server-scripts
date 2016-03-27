/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.request.convergegame;

import convergegame.match.*;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import net.request.GameRequest;
import net.response.convergegame.ResponseConvergeGetTime;
import util.Log;

/**
 *
 * @author justinacotter
 */
public class RequestConvergeGetTime extends GameRequest {
    
    private short betTime;
    private HashMap<Integer, Integer> betStatusList = new HashMap<Integer, Integer>();
    
    @Override
    public void parse(DataInputStream dataInput) throws IOException {
    	Log.consoleln("Parsing RequestConvergeGetTime");
    }

    @Override
    public void process() throws Exception {
        ResponseConvergeGetTime response = new ResponseConvergeGetTime();
        
        int player_id = client.getPlayerID();
        MCMatchManager manager = MCMatchManager.getInstance();
        MCMatch match = manager.getMatchByPlayer(player_id);
        betTime = (short) client.getBetTime();
        
        long startTime = match.getStartTime();
        long presentTime = System.currentTimeMillis();
        betTime = (short) ( betTime - (presentTime - startTime) / 1000);
        
        response.setTime(betTime);
        Log.consoleln("Processed RequestConvergeGetTime. betTime = " + betTime);
         
        Map<Integer, MCMatchPlayer> playersList = match.getPlayers();
        Log.println("RequestConvergeGetTime, bet status values");
        Log.println("player id: " + player_id);
        for (Map.Entry<Integer, MCMatchPlayer> entry : playersList.entrySet()) {
            Integer key = entry.getKey();
            if (key != player_id) {
                MCMatchPlayer value = entry.getValue();
                Integer betStatus = value.getBetStatus();
                Log.println("Original id/ Bet status: " + key + " " + betStatus);
                // MCMatchPlayer betStatus is:
                //      0->no response; 1->no bet; 2->bet
                // This response status must be:
                //      0->no bet yet; 1->bet
                if (betStatus == 2) {
                    betStatus = 1;
                }
                else {
                    betStatus = 0;
                }
                betStatusList.put(key, betStatus);
                Log.println("Bet status: " + betStatus);
            }
        }   
        // Make sure the size is 4. That complies with the protocol
        while (betStatusList.size() < 4) {
            betStatusList.put(-betStatusList.size(), 0);
        }
        response.setBetStatusList(betStatusList);
        Log.println("RequestConvergeGetTime finished");
        client.add(response);        
    }
}
