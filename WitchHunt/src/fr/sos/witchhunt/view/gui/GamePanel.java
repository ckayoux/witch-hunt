package fr.sos.witchhunt.view.gui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import fr.sos.witchhunt.InputMediator;
import fr.sos.witchhunt.controller.DeckSelectorButtonController;
import fr.sos.witchhunt.model.Menu;
import fr.sos.witchhunt.model.cards.Card;
import fr.sos.witchhunt.model.cards.RumourCard;
import fr.sos.witchhunt.model.cards.RumourCardsPile;
import fr.sos.witchhunt.model.players.Player;  

public class GamePanel extends GridBagPanel {
	
	private InputMediator inputMediator;
	
	private TopNotificationsPanel topNotificationsPanel;
	private ActionsPanel actionsPanel;
	private DeckSelectorPanel deckSelectorPanel;
	private CardsPanel cardsPanel;
	private BotNotificationsPanel botNotificationsPanel;
	private ScorePanel scorePanel;
	
	
	
	public GamePanel() {
		super(10,10);
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

	
	public void makeChoice(Menu m) {
		actionsPanel.makeChoice(m, inputMediator);
	}
	
	public void setActionsPanelThemeAsForActionType(Object o) {
		actionsPanel.setThemeAsForActionType(o);
	}

	
	public void resetActionPanel() {
		if(actionsPanel.isRendered()) actionsPanel.resetPane();
	}
	
	
	
	
	
	public void renderPlayers(List<Player> pList) {
		pList.forEach(p->{
			new DeckSelectorButtonController(
					this.deckSelectorPanel.addPlayerButton(p),
					this
			);
		});
		this.deckSelectorPanel.renderPlayers();
	}
	
	public void renderPile(RumourCardsPile pile) {
		new DeckSelectorButtonController(this.deckSelectorPanel.makePileButton(pile),this);
		this.deckSelectorPanel.renderPile();
	}
	
	public void updateScoreDisplay(Player p) {
		this.deckSelectorPanel.updateScoreDisplay(p);
	}
	
	public void updatePileContent() {
		this.deckSelectorPanel.updatePileButton();
	}
	
	public void updatePlayerActivityStatus(Player p) {
		this.deckSelectorPanel.updateButtonByPlayer(p);
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

	public void showCurrentPlayer(Player currentPlayer) {
		deckSelectorPanel.themeUpPlayerButton(currentPlayer,NotificationType.TURN);
	}
	
	public void boldenPlayer(Player p) {
		deckSelectorPanel.boldenPlayerButton(p);
	}
	
	public void unBoldenPlayer(Player p) {
		deckSelectorPanel.unBoldenPlayerButton(p);
	}
	
	public void resetPlayersEffects() {
		deckSelectorPanel.resetPlayersEffects();
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
		}
	

	/*	public void resetAccusablePlayersEffects() {
			//TODO
		}*/

		public void themeUpPlayerButton(Player p, NotificationType nt) {
			this.playersBList.stream().filter(b->b.getAssociatedPlayer()==p).forEach(b->b.makeTheme(nt));
		}
		
		public void boldenPlayerButton(Player p) {
			this.playersBList.stream().filter(b->b.getAssociatedPlayer()==p).forEach(b->b.bolden());
		}
		
		public void unBoldenPlayerButton(Player p) {
			this.playersBList.stream().filter(b->b.getAssociatedPlayer()==p).forEach(b->b.unBolden());
		}
			
		
		public void setAccusablePlayers() {
			// TODO Auto-generated method stub
			
		}
		
		public void setSelectedDeckButton(DeckSelectorButton b) {
			if(this.selectedDeckButton!=null) {
				selectedDeckButton.unBolden();
			}
			
			if(b!=null) {
				b.bolden();
			}
			else {
				if(this.selectedDeckButton!=null) this.selectedDeckButton.unBolden();
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
		
		public CardsPanel(int x, int y,int w, int h) {
			super(x,y,w,h,defaultCellBorder,cellsList);
			this.getPan().setBackground(Color.YELLOW);
		}
		
		@Override
		public void init() {
			this.getPan().setPreferredSize(this.getPan().getPreferredSize());
			this.getPan().setLayout(cl);
		}
		
		public void addDeck(RumourCardsPile rcp) {
			M.put(rcp, new DeckPanel(rcp));
		}
		
		public DeckPanel getDeck(RumourCardsPile rcp) {
			return M.get(rcp);
		}
		
		public void showDeck(RumourCardsPile rcp) {
			cl.show(this.getPan(),getDeck(rcp).toString());
		}
		public void showDeck(Player p) {
			this.showDeck(p.getHand());
		}
		
		private class DeckPanel extends JPanel {
			private String name;
			private List<RenderedCard> renderedCardsList = new ArrayList<RenderedCard>();
			
			public DeckPanel(RumourCardsPile rcp) {
				super();
				this.setLayout(new BoxLayout(this,BoxLayout.LINE_AXIS));
				rcp.getCards().forEach(c->this.add(new JButton(c.getName())));
				if(rcp.getOwner()!=null) {
					this.name=Integer.toString(rcp.getOwner().getId());
				}
				else {
					this.name="Pile";
				}
				//rcp.getCards().forEach(rc->this.addCard(rc));
			}
			
			@Override
			public String toString() {
				return name;
			}
			
			public void resetPane() {
				this.removeAll();
			}
			
			public void renderPane() {
				resetPane();
				this.renderedCardsList.forEach(j->this.add(j));
			}
			
			public void addCard(RumourCard rc) {
				this.renderedCardsList.add(new RenderedCard(rc));
			}
			
			public void removeCard(RumourCard rc) {
				RenderedCard jay = this.renderedCardsList.stream().filter(j->j.getAssociatedRumourCard()==rc).findAny().get();
				if(jay!=null) {
					this.renderedCardsList.remove(jay);
					this.remove(jay);
				}
			}
			
			public void updateCard(RumourCard rc) {
				this.renderedCardsList.stream().filter(j->j.getAssociatedRumourCard()==rc).forEach(j->j.update());
			}
		}
		
		private class RenderedCard extends JLabel {
			private RumourCard represents=null;
			private boolean revealed = false;
			private final static ImageIcon unrevealedRenderedCard = new ImageIcon(Card.getUnrevealedCardImage());
			private static Map<RumourCard,ImageIcon> renderedCardIconsMap = new HashMap<RumourCard,ImageIcon>();
			
			public RenderedCard(RumourCard rc) {
				super();
				this.represents=rc;
				this.update();
			}
			
			public void update() {
				if(this.represents!=null) {
					if(this.represents.isRevealed()&&!revealed) {
						ImageIcon alreadyRendered = renderedCardIconsMap.get(this.represents);
						if(alreadyRendered!=null) {
							this.setIcon(alreadyRendered);
						}
						else {
							ImageIcon i = new ImageIcon(this.represents.getImage());
							renderedCardIconsMap.put(this.represents, i);
							this.setIcon(i);
						}
					}
					else if (!this.represents.isRevealed()&&revealed) {
						this.setIcon(unrevealedRenderedCard);
					}
				}
			}
			
			public RumourCard getAssociatedRumourCard() {
				return this.represents;
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
