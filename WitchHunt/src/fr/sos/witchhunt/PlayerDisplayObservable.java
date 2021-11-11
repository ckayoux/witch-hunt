package fr.sos.witchhunt;

import fr.sos.witchhunt.DisplayMediator;
import fr.sos.witchhunt.model.Menu;
import fr.sos.witchhunt.model.players.Player;

public interface PlayerDisplayObservable {

	public void requestLog(String msg);
	public void requestDisplayPossibilities(Menu m);
	public void setDisplayMediator(DisplayMediator dm);
	public void requestPlayTurnScreen();
	public void requestAccusationScreen(Player accused);
	public void requestChooseDefenseScreen();
	public void requestForcedToRevealScreen();
	public void requestIdentityRevealScreen();
	public void requestScoreUpdateScreen(int scoreUpdatedBy);
	public void requestEliminationScreen(Player victim);
	public void requestLastUnrevealedPlayerScreen();
	public void requestNoCardsScreen();
	public void requestSelectCardScreen();
	public void requestSelectUnrevealedCardScreen();
	public void requestSelectRevealedCardScreen();
	public void requestPlayerPlaysEffectScreen();
}
