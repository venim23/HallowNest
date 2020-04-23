package Hallownest.monsters.KingdomsEdgeEnemies;

import Hallownest.BetterSpriterAnimation;
import Hallownest.HallownestMod;
import Hallownest.powers.powerThreatened;
import Hallownest.relics.DreamNailRelic;
import Hallownest.util.SoundEffects;
import basemod.abstracts.CustomMonster;
import com.brashmonkey.spriter.Animation;
import com.brashmonkey.spriter.Player;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.TalkAction;
import com.megacrit.cardcrawl.actions.common.*;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.powers.FrailPower;
import com.megacrit.cardcrawl.powers.VulnerablePower;

public class monsterShieldedFool extends CustomMonster
{
    public static final String ID = HallownestMod.makeID("monsterShieldedFool");
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterShieldedFool.monsterStrings.NAME;
    public static final String[] MOVES = monsterShieldedFool.monsterStrings.MOVES;
    public static final String[] DIALOG = monsterShieldedFool.monsterStrings.DIALOG;

    private static final byte THREE_MOVE = 0;
    private static final byte SLASH_MOVE = 1;
    private static final byte BLOCK_MOVE = 2;






    //Values
    private int  Three_DMG = 5;
    private int  Three_HITS = 3;
    private int  Slash_DMG = 13;
    private int  Slash_VAL = 1;
    private int  Block_BLOCK = 18;





    //Max and Min HP Values, used with SetHP to generate a random hp variable for the fight

    private int minHP = 66;
    private int maxHP = 70;

    //Custom Variables for the backend calculations like timing of moves, when to trigger something etc.
    private int numTurns = 0;

    //Name of Anims (so you can close program if necessary)
    private String IdleAnim = "Idle";
    private String BlockAnim = "Block";
    private String SlashAnim = "Slash";
    private String ThreeSlashAnim = "ThreeSlash";
    private String HitAnim = "Hit";



    public monsterShieldedFool() {
        this(0.0f, 0.0F);
    }

    public monsterShieldedFool(float x, float y) {
        super(monsterShieldedFool.NAME, ID, 130, 0, 0, 150.0f, 300.0f, null, x, y);
        this.animation = new BetterSpriterAnimation("HallownestResources/images/monsters/KingdomsEdge/ShieldedFool/ShieldedFool.scml");
        ((BetterSpriterAnimation)this.animation).myPlayer.scale(1.00f);
        this.type = EnemyType.NORMAL;
        //this.dialogX = (this.hb_x - 70.0F) * Settings.scale;
        //this.dialogY -= (this.hb_y - 55.0F) * Settings.scale;

        if (AbstractDungeon.ascensionLevel >= 7)
        {
            //For monsters encountered at higher ascension levels adds a bit more HP
            this.minHP += 6;
            this.maxHP += 6;

        }

        if (AbstractDungeon.ascensionLevel >= 2)
        {
            //for Ascenction 3 and higher, adds a bit more damage
            this.Three_DMG+=1;
            this.Slash_DMG+=2;
        }

        if (AbstractDungeon.ascensionLevel >= 17)
        {
            this.Block_BLOCK+=7;

        }

        setHp(this.minHP,this.maxHP);

        
        Player.PlayerListener listener = new AnimationListener(this);
        ((BetterSpriterAnimation)this.animation).myPlayer.addListener(listener);

        this.damage.add(new DamageInfo(this, this.Three_DMG)); // attack 0 damage
        this.damage.add(new DamageInfo(this, this.Slash_DMG)); // attack 0 damage



    }

    @Override
    public void usePreBattleAction() {
        if (AbstractDungeon.player.hasRelic(DreamNailRelic.ID)){
            int nailchance = AbstractDungeon.miscRng.random(0,99);
            if (nailchance < 25) {
                AbstractDungeon.actionManager.addToBottom(new RelicAboveCreatureAction(AbstractDungeon.player, AbstractDungeon.player.getRelic(DreamNailRelic.ID)));
                int dialogoptions = DIALOG.length;
                int random = AbstractDungeon.miscRng.random(0, dialogoptions - 1);
                this.dialogX = (this.hb_x) * Settings.scale;
                this.dialogY = (this.hb_y) * Settings.scale;
                AbstractDungeon.actionManager.addToBottom(new TalkAction(this, DIALOG[random], 2.0f, 2.0F));
            }
        }


    }

    
    @Override
    public void takeTurn() {
        AbstractPlayer p = AbstractDungeon.player;
        //Trigger the Spew Summon Action if the timer lines up even before starting the switch case.


        switch (this.nextMove) {
            case THREE_MOVE:{
                runAnim(ThreeSlashAnim);

                for (int i = 0; i < this.Three_HITS; ++i) {
                    CardCrawlGame.sound.playV(SoundEffects.GenSword.getKey(),1.3F);
                    AbstractDungeon.actionManager.addToBottom(new DamageAction(p, this.damage.get(0), AbstractGameAction.AttackEffect.SLASH_DIAGONAL));
                }
                break;
            }
            case SLASH_MOVE:{
                runAnim(SlashAnim);
                AbstractDungeon.actionManager.addToBottom(new DamageAction(p, this.damage.get(1), AbstractGameAction.AttackEffect.SLASH_DIAGONAL));
                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(p,this, new FrailPower(p, this.Slash_VAL, true),this.Slash_VAL));
                //AbstractDungeon.actionManager.addToBottom(new SFXVAction(SoundEffects.BeeBuff.getKey()));

                break;
            }
            case BLOCK_MOVE:{
                runAnim(BlockAnim);
                CardCrawlGame.sound.playV(SoundEffects.GenericShield.getKey(),1.3F);
                AbstractDungeon.actionManager.addToBottom(new GainBlockAction(this, this.Block_BLOCK));
                //make him cry now please
                //AbstractDungeon.actionManager.addToBottom(new SFXVAction(SoundEffects.JellySmall.getKey()));
                break;
            }
        }
        AbstractDungeon.actionManager.addToBottom(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        this.numTurns++;
        if ((!this.lastMove(BLOCK_MOVE)) || ((numTurns == 1))) {
            this.setMove(BLOCK_MOVE, Intent.DEFEND);
            return;
        }

        if (num < 50){
            this.setMove(THREE_MOVE, Intent.ATTACK, ((DamageInfo) this.damage.get(0)).base, this.Three_HITS, true);
        } else {
            this.setMove(SLASH_MOVE, Intent.ATTACK_DEBUFF, ((DamageInfo) this.damage.get(1)).base);
        }
    }

    public void damage(DamageInfo info)
    {
        super.damage(info);
        if ((info.owner != null) && (info.type != DamageInfo.DamageType.THORNS) && (info.output > 0))
        {
            runAnim(HitAnim);

        }
    }

    @Override
    public void die() {
        this.stopAnimation();
        useShakeAnimation(1.0F);
        //runAnim("Defeat");
        super.die();
    }

    //Runs a specific animation
    public void runAnim(String animation) {
        ((BetterSpriterAnimation)this.animation).myPlayer.setAnimation(animation);
    }

    //Resets character back to idle animation
    public void resetAnimation() {
        ((BetterSpriterAnimation)this.animation).myPlayer.setAnimation(IdleAnim);
    }

    //Prevents any further animation once the death animation is finished
    public void stopAnimation() {
        int time = ((BetterSpriterAnimation)this.animation).myPlayer.getAnimation().length;
        ((BetterSpriterAnimation)this.animation).myPlayer.setTime(time);
        ((BetterSpriterAnimation)this.animation).myPlayer.speed = 0;
    }

    public class AnimationListener implements Player.PlayerListener {

        private monsterShieldedFool character;

        public AnimationListener(monsterShieldedFool character) {
            this.character = character;
        }

        public void animationFinished(Animation animation){

            if (!animation.name.equals(IdleAnim)) {
                character.resetAnimation();
            }



        }

        //UNUSED
        public void animationChanged(Animation var1, Animation var2){

        }

        //UNUSED
        public void preProcess(Player var1){

        }

        //UNUSED
        public void postProcess(Player var1){

        }

        //UNUSED
        public void mainlineKeyChanged(com.brashmonkey.spriter.Mainline.Key var1, com.brashmonkey.spriter.Mainline.Key var2){

        }
    }
}