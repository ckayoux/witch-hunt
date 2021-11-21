package fr.sos.witchhunt.model.players;

import java.util.List;
import java.util.concurrent.TimeUnit;

import fr.sos.witchhunt.controller.Tabletop;
import fr.sos.witchhunt.model.Identity;
import fr.sos.witchhunt.model.Menu;
import fr.sos.witchhunt.model.cards.RumourCard;
import fr.sos.witchhunt.model.cards.RumourCardsPile;
import fr.sos.witchhunt.model.players.cpustrategies.*;

public final class CPUPlayer extends Player {
	
	private PlayStrategy chosenStrategy= new ExploringStrategy();
	
	public CPUPlayer(int id, int cpuNumberHowMuch) {
		super(id);
		this.name="CPU "+Integer.toString(cpuNumberHowMuch);
	}
	
	@Override
	public void playTurn() {
		super.playTurn();		
	}
	
	@Override
	public final void chooseIdentity() {
		this.identity = chosenStrategy.selectIdentity();
		this.identityCard.setChosenIdentity(this.identity);
		displayMediator.passLog("\t"+this.name+" has chosen its identity.");
	}

	@Override
	protected Player choosePlayerToAccuse() {
		return chosenStrategy.selectPlayerToAccuse(getAccusablePlayers());
	}
	
	@Override
	public Player chooseTarget(List<Player> eligiblePlayers) {
		return chosenStrategy.selectTarget(eligiblePlayers);
	}

	@Override
	public Player chooseNextPlayer() {
		return chosenStrategy.selectNextPlayer(Tabletop.getInstance().getActivePlayersList().stream().filter(p->p!=this).toList());
	}
	
	@Override
	public TurnAction chooseTurnAction() {
		return chosenStrategy.selectTurnAction(this.identity,this.hand,this.canHunt());
	}

	@Override
	public DefenseAction chooseDefenseAction() {
		//must initialize Tabletop's hunter with canHunt, as the player will look at its playable hunt cards to make their decision
		this.canHunt();
		return chosenStrategy.selectDefenseAction(this.canWitch(),this.hand);
	}


	@Override
	public RumourCard selectWitchCard() {
		return this.chosenStrategy.selectWitchCard(this.hand.getPlayableWitchSubpile());
	}

	@Override
	public RumourCard selectCardToDiscard(RumourCardsPile in) {
		if(this.hasUnrevealedRumourCards()) {
			return chosenStrategy.selectCardToDiscard(in.getUnrevealedSubpile());
		}
		else return chosenStrategy.selectCardToDiscard(in);
	}

	@Override
	public RumourCard selectHuntCard() {
		return this.chosenStrategy.selectHuntCard(this.hand.getPlayableHuntSubpile());
	}

	@Override
	public RumourCard chooseAnyCard(RumourCardsPile rcp, boolean seeUnrevealedCards) {
		if(targetPileContainsCards(rcp)) {
			return chosenStrategy.selectBestCard(rcp,seeUnrevealedCards);
		}
		else return null;
	}
	
	@Override
	public RumourCard chooseRevealedCard(RumourCardsPile from) {
		if(targetPileContainsCards(from.getRevealedSubpile())) {
			return chosenStrategy.selectBestCard(from.getRevealedSubpile(),false);
		}
		else return null;
	}

	@Override
	public Identity lookAtPlayersIdentity(Player target) {
		Identity id = super.lookAtPlayersIdentity(target);
		/*Tout doux : remember the players identity and accuse him the next time
		 * not necessarely if it is a villager and there is another very suspicious and weak player,
		 * necessarely otherwise
		 */
		return id;
	}

	@Override
	public DefenseAction revealOrDiscard() {
		if(this.hasRumourCards()&&!this.isRevealed()) {
			return chosenStrategy.revealOrDiscard(this.getIdentity(),this.getHand());
		}
		else if(!this.hasRumourCards()&&!this.isRevealed()) {
			requestNoCardsScreen();
			requestForcedToRevealScreen();
			return DefenseAction.REVEAL;
		}
		else { //cannot be chosen by ducking stool if is revealed and has no rumour cards
			return DefenseAction.DISCARD;
		}
	}



}
