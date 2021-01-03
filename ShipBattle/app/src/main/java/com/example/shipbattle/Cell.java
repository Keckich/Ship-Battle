package com.example.shipbattle;

public class Cell {
    int posX, posY;
    CellCondition cellCondition;

    Cell(int posX, int posY, CellCondition cellCondition) {
        this.posX = posX;
        this.posY = posY;
        this.cellCondition = cellCondition;
    }

    public int getPosX() {
        return posX;
    }

    public int getPosY() {
        return posY;
    }

    public void setPosX(int posX) {
        this.posX = posX;
    }

    public void setPosY(int posY) {
        this.posY = posY;
    }

    public void setCellCondition(CellCondition cellCondition) {
        this.cellCondition = cellCondition;
    }

    public CellCondition getCellCondition() {
        return cellCondition;
    }
}
