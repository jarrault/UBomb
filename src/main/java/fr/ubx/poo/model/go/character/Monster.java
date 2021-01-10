package fr.ubx.poo.model.go.character;

import fr.ubx.poo.game.Direction;
import fr.ubx.poo.game.Game;
import fr.ubx.poo.game.Position;
import fr.ubx.poo.model.decor.Decor;
import fr.ubx.poo.model.decor.Door;

import java.util.concurrent.TimeUnit;

/**
 * Monster character class
 */
public class Monster extends Character {

    /**
     * //TODO
     */
    long timeStamp = 0;

    public Monster(Game game, Position position) {
        super(game, position);
    }

    @Override
    public boolean canMove(Direction direction) {
        Position nextPos = direction.nextPosition(getPosition());
        Decor decor = this.world.get(nextPos);

        if (decor instanceof Door) {
            return false;
        }

        return nextPositionInWorldAndEmpty(nextPos);
    }

    @Override
    public void update(long now) {
        Direction direction = Direction.random();

        long convert = TimeUnit.SECONDS.convert(now, TimeUnit.NANOSECONDS);// / 1__000__000__000;

        if(convert > timeStamp) { //TODO I don't know if it's a good idea to do it like that
            timeStamp = convert;

            if (canMove(direction)) {
                doMove(direction);

                // to inflict damage to the player if it's possibleT
                if(this.game.getPlayer().getPosition().equals(this.getPosition())){
                    this.game.inflictDamageToPlayer(1);
                }
            }
        }

        //to check if the monster is dead
        this.checkIfCharacterIsDead();
    }
}
