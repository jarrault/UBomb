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
     *
     * @return true if it's correctly open
     */
    public boolean openTheDoor() {
        if (this.type == WorldEntity.DoorNextClosed) {
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

    public boolean isOpen() {
        if (this.type == WorldEntity.DoorNextClosed) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * to check if the door is open to the next level
     *
     * @return true if the door is open to the next level
     */
    public boolean isOpenToNextLevel() {
        return (this.type == WorldEntity.DoorNextOpened);
    }

    /**
     * to check if the door is open to the previous level
     *
     * @return true if the door is open to the previous level
     */
    public boolean isOpenToPreviousLevel() {
        return (this.type == WorldEntity.DoorPrevOpened);
    }
}
