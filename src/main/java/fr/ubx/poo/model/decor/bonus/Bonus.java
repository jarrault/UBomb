package fr.ubx.poo.model.decor.bonus;

import fr.ubx.poo.model.decor.Decor;
import fr.ubx.poo.model.go.character.Player;

<<<<<<< HEAD
public abstract class Bonus extends Decor {
    public abstract void doAction(Player player);
=======
public class Bonus extends Decor {
    @Override
    public boolean isDestructible() { return true; }
>>>>>>> bombs
}
