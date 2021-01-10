/*
 * Copyright (c) 2020. Laurent Réveillère
 */

package fr.ubx.poo.model.go.character;

import fr.ubx.poo.game.Direction;
import fr.ubx.poo.game.Position;
import fr.ubx.poo.model.decor.*;
import fr.ubx.poo.model.go.Bomb;
import fr.ubx.poo.model.decor.Box;
import fr.ubx.poo.model.decor.Decor;
import fr.ubx.poo.model.decor.Princess;
import fr.ubx.poo.game.Game;
import fr.ubx.poo.model.decor.bonus.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Player extends Character {

    private boolean moveRequested = false;

    private int keys = 0;
    private boolean winner;

    private int numberOfBombs = 1;
    private boolean bombRequested = false;
    private int bombsRange = 1;
    private final List<Bomb> bombs;

    private int countdown = 0;
    private boolean isInvincible = false;

    public Player(Game game, Position position) {
        super(game, position);
        this.lives = game.getInitPlayerLives();
        this.bombs = new ArrayList<>();
    }

    public void requestMove(Direction direction) {
        if (direction != this.direction) {
            this.direction = direction;
        }
        moveRequested = true;
    }

    public void requestBomb() {
        bombRequested = true;
    }

    /**
     * To try open a door when ENTER input
     */
    public void requestOpenDoor() {
        Position myPos = this.getPosition();
        Decor decor = this.game.getWorld().get(myPos);

        if (decor instanceof Door) {//TODO verify if there is a better way to check it
            Door door = (Door) decor;
            if (!door.isOpen() && keys >= 1) {//open the door only if the player have keys
                this.game.getWorld().openDoor(door); // it's to verify if the door correctly open (without checking the keys)
                this.keys--;


                this.moveOnSpecialDecor();


            }
        }
    }

    @Override
    public boolean canMove(Direction direction) {
        Position nextPos = direction.nextPosition(getPosition());
        Decor decor = this.game.getWorld().get(nextPos);

        if (decor instanceof Box) {
            return canMoveBox(direction, nextPos, decor);
        }

        return nextPositionInWorldAndEmpty(nextPos);
    }

    /**
     * Check if the box can be moved and move it if so
     *
     * @param direction current direction of the player
     * @param nextPos next position from the direction of the player
     * @param decor box to move
     *
     * @return true if the box can be moved, false otherwise
     */
    private boolean canMoveBox(Direction direction, Position nextPos, Decor decor) {
        Position newPosition = direction.nextPosition(nextPos);

        if (!this.game.getWorld().isInside(newPosition)) {
            return false;
        }

        if (!this.game.getWorld().isEmpty(newPosition)) {
            return false;
        }

        for (Monster monster : game.getMonsters()) {
            if (monster.getPosition().equals(newPosition)) {
                return false;
            }
        }

        this.game.getWorld().clear(nextPos);
        this.game.getWorld().set(newPosition, decor);

        return true;
    }

    /**
     * @return true if the player can put a bomb, false otherwise
     */
    public boolean canPutBomb() {
        if (this.numberOfBombs == 0) { // check if the player have enough bombs
            return false;
        }

        for (Bomb bomb : bombs) { // check if there is not already a bomb on the position
            if (bomb.getPosition().equals(getPosition())) {
                return false;
            }
        }

        return true;
    }

    /**
     * @param now time when the bomb is posed
     *
     * @return the bomb the player posed, null if the player can't place it
     */
    public Bomb doPutBomb(long now) {
        if (!canPutBomb()) {
            return null;
        }

        Bomb bomb = new Bomb(game, getPosition(), this.bombsRange);
        bombs.add(bomb);

        this.numberOfBombs--;

        return bomb;
    }

    /**
     * Check if the player is on a monster, if so the player lose one live
     */
    private void removeLifeIfOnMonster() {
        for (Monster monster : this.game.getMonsters()) {
            if (monster.getPosition().equals(getPosition())) {
                this.inflictDamage(1);
                checkIfCharacterIsDead();
                return;
            }
        }
    }

    /**
     * Check if the player is on the Princess position, if so set the player to winner
     */
    private void checkIfPlayerWin() {
        if (this.game.getWorld().get(getPosition()) instanceof Princess) {
            this.winner = true;
        }
    }

    /**
     * Check if the current position contains a bonus, if so execute the bonus action and removed it from the board
     */
    private void checkIfContainsBonus() {
        Decor decor = this.game.getWorld().get(getPosition());

        if (decor instanceof Bonus) {
            ((Bonus) decor).doAction(this);
            this.game.getWorld().clear(getPosition());
        }
    }

    @Override
    public void update(long now) {
        checkIfCharacterIsDead();

        if (moveRequested) {
            if (canMove(direction)) {
                doMove(direction);
                removeLifeIfOnMonster();
                checkIfPlayerWin();
                checkIfContainsBonus();
                moveOnSpecialDecor();
            }
        }

        if (this.game.isLevelChange()) {
            this.world = this.game.getWorld();
        }

        if (bombRequested) {
            if (canPutBomb()) {
                doPutBomb(now);
            }
        }

        if (this.isInvincible) {
            checkInvincibility(now);
        }

        bombRequested = false;
        moveRequested = false;
    }

    /**
     * Check if the player is currently invincible
     *
     * @param now time when the check is made
     */
    private void checkInvincibility(long now) {
        long convert = TimeUnit.SECONDS.convert(now, TimeUnit.NANOSECONDS);// / 1__000__000__000;

        if (convert > timeStamp) { //TODO I don't know if it's a good idea to do it like that
            timeStamp = convert;

            long invincibilityTime = 1;
            if (this.countdown == invincibilityTime) {
                this.isInvincible = false;
                this.timeStamp = 0;
                this.countdown = 0;
            } else {
                this.countdown++;
            }
        }
    }

    /**
     * Check if the player is on a Door or a key and perform the corresponding action in that case
     */
    private void moveOnSpecialDecor() {
        Position myPos = this.getPosition();
        Decor decor = this.game.getWorld().get(this.getPosition());

        if (decor instanceof Door) {
            Door door = (Door) decor;

            if (door.isOpenToNextLevel()) {
                this.game.goNextLevel();

            } else if (door.isOpenToPreviousLevel()) {
                this.game.goPreviousLevel();
            }
        }

        if (decor instanceof Key) {
            this.keys++;
            this.game.getWorld().clear(myPos);
        }

    }

    /**
     * Inflict damage to the player if he is not invincible
     *
     * @param damage amount of damage
     */
    @Override
    public void inflictDamage(int damage) {
        if (!this.isInvincible) {
            this.lives -= damage;
            this.isInvincible = true;
        }
    }

    /**
     * To add one bomb in number of allowed bomb in the player status
     */
    public void incrementBombNumber() {
        this.numberOfBombs++;
    }

    public int getKeys() {
        return keys;
    }

    public boolean isWinner() {
        return winner;
    }

    public int getNumberOfBombs() {
        return numberOfBombs;
    }

    public void setNumberOfBombs(int numberOfBombs) {
        this.numberOfBombs = numberOfBombs;
    }

    public int getBombsRange() {
        return bombsRange;
    }

    public void setBombsRange(int bombsRange) {
        this.bombsRange = bombsRange;
    }

    public List<Bomb> getBombs() {
        return bombs;
    }

    public int getLives() {
        return this.lives;
    }

    public void setLives(int lives) {
        this.lives = lives;
    }

    public boolean isInvincible() {
        return isInvincible;
    }
}
