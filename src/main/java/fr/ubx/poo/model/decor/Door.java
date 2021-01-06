package fr.ubx.poo.model.decor;

import fr.ubx.poo.game.World;
import fr.ubx.poo.game.WorldEntity;

/**
 * door's logic class
 */
public class Door extends Decor {
    /**
     * Door's type (closed, open to the next level, open to the previous level)
     */
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

    /**
     * Setter for door's type closed, open to the next level, open to the previous level)
     * @param type new type value
     */
    public void setType(WorldEntity type) {
        this.type = type;
    }

    /**
     * Getter for door's type closed, open to the next level, open to the previous level)
     * @return door's type
     */
    public WorldEntity getType() {
        return type;
    }

    /**
     * To check if the door is open
     * @return true if the door is open
     */
    public boolean isOpen(){
        if(this.type == WorldEntity.DoorNextClosed){
            return false;
        }else {
            return true;
        }
    }

    //TODO could we factorize in one the two methods under ?

    /**
     * To check if the door is open to the next level
     * @return true if the door is open to next level
     */
    public boolean isOpenToNextLevel(){
        if(this.type == WorldEntity.DoorNextOpened){ //TODO try a tertier condition (with ?)
            return true;
        } else {
            return false;
        }
    }

    /**
     * To check if the door is open to the previous level
     * @return true if the door is open to previous level
     */
    public boolean isOpenToPreviousLevel(){
        if(this.type == WorldEntity.DoorPrevOpened){ //TODO try a tertier condition (with ?)
            return true;
        } else {
            return false;
        }
    }
}
