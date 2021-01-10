package fr.ubx.poo.game;

import fr.ubx.poo.model.decor.*;
import fr.ubx.poo.model.decor.bonus.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;

public class WorldBuilder {
    private final Map<Position, Decor> grid = new Hashtable<>();

    private WorldBuilder() {
    }

    /**
     * To build a grid according to an entites' matrix
     *
     * @param raw       entities' matrix
     * @param dimension dimension of the grid
     * @return a map which represents a grid
     */
    public static Map<Position, Decor> build(WorldEntity[][] raw, Dimension dimension) {
        WorldBuilder builder = new WorldBuilder();
        for (int x = 0; x < dimension.width; x++) {
            for (int y = 0; y < dimension.height; y++) {
                Position pos = new Position(x, y);
                Decor decor = processEntity(raw[y][x]);
                if (decor != null)
                    builder.grid.put(pos, decor);
            }
        }
        return builder.grid;
    }

    /**
     * To build a grid according to an entites' matrix
     *
     * @param worldFileReader class which read World's attributes from a file
     * @return a map which represents a grid
     */
    public static Map<Position, Decor> build(WorldFileReader worldFileReader) {
        return WorldBuilder.build(worldFileReader.getEntities(), worldFileReader.getDimension());
    }

    /**
     * To get the correct decor according to an entity
     *
     * @param entity wanted entity
     * @return correct decor according to the entity
     */
    private static Decor processEntity(WorldEntity entity) {
        switch (entity) {
            case Stone:
                return new Stone();
            case Tree:
                return new Tree();
            case Box:
                return new Box();
            case Heart:
                return new Heart();
            case Key:
                return new Key();
            case BombNumberDec:
                return new BombNumberDec();
            case BombNumberInc:
                return new BombNumberInc();
            case BombRangeDec:
                return new BombRangeDec();
            case BombRangeInc:
                return new BombRangeInc();

            //the tree "door entities" are represented by the same Decor class
            case DoorNextClosed:
            case DoorNextOpened:
            case DoorPrevOpened:
                return new Door(entity);

            case Princess:
                return new Princess();
            default:
                return null;
        }
    }
}
