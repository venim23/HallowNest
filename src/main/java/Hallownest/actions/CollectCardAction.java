package Hallownest.actions;

import Hallownest.powers.powerCollectedCard;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.utility.ShowCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.StasisPower;

public class CollectCardAction extends AbstractGameAction {
    private AbstractCreature owner;
    private float startingDuration;
    private AbstractCard card = null;
    private int Hits;

    public CollectCardAction(AbstractCreature owner, int ReturnHits) {
        this.Hits = ReturnHits;
        this.owner = owner;
        this.duration = Settings.ACTION_DUR_LONG;
        this.startingDuration = Settings.ACTION_DUR_LONG;
        this.actionType = ActionType.WAIT;
    }

    public void update() {
        if (AbstractDungeon.player.drawPile.isEmpty() && AbstractDungeon.player.discardPile.isEmpty()) {
            this.isDone = true;
        } else {
            if (this.duration == this.startingDuration) {
                if (AbstractDungeon.player.drawPile.isEmpty()) {
                    this.card = AbstractDungeon.player.discardPile.getRandomCard(AbstractDungeon.cardRandomRng, AbstractCard.CardRarity.RARE);
                    if (this.card == null) {
                        this.card = AbstractDungeon.player.discardPile.getRandomCard(AbstractDungeon.cardRandomRng, AbstractCard.CardRarity.UNCOMMON);
                        if (this.card == null) {
                            this.card = AbstractDungeon.player.discardPile.getRandomCard(AbstractDungeon.cardRandomRng, AbstractCard.CardRarity.COMMON);
                            if (this.card == null) {
                                this.card = AbstractDungeon.player.discardPile.getRandomCard(AbstractDungeon.cardRandomRng);
                            }
                        }
                    }

                    AbstractDungeon.player.discardPile.removeCard(this.card);
                } else {
                    this.card = AbstractDungeon.player.drawPile.getRandomCard(AbstractDungeon.cardRandomRng, AbstractCard.CardRarity.RARE);
                    if (this.card == null) {
                        this.card = AbstractDungeon.player.drawPile.getRandomCard(AbstractDungeon.cardRandomRng, AbstractCard.CardRarity.UNCOMMON);
                        if (this.card == null) {
                            this.card = AbstractDungeon.player.drawPile.getRandomCard(AbstractDungeon.cardRandomRng, AbstractCard.CardRarity.COMMON);
                            if (this.card == null) {
                                this.card = AbstractDungeon.player.drawPile.getRandomCard(AbstractDungeon.cardRandomRng);
                            }
                        }
                    }

                    AbstractDungeon.player.drawPile.removeCard(this.card);
                }

                AbstractDungeon.player.limbo.addToBottom(this.card);
                this.card.setAngle(0.0F);
                this.card.targetDrawScale = 0.75F;
                this.card.target_x = (float)Settings.WIDTH / 2.0F;
                this.card.target_y = (float)Settings.HEIGHT / 2.0F;
                this.card.lighten(false);
                this.card.unfadeOut();
                this.card.unhover();
                this.card.untip();
                this.card.stopGlowing();
            }

            this.tickDuration();
            if (this.isDone && this.card != null) {
                this.addToTop(new ApplyPowerAction(this.owner, this.owner, new powerCollectedCard(this.owner, this.card, this.Hits )));
                this.addToTop(new ShowCardAction(this.card));
            }

        }
    }
}
