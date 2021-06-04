/* Main game class for Tetris. Learnt JavaFX from YouTuber "thenewboston" and packages from Gaet... ask him. Also, referenced 
 * websites are commented throughout. This was mostly to learn how to do specific things in javafx but never pertained directly to
 * Tetris or how to implement the game's logic. Further, I always customized the code and never copy-and-pasted any code into my project.
 * David De Martin
 * May 14, 2020
 */

package tetris;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import javafx.animation.AnimationTimer;
import javafx.application.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import java.net.URL;
import javafx.scene.media.AudioClip;

// Game class containing the Game methods
public class Game extends Application
{
    //////////////////////////////////////////////////////////////////////
    ////////////////////////////// GAME VARS /////////////////////////////
    //////////////////////////////////////////////////////////////////////

    Scene gameScene;

    Board board;
    Block currentBlock; // current block we are manipulating and using
    
    boolean gameOver; // tells if game is over
    boolean getBlock; // tells us if we need a new block.
    boolean softDrop; // tells if the block is soft dropping
    
    int level; // current level
    int linesThisLevel;
    int intScore; // current score
    
    int[] dropRates; // frame rates for each level
    int dropRate; // how many frames before the block drops. starts at 48 frames per drop
    int framesSinceDrop; // keeps track of how many frames it's been since the block has dropped one gridcell
    int framesSinceLock = 0; // counts frames until the next block is spawned after one lock on the field
    
    Icons icons; // icons for the "next" functionality
    List<Integer> order; // create a block order
    int orderIndex; // current index of the order we are at.

    // bools to tell if shift or rotation needs to occur
    boolean shiftR; 
    boolean shiftL;
    boolean rotateClockwise;
    boolean rotateCounterclockwise;

    //////////////////////////////////////////////////////////////////////
    ////////////////////////////// UI ////////////////////////////////////
    //////////////////////////////////////////////////////////////////////
    
    Font tetrisFontLarge = Font.loadFont(getClass().getResourceAsStream("\\tetrisFont.ttf"), 32);
    Font tetrisFontSmall = Font.loadFont(getClass().getResourceAsStream("\\tetrisFont.ttf"), 16);
    Font tetrisFont = Font.loadFont(getClass().getResourceAsStream("\\tetrisFont.ttf"), 26); // create a font from the file tetrisFont.ttf as found on http://java-buddy.blogspot.com/2013/02/load-true-type-font-ttf-in-javafx.html
    // above font's licence
    /* 
        The FontStruction “Tetris” (https://fontstruct.com/fontstructions/show/1102867) by Patrick Lauke
        is licensed under a Creative Commons Attribution license (http://creativecommons.org/licenses/by/3.0/).
    */

    // main menu stuff
    Scene mainMenu;
    Button settingsButton = new Button("Settings");
    Button playButton = new Button("Play");
    Button leaderboardsButton = new Button("Leaderboards");
    Label title = new Label("TETRIS");
    URL bgImagePath = getClass().getResource("\\mainMenuImage.png");
    BackgroundImage bgImage = new BackgroundImage(new Image(bgImagePath.toString(), 420, 650, true, false), BackgroundRepeat.REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
    BackgroundSize.DEFAULT);

    // settings page stuff
    Scene settingsPage;
    ColorPicker[] pickers = new ColorPicker[7];
    Label[] colorLabels = new Label[7];
    Color[] colors = new Color[7];
    Button resetColors = new Button ("Reset Colors");
    public Boolean soundOn = true;
    CheckBox soundSwitch = new CheckBox("Sound");
    GridPane settingsGrid = new GridPane();
    Button backToMainMenuFromSettings = new Button("Back");
    Label settingsTitle = new Label("Settings");
    
    // leaderboards page stuff
    Scene leaderboard;
    Label[] listOfScores = new Label[3];
    Label leaderboardTitle = new Label("Leaderboard");
    Button backToMainMenuFromLeaderboards = new Button("Back");
    HBox[] linesOfLeaderboard = new HBox[3];

    // endgame screen stuff
    Scene endgameScreen;
    TextField nameField = new TextField();
    Label nameSubmissionPrompt = new Label("Enter name:");
    Button submit = new Button("Submit");
    HBox nameSubmissionPane = new HBox(1, nameSubmissionPrompt, nameField);
    VBox endgamePane = new VBox(25, nameSubmissionPane, submit);

    //////////////////////////////////////////////////////////////////////
    ////////////////////////////// SOUNDS ////////////////////////////////
    //////////////////////////////////////////////////////////////////////

    // main menu
    URL mainMenPath = getClass().getResource("\\mainMenu.mp3");
    public AudioClip mainMenuMusic = new AudioClip(mainMenPath.toString());

    // translate
    URL translatePath = getClass().getResource("\\translate.mp3");
    public AudioClip translate = new AudioClip(translatePath.toString());

    // lock block
    URL lockPath = getClass().getResource("\\lock.mp3");
    public AudioClip lock = new AudioClip(lockPath.toString());

    // bg music
    URL bgMusicPath = getClass().getResource("\\bgMusic.mp3");
    public AudioClip bgMusic = new AudioClip(bgMusicPath.toString());

    // bg music fast
    URL bgMusicFastPath = getClass().getResource("\\bgMusicFast.mp3");
    public AudioClip bgMusicFast = new AudioClip(bgMusicFastPath.toString());

    // next level
    URL nextLevelPath = getClass().getResource("\\nextLevel.mp3");
    public AudioClip nextLevel = new AudioClip(nextLevelPath.toString());

    // this is like the "main method" in javafx we override the start method in the application class to customize what we want it to do.
    @Override
    public void start(Stage gameWindow) throws Exception, IOException, FileNotFoundException
    {
        gameWindow.setTitle("Tetris by David"); // set the window title
        gameWindow.setResizable(false);

        // main menu scene
        title.setFont(tetrisFontLarge);
        title.setTextFill(Color.WHITE);
        Effects.styleButton(playButton);
        Effects.styleButton(settingsButton);
        Effects.styleButton(leaderboardsButton);
        VBox mainMenuBox = new VBox(25, title, playButton, leaderboardsButton, settingsButton);
        mainMenuBox.setAlignment(Pos.BASELINE_CENTER);
        mainMenuBox.setPadding(new Insets(175, 0, 0, 0));
        mainMenuBox.setBackground(new Background(bgImage));
        mainMenu = new Scene(mainMenuBox, 420, 650);

        // setttings scene
        initColorPickers();
        initColorLabels();
        setColors();
        settingsTitle.setFont(tetrisFont);
        settingsTitle.setTextFill(Color.WHITE);
        settingsGrid.add(settingsTitle, 0, 0);
        for (int i = 0; i < 7; i++)
        {
            settingsGrid.add(new HBox(50, colorLabels[i], pickers[i]), 0, i + 1);
            // settingsGrid.add(colorLabels[i], 0, i+1);
            // settingsGrid.add(pickers[i], 1, i+1);
        }
        soundSwitch.setTextFill(Color.WHITE);
        settingsGrid.add(new HBox(50, soundSwitch, resetColors), 0, 8);
        soundSwitch.fire();
        Effects.styleButton(backToMainMenuFromSettings);
        settingsGrid.add(backToMainMenuFromSettings, 0, 9);
        settingsGrid.setHalignment(backToMainMenuFromSettings, HPos.CENTER);
        settingsGrid.setBackground(Effects.background);
        settingsGrid.setHgap(20);
        settingsGrid.setVgap(20);
        settingsGrid.setAlignment(Pos.CENTER);
        settingsPage = new Scene(settingsGrid , 420, 650);
        

        // leaderboards screen
        initListOfScores();
        getHighScoresFromTextFile();
        leaderboardTitle.setFont(tetrisFont);
        leaderboardTitle.setTextFill(Color.WHITE);
        for (int i = 1; i <= 3; i++)
        {
            // leaderboardBox.add(new Label(Integer.toString(i)), 0, i);
            Label temp = new Label(Integer.toString(i));
            temp.setTextFill(Color.WHITE);
            temp.setFont(tetrisFontSmall);
            HBox tempBox = new HBox(25, temp, listOfScores[i - 1]);
            // tempBox.setPadding(new Insets(0, 0, 0, 65));
            tempBox.setAlignment(Pos.CENTER);
            linesOfLeaderboard[i - 1] = tempBox;
            //leaderboardBox.add(listOfScores[i - 1], 0, i);
        }
        Effects.styleButton(backToMainMenuFromLeaderboards);
        VBox leaderboardBox = new VBox(50, leaderboardTitle, linesOfLeaderboard[0], linesOfLeaderboard[1], linesOfLeaderboard[2], backToMainMenuFromLeaderboards);
        // leaderboardBox.setPadding(new Insets(175, 50, 100, 50));
        leaderboardBox.setAlignment(Pos.CENTER);
        leaderboardBox.setBackground(Effects.background);
        leaderboard = new Scene(leaderboardBox, 420, 650);

        // endgame screen
        nameField.setMinSize(50, 10);
        nameSubmissionPrompt.setFont(tetrisFontSmall);
        nameSubmissionPrompt.setTextFill(Color.WHITE);
        Effects.styleButton(submit);
        nameSubmissionPane.setAlignment(Pos.CENTER);
        endgamePane.setAlignment(Pos.CENTER);
        endgamePane.setBackground(Effects.background);
        endgameScreen = new Scene(endgamePane, 420, 650);

        //////////////////////////////////////////////////////////////////////
        ////////////////////////////// GAME //////////////////////////////////
        //////////////////////////////////////////////////////////////////////

        // use an AnimationTimer to do a certain action 60 times a second. This will give the game 60 fps. https://stackoverflow.com/questions/29962395/how-to-write-a-keylistener-for-javafx was referenced
        // in essence, this is where the game is: the game loop.
        AnimationTimer timer = new AnimationTimer()
        {
            @Override
            public void handle (long now)
            {
                // this code will be run every 60th of a second, allowing in theory for 60 fps.
                
                // runs if new block needs to be spawned
                if (getBlock)
                {
                    //framesSinceLock++;
                    if (++framesSinceLock >= 10)
                    {
                        framesSinceLock = 0;
                        getBlock = !getBlock;

                        // generate the block given by the shuffled list at the index we are at: orderIndex
                        currentBlock = getNewBlock(order, orderIndex++); // increment order index for next time
                        
                        // if we're at the end of the list, we shuffle and return to the beginning
                        if (orderIndex == 7)
                        {
                            orderIndex = 0;
                            Collections.shuffle(order);
                        }

                        // set the next block icon to show the upcoming block
                        board.setNext(order, orderIndex, icons);

                        // check if we can spawn the block
                        if (!board.canSpawnBlock(currentBlock))
                        {
                            gameOver = true;
                        }
                        
                        board.spawnBlock(currentBlock);
                    }
                }
                else
                {
                    // shifts block left
                    if (shiftL && board.canShiftBlock(currentBlock, -1, 0))
                    {
                        board.shiftBlock(currentBlock, -1, 0);
                        if (soundOn) { playTranslate(); }
                    }

                    // shifts block right
                    if (shiftR && board.canShiftBlock(currentBlock, 1, 0))
                    {
                        board.shiftBlock(currentBlock, 1, 0);
                        if (soundOn) { playTranslate(); }
                    }

                    // rotates block clockwise
                    if (rotateClockwise && board.canRotateBlockClockwise(currentBlock))
                    {
                        currentBlock.rotateClockwise();
                        board.drawBlock(currentBlock);
                        if (soundOn) { playTranslate(); }
                    }

                    //rotate block counterclockwise
                    if (rotateCounterclockwise && board.canRotateBlockCounterclockwise(currentBlock))
                    {
                        currentBlock.rotateCounterclockwise();
                        board.drawBlock(currentBlock);
                        if (soundOn) { playTranslate(); }
                    }

                    // shift block down
                    if (++framesSinceDrop >= dropRate)
                    {
                        framesSinceDrop = 0;
                        // code for making the block drop down one level...
                        // pseudo: for each square in block, if vertical coord+1 is occupied, check if any rows are filled. if so, clear the row. then, change the current block. Else, lower the block by one gridcell
                        if (board.canShiftBlock(currentBlock, 0, 1))
                        {
                            board.shiftBlock(currentBlock, 0, 1);
                        }
                        else
                        {
                            getBlock = true;
                            if (soundOn) { playLock(); }
                            int[] linesAndScore = board.checkForAndProcessLines(level); // { numCompletedLines, score }
                            if (linesAndScore[0] > 0)
                            {
                                intScore += linesAndScore[1];
                                linesThisLevel += linesAndScore[0];

                                framesSinceLock -= 17;

                                if (linesThisLevel >= 10)
                                {
                                    linesThisLevel -= 10;
                                    level++;
                                    board.incrementLevelLabel();
                                    if (soundOn)
                                    { 
                                        playNextLevel();
                                        if (level == 8)
                                        {
                                            bgMusic.stop();
                                            playFastBGMusic();
                                        } 
                                    }
                                    
                                    dropRate = dropRates[level];
                                }
                            }
                            setScore(intScore, board.getScore());
                        }
                        if (softDrop)
                        {
                            intScore += 1;
                        }
                    }
                }

                if (gameOver)
                { 
                    // change the scene to the end screen!!!!!
                    if (soundOn)
                    { 
                        bgMusic.stop();
                        bgMusicFast.stop();
                        playMainMenuMusic();
                    }
                    gameWindow.setScene(endgameScreen);
                    this.stop(); // basically same as "break;" for the game loop 
                }


                shiftL = false;
                shiftR = false;
                rotateClockwise = false;
                rotateCounterclockwise = false;
            }
        };

        //////////////////////////////////////////////////////////////////////
        ////////////////////////// BUTTON EVENTS /////////////////////////////
        //////////////////////////////////////////////////////////////////////

        // set what the buttons do
        playButton.setOnAction(e -> 
        { 
            resetGameVars();
            icons = new Icons(colors);
            gameWindow.setScene(gameScene);
            timer.start();
            if (soundOn) 
            { 
                mainMenuMusic.stop();
                playBGMusic(); 
            }
        });

        settingsButton.setOnAction(e -> 
        {
            gameWindow.setScene(settingsPage);
        });

        leaderboardsButton.setOnAction(e -> 
        {
            try
            {
                getHighScoresFromTextFile();
            }
            catch (Exception error)
            {
                error.printStackTrace();
            }
            gameWindow.setScene(leaderboard);
        });

        backToMainMenuFromSettings.setOnAction(e -> 
        {
            // take all the settings and apply them somehow... may not be needed
            setColors();
            gameWindow.setScene(mainMenu);
        });

        backToMainMenuFromLeaderboards.setOnAction(e -> 
        {
            // return to the main menu screen
            gameWindow.setScene(mainMenu);
        });

        submit.setOnAction(e -> 
        {
            processAndStoreScore(intScore, nameField.getText());
            try
            {
                setHighScoresTextFile();
            }
            catch (Exception error)
            {
                error.printStackTrace();
            }
            nameField.clear();
            gameWindow.setScene(mainMenu);
        });

        soundSwitch.setOnAction(e -> 
        {
            if (soundOn)
            { 
                soundOn = false;
                mainMenuMusic.stop(); 
            }
            else 
            { 
                soundOn = true; 
                playMainMenuMusic();
            }
        });

        resetColors.setOnAction(e -> 
        {
            resetColorPickers();
        });

        
        gameWindow.setScene(mainMenu);
        gameWindow.show(); // show the window
        playMainMenuMusic();

    }

    Block getNewBlock (List<Integer> order, int orderIndex) // returns a new block based on the order
    {
        switch (order.get(orderIndex))
        {
            case 0:
                return new IBlock(3, -1, colors[0]);
            case 1:
                return new JBlock(3, 0, colors[1]);
            case 2:
                return new LBlock(3, 0, colors[2]);
            case 3:
                return new SBlock(3, 0, colors[3]);
            case 4:
                return new ZBlock(3, 0, colors[4]);
            case 5:
                return new TBlock(3, 0, colors[5]);
            case 6:
                return new OBlock(3, 0, colors[6]);
        }
        return null;
    }

    List<Integer> getOrder () 
    {
        List<Integer> order = new ArrayList<Integer>(); // gives the order for the blocks to spawn
        for (int i = 0; i < 7; i++)
        {
            order.add(i);
        }
        Collections.shuffle(order); // shuffle the order of the spawning
        return order;
    }

    // resets the score label based on a new score
    void setScore (int score, Label label)
    {
        String stringScore = Integer.toString(score); // change the score to a string to display as text
        int l = stringScore.length(); // store the length because it will be changing as we add chars

        if (l < 9) 
        {
            for (int i = 0; i < 9 - l; i++) // add 0s to pad the left of the score label for that RETRO FEEL
            {
                stringScore = "0" + stringScore;
            }
        }
        label.setText(stringScore);
    }

    void initColorPickers ()
    {
        pickers[0] = new ColorPicker(Color.web("#0AB9E6"));
        pickers[1] = new ColorPicker(Color.web("#4655F5"));
        pickers[2] = new ColorPicker(Color.web("#FAA005"));
        pickers[3] = new ColorPicker(Color.web("#1EDC00"));
        pickers[4] = new ColorPicker(Color.web("#FF3C28"));
        pickers[5] = new ColorPicker(Color.web("#B400E6"));
        pickers[6] = new ColorPicker(Color.web("#E6FF00"));
    }

    void resetColorPickers () 
    {
        pickers[0].setValue(Color.web("#0AB9E6"));
        pickers[1].setValue(Color.web("#4655F5"));
        pickers[2].setValue(Color.web("#FAA005"));
        pickers[3].setValue(Color.web("#1EDC00"));
        pickers[4].setValue(Color.web("#FF3C28"));
        pickers[5].setValue(Color.web("#B400E6"));
        pickers[6].setValue(Color.web("#E6FF00"));
    }

    void setColors ()
    {
        for (int i = 0; i < 7; i++)
        {
            colors[i] = pickers[i].getValue();
        }
    }

    void initColorLabels ()
    {
        colorLabels[0] = new Label("I Block");
        colorLabels[1] = new Label("J Block");
        colorLabels[2] = new Label("L Block");
        colorLabels[3] = new Label("S Block");
        colorLabels[4] = new Label("Z Block");
        colorLabels[5] = new Label("T Block");
        colorLabels[6] = new Label("O Block");

        colorLabels[0].setTextFill(Color.WHITE);
        colorLabels[1].setTextFill(Color.WHITE);
        colorLabels[2].setTextFill(Color.WHITE);
        colorLabels[3].setTextFill(Color.WHITE);
        colorLabels[4].setTextFill(Color.WHITE);
        colorLabels[5].setTextFill(Color.WHITE);
        colorLabels[6].setTextFill(Color.WHITE);
    }

    void resetGameVars()
    {
        board = new Board(Integer.valueOf(listOfScores[0].getText().substring(listOfScores[0].getText().lastIndexOf(" ") + 1)), soundOn);
        currentBlock = null; // current block we are manipulating and using
    
        gameOver = false; // tells if game is over
        getBlock = true; // tells us if we need a new block.
        softDrop = false; // tells if the block is soft dropping
    
        level = 0; // current level
        linesThisLevel = 0;
        intScore = 0; // current score
    
        dropRates = new int[]{ 48, 43, 38, 33, 28, 23, 18, 13, 8, 6, 5, 4, 3, 2, 1 }; // frame rates for each level
        dropRate = dropRates[level]; // how many frames before the block drops. starts at 48 frames per drop
        framesSinceDrop = 0; // keeps track of how many frames it's been since the block has dropped one gridcell
        framesSinceLock = 0; // counts frames until the next block is spawned after one lock on the field
    
        order = getOrder(); // create a block order
        orderIndex = 0; // current index of the order we are at.

        // bools to tell if shift or rotation needs to occur
        shiftR = false; 
        shiftL = false;
        rotateClockwise = false;
        rotateCounterclockwise = false;

        gameScene = new Scene(board.getBoard(), 420, 650);

        gameScene.setOnKeyPressed(new EventHandler<KeyEvent>()
        {
            @Override
            public void handle (KeyEvent event)
            {
                switch (event.getCode())
                {
                    case W:
                        // rotate the block counterclockwise
                        rotateCounterclockwise = true;
                        break; 
                    case A:
                        // shift the block to the left
                        shiftL = true;
                        break;
                    case S:
                        // rotate the block clockwise
                        rotateClockwise = true;
                        break;
                    case D:
                        // shift the block right
                        shiftR = true;
                        break;
                    case SPACE:
                        // increase the drop rate when spacebar is held down
                        if (dropRate == dropRates[level])
                        {
                            dropRate = 3 - (level / 15); // formula for hard drop
                        }
                        softDrop = true;
                        break;
                }
            }
        });

        gameScene.setOnKeyReleased(new EventHandler<KeyEvent>()
        {
            //@Override
            public void handle (KeyEvent event)
            {
                switch (event.getCode())
                {
                    case SPACE:
                        // when spacebar released, return drop rate to regular speed
                        dropRate = dropRates[level];
                        softDrop = false;
                        break;
                }
            }
        });
    }

    // ... a bit bugged with the leading number...

    void getHighScoresFromTextFile () throws FileNotFoundException, IOException
    {
        BufferedReader reader = new BufferedReader(new FileReader("Leaderboards.txt"));
        String line = "";
        for (int i = 0; i < 3; i++)
        {
            try
            {
                line = reader.readLine();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            if (line != null)
            {
                listOfScores[i].setText(getText(line));
            }
        }

        reader.close();
    }

    void setHighScoresTextFile () throws FileNotFoundException, IOException
    {
        BufferedWriter writer = new BufferedWriter(new FileWriter("Leaderboards.txt", false));
        
        for (int i = 0; i < 3; i++)
        {
            String line = listOfScores[i].getText();
            writer.write(line.length() + " " + line);
            writer.newLine();
        }

        writer.close();
    }

    void processAndStoreScore (int score, String name)
    {
        String curString = name + " " + Integer.toString(score);
        String temp;
        for (int i = 0; i < 3; i++)
        {
            if (!listOfScores[i].getText().equals("N/A"))
            {
                int curScore = Integer.valueOf(listOfScores[i].getText().substring(listOfScores[i].getText().lastIndexOf(" ") + 1));
                if (score > curScore)
                {
                    temp = listOfScores[i].getText();
                    listOfScores[i].setText(curString);
                    curString = temp;
                    score = curScore;
                }
            } 
            else
            {
                listOfScores[i].setText(curString);
                break;
            }
        }
    }

    void initListOfScores ()
    {
        for (int i = 0; i < 3; i++)
        {
            listOfScores[i] = new Label("N/A");
            listOfScores[i].setFont(tetrisFontSmall);
            listOfScores[i].setTextFill(Color.WHITE);
        }
    }

    String getText (String line)
    {
        if (line.equals("N/A")){ return line; }

        int space = line.indexOf(" ");
        int l = Integer.valueOf(line.substring(0, space));
        
        return line.substring(space + 1, space + l + 1);
    }

    public void playTranslate ()
    {
        translate.play();
    }



    public void playLock ()
    {
        lock.setVolume(10.0);
        lock.play();
    }

    public void playBGMusic ()
    {
        bgMusic.setCycleCount(Integer.MAX_VALUE);
        bgMusic.setVolume(0.3);
        bgMusic.play();
    }

    public void playMainMenuMusic ()
    {
        mainMenuMusic.setCycleCount(Integer.MAX_VALUE);
        mainMenuMusic.setVolume(0.35);
        mainMenuMusic.play();
    }

    public void playNextLevel ()
    {
        nextLevel.setVolume(0.7);
        nextLevel.play();
    }

    void playFastBGMusic ()
    {
        bgMusicFast.setCycleCount(Integer.MAX_VALUE);
        bgMusicFast.setVolume(0.3);
        bgMusicFast.play();
    }
}

