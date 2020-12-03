/*
 * Copyright (c) 2020. Laurent Réveillère
 */

package fr.ubx.poo.game;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import fr.ubx.poo.model.go.character.Monster;
import fr.ubx.poo.model.go.character.Player;

public class Game {

    private final List<World> worlds;
    private int level;
    private boolean isLevelChange;

    private final Player player;
    private final ArrayList<Monster> monsters = new ArrayList<>();
    private final String worldPath;
    public int initPlayerLives;
    public String levelFilePrefix; //is it necessary to be public ?

    public Game(String worldPath) {
        this.worldPath = worldPath;
        loadConfig(worldPath);

        //to initialise all the world (thanks to WorldFileReader)
        this.level = 0;//because worlds' list first index is 0
        this.isLevelChange = false;
        this.worlds = initializeWorlds(worldPath);
        World world = this.getWorld();

        Position positionPlayer = null;
        try {
            positionPlayer = world.findPlayer();
            player = new Player(this, positionPlayer);
        } catch (PositionNotFoundException e) {
            System.err.println("Position not found : " + e.getLocalizedMessage());
            throw new RuntimeException(e);
        }

        ArrayList<Position> monstersPositions = world.findMonsters();
        for (Position monsterPosition : monstersPositions) {
            monsters.add(new Monster(this, monsterPosition));
        }
    }

    private List<World> initializeWorlds(String worldPath) {
        List<World> worldsList = new ArrayList<>();

        File folder = new File(worldPath);
        int lvl = 1;

        //TODO change var names bellow
        for (final File fileEntry : folder.listFiles()) {
            //is it necessary to check if the folder contanis other folder ?

            if (fileEntry.getName().contains(this.levelFilePrefix)) {
                World world = new World(fileEntry.getPath());
                world.setLevelNumber(lvl);
                worldsList.add(world);
                lvl++;
            }

        }

        return worldsList;
    }

    public int getInitPlayerLives() {
        return initPlayerLives;
    }

    private void loadConfig(String path) {
        try (InputStream input = new FileInputStream(new File(path, "config.properties"))) {
            Properties prop = new Properties();
            // load the configuration file
            prop.load(input);
            initPlayerLives = Integer.parseInt(prop.getProperty("lives", "3"));
            levelFilePrefix = prop.getProperty("prefix", "level");
        } catch (IOException ex) {
            System.err.println("Error loading configuration");
        }
    }

    public World getWorld() {
        return this.worlds.get(this.level);
    }

    public Player getPlayer() {
        return this.player;
    }

    public ArrayList<Monster> getMonsters() {
        return monsters;
    }

    public void goPreviousLevel() {
        this.isLevelChange = true;
        this.level--;
        this.getWorld().comeFromNextLevel();
        System.out.println("prev level : " + this.level);
    }

    public void goNextLevel() {
        this.isLevelChange = true;
        this.level++;
        System.out.println("new level : " + this.level);
    }

    public boolean isLevelChange() {
        return isLevelChange;
    }

    public void setLevelChange(boolean levelChange) {
        isLevelChange = levelChange;
    }

    public void updateScene() {
        World world = this.getWorld();

        Position positionPlayer = null;
        try { //TODO set player position when go to previous level
            positionPlayer = world.findPlayer();

            this.player.setPosition(positionPlayer);
//            player = new Player(this, positionPlayer);

        } catch (PositionNotFoundException e) {
            System.err.println("Position not found : " + e.getLocalizedMessage());
            throw new RuntimeException(e);
        }

        ArrayList<Position> monstersPositions = world.findMonsters();
        for (Position monsterPosition : monstersPositions) {
            monsters.add(new Monster(this, monsterPosition));
        }
    }
}
