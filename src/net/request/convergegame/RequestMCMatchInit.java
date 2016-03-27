package net.request.convergegame;

import java.io.DataInputStream;
import java.io.IOException;

import convergegame.match.MCMatchManager;
import util.DataReader;
import util.Log;
import net.request.GameRequest;
import net.response.convergegame.ResponseMCMatchInit;
import convergegame.match.*;
import java.util.Map;

// DH change - copied + modified from CW

public class RequestMCMatchInit extends GameRequest {

    private int playerID;
    private int matchID;
    private short host;
    private String playerName;

    @Override
    public void parse(DataInputStream dataInput) throws IOException {
        playerID = DataReader.readInt(dataInput);
        matchID = DataReader.readInt(dataInput);
        host = DataReader.readShort(dataInput);
        playerName = DataReader.readString(dataInput).trim();
    }

    @Override
    public void process() throws Exception {
        ResponseMCMatchInit response = new ResponseMCMatchInit();
        MCMatchManager manager = MCMatchManager.getInstance();
        short status;

        Log.printf("MC matchID = %d", matchID);

	// Assume player is in DB otherwise 
        //Match match = manager.createMatch(playerID1, playerID2);
        MCMatch match = manager.matchPlayerTo(this.matchID, playerID);
        if (match != null) {
            // TODO: add response success constant
            status = 0;
            matchID = match.getMatchID();
        } else {
            // status !=0 means failure
            status = 1;
            Log.printf("Failed to create Match");
        }
        Log.printf("Initializing match for player '%d' in match %d",
                playerID, matchID);
        Log.println("Player name: " + playerName);
        
        Map<Integer, Integer> matchIDList = manager.getMatchIDList();
        Log.println("Player ID /   Match ID");
        for (Integer key: matchIDList.keySet()) {
            System.out.println(key + "         /    " + matchIDList.get(key));
        }

        response.setStatus(status);
        response.setMatchID(matchID);
        match.playerList.get(playerID).setBetStatus(0);
        match.playerList.get(playerID).setPlayerName(playerName);
        client.add(response);
        client.setPlayerID(playerID);
        client.setMatchID(matchID);
        client.setHost(host);
        Log.println("This client's host value is: " + host);
        long timeValue = System.currentTimeMillis();
        Log.println("Current time is: " + timeValue);
        
        if (host == 1) {
            manager.getMatch(matchID).setStartTime(timeValue);
        }
    }
}
