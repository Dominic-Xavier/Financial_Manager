package com.myapp.finance;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class RecyclerBaseAdapter extends RecyclerView.Adapter<RecyclerBaseAdapter.Viewholder> {

    Context context;
    private List<String> year;
    private List<String> month;
    private List<String>expense;
    private List<String>income;
    private List<Integer>total;



    public RecyclerBaseAdapter(Context context,List year, List<String> month, List<String> expense, List<String> income, List<Integer> total) {
        //View.OnClickListener mOnClickListener = new RecyclerBaseAdapter();
        this.context = context;
        this.year = year;
        this.month = month;
        this.expense = expense;
        this.income = income;
        this.total = total;
    }


    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_design,parent,false);
        Viewholder viewholder = new Viewholder(view);
        return viewholder;
    }


    @Override
    public void onBindViewHolder(@NonNull Viewholder holder, int position) {
        holder.Total.setText("Total:"+total.get(position));
        holder.Expense.setText("Expense:"+expense.get(position));
        holder.Income.setText("Income:"+income.get(position));
        holder.Month.setText(month.get(position)+"-"+year.get(position));
        int t = total.get(position);
        if(t<0)
        holder.Total.setTextColor(Color.parseColor("#FF0000"));
    }

    @Override
    public int getItemCount() {
        return month.size();
    }

    class Viewholder extends RecyclerView.ViewHolder {

        TextView Month,Expense,Income,Total;

        public Viewholder(@NonNull View itemView) {
            super(itemView);
            Month = itemView.findViewById(R.id.month);
            Expense = itemView.findViewById(R.id.expense);
            Income = itemView.findViewById(R.id.income);
            Total = itemView.findViewById(R.id.total);
        }
    }
    @FunctionalInterface
    interface RecyclerClickListener{
        void onClickView(int position);
    }
}


