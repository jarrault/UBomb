package fr.ubx.poo.model.go;

import fr.ubx.poo.game.Direction;
import fr.ubx.poo.game.Game;
import fr.ubx.poo.game.Position;
import fr.ubx.poo.game.World;
import fr.ubx.poo.model.decor.Decor;
import fr.ubx.poo.model.decor.Door;
import fr.ubx.poo.model.decor.Explosion;
import fr.ubx.poo.model.go.character.Monster;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class Bomb extends GameObject {

    private int countdown = 0;
    private final int bombRange;
    private boolean isExplode;
    private boolean isDisplayed;

    private World world;

    public Bomb(Game game, Position position, int bombRange) {
        super(game, position);

        this.bombRange = bombRange;
        this.isExplode = false;
        this.isDisplayed = false;
        this.world = game.getWorld();
    }

    long timeStamp = 0;

    public void update(long now) {
        long convert = TimeUnit.SECONDS.convert(now, TimeUnit.NANOSECONDS);// / 1__000__000__000;

        if (convert > timeStamp) {
            timeStamp = convert;

            long livingTime = 3;
            if (this.countdown == livingTime) {
                timerEnds();
            } else {
                this.countdown++;
            }
        }
    }

    /**
     * Check if the bomb is at the same position of the character or a monster
     *
     * @param position position of the bomb
     */
    private void checkIfInflictDamageToCharacter(Position position) {
        // For Player
        if (this.game.getPlayer().getPosition().equals(position)) {
            this.game.inflictDamageToPlayer(1);
        }

        // For Monster
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

        this.game.getPlayer().incrementBombNumber();
    }

    /**
     * Manage the explosion of the bomb in each directions
     */
    private void bombExplodes() {
        // To make an explosion at the bomb's position
        Position pos = this.getPosition();
        Decor decor = this.world.get(pos);
        if (decor != null) {
            if (decor.isDestructible()) {
                // To destroy the entity
                this.world.clear(pos);
                makeExplosionAnimation(pos);
            }
        } else {
            makeExplosionAnimation(pos);
        }
        this.checkIfInflictDamageToCharacter(pos);

        // Loop to scan and process the explosion cross
        for (Direction direction : Direction.values()) {
            checkExplosionDirection(direction);
        }

        this.isExplode = true;
    }

    /**
     * Remove the decor if the explosion pass throw it and stop the explosion when an obstacle is in is way (except for bonus)
     *
     * @param direction direction of the explosion
     */
    private void checkExplosionDirection(Direction direction) {
        Position pos = getPosition();
        boolean isExplosionObstructed = false;

        for (int range = 1; range <= this.bombRange; range++) {
            pos = direction.nextPosition(pos);

            if (this.world.isInside(pos)) {
                Decor decor = this.world.get(pos);

                if (decor != null) {
                    if (decor.isDestructible() && !isExplosionObstructed) {
                        if (!decor.isTraversable()) {
                            isExplosionObstructed = true;
                        }

                        // To destroy the entity
                        this.world.clear(pos);
                        makeExplosionAnimation(pos);

                        this.checkIfInflictDamageToCharacter(pos);
                    } else if (!decor.isDestructible()) {
                        isExplosionObstructed = true;
                    }
                } else if (!isExplosionObstructed) {
                    makeExplosionAnimation(pos);
                    this.checkIfInflictDamageToCharacter(pos);
                }
            }
        }
    }

    /**
     * Make the animation of the explosion
     *
     * @param pos position where the explosion animation need to be
     */
    private void makeExplosionAnimation(Position pos) {
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
