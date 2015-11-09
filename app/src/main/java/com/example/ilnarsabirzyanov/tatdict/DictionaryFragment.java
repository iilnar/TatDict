package com.example.ilnarsabirzyanov.tatdict;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ilnar Sabirzyanov on 09.11.2015.
 */
public class DictionaryFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.app_bar_main, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        final List<DictionaryRecord> records = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            records.add(new DictionaryRecord(Integer.toString(i), "i"));
        }
        try {
            Dictionary dictionary = new Dictionary();
            dictionary.readDump(new File(getActivity().getExternalFilesDir(null), "tat_to_rus.file"));
            for (DictionaryRecord dictionaryRecord : dictionary.a) {
                records.add(dictionaryRecord);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        final RecyclerView recyclerView = (RecyclerView) getView().findViewById(R.id.recyclerView);
        final RecyclerViewAdapter recyclerViewAdapter = new RecyclerViewAdapter(records);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();

        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL_LIST));
        recyclerView.setLayoutManager(linearLayoutManager);
        //recyclerView.setItemAnimator(itemAnimator);
    }
}
