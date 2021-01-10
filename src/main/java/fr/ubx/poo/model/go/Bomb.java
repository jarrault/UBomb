package fr.ubx.poo.model.go;

import fr.ubx.poo.game.Direction;
import fr.ubx.poo.game.Game;
import fr.ubx.poo.game.Position;
import fr.ubx.poo.model.decor.Decor;
import fr.ubx.poo.model.go.character.Monster;
import javafx.geometry.Pos;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import static java.lang.Thread.sleep;

public class Bomb extends GameObject {

    private int countdown = 0;
    private int bombRange;
    private boolean isExplode;
    private boolean isDisplayed;
    private long creationDate;
    private long livingTime = 3;

    public Bomb(Game game, Position position, long creationDate, int bombRange) {
        super(game, position);

        this.bombRange = bombRange;
        this.creationDate = TimeUnit.SECONDS.convert(creationDate, TimeUnit.NANOSECONDS);// / 1__000__000__000;
//        this.livingTime = TimeUnit.SECONDS.convert(4);// / 1__000__000__000;

        this.isExplode = false;
        this.isDisplayed = false;

//        launchTask();
    }

    long timeStamp = 0;

    public void update(long now) {
        long convert = TimeUnit.SECONDS.convert(now, TimeUnit.NANOSECONDS);// / 1__000__000__000;

        if (convert > timeStamp) { //TODO I don't know if it's a good idea to do it like that
//        if(convert > (this.creationDate + this.livingTime)){
            timeStamp = convert;

            if (this.countdown == this.livingTime) {
                timerEnds();
            } else {
                this.countdown++;
            }

        }
    }

    private void checkIfInflictDamageToCharacter(Position position) {
        //TODO complet this method

        //for Player
        if (this.game.getPlayer().getPosition().equals(position)) {
            this.game.inflictDamageToPlayer(1); //be correct when merge with other branchs don't worry
        }

        //for Monster
        if (!this.game.getMonsters().isEmpty()) {
            for (Monster monster : this.game.getMonsters()) {
                if (monster.getPosition().equals(position)) {
                    this.game.inflictDamageToMonster(monster, 1);
                }
            }
        }
    }

    private void timerEnds() {
        bombExplodes();

        //"Lorsque une bombe explose, une nouvelle bombe est ajoutée à l’inventaire du joueur."
        this.game.getPlayer().incrementBombNumber();
    }

    private void bombExplodes() {
        //to make an explosion a the bomb's position
        makeExplosion(this.getPosition());

        //loop to scan and process the explosion cross
        for (Direction direction : Direction.values()) {
            checkExplosionDirection(direction);
        }

        this.isExplode = true;

//        this.game.getPlayer().removeBomb(this);
    }

    private void checkExplosionDirection(Direction direction) {
        Position pos = getPosition();
        boolean isExplosionObstacled = false;
        boolean willExploded = true;

        for (int range = 1; range <= this.bombRange; range++) {
            pos = direction.nextPosition(pos);

            if (this.world.isInside(pos)) {
                Decor decor = this.world.get(pos);

                if (isExplosionObstacled) {
//                    makeExplosion(pos);
                    willExploded = false;
                }

                if (decor != null) {
                    if (decor.isDestructible() && !isExplosionObstacled) {
                        if (!decor.isTraversable()) { //it work for Box and other decor which "stop" explosion ?
                            isExplosionObstacled = true;
                        }

                        //TODO I think it's here to begin Explosion Object creation

                        //to destroy the entity
                        this.world.clear(pos);

                        makeExplosion(pos);
                        this.checkIfInflictDamageToCharacter(pos);

                    } else if (!decor.isDestructible()) {
                        isExplosionObstacled = true;
                    }
                } else if ((decor == null) && (!isExplosionObstacled)) {
                    makeExplosion(pos);
                    this.checkIfInflictDamageToCharacter(pos);
                }

            }
        }
    }

    private void makeExplosion(Position pos) {
        this.world.set(pos, new Explosion());

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            public void run() {
                try {
                    Thread.sleep(200);
                    world.clear(pos);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, new Date());
    }

    public int getCountdown() {
        return countdown;
    }

    public boolean isExplode() {
        return isExplode;
    }

    public boolean isDisplayed() {
        return isDisplayed;
    }

    public void setDisplayed(boolean displayed) {
        isDisplayed = displayed;
    }
}
