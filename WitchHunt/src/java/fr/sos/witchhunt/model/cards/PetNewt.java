package fr.sos.witchhunt.model.cards;

import java.util.List;

import fr.sos.witchhunt.model.flow.Tabletop;
import fr.sos.witchhunt.model.players.Player;

public final class PetNewt extends RumourCard {

	
	public PetNewt () {
		
		this.givesCards=true;
		this.isOffensive=true;
		
		this.witchEffect = new WitchEffect() {
			
			@Override
			public void perform() {
				takeNextTurn();
			}
		};
		
		this.huntEffect = new HuntEffect("Take a revealed rumour card from any other player into your hand.\n"
				+"/+/Choose next player.",2) {
			//cpu players target player with the best revealed rumour cards first
			@Override
			public void perform() {
				//take a revealed (and not random) rumour card from any other player into your hand + reset it ?
				Player me = getMyself();
				List<Player> eligiblePlayers = Tabletop.getInstance().getActivePlayersList().stream()
						.filter(p -> (p != getMyself() && p.hasRevealedRumourCards() )).toList();
				Player target = me.chooseHuntedTarget(eligiblePlayers);
				RumourCard chosenCard = me.chooseRevealedCard(target.getRevealedSubhand());
				me.takeRumourCard(chosenCard, target);
				me.requestHasChosenCardScreen(chosenCard, target.getHand(),false);
				chosenCard.reset();
				chooseNextPlayer();
			}
			
			@Override
			public boolean isAllowed() {
				//there must be at least 1 other player with a revealed rumour card
				List<Player> eligiblePlayers = Tabletop.getInstance().getActivePlayersList().stream()
						.filter(p -> (p != getMyself() && p.hasRevealedRumourCards() )).toList();
				return (!eligiblePlayers.isEmpty());
			}
			
		};
	}

}
