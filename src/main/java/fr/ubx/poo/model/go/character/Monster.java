package fr.ubx.poo.model.go.character;

import fr.ubx.poo.game.Direction;
import fr.ubx.poo.game.Game;
import fr.ubx.poo.game.Position;
import fr.ubx.poo.model.Movable;
import fr.ubx.poo.model.decor.*;
import fr.ubx.poo.model.go.GameObject;

import java.util.concurrent.TimeUnit;

public class Monster extends Character {

    public Monster(Game game, Position position) {
        super(game, position);
    }

    @Override
    public boolean canMove(Direction direction) {
        Position nextPos = direction.nextPosition(getPosition());

        if (!this.world.isInside(nextPos)) {
            return false;
        }

        Decor decor = this.world.get(nextPos);

//        if (decor instanceof Box) {
//            return canMoveBox(direction, nextPos, decor);
//        }

        if (decor != null) {
            return decor.isTraversable();
        }

        return true;
    }

    @Override
    public void doMove(Direction direction) {
        Position nextPos = direction.nextPosition(getPosition());
        setPosition(nextPos);

    }

    long timeStamp = 0;

    @Override
    public void update(long now) {
        Direction dir = Direction.random();

        long convert = TimeUnit.SECONDS.convert(now, TimeUnit.NANOSECONDS);// / 1__000__000__000;

        if(convert > timeStamp) { //TODO I don't know if it's a good idea to do it like that
            timeStamp = convert;

            if (canMove(dir)) {
                doMove(dir);

                checkIfInflictDamageToPlayer();
            }

        }
    }

    private void checkIfInflictDamageToPlayer() {
        if(this.game.getPlayer().getPosition().equals(this.getPosition())){
            this.game.inflictDamageToPlayer(1);
        }
    }
}
