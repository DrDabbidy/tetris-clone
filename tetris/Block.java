package tetris;

import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;

// superclass for tetrominoes. Consists of an array of cubes which make up the tetrominoe and a 
// position measured from the top left of a 4x4 bounding box
public class Block 
{
    public Square[][] squares;
    int boundingBoxLength;
    int x;
    int y;
    Color color;

    public int getX()
    {
        return this.x;
    }

    public int getY()
    {
        return this.y;
    }

    public void setX(int x)
    {
        this.x = x;
    }

    public void setY(int y)
    {
        this.y = y;
    }

    public void draw(GridPane grid)
    {
        for (int i = 0; i < this.squares.length; i++) // go through the x and y coords of the block
        {
            for (int j = 0; j < this.squares.length; j++)
            {
                if (this.squares[i][j] != null)
                {
                    // reset the position of the squares that make up the block
                    grid.setConstraints(this.squares[i][j].getSquare(), j+1+this.getX(), i+1+this.getY());
                }
            }
        }
    }

    public void rotateClockwise() // rotate the block clockwise
    {
        Square[][] temp = new Square[this.boundingBoxLength][this.boundingBoxLength];

        for (int i = 0; i < this.boundingBoxLength; i++)
        {
            for (int j = 0; j < this.boundingBoxLength; j++)
            {
                temp[j][this.boundingBoxLength-1-i] = this.squares[i][j];
            }
        }

        this.squares = temp;
    }
    
    public void rotateCounterclockwise() // rotate the block coutnerclockwise
    {
        Square[][] temp = new Square[this.boundingBoxLength][this.boundingBoxLength];

        for (int i = 0; i < this.boundingBoxLength; i++)
        {
            for (int j = 0; j < this.boundingBoxLength; j++)
            {
                temp[this.boundingBoxLength-1-j][i] = this.squares[i][j];
            }
        }
        
        this.squares = temp;
    }

}