package fr.ubx.poo.model.go;

import fr.ubx.poo.game.Game;
import fr.ubx.poo.game.Position;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class Bomb extends GameObject {

    private int countdown = 0;

    public Bomb(Game game, Position position) {
        super(game, position);
        launchTask();
    }

    private void launchTask() {
        Timer timer = new Timer("Timer");
        long period = 1000L;

        timer.schedule(new TimerTask() {
            final long t0 = System.currentTimeMillis();
            public void run() {
                if (System.currentTimeMillis() - t0 > 2L * 1000L) {
                    cancel();
                }
                long t = System.currentTimeMillis() - t0;
                countdown = ((int) (t / 1000) + 1);
            }
        }, new Date() , period);
    }

    public int getCountdown() {
        return countdown;
    }
}
