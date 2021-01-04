package com.example.shipbattle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class GameActivity extends AppCompatActivity implements RecyclerViewAdapter.ItemClickListener {
    TextView textViewCell;
    RecyclerViewAdapter adapter;
    //Cell[][] cells;
    ArrayList<ArrayList<Cell>> cells, tempCells;
    RecyclerView recyclerViewCreator, recyclerViewOpponent, recyclerViewTemp, recyclerView;
    DatabaseReference databaseReference;
    Game gameTemp;
    ArrayList<Game> games;
    ArrayList<DatabaseReference> gameReferences;
    String opponentId, creatorId, lobbyId;
    ValueEventListener valueEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        Bundle extras = getIntent().getExtras();
        opponentId = extras.getString("opponentId");
        creatorId = extras.getString("creatorId");
        lobbyId = extras.getString("lobbyId");

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Game").child(lobbyId);
        games = new ArrayList<Game>();
        String[] data = new String[100];
        ArrayList<Cell> tmp;
        cells = new ArrayList<ArrayList<Cell>>();
        tempCells = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            tmp = new ArrayList<>();
            for (int j = 0; j < 10; j++) {
                tmp.add(new Cell(j, i, CellCondition.Empty));
            }
            cells.add(tmp);
        }
        for (int i = 0; i < 10; i++) {
            tmp = new ArrayList<>();
            for (int j = 0; j < 10; j++) {
                tmp.add(new Cell(j, i, CellCondition.Empty));
            }
            tempCells.add(tmp);
        }

        for (int i = 0; i < 100; i++) {
            data[i] = String.valueOf(i);
        }
        int numberOfColumns = 10;



        if (FirebaseAuth.getInstance().getCurrentUser().getUid().equals(opponentId)) {
            ArrayList<Ship> ships = new ArrayList<Ship>();
            cellsGeneration(cells, ships);
            databaseReference.child("opponent").setValue(cells);
            recyclerViewOpponent = findViewById(R.id.recyclerViewOpponent);
            recyclerViewOpponent.setLayoutManager(new GridLayoutManager(this, numberOfColumns));
            recyclerViewTemp = recyclerViewOpponent;

        } else {
            ArrayList<Ship> ships = new ArrayList<Ship>();
            cellsGeneration(cells, ships);
            databaseReference.child("creator").setValue(cells);
            recyclerViewCreator = findViewById(R.id.recyclerViewCreator);
            recyclerViewCreator.setLayoutManager(new GridLayoutManager(this, numberOfColumns));
            recyclerViewTemp = recyclerViewCreator;
        }

        if (FirebaseAuth.getInstance().getCurrentUser().getUid().equals(opponentId)) {
            RecyclerViewAdapter adapter = new RecyclerViewAdapter(this, data, tempCells);
            adapter.setClickListener(this);
            recyclerViewCreator = findViewById(R.id.recyclerViewCreator);
            recyclerViewCreator.setLayoutManager(new GridLayoutManager(this, numberOfColumns));
            recyclerViewCreator.setAdapter(adapter);
        } else {
            RecyclerViewAdapter adapter = new RecyclerViewAdapter(this, data, tempCells);
            adapter.setClickListener(this);
            recyclerViewOpponent = findViewById(R.id.recyclerViewOpponent);
            recyclerViewOpponent.setLayoutManager(new GridLayoutManager(this, numberOfColumns));
            recyclerViewOpponent.setAdapter(adapter);
        }


        adapter = new RecyclerViewAdapter(this, data, cells);
        //adapter.setClickListener(this);
        recyclerViewTemp.setAdapter(adapter);

        /*adapterOpponent = new RecyclerViewAdapter(this, data, cellsOpponent);
        adapterOpponent.setClickListener(this);
        recyclerViewOpponent.setAdapter(adapterOpponent);*/


    }

    public void cellsFill(/*Cell[][]*/ArrayList<ArrayList<Cell>> cells, ArrayList<Ship> ships, int i, int size, int extra) {
        if ((ships.get(i)).getOrientation() == 0) {
            if (((ships.get(i)).getEndPos()).getPosX() < ((ships.get(i)).getBeginPos()).getPosX()) {
                ((cells.get(ships.get(i).getEndPos().getPosY())).get(ships.get(i).getEndPos().getPosX() + (size - extra))).setCellCondition(CellCondition.Ship);
            } else {
                ((cells.get(ships.get(i).getEndPos().getPosY())).get(ships.get(i).getEndPos().getPosX() - (size - extra))).setCellCondition(CellCondition.Ship);
            }

        } else {
            if (((ships.get(i)).getEndPos()).getPosY() < ((ships.get(i)).getBeginPos()).getPosY()) {
                ((cells.get(ships.get(i).getEndPos().getPosY() + (size - extra))).get(ships.get(i).getEndPos().getPosX())).setCellCondition(CellCondition.Ship);
            } else {
                ((cells.get(ships.get(i).getEndPos().getPosY() - (size - extra))).get(ships.get(i).getEndPos().getPosX())).setCellCondition(CellCondition.Ship);
            }
        }
    }

    public void cellsGeneration(/*Cell[][]*/ArrayList<ArrayList<Cell>> cells, ArrayList<Ship> ships) {

        /*for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                cells.get(i).set(j, new Cell(j, i, CellCondition.Empty));
            }
        }*/
        ArrayList<Cell> positionAround = new ArrayList<Cell>();
        ships = generationShips(ships, positionAround);
        for (int i = 0; i < ships.size(); i++) {
            ((cells.get(ships.get(i).getBeginPos().getPosY())).get(ships.get(i).getBeginPos().getPosX())).setCellCondition(CellCondition.Ship);
            ((cells.get(ships.get(i).getEndPos().getPosY())).get(ships.get(i).getEndPos().getPosX())).setCellCondition(CellCondition.Ship);
            if ((ships.get(i)).getSize() > 2) {
                cellsFill(cells, ships, i, ships.get(i).getSize(), -1 + ships.get(i).getSize());
            }
            if ((ships.get(i)).getSize() == 4) {
                cellsFill(cells, ships, i, ships.get(i).getSize(), 2);
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
            } else {
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
        } else {
            if (begin.getPosY() < end.getPosY()) {
                positionAround.add(exist(new Cell(end.getPosX(), end.getPosY() + 1, CellCondition.Ship)));
                positionAround.add(exist(new Cell(begin.getPosX(), begin.getPosY() - 1, CellCondition.Ship)));
                positionAround.add(exist(new Cell(end.getPosX() + 1, end.getPosY() + 1, CellCondition.Ship)));
                positionAround.add(exist(new Cell(end.getPosX() - 1, end.getPosY() + 1, CellCondition.Ship)));
            } else {
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
        for (int size : sizes) {
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
        //Log.i("TAG", "You clicked number " + adapter.getItem(position) + ", which is at cell position " + position);

        //textViewCell.setText("X");
        int posY = position / 10;
        int posX = position % 10;
        final String[] condition = new String[1];
        gameTemp = null;
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Game game = snapshot.getValue(Game.class);
                textViewCell = view.findViewById(R.id.info_text);
                Log.e("g2", game.getOpponent().get(posY).get(posX).getCellCondition().toString());
                Log.e("g1", game.getCreator().get(posY).get(posX).getCellCondition().toString());
                Cell cell;
                if (FirebaseAuth.getInstance().getCurrentUser().getUid().equals(opponentId)) {
                    cell = game.getCreator().get(posY).get(posX);
                } else {
                    cell = game.getOpponent().get(posY).get(posX);
                }
                if (cell.getCellCondition().equals(CellCondition.Ship)) {
                    textViewCell.setBackgroundColor(Color.CYAN);
                    cell.setCellCondition(CellCondition.Hit);
                    condition[0] = CellCondition.Hit.toString();
                } else {
                    cell.setCellCondition(CellCondition.Miss);
                    condition[0] = CellCondition.Miss.toString();
                }
                Log.e("condition", condition[0]);
                gameTemp = game;
                if (cell.getCellCondition().equals(CellCondition.Hit) || cell.getCellCondition().equals(CellCondition.Miss)) {
                    textViewCell.setText("X");
                }
                if (game.getOpponent().get(posY).get(posX).getCellCondition().equals(CellCondition.Hit) ||
                        game.getOpponent().get(posY).get(posX).getCellCondition().equals(CellCondition.Miss) ||
                        game.getCreator().get(posY).get(posX).getCellCondition().equals(CellCondition.Hit) ||
                        game.getCreator().get(posY).get(posX).getCellCondition().equals(CellCondition.Miss)) {
                    textViewCell.setText("X");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        databaseReference.addValueEventListener(valueEventListener);

        if (gameTemp != null) {
            Log.e("OPPONENT", gameTemp.getOpponent().get(posY).get(posX).getCellCondition().toString());
            Log.e("CREATOR", gameTemp.getCreator().get(posY).get(posX).getCellCondition().toString());
        }
        /*if (FirebaseAuth.getInstance().getCurrentUser().getUid().equals(opponentId)) {

            if ((databaseReference.getRef().getParent().child("opponent").child(String.valueOf(posY))
                    .child(String.valueOf(posX)).child("cellCondition").toString()).equals(CellCondition.Ship.toString())) {
                databaseReference.child("creator").child(String.valueOf(posY))
                        .child(String.valueOf(posX)).child("cellCondition").setValue(CellCondition.Hit.toString());
            } else {
                databaseReference.child("creator").child(String.valueOf(posY))
                        .child(String.valueOf(posX)).child("cellCondition").setValue(CellCondition.Miss.toString());
            }
        } else {
            if ((databaseReference.getRef().getParent().child("creator").child(String.valueOf(posY))
                    .child(String.valueOf(posX)).child("cellCondition").toString()).equals(CellCondition.Ship.toString())) {
                databaseReference.child("opponent").child(String.valueOf(posY))
                        .child(String.valueOf(posX)).child("cellCondition").setValue(CellCondition.Hit.toString());
            } else {
                databaseReference.child("opponent").child(String.valueOf(posY))
                        .child(String.valueOf(posX)).child("cellCondition").setValue(CellCondition.Miss.toString());
            }
        }*/

    }
}