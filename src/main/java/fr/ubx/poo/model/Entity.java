package fr.ubx.poo.model;

public abstract class Entity {
    public boolean isTraversable() {
        return true;
    }
    public boolean isDestructible(){ return false; }
}
