package com.example.shipbattle;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private String[] mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private View view;
    private /*Cell[][]*/ArrayList<ArrayList<Cell>> cells;

    // data is passed into the constructor
    RecyclerViewAdapter(Context context, String[] data, /*Cell[][]*/ArrayList<ArrayList<Cell>> cells) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.cells = cells;
    }

    // inflates the cell layout from xml when needed
    @Override
    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view = mInflater.inflate(R.layout.recycleremptycell_item, parent, false);

        return new ViewHolder(view);
    }

    // binds the data to the TextView in each cell
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.myTextView.setText(mData[position]);
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                if ((((cells.get(i)).get(j)).getCellCondition()).equals(CellCondition.Ship) && i * 10 + j == position) {
                    holder.myTextView.setBackgroundColor(Color.CYAN);
                }
            }

        }
    }

    // total number of cells
    @Override
    public int getItemCount() {
        return mData.length;
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView myTextView;

        ViewHolder(View itemView) {
            super(itemView);
            myTextView = itemView.findViewById(R.id.info_text);
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }


        public void setColor() {

        }
    }

    // convenience method for getting data at click position
    String getItem(int id) {
        return mData[id];
    }
    //View getView(int id) {;}


    // allows clicks events to be caught
    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }


    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}
