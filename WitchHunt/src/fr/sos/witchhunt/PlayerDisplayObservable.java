package fr.sos.witchhunt;

import fr.sos.witchhunt.DisplayMediator;

import fr.sos.witchhunt.model.Menu;
import fr.sos.witchhunt.model.cards.RumourCard;
import fr.sos.witchhunt.model.cards.RumourCardsPile;
import fr.sos.witchhunt.model.players.Player;
/**
 * <p><b>Interface implemented by Player and its daughter classes.</b></p>
 * <p>Specifies a set of methods allowing the players to communicate with a display controller implementing {@link DisplayMediator}.</p>
 * 
 * @see DisplayMediator
 * @see fr.sos.witchhunt.model.players.Player
 * @see fr.sos.witchhunt.model.players.CPUPlayer
 * @see fr.sos.witchhunt.model.players.HumanPlayer
 */
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
	public void requestPlayerPlaysWitchEffectScreen(RumourCard rc);
	public void requestPlayerPlaysHuntEffectScreen(RumourCard rc);
	public void requestEmptyRCPScreen(RumourCardsPile rcp);
	public void requestDiscardCardScreen(RumourCard rc);
	public void requestLookAtPlayersIdentityScreen(Player target);
	public void requestHasChosenCardScreen(RumourCard chosen, boolean forceReveal);
	public void requestHasResetCardScreen(RumourCard chosen);
	public void requestTakeNextTurnScreen();
	public void requestPlayTurnAgainScreen();
	public void sleep(int ms);
	public void requestForcedToAccuseScreen(Player by);
	public void requestStealCardFromScreen(Player stolenPlayer);

}
