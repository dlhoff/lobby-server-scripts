package convergegame.match;

import java.util.ArrayList;

import util.Log;
import core.GameServer;
import model.Player;
import net.response.*;
import core.GameClient;
import net.response.convergegame.ResponseConvergeBetUpdate;

// import model.CardDeck;
// import model.Card;
/*
 * @author Nathanael Aff
 */

public class MCMatchPlayer{
	
	private Player player;
	// TODO: which status variables are needed?
	private boolean isActive = false;

	// Client initialization is finished
	private boolean isReady = false;
	private int matchID;
	private String playerName; 
	
	// Copy of players deck (added to allow for design change)
	// private CardDeck deck;
	private Boolean isBuilt;
	
	// ActionQueue records opponents turn based actions
	MCActionQueue actionQueue;
	// inactive is just set when player leaves or quits so no more 
	// actions are added to his queue
	private boolean hasDisconnected = false;
        
        // DH change
        // betStatus:
        // 0 = no response 
        // 1 = not betting
        // 2 = betting
        private int betStatus = 0;
        private int improveAmount = 0;   // amount of improvement	
        private ResponseConvergeBetUpdate response;
        private GameClient client;
	
	// TODO: register client? or access elsewhere
	public MCMatchPlayer(int playerID, int matchID){
		this.setPlayer(GameServer.getInstance().getActivePlayer(playerID));
		actionQueue = new MCActionQueue();
		this.matchID = matchID;
	}
	
	// Hack constructor for single player version
	public MCMatchPlayer(int playerID, int matchID, Boolean setDeck){
		this.setPlayer(GameServer.getInstance().getActivePlayer(playerID));
		actionQueue = new MCActionQueue();
		this.matchID = matchID;
	}
	
	public int addMatchAction(MCMatchAction action){
		actionQueue.push(action);
		return actionQueue.getActionCount();
	}
	
	public MCMatchAction getMatchAction(){
		return actionQueue.pop();
	}

	public void setHasDisconnected(boolean hasDisconnected){
		this.hasDisconnected = hasDisconnected;
	}
	
	public boolean getHasDisconnected(){
		return hasDisconnected;
	}
		
	public int getID(){
		return getPlayer().getID();
	}
	
	/*
	 * @param response : Response to set
	 */
	public void addResponse(GameResponse response){
		getPlayer().getClient().add(response);
	}
	

	/**
	 * @return the isReady
	 */
	public boolean isReady() {
		return isReady;
	}


	/**
	 * @return the matchID
	 */
	public int getMatchID() {
		return matchID;
	}


	/**
	 * @param isReady the isReady to set
	 */
	public void setReady(boolean isReady) {
		this.isReady = isReady;
	}


	/**
	 * @param matchID the matchID to set
	 */
	public void setMatchID(int matchID) {
		this.matchID = matchID;
	}
	
	/**
	 * @return the isActive
	 */
	public boolean isActive() {
		return isActive;
	}


	/**
	 * @param isActive the isActive to set
	 */
	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}


	public Player getPlayer() {
		return player;
	}


	public void setPlayer(Player player) {
		this.player = player;
	}
	
	public static void  main(){
		MCMatchPlayer player = new MCMatchPlayer(10, 20);
		MCMatchAction action = new MCMatchAction();
		action.setActionID((short)209);
		action.setIntCount(1);
		action.addInt(2);
		player.addMatchAction(action);
		MCMatchAction action2 = player.getMatchAction();
		Log.printf("Poppped action", action2.getActionID());
		
		System.exit(0);
	}
	
        // DH change
        public int getBetStatus() {
            return betStatus;
        }
        public void setBetStatus(int betStatus) {
            this.betStatus = betStatus;
        }
        public int getImproveAmount() {
            return improveAmount;
        }
        public void setImproveAmount(int improveAmount) {
            this.improveAmount = improveAmount;
        }
        public void setPlayerName(String playerName) {
            this.playerName = playerName;
        }
        public String getPlayerName() {
            return playerName;
        }        
        
        public GameClient getClient() {
            return client;
        }
        public void setClient(GameClient client) {
            this.client = client;
        }
        public ResponseConvergeBetUpdate getResponse() {
            return response;
        }
        public void setResponse(ResponseConvergeBetUpdate response) {
            this.response = response;
        }
}
