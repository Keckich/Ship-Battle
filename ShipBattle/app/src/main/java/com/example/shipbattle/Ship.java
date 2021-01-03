package com.example.shipbattle;

public class Ship {
    private int size, orientation;
    private Cell beginPos, endPos;

    Ship(int size, Cell beginPos, Cell endPos, int orientation) {
        this.size = size;
        this.beginPos = beginPos;
        this.endPos = endPos;
        this.orientation = orientation;
    }

    public Cell getBeginPos() {
        return beginPos;
    }

    public Cell getEndPos() {
        return endPos;
    }

    public int getSize() {
        return size;
    }

    public int getOrientation() {
        return orientation;
    }
}
