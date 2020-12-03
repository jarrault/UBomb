package fr.ubx.poo.model.go.character;

import fr.ubx.poo.game.Direction;
import fr.ubx.poo.game.Game;
import fr.ubx.poo.game.Position;
import fr.ubx.poo.model.Movable;
import fr.ubx.poo.model.decor.*;
import fr.ubx.poo.model.go.GameObject;

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

    @Override
    public void update(long now) {
        Direction dir = Direction.random();

        if(canMove(dir)){
            doMove(dir);

            checkIfInflictDamageToPlayer();
        }
    }

    private void checkIfInflictDamageToPlayer() {
        if(this.game.getPlayer().getPosition().equals(this.getPosition())){
            this.game.inflictDamageToPlayer(1);
        }
    }
}
