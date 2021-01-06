package fr.ubx.poo.model.decor.bonus;

import fr.ubx.poo.model.decor.Decor;
import fr.ubx.poo.model.go.character.Player;

public class BombRangeInc extends Bonus {
    @Override
    public String toString() {
        return "bomb range increment";
    }

    @Override
    public void doAction(Player player) {
        player.setBombsRange(player.getBombsRange() + 1);
    }
}
