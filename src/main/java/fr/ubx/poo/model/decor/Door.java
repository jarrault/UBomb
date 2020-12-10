package fr.ubx.poo.model.decor;

import fr.ubx.poo.game.World;
import fr.ubx.poo.game.WorldEntity;

public class Door extends Decor {
    private WorldEntity type;

    public Door(WorldEntity type) {
        super();
        this.type = type;
    }

    @Override
    public String toString() {
        return "Previous door opened";
    }

    /**
     * to open the door
     * @return true if it's correctly open
     */
    public boolean openTheDoor(){
        if(this.type == WorldEntity.DoorNextClosed) {
            this.type = WorldEntity.DoorNextOpened;
            return true;
        } else {
            return false;
        }
    }

    public void setType(WorldEntity type) {
        this.type = type;
    }

    public WorldEntity getType() {
        return type;
    }

    public boolean isOpen(){
        if(this.type == WorldEntity.DoorNextClosed){
            return false;
        }else {
            return true;
        }
    }

    public boolean isOpenToNextLevel(){
        if(this.type == WorldEntity.DoorNextOpened){ //TODO try a tertier condition (with ?)
            return true;
        } else {
            return false;
        }
    }

    public boolean isOpenToPreviousLevel(){
        if(this.type == WorldEntity.DoorPrevOpened){ //TODO try a tertier condition (with ?)
            return true;
        } else {
            return false;
        }
    }
}
