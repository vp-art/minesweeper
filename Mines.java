import javax.swing.*;
import java.awt.*;
import java.util.Random;
import java.util.Stack;

import static java.lang.StrictMath.floor;

class Minesweeper {
    private int v1, v2;
    public int grid_1[][] = {
            {0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0}
    };
    public int search_directions[][] = {
            {-1,-1},
            {-1,0},
            {-1,1},
            {1,-1},
            {1,0},
            {0,-1},
            {1,1},
            {0,1}
    };

    // Get random number for the location of mines
    private int getRandomNumber(int min, int max) {
        Random r1 = new Random();
        return r1.nextInt((max - min) + 1) + min;
    }

    // Place mines at random locations
    void place_mines() {
        v1 = getRandomNumber(0, 7);
        v2 = getRandomNumber(0, 7);
        grid_1[v1][v2] = -1;
    }

    // Number of mines in its adjacent cells
    void set_bomb_count(){
        int row = 0, col = 0;
        for(int i = 0; i<8; i++){
            for(int j = 0; j<8; j++){
                if(grid_1[i][j] == -1){
                    continue;
                }
                for(int k = 0; k<8;k++) {
                    row = i+search_directions[k][0];
                    col = j+search_directions[k][1];
                    if(row >= 0 && col >= 0 && row < 8 && col < 8)
                        if(grid_1[row][col] == -1){
                            grid_1[i][j]++;
                        }
                }
            }
        }
    }
    void display_grid(){
        System.out.println("*****Mines******");
        for (int i = 0; i < 8; i++) {
            System.out.println();
            for (int j = 0; j < 8; j++) {
                System.out.print(grid_1[i][j] + "\t");
            }
        }
    }
}


class Create_Window extends Minesweeper {
    JFrame frame = new JFrame(" MINESWEEPER ");
    JFrame new_frame = new JFrame("Game Over");
    JButton[][] button_array = new JButton[8][8];
    boolean[][] visited = new boolean[8][8];
    //JButton btn = new JButton();
    JButton restart = new JButton();
    int[][] adj = new int[64][64];

    Create_Window() {
        for (int j = 0; j < 10; j++) {
            place_mines();
        }
        set_bomb_count();
        //display_grid();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new GridLayout(8, 8));
        frame.setResizable(false);
        for(int i = 0; i < 8; i++){
            for(int j = 0; j < 8; j++){
                JButton button = new JButton(" ");
                button.setFont(new Font("Arial", Font.PLAIN, 20));
                button.setFont(button.getFont().deriveFont(Font.BOLD));
                button_array[i][j] = button;
                button.setPreferredSize(new Dimension(50, 50));
                frame.add(button);
                button.addActionListener(e -> {
                    int button_X = button.getLocation().x;
                    int button_Y = button.getLocation().y;
                    int row = button_Y/50;
                    int col = button_X/50;
                    for(int a = 1; a < 8; a++) {
                        if (grid_1[row][col] == a) {
                            // displays the number when clicked
                            button.setText(String.valueOf(grid_1[row][col]));
                        }
                    }
                    if(grid_1[row][col] == -1){
                        //clicks on bomb, reveal all the bombs
                        for(int b = 0; b < 8; b++){
                            for(int p = 0; p < 8; p++){
                                if(grid_1[b][p] == -1) {
                                    button_array[b][p].setText("*");
                                }
                            }
                        }
                        frame.setEnabled(false);
                        launchGameOverWindow();
                        return;
                    }
                    else if(grid_1[row][col] == 0){
                        // Creating an adjacency matrix
                        adjacency_mat(8, 8);
                        //clicks on empty cell
                        button.setBackground(Color.lightGray);
                        search(row, col);
                    }
                    else {
                        System.out.print("");
                    }
                });
            }
        }
        frame.pack();
        frame.setVisible(true);
    }


    void launchGameOverWindow(){
        new_frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        new_frame.setBounds(50, 50, 300, 100);
        //int answer = JOptionPane.showConfirmDialog(new_frame,"Ha!! You're an idiot \n Try again? ");
        int answer = JOptionPane.showConfirmDialog(new_frame,"Oops. Clicked on a mine \n Try again? ", "Game Over", JOptionPane.YES_NO_OPTION);
        restart.setPreferredSize(new Dimension(100, 75));
        if (answer == JOptionPane.YES_OPTION) {
            new Create_Window();
            new_frame.dispose();
            frame.dispose();
        }
        else{
            System.exit(0);
        }
    }


    void search(int row, int col){
        Stack<Integer> s = new Stack<Integer>();
        for(int i = 0; i < 8; i++){
            for(int j = 0; j < 8; j++){
                visited[i][j] = false;
            }
        }
        s.push(row);
        s.push(col);
        while(!s.isEmpty()){
            int new_row, new_col;
            new_col = s.pop();
            new_row = s.pop();
            if(!visited[new_row][new_col] && grid_1[new_row][new_col] == 0){
                visited[new_row][new_col] = true;
                for (int i = 0; i < 64; i++) {
                    // If its a neighbour
                    if(adj[(new_row * 8) + new_col][i] == 1){
                        if(grid_1[(int) (floor(i/8))][i%8] == 0) {
                            button_array[(int) (floor(i / 8))][i % 8].setBackground(Color.lightGray);
                        }
                        else {
                            button_array[(int) (floor(i / 8))][i % 8].setText(String.valueOf(grid_1[(int) (floor(i / 8))][i % 8]));
                        }
                        // If neighbour's not visited
                        if(!visited[(int) (floor(i/8))][i%8]) {
                            // If neighbour is zero
                            if(grid_1[(int) (floor(i/8))][i%8] == 0) {
                                s.push((int) (floor(i/8)));
                                s.push(i%8);
                            }
                        }
                    }
                }
            }
        }
    }


    void adjacency_mat(int rows, int cols){
        int k = 0;
        for(int i = 0; i < rows; i++){
            for(int j = 0; j < cols; j++){
                if((j+1) < cols){
                    adj[k][(i * cols) + (j+1)] = 1;
                }
                if((i+1) < rows){
                    adj[k][((i+1) * cols) + j] = 1;
                }
                if((i-1) >= 0){
                    adj[k][((i-1) * cols) + j] = 1;
                }
                if((j-1) >= 0){
                    adj[k][(i * cols) + (j-1)] = 1;
                }
                if((i+1) < rows && (j+1) < cols){
                    adj[k][((i+1)*cols) + (j+1)] = 1;
                }
                if((i+1) < rows && (j-1) >= 0){
                    adj[k][((i+1)*cols) + (j-1)] = 1;
                }
                if((i-1) >= 0 && (j+1) < cols){
                    adj[k][((i-1)*cols) + (j+1)] = 1;
                }
                if((i-1) >= 0 && (j-1) >= 0){
                    adj[k][((i-1)*cols) + (j-1)] = 1;
                }
                k++;
            }
        }
    }
}


public class Mines {
    public static void main(String[] args) {
        Create_Window create = new Create_Window();
    }
}
