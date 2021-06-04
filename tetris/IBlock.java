package tetris;

import javafx.scene.paint.Color;

public class IBlock extends Block
{
    IBlock (int x, int y, Color color)
    {
        this.color = color;
        this.x = x;
        this.y = y;
        this.squares = new Square[][]{

            { null, null, null, null },
            { new Square(0, 1, 25, this.color), new Square(1, 1, 25, this.color), new Square(2, 1, 25, this.color), new Square(3, 1, 25, this.color) },
            { null, null, null, null },
            { null, null, null, null }

        };
        this.boundingBoxLength = 4; // perhaps redundant from the 2S arr above
    } 

    IBlock (int x, int y, int squareLength, Color color)
    {
        this.color = color;
        this.x = x;
        this.y = y;
        this.squares = new Square[][]{

            { null, null, null, null },
            { new Square(0, 1, squareLength, this.color), new Square(1, 1, squareLength, this.color), new Square(2, 1, squareLength, this.color), new Square(3, 1, squareLength, this.color) },
            { null, null, null, null },
            { null, null, null, null }

        };
        this.boundingBoxLength = 4; // perhaps redundant from the 2S arr above
    } 
}