package com.jun.rxdemo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jun.rxdemo.R;

import java.util.List;

public class BindingAdapter extends RecyclerView.Adapter<MyViewHolder> {

    private List<String> list;
    private Context context;

    public BindingAdapter(List<String> list, Context context){
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_item, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.getTextView().setText(list.get(position));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}

class MyViewHolder extends RecyclerView.ViewHolder {
    private final TextView textView;
    public MyViewHolder(View v) {
        super(v);

        textView = (TextView) v.findViewById(R.id.tv_content);
    }
    public TextView getTextView() {
        return textView;
    }
}