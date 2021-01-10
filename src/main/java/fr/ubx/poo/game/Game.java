/*
 * Copyright (c) 2020. Laurent Réveillère
 */

package fr.ubx.poo.game;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import fr.ubx.poo.model.go.character.Monster;
import fr.ubx.poo.model.go.character.Player;

public class Game {

    private final List<World> worlds;
    private int level;
    private boolean isLevelChange;

    private final Player player;

    private List<Monster> monsters;

    private int initPlayerLives;
    private String levelFilePrefix;

    private final Map<Integer, List<Monster>> monstersLists;

    public Game(String worldPath) {
        loadConfig(worldPath);

        // To initialise all the world (thanks to WorldFileReader)
        this.level = 0; // Because worlds' list first index is 0
        this.isLevelChange = false;
        this.worlds = initializeWorlds(worldPath);
        World world = this.getWorld();

        // Initialize monsters lists
        this.monstersLists = initializeMonstersLists();

        Position positionPlayer;
        try {
            positionPlayer = world.findPlayer();
            player = new Player(this, positionPlayer);
        } catch (PositionNotFoundException e) {
            System.err.println("Position not found : " + e.getLocalizedMessage());
            throw new RuntimeException(e);
        }

        this.monsters = this.monstersLists.get(world.getLevelNumber());
    }

    /**
     * To initialize lists of monsters according to levels
     *
     * @return a map of monsters lists with level number as key
     */
    private Map<Integer, List<Monster>> initializeMonstersLists() {
        Map<Integer, List<Monster>> newMonstersLists = new HashMap<>();

        for (World world : this.worlds) {
            List<Monster> tmpMonsterList = new ArrayList<>();
            for (Position position : world.findMonsters()) {
                tmpMonsterList.add(new Monster(this, position));
            }
            newMonstersLists.put(world.getLevelNumber(), tmpMonsterList);
        }

        return newMonstersLists;
    }

    /**
     * To initialize worlds list
     *
     * @param worldPath path of the config files
     * @return list of worlds corresponding to the config files
     */
    private List<World> initializeWorlds(String worldPath) {
        List<World> worldsList = new ArrayList<>();

        File folder = new File(worldPath);
        int lvl = 1;

        for (final File fileEntry : Objects.requireNonNull(folder.listFiles())) {
            if (fileEntry.getName().contains(this.levelFilePrefix)) {
                World world = new World(fileEntry.getPath());
                world.setLevelNumber(lvl);
                worldsList.add(world);
                lvl++;
            }
        }

        return worldsList;
    }

    /**
     * Getter for initial player lives number
     *
     * @return initial Player lives number
     */
    public int getInitPlayerLives() {
        return initPlayerLives;
    }

    /**
     * To load config properties file
     *
     * @param path path of the file
     */
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

    /**
     * Getter for the current world
     *
     * @return the current world
     */
    public World getWorld() {
        return this.worlds.get(this.level);
    }

    /**
     * Getter for the player object
     *
     * @return the player
     */
    public Player getPlayer() {
        return this.player;
    }

    /**
     * Getter for the monsters list
     *
     * @return monsters list
     */
    public List<Monster> getMonsters() {
        return monsters;
    }

    /**
     * To process level modifications when go to the previous level
     */
    public void goPreviousLevel() {
        this.isLevelChange = true;
        this.level--;
        this.getWorld().comeFromNextLevel();
    }

    /**
     * To process level modifications when go to the next level
     */
    public void goNextLevel() {
        this.isLevelChange = true;
        this.level++;
    }

    /**
     * To check if the level changes
     *
     * @return true if the level changes
     */
    public boolean isLevelChange() {
        return isLevelChange;
    }

    /**
     * To set boolean which check if the level changes
     *
     * @param levelChange new value
     */
    public void setLevelChange(boolean levelChange) {
        isLevelChange = levelChange;
    }

    /**
     * To update the scene and needed attribute for the scene
     */
    public void updateScene() {
        World world = this.getWorld();

        Position positionPlayer;
        try {
            positionPlayer = world.findPlayer();

            this.player.setPosition(positionPlayer);

        } catch (PositionNotFoundException e) {
            throw new RuntimeException(e);
        }

        this.monsters = this.monstersLists.get(this.getWorld().getLevelNumber());
    }

    /**
     * To process damage caused to the player
     *
     * @param damage value of damage
     */
    public void inflictDamageToPlayer(int damage) {
        this.getPlayer().inflictDamage(damage);

        // To update player logic
        this.getPlayer().update(0);
    }

    /**
     * To process damage caused to a monster
     *
     * @param monster the monster concerned
     * @param damage  value of damage
     */
    public void inflictDamageToMonster(Monster monster, int damage) {
        monster.inflictDamage(damage);

        // To update monster logic
        monster.update(0);
    }

    /**
     * To remove the monster from the list
     *
     * @param monster the monster concerned
     */
    public void removeMonster(Monster monster) {
        this.monsters.remove(monster);
    }
}
