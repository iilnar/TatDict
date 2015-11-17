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
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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
    String dir;
    EditText editText;

    public DictionaryFragment() {
        setRetainInstance(true);
    }

    private void swapTextViev(TextView a, TextView b) {
        CharSequence t = a.getText();
        a.setText(b.getText());
        b.setText(t);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dictionary_fragment, container, false);
    }

//    @Override
//    public void onSaveInstanceState(Bundle outState) {
//        outState.putString("dir", dir);
//    }

    private boolean extractDictionary(File f) {
        try {
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
        rootView.findViewById(R.id.text).setVisibility(View.VISIBLE);

        recyclerView = (RecyclerView) getView().findViewById(R.id.recyclerView);

        if (recyclerViewAdapter == null) {
            recyclerViewAdapter = new RecyclerViewAdapter(records);
        } else {
            if (recyclerViewAdapter.isShowingAlertDialog) {
                recyclerViewAdapter.showAlertDialog();
            }
        }
        if (dir == null) {
            dir = "tat_to_rus";
        }
        if (dir.equals("rus_to_tat")) {
            swapTextViev((TextView) rootView.findViewById(R.id.fromLang), (TextView) rootView.findViewById(R.id.toLang));
        }
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        //RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();

        extractDictionary(new File(getActivity().getExternalFilesDir(null), dir + ".file"));

        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL_LIST));
        recyclerView.setLayoutManager(linearLayoutManager);
        //recyclerView.setItemAnimator(itemAnimator);

        editText = (EditText) rootView.findViewById(R.id.text);
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
                        ImageButton ib = (ImageButton) rootView.findViewById(R.id.swapButton);
                        RotateAnimation ra = new RotateAnimation(0, 180, ib.getWidth() / 2, ib.getHeight() / 2);
                        ra.setFillAfter(true);
                        ra.setDuration(500);
                        ib.setAnimation(ra);
                        switch (dir) {
                            case "tat_to_rus":
                                dir = "rus_to_tat";
                                break;
                            case "rus_to_tat":
                                dir = "tat_to_rus";
                                break;
                        }
                        extractDictionary(new File(getActivity().getExternalFilesDir(null), dir + ".file"));
                        ArrayList<DictionaryRecord> res = dictionary.search(editText.getText().toString());
                        records.clear();
                        for (DictionaryRecord dr : res) {
                            records.add(dr);
                        }
                        res = null;
                        recyclerViewAdapter.notifyDataSetChanged();
                        swapTextViev((TextView) rootView.findViewById(R.id.fromLang), (TextView) rootView.findViewById(R.id.toLang));
                    }
                }
        );

        int[] buttons = {R.id.button1, R.id.button2, R.id.button3, R.id.button4, R.id.button5, R.id.button6};
        for (int button : buttons) {
            rootView.findViewById(button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    editText.getText().insert(editText.getSelectionStart(), ((Button) v).getText().toString().toLowerCase());
                }
            });
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
