package fr.ubx.poo.model.decor;

public class Box extends Decor {
    @Override
    public String toString() {
        return "Box";
    }

    @Override
    public boolean isTraversable() {
        return false;
    }

    @Override
    public boolean isDestructible() { return true; }
}
