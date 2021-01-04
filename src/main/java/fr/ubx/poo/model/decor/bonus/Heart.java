package fr.ubx.poo.model.decor.bonus;

import fr.ubx.poo.model.decor.Decor;
import fr.ubx.poo.model.go.character.Player;

public class Heart extends Bonus {
    @Override
    public String toString() {
        return "Heart";
    }

    @Override
    public void doAction(Player player) {
        player.setLives(player.getLives() + 1);
    }
}
