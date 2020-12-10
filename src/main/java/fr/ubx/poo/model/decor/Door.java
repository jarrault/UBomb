package fr.ubx.poo.model.decor;

import fr.ubx.poo.game.World;
import fr.ubx.poo.game.WorldEntity;

public class Door extends Decor {
    private WorldEntity type;

    @Override
    public String toString() {
        return "Previous door opened";
    }

    /**
     * to open the door
     * @return true if it's correctly open
     */
    public boolean open(){
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
}
