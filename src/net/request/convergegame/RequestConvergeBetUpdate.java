package net.request.convergegame;

import java.io.DataInputStream;
import java.io.IOException;

import convergegame.match.MCMatchManager;
import core.GameClient;
import util.DataReader;
import util.Log;
import net.request.GameRequest;
import net.response.convergegame.ResponseConvergeBetUpdate;
import convergegame.match.*;
import java.util.Map;

// DH change - copied + modified from CW

public class RequestConvergeBetUpdate extends GameRequest {

    private short betStatus;
    private int improveAmount;
    private int match_id;
    private int player_id;
    MCMatch match;
    int bet;
    int totalBet;
    int tieCount;
    int bestImprove;
    private ResponseConvergeBetUpdate response;

    @Override
    public void parse(DataInputStream dataInput) throws IOException {
        // betStatus: 1 -> bet submitted; 0 -> no bet submitted
        betStatus = DataReader.readShort(dataInput);
        improveAmount = DataReader.readInt(dataInput);
    }

    @Override
    public void process() throws Exception {
        ResponseConvergeBetUpdate response = new ResponseConvergeBetUpdate();
        MCMatchManager manager = MCMatchManager.getInstance();
        player_id = client.getPlayerID();
        match_id = client.getMatchID();
        bet = client.bet;
        
        // Put this player's information in playerList
        match = manager.getMatch(match_id);
        MCMatchPlayer player = match.playerList.get(player_id);
        // MCMatchPlayer BetStatus
        // 0 -> no response yet
        // 1 -> response, not betting
        // 2 -> response, betting
        player.setBetStatus(betStatus+1);
        player.setImproveAmount(improveAmount);
        player.setClient(client);
        player.setResponse(response);
        
        // Check if all players have responsed. 
        // Iterator it = match.playerList.entrySet().iterator();
        boolean found = false;   // found someone not answered yet
        totalBet = 0; 
        tieCount = 1;
        bestImprove = -1000000;
        for (Map.Entry<Integer, MCMatchPlayer> entry : match.playerList.entrySet()) {
            // Integer key = entry.getKey();
            MCMatchPlayer player1 = entry.getValue();
            int improve1 = player1.getImproveAmount();
            Log.println("RCBU: improve/betStatus: " + improve1 + " " + player1.getBetStatus());
            if (player1.getBetStatus() == 2) {  // only consider if he is betting
                totalBet += bet;
                if (improve1 > bestImprove) {
                    bestImprove = improve1;
                    tieCount = 1;
                } else if (improve1 == bestImprove) {
                    tieCount++;
                }
            }
            if (player1.getBetStatus() == 0) {
                found = true; 
            } 
        }
        
        Log.println("Total bet so far: " + totalBet);
        if (!found) {  // everyone has bet. Now we can send off responses
            Log.println("Best Improved / Tie count = " + bestImprove + " " + tieCount);
            int dividedBet = totalBet / tieCount;
            short won = 1;
            short lost = 0;            
            for (Map.Entry<Integer, MCMatchPlayer> entry : match.playerList.entrySet()) {
                MCMatchPlayer player1 = entry.getValue();
                ResponseConvergeBetUpdate response1 = player1.getResponse();
                Log.println("This player improved: " + player1.getImproveAmount());
                if (player1.getBetStatus() == 2) { // if this player betted
                    if (player1.getImproveAmount() == bestImprove) {
                        Log.println("He won");
                        response1.setWon(won);
                        response1.setWonAmount(dividedBet);
                    } else {
                        Log.println("He lost");
                        response1.setWon(lost);
                        response1.setWonAmount(0);
                    } 
                } else { // this player did not bet
                    Log.println("He did not bet");
                    response1.setWon(lost);  // these entries should not matter
                    response1.setWonAmount(0); 
                }
                player1.setBetStatus(0);
                GameClient client1 = player1.getClient();
                client1.add(response1);
            } 
            
            long timeValue = System.currentTimeMillis();
            Log.println("RCBU Current time is: " + timeValue);
            manager.getMatch(match_id).setStartTime(timeValue);   
        }
    }
}
