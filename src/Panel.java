import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.function.IntFunction;
import javax.swing.*;

public class Panel extends JPanel implements Runnable{

    static final int gameWidth = 1000;
    static final int gameHeight = (int)(gameWidth * (0.5555));
    static final Dimension screenSize = new Dimension(gameWidth, gameHeight);
    static final int ballDiameter = 20;
    static final int paddleWidth = 25;
    static final int paddleHeight = 100;

    Thread gameThread;
    Image image;
    Graphics graphics;
    Random random;
    Paddle paddleA;
    Paddle paddleB;
    Ball ball;
    Score score;

    Panel(){
        newPaddles();
        newBall();
        score = new Score(gameWidth, gameHeight);
        this.setFocusable(true);
        this.addKeyListener(new AL());
        this.setPreferredSize(screenSize);

        gameThread = new Thread(this);
        gameThread.start();
    }

    public void newBall(){
        random = new Random();
        ball = new Ball((gameWidth/2) - (ballDiameter/2), random.nextInt(gameHeight - ballDiameter), ballDiameter, ballDiameter);
    }

    public void newPaddles(){
        paddleA = new Paddle(0, (gameHeight/2) - (paddleHeight/2), paddleWidth, paddleHeight, 1);
        paddleB = new Paddle(gameWidth - paddleWidth, (gameHeight/2) - (paddleHeight/2), paddleWidth, paddleHeight, 2);
    }

    public void draw(Graphics g){
        paddleA.draw(g);
        paddleB.draw(g);
        ball.draw(g);
        score.draw(g);
    }

    public void paint(Graphics g){
        image = createImage(getWidth(), getHeight());
        graphics = image.getGraphics();
        draw(graphics);
        g.drawImage(image, 0, 0, this);
    }

    public void move(){
        paddleA.move();
        paddleB.move();
        ball.move();
    }

    public void checkCollision(){

        //bounce ball off top & bottom edges
        if (ball.y <= 0){
            ball.setYDirection(-ball.velocityY);
        }
        if (ball.y >= gameHeight - ballDiameter){
            ball.setYDirection(-ball.velocityY);
        }

        //bounce ball off paddles
        if (ball.intersects(paddleA)){
            ball.velocityX = Math.abs(ball.velocityX);
            ball.velocityX++; //More difficulty

            if (ball.velocityY > 0){
                ball.velocityY++; //More difficulty
            }
            else {
                ball.velocityY--;
            }
            ball.setXDirection(ball.velocityX);
            ball.setYDirection(ball.velocityY);
        }

        if (ball.intersects(paddleB)){
            ball.velocityX = Math.abs(ball.velocityX);
            ball.velocityX++; //More difficulty

            if (ball.velocityY > 0){
                ball.velocityY++; //More difficulty
            }
            else {
                ball.velocityY--;
            }
            ball.setXDirection(-ball.velocityX);
            ball.setYDirection(ball.velocityY);
        }

        //Stopping paddles at window edges
        if (paddleA.y <= 0){
            paddleA.y = 0;
        }
        if (paddleA.y >= (gameHeight - paddleHeight)){
            paddleA.y = gameHeight - paddleHeight;
        }
        if (paddleB.y <= 0){
            paddleB.y = 0;
        }
        if (paddleB.y >= (gameHeight - paddleHeight)){
            paddleB.y = gameHeight - paddleHeight;
        }

        //Giving 1 point and creating new paddles and ball
        if (ball.x <= 0){
            score.player2++;
            newPaddles();
            newBall();
            System.out.println("Player 2:" + score.player2);
        }
        if (ball.x >= gameWidth - ballDiameter){
            score.player1++;
            newPaddles();
            newBall();
            System.out.println("Player 1:" + score.player1);
        }
    }

    public void run(){
        long lastTime = System.nanoTime();
        double amountOfTicks = 60.0;
        double ns = 1000000000 / amountOfTicks;
        double delta = 0;

        while (true){
            long now = System.nanoTime();
            delta += (now - lastTime)/ns;
            lastTime = now;
            if (delta >= 1){
                move();
                checkCollision();
                repaint();
                delta--;
            }
        }
    }

    public class AL extends KeyAdapter{
        @Override
        public void keyPressed(KeyEvent e) {
            paddleA.keyPressed(e);
            paddleB.keyPressed(e);
        }

        @Override
        public void keyReleased(KeyEvent e) {
            paddleA.keyReleased(e);
            paddleB.keyReleased(e);
        }
    }
}
