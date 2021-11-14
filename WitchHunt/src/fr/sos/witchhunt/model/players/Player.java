package fr.sos.witchhunt.model.players;

import java.util.List;

import fr.sos.witchhunt.model.Menu;
import fr.sos.witchhunt.PlayerDisplayObservable;
import fr.sos.witchhunt.DisplayMediator;
import fr.sos.witchhunt.controller.Tabletop;
import fr.sos.witchhunt.model.Identity;
import fr.sos.witchhunt.model.Resettable;
import fr.sos.witchhunt.model.cards.IdentityCard;
import fr.sos.witchhunt.model.cards.RumourCard;
import fr.sos.witchhunt.model.cards.RumourCardsPile;

public abstract class Player implements PlayerDisplayObservable, Resettable {
	
	//ATTRIBUTES
	protected String name;
	protected int id;
	protected int score;
	protected RumourCardsPile hand;
	protected Identity identity;
	protected IdentityCard identityCard;
	protected DisplayMediator displayMediator;
	private boolean immunized; //the Rumour Card EvilEye can give immunity to accusation to one player for one turn
	private boolean active = true;
	
	//CONSTRUCTORS
	public Player(String name, int id) {
		if(name=="") {
			this.name="Player "+Integer.toString(id);
		}
		else {
			this.name=name;
			this.id=id;
		}
		this.identityCard = new IdentityCard();
		this.hand = new RumourCardsPile(this);
	}
	public Player(int id) {
		this.id=id;
		this.identityCard = new IdentityCard();
		this.hand = new RumourCardsPile();
	}
	
	//GAME ACTIONS METHODS
	public abstract void chooseIdentity();;
	public Identity revealIdentity() {
		requestIdentityRevealScreen();
		this.identityCard.reveal();
		return this.identity;
	}
	
	/*public void addRumourCard(RumourCard rc) {
		hand.addCard(rc);
	}*/
	public void takeRumourCard(RumourCard rc,RumourCardsPile from) {
		from.giveCard(rc, this.hand);
	}
	
	protected abstract RumourCard selectCardToDiscard() ;
		
	public void discard(RumourCard rc) {
		//rc.reset();
		requestDiscardCardScreen(rc);
		this.hand.giveCard(rc, Tabletop.getInstance().getPile());
	}
	public void discard() {
		discard(selectCardToDiscard());
	}
	public void discardRandomCard() {
		//let's assume we can only discard unrevealed cards, unless all the cards are revealed
		RumourCard chosenCard;
		if(this.hasRumourCards()) {
			if(this.hasUnrevealedRumourCards()) {
				chosenCard = this.getUnrevealedSubhand().getRandomCard();
			}
			else {
				chosenCard = this.hand.getRandomCard();
			}
			
			if (chosenCard != null) discard(chosenCard);
		}
		else requestNoCardsScreen();
	}
	
	public boolean hasUnrevealedRumourCards() {
		return !this.getRevealedSubhand().equals(this.hand);
	}
	
	public abstract TurnAction chooseTurnAction();
	
	public void playTurn() {
		requestPlayTurnScreen();
		switch(chooseTurnAction()) {
			case ACCUSE:
				accuse();
				break;
			case HUNT:
				hunt();
				break;
		}
		
	}
	
	protected void accuse() {
		accuse(choosePlayerToAccuse());
		clearImmunities();
	}
	
	protected abstract Player choosePlayerToAccuse();
	
	public void accuse(Player p) {
		Tabletop.getInstance().setAccusator(this);
		Tabletop.getInstance().setAccusedPlayer(p);
		requestAccusationScreen(p);
		Identity returnedIdentity = p.defend();
		if(returnedIdentity != null) {
			switch(returnedIdentity) {
			case VILLAGER:
				this.addScore(1);
				break;
			case WITCH:
				eliminate(p);
				this.addScore(2);
				break;
			}
		}
	}
	
	
	public abstract DefenseAction chooseDefenseAction();
	
	public Identity defend() {
		if(this.canWitch()) {
			requestChooseDefenseScreen();
			switch(chooseDefenseAction()) {
				case WITCH:
					this.witch();
					return null; //every witch effect protects the accused player's identity. null is always returned
				case REVEAL:
					return this.revealIdentity(); //accused players reveal their identity and return it 
			}
		}
		else return forcedReveal();
		return null;
	}
	
	public Identity forcedReveal() {
		requestForcedToRevealScreen();
		return this.revealIdentity();
		
	}
	public abstract RumourCard selectWitchCard();
	public abstract RumourCard selectHuntCard();
	
	protected void witch () {
		RumourCard chosen = selectWitchCard();
		requestPlayerPlaysWitchEffectScreen(chosen);
		chosen.witch();
	}
	
	protected void hunt() {
		Tabletop.getInstance().setHunter(this);
		RumourCard chosen = selectHuntCard();
		requestPlayerPlaysHuntEffectScreen(chosen);
		chosen.hunt();
	};
	public abstract Player chooseTarget(List<Player> eligiblePlayers);
	
	public Player chooseHuntedTarget(List<Player> eligiblePlayersList) {
		Player chosenTarget = chooseTarget(eligiblePlayersList);
		Tabletop.getInstance().setHuntedPlayer(chosenTarget);
		return chosenTarget;
	}
	
	public abstract Player chooseNextPlayer();
	
	public void winRound() {
		Tabletop.getInstance().setLastUnrevealedPlayer(this);
		requestLastUnrevealedPlayerScreen();
		switch (this.identity) {
			case WITCH:
				this.addScore(2);
				break;
				
			case VILLAGER:
				this.addScore(1);
				break;
		}
	}
	
	public void reset() {
		this.identity = null;
		this.identityCard.reset();
		this.active = true;
	}
	
	//DISPLAY METHODS
	@Override
	public void requestLog(String msg) {
		displayMediator.passLog(msg);
	}
	
	@Override
	public void requestPlayTurnScreen() {
		displayMediator.displayPlayTurnScreen(name);
	}
	
	@Override
	public void requestAccusationScreen(Player p) {
		displayMediator.displayAccusationScreen(this,p);
	}	
	
	@Override
	public void requestDisplayPossibilities(Menu m) {
		displayMediator.displayPossibilities(m);
	}
	

	@Override
	public void requestChooseDefenseScreen() {
		displayMediator.displayChooseDefenseScreen();
	}
	
	@Override
	public void requestForcedToRevealScreen() {
		displayMediator.displayForcedToRevealScreen();
	}
	
	@Override
	public void requestIdentityRevealScreen() {
		displayMediator.displayIdentityRevealScreen(this);
	}
	
	@Override
	public void requestScoreUpdateScreen(int scoreUpdatedBy) {
		displayMediator.displayScoreUpdateScreen(this,scoreUpdatedBy);
	}
	
	@Override
	public void requestEliminationScreen(Player victim) {
		displayMediator.displayEliminationScreen(this,victim);
	}
	
	@Override
	public void requestLastUnrevealedPlayerScreen() {
		displayMediator.displayLastUnrevealedPlayerScreen(this);
	}
	
	@Override
	public void requestNoCardsScreen() {
		displayMediator.displayNoCardsScreen(this);
	}
	
	@Override
	public void requestSelectCardScreen() {
		displayMediator.displaySelectCardScreen();
	}

	@Override
	public void requestSelectUnrevealedCardScreen() {
		displayMediator.displaySelectUnrevealedCardScreen();
	}
	
	@Override
	public void requestSelectRevealedCardScreen() {
		displayMediator.displaySelectRevealedCardScreen();
	}
	
	public void showHand(boolean forcedReveal) {
		this.hand.show(displayMediator, forcedReveal);
	}
	
	@Override
	public void requestPlayerPlaysWitchEffectScreen(RumourCard rc) {
		displayMediator.displayPlayerPlaysWitchEffectScreen(this,rc);
	}
	@Override
	public void requestPlayerPlaysHuntEffectScreen(RumourCard rc) {
		displayMediator.displayPlayerPlaysHuntEffectScreen(this,rc);
	}
	@Override
	public void requestHasChosenCardScreen(RumourCard chosen) {
		displayMediator.displayHasChosenCardScreen(this,chosen);
	}
	@Override
	public void requestEmptyRCPScreen(RumourCardsPile rcp) {
		displayMediator.displayNoCardsInPileScreen(rcp);
	};
	@Override
	public void requestDiscardCardScreen(RumourCard rc) {
		displayMediator.displayDiscardCardScreen(this,rc);
	}
	@Override
	public void requestLookAtPlayersIdentityScreen(Player target) {
		displayMediator.displayLookAtPlayersIdentityScreen(this,target);
	}
	@Override
	public void requestHasResetCardScreen(RumourCard chosen) {
		displayMediator.displayPlayerHasResetCardScreen(this,chosen);
	}
	
	//GETTERS
	public String getName() {
		return this.name;
	}
	public RumourCardsPile getHand() {
		return this.hand;
	}
	public RumourCardsPile getUnrevealedSubhand() {
		return this.hand.getUnrevealedSubpile();
	}
	public RumourCardsPile getRevealedSubhand() {
		return this.hand.getRevealedSubpile();
	}
	public Identity getIdentity() {
		return this.identity;
	}
	public IdentityCard getIdentityCard() {
		return this.identityCard;
	}
	public boolean isRevealed() {
		return this.identityCard.isRevealed();
	}
	public int getScore() {
		return this.score;
	}
	public boolean isImmunized() {
		return this.immunized;
	}
	public boolean isAccusable() {
		if(!this.isRevealed() && !this.immunized) return true;
		else return false;
	}
	public boolean isActive() {
		return this.active;
	}
	public boolean isImmunizedAgainstRumourCard(RumourCard rc) {
		//some cards grant immunity against the huntEffect of others. Example : Broomstick prevents from being targeted by the huntEffect of AngryMob
		for(RumourCard card : this.hand.getCards()) {
			if(card.grantsImmunityAgainst(rc)) return true;
		}
		return false;
	}
	public boolean hasRumourCards() {
		return (!this.hand.isEmpty());
	}
	
	//SETTERS
	public void addScore(int pts) {
		this.score += pts;
		requestScoreUpdateScreen(pts);
	}
	
	public void immunize() {
		this.immunized = true;
	}
	
	public void looseImmunity() {
		this.immunized = false;
	}
	private void clearImmunities() {
		if (!this.immunized) Tabletop.getInstance().getPlayersList().forEach(p -> {if(p.isImmunized()) p.looseImmunity();});
		/*is called at the end of the accuse method.
		if the accusator is not the player who immunized himself,
		he will remove everyone's immunity afterwhile - because this bonus is only one-use.*/
		this.immunized = false;
	}
	
	public void eliminate() {
		this.active = false;
	}
	
	private void eliminate(Player victim) {
		victim.eliminate();
		requestEliminationScreen(victim);
	}
	
	@Override
	public void setDisplayMediator(DisplayMediator dm) {
		this.displayMediator=dm;
	}
	
	//UTILS
	public List<Player> getAccusablePlayers() {
		List <Player> l = Tabletop.getInstance().getAccusablePlayersList();
		l.remove(this);
		return l;
	}
	
	protected boolean canHunt() {
		Tabletop.getInstance().setHunter(this);
		return (!this.hand.getPlayableHuntSubpile().isEmpty());
	}
	
	public boolean canWitch() {
		return (!this.hand.getPlayableWitchSubpile().isEmpty());
	}
	
	public String toString() {
		return this.name;
	}
	
	public abstract RumourCard chooseAnyCard(RumourCardsPile from,boolean seeUnrevealedCards);
	public abstract RumourCard chooseRevealedCard(RumourCardsPile from);
	
	public boolean targetPileContainsCards(RumourCardsPile rcp) {
		if(rcp.isEmpty()) {
			requestEmptyRCPScreen(rcp);
			return false;
		}
		return true;
	}
	public Identity lookAtPlayersIdentity(Player target) {
		requestLookAtPlayersIdentityScreen(target);
		return target.getIdentity();
	}
	
	
}
