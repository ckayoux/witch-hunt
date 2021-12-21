package fr.sos.witchhunt.model.cards;

import fr.sos.witchhunt.model.players.Player;

public final class PointedHat extends RumourCard {
	
	private PointedHat cardInstance;
	//done, but not tested at all
	//toutDoux : overall value increases when you have a revealed rumour card
	public PointedHat () {
		this.givesCards=true;
		cardInstance = this;
		
		this.witchEffect = new WitchEffect("(Only playable if you have a revealed Rumour card)\n"
				+ "/+/Take one of your own revealed Rumour cards into your hand.\n"
				+ "/+/Take next turn.",2) {
			private Player me;
			@Override
			public void perform() {
				me=getMyself();
				RumourCard chosen = me.chooseRevealedCard(new RumourCardsPile(
						me.getHand().getCards().stream()
						.filter(c -> c!=cardInstance).toList())
						); //so the hunter cant choose this one card
				chosen.reset();
				me.requestHasResetCardScreen(chosen);
				
				takeNextTurn();
			}
			
			@Override
			public boolean isAllowed() {
				me=getMyself();
				return (!me.getHand().getRevealedSubpile().isEmpty());
			}
			
		};
		
		this.huntEffect = new HuntEffect("(Only playable if you have a revealed Rumour card)\n"
				+ "/+/Take one of your own revealed Rumour cards into your hand.\n"
				+ "/+/Choose next player.",2) {
			private Player me;
			@Override
			public void perform() {
				me=getMyself();
				RumourCard chosen = me.chooseRevealedCard(new RumourCardsPile(
						me.getHand().getCards().stream()
						.filter(c -> c!=cardInstance).toList())
						); //so the hunter cant choose this one card
				chosen.reset();
				me.requestHasResetCardScreen(chosen);
				
				chooseNextPlayer();
			}
			
			@Override
			public boolean isAllowed() {
				me=getMyself();
				return (!me.getHand().getRevealedSubpile().isEmpty());
			}
			
		};
	}

}
