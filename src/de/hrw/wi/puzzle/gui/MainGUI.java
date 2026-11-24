package de.hrw.wi.puzzle.gui;


import de.hrw.wi.puzzle.gui.window.EightPuzzleGUI;

public class MainGUI {

    public static void main(String[] args) {
        // Start GUI
        int dimension = 3;
        if(args.length == 1) {
            try {
                dimension = Integer.parseInt(args[0]);
            } catch(Exception e) {
                help();
                System.exit(-1);
            }
        }
        EightPuzzleGUI eightPuzzleGUI = new EightPuzzleGUI(dimension);
        eightPuzzleGUI.setVisible(true);
    }

    private static void help() {
        System.out.println("Arguments: none -> 3x3 Board, a single positive number n : n x n board");
    }
}
