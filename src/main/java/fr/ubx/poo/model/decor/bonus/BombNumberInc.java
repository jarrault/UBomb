package fr.ubx.poo.model.decor.bonus;

import fr.ubx.poo.model.decor.Decor;
import fr.ubx.poo.model.go.character.Player;

public class BombNumberInc extends Bonus {
    @Override
    public String toString() {
        return "bomb number increment";
    }

    @Override
    public void doAction(Player player) {
        player.setNumberOfBombs(player.getNumberOfBombs() + 1);
    }
}
