package Hallownest.powers;

import Hallownest.HallownestMod;
import Hallownest.util.SoundEffects;
import Hallownest.util.TextureLoader;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.StrengthPower;

import static Hallownest.HallownestMod.makePowerPath;


public class powerHKParry extends AbstractPower {

    public static final String POWER_ID = HallownestMod.makeID("powerHKParry");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    private static final Texture tex84 = TextureLoader.getTexture(makePowerPath("powerHKParry84.png"));
    private static final Texture tex32 = TextureLoader.getTexture(makePowerPath("powerHKParry32.png"));

    public powerHKParry(AbstractCreature owner) {
        name = NAME;
        ID = POWER_ID;

        this.owner = owner;
        this.amount = 0;

        type = PowerType.BUFF;

        this.region128 = new TextureAtlas.AtlasRegion(tex84, 0, 0, 84, 84);
        this.region48 = new TextureAtlas.AtlasRegion(tex32, 0, 0, 32, 32);

        updateDescription();
    }


    @Override
    public int onAttacked(DamageInfo info, int damageAmount) {
        if (info.owner != null && info.owner != this.owner && damageAmount >0) {
            this.flash();
            this.amount ++;
            updateDescription();
        }

        return ((damageAmount/2));
    }



    public void updateDescription() {
            description = DESCRIPTIONS[0];
    }
}
