/*
 * Copyright (c) 2020. Laurent Réveillère
 */

package fr.ubx.poo.game;

import fr.ubx.poo.model.go.Bomb;
import fr.ubx.poo.model.decor.Decor;
import fr.ubx.poo.model.decor.Door;
import fr.ubx.poo.model.go.character.Monster;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

public class World {
    private final Map<Position, Decor> grid;
    private final WorldEntity[][] raw;
    public final Dimension dimension;

    private final ArrayList<Position> monsterPositionList;

    private int levelNumber;//it could be final ?
    private boolean comeFromNextLevel;

    public World(WorldEntity[][] raw) {
        this.raw = raw;
        this.dimension = new Dimension(raw.length, raw[0].length);
        this.grid = WorldBuilder.build(raw, dimension);

        this.levelNumber = -1;
        this.comeFromNextLevel = false;

        this.monsterPositionList = this.initializeMonstersPosition();
    }

    public World(String filepath) {
        WorldFileReader world = new WorldFileReader(filepath);
        this.raw = world.getEntities();
        this.dimension = world.getDimension();
        this.grid = WorldBuilder.build(world);

        this.levelNumber = -1;
        this.comeFromNextLevel = false;

        this.monsterPositionList = this.initializeMonstersPosition();
    }

    /**
     * To get postion of the player
     * @return postion of the player
     * @throws PositionNotFoundException if the player is not found
     */
    public Position findPlayer() throws PositionNotFoundException {
//        debug_showGrid();

        for (int x = 0; x < dimension.width; x++) {
            for (int y = 0; y < dimension.height; y++) {

                if(this.comeFromNextLevel) {
                    if (raw[y][x] == WorldEntity.DoorNextClosed) {
                        this.comeFromNextLevel = false;
                        return new Position(x,y);
                    }
                } else {

                    if (raw[y][x] == WorldEntity.Player && this.levelNumber == 1) {
                        return new Position(x, y);
                    } else if ((raw[y][x] == WorldEntity.Player || raw[y][x] == WorldEntity.DoorPrevOpened)
                            && this.levelNumber > 1) {
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


    /**
     * To find positions of monsters
     * @return list of monsters' position
     */
    public ArrayList<Position> findMonsters() {
//        ArrayList<Position> monstersPositions = new ArrayList<>();
//
//        for (int x = 0; x < dimension.width; x++) {
//            for (int y = 0; y < dimension.height; y++) {
//                if (raw[y][x] == WorldEntity.Monster) {
////                if (raw[x][y] == WorldEntity.Monster) {
//                    monstersPositions.add(new Position(x, y));
//                }
//            }
//        }
//
//        return monstersPositions;
        //this is old version of the method, I keep it because it's a temporary modification

        return this.monsterPositionList;
    }

    /**
     * To initialize list of the monsters' position
     * @return list of the monsters' position
     */
    public ArrayList<Position> initializeMonstersPosition() {
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

    public void addMonsterPosition(Position position){
        this.monsterPositionList.add(position);
    }

    public void removeMonsterPosition(Position position){
//        System.out.println("remove monster pos => " + position);
        this.monsterPositionList.remove(position); //TODO it don't work because when monster died, he is not at the same postion than when we create him
//        System.out.println("oui");
    }

    /**
     * To get the decor according to the position
     * @param position position of the decor
     * @return decor according to the position
     */
    public Decor get(Position position) {
        return grid.get(position);
    }

    /**
     * To set a decor in the grid
     * @param position position of the decor
     * @param decor the decor
     */
    public void set(Position position, Decor decor) {
        grid.put(position, decor);
    }

    /**
     * To clear/remove a decor in the grid
     * @param position position of the concerned decor
     */
    public void clear(Position position) {
        grid.remove(position);
    }

    //TODO javadoc
    public void forEach(BiConsumer<Position, Decor> fn) {
        grid.forEach(fn);
    }

    //TODO javadoc
    public Collection<Decor> values() {
        return grid.values();
    }

    /**
     * To check if the position is inside the screen
     * @param position concerned position
     * @return true if the position is inside the screen
     */
    public boolean isInside(Position position) {
        return position.inside(this.dimension);
    }

    public boolean isEmpty(Position position) {
        return grid.get(position) == null;
    }

    /**
     * To process the door's opening
     * @param door concerned door
     */
    public void openDoor(Door door) {
        door.openTheDoor();
    }

    /**
     * Getter of the level number
     * @return level number
     */
    public int getLevelNumber() {
        return levelNumber;
    }

    /**
     * Setter of the level number
     * @param levelNumber new value
     */
    public void setLevelNumber(int levelNumber) {
        this.levelNumber = levelNumber;
    }

    /**
     * To process the event that player come from the next level
     */
    public void comeFromNextLevel() {
        this.comeFromNextLevel = true;
    }
}
