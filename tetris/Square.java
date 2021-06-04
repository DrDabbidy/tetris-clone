package tetris;

import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.effect.*; 

// creates a rectangle with coords to position it.
public class Square 
{
    Square (int x, int y, int len, Color color)
    {
        this.x = x;
        this.y = y;
        this.square = new Rectangle(len, len, color);

        // set the lighting on the square. see game class
        Light.Point light = new Light.Point(25.0/2, 25.0/2, 35.0, Color.WHITE);
        Lighting lightEffect = new Lighting();
        lightEffect.setLight(light);

        this.square.setEffect(lightEffect);
    }

    int x;
    int y;
    Rectangle square;

    public int getX()
    {
        return this.x;
    }

    public int getY()
    {
        return this.y;
    }

    public Rectangle getSquare()
    {
        return this.square;
    }

    public void setX(int x)
    {
        this.x = x;
    }

    public void setY(int y)
    {
        this.y = y;
    }

    public void setSquare(Rectangle square)
    {
        this.square = square;
    }
}