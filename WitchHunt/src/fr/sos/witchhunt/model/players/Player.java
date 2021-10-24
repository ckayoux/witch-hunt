package fr.sos.witchhunt.model.players;

import fr.sos.witchhunt.DisplayObservable;
import fr.sos.witchhunt.DisplayObserver;

public abstract class Player implements DisplayObservable {
	
	//ATTRIBUTES
	protected String name;
	protected int id;
	protected DisplayObserver displayObserver;
	
	//CONSTRUCTORS
	public Player(String name, int id) {
		if(name=="") {
			this.name="Player "+Integer.toString(id);
		}
		else {
			this.name=name;
			this.id=id;
		}
	}
	public Player(int id) {
		this.id=id;
	}
	
	//GAME ACTIONS METHODS
	public void playTurn() {
		requestLog(this.name + " : it's my turn !");
	}
	
	//DISPLAY METHODS
	@Override
	public void requestLog(String msg) {
		displayObserver.passLog(msg);
	}
	
	//SETTERS
	public void setDisplayObserver(DisplayObserver dO) {
		this.displayObserver=dO;
	}
	
}
