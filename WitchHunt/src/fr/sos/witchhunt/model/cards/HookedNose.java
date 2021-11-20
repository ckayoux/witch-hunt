package fr.sos.witchhunt.model.cards;

import fr.sos.witchhunt.model.players.Player;

public final class HookedNose extends RumourCard {
	//TODO : test
	public HookedNose () {
		this.witchEffect = new WitchEffect("Take one card from the hand of the player who accused you.\n"
				+ "/+/Take next turn.",3) {
			
			@Override
			public void perform() {
				//TODO : take one card (not random) from the hand of the player who accused you (if hasRumourCards)
				Player me = getMyself();
				Player accusator = getTarget();
				if(accusator.hasRumourCards()) {
					RumourCard chosen = me.chooseAnyCard(accusator.getHand(), false);
					chosen.reset();
					me.requestHasChosenCardScreen(chosen,false);
					me.takeRumourCard(chosen, accusator);
				}
				else {
					accusator.requestNoCardsScreen();
				}
				takeNextTurn();
			}
		};
		
		this.huntEffect = new HuntEffect("Choose next player.\n"
				+ "/+/Before their turn, take a random card from their hand and add it to your hand."
				, 2) {
			@Override
			public void perform() {
				Player me = getMyself();
				Player target = chooseNextPlayer();
				if(target.hasRumourCards()) {
					RumourCard chosen = me.chooseAnyCard(target.getHand(), false);
					chosen.reset();
					me.requestHasChosenCardScreen(chosen,false);
					me.takeRumourCard(chosen, target);
				}
				else {
					target.requestNoCardsScreen()
;				}
			}
			
		};
	}

}
