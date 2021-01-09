package fr.ubx.poo.model.go.character;

import fr.ubx.poo.game.Direction;
import fr.ubx.poo.game.Game;
import fr.ubx.poo.game.Position;
import fr.ubx.poo.model.Movable;
import fr.ubx.poo.model.go.GameObject;

public abstract class Character extends GameObject implements Movable {

    Direction direction;

    protected boolean alive = true;
    protected int lives = 1;
    protected long timeStamp = 0;

    public Character(Game game, Position position) {
        super(game, position);
        this.direction = Direction.S;
    }

    @Override
    public abstract boolean canMove(Direction direction);

    @Override
    public abstract void doMove(Direction direction);

    public Direction getDirection() {
        return direction;
    }

    public abstract void update(long now);

    public void inflictDamage(int damage){
        this.lives -= 1;
    }

    protected void checkIfCharacterIsDead() {
        if (lives == 0) {
            this.alive = false;
        }
    }

    public boolean isAlive() {
        return alive;
    }
}
