package fr.sos.witchhunt.view.gui;

import java.util.List;
import java.util.function.Supplier;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import fr.sos.witchhunt.controller.InputMediator;
import fr.sos.witchhunt.model.Menu;
import fr.sos.witchhunt.model.cards.RumourCard;
import fr.sos.witchhunt.model.cards.RumourCardsPile;
import fr.sos.witchhunt.model.flow.Tabletop;
import fr.sos.witchhunt.model.flow.ScoreCounter.ScoreBoard;
import fr.sos.witchhunt.model.players.DefenseAction;
import fr.sos.witchhunt.model.players.Player;
import fr.sos.witchhunt.model.players.TurnAction;
import fr.sos.witchhunt.model.players.cpustrategies.PlayStrategy;
import fr.sos.witchhunt.view.gui.scenes.game.GamePanel;
import fr.sos.witchhunt.view.gui.scenes.mainmenu.MainMenuPanel;
import fr.sos.witchhunt.view.gui.scenes.matchsetup.MatchSetupPanel;

public final class GUIView {

	private InputMediator inputMediator;
	private Window w; 
	private GamePanel gamePanel;
	private MainMenuPanel mainMenuPanel;
	private MatchSetupPanel matchSetupPanel;
	private JPanel currentPanel = null;
	private Tabletop tabletop;
	
	//CONSTRUCTOR
	public GUIView (InputMediator im) {
		this.inputMediator=im;
		this.w = new Window ();

		//gotoMainMenuPanel();

	}
	
	
	public void gotoMainMenuPanel() {
		if(this.mainMenuPanel==null) {
			this.w.setResizable(false);
			this.gamePanel=null;
			this.matchSetupPanel=null;
			this.mainMenuPanel=this.w.renderMainMenuPanel();
			this.mainMenuPanel.setInputMediator(inputMediator);
		}
		
	}
	public void gotoMatchSetupPanel() {
		if (this.matchSetupPanel==null) {
			this.w.setResizable(true);
			this.gamePanel=null;
			this.mainMenuPanel=null;
			this.w.setTitle(Window.defaultTitle+" : match setup");
			this.matchSetupPanel=this.w.renderMatchSetupPanel(this.tabletop);
			this.matchSetupPanel.setInputMediator(inputMediator);
			this.matchSetupPanel.update();
		}
		
	}
	
	public void gotoGamePanel() {
		if (this.gamePanel==null&&this.tabletop!=null){
			this.matchSetupPanel=null;
			this.mainMenuPanel=null;
			this.gamePanel=this.w.renderGamePanel();
			this.gamePanel.setInputMediator(inputMediator);
			this.gamePanel.displayMainNotification(new Notification("Let the witch hunt begin !\n",Theme.NORMAL));
			this.gamePanel.renderPlayers(tabletop.getPlayersList());
			this.gamePanel.setPlayerButtonsEnabled(false);
		}
	}


	
	public void wannaContinue(InputMediator im) {
		SwingUtilities.invokeLater(new Runnable() {
		    @Override
			public void run() {
		        gamePanel.wannaContinue(im);
		    }
		});
	}
	
	public void choiceHasBeenMade(Object o) {
		if(this.gamePanel!=null) {
			SwingUtilities.invokeLater(new Runnable() {
				 @Override
				public void run() {
					 if(gamePanel!=null) gamePanel.choiceHasBeenMade(o);
				}
			});
		}
		else if (this.mainMenuPanel!=null){
			SwingUtilities.invokeLater(new Runnable() {
				 @Override
				public void run() {
				    mainMenuPanel.resetChoicesPanel();
				}
			});
		}
	}

	public void displayCardsChoiceMenu(Menu m,boolean forceReveal) {
		if(this.gamePanel!=null) {
			SwingUtilities.invokeLater(new Runnable() {
				 @Override
				public void run() {
				    gamePanel.displayCards(m,forceReveal);
				}
			});
		}
	}
	
	public void displayMenu(Menu m) {
		if(this.gamePanel!=null) {
			SwingUtilities.invokeLater(new Runnable() {
				 @Override
				public void run() {
				    gamePanel.displayMenu(m);
				}
			});
		}
		else if (this.mainMenuPanel!=null) {
			SwingUtilities.invokeLater(new Runnable() {
				 @Override
				public void run() {
				    mainMenuPanel.displayMenu(m);
				}
			});
		}
	}
	
	public void makeChoice(Menu m) {
		if(this.gamePanel!=null) {
			SwingUtilities.invokeLater(new Runnable() {
				 @Override
				public void run() {
				    gamePanel.makeChoice(m);
				}
			});
		}
		else if (this.mainMenuPanel!=null) {
			SwingUtilities.invokeLater(new Runnable() {
				 @Override
				public void run() {
				    mainMenuPanel.makeChoice(m);
				}
			});
		}
	}
	
	public void displayPlayerAddedScreen(Player p) {
		if(this.matchSetupPanel!=null) this.matchSetupPanel.addedOne(p);
	}

	public void displayRoundStartScreen(int roundNumber) {
		if(gamePanel!=null) {
			this.w.setTitle(Window.defaultTitle+" : Round "+roundNumber);
			gamePanel.resetNotificationsBoxes();
			gamePanel.displayMainNotification(new Notification("ROUND "+roundNumber+"\n",Theme.NORMAL));
			gamePanel.displaySecondaryNotification(new Notification(Theme.HARD_SEPARATOR));
			gamePanel.setPlayerButtonsEnabled(false);
			gamePanel.updateScoreDisplay(null, tabletop.getScoreCounter());
		}
		
	}

	public void displayRoundEndScreen(int roundNumber) {
		if (gamePanel!=null) {
			gamePanel.displayMainNotification(new Notification("ROUND "+roundNumber+" IS OVER.\n",Theme.NORMAL));
			gamePanel.displaySecondaryNotification(new Notification(Theme.HARD_SEPARATOR));
			gamePanel.resetCardsPanel();
			gamePanel.hidePile();
		}
		
	}


	public void displayWinnerScreen(Player winner) {
		if(gamePanel!=null) {
			gamePanel.displayMainNotification(
				new Notification(
						winner.getName() + " has won the game with a score of "+winner.getScore()+" !\n",
						Theme.SCORE
				)
			);
		}
	}


	public void displayChooseIdentityScreen() {
		if (gamePanel!=null) {
			gamePanel.displaySecondaryNotification(new Notification(Theme.CRLF));
			gamePanel.displayMainNotification(
					new Notification(
							"Please choose your identity for this round.\n",
							Theme.NORMAL
					)
			);
			gamePanel.displaySecondaryNotification(new Notification(Theme.CRLF));
			gamePanel.boldenPlayer(tabletop.getPlayersList().get(0));
		}
	}

	public void displayHasChosenIdentityScreen(Player p) {
		if(gamePanel!=null) {
			gamePanel.displaySecondaryNotification(
					new Notification(
							"\t"+p.getName()+" has chosen their identity.\n",
							Theme.NORMAL
					)
			);
			gamePanel.unBoldenPlayer(p);
			gamePanel.updatePlayerActivityStatus(p);
			int index = tabletop.getPlayersList().indexOf(p);
			if (index+1<tabletop.getPlayersList().size()) {
				gamePanel.boldenPlayer(tabletop.getPlayersList().get(index+1));
			}
		}
	}
	
	public void displayAllPlayersHaveChosenTheirIdentityScreen() {
		gamePanel.displaySecondaryNotification(new Notification(Theme.CRLF));
		if(gamePanel!=null)
			gamePanel.displayMainNotification(
					new Notification(
							"All players have chosen their identity.\n",
							Theme.NORMAL
					)
			);
			gamePanel.displaySecondaryNotification(new Notification(Theme.CRLF));
			gamePanel.displaySecondaryNotification(new Notification(Theme.HARD_SEPARATOR));
			gamePanel.displaySecondaryNotification(new Notification(Theme.CRLF));
	}

	public void displayHandDistributionScreen(int distributedCardsCount, int discardedCardsCount) {
		if (gamePanel!=null) {
			gamePanel.displayMainNotification(
					new Notification(
							"Everyone has received "+distributedCardsCount+" Rumour cards."
							+((discardedCardsCount>0)?"\n"+ discardedCardsCount+ " were put into the pile.":"")+"\n",
							Theme.NORMAL
					)
			);
			gamePanel.displaySecondaryNotification(new Notification(Theme.CRLF));
			gamePanel.displaySecondaryNotification(new Notification(Theme.HARD_SEPARATOR));
			gamePanel.displaySecondaryNotification(new Notification(Theme.CRLF));
			gamePanel.renderPile(tabletop.getPile());
			tabletop.getActivePlayersList().forEach(p->gamePanel.updateDeckContent(p.getHand(),false));
			gamePanel.displayOwnerLabels();
			gamePanel.updateDeckContent(tabletop.getPile(),false);
		}
	}


	public void displayPlayTurnScreen(Player p) {
		if(gamePanel!=null) {
			String name = p.getName();
			gamePanel.displayMainNotification(
					new Notification(
							"It is " + name + (name.charAt(name.length()-1)!='s'?"'s":"'") + " turn.\n",
							Theme.TURN
					)
			);

			gamePanel.switchDeck(p.getHand(),true);
			gamePanel.showCurrentPlayer(p);
		}
	}
	
	public void displayEndOfTurnScreen() {
		if(gamePanel!=null){
			
			gamePanel.displaySecondaryNotification(new Notification(Theme.CRLF));
			gamePanel.displaySecondaryNotification(
					new Notification(
							Theme.LIGHT_SEPARATOR
					)
			);
			gamePanel.displaySecondaryNotification(new Notification(Theme.CRLF));
			gamePanel.resetEffects();
		}
	}
	
	

	public void displayAccusationScreen(Player accusator, Player accused) {
		if(gamePanel!=null) {
			gamePanel.displayMainNotification(
					new Notification(
							accused.getName()+", you've been accused by "+accusator.getName()+" !\n",
							Theme.OFFENSIVE
					)
			);
			gamePanel.showAccusedPlayer(accused);
		}
		
	}
	
	public void showHuntedPlayer(Player huntedPlayer) {
		if(gamePanel!=null)  {
			gamePanel.showHuntedPlayer(huntedPlayer);
		}
	}
	

	public void showWitchingPlayer(Player witchingPlayer) {
		if(gamePanel!=null)  {
			gamePanel.showWitchingPlayer(witchingPlayer);
		}
	}



	public void displayChooseDefenseMessage(Player p) {
		if(gamePanel!=null) 
			gamePanel.switchDeck(p.getHand(),true);
		/*if(gamePanel!=null) 
			gamePanel.displaySecondaryNotification(
				new Notification(
						"The village's pyre has been lit up.\n",
						Theme.NORMAL
				)
			);*/

	}


	public void displayForcedToRevealScreen() {
		if(gamePanel!=null)
			gamePanel.displaySecondaryNotification(
					new Notification(
							"You have no other choice but to reveal who you really are...\n",
							Theme.NORMAL
					)
			);
	}
	
	public void displayGoingToRevealScreen(Player p) {
		if(gamePanel!=null)
			gamePanel.displayMainNotification(
					new Notification(
							p.getName()+" is going to reveal their identity !\n",
							Theme.NORMAL
					)
			);
	}


	public void displayVillagerRevealScreen(Player p) {
		if(gamePanel!=null)
			gamePanel.displayMainNotification(
					new Notification(
							p.getName()+" is only a Villager.\n",
							Theme.HUNT
					)
			);
	}


	public void displayWitchRevealScreen(Player p) {
		if(gamePanel!=null)
			gamePanel.displayMainNotification(
					new Notification(
							"You fools, "+p.getName()+" was a Witch !\n",
							Theme.WITCH
					)
			);
	}


	public void displayWitchEliminatedScreen(Player p) {
		if(gamePanel!=null)
			gamePanel.displayMainNotification(
					new Notification(
							p.getName()+" is a Witch !\n",
							Theme.WITCH
					)
			);
	}


	public void displayScoreUpdateScreen(Player p, int points) {
		if (gamePanel!=null) {
			StringBuffer sb = new StringBuffer(p.getName());
			sb.append(' ');
			if(points>0) sb.append("earned");
			else if (points<0) sb.append("lost");
			sb.append(' ');
			sb.append(Integer.toString(Math.abs(points)));
			sb.append(" point");sb.append((Math.abs(points) == 1)?'s':"");
			sb.append(" (Total : "+ p.getScore()+")\n");
			
			gamePanel.displayMainNotification(new Notification(sb.toString(),Theme.SCORE));
			gamePanel.updateScoreDisplay(p,tabletop.getScoreCounter());
		}
	}


	public void displayEliminationScreen(Player eliminator, Player victim) {
		if(gamePanel!=null) {
			Notification eliminationNotification;
			if(eliminator!=victim)
				eliminationNotification=new Notification(eliminator.getName()+ " has sentenced " + victim.getName() + " to the stake !\n",Theme.OFFENSIVE);
			else 
				eliminationNotification=new Notification(eliminator.getName()+" has eliminated themselve !\n",Theme.OFFENSIVE);
			gamePanel.displayMainNotification(eliminationNotification);
			gamePanel.updatePlayerActivityStatus(victim);
		}
		
	}


	public void displayLastUnrevealedScreen(Player lastUnrevealedPlayer) {
		if(gamePanel!=null)
			gamePanel.displaySecondaryNotification(
					new Notification(
							lastUnrevealedPlayer.getName()+" is the last unrevealed player remaining.\n",
							Theme.NORMAL
					)
			);
	}


	public void displayNoCardsScreen(Player p) {
		if(gamePanel!=null)
			gamePanel.displaySecondaryNotification(
					new Notification(
							p.getName() + " has got no cards.\n",
							Theme.NORMAL
					)
			);
	}


	public void displayOnlyTwoUnrevealedRemainingScreen() {
		//AFFICHER QUELQUE CHOSE DE PARTICULIER SUR LE PLAYERS PANEL
	}
	

	

	public void displayPlayerPlaysWitchEffectScreen(Player p, RumourCard rc) {
		if(gamePanel!=null) {
			//gamePanel.displaySecondaryNotification(new Notification(Theme.CRLF));
			gamePanel.displayMainNotification(
					new Notification(
							p.getName()+" uses "+stringifyRumourCard(rc, true,rc::getWitchEffectDescription),
							Theme.WITCH
					)
			);
			gamePanel.updateCardRevealStatus(rc);
			//gamePanel.displaySecondaryNotification(new Notification(Theme.CRLF));
		}
		//UPDATE REVEAL STATUS
	}
	
	public void displayPlayerPlaysHuntEffectScreen(Player p, RumourCard rc) {
		if(gamePanel!=null) {
			//gamePanel.displaySecondaryNotification(new Notification(Theme.CRLF));
			gamePanel.displayMainNotification(
					new Notification(
						p.getName()+" uses "+stringifyRumourCard(rc, true,rc::getHuntEffectDescription),
						Theme.HUNT
					)
			);
			gamePanel.updateCardRevealStatus(rc);
			//gamePanel.displaySecondaryNotification(new Notification(Theme.CRLF));
		}
		//UPDATE REVEAL STATUS
	}
	
	
	public void displayPlayerHasChosenCardScreen(Player p, RumourCard chosen, RumourCardsPile from, boolean forceReveal) {
		if(gamePanel!=null) {
			//gamePanel.displaySecondaryNotification(new Notification(Theme.CRLF));
			gamePanel.displayMainNotification(
					new Notification(
							p.getName()+" has taken "+stringifyRumourCard(chosen, forceReveal, chosen::getWitchEffectDescription ,chosen::getHuntEffectDescription),
							Theme.NORMAL
					)
			);
			gamePanel.updateDeckContent(p.getHand(),forceReveal||playerChoosesOwnCard(p, from));
			if(from!=p.getHand()) gamePanel.updateDeckContent(from,forceReveal||playerChoosesOwnCard(p, from));
			gamePanel.resetCardsEffects();
		}
		//UPDATE CARDS PANEL
	}


	public void displayPlayerHasDiscardedCardScreen(Player owner, RumourCard rc) {
		if(gamePanel!=null) {
			//if(rc.isRevealed())gamePanel.displaySecondaryNotification(new Notification(Theme.CRLF));
			gamePanel.displayMainNotification(
					new Notification(
						owner.getName()+" has discarded "+stringifyRumourCard(rc, false, rc::getWitchEffectDescription ,rc::getHuntEffectDescription),
						Theme.NORMAL
					)
			);
			//if(rc.isRevealed())gamePanel.displaySecondaryNotification(new Notification(Theme.CRLF));
			gamePanel.updateDeckContent(owner.getHand(),false);
			gamePanel.updateDeckContent(tabletop.getPile(),false);
			gamePanel.resetCardsEffects();
		}
		//UPDATE PILE AND OWNER'S HAND
	}


	public void displayEmptyHandMessage(Player owner) {
		if(gamePanel!=null)
			gamePanel.displayMainNotification(
					new Notification(
						owner.getName()+"'"+((owner.getName().charAt(owner.getName().length()-1)!='s')?"s":"")+ "hand is empty.\n",
						Theme.NORMAL
					)
			);
	}


	public void displayNoCardsInPileScreen() {
		if(gamePanel!=null)
			gamePanel.displayMainNotification(
					new Notification(
						"The pile is empty.\n",
						Theme.NORMAL
					)
			);
	}


	public void displayLookAtPlayersIdentityScreen(Player me, Player target) {
		if(gamePanel!=null)
			gamePanel.displaySecondaryNotification(
					new Notification(
							me.getName() + " is looking at "+target.getName()+"'"+((target.getName().charAt(target.getName().length()-1)!='s')?"s":"") + " identity.\n",
							Theme.HUNT
					)
			);
	}


	public void displaySecretlyDisplayIdentity(Player target) {
		if(gamePanel!=null)
			gamePanel.displayMainNotification(
					new Notification(
							"Others, close your eyes !\n"
							+ "\t"+target.getName() + " is a "+target.getIdentity().toString().toLowerCase()+".\n",
							Theme.NORMAL
					)
			);
	}


	public void displayPlayerHasResetCardScreen(Player player, RumourCard rc) {
		if(gamePanel!=null) {
			//gamePanel.displaySecondaryNotification(new Notification(Theme.CRLF));
			gamePanel.displayMainNotification(
					new Notification(
							player.getName() +" took back "+stringifyRumourCard(rc, true, rc::getWitchEffectDescription ,rc::getHuntEffectDescription),
							Theme.NORMAL
					)
			);
			//gamePanel.updateCardRevealStatus(rc); no
			//gamePanel.displaySecondaryNotification(new Notification(Theme.CRLF));
		}
		//MAKE RC UNREVEALED ON CARDS PANEL
	}


	public void displayTakeNextTurnScreen(Player p) {
		if(gamePanel!=null)
			gamePanel.displayMainNotification(
					new Notification(
							p.getName() + " takes the next turn.\n",
							Theme.TURN
					)
			);
	}
	
	
	public void displayPlayTurnAgainScreen(Player p) {
		if(gamePanel!=null)
			gamePanel.displayMainNotification(
					new Notification(
							p.getName() + " takes another turn.\n",
							Theme.TURN
					)
			);
	}



	public void displayForcedToAccuseScreen(Player theOneWhoMustAccuse, Player theOneWhoForcedThem) {
		if(gamePanel!=null) {
			String accuserName = theOneWhoMustAccuse.getName();
			String forcedByName = theOneWhoForcedThem.getName();
			gamePanel.displayMainNotification(
				new Notification(
						accuserName+", "+forcedByName+" forced you to accuse "+((theOneWhoForcedThem.isImmunized())?"someone else ":"them ")+ "!\n",
						Theme.NORMAL
				)
			);
		}
	}


	public void displayStealCardScreen(Player thief, Player stolenPlayer) {
		if(gamePanel!=null)
			gamePanel.displayMainNotification(
					new Notification(
							thief.getName()+" is subtilizing a card from "+stolenPlayer.getName()+"'"+((stolenPlayer.getName().charAt(stolenPlayer.getName().length()-1)!='s')?"s":"")+" hands !\n",
							Theme.NORMAL
					)
			);
	}


	public void displayGameIsTiedScreen(int score, List<Player> potentialWinners) {
		if(gamePanel!=null) {
			gamePanel.displayMainNotification(
					new Notification("~ The game is tied ! ~",
					Theme.SCORE
				)
			);
			
			gamePanel.displaySecondaryNotification(
					new Notification(Theme.HARD_SEPARATOR)
			);
			
			List<String> duelists = potentialWinners.stream().map(p->p.getName()).toList();
			StringBuffer sb = new StringBuffer("\t");
			for(int i=0; i<duelists.size(); i++) {
				sb.append(duelists.get(i));
				if(i<duelists.size()-2) sb.append(", ");
				else if(i==duelists.size()-2) sb.append(" and ");
			}
			sb.append((duelists.size()>2)?" all":" both");
			sb.append(" have a score of ");
			sb.append(score);
			sb.append(" and must compete for the first place !\n");
			
			gamePanel.displaySecondaryNotification(new Notification(
						sb.toString(),
						Theme.TURN
					)
			);
			//DISABLE inactive players' button
		}
		
	}
	
	private String stringifyRumourCard(RumourCard rc,boolean forceReveal,Supplier<String> ...effectGetters) {
		StringBuffer sb = new StringBuffer();
		if(forceReveal||rc.isRevealed()) {
			sb.append(rc.getName());
			sb.append(" :");
			sb.append('\n');
			if(!rc.getAdditionalEffectDescription().equals("")) {
				sb.append("\t*"); 
				sb.append(rc.getAdditionalEffectDescription().replace("/+/", "\t ")); 
				sb.append("*\n");
			}
			for(Supplier<String> getter : effectGetters) {
				String off;
				if(getter.get().equals(rc.getWitchEffectDescription())&&effectGetters.length>1) {
					sb.append("\tWitch : ");
					off=" ".repeat("Witch : ".length());
				}
				else if (getter.get().equals(rc.getHuntEffectDescription())&&effectGetters.length>1) {
					sb.append("\tHunt : ");
					off=" ".repeat("Hunt : ".length());
				}
				else {
					sb.append("\t");
					off="";
				}
				sb.append(getter.get().replace("/+/","\t"+off));
				sb.append('\n');
			}	
			return sb.toString();
		}
		else {
			return "*Unrevealed*\n";
		}
		
	}
	
	public void setTabletop(Tabletop tabletop) {
		this.tabletop=tabletop;
	}
	
	private boolean playerChoosesOwnCard(Player p,RumourCardsPile from) {

		return p==from.getOwner();
	}
	
	public void displayChooseAnyCardScreen(Player p,RumourCardsPile from) {
		if(gamePanel!=null) { 
			gamePanel.switchDeck(from,playerChoosesOwnCard(p,from));
			gamePanel.makeCardsChoosable(p, from, from, Theme.NORMAL);
			displayCardsChoiceMenu(new Menu("Select any card",from.getCards().toArray()),playerChoosesOwnCard(p,from));
		}
	}
	public void displayChooseRevealedCardScreen(Player p,RumourCardsPile from) {
		gamePanel.switchDeck(from,playerChoosesOwnCard(p,from));
		gamePanel.makeCardsChoosable(p, from, from.getRevealedSubpile(), Theme.NORMAL);
		if(gamePanel!=null) displayCardsChoiceMenu(new Menu("Select a revealed card",from.getCards().toArray()),playerChoosesOwnCard(p,from));
	}
	public void displayChooseUnrevealedCardScreen(Player p,RumourCardsPile from) {
		gamePanel.switchDeck(from,playerChoosesOwnCard(p,from));
		gamePanel.makeCardsChoosable(p, from, from.getUnrevealedSubpile(), Theme.NORMAL);
		if(gamePanel!=null) displayCardsChoiceMenu(new Menu("Select an unrevealed card",from.getCards().toArray()),playerChoosesOwnCard(p,from));
	}


	public void displayChooseWitchCardScreen(Player p,RumourCardsPile from) {
		if(gamePanel!=null) {
			gamePanel.switchDeck(p.getHand(),true);
			gamePanel.makeCardsChoosable(p, p.getHand() , from, Theme.WITCH);
			displayCardsChoiceMenu(new Menu("Select a card with a valid Witch? effect",from.getCards().toArray()),true);
			SwingUtilities.invokeLater(new Runnable() {
			    @Override
				public void run() {
			    	gamePanel.setActionsPanelThemeAsForActionType(DefenseAction.WITCH);
			    }
			});
			
		}
	}


	public void displayChooseHuntCardScreen(Player p,RumourCardsPile from) {
		if(gamePanel!=null)  {
			gamePanel.switchDeck(p.getHand(),true);
			gamePanel.makeCardsChoosable(p, p.getHand() , from, Theme.HUNT);
			displayCardsChoiceMenu(new Menu("Select a card with a valid Hunt! effect",from.getCards().toArray()),true);
			SwingUtilities.invokeLater(new Runnable() {
			    @Override
				public void run() {
			    	gamePanel.setActionsPanelThemeAsForActionType(TurnAction.HUNT);
			    }
			});
			
		}
	}


	public void displayChooseCardToDiscardScreen(Player p) {
		if(gamePanel!=null) gamePanel.switchDeck(p.getHand(), true);
	}


	public void displayStrategyChange(Player p, PlayStrategy strat) {
		if(gamePanel!=null) {
			gamePanel.displaySecondaryNotification(
					new Notification(p.getName()+" opts for "+strat.toString()+".\n",
					Theme.SCORE
				)
			);
		}
	}


	public void displayScoreBoard(ScoreBoard sb) {
		if(gamePanel!=null) {
			gamePanel.displaySecondaryNotification(new Notification(Theme.CRLF));
			gamePanel.displaySecondaryNotification(
				new Notification(
						"\t"+sb.toString().replace("/+/", "\t"),
						Theme.SCORE
				)
			);
			gamePanel.displaySecondaryNotification(new Notification(Theme.CRLF));
		}
	}


	
}
