package tetris;

import java.net.URL;
import java.util.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.media.AudioClip;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.text.Font;

public class Board 
{

    GridPane board = new GridPane();
    Label score;
    Label topScore;
    Label lines;
    Label level;
    final int BOARD_WIDTH = 10;
    final int BOARD_HEIGHT = 24;
    boolean soundOn;

    // line clear
    URL lineClearPath = getClass().getResource("\\lineClear.mp3");
    public AudioClip lineClear = new AudioClip(lineClearPath.toString());
    // tetris clear
    URL tetrisClearPath = getClass().getResource("\\tetrisClear.mp3");
    public AudioClip tetrisClear = new AudioClip(tetrisClearPath.toString());

    Board(int highScore, boolean soundOn)
    {
        // set the backgorund colour of the gridpane to be black by default
        board.setBackground(Effects.background);
        
        // add the grey border
        addBorder();

        // add info labels like score, top, etc.
        addLabels(highScore);

        // set the gap between squares to be 0
        board.setHgap(0);
        board.setVgap(0);

        this.soundOn = soundOn;
    }

    GridPane getBoard ()
    {
        return this.board;
    }

    Label getScore ()
    {
        return this.score;
    }

    Label getTopScore ()
    {
        return this.topScore;
    }

    void addBorder ()
    {
        // create the grey border around the playfield by adding grey squares to the edges.
        for (int i = 0; i < BOARD_WIDTH + 2; i++)
        {
            for (int j = 0; j < BOARD_HEIGHT + 2; j++)
            {
                if (i == 0 || i == 11 || j == 0 || j == 25)
                {
                    Rectangle rect = new Rectangle(25, 25, Color.web("#6e6e6e")); // HEX code is from web
                    rect.setEffect(Effects.lightEffect); // this light effects gives the old retro block feel
                    board.getChildren().add(rect);
                    board.setConstraints(rect, i, j);
                }
            }
        }
    }

    void addLabels (int highScore)
    {
        // add all the info labels on the right side of the screen for top score and score as well as the next piece
        Font tetrisFont = Font.loadFont(getClass().getResourceAsStream("\\tetrisFont.ttf"), 11); // create a font from the file tetrisFont.ttf as found on http://java-buddy.blogspot.com/2013/02/load-true-type-font-ttf-in-javafx.html
        // above font's licence
        /* 
            The FontStruction “Tetris” (https://fontstruct.com/fontstructions/show/1102867) by Patrick Lauke
            is licensed under a Creative Commons Attribution license (http://creativecommons.org/licenses/by/3.0/).
        */

        Label topScoreTitle = new Label("Top"); // create the label
        topScoreTitle.setFont(tetrisFont); // set the font
        topScoreTitle.setTextFill(Color.WHITE); // set the font colour
        
        String strHighScore = Integer.toString(highScore);
        while(strHighScore.length() < 9)
        {
            strHighScore = "0" + strHighScore;
        }
        this.topScore = new Label(strHighScore);
        this.topScore.setFont(tetrisFont);
        this.topScore.setTextFill(Color.WHITE);
        
        Label scoreTitle = new Label("Score");
        scoreTitle.setFont(tetrisFont);
        scoreTitle.setTextFill(Color.WHITE);

        this.score = new Label("000000000");
        this.score.setFont(tetrisFont);
        this.score.setTextFill(Color.WHITE);

        Label next = new Label("Next");
        next.setFont(tetrisFont);
        next.setTextFill(Color.WHITE);

        Label levelLabel = new Label("Level");
        levelLabel.setFont(tetrisFont);
        levelLabel.setTextFill(Color.WHITE);

        this.level = new Label("001");
        this.level.setFont(tetrisFont);
        this.level.setTextFill(Color.WHITE);

        Label linesLabel = new Label("Lines");
        linesLabel.setFont(tetrisFont);
        linesLabel.setTextFill(Color.WHITE);

        this.lines = new Label("000");
        this.lines.setFont(tetrisFont);
        this.lines.setTextFill(Color.WHITE);

        Rectangle filler = new Rectangle(15, 15, Color.BLACK); // random filler for formatting

        board.getChildren().addAll(topScoreTitle, this.topScore, scoreTitle, this.score, next, filler, levelLabel, linesLabel, this.lines, this.level);
        board.setConstraints(topScoreTitle, 13, 3);
        board.setConstraints(topScore, 13, 4);
        board.setConstraints(scoreTitle, 13, 6);
        board.setConstraints(score, 13, 7);
        board.setConstraints(next, 13, 9);
        board.setConstraints(filler, 12, 10);
        board.setConstraints(levelLabel, 13, 12);
        board.setConstraints(this.level, 13, 13);
        board.setConstraints(linesLabel, 13, 15);
        board.setConstraints(this.lines, 13, 16);
        
    }

    boolean isContentNotInBlock (int x, int y, Block block) // checks if there are contents in the gridpane at given x and y in the playfield
    {
        for (Node n : this.board.getChildren())
        {
            if (this.board.getColumnIndex(n) == x && this.board.getRowIndex(n) == y)
            {
                if (!blockContainsNode(n, block) && n != null) // if the found node is not in the block and not null, then it will be hit if we shift
                {
                    return true;
                }
            }
        }
        return false;
    }

    boolean blockContainsNode (Node n, Block block) // check if the node found in isContent method is part of the block. if it is, we don't count it for collision detection
    {
        for (int i = 0; i < block.squares.length; i++)
        {
            for (int j = 0; j < block.squares.length; j++)
            {
                if (block.squares[i][j] != null && n == block.squares[i][j].getSquare())
                {
                    return true;
                }
            }
        }
        return false;
    }

    Node getContent (int x, int y) // get the Node at the given x and y in the gridpane
    {
        for (Node n : board.getChildren())
        {
            if (this.board.getColumnIndex(n) == x && this.board.getRowIndex(n) == y)
            {
                return n;
            }
        }
        return null;
    }

    int[] checkForAndProcessLines (int level)
    {
        int score = 0;
        int numCompletedLines = 0; // will count number of completed lines
        List<Integer> completedLines = new ArrayList<Integer>(); // list of the indexes of those lines

        // look through the board. if a row is found with no null cells, add one to counter and add the index to the list
        for (int i = 0; i < BOARD_HEIGHT; i++)
        {
            boolean foundNullCell = false;
            for (int j = 0; j < BOARD_WIDTH; j++)
            {
                if (getContent(j+1, i+1) == null)
                {
                    foundNullCell = true;
                }
            }
            if (!foundNullCell)
            {
                numCompletedLines++;
                completedLines.add(i);
            }
        }

        // update the score
        switch (numCompletedLines)
        {
            case 1:
                score += 40 * (level + 1);
                break;
            case 2:
                score += 100 * (level + 1);
                break;
            case 3:
                score += 300 * (level + 1);
                break;
            case 4:
                score += 1200 * (level + 1);
                break;
        }

        if (numCompletedLines > 0)
        {
            incrementLinesLabel(numCompletedLines);
            if (soundOn)
            { 
                if (numCompletedLines < 4){ playLineClear(); }
                else { playTetrisClear(); } 
            }
            // remove the lines from the board
            for (int i = 0; i < completedLines.size(); i++)
            {
                // removes the squares from the rows that are filled and shift the lines above down
                removeLine(completedLines.get(i));
                shiftLinesAboveDown(completedLines.get(i));
            } 
        }

        return new int[]{ numCompletedLines, score }; // return needed info
    }

    void removeLine (int row)
    {
        for (int j = 0; j < BOARD_WIDTH; j++)
        {
            this.board.getChildren().remove(getContent(j+1, row+1));
        }
    }

    void shiftLinesAboveDown (int row)
    {
        for (int i = row-1; i >= 0; i--)
        {
            for (int j = 0; j < BOARD_WIDTH; j++)
            {
                Node n = getContent(j+1, i+1);
                if (n != null)
                {
                    this.board.setConstraints(n, j+1, i+2);
                }
            }
        }
    }

    // check if the block can rotate clockwise BUGGED
    boolean canRotateBlockClockwise (Block block)
    {
        for (int i = 0; i < block.boundingBoxLength; i++)
        {
            for (int j = 0; j < block.boundingBoxLength; j++)
            {
                if (block.squares[i][j] != null)
                {
                    if (isContentNotInBlock(block.boundingBoxLength-i+block.getX(), j+1+block.getY(), block))
                    {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    boolean canRotateBlockCounterclockwise (Block block)
    {
        for (int i = 0; i < block.boundingBoxLength; i++)
        {
            for (int j = 0; j < block.boundingBoxLength; j++)
            {
                if (block.squares[i][j] != null)
                {
                    if (isContentNotInBlock(i+1+block.getX(), block.boundingBoxLength-j+block.getY(), block))
                    {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    //check if there is room to spawn a block. if not, we will end the game
    boolean canSpawnBlock (Block block)
    {
        for (int i = 0; i < block.squares.length; i++)
        {
            for (int j = 0; j < block.squares.length; j++)
            {
                if (block.squares[i][j] != null)
                {
                    if (getContent(j+1+block.getX(), i+1+block.getY()) != null) // if there is something in that space
                    {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    // spawns the block onto the board
    void spawnBlock (Block block)
    {
        for (int i = 0; i < block.squares.length; i++)
        {
            for (int j = 0; j < block.squares.length; j++)
            {
                if (block.squares[i][j] != null)
                {
                    this.board.getChildren().add(block.squares[i][j].getSquare());
                    this.board.setConstraints(block.squares[i][j].getSquare(), j+1+block.getX(), i+1+block.getY());
                }
            }
        }
    }

    public void drawBlock (Block block)
    {
        for (int i = 0; i < block.squares.length; i++) // go through the x and y coords of the block
        {
            for (int j = 0; j < block.squares.length; j++)
            {
                if (block.squares[i][j] != null)
                {
                    // reset the position of the squares that make up the block
                    this.board.setConstraints(block.squares[i][j].getSquare(), j+1+block.getX(), i+1+block.getY());
                }
            }
        }
    }

    void shiftBlock (Block block, int xChange, int yChange) // shift the block down. if not possible, return false. else, return true
    {
        // shift the coords of the block according to x and y change
        block.setX(block.getX()+xChange);
        block.setY(block.getY()+yChange); 

        // shift the block on the gridpane
        drawBlock(block);
    }

    boolean canShiftBlock (Block block, int xChange, int yChange)
    {
        // check if block can't shift
        for (int i = 0; i < block.squares.length; i++)
        {
            for (int j = 0; j < block.squares.length; j++)
            {
                if (block.squares[i][j] != null)
                {
                    if (isContentNotInBlock(j+1+xChange+block.getX(), i+1+yChange+block.getY(), block))
                    {
                        return false; // we can't shift the block down. it will hit something.
                    }
                }
            }
        }

        return true;
    }

    void setNext (List<Integer> order, int orderIndex, Icons icons)
    {
        GridPane icon = new GridPane();

        switch (order.get(orderIndex))
        {
            case 0: // i
                icon = icons.getIBlock();
                break;
            case 1: // j
                icon = icons.getJBlock();
                break; 
            case 2: // l
                icon = icons.getLBlock();
                break; 
            case 3: // s
                icon = icons.getSBlock();
                break;
            case 4: // z
                icon = icons.getZBlock();
                break;
            case 5: // t
                icon = icons.getTBlock();
                break;
            case 6: // o
                icon = icons.getOBlock();
                break;
        }

        this.board.getChildren().remove(getContent(13, 10));
        this.board.getChildren().add(icon);
        this.board.setConstraints(icon, 13, 10);
    }

    public void playLineClear ()
    {
        lineClear.play();
    }

    public void playTetrisClear ()
    {
        tetrisClear.setVolume(0.7);
        tetrisClear.play();
    }

    void incrementLevelLabel () 
    {
        int curLevel = Integer.valueOf(this.level.getText());
        String newText = Integer.toString(++curLevel);
        
        while (newText.length() < 3)
        {
            newText = "0" + newText;
        }
        
        this.level.setText(newText);
    }
    
    public void incrementLinesLabel(int x)
    {
        int curLines = Integer.valueOf(this.lines.getText());
        String newText = Integer.toString(curLines + x);
        
        while (newText.length() < 3)
        {
            newText = "0" + newText;
        }
        
        this.lines.setText(newText);
    }
}