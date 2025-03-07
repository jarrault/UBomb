/*
 * Copyright (c) 2020. Laurent Réveillère
 */

package fr.ubx.poo.engine;

import fr.ubx.poo.game.*;
import fr.ubx.poo.model.go.Bomb;
import fr.ubx.poo.model.go.character.Monster;
import fr.ubx.poo.view.sprite.*;
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
    private final List<SpriteMonster> spriteMonsters = new ArrayList<>();
    private final List<SpriteBomb> spriteBombs = new ArrayList<>();
    private List<Monster> monsters;
    private StatusBar statusBar;
    private Pane layer;
    private Input input;
    private final Stage stage;
    private SpritePlayer spritePlayer;

    public GameEngine(final String windowTitle, Game game, final Stage stage) {
        this.windowTitle = windowTitle;
        this.game = game;
        this.player = game.getPlayer();
        this.monsters = game.getMonsters();
        this.stage = stage;
        initialize();
        buildAndSetGameLoop();
    }

    /**
     * To initialize important elements (example : Scene, sprites, ...)
     */
    private void initialize() {
        this.updateScene();
        this.updateSprites();

        stage.setTitle(windowTitle);
        stage.setResizable(false);
        stage.show();
    }

    /**
     * To build and set game loop
     */
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
     *
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

    /**
     * To show ending message
     *
     * @param msg   content of the message
     * @param color color of the message
     */
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

    /**
     * To update common sprites ( but not monsters, player and bombs sprites)
     */
    private void updateSprites() {
        sprites.forEach(Sprite::remove);
        sprites.clear();
        game.getWorld().forEach((pos, d) -> sprites.add(SpriteFactory.createDecor(layer, pos, d)));
    }

    /**
     * To update the scene
     */
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

        // Update monsters list
        this.monsters = new ArrayList<>(this.game.getMonsters());

        // Create Monsters sprites
        this.spriteMonsters.clear();
        monsters.forEach((monster) -> spriteMonsters.add(SpriteFactory.createMonster(layer, monster)));

        //Create Player sprite
        spritePlayer = (SpritePlayer) SpriteFactory.createPlayer(layer, player);
    }

    /**
     * To update the game at each frame
     *
     * @param now the timestamp of the current frame given in nanoseconds.
     */
    private void update(long now) {
        // Update the game when the level changes
        if (this.game.isLevelChange()) {
            this.game.setLevelChange(false);
            this.game.updateScene();
            updateScene();
            updateSprites();
        }

        player.update(now);
        checkIfGameIsOver();

        updateMonsters(now);
        updateBombs(now);
        updateSprites();
    }

    /**
     * To update monsters' logic and sprites
     *
     * @param now the timestamp of the current frame given in nanoseconds.
     */
    private void updateMonsters(long now) {
        Iterator<Monster> monsterIterator = this.monsters.iterator();

        while (monsterIterator.hasNext()) {
            Monster monster = monsterIterator.next();

            monster.update(now);

            if (!monster.isAlive()) {
                this.game.removeMonster(monster);

                monsterIterator.remove();

                // Get the sprite that match with the monster which died
                Optional<SpriteMonster> monsterSprite = spriteMonsters.stream().filter(m -> m.getGo().equals(monster)).findFirst();

                // Remove the sprite from the layer and remove it from the Sprites list
                monsterSprite.ifPresent(Sprite::remove);
                monsterSprite.ifPresent(spriteMonsters::remove);
            }
        }
    }

    /**
     * to check if the player win the game or lose it
     */
    private void checkIfGameIsOver() {
        if (!player.isAlive()) {
            gameLoop.stop();
            showMessage("Perdu!", Color.RED);
        }
        if (player.isWinner()) {
            gameLoop.stop();
            showMessage("Gagné", Color.BLUE);
        }
    }

    /**
     * updateBombs loop over all the bombs and update it's states.
     * 1. Add the sprite of a bomb on the layer if a bomb is posed
     * 2. Remove the sprite of the bomb from the layer if it as explode
     *
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

                // Update sprite of entities which could be destroyed by bomb
                updateSprites();
            }
        }
    }

    /**
     * To render sprites
     */
    private void render() {
        sprites.forEach(Sprite::render);
        spriteMonsters.forEach(Sprite::render);
        spriteBombs.forEach(Sprite::render);

        // last rendering to have player in the foreground
        if (player.isInvincible()) {
            spritePlayer.render(0.5);
        } else {
            spritePlayer.render();
        }
    }

    /**
     * to start the game loop
     */
    public void start() {
        gameLoop.start();
    }
}
