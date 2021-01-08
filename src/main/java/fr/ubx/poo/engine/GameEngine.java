/*
 * Copyright (c) 2020. Laurent Réveillère
 */

package fr.ubx.poo.engine;

import fr.ubx.poo.game.Direction;
import fr.ubx.poo.game.PositionNotFoundException;
import fr.ubx.poo.game.World;
import fr.ubx.poo.model.go.Bomb;
import fr.ubx.poo.model.go.character.Monster;
import fr.ubx.poo.view.sprite.Sprite;
import fr.ubx.poo.view.sprite.SpriteBomb;
import fr.ubx.poo.view.sprite.SpriteFactory;
import fr.ubx.poo.game.Game;
import fr.ubx.poo.model.go.character.Player;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

import java.util.*;


public final class GameEngine {

    private static AnimationTimer gameLoop;
    private final String windowTitle;
    private final Game game;
    private final Player player;
    private final List<Sprite> sprites = new ArrayList<>();
    private final List<Sprite> spriteMonsters = new ArrayList<>();
    private final List<SpriteBomb> spriteBombs = new ArrayList<>();
    private final ArrayList<Monster> monsters;
    private StatusBar statusBar;
    private Pane layer;
    private Input input;
    private Stage stage;
    private Sprite spritePlayer;
    private Sprite spriteMonster;

    public GameEngine(final String windowTitle, Game game, final Stage stage) {
        this.windowTitle = windowTitle;
        this.game = game;
        this.player = game.getPlayer();
        this.monsters = game.getMonsters();
        this.stage = stage;
        initialize();
        buildAndSetGameLoop();
    }

    private void initialize() {
        this.updateScene();
        this.updateSprites();

        stage.setTitle(windowTitle);
        stage.setResizable(false);
        stage.show();
    }

    protected final void buildAndSetGameLoop() {
        gameLoop = new AnimationTimer() {
            public void handle(long now) {
                // Check keyboard actions
                processInput(now);

                // Do actions
                update(now);

                // Graphic update
                render();
                statusBar.update(game);
            }
        };
    }

    /**
     * To process keys input
     * @param now the timestamp of the current frame given in nanoseconds.
     */
    private void processInput(long now) {
        if (input.isExit()) {
            gameLoop.stop();
            Platform.exit();
            System.exit(0);
        }
        if (input.isMoveDown()) {
            player.requestMove(Direction.S);
        }
        if (input.isMoveLeft()) {
            player.requestMove(Direction.W);
        }
        if (input.isMoveRight()) {
            player.requestMove(Direction.E);
        }
        if (input.isMoveUp()) {
            player.requestMove(Direction.N);
        }
        if (input.isKey()) {
            //when player press ENTER he try to open a door
            player.requestOpenDoor();
        }
        if (input.isBomb()) {
            this.player.requestBomb();
        }

        input.clear();
    }

    private void showMessage(String msg, Color color) {
        Text waitingForKey = new Text(msg);
        waitingForKey.setTextAlignment(TextAlignment.CENTER);
        waitingForKey.setFont(new Font(60));
        waitingForKey.setFill(color);
        StackPane root = new StackPane();
        root.getChildren().add(waitingForKey);
        Scene scene = new Scene(root, 400, 200, Color.WHITE);
        stage.setTitle(windowTitle);
        stage.setScene(scene);
        input = new Input(scene);
        stage.show();
        new AnimationTimer() {
            public void handle(long now) {
                processInput(now);
            }
        }.start();
    }

    private void updateSprites() {
        sprites.forEach(Sprite::remove);
        sprites.clear();
        game.getWorld().forEach((pos, d) -> sprites.add(SpriteFactory.createDecor(layer, pos, d)));
    }

    private void updateScene() {
        Group root = new Group();
        layer = new Pane();

        int height = game.getWorld().dimension.height;
        int width = game.getWorld().dimension.width;
        int sceneWidth = width * Sprite.size;
        int sceneHeight = height * Sprite.size;

        Scene scene = new Scene(root, sceneWidth, sceneHeight + StatusBar.height);
        scene.getStylesheets().add(getClass().getResource("/css/application.css").toExternalForm());

        stage.setScene(scene);

        input = new Input(scene);
        root.getChildren().add(layer);
        statusBar = new StatusBar(root, sceneWidth, sceneHeight, game);

        // Create Monsters sprites
        monsters.forEach((monster) -> spriteMonsters.add(SpriteFactory.createMonster(layer, monster)));

        //Create Player sprite
        spritePlayer = SpriteFactory.createPlayer(layer, player);
    }

    /**
     * To update the game at each frame
     * @param now the timestamp of the current frame given in nanoseconds.
     */
    private void update(long now) {
        //update the game when the level changes
        if (this.game.isLevelChange()) {
            monsters.clear();//TODO maybe do it somewhere else
            this.game.setLevelChange(false);
            this.game.updateScene();
            updateScene();
            updateSprites();
        }

        player.update(now);

        monsters.forEach((monster) -> monster.update(now) );

        if (!player.isAlive()) {
            gameLoop.stop();
            showMessage("Perdu!", Color.RED);
        }
        if (player.isWinner()) {
            gameLoop.stop();
            showMessage("Gagné", Color.BLUE);
        }

        updateBombs(now);
        updateSprites();
    }

    /**
     * updateBombs loop over all the bombs and update it's states.
     * 1. Add the sprite of a bomb on the layer if a bomb is posed
     * 2. Remove the sprite of the bomb from the layer if it as explode

     * @param now the timestamp of the current frame given in nanoseconds.
     */
    private void updateBombs(long now) {
        Iterator<Bomb> bombIterator = this.player.getBombs().iterator();

        while (bombIterator.hasNext()) {
            Bomb bomb = bombIterator.next();

            //update bomb's logic
            bomb.update(now);

            //if need to create the bomb
            if (!bomb.isDisplayed()) {
                spriteBombs.add(SpriteFactory.createBomb(layer, bomb));
                bomb.setDisplayed(true);
            }

            //if the bomb ends and need to be removed
            if (bomb.isExplode()) {
                //to remove from the bombs' list
                bombIterator.remove();

                // Get the sprite that match with the bomb that explode
                Optional<SpriteBomb> bombSprite = spriteBombs.stream().filter(b -> b.getGo().equals(bomb)).findFirst();

                // Remove the sprite from the layer and remove it from the Sprites list
                bombSprite.ifPresent(Sprite::remove);
                bombSprite.ifPresent(spriteBombs::remove);

                //to update sprite of entites which could be destroyed by bomb
                updateSprites();
            }
        }
    }

    private void render() {
        sprites.forEach(Sprite::render);
        spriteMonsters.forEach(Sprite::render);
        spriteBombs.forEach(Sprite::render);
        // last rendering to have player in the foreground
        spritePlayer.render();
    }

    public void start() {
        gameLoop.start();
    }
}
