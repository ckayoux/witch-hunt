package fr.sos.witchhunt.view.gui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import fr.sos.witchhunt.InputMediator;
import fr.sos.witchhunt.controller.CardSelectorController;
import fr.sos.witchhunt.controller.DeckSelectorButtonController;
import fr.sos.witchhunt.model.Menu;
import fr.sos.witchhunt.model.cards.RumourCard;
import fr.sos.witchhunt.model.cards.RumourCardsPile;
import fr.sos.witchhunt.model.players.HumanPlayer;
import fr.sos.witchhunt.model.players.Player;  

public class GamePanel extends GridBagPanel {
	
	private InputMediator inputMediator;
	
	private TopNotificationsPanel topNotificationsPanel;
	private ActionsPanel actionsPanel;
	private DeckSelectorPanel deckSelectorPanel;
	private CardsPanel cardsPanel;
	private BotNotificationsPanel botNotificationsPanel;
	private ScorePanel scorePanel;
	
	private GamePanel gamePanelInstance;
	
	
	
	public GamePanel() {
		super(10,10);
		this.gamePanelInstance=this;
		this.topNotificationsPanel = new TopNotificationsPanel(0,0,8,1);
		this.actionsPanel = new ActionsPanel(8,0,2,7);
		this.deckSelectorPanel = new DeckSelectorPanel(0,1,2,6);
		this.cardsPanel = new CardsPanel(2,1,6,6);
		this.botNotificationsPanel = new BotNotificationsPanel (0,7,8,3);
		this.scorePanel = new ScorePanel(8,7,2,3);
		
		buildCustomGridBag();
	}
	
	@Override
	public void init ( ) {
		/*TextBox textBox = new TextBox();
		textBox.setPreferredSize(this.botNotificationsPanel.getPan().getPreferredSize());
		this.botNotificationsPanel.getPan().add(textBox);
		this.botNotificationsPanel.setTextBox(textBox);*/
		cellsList.forEach(c->c.init());
	}
	
	@Override
	public Dimension getPreferredSize() {
		return new Dimension(Window.WIDTH,Window.HEIGHT);
	}
	@Override
	public Insets getInsets() {
		return new Insets(15,7,15,7); //vertical margin is double the horizontal margin
	}

	public void setInputMediator(InputMediator inputMediator) {
		this.inputMediator=inputMediator;
	}


	//DISPLAY METHODS
	
	public void displayMainNotification(Notification n) {
		botNotificationsPanel.appendNotification(n);
		topNotificationsPanel.setNotification(n);
	}
	public void displaySecondaryNotification(Notification n) {
		botNotificationsPanel.appendNotification(n);
	}
	
	public void resetNotificationsBoxes() {
		botNotificationsPanel.eraseContent();
		topNotificationsPanel.eraseContent();
	}
	
	
	
	
	
	
	public void wannaContinue(InputMediator im) {
		actionsPanel.wannaContinue(im);
	}
	
	public void displayMenu(Menu m) {
		actionsPanel.displayMenu(m);
	}
	
	public void displayCards(Menu m,boolean forceReveal) {
		actionsPanel.displayCards(m,forceReveal);
	}
	
	public void makeChoice(Menu m) {
		actionsPanel.makeChoice(m, inputMediator);
	}
	
	public void setActionsPanelThemeAsForActionType(Object o) {
		actionsPanel.setThemeAsForActionType(o);
	}

	
	public void choiceHasBeenMade(int choice) {
		if(actionsPanel.isRendered()) 
			actionsPanel.resetPane();
		if(actionsPanel.isChoosingACard())
			cardHasBeenChosen(cardsPanel.currentlyUsedForChoosingCard.choosableCards.get(choice-1));
	}
	
	
	
	
	
	public void renderPlayers(List<Player> pList) {
		pList.forEach(p->{
			new DeckSelectorButtonController(
					this.deckSelectorPanel.addPlayerButton(p),
					this
			);
			this.cardsPanel.addDeck(p.getHand());
		});
		this.deckSelectorPanel.renderPlayers();
	}
	
	public void renderPile(RumourCardsPile pile) {
		if(this.deckSelectorPanel.pileButton==null) {
			new DeckSelectorButtonController(this.deckSelectorPanel.makePileButton(pile),this);
			this.cardsPanel.addDeck(pile);
		}
		this.deckSelectorPanel.renderPile();
	}
	
	public void updateScoreDisplay(Player p) {
		this.deckSelectorPanel.updateScoreDisplay(p);
	}

	public void updatePlayerActivityStatus(Player p) {
		this.deckSelectorPanel.updateButtonByPlayer(p);
		if(!p.isActive()) cardsPanel.getDeck(p.getHand()).resetPane();
	}
	
	public void updateActivePlayers() {
		this.deckSelectorPanel.updatePlayerButtons();
	}
	
	public void showAccusedPlayer(Player accused) {
	//	deckSelectorPanel.resetAccusablePlayersEffects();
		deckSelectorPanel.themeUpPlayerButton(accused,NotificationType.OFFENSIVE);
	}

	public void showAccusablePlayers(List<Player> accusablePlayers) {
		deckSelectorPanel.setAccusablePlayers();
	}

	public void showHuntedPlayer(Player huntedPlayer) {
		deckSelectorPanel.themeUpPlayerButton(huntedPlayer,NotificationType.HUNT);
		
	}
	
	public void showWitchingPlayer(Player witchingPlayer) {
		deckSelectorPanel.themeUpPlayerButton(witchingPlayer, NotificationType.WITCH);
	}

	private void forceRevealCardsIfHuman(Player p) {
		if(p instanceof HumanPlayer) this.cardsPanel.getDeck(p.getHand()).forceReveal(true); //force reveal human players' cards when it's their turn
	}
	
	public void showCurrentPlayer(Player currentPlayer) {
		switchDeck(currentPlayer.getHand(), true);
		deckSelectorPanel.themeUpPlayerButton(currentPlayer,NotificationType.TURN);
	}

	public void boldenPlayer(Player p) {
		deckSelectorPanel.boldenPlayerButton(p);
	}
	
	public void unBoldenPlayer(Player p) {
		deckSelectorPanel.unBoldenPlayerButton(p);
	}
	
	public void resetEffects() {
		deckSelectorPanel.resetPlayersEffects();
		cardsPanel.backToNormalMode();
	}
	public void setPlayerButtonsEnabled(boolean enabled) {
		deckSelectorPanel.setPlayerButtonsEnabled(enabled);
	}
	
	public void setSelectedDeckButton(DeckSelectorButton b) {
		deckSelectorPanel.setSelectedDeckButton(b);
	}
	
	public DeckSelectorButton getSelectedDeckButton() {
		return deckSelectorPanel.getSelectedDeckButton();
	}
	
	public void updateCardRevealStatus(RumourCard rc){
		this.cardsPanel.getDeck(rc).updateCardRevealStatus(rc);
	}
	
	public void updateDeckContent(RumourCardsPile rcp) {
		this.cardsPanel.getDeck(rcp).updateContent();
		this.cardsPanel.getDeck(rcp).renderPane();
		if(rcp.isThePile()) this.deckSelectorPanel.updatePileButton();
	}
	
	public void switchDeck(RumourCardsPile rcp) {
		DeckSelectorButton selectedDeckButton = deckSelectorPanel.getSelectedDeckButton();
		if(selectedDeckButton!=null) {
			if(selectedDeckButton.getDeck()!=rcp) {
				deckSelectorPanel.setSelectedDeckButton(null);
			}
		}
		if(rcp.isThePile()) this.cardsPanel.showDeck(rcp);
		else this.cardsPanel.showDeck(rcp,false);
	}
	public void switchDeck(RumourCardsPile rcp, boolean onlyIffHuman) {
		if(rcp.getOwner() instanceof HumanPlayer) {
			forceRevealCardsIfHuman(rcp.getOwner());
			switchDeck(rcp);
		}
	}
	public void setSelectedCard(RenderedCard j) {
		this.cardsPanel.setSelectedCard(j);
	}
	public void setSelectedCard(RumourCard rc) {
		this.cardsPanel.setSelectedCard(rc);
	}
	public RenderedCard getSelectedCard() {
		return this.cardsPanel.getSelectedCard();
	}
	
	public void cardHasBeenChosen(RenderedCard j) {
		this.cardsPanel.resetChoosableCardsTheme(j);
		if(j!=null) j.bolden();
	}
	
	public void makeCardsChoosable(Player whoHasToChoose,RumourCardsPile deck, RumourCardsPile validSubpile,NotificationType theme) {
		if(whoHasToChoose instanceof HumanPlayer) {
			this.cardsPanel.makeCardsChoosable(deck, validSubpile, theme);
		}
	}
	
//SUBPANELS
	private class TopNotificationsPanel extends GridBagCell {
		private NotificationsBox notificationsBox = new NotificationsBox();
		
		public TopNotificationsPanel(int x, int y,int w, int h)  {
			super(x,y,w,h,defaultCellBorder,cellsList);
			this.getPan().setLayout(new BorderLayout());
			this.getPan().setBackground(Color.BLACK);
			//this.getPan().add(textBox);
		}
		
		public void setNotification(Notification n) {
			this.notificationsBox.setNotification(n);
		}

		public NotificationsBox getTextBox () {
			return notificationsBox;
		}
		
		public void eraseContent() {
			this.notificationsBox.eraseContent();
		}
		
		@Override
		public void init() {
			notificationsBox = new NotificationsBox();
			notificationsBox.setPreferredSize(getPan().getPreferredSize());
			this.getPan().add(notificationsBox);
		}
	}
	
	private class ActionsPanel extends ChoicesPanel{
		public ActionsPanel(int x, int y, int w, int h) {
			super(x,y,w,h,defaultCellBorder,cellsList);
		}
	}
	
	private class DeckSelectorPanel extends GridBagCell {
		private int marginHeight=40;
		private List<DeckSelectorButton> playersBList = new ArrayList<DeckSelectorButton>();
		private JPanel playersPart = new JPanel() {
			@Override
			public Insets getInsets() {
				return new Insets(marginHeight/4,0,0,0);
			}
		};
		private boolean firstScoreUpdateDone = false;
		private DeckSelectorButton selectedDeckButton = null;
		
		private DeckSelectorButton pileButton;
		private JPanel pilePart = new JPanel() {
			@Override
			public Insets getInsets() {
			return new Insets(0,0,marginHeight/4,0);
		}
	} ;
		
		public DeckSelectorPanel(int x, int y,int w, int h) {
			super(x,y,w,h,defaultCellBorder,cellsList);
			this.getPan().setBorder(null);
			this.getPan().setLayout(new BoxLayout(this.getPan(),BoxLayout.PAGE_AXIS));
			//this.getPan().setBackground(Color.GREEN);
		}



		@Override
		public void init() {
			this.getPan().setLayout(new BorderLayout());
			this.getPan().setPreferredSize(this.getPan().getPreferredSize());
			
			this.playersPart.setLayout(new BoxLayout(this.playersPart,BoxLayout.PAGE_AXIS));
			this.getPan().add(playersPart,BorderLayout.CENTER);
			this.getPan().add(pilePart,BorderLayout.SOUTH);
		}
		
		
		public void normalizeButtonsWidth() {
			double maxButtonWidth = Collections.max(playersBList.stream().mapToDouble(b->b.getPreferredSize().getWidth())
					.boxed().toList());
			playersBList.forEach(b->{
				int missingXMargin = (int) (maxButtonWidth - b.getPreferredSize().getWidth());
				int missingLeftMargin = missingXMargin/2 + missingXMargin%2;
				int missingRightMargin = missingXMargin - missingLeftMargin;
				b.setInsets(new Insets(b.getMargin().top,b.getMargin().left + missingLeftMargin, b.getMargin().bottom,b.getMargin().right + missingRightMargin));
				
			});
		}
	
		
		public DeckSelectorButton addPlayerButton(Player p) {
			DeckSelectorButton b = new DeckSelectorButton(p);
			this.playersBList.add(b);
			return b;

		}
		
		public DeckSelectorButton makePileButton(RumourCardsPile pile) {
			this.pileButton=new DeckSelectorButton(pile);
			return this.pileButton;
		}
		
		public void updateScoreDisplay(Player p) {
			if(!firstScoreUpdateDone) {
				this.playersBList.forEach(b->b.updateText());
				normalizeButtonsWidth();
				firstScoreUpdateDone=true;
			}
			else {
				this.playersBList.stream().filter(b->b.getAssociatedPlayer()==p).forEach(b->b.updateText());
			}
		}
		
		public void updatePlayerButtons() {
			this.playersBList.forEach(b-> {
				b.setEnabled(b.getAssociatedPlayer().isActive());
			});
			this.getPan().updateUI();
		}
		
		public void updatePileButton() {
			pileButton.updateText();
			pileButton.setEnabled(!pileButton.getDeck().isEmpty());
			this.getPan().updateUI();
		}
		
		
		public void updateButtonByPlayer(Player p) {
			this.playersBList.stream().filter(b->b.getAssociatedPlayer()==p).forEach(b->{
				b.setEnabled(b.getAssociatedPlayer().isActive());
			});
			this.getPan().updateUI();
		}
		
		public void resetPane() {
			this.playersPart.removeAll();
			this.pilePart.removeAll();
			this.playersPart.updateUI();
			this.pilePart.updateUI();
			pileButton=null;
		}
		
		public void renderPlayers() {
			normalizeButtonsWidth();
			this.playersBList.forEach(pb->{
				this.playersPart.add(pb);
				this.playersPart.add(Box.createRigidArea(new Dimension(0, this.marginHeight)));
			});
		}
		


		public void resetPlayersEffects() {
			playersBList.forEach(b->b.resetTheme());
			updatePlayerButtons();
		}
		
		public void setPlayerButtonsEnabled(boolean enabled) {
			playersBList.forEach(b->b.setEnabled(enabled));
		}

		public void renderPile() {
			if(pileButton!=null) this.pilePart.add(pileButton);
			else this.pileButton.updateText();
		}
	

	/*	public void resetAccusablePlayersEffects() {
			//TODO
		}*/

		public void themeUpPlayerButton(Player p, NotificationType nt) {
			this.playersBList.stream().filter(b->b.getAssociatedPlayer()==p).forEach(b->{
				b.resetTheme();
				b.makeTheme(nt);
			});
		}
		
		public void boldenPlayerButton(Player p) {
			this.playersBList.stream().filter(b->b.getAssociatedPlayer()==p).forEach(b->b.bolden());
		}
		
		public void unBoldenPlayerButton(Player p) {
			this.playersBList.stream().filter(b->b.getAssociatedPlayer()==p).forEach(b->b.unbolden());
		}
			
		
		public void setAccusablePlayers() {
			// TODO Auto-generated method stub
			
		}
		
		public void setSelectedDeckButton(DeckSelectorButton b) {
			if(this.selectedDeckButton!=null) {
				selectedDeckButton.unbolden();
			}
			
			if(b!=null) {
				cardsPanel.showDeck(b.getDeck());
				b.bolden();
			}
			else {
				if(this.selectedDeckButton!=null) this.selectedDeckButton.unbolden();
			}
			this.selectedDeckButton=b;
		}
		public DeckSelectorButton getSelectedDeckButton() {
			return selectedDeckButton;
		}


	}
	private class CardsPanel extends GridBagCell {
		
		private CardLayout cl = new CardLayout();
		private Map<RumourCardsPile,DeckPanel> M = new HashMap<RumourCardsPile,DeckPanel>();
		private List<DeckPanel> forceRevealedDecksList = new ArrayList<DeckPanel>();
		private RenderedCard selectedCard = null;
		private DeckPanel currentlyUsedForChoosingCard=null;
		
		public CardsPanel(int x, int y,int w, int h) {
			super(x,y,w,h,defaultCellBorder,cellsList);
			//this.getPan().setBackground(Color.YELLOW);
		}
		
		@Override
		public void init() {
			this.getPan().setPreferredSize(this.getPan().getPreferredSize());
			this.getPan().setLayout(cl);
		}
		
		public void addDeck(RumourCardsPile rcp) {
			DeckPanel deckPanel = new DeckPanel(rcp);
			M.put(rcp, deckPanel);
			deckPanel.updateContent();
			deckPanel.renderPane();
			JScrollPane jsp = new JScrollPane(deckPanel);
			jsp.setPreferredSize(this.getPan().getPreferredSize());
			jsp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			this.getPan().add(jsp,deckPanel.toString());
		}
		
		public DeckPanel getDeck(RumourCardsPile rcp) {
			DeckPanel mainPile = M.get(rcp);
			DeckPanel output=mainPile;
			if(mainPile==null) {
				//match on pile owner
				output = M.get(M.keySet().stream().filter(k->k.getOwner()==rcp.getOwner()).findFirst().get());
			}
			return output;
		}
		
		public DeckPanel getDeck(RumourCard rc) {
			RumourCardsPile containedIn = M.keySet().stream().filter(rcp->rcp.contains(rc)).findFirst().get();
			return M.get(containedIn);
		}
		
		public void showDeck(RumourCardsPile rcp) {
			cl.show(this.getPan(),getDeck(rcp).toString());
		}
		public void showDeck(RumourCardsPile rcp,boolean forceReveal) {
			if(forceReveal) getDeck(rcp).forceReveal(forceReveal);
			this.showDeck(rcp);
		}
		
		public void backToNormalMode() {
			Iterator<DeckPanel> it = forceRevealedDecksList.iterator();
			while(it.hasNext()) {
				DeckPanel d = it.next();
				d.forceReveal(false);
				if(d.choosableCards!=null) d.resetChoosableCardsTheme(null);
				it.remove();
			}
		}
		public void setSelectedCard(RenderedCard view) {			
			if(view!=null) {
				if(view!=selectedCard) {
					if(this.selectedCard!=null) this.selectedCard.unbolden();
					selectedCard=view;
					selectedCard.bolden();
				}
				else {
					selectedCard.unbolden(); //unbolden if already selected
					selectedCard=null;
				}
			}
			else {
				if(this.selectedCard!=null) this.selectedCard.unbolden();
			}
			
		}
		
		public void setSelectedCard(RumourCard rc) {
			RenderedCard view = this.getDeck(rc).getRenderedCardsList().stream().filter(j->j.getAssociatedRumourCard()==rc).findFirst().get();
			this.setSelectedCard(view);
		}
		
		public RenderedCard getSelectedCard() {
			return selectedCard;
		}
		
		public void makeCardsChoosable(RumourCardsPile deck,RumourCardsPile choosableSubpile, NotificationType theme) {
			DeckPanel deckPanel = getDeck(deck);
			if(deckPanel != null) this.currentlyUsedForChoosingCard=deckPanel;
			deckPanel.makeCardsChoosable(choosableSubpile,theme);
		}
		
		public void resetChoosableCardsTheme(RenderedCard exceptedThisOne) {
			if(currentlyUsedForChoosingCard!=null) this.currentlyUsedForChoosingCard.resetChoosableCardsTheme(exceptedThisOne);
		}
		
		
		private class DeckPanel extends JPanel {
			private Dimension cardsSize;
			private String name;
			private RumourCardsPile represents;
			private List<RenderedCard> renderedCardsList = new ArrayList<RenderedCard>();
			private Map<RenderedCard,Component> hMarginsMap = new HashMap<RenderedCard,Component>();
			private final static int marginSize = 12;
			private CardSelectorController controller=null;
			private List<RenderedCard> choosableCards = null;
			
			public DeckPanel(RumourCardsPile rcp) {
				super();
				this.represents = rcp;
				this.setLayout(new BoxLayout(this,BoxLayout.LINE_AXIS));
				this.cardsSize=new Dimension((int)(this.getPreferredSize().getWidth()/6.5),(int)(this.getPreferredSize().getHeight()/1.6));
				if(rcp.getOwner()!=null) {
					this.name=Integer.toString(rcp.getOwner().getId());
				}
				else {
					this.name="Pile";
				}
			}

			@Override
			public Insets getInsets() {
				return new Insets(marginSize,marginSize,marginSize,marginSize);
			}
			
			@Override
			public String toString() {
				return name;
			}
			
			public void resetPane() {
				this.hMarginsMap.clear();
				this.renderedCardsList.removeIf(truth->true);
				this.removeAll();
			}
			
			public void updateContent() {
				if(this.controller!=null)
					this.controller.destroyAllMouseListeners();
				
				this.represents.getCards().forEach(rc->{
					if(!this.renderedCardsList.stream().map(j->j.getAssociatedRumourCard()).toList().contains(rc)) {
						//if card in deck but not rendered, add it to the rendered deck
						this.addCard(rc);
					}
				});
				List<RumourCard> toRemove = new ArrayList<RumourCard>();
				for(RenderedCard j : renderedCardsList) {
					if(!this.represents.contains(j.getAssociatedRumourCard())) {
						//if card in rendered cards list but not in deck, remove it from the rendered deck
						toRemove.add(j.getAssociatedRumourCard());
					}
					else{
						j.update(false);
					}
				}
				toRemove.forEach(rc->this.removeCard(rc));
				this.controller=new CardSelectorController(renderedCardsList, gamePanelInstance);
			}
			
			public void renderPane() {
				resetPane();
				Iterator<RenderedCard> it= renderedCardsList.iterator();
				while(it.hasNext()) {
					RenderedCard j = it.next();
					this.setMaximumSize(cardsSize);
					this.add(j);;
					Component hMargin = Box.createHorizontalStrut(marginSize);
					hMarginsMap.put(j,hMargin);
					if(it.hasNext()) this.add(hMargin);
				}
				this.updateUI();
			}
			
			public RenderedCard addCard(RumourCard rc) {
				RenderedCard j = new RenderedCard(rc);
				//j.setPreferredSize(cardsSize);
				this.renderedCardsList.add(j);
				return j;
			}
			
			public void removeCard(RumourCard rc) {
				RenderedCard jay = this.renderedCardsList.stream().filter(j->j.getAssociatedRumourCard()==rc).findAny().get();
				if(jay!=null) {
					if(hMarginsMap.get(jay)!=null) {
						this.remove(hMarginsMap.get(jay));
						hMarginsMap.remove(jay);
					}
					this.renderedCardsList.remove(jay);
					this.remove(jay);
				}
			}
			
			public void updateCardRevealStatus(RumourCard rc) {
				this.renderedCardsList.stream().filter(j->j.getAssociatedRumourCard()==rc).forEach(j->j.update(false));
			}
			
			public void forceReveal(boolean yes) {
				if(yes&&!forceRevealedDecksList.contains(this)) forceRevealedDecksList.add(this);
				this.renderedCardsList.forEach(j->j.update(yes));
			}
			
			public void makeCardsChoosable(RumourCardsPile choosableSubpile, NotificationType theme) {
				this.choosableCards = renderedCardsList.stream().filter(j->choosableSubpile.contains(j.getAssociatedRumourCard())).toList();
				choosableCards.forEach(j->j.setTheme(theme));
				new CardSelectorController(choosableCards, gamePanelInstance,inputMediator);
			}
			
			public void resetChoosableCardsTheme(RenderedCard exceptedThisOne) {
				List<RenderedCard> toReset = choosableCards.stream().filter(j->j!=exceptedThisOne).toList();
				toReset.forEach(j->{
					j.resetTheme();
					j.unbolden();
				});
				if(exceptedThisOne==null) choosableCards=null;
				else {
					choosableCards = new ArrayList<RenderedCard>();
					choosableCards.add(exceptedThisOne);
				}
				
			}
			
			public List<RenderedCard> getRenderedCardsList () {
				return renderedCardsList;
			}
		}
		

		
	}
	private class BotNotificationsPanel extends GridBagCell {
		private NotificationsBox notificationsBox;// = new TextBox();
		
		public BotNotificationsPanel(int x, int y,int w, int h)  {
			super(x,y,w,h,defaultCellBorder,cellsList);
			this.getPan().setLayout(new BorderLayout());
			this.getPan().setBackground(Color.ORANGE);
			//this.getPan().add(textBox);
		}
		
		@Override
		public void init() {
			notificationsBox = new NotificationsBox();
			notificationsBox.setPreferredSize(getPan().getPreferredSize());
			this.getPan().add(notificationsBox);
		}
		
		public NotificationsBox getTextBox () {
			return notificationsBox;
		}
		public void eraseContent() {
			this.notificationsBox.eraseContent();
		}
		
		public void setTextBox (NotificationsBox tb) {
			this.notificationsBox=tb;
		}
		
		public void appendNotification(Notification n) {
			this.notificationsBox.printNotification(n);
		}
	}
	private class ScorePanel extends GridBagCell{
		public ScorePanel(int x, int y,int w, int h) {
			super(x,y,w,h,defaultCellBorder,cellsList);
			this.getPan().setBackground(Color.PINK);
		}
	}
	
	
	
}
