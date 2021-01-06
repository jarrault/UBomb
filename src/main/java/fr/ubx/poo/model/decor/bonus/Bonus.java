package fr.ubx.poo.model.decor.bonus;

import fr.ubx.poo.model.decor.Decor;
import fr.ubx.poo.model.go.character.Player;

public abstract class Bonus extends Decor {
    public abstract void doAction(Player player);

    @Override
    public boolean isDestructible() { return true; }
}
