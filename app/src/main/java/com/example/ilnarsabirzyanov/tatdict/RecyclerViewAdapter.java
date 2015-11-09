package com.example.ilnarsabirzyanov.tatdict;

import android.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Ilnar Sabirzyanov on 07.11.2015.
 */
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> implements View.OnClickListener{

    private List<DictionaryRecord> dictionaryRecordList;

    public RecyclerViewAdapter(List<DictionaryRecord> l) {
        this.dictionaryRecordList = l;
    }

    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_item, parent, false);
        v.setOnClickListener(this);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerViewAdapter.ViewHolder holder, int position) {
        DictionaryRecord record = dictionaryRecordList.get(position);
        holder.word.setText(record.word);
        holder.itemView.setTag(R.id.tag_dict, record);
    }

    @Override
    public int getItemCount() {
        return dictionaryRecordList.size();
    }

    @Override
    public void onClick(View v) {
        DictionaryRecord dictionaryRecord = (DictionaryRecord)v.getTag(R.id.tag_dict);
        if (dictionaryRecord != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
            builder.setMessage(dictionaryRecord.translation).create().show();
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        private TextView word;

        public ViewHolder(View itemView) {
            super(itemView);
            word = (TextView)itemView.findViewById(R.id.recyclerViewItemName);
        }

    }
}
