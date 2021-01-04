package fr.ubx.poo.model.go;

import fr.ubx.poo.game.Direction;
import fr.ubx.poo.game.Game;
import fr.ubx.poo.game.Position;
import fr.ubx.poo.model.decor.Box;
import fr.ubx.poo.model.decor.Decor;
import fr.ubx.poo.model.decor.bonus.Bonus;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class Bomb extends GameObject {

    private int countdown = 0;
    private int bombRange;
    private boolean isExplode;
    private boolean isDisplayed;

    public Bomb(Game game, Position position) {
        super(game, position);
        this.bombRange = 1;
        this.isExplode = false;
        this.isDisplayed = false;
        launchTask();
    }

    private void launchTask() {
        Timer timer = new Timer("Timer");
        long period = 1000L;

        timer.schedule(new TimerTask() {
            final long t0 = System.currentTimeMillis();
            public void run() {
                if (System.currentTimeMillis() - t0 > 2L * 1000L) { //to check if the timer ends
                    timerEnds();
                    cancel();
                }
                long t = System.currentTimeMillis() - t0;
                countdown = ((int) (t / 1000) + 1);
            }
        }, new Date() , period);
    }

    private void timerEnds() {
        bombExplodes();
    }

    private void bombExplodes() {
        //sout my pos
        System.out.println("=> " + this.getPosition());

        //loop to scan and process the explosion cross
        for(Direction direction : Direction.values()){
            checkExplosionDirection(direction);
        }

        this.isExplode = true;
        System.out.println("bomb explode");

//        this.game.getPlayer().removeBomb(this);
    }

    private void checkExplosionDirection(Direction direction) {
        System.out.println(direction);
        Position pos = getPosition();
        boolean objectAlreadyDestroy = false;

        for(int range=1; range <= this.bombRange; range++){
            pos = direction.nextPosition(pos);
            System.out.println("    >> " + pos);

            if (this.world.isInside(pos)) {
                Decor decor = this.world.get(pos);
                System.out.println("    -->> " + decor);

                //TODO need to find a solution to instanceof check for Decor
                if(decor != null) {
                    if (decor.isDestructible() && !objectAlreadyDestroy) {
                        if (!decor.isTraversable()) { //it wwork for Box and other decor which "stop" explosion ?
                            objectAlreadyDestroy = true;
                        }

                        //TODO I think it's here to begin Explosion Object creation
                        //under, some decor don't have to be destroyed (ex: key), but we can "check" it in overrided method in Key I think
                        //decor.destroy(); //TODO
                        System.out.println("    \\_ destroy");
                    }
                }
                //that's all ?

            }

//            System.out.println("    > "+range);
        }
    }

    public int getCountdown() {
        return countdown;
    }

    public boolean isExplode() {
        return isExplode;
    }

    public boolean isDisplayed() {
        return isDisplayed;
    }

    public void setDisplayed(boolean displayed) {
        isDisplayed = displayed;
    }
}
