package fr.ubx.poo.model.decor.bonus;

import fr.ubx.poo.model.go.character.Player;

public class BombRangeDec extends Bonus {
    @Override
    public String toString() {
        return "bomb range decrement";
    }

    @Override
    public void doAction(Player player) {
        if (player.getBombsRange() > 1) {
            player.setBombsRange(player.getBombsRange() - 1);
        }
    }
}
