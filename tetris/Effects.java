package tetris;

import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.geometry.*;
import javafx.scene.control.Button;
import javafx.scene.effect.*; 

// contains the effects that are used for the GUI
public class Effects 
{
    // create lighting effect for the blocks learnt on https://www.geeksforgeeks.org/javafx-light-point-class/
    // makes them look similar to the blocks in tetris or other retro games
    static Light.Point light = new Light.Point(25.0/2, 25.0/2, 100.0, Color.GAINSBORO);    // creates a light which shines from a point
    public static Lighting lightEffect = new Lighting(light);                              // creates new lighting (javafx thingy)

    static BackgroundFill bgFill = new BackgroundFill(Color.BLACK, CornerRadii.EMPTY, Insets.EMPTY);    // makes a new black bg fill
    public static Background background = new Background(bgFill);                                       // makes a bg with the bg fill above    

    public static String buttonStyle = "-fx-background-color: white; -fx-font-size: 1.5em;";
    public static int buttonWidth = 125;

    public static void styleButton (Button b)
    {
        b.setMinWidth(Effects.buttonWidth);
        b.setStyle(buttonStyle);
    }
}