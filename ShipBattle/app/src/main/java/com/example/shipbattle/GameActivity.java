package com.example.shipbattle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameActivity extends AppCompatActivity implements RecyclerViewAdapter.ItemClickListener {
    TextView textViewCell;
    RecyclerViewAdapter adapter;
    Cell[][] cells;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        String[] data = new String[100];
        cells = new Cell[10][10];
        for (int i = 0; i < 100; i++) {
            data[i] = String.valueOf(i);
        }
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                cells[i][j] = new Cell(j, i, CellCondition.Empty);
            }
        }

        ArrayList<Ship> ships = new ArrayList<Ship>();
        ArrayList<Cell> positionAround = new ArrayList<Cell>();
        ships = generationShips(ships, positionAround);
        for (int i = 0; i < ships.size(); i++) {
            cells[ships.get(i).getBeginPos().getPosY()][ships.get(i).getBeginPos().getPosX()].setCellCondition(CellCondition.Ship);
            cells[ships.get(i).getEndPos().getPosY()][ships.get(i).getEndPos().getPosX()].setCellCondition(CellCondition.Ship);
            if (ships.get(i).getSize() > 2) {
                cellsFill(cells, ships, i, ships.get(i).getSize(), -1+ships.get(i).getSize());
            }
            if (ships.get(i).getSize() == 4) {
                cellsFill(cells, ships, i, ships.get(i).getSize(), 2);
            }
        }



        // set up the RecyclerView
        RecyclerView recyclerViewCreator = findViewById(R.id.recyclerViewCreator);
        RecyclerView recyclerViewOpponent = findViewById(R.id.recyclerViewOpponent);
        int numberOfColumns = 10;
        recyclerViewCreator.setLayoutManager(new GridLayoutManager(this, numberOfColumns));
        recyclerViewOpponent.setLayoutManager(new GridLayoutManager(this, numberOfColumns));
        adapter = new RecyclerViewAdapter(this, data, cells);
        adapter.setClickListener(this);
        recyclerViewCreator.setAdapter(adapter);
        recyclerViewOpponent.setAdapter(adapter);


    }

    public void cellsFill(Cell[][] cells, ArrayList<Ship> ships, int i, int size, int extra) {
        if (ships.get(i).getOrientation() == 0) {
            if (ships.get(i).getEndPos().getPosX() < ships.get(i).getBeginPos().getPosX()) {
                cells[ships.get(i).getEndPos().getPosY()][ships.get(i).getEndPos().getPosX() + (size - extra)].setCellCondition(CellCondition.Ship);
            } else {
                cells[ships.get(i).getEndPos().getPosY()][ships.get(i).getEndPos().getPosX() - (size - extra)].setCellCondition(CellCondition.Ship);
            }

        } else {
            if (ships.get(i).getEndPos().getPosY() < ships.get(i).getBeginPos().getPosY()) {
                cells[ships.get(i).getEndPos().getPosY() + (size - extra)][ships.get(i).getEndPos().getPosX()].setCellCondition(CellCondition.Ship);
            } else {
                cells[ships.get(i).getEndPos().getPosY() - (size - extra)][ships.get(i).getEndPos().getPosX()].setCellCondition(CellCondition.Ship);
            }
        }
    }
    /*Log.e("shipX", String.valueOf(ship.getBeginPos().posX));
                Log.e("shipY", String.valueOf(ship.getBeginPos().posY));
                for (int i = 0; i < positionAround.size(); i++) {
            if (positionAround.get(i) != null) {
                Log.e("posSize", String.valueOf(positionAround.size()));
                Log.e("posX", String.valueOf(positionAround.get(i).posX));
                Log.e("posY", String.valueOf(positionAround.get(i).posY));
                Log.e("----", "------------------");
            }
        }
                Log.e("####", "######################");*/


    public Cell exist(Cell cell) {
        if (cell.getPosX() < 10 && cell.getPosY() < 10 && cell.getPosX() >= 0 &&
                cell.getPosY() >= 0) {
            return cell;
        }
        return null;
    }



    public ArrayList<Cell> getAround(ArrayList<Cell> positionAround, Ship ship) {
        Cell begin = ship.getBeginPos();
        Cell end = ship.getEndPos();
        int size = ship.getSize();

        if (ship.getOrientation() == 0) {
            if (begin.getPosX() < end.getPosX()) {
                positionAround.add(exist(new Cell(end.getPosX() + 1, end.getPosY(), CellCondition.Ship)));
                positionAround.add(exist(new Cell(begin.getPosX() - 1, begin.getPosY(), CellCondition.Ship)));
                positionAround.add(exist(new Cell(end.getPosX() + 1, end.getPosY() + 1, CellCondition.Ship)));
                positionAround.add(exist(new Cell(end.getPosX() + 1, end.getPosY() - 1, CellCondition.Ship)));
            }
            else {
                positionAround.add(exist(new Cell(end.getPosX() - 1, end.getPosY(), CellCondition.Ship)));
                positionAround.add(exist(new Cell(begin.getPosX() + 1, begin.getPosY(), CellCondition.Ship)));
                positionAround.add(exist(new Cell(begin.getPosX() + 1, begin.getPosY() + 1, CellCondition.Ship)));
                positionAround.add(exist(new Cell(begin.getPosX() + 1, begin.getPosY() - 1, CellCondition.Ship)));
            }
            positionAround.add(exist(new Cell(begin.getPosX() - 1, begin.getPosY() + 1, CellCondition.Ship)));
            positionAround.add(exist(new Cell(begin.getPosX() - 1, begin.getPosY() - 1, CellCondition.Ship)));
            positionAround.add(exist(new Cell(begin.getPosX(), begin.getPosY() + 1, CellCondition.Ship)));
            positionAround.add(exist(new Cell(begin.getPosX(), begin.getPosY() - 1, CellCondition.Ship)));
            positionAround.add(exist(new Cell(end.getPosX(), end.getPosY() + 1, CellCondition.Ship)));
            positionAround.add(exist(new Cell(end.getPosX(), end.getPosY() - 1, CellCondition.Ship)));

            if (size > 2) {
                positionAround.add(exist(new Cell(end.getPosX() - 1, end.getPosY() + 1, CellCondition.Ship)));
                positionAround.add(exist(new Cell(end.getPosX() - 1, end.getPosY() - 1, CellCondition.Ship)));
            }
            if (size > 3) {
                positionAround.add(exist(new Cell(end.getPosX() - 2, end.getPosY() + 1, CellCondition.Ship)));
                positionAround.add(exist(new Cell(end.getPosX() - 2, end.getPosY() - 1, CellCondition.Ship)));
            }
        }
        else {
            if (begin.getPosY() < end.getPosY()) {
                positionAround.add(exist(new Cell(end.getPosX(), end.getPosY() + 1, CellCondition.Ship)));
                positionAround.add(exist(new Cell(begin.getPosX(), begin.getPosY() - 1, CellCondition.Ship)));
                positionAround.add(exist(new Cell(end.getPosX() + 1, end.getPosY() + 1, CellCondition.Ship)));
                positionAround.add(exist(new Cell(end.getPosX() - 1, end.getPosY() + 1, CellCondition.Ship)));
            }
            else {
                positionAround.add(exist(new Cell(end.getPosX(), end.getPosY() - 1, CellCondition.Ship)));
                positionAround.add(exist(new Cell(begin.getPosX(), begin.getPosY() + 1, CellCondition.Ship)));
                positionAround.add(exist(new Cell(begin.getPosX() + 1, begin.getPosY() + 1, CellCondition.Ship)));
                positionAround.add(exist(new Cell(begin.getPosX() - 1, begin.getPosY() + 1, CellCondition.Ship)));
            }
            positionAround.add(exist(new Cell(begin.getPosX() + 1, begin.getPosY() - 1, CellCondition.Ship)));
            positionAround.add(exist(new Cell(begin.getPosX() - 1, begin.getPosY() - 1, CellCondition.Ship)));
            positionAround.add(exist(new Cell(begin.getPosX() + 1, begin.getPosY(), CellCondition.Ship)));
            positionAround.add(exist(new Cell(begin.getPosX() - 1, begin.getPosY(), CellCondition.Ship)));
            positionAround.add(exist(new Cell(end.getPosX() + 1, end.getPosY(), CellCondition.Ship)));
            positionAround.add(exist(new Cell(end.getPosX() - 1, end.getPosY(), CellCondition.Ship)));

            if (size > 2) {
                positionAround.add(exist(new Cell(end.getPosX() + 1, end.getPosY() - 1, CellCondition.Ship)));
                positionAround.add(exist(new Cell(end.getPosX() - 1, end.getPosY() - 1, CellCondition.Ship)));
            }
            if (size > 3) {
                positionAround.add(exist(new Cell(end.getPosX() + 1, end.getPosY() - 2, CellCondition.Ship)));
                positionAround.add(exist(new Cell(end.getPosX() - 1, end.getPosY() - 2, CellCondition.Ship)));
            }
        }
        return positionAround;
    }

    public ArrayList<Ship> generationShips(ArrayList<Ship> ships, ArrayList<Cell> positionAround) {
        int[] sizes = {1, 1, 1, 1, 2, 2, 2, 3, 3, 4};
        Random random = new Random();
        for (int size: sizes) {
            while (true) {
                boolean contains = false;
                int posX = random.nextInt(10);
                int posY = random.nextInt(10);
                int orientation = random.nextInt(2);
                Cell posBegin = new Cell(posX, posY, CellCondition.Ship);
                Cell posEnd;
                if (orientation == 0) {
                    if (posX < 11 - size) {
                        posEnd = new Cell(posX + size - 1, posY, CellCondition.Ship);
                    } else {
                        posEnd = new Cell(posX - (size - 1), posY, CellCondition.Ship);
                    }
                } else {
                    if (posY < 11 - size) {
                        posEnd = new Cell(posX, posY + size - 1, CellCondition.Ship);
                    } else {
                        posEnd = new Cell(posX, posY - (size - 1), CellCondition.Ship);
                    }

                }

                Ship ship = new Ship(size, posBegin, posEnd, orientation);
                if (ships.contains(ship)) {
                    continue;
                }
                for (int i = 0; i < positionAround.size(); i++) {
                    if (positionAround.get(i) != null) {
                        if (positionAround.get(i).getPosY() == posBegin.getPosY() &&
                                positionAround.get(i).getPosX() == posBegin.getPosX() ||
                                positionAround.get(i).getPosY() == posEnd.getPosY() &&
                                        positionAround.get(i).getPosX() == posEnd.getPosX()) {
                            contains = true;
                            break;
                        }
                        if (orientation == 0) {
                            if (size > 2) {
                                if ((positionAround.get(i).getPosY() == posBegin.getPosY() &&
                                        positionAround.get(i).getPosX() == posBegin.getPosX() + 1) ||
                                        (positionAround.get(i).getPosY() == posBegin.getPosY() &&
                                                positionAround.get(i).getPosX() == posBegin.getPosX() - 1)) {
                                    contains = true;
                                    break;
                                }
                            }
                            if (size == 4) {
                                if ((positionAround.get(i).getPosY() == posBegin.getPosY() &&
                                        positionAround.get(i).getPosX() == posBegin.getPosX() + 2) ||
                                        (positionAround.get(i).getPosY() == posBegin.getPosY() &&
                                                positionAround.get(i).getPosX() == posBegin.getPosX() - 2)) {
                                    contains = true;
                                    break;
                                }
                            }

                        } else {
                            if (size > 2) {
                                if ((positionAround.get(i).getPosY() == posBegin.getPosY() + 1 &&
                                        positionAround.get(i).getPosX() == posBegin.getPosX()) ||
                                        (positionAround.get(i).getPosY() == posBegin.getPosY() - 1 &&
                                                positionAround.get(i).getPosX() == posBegin.getPosX())) {
                                    contains = true;
                                    break;
                                }
                            }
                            if (size == 4) {
                                if ((positionAround.get(i).getPosY() == posBegin.getPosY() + 2 &&
                                        positionAround.get(i).getPosX() == posBegin.getPosX()) ||
                                        (positionAround.get(i).getPosY() == posBegin.getPosY() - 2 &&
                                                positionAround.get(i).getPosX() == posBegin.getPosX())) {
                                    contains = true;
                                    break;
                                }
                            }

                        }
                    }
                }
                if (contains) {
                    continue;
                }
                ships.add(ship);
                positionAround = getAround(positionAround, ship);
                break;
            }
        }
        return ships;
    }

    @Override
    public void onItemClick(View view, int position) {
        Log.i("TAG", "You clicked number " + adapter.getItem(position) + ", which is at cell position " + position);
        textViewCell = view.findViewById(R.id.info_text);
        if (textViewCell.getText().equals("")) {
            textViewCell.setText("X");
            cells[position % 10][position / 10].setCellCondition(CellCondition.Hit);
        }


    }
}