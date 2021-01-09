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

//    private final ArrayList<Monster> monsters = new ArrayList<>();
    private List<Monster> monsters; //TODO is it necessary it's been final ?

    private final String worldPath;
    public int initPlayerLives;
    public String levelFilePrefix; //is it necessary to be public ?

    private Map<Integer, List<Monster>> monstersLists;

    public Game(String worldPath) {
        this.worldPath = worldPath;
        loadConfig(worldPath);

        //to initialise all the world (thanks to WorldFileReader)
        this.level = 0;//because worlds' list first index is 0
        this.isLevelChange = false;
        this.worlds = initializeWorlds(worldPath);
        World world = this.getWorld();

        //initialize monsters lists
        this.monstersLists = initializeMonstersLists();

        Position positionPlayer = null;
        try {
            positionPlayer = world.findPlayer();
            player = new Player(this, positionPlayer);
        } catch (PositionNotFoundException e) {
            System.err.println("Position not found : " + e.getLocalizedMessage());
            throw new RuntimeException(e);
        }

//        ArrayList<Position> monstersPositions = world.findMonsters();
//        for (Position monsterPosition : monstersPositions) {
//            monsters.add(new Monster(this, monsterPosition));
//        }

        this.monsters = this.monstersLists.get(world.getLevelNumber());
    }

    /**
     * To initialize lists of monsters according to levels
     * @return a map of monsters lists with level number as key
     */
    private Map<Integer, List<Monster>> initializeMonstersLists() {
        Map<Integer, List<Monster>> newMonstersLists = new HashMap<>();

        for(World world : this.worlds){
            List<Monster> tmpMonsterList = new ArrayList<>();
            for(Position position : world.findMonsters()){
                tmpMonsterList.add(new Monster(this, position));
            }
            newMonstersLists.put(world.getLevelNumber(), tmpMonsterList);
        }

        return newMonstersLists;
    }

    private List<World> initializeWorlds(String worldPath) {
        List<World> worldsList = new ArrayList<>();

        File folder = new File(worldPath);
        int lvl = 1;

        for (final File fileEntry : folder.listFiles()) {
            //TODO is it necessary to check if the folder contanis other folder ?

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

    public List<Monster> getMonsters() {
//        System.out.println("Game.getMonsters ==> " + this.monsters + " ( " + this + " )");
//        System.out.println("Game.getMonsters ==> " + this.monsters );
        return monsters;
    }

    public void goPreviousLevel() {
        this.isLevelChange = true;
        this.level--;
        this.getWorld().comeFromNextLevel();
    }

    public void goNextLevel() {
        this.isLevelChange = true;
        this.level++;
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
        try {
            positionPlayer = world.findPlayer();

            this.player.setPosition(positionPlayer);
//            player = new Player(this, positionPlayer);

        } catch (PositionNotFoundException e) {
//            System.err.println("Position not found : " + e.getLocalizedMessage());
            throw new RuntimeException(e);
        }

//        System.out.println("b> " + this.monsters.size());
        this.monsters = this.monstersLists.get(this.getWorld().getLevelNumber());
//        System.out.println("a> " + this.monsters.size());
    }

    public void inflictDamageToPlayer(int damage) { //TODO I'm not sure it's a good way to do it
        this.getPlayer().inflictDamage(damage);
        this.getPlayer().update(0);
    }

    public void inflictDamageToMonster(Monster monster, int damage) { //TODO I'm not sure it's a good way to do it
        monster.inflictDamage(damage);
        monster.update(0);
    }

    public void removeMonster(Monster monster) {
        this.monsters.remove(monster);
    }
}
