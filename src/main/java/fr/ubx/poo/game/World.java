/*
 * Copyright (c) 2020. Laurent Réveillère
 */

package fr.ubx.poo.game;

import fr.ubx.poo.model.decor.Decor;
import fr.ubx.poo.model.decor.Door;
import fr.ubx.poo.model.decor.DoorNextOpened;
import fr.ubx.poo.model.go.character.Monster;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.function.BiConsumer;

import static fr.ubx.poo.game.WorldEntity.*;

public class World {
    private final Map<Position, Decor> grid;
    private final WorldEntity[][] raw;
    public final Dimension dimension;

    private int levelNumber;//it could be final ?
    private boolean comeFromNextLevel;

    public World(WorldEntity[][] raw) {
        this.raw = raw;
        this.dimension = new Dimension(raw.length, raw[0].length);
        this.grid = WorldBuilder.build(raw, dimension);

        this.levelNumber = -1;
        this.comeFromNextLevel = false;
    }

    public World(String filepath) {
        WorldFileReader world = new WorldFileReader(filepath);
        this.raw = world.getEntities();
        this.dimension = world.getDimension();
        this.grid = WorldBuilder.build(world);

        this.levelNumber = -1;
        this.comeFromNextLevel = false;
    }

    public Position findPlayer() throws PositionNotFoundException {
//        debug_showGrid();

        for (int x = 0; x < dimension.width; x++) {
            for (int y = 0; y < dimension.height; y++) {

                //TODO do it better ( => not throw PlayerNotFoundExeception when it's not the first level)
                // because the other level don't contain Player case

                if(this.comeFromNextLevel) {
                    if (raw[y][x] == DoorNextClosed) {
                        this.comeFromNextLevel = false;
                        return new Position(x,y);
                    }
                } else {

                    if (raw[y][x] == WorldEntity.Player && this.levelNumber == 1) {
//                    System.out.println("findPlayer : find player in level 1");
                        return new Position(x, y);
                    } else if ((raw[y][x] == WorldEntity.Player || raw[y][x] == DoorPrevOpened)
                            && this.levelNumber > 1) {
//                    System.out.println("findPlayer : find opened door");
                        return new Position(x, y);
                    }


                }

            }
        }
        throw new PositionNotFoundException("Player (in level " + this.levelNumber + ")");
    }

    private void debug_showGrid() {
        for (int x = 0; x < dimension.height; x++) {
            for (int y = 0; y < dimension.width; y++) {
                System.out.print(raw[x][y]);
            }
            System.out.println();
        }

    }


    public ArrayList<Position> findMonsters() {
        ArrayList<Position> monstersPositions = new ArrayList<>();

        for (int x = 0; x < dimension.width; x++) {
            for (int y = 0; y < dimension.height; y++) {
                if (raw[y][x] == WorldEntity.Monster) {
//                if (raw[x][y] == WorldEntity.Monster) {
                    monstersPositions.add(new Position(x, y));
                }
            }
        }

        return monstersPositions;
    }

    public Decor get(Position position) {
        return grid.get(position);
    }

    public void set(Position position, Decor decor) {
        grid.put(position, decor);
    }

    public void clear(Position position) {
        grid.remove(position);
    }

    public void forEach(BiConsumer<Position, Decor> fn) {
        grid.forEach(fn);
    }

    public Collection<Decor> values() {
        return grid.values();
    }

    public boolean isInside(Position position) {
        return position.inside(this.dimension);
    }

    public boolean isEmpty(Position position) {
        return grid.get(position) == null;
    }

//    public void openDoor(Position pos) {
    public void openDoor(Door door) {
//        //TODO is it necessary to check if the Position is a door ?
//        //remove the closed door
//        this.clear(pos);
//
//        //set the new door
//        this.set(pos, new DoorNextOpened());

        door.openTheDoor();
    }

    public int getLevelNumber() {
        return levelNumber;
    }

    public void setLevelNumber(int levelNumber) {
        this.levelNumber = levelNumber;
    }

    public void comeFromNextLevel() {
        this.comeFromNextLevel = true;
    }
}
