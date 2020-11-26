/*
 * Copyright (c) 2020. Laurent Réveillère
 */

package fr.ubx.poo.model.go.character;

import fr.ubx.poo.game.Direction;
import fr.ubx.poo.game.Position;
import fr.ubx.poo.game.WorldEntity;
import fr.ubx.poo.model.Movable;
import fr.ubx.poo.model.decor.Decor;
import fr.ubx.poo.model.decor.DoorNextClosed;
import fr.ubx.poo.model.decor.Princess;
import fr.ubx.poo.model.go.GameObject;
import fr.ubx.poo.game.Game;

public class Player extends Character {

    private boolean alive = true;
    private boolean moveRequested = false;
    private int lives = 1;
    private int keys = 0;
    private boolean winner;

    public Player(Game game, Position position) {
        super(game, position);
        this.lives = game.getInitPlayerLives();
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

    /**
     * to try open a door when ENTER input
     */
    public void requestOpenDoor(){
        Position myPos = this.getPosition();
        Decor decor = this.world.get(myPos);

        if(decor instanceof DoorNextClosed) {//TODO verify if there is a better way to check it
            if ( keys >= 1 ){//only if the player have keys
                //open the door

                this.keys--;
            }
        }

    }

    @Override
    public boolean canMove(Direction direction) {
        Position nextPos = direction.nextPosition(getPosition());

        if (!this.world.isInside(nextPos)) {
            return false;
        }

        Decor decor = this.world.get(nextPos);
        if (decor != null) {
            return decor.isTraversable();
        }

        return true;
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

    public void update(long now) {
        if (moveRequested) {
            if (canMove(direction)) {
                doMove(direction);
                removeLifeIfOnMonster();
                checkIfPlayerWin();
            }
        }
        moveRequested = false;
    }

    public boolean isWinner() {
        return winner;
    }

    public boolean isAlive() {
        return alive;
    }

}
