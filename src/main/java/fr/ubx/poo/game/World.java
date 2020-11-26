/*
 * Copyright (c) 2020. Laurent Réveillère
 */

package fr.ubx.poo.game;

import fr.ubx.poo.model.decor.Decor;
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

    public World(WorldEntity[][] raw) {
        this.raw = raw;
        this.dimension = new Dimension(raw.length, raw[0].length);
        this.grid = WorldBuilder.build(raw, dimension);
    }

    public World(String filepath) {
            WorldFileReader world = new WorldFileReader(filepath);
            this.raw = world.getEntities();
            this.dimension = world.getDimension();
            this.grid = WorldBuilder.build(world);
    }

    public Position findPlayer() throws PositionNotFoundException {
        for (int x = 0; x < dimension.width; x++) {
            for (int y = 0; y < dimension.height; y++) {
                if (raw[y][x] == WorldEntity.Player) {
                    return new Position(x, y);
                }
            }
        }
        throw new PositionNotFoundException("Player");
    }

    public ArrayList<Position> findMonsters() {
        ArrayList<Position> monstersPositions = new ArrayList<>();

        for (int x = 0; x < dimension.width; x++) {
            for (int y = 0; y < dimension.height; y++) {
                if (raw[y][x] == WorldEntity.Monster) {
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

    public void openDoor(Position pos) {
        //TODO is it necessary to check if the Position is a door ?
        //remove the closed door
        this.clear(pos);

        //set the new door
        this.set(pos, new DoorNextOpened());
    }
}
