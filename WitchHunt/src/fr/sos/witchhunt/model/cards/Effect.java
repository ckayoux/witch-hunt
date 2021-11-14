package fr.sos.witchhunt.model.cards;

import fr.sos.witchhunt.controller.Tabletop;
import fr.sos.witchhunt.model.players.Player;

public abstract class Effect {
	private String description;
	private int value;
	
	public Effect () {
		value=1;
		description="";
	}
	public Effect (String desc,int value) {
		this.value=value;
		this.description=desc;
	}
	
	public abstract void perform(); //has to be redefined
	public boolean isAllowed() {
		return true; //has to be redefined for some cards
	}
	
	public void takeNextTurn() {
		Tabletop.getInstance().getCurrentRound().setNextPlayer(getMyself());
	}
	
	public Player chooseNextPlayer() {
		Player nextPlayer = getMyself().chooseNextPlayer();
		Tabletop.getInstance().getCurrentRound().setNextPlayer(nextPlayer);
		return nextPlayer;
	}
	
	//GETTERS
	public String getDescription() {
		return description;
	}
	public int getValue() {
		return value;
	}
	protected abstract Player getMyself();
}
