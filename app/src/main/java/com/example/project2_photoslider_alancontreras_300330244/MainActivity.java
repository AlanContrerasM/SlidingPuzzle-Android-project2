package com.example.project2_photoslider_alancontreras_300330244;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    RelativeLayout buttonsGroup;
    ArrayList<ImageButton> buttons;
    ArrayList<Integer> previousMoves;
    ArrayList<Integer> drawablesResources;
    TextView moves_counter;
    int blankY;
    int blankX;
    int blankPos;
    int movesCounter = 0;
    TextView message;
    Button new_puzzle;
    Button solve_puzzle;
    Boolean playing;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonsGroup = findViewById(R.id.buttons_group);
        new_puzzle = findViewById(R.id.new_puzzle_button);
        solve_puzzle = findViewById(R.id.solve_puzzle_button);
        moves_counter = findViewById(R.id.moves_counter);
        message = findViewById(R.id.message);

        //getting all buttons inside the relative layout
        int child_count = buttonsGroup.getChildCount();
        buttons = new ArrayList<ImageButton>();
        previousMoves = new ArrayList<Integer>();
        for(int i = 0; i < child_count; i++){
            buttons.add((ImageButton) buttonsGroup.getChildAt(i));
        }

        //creating array of ints from image resources
        drawablesResources = new ArrayList<Integer>();
        drawablesResources.add(R.drawable.img1);
        drawablesResources.add(R.drawable.img2);
        drawablesResources.add(R.drawable.img3);
        drawablesResources.add(R.drawable.img4);
        drawablesResources.add(R.drawable.img5);
        drawablesResources.add(R.drawable.img6);
        drawablesResources.add(R.drawable.img7);
        drawablesResources.add(R.drawable.img8);
        drawablesResources.add(R.drawable.img9);
        drawablesResources.add(R.drawable.img10);
        drawablesResources.add(R.drawable.img11);
        drawablesResources.add(R.drawable.img12);
        drawablesResources.add(R.drawable.img13);
        drawablesResources.add(R.drawable.img14);
        drawablesResources.add(R.drawable.img15);
        drawablesResources.add(R.drawable.blank);

        //blank position set because of many different usages
        //blank is at last position so we set his blanks x and y
        blankX = 4;
        blankY = 4;
        blankPos = 15;
        playing = false;

        if(savedInstanceState!=null){
            if(savedInstanceState.getBoolean("playing")){
                playing = true;
                solve_puzzle.setEnabled(true);
                new_puzzle.setEnabled(false);
                blankPos = savedInstanceState.getInt("blankPos");
                blankY = savedInstanceState.getInt("blankY");
                blankX = savedInstanceState.getInt("blankX");
                movesCounter = savedInstanceState.getInt("movesCounter");
                moves_counter.setText("" + movesCounter);
                message.setText(savedInstanceState.getString("message"));
                previousMoves = savedInstanceState.getIntegerArrayList("previousMoves");

                ArrayList<Integer> contentDescriptions = savedInstanceState.getIntegerArrayList("contentDescriptions");
                int counter = 0;
                for(ImageButton btn : buttons){
                    //set new content description
                    btn.setContentDescription(contentDescriptions.get(counter).toString());
                    //set new drawables
                    btn.setImageResource(drawablesResources.get(contentDescriptions.get(counter)-1));
                    counter++;
                }
            }else{
                movesCounter = savedInstanceState.getInt("movesCounter");
                moves_counter.setText("" + movesCounter);
                message.setText(savedInstanceState.getString("message"));
            }
        }
    }

    public void pressedButton(View view){

        //first string is position in ArrayList, second is y, and third is x
        String tag = view.getTag().toString();
        String[] str = tag.split(",");

        if(!playing){
            //game not in progress
            setMessage("You need to create a new puzzle in order to play!");
        }else if(checkValidMovement(Integer.parseInt(str[1]), Integer.parseInt(str[2]))){
            //valid move
            setMessage("");
            previousMoves.add(blankPos);
            updateDrawables(str);
            movesCounter ++;
            moves_counter.setText("" + movesCounter);
            checkWin();

        }else{
            setMessage("Illegal Move!");
        }

    }

    public Boolean checkValidMovement(int y, int x){
        Boolean valid = false;

        if(y == blankY && x == blankX){
            //pressed empty blank button, do nothing
        }else if(y == blankY && (x - blankX == 1 || blankX - x == 1)){
            //valid
            valid = true;
        }else if(x == blankX && (y-blankY==1 || blankY - y == 1)){
            valid = true;
        }

        return valid;
    }

    public void updateDrawables(String[] str){
        int pos = Integer.parseInt(str[0]);
        int y = Integer.parseInt(str[1]);
        int x = Integer.parseInt(str[2]);

        Drawable draw = buttons.get(pos).getDrawable();

        //get content description and interchange them
        String currentDesc = buttons.get(pos).getContentDescription().toString();
        String blankDesc = buttons.get(blankPos).getContentDescription().toString();

        buttons.get(pos).setContentDescription(blankDesc);
        buttons.get(blankPos).setContentDescription(currentDesc);

        //set new images
        buttons.get(blankPos).setImageDrawable(draw);
        buttons.get(pos).setImageResource(R.drawable.blank);

        //set new blank positions
        blankPos = pos;
        blankY = y;
        blankX = x;

    }

    public void setMessage(String msg){
        message.setText(msg);
    }

    public void newPuzzle(View view) {
        scramblePuzzle();
        movesCounter = 0;
        new_puzzle.setEnabled(false);
        solve_puzzle.setEnabled(true);
        setMessage("Happy Playing");
        moves_counter.setText("" + movesCounter);
        playing = true;
    }


    public void solvePuzzle(View view) {
        if(previousMoves.size() != 0){
            //Sort through array in reverse order to solve
            ArrayList<Integer> reverse = new ArrayList<Integer>();
            for (int i = previousMoves.size() - 1; i >= 0; i--) {
                reverse.add(previousMoves.get(i));
            }
            int speed = 70;
            if(previousMoves.size()>200){
                speed = 10;
            }else if(previousMoves.size()>100){
                speed = 30;
            }
            final Handler handler = new Handler();
            int finalSpeed = speed;
            final Runnable runnable = new Runnable() {
                int count = 0;
                public void run() {
                    // need to do tasks on the UI thread
                    updateDrawables(buttons.get(reverse.get(count)).getTag().toString().split(","));

                    //checkWin disabled so that user does not get a congrats message, but easily enabled.
                    //checkWin();
                    if (count++ < reverse.size()-1) {
                        handler.postDelayed(this, finalSpeed);
                    }
                }
            };
            // trigger first time
            handler.post(runnable);

            //since checkWin is disabled we do end the game like this
            playing = false;
            solve_puzzle.setEnabled(false);
            new_puzzle.setEnabled(true);
            previousMoves.clear();
            setMessage("Puzzle Automatically Solved");
        }
    }

    public void scramblePuzzle() {
        //First we'll decide how many moves we will scramble it for Random Number between 40 and 80
        int moves = 40 + (int)Math.floor(Math.random()*41);
        //basically we only need either blank position - 1 or -2 or blankpos +4 or -4
        final Handler handler = new Handler();
        final Runnable runnable = new Runnable() {
            int count = 0;
            public void run() {
                // create either horizontal or vertical movement, if invalid, change directions.
                int movement;
                if(Math.random()*2 >1){
                    movement = 4;
                    if(Math.random()* 2 >1){ movement *= -1; }
                }else{
                    movement = 1;
                    if(Math.random()* 2 >1){ movement *= -1; }
                }
                if(blankPos + movement < 0 || blankPos + movement >15){ movement *= -1; }
                previousMoves.add(blankPos);
                updateDrawables(buttons.get(blankPos + movement).getTag().toString().split(","));
                if (count++ < moves) {
                    handler.postDelayed(this, 50);
                }
            }
        };
        // trigger first time
        handler.post(runnable);
    }

    public void checkWin(){
        int counter = 1;
        Boolean win = true;
        for(ImageButton btn : buttons){
            if (Integer.parseInt(btn.getContentDescription().toString()) != counter){
                win = false;
                break;
            }
            counter ++;
        }

        if(win){

            setMessage("You solved the puzzle in " + movesCounter + " moves!");


            playing = false;
            solve_puzzle.setEnabled(false);
            new_puzzle.setEnabled(true);
            previousMoves.clear();
        }

    }

    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        ArrayList<Integer> contentDescriptions = new ArrayList<Integer>();
        //create array of each button content description
        for (ImageButton btn: buttons){
            contentDescriptions.add(Integer.parseInt(btn.getContentDescription().toString()));
        }
        outState.putIntegerArrayList("contentDescriptions", contentDescriptions);
        //create playing status
        outState.putBoolean("playing", playing);
        //create blank pos
        outState.putInt("blankPos", blankPos);
        //blank y
        outState.putInt("blankY", blankY);
        //blank x
        outState.putInt("blankX", blankX);
        //moves counter
        outState.putInt("movesCounter", movesCounter);
        //message
        outState.putString("message", message.getText().toString());
        //previous moves
        outState.putIntegerArrayList("previousMoves", previousMoves);
    }
}