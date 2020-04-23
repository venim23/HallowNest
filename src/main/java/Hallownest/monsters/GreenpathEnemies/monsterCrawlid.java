package Hallownest.monsters.GreenpathEnemies;

import Hallownest.BetterSpriterAnimation;
import Hallownest.HallownestMod;
import Hallownest.actions.SFXVAction;
import Hallownest.cards.status.Swarmed;
import Hallownest.util.SoundEffects;
import basemod.abstracts.CustomMonster;
import com.brashmonkey.spriter.Animation;
import com.brashmonkey.spriter.Player;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.*;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.powers.ThornsPower;
import com.megacrit.cardcrawl.powers.WeakPower;

public class monsterCrawlid extends CustomMonster
{
    public static final String ID = HallownestMod.makeID("monsterCrawlid");
    private static final MonsterStrings monsterStrings;
    public static final String NAME;
    public static final String[] MOVES;
    public static final String[] DIALOG;

    private static final byte EXIST_MOVE = 0;
    private static final byte MOVE_MOVE = 1;




    //Hornet Values
    private int  Walk_DMG = AbstractDungeon.monsterHpRng.random(4, 6);
    private int  Walk_VAL = 1;
    private int  Swarm_VAL = 1;
    private int  StartingThorns = 1;





    //Max and Min HP Values, used with SetHP to generate a random hp variable for the fight
    private int maxHP = 16;
    private int minHP = 13;


    //Custom Variables for the backend calculations like timing of moves, when to trigger something etc.
    private int numTurns = 0;
    private int chargeCount = 0;

    //Name of Anims (so you can close program if necessary)
    private String IdleAnim = "Idle";
    private String ExistAnim = "Vibe";
    private String MoveAnim = "Walk";
    private String HitAnim = "Hit";

    public monsterCrawlid() {
        this(0.0f, 0.0f);
    }

    public monsterCrawlid(float x) {
        this(x, 0.0f);
    }

    public monsterCrawlid(float x, float y) {
        super(monsterCrawlid.NAME, ID, 15, 0, 0, 90.0f, 110.0f, null, x, y);
        this.animation = new BetterSpriterAnimation("HallownestResources/images/monsters/Greenpath/crawlids/Crawlid.scml");
        this.type = EnemyType.NORMAL;
        //this.dialogX = (this.hb_x - 70.0F) * Settings.scale;
        //this.dialogY -= (this.hb_y - 55.0F) * Settings.scale;

        if (AbstractDungeon.ascensionLevel >= 7)
        {
            //For monsters encountered at higher ascension levels adds a bit more HP
            this.minHP += 2;
            this.maxHP += 2;

        }

        if (AbstractDungeon.ascensionLevel >= 2)
        {
            //for Ascenction 3 and higher, adds a bit more damage
            this.Walk_DMG = AbstractDungeon.monsterHpRng.random(5, 7);
        }

        if (AbstractDungeon.ascensionLevel >= 17)
        {
            this.StartingThorns += 1;
        }

        setHp(this.minHP,this.maxHP);

        
        Player.PlayerListener listener = new AnimationListener(this);
        ((BetterSpriterAnimation)this.animation).myPlayer.addListener(listener);

        this.damage.add(new DamageInfo(this, this.Walk_DMG)); // attack 0 damage
        
    }

    @Override
    public void usePreBattleAction() {
        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this, this, new ThornsPower(this, this.StartingThorns)));

    }

    
    @Override
    public void takeTurn() {
        AbstractPlayer p = AbstractDungeon.player;

        //Trigger the Spew Summon Action if the timer lines up even before starting the switch case.


        switch (this.nextMove) {
            case EXIST_MOVE:{
                runAnim(ExistAnim);
                CardCrawlGame.sound.playV(SoundEffects.CrawlerAct.getKey(),1.4F);
                AbstractDungeon.actionManager.addToBottom(new MakeTempCardInHandAction(new Swarmed(),1));
                break;
            }
            case MOVE_MOVE:{
                runAnim(MoveAnim);
                AbstractDungeon.actionManager.addToBottom(new SFXVAction(SoundEffects.CrawlerAttack.getKey()));
                AbstractDungeon.actionManager.addToBottom(new DamageAction(p, this.damage.get(0), AbstractGameAction.AttackEffect.BLUNT_LIGHT));
                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(p, this, new WeakPower(p, this.Walk_VAL, true),this.Walk_VAL));

                break;
            }

        }
        AbstractDungeon.actionManager.addToBottom(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        this.numTurns++;
        if (((num < 30) && (!lastMove(EXIST_MOVE))) || this.numTurns == 1){
            this.setMove(EXIST_MOVE, Intent.STRONG_DEBUFF);
        } else {
            this.setMove(MOVE_MOVE, Intent.ATTACK_DEBUFF, ((DamageInfo) this.damage.get(0)).base);
        }
    }

    
    static {
        monsterStrings = CardCrawlGame.languagePack.getMonsterStrings("Hallownest:monsterCrawlid");
        NAME = monsterCrawlid.monsterStrings.NAME;
        MOVES = monsterCrawlid.monsterStrings.MOVES;
        DIALOG = monsterCrawlid.monsterStrings.DIALOG;
    }

    public void damage(DamageInfo info)
    {
        super.damage(info);
        //just checks to make sure the attack came from the plaer basically.
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

        private monsterCrawlid character;

        public AnimationListener(monsterCrawlid character) {
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