/*
 * Copyright (c) 2020. Laurent Réveillère
 */

package fr.ubx.poo.engine;

import fr.ubx.poo.game.Direction;
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
    private final List<Sprite> spriteBombs = new ArrayList<>();
    private final ArrayList<Monster> monsters;
    private StatusBar statusBar;
    private Pane layer;
    private Input input;
    private Stage stage;
    private Sprite spritePlayer;

    public GameEngine(final String windowTitle, Game game, final Stage stage) {
        this.windowTitle = windowTitle;
        this.game = game;
        this.player = game.getPlayer();
        this.monsters = game.getMonsters();
        initialize(stage, game);
        buildAndSetGameLoop();
    }

    private void initialize(Stage stage, Game game) {
        this.stage = stage;
        Group root = new Group();
        layer = new Pane();

        int height = game.getWorld().dimension.height;
        int width = game.getWorld().dimension.width;
        int sceneWidth = width * Sprite.size;
        int sceneHeight = height * Sprite.size;
        Scene scene = new Scene(root, sceneWidth, sceneHeight + StatusBar.height);
        scene.getStylesheets().add(getClass().getResource("/css/application.css").toExternalForm());

        stage.setTitle(windowTitle);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();

        input = new Input(scene);
        root.getChildren().add(layer);
        statusBar = new StatusBar(root, sceneWidth, sceneHeight, game);
        // Create decor sprites
        game.getWorld().forEach((pos, d) -> sprites.add(SpriteFactory.createDecor(layer, pos, d)));
        monsters.forEach((monster) -> spriteMonsters.add(SpriteFactory.createMonster(layer, monster)));

        spritePlayer = SpriteFactory.createPlayer(layer, player);
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
        if (input.isBomb()) {
//            Bomb bomb = new Bomb(game, player.getPosition());
//            this.game.getWorld().addGameObject(bomb);
            // problem here because bomb is not in the grid // BUT IT MAYBE NOT NECESSARY

//            this.player.addBomb(bomb);
//            spriteBombs.add(SpriteFactory.createBomb(layer, bomb));

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

    private void update(long now) {
        //TODO is it a good idea to factorise all player update of this method ?
        if (player.isUpdateSprites()) {
            updateSprites();
            player.setUpdateSprites(false);
        }

        player.update(now);

        if (!player.isAlive()) {
            gameLoop.stop();
            showMessage("Perdu!", Color.RED);
        }
        if (player.isWinner()) {
            gameLoop.stop();
            showMessage("Gagné", Color.BLUE);
        }

        updateBombs();
    }

    /**
     * To create sprite and/or to update bomb's lists and sprites
     * <p>
     * for update sprite BUT it doesn't work and i dont understand why
     * it seems spritesBombs could be empty and still got a bomb's sprite and i dont know how it possible
     */
    private void updateBombs() {
//        if(!this.spriteBombs.isEmpty()) { //i dont know which one is the best | it'is in order to not do the loop each frame
        if (!this.player.getBombs().isEmpty()) {

            Iterator<Bomb> bombIterator = this.player.getBombs().iterator();
//            List<Bomb> newBombs = new ArrayList<>();
            List<Bomb> newBombs = this.player.getBombs();

            while (bombIterator.hasNext()) {
                Bomb bomb = bombIterator.next();

                //to update bomb's list and sprites
                if (bomb.isExplode()) {
                    this.spriteBombs.clear();//TODO change that

                    bombIterator.remove();
//                    newBombs.add(bomb);
                    newBombs.remove(bomb);

                    updateSprites();

                } else {//to create sprites
//                    this.player.addBomb(bomb);
                    spriteBombs.add(SpriteFactory.createBomb(layer, bomb));
                }
            }

            this.player.setBombs(newBombs);

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
