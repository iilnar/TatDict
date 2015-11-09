package com.example.ilnarsabirzyanov.tatdict;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ilnar Sabirzyanov on 09.11.2015.
 */
public class DictionaryFragment extends Fragment {

    Dictionary dictionary = new Dictionary();
    final List<DictionaryRecord> records = new ArrayList<>();
    RecyclerView recyclerView;
    RecyclerViewAdapter recyclerViewAdapter;
    String dir = "tat_to_rus";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dictionary_fragment, container, false);
    }

    private boolean extractDictionary(File f) {
        try {
            dictionary = new Dictionary();
            dictionary.readDump(f);
            records.clear();
            for (DictionaryRecord dictionaryRecord : dictionary.a) {
                records.add(dictionaryRecord);
            }
            recyclerViewAdapter.notifyDataSetChanged();
            return true;
        } catch (Exception e) {
            return false;
        }

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        final View rootView = view.getRootView();
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
        rootView.findViewById(R.id.toolbarView).setVisibility(View.VISIBLE);
        recyclerView = (RecyclerView) getView().findViewById(R.id.recyclerView);
        recyclerViewAdapter = new RecyclerViewAdapter(records);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        //RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        extractDictionary(new File(getActivity().getExternalFilesDir(null), dir + ".file"));

        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL_LIST));
        recyclerView.setLayoutManager(linearLayoutManager);
        //recyclerView.setItemAnimator(itemAnimator);

        EditText editText = (EditText) rootView.findViewById(R.id.text);
        editText.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        ArrayList<DictionaryRecord> res = dictionary.search(s.toString());
                        records.clear();
                        for (DictionaryRecord dr : res) {
                            records.add(dr);
                        }
                        recyclerViewAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                    }
                }
        );

        rootView.findViewById(R.id.swapButton).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        switch (dir) {
                            case "tat_to_rus":
                                dir = "rus_to_tat";
                                break;
                            case "rus_to_tat":
                                dir = "tat_to_rus";
                                break;
                        }
                        extractDictionary(new File(getActivity().getExternalFilesDir(null), dir + ".file"));
                        CharSequence t = ((TextView) rootView.findViewById(R.id.fromLang)).getText();
                        ((TextView) rootView.findViewById(R.id.fromLang)).setText(((TextView) rootView.findViewById(R.id.toLang)).getText());
                        ((TextView) rootView.findViewById(R.id.toLang)).setText(t);
                    }
                }
        );
    }
}
