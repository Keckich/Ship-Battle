package com.example.shipbattle;

import java.util.ArrayList;

public class Game {
    ArrayList<ArrayList<Cell>> creator, opponent;

    Game() {}

    Game(ArrayList<ArrayList<Cell>> creator, ArrayList<ArrayList<Cell>> opponent) {
        this.creator = creator;
        this.opponent = opponent;
    }

    public ArrayList<ArrayList<Cell>> getCreator() {
        return creator;
    }

    public ArrayList<ArrayList<Cell>> getOpponent() {
        return opponent;
    }

    public void setCreator(ArrayList<ArrayList<Cell>> creator) {
        this.creator = creator;
    }

    public void setOpponent(ArrayList<ArrayList<Cell>> opponent) {
        this.opponent = opponent;
    }
}
