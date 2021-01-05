/*
 * Copyright (c) 2020. Laurent Réveillère
 */

package fr.ubx.poo.model.go.character;

import fr.ubx.poo.game.Direction;
import fr.ubx.poo.game.Position;
import fr.ubx.poo.model.go.Bomb;
import fr.ubx.poo.model.decor.Box;
import fr.ubx.poo.model.decor.Decor;
import fr.ubx.poo.model.decor.Princess;
import fr.ubx.poo.game.Game;
import fr.ubx.poo.model.decor.bonus.*;

import java.util.ArrayList;
import java.util.List;

public class Player extends Character {

    private boolean alive = true;
    private boolean moveRequested = false;
    private boolean bombRequested = false;
    private int lives = 1;
    private boolean winner;
    private boolean updateSprites = false;
    private int numberOfBombs = 2;
//    private int bombsRange = 1;
    private int bombsRange = 3;
    private List<Bomb> bombs;

    public Player(Game game, Position position) {
        super(game, position);
        this.lives = game.getInitPlayerLives();
        this.bombs = new ArrayList<>();
    }

    public int getLives() {
        return lives;
    }

    public void requestMove(Direction direction) {
        if (direction != this.direction) {
            this.direction = direction;
        }
        moveRequested = true;
    }

    public void requestBomb() {
//        if (this.game.getWorld().get(getPosition()) instanceof Bomb) { //dont work but it's an idea like that
//            this.direction = direction;
//        }
        bombRequested = true;
    }

    @Override
    public boolean canMove(Direction direction) {
        Position nextPos = direction.nextPosition(getPosition());

        if (!this.world.isInside(nextPos)) {
            return false;
        }

        Decor decor = this.world.get(nextPos);

        if (decor instanceof Box) {
            return canMoveBox(direction, nextPos, decor);
        }

        if (decor != null) {
            return decor.isTraversable();
        }

        return true;
    }

    private boolean canMoveBox(Direction direction, Position nextPos, Decor decor) {
        Position newPosition = direction.nextPosition(nextPos);

        if (!this.world.isInside(newPosition)) {
            return false;
        }

        if (this.world.get(newPosition) != null) {
            return false;
        }

        for (Monster monster : game.getMonsters()) {
            if (monster.getPosition().equals(newPosition)) {
                return false;
            }
        }

        world.clear(nextPos);
        world.set(newPosition, decor);
        updateSprites = true;

        return true;
    }

    public boolean canPutBomb() {
        if(this.numberOfBombs == 0){
            return false;
        }

        for (Bomb bomb : bombs) {
            if (bomb.getPosition().equals(getPosition())) {
                return false;
            }
        }

        return true;
    }

    public Bomb doPutBomb(long now) {
        if (!canPutBomb()) {
            return null;
        }

        Bomb bomb = new Bomb(game, getPosition(), now, this.bombsRange);
        this.numberOfBombs--;
        bombs.add(bomb);
        updateSprites = true;
        return bomb;
    }

    public void doMove(Direction direction) {
        Position nextPos = direction.nextPosition(getPosition());
        setPosition(nextPos);
    }

    private void removeLifeIfOnMonster() {
        for (Monster monster : this.game.getMonsters()) {
            if (monster.getPosition().equals(getPosition())) {
                lives--;
                checkIfPlayerLoose();
                return;
            }
        }
    }

    private void checkIfPlayerWin() {
        if (this.world.get(getPosition()) instanceof Princess) {
            this.winner = true;
        }
    }

    private void checkIfPlayerLoose() {
        if (lives == 0) {
            this.alive = false;
        }
    }

    private void checkIfContainsBonus() {
        Decor decor = world.get(getPosition());

        if (decor instanceof Bonus) {
            world.clear(getPosition());
            updateSprites = true;
        }

        if (decor instanceof BombNumberDec && numberOfBombs > 1) {
            numberOfBombs--;
        }

        if (decor instanceof BombNumberInc) {
            numberOfBombs++;
        }

        if (decor instanceof BombRangeDec && bombsRange > 1) {
            bombsRange--;
        }

        if (decor instanceof BombRangeInc) {
            bombsRange++;
        }

        if (decor instanceof Heart) {
            lives++;
        }
    }

    public void update(long now) {
        if (moveRequested) {
            if (canMove(direction)) {
                doMove(direction);
                removeLifeIfOnMonster();
                checkIfPlayerWin();
                checkIfContainsBonus();
            }
        }

        if (bombRequested){
            if(canPutBomb()){
                doPutBomb(now);
            }
        }

        bombRequested = false;
        moveRequested = false;
    }

    public void addBomb(Bomb bomb){
        this.bombs.add(bomb);
    }

    public void removeBomb(Bomb bomb){
        this.bombs.remove(bomb);
    }

    /**
     * To add one bomb in number of allowed bomb in the player status
     */
    public void incrementBombNumber(){
        this.numberOfBombs++;
    }

    public boolean isWinner() {
        return winner;
    }

    public boolean isAlive() {
        return alive;
    }

    public boolean isUpdateSprites() {
        return updateSprites;
    }

    public void setUpdateSprites(boolean updateSprites) {
        this.updateSprites = updateSprites;
    }

    public void setBombs(List<Bomb> bomb){
        this.bombs = bomb;
    }

    public int getNumberOfBombs() {
        return numberOfBombs;
    }

    public int getBombsRange() {
        return bombsRange;
    }

    public List<Bomb> getBombs() {
        return bombs;
    }
}
