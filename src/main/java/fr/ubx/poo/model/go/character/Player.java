/*
 * Copyright (c) 2020. Laurent Réveillère
 */

package fr.ubx.poo.model.go.character;

import fr.ubx.poo.game.Direction;
import fr.ubx.poo.game.Position;
import fr.ubx.poo.game.WorldEntity;
import fr.ubx.poo.model.Movable;
import fr.ubx.poo.model.decor.*;
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
            if ( keys >= 1 ){//open the door only if the player have keys
//                this.world.openDoor(myPos);
                this.keys--;
            }
            this.world.openDoor(myPos); // it's to verify if the door correctly open (without checking the keys)
        }
        //TODO maybe a problem here when open the door, it is called twice ?

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

        //If move to an opened door
        Decor decor = this.world.get(this.getPosition());
        if(decor instanceof DoorPrevOpened) {//TODO verify if there is a better way to check it
            //move to the previous level
            this.game.goPreviousLevel();
        }
        if(decor instanceof DoorNextOpened) {//to do verify the checking
            //move to the next level
            this.game.goNextLevel();
//            System.out.println("je suis sur la porte");
        }
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
