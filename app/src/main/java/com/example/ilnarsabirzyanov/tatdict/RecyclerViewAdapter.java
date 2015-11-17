package com.example.ilnarsabirzyanov.tatdict;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Ilnar Sabirzyanov on 07.11.2015.
 */
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> implements View.OnClickListener{
    AlertDialog.Builder builder;
    protected boolean isShowingAlertDialog;
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
            builder = new AlertDialog.Builder(v.getContext());
            String text = dictionaryRecord.translation.replace("<i>", "<font color = 'red'>");
            text = text.replace("</i>", "</font>");
            builder.setTitle(dictionaryRecord.word);
            builder.setMessage(Html.fromHtml(text));
            builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    isShowingAlertDialog = false;
                }
            });
            isShowingAlertDialog = true;
            showAlertDialog();
        }
    }

    public boolean isShowingAlertDialog() {
        return isShowingAlertDialog;
    }

    public void showAlertDialog() {
        builder.create().show();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        private TextView word;

        public ViewHolder(View itemView) {
            super(itemView);
            word = (TextView)itemView.findViewById(R.id.recyclerViewItemName);
        }

    }
}
