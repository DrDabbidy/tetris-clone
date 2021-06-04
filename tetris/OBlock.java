package tetris;

import javafx.scene.paint.Color;

public class OBlock extends Block
{
    OBlock (int x, int y, Color color)
    {
        this.color = color;
        this.x = x;
        this.y = y;
        this.squares = new Square[][]{

            { new Square(0, 0, 25, this.color), new Square(1, 0, 25, this.color) },
            { new Square(0, 1, 25, this.color), new Square(1, 1, 25, this.color) },

        };
        this.boundingBoxLength = 2; // perhaps redundant from the 2S arr above
    }

    OBlock (int x, int y, int squareLength, Color color)
    {
        this.color = color;
        this.x = x;
        this.y = y;
        this.squares = new Square[][]{

            { new Square(0, 0, squareLength, this.color), new Square(1, 0, squareLength, this.color) },
            { new Square(0, 1, squareLength, this.color), new Square(1, 1, squareLength, this.color) },

        };
        this.boundingBoxLength = 2; // perhaps redundant from the 2S arr above
    }
}