package tetris;

import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;

public class Icons 
{
    GridPane iBlock = new GridPane();
    GridPane jBlock = new GridPane();
    GridPane lBlock = new GridPane();
    GridPane sBlock = new GridPane();
    GridPane zBlock = new GridPane();
    GridPane oBlock = new GridPane();
    GridPane tBlock = new GridPane();

    Icons (Color[] colors)
    {
        spawn(new IBlock(0,0, 12, colors[0]), iBlock);
        spawn(new JBlock(0,0, 12, colors[1]), jBlock);
        spawn(new LBlock(0,0, 12, colors[2]), lBlock);
        spawn(new SBlock(0,0, 12, colors[3]), sBlock);
        spawn(new ZBlock(0,0, 12, colors[4]), zBlock);
        spawn(new TBlock(0,0, 12, colors[5]), tBlock);
        spawn(new OBlock(0,0, 12, colors[6]), oBlock);
    }

    public GridPane getIBlock()
    {
        return iBlock;
    }

    public GridPane getJBlock()
    {
        return jBlock;   
    }

    public GridPane getLBlock()
    {
        return lBlock;   
    }

    public GridPane getSBlock()
    {
        return sBlock;
    }
    
    public GridPane getZBlock()
    {
        return zBlock;
    }

    public GridPane getOBlock()
    {
        return oBlock;
    }

    public GridPane getTBlock()
    {
        return tBlock;
    }

    void spawn (Block block, GridPane board)
    {
        for (int i = 0; i < block.squares.length; i++)
        {
            for (int j = 0; j < block.squares.length; j++)
            {
                if (block.squares[i][j] != null)
                {
                    board.getChildren().add(block.squares[i][j].getSquare());
                    board.setConstraints(block.squares[i][j].getSquare(), j+1+block.getX(), i+1+block.getY());
                }
            }
        }

    }
}