/*
 * Copyright (c) 2020. Laurent Réveillère
 */

package fr.ubx.poo.view.sprite;

import static fr.ubx.poo.view.image.ImageResource.*;

import fr.ubx.poo.game.Position;
import fr.ubx.poo.model.decor.*;
import fr.ubx.poo.model.decor.bonus.*;
import fr.ubx.poo.model.go.Bomb;
import fr.ubx.poo.model.decor.Explosion;
import fr.ubx.poo.model.go.character.Monster;
import fr.ubx.poo.model.go.character.Player;
import fr.ubx.poo.view.image.ImageFactory;
import javafx.scene.layout.Pane;


public final class SpriteFactory {

    public static Sprite createDecor(Pane layer, Position position, Decor decor) {
        ImageFactory factory = ImageFactory.getInstance();
        if (decor instanceof Stone)
            return new SpriteDecor(layer, factory.get(STONE), position);
        if (decor instanceof Tree)
            return new SpriteDecor(layer, factory.get(TREE), position);
        if (decor instanceof Box)
            return new SpriteDecor(layer, factory.get(BOX), position);
        if (decor instanceof Key)
            return new SpriteDecor(layer, factory.get(KEY), position);
        if (decor instanceof Heart)
            return new SpriteDecor(layer, factory.get(HEART), position);
        if (decor instanceof BombRangeDec)
            return new SpriteDecor(layer, factory.get(BONUS_BOMB_RANGE_DEC), position);
        if (decor instanceof BombRangeInc)
            return new SpriteDecor(layer, factory.get(BONUS_BOMB_RANGE_INC), position);
        if (decor instanceof BombNumberDec)
            return new SpriteDecor(layer, factory.get(BONUS_BOMB_NUMBER_DEC), position);
        if (decor instanceof BombNumberInc)
            return new SpriteDecor(layer, factory.get(BONUS_BOMB_NUMBER_INC), position);

        if (decor instanceof Door)
            return createDoor(layer, position, (Door) decor);

        if (decor instanceof Princess)
            return new SpriteDecor(layer, factory.get(PRINCESS), position);

        if (decor instanceof Explosion)
            return new SpriteDecor(layer, factory.get(EXPLOSION), position);

        //return null;
        throw new RuntimeException("Unsuported sprite for decor " + decor);
    }

    public static Sprite createPlayer(Pane layer, Player player) {
        return new SpritePlayer(layer, player);
    }

    public static SpriteMonster createMonster(Pane layer, Monster monster) {
        return new SpriteMonster(layer, monster);
    }

    public static  Sprite createDoor(Pane layer, Position position, Door door) {
        return new SpriteDoor(layer, position, door);
    }

    public static SpriteBomb createBomb(Pane layer, Bomb bomb) {
        return new SpriteBomb(layer, bomb);
    }
}
