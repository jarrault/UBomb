package fr.ubx.poo.model.go.character;

import fr.ubx.poo.game.Direction;
import fr.ubx.poo.game.Game;
import fr.ubx.poo.game.Position;
import fr.ubx.poo.model.Movable;
import fr.ubx.poo.model.decor.Decor;
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

    public abstract void update(long now);

    public abstract boolean canMove(Direction direction);

    public void doMove(Direction direction) {
        Position nextPos = direction.nextPosition(getPosition());
        setPosition(nextPos);
    }

    protected boolean nextPositionInWorldAndEmpty(Position nextPosition) {
        if (!this.world.isInside(nextPosition)) {
            return false;
        }

        Decor decor = this.world.get(nextPosition);

        if (!this.world.isEmpty(nextPosition)) {
            return decor.isTraversable();
        }

        return true;
    }

    public Direction getDirection() {
        return direction;
    }

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
