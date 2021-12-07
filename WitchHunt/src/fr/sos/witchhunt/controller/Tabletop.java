package fr.sos.witchhunt.controller;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import fr.sos.witchhunt.model.cards.ExistingRumourCards;

import fr.sos.witchhunt.model.cards.RumourCardsPile;

import fr.sos.witchhunt.model.players.Player;

/**
 * <p><b>Class playing a central role, aggregating all of Model's components and controlling the game's flow.</b></p> 
 * <p>Starts the match and starts new {@link Round rounds} until the victory conditions are met.</p>
 * <p>Contains a {@link this.playersList list} of all {@link fr.sos.witchhunt.model.players.Player players} participating to the match.</p>
 * <p>Interacts with an instance of {@link ScoreCounter} to check whether victory conditions are met or not.</p>
 * <p>Instanciated as a {@link https://refactoringguru.cn/design-patterns/singleton Singleton} using the {@link this#getInstance() static method}.
 * Can be accessed globally, exposing its public and non-static members, using the same method.</p>
 * @see #getInstance()
 * @see #playersList
 * @see Round
 */
public final class Tabletop {
	
	//ATTRIBUTES
	/**
	 * Unique instance of this class. Initialized and returned by {@link #getInstance()}.
	 */
	private static volatile Tabletop instance = null;
	
	private Round currentRound;
	/**
	 * Reference to an instance of ScoreCounter
	 */
	private ScoreCounter scoreCounter;
	
	/**
	 * List of all players participating to the match.
	 */
	private List <Player> playersList;
	/**
	 * List containing all existing rumour cards in the match. They should never be cloned.
	 */
	private RumourCardsPile allCardsPile;
	/**
	 * <p><b>Reference to the last player whose identity wasn't revealed at the end of a round.</b></p>
	 * <p>Is kept after a round in order to determine the first player to play the next one.</p>
	 * @see Round#checkLastUnrevealedPlayer()
	 */
	private Player lastUnrevealedPlayer=null;
	/**
	 * This boolean becomes true when the game is tied, that is to say,
	 * when two players of more have the same score, the latter one being 5 or higher.
	 */
	private boolean gameIsTied=false;
	
	//CONSTRUCTOR
	/**
	 * <p><b>Private Constructor called by the {@link #getInstance()} method.</b></p>
	 * <p>Initializes the players list and creates a list containing one unique instance of each type of Rumour Card.</p> 
	 * @see fr.sos.witchhunt.model.cards.ExistingRumourCards ExistingRumourCards
	 */
	private Tabletop () {
		playersList = new ArrayList<Player> ();
		allCardsPile = new RumourCardsPile(ExistingRumourCards.getInstance().getList());
	}
	/**
	 * <b>Public and static method used for constructing an unique instance of it and accessing its non-static members</b>
	 * @return A reference to the unique instance of this class.
	 */
	public static Tabletop getInstance() {
		if(Tabletop.instance == null) {
			synchronized(Tabletop.class) {
				if(Tabletop.instance == null) {
					Tabletop.instance = new Tabletop();
				}
			}
		}
		return Tabletop.instance;
	}
	
	//UTILS METHODS
	/**
	 * <b>Adds a player permanently to the list of all participating players.</b>
	 * @param p The player to be added.
	 */
	public static void addPlayer(Player p) {
		instance.playersList.add(p);
	}

	/**
	 * <p><b>Reset the states of every player and card in the match.</b></p>
	 * <p>Calls the {@link fr.sos.witchhunt.model.Resettable#reset() reset} method of all known {@link fr.sos.witchhunt.model.Resettable} entities.
	 * @see fr.sos.witchhunt.model.Resettable
	 * @see fr.sos.witchhunt.model.Resettable#reset()
	 * @see fr.sos.witchhunt.model.players.Player#reset()
	 * @see fr.sos.witchhunt.model.cards.RumourCard#reset()
	 */
	private void resetStates() {
		int playersCount = this.playersList.size();
		allCardsPile.getCards().forEach(rc -> rc.reset());
		for(int i=0; i<playersCount; i++) {
			/*Classic for loop required here : it is not possible to iterate directly on playersList,
			 * As CPU Players' changes of strategy are considered as a modification of the list, which will throw an Exception.
			 */
			playersList.get(i).reset();
		}
	}
	
	//GAME FLOW METHODS
	/**
	 * <p><b>Checks whether the victory conditions are met or not.</b></p>
	 * <p>Called after each {@link Round round}. While false is returned, a new one starts.</p>
	 * @return Depends on the victory conditions defined at {@link ScoreCounter#hasWinner()}.
	 * @see ScoreCounter
	 * @see ScoreCounter#hasWinner()
	 * @see #startPlaying()
	 */
	private boolean gameIsOver() {
		return scoreCounter.hasWinner();
	}
	
	/**
	 * <p><b>Game-loop method. Handles game flow from the match's start to its end.</b></p>
	 * <p>Not in charge of the match's configuration, only of its flow.</p>
	 * <p>While the game has no unique winner, resets the states of every player and card and creates a new round.</p>
	 * <p>Creates a special round containing only leading players if the game is tied ({@link #gameIsTied}</p>
	 * <p>Instanciates the match's {@link ScoreCounter score counter}.</p>
	 */
	public void startPlaying() {
		Application.displayController.displayMatchStartScreen();

		scoreCounter = new ScoreCounter(); //Instanciating the score counter
		
		new Round(); //Starting a first round.
		while (!gameIsOver()){ //Looping while the game has no unique winner at the end of a round.
			Application.displayController.displayScoreBoard(scoreCounter.getScoreBoard()); //Displays the scoreboard at the end of each round
			Application.inputController.wannaContinue(); //Pauses the game until user-input is received
			Application.displayController.crlf();
			resetStates(); //After a round, reset the states of all known cards and players.
			List<Player> potentialWinners = scoreCounter.getPotentialWinners(); //List of leading players having score >=5
			if(!potentialWinners.isEmpty()) {
				playersList.stream().filter(p->!potentialWinners.contains(p)).forEach(p->p.setActive(false));
				Application.displayController.displayGameIsTiedScreen(potentialWinners);
				this.gameIsTied=true; //preparing a special round with only the leading players.
			}
			currentRound = null;
			currentRound = new Round(); //Starting new rounds until the victory conditions are finally met.
		}
		resetStates();
		this.gameIsTied=false;
		currentRound.setRoundNumber(0);
		currentRound = null;
		
		Application.displayController.displayMatchEndScreen();
		Application.displayController.displayScoreBoard(scoreCounter.getScoreBoard());
		Application.displayController.displayWinnerScreen(scoreCounter.getWinner()); //Requests for a display of the winning player.
		
		
		scoreCounter=null;
		playersList = null;
		playersList = new ArrayList<Player>();
		
		Application.inputController.wannaContinue();
		Game.getInstance().gotoMainMenu(); //Going back to the game's main menu at the end of a round.
	}
	
	//GETTERS
	public int getRoundNumber() {
		return currentRound.getRoundNumber();
	}
	
	public Round getCurrentRound() {
		return currentRound;
	}
	
	public RumourCardsPile getAllCardsPile() {
		return allCardsPile;
	}
	
	public RumourCardsPile getPile() {
		return currentRound.getPile();
	}
	
	public Turn getCurrentTurn() {
		return currentRound.getCurrentTurn();
	}
	
	/**
	 * @return the latest player accused.
	 */
	public Player getAccusedPlayer() {
		return currentRound.getCurrentTurn().getAccusedPlayer();
	}
	/**
	 * @return the latest player who accused another one.
	 */
	public Player getAccusator() {
		return getCurrentTurn().getAccusator();
	}
	/**
	 * @return the latest player who got targetted by a Hunt! effect.
	 */
	public Player getHuntedPlayer() {
		return getCurrentTurn().getHuntedPlayer();
	}
	/**
	 * @return the latest player who triggered a Hunt! effect or checked if it was possible to do so.
	 */
	public Player getHunter() {
		return getCurrentTurn().getHunter();
	}
	/**
	 * @return the player playing the current turn.
	 * @see Turn
	 */
	public Player getCurrentPlayer() {
		return currentRound.getCurrentPlayer();
	}
	/**
	 * @return The list of all players participating to the match.
	 */
	public List<Player> getPlayersList() {
		return playersList;
	}
	/**
	 * @return The list of all players who are still participating to the current Round.
	 * @see fr.sos.witchhunt.model.players.Player#isActive() Player::isActive()
	 */
	public List<Player> getActivePlayersList() {
		return this.playersList.stream().filter(p -> p.isActive()).collect(Collectors.toList());
	}
	public ScoreCounter getScoreCounter() {
		return scoreCounter;
	}
	public int getPlayersCount() {
		return playersList.size();
	}
	/**
	 * @return The list of all currently accusable players.
	 * @see fr.sos.witchhunt.model.players.Player#isAccusable() Player::isAccusable()
	 */
	public List<Player> getAccusablePlayersList() {
		return this.getActivePlayersList().stream().filter(p -> p.isAccusable()).collect(Collectors.toList());
	}
	/**
	 * @return The list of all currently unrevealed players.
	 * @see fr.sos.witchhunt.model.players.Player#isRevealed() Player::isRevealed()
	 */
	public List<Player> getUnrevealedPlayersList() {
		return getActivePlayersList().stream().filter(p -> !p.isRevealed()).collect(Collectors.toList());
	}
	
	public Player getLastUnrevealedPlayer() {
		return this.lastUnrevealedPlayer;
	}
	
	/**
	 * Notably used by CPUPlayers to choose their strategy.
	 * @return The list of players having the highest score.
	 * @see ScoreCounter#getLeadingPlayers()
	 */
	public List<Player> getLeadingPlayers() {
		return this.scoreCounter.getLeadingPlayers();
	}
	/**
	 * Notably used by CPUPlayers to choose their strategy.
	 * @return The list of players having the lowest score.
	 * @see ScoreCounter#getLastPlayers()
	 */
	public List<Player> getLastPlayers() {
		return this.scoreCounter.getLastPlayers();
	}
	/**
	 * @return The list of all players, ranked by descending score.
	 */
	public List<Player> getRanking(){
		return this.scoreCounter.getRanking();
	}
	public boolean gameIsTied() {
		return this.gameIsTied;
	}
	public int getTotalRumourCardsCount() {
		return ExistingRumourCards.getInstance().getSet().size();
	}
	
	//SETTERS
	public void setCurrentRound(Round r) {
		currentRound = r;
	}
	/**
	 * @param accusedPlayer The latest player who got accused.
	 */
	public void setAccusedPlayer(Player accusedPlayer) {
		getCurrentTurn().setAccusedPlayer(accusedPlayer);
	}
	/**
	 * @param accusator The player who accused another.
	 */
	public void setAccusator(Player accusator) {
		getCurrentTurn().setAccusator(accusator);
	}
	/**
	 * @param The latest player targetted by a hunt effect.
	 */
	public void setHuntedPlayer(Player huntedPlayer) {
		getCurrentTurn().setHuntedPlayer(huntedPlayer);
	}
	/**
	 * @param The latest player who triggered a Hunt! effect or checked if they could do so.
	 */
	public void setHunter(Player hunter) {
		getCurrentTurn().setHunter(hunter);
	}
	public void setLastUnrevealedPlayer(Player p) {
		this.lastUnrevealedPlayer=p;
	}
}
