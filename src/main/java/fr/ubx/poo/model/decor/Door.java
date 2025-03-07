package fr.ubx.poo.model.decor;

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
     *
     */
    public void openTheDoor() {
        if (this.type == WorldEntity.DoorNextClosed) {
            this.type = WorldEntity.DoorNextOpened;
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
        return this.type != WorldEntity.DoorNextClosed;
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
