package fr.sos.witchhunt.model.cards;

import java.awt.image.BufferedImage;
import java.net.URL;
import java.nio.file.Paths;

import javax.imageio.ImageIO;

import fr.sos.witchhunt.model.players.cpustrategies.CardValue;
public abstract class RumourCard extends Card {
	
	protected Effect witchEffect;
	protected Effect huntEffect;
	protected String additionnalEffectDescription="";
	protected int defaultAdditionnalValue=0;
	protected boolean givesCards=false;
	protected boolean isRisked=false;
	protected boolean isOffensive=false;
	
	protected BufferedImage image;
	
	public RumourCard() {
		this.loadImage();
	}
	
	public RumourCard(String additionnalEffectDescription,int defaultAdditionnalValue) {
		this();
		this.additionnalEffectDescription=additionnalEffectDescription;
		this.defaultAdditionnalValue=defaultAdditionnalValue;
	}
	
	//public void discard(){}
	public boolean witch() {
		if(this.witchEffect.isAllowed()) {
			this.witchEffect.perform();
			this.reveal();
			return true;
		}
		else {
			return false;
		}
	};
	public boolean hunt() {
		if(this.huntEffect.isAllowed()) {
			this.reveal();
			this.huntEffect.perform();
			return true;
		}
		else {
			return false;
		}
	}
	public CardValue getDefaultValue() {
		CardValue cv;
		if(this.defaultAdditionnalValue<=0) {
			cv= new CardValue(witchEffect.getValue(),huntEffect.getValue());
		}
		else {
			cv= new CardValue(witchEffect.getValue(),huntEffect.getValue(),this.defaultAdditionnalValue);
		}
		if(this.isOffensive) cv.setOffensive(true);
		if(this.isRisked) cv.setRisked(true);
		if(this.givesCards) cv.setGivesCards(true);
		return cv;
	};
	
	public boolean grantsImmunityAgainst(RumourCard rc) {
		return false;
	}
	
	public boolean canWitch() {
		return (this.witchEffect.isAllowed() && !this.revealed);
	}
	
	public boolean canHunt() {
		return (this.huntEffect.isAllowed() && !this.revealed);
	}
	
	public String getName() {
		//converts CardName into Card Name automatically
		String cardClassName = this.getClass().getSimpleName();
		StringBuffer sb = new StringBuffer();
		for (int i=0; i<cardClassName.length(); i++) {
			if (i!=0) {
				if(Character.isUpperCase(cardClassName.charAt(i))) {
					sb.append(' ');
				}
			}
			sb.append(cardClassName.charAt(i));
		}
		return sb.toString();
	}

	public String getWitchEffectDescription() {
		return this.witchEffect.getDescription();
	}
	
	public String getHuntEffectDescription() {
		return this.huntEffect.getDescription();
	}
	
	public String getAdditionnalEffectDescription() {
		return this.additionnalEffectDescription;
	}

	private final void loadImage() {
		URL resource = getImageURL();
		try {
			this.image=Card.resizeCardImage(ImageIO.read(Paths.get(resource.toURI()).toFile()));
		}
		catch(Exception e) {
			System.err.println("Could not load resource : "+resource.toString());
			System.out.println("Aborting...");
			System.exit(-1);
		}
	}
	
	private final URL getImageURL() {
		String className = this.getClass().getSimpleName();
		return this.getClass().getResource("/images/cards/"+className+".png");
	}
	
	public BufferedImage getImage() {
		return this.image;
	}

}
