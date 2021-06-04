package tetris;

import javafx.scene.paint.Color;

public class TBlock extends Block
{
    TBlock (int x, int y, Color color)
    {
        this.color = color;
        this.x = x;
        this.y = y;
        this.squares = new Square[][]{

            { null, new Square(1, 0, 25, this.color), null },
            { new Square(0, 1, 25, this.color), new Square(1, 1, 25, this.color), new Square(2, 1, 25, this.color) },
            { null, null, null },

        };
        this.boundingBoxLength = 3; // perhaps redundant from the 2S arr above
    } 
    
    TBlock (int x, int y, int squareLength, Color color) 
    {
        this.color = color;
        this.x = x;
        this.y = y;
        this.squares = new Square[][]{

            { null, new Square(1, 0, squareLength, this.color), null },
            { new Square(0, 1, squareLength, this.color), new Square(1, 1, squareLength, this.color), new Square(2, 1, squareLength, this.color) },
            { null, null, null },

        };
        this.boundingBoxLength = 3; // perhaps redundant from the 2S arr above
    } 
}