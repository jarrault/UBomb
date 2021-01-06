package fr.ubx.poo.model.decor.bonus;

import fr.ubx.poo.model.decor.Decor;
import fr.ubx.poo.model.go.character.Player;

public class BombNumberDec extends Bonus {
    @Override
    public String toString() {
        return "bomb number decrement";
    }

    @Override
    public void doAction(Player player) {
        if (player.getNumberOfBombs() > 1) {
            player.setNumberOfBombs(player.getNumberOfBombs() - 1);
        }
    }
}
