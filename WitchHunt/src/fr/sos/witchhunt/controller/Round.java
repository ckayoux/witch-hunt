package fr.sos.witchhunt.controller;

import java.util.List;

import fr.sos.witchhunt.model.cards.RumourCard;
import fr.sos.witchhunt.model.cards.RumourCardsPile;
import fr.sos.witchhunt.model.players.Player;

public final class Round {
	
	//ATTRIBUTES
	private static Player currentPlayer;
	private static Player nextPlayer;
	private static int roundNumber=0;
	private Turn currentTurn;
	private RumourCardsPile commonPile;
	
	//CONSTRUCTOR
	public Round() {
		//For the first round, a random player begins.
		if(roundNumber == 0) currentPlayer=Tabletop.getInstance().getPlayersList().get((int)Math.random()*Tabletop.getInstance().getPlayersCount());
		//For all other rounds, the last unrevealed player begins.
		else currentPlayer=Tabletop.getInstance().getLastUnrevealedPlayer();
		
		roundNumber++;
		Turn.setTurnNumber(0);
		
		Application.displayController.displayRoundStartScreen(roundNumber);
		//at the start of a round, before the first turn, each player has to choose an Identity and Rumour cards.
		distributeIdentity();
		commonPile = new RumourCardsPile();
		distributeHand();
		
		do {
			setNextPlayerClockwise(); //By default, the player who takes the next turn is the one after the current player in Tabletop's players list.
			new Turn(currentPlayer);
			currentPlayer=nextPlayer; //Rumour cards played during the turn might have changed the next player.
		}while(!isOver()); //We keep starting new turns until the round is over.
		
		recycleRumourCards(); //returning all rumourCards to the main pile, of Tabletop's instance
		
		Tabletop.getInstance().setLastUnrevealedPlayer(getNextPlayer());//TODO : pass last unrevealed player instead.
		
		Application.displayController.displayRoundEndScreen(roundNumber);
	}
	
	//UTILS METHODS
	private void distributeIdentity() {
		Application.displayController.chooseIdentityScreen();
		for(Player p : getPlayersList()) {
			p.chooseIdentity();
		}
		Application.displayController.crlf();
		Application.displayController.drawWeakDashedLine();
	}
	private void distributeHand() {
		int playersNumber = Tabletop.getInstance().getPlayersCount();
		int totalCardsCount = Game.getTotalRumourCardsCount();
		int distributedCardsNumber = (int) Math.floor(totalCardsCount / (float)playersNumber);
		int discardedCardsNumber = totalCardsCount % playersNumber;
		
		RumourCardsPile allCardsPile = Tabletop.getInstance().getAllCardsPile();
		allCardsPile.shuffle();
		for (int i=0; i<discardedCardsNumber; i++) {
			RumourCard givenCard = allCardsPile.getCards().get(i);
			allCardsPile.giveCard(givenCard, commonPile);
		}
		for (Player p : Tabletop.getInstance().getPlayersList()) {
			for(int i=0; i<distributedCardsNumber; i++) {
				RumourCard givenCard = allCardsPile.getCards().get(0);
				allCardsPile.giveCard(givenCard, p.getHand());
			}
		}
		
		Application.displayController.distributeHandScreen();
		
	}
	private boolean isOver() {
		/*The round is over when :
		 * - Whether only one player is still unrevealed
		 * - or one player reached a score of 5*/
		/*REAL CONDITION, To uncomment when other functionnalities are ready :
		int unrevealedPlayersCount = 0;
		for (Player p : Tabletop.getInstance().getPlayersList()) {
			if (!p.isRevealed()) unrevealedPlayersCount++;
		}
		return (unrevealedPlayersCount <= 1);*/
		return (Turn.getTurnNumber()<5)?false:true;//TEMPORARY
	}
	
	private void recycleRumourCards() {
		Tabletop.getInstance().getAllCardsPile().eat(commonPile); //returning the common pile to the main cards pile
		for(Player p : getPlayersList()) Tabletop.getInstance().getAllCardsPile().eat(p.getHand());
	}
	
	//GETTERS
	public int getRoundNumber() {
		return this.roundNumber;
	}
	public static Player getCurrentPlayer() {
		return currentPlayer;
	}
	public static Player getNextPlayer() {
		return nextPlayer;
	}
	public Turn getCurrentTurn() {
		return currentTurn;
	}
	private List<Player> getPlayersList() {
		return Tabletop.getInstance().getPlayersList();
	}
	public RumourCardsPile getPile() {
		return this.commonPile;
	}
	
	//SETTERS
	public void setNextPlayer(Player p) {
		nextPlayer = p;
	}
	public void setNextPlayerClockwise() {
		//automatically sets the next player to the one after the current player in Tabletop's players list
		List <Player> L = Tabletop.getInstance().getActivePlayersList();
		nextPlayer = L.get((L.indexOf(currentPlayer)+1) % L.size());
	}

	public static void setRoundNumber(int n) {
		roundNumber = n;
	}
}
