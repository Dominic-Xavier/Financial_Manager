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

    /**
     * Called when RecyclerView needs a new {@link ViewHolder} of the given type to represent
     * an item.
     * <p>
     * This new ViewHolder should be constructed with a new View that can represent the items
     * of the given type. You can either create a new View manually or inflate it from an XML
     * layout file.
     * <p>
     * The new ViewHolder will be used to display items of the adapter using
     * {@link #onBindViewHolder(ViewHolder, int, List)}. Since it will be re-used to display
     * different items in the data set, it is a good idea to cache references to sub views of
     * the View to avoid unnecessary {@link View#findViewById(int)} calls.
     *
     * @param parent   The ViewGroup into which the new View will be added after it is bound to
     *                 an adapter position.
     * @param viewType The view type of the new View.
     * @return A new ViewHolder that holds a View of the given view type.
     * @see #getItemViewType(int)
     * @see #onBindViewHolder(ViewHolder, int)
     */
    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_design,parent,false);
        Viewholder viewholder = new Viewholder(view);
        return viewholder;
    }

    /**
     * Called by RecyclerView to display the data at the specified position. This method should
     * update the contents of the {@link ViewHolder#itemView} to reflect the item at the given
     * position.
     * <p>
     * Note that unlike {@link ListView}, RecyclerView will not call this method
     * again if the position of the item changes in the data set unless the item itself is
     * invalidated or the new position cannot be determined. For this reason, you should only
     * use the <code>position</code> parameter while acquiring the related data item inside
     * this method and should not keep a copy of it. If you need the position of an item later
     * on (e.g. in a click listener), use {@link ViewHolder#getAdapterPosition()} which will
     * have the updated adapter position.
     * <p>
     * Override {@link #onBindViewHolder(ViewHolder, int, List)} instead if Adapter can
     * handle efficient partial bind.
     *
     * @param holder   The ViewHolder which should be updated to represent the contents of the
     *                 item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
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


