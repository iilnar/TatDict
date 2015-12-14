package com.example.ilnarsabirzyanov.tatdict;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.RotateAnimation;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

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
    AsyncReader asyncReader;

    public DictionaryFragment() {
        setRetainInstance(true);
    }

    private void swapTextView(TextView a, TextView b) {
        CharSequence t = a.getText();
        a.setText(b.getText());
        b.setText(t);
    }

    public boolean search (String s) {
        if (dictionary.state == Dictionary.State.RUNNING) return false;
        ArrayList<DictionaryRecord> res = dictionary.search(s);
        if (res.size() == 0) {
            res = dictionary.deepSearch(s);
        }
        records.clear();
        for (DictionaryRecord dr : res) {
            records.add(dr);
        }
        recyclerViewAdapter.notifyDataSetChanged();
        return true;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dictionary_fragment, container, false);
    }

    private void extractDictionary(File f) {
        this.getActivity().findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
        if (asyncReader != null) {
            if (dictionary.state == dictionary.state.RUNNING) {
                asyncReader.cancel(true);
            }
        }
        dictionary.state = Dictionary.State.RUNNING;
        asyncReader = new AsyncReader(this);
        asyncReader.execute(f);
    }

    static class AsyncReader extends AsyncTask<File, Void, Boolean> {
        DictionaryFragment fragment;
        AsyncReader(DictionaryFragment fragment) {
            this.fragment = fragment;
        }

        @Override
        protected Boolean doInBackground(File... files) {
            try {
                return fragment.dictionary.readDump(files[0]);
            } catch (Exception e) {
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean res) {
            if (res) {
                fragment.search(fragment.editText.getText().toString());
                fragment.getActivity().findViewById(R.id.progressBar).setVisibility(View.INVISIBLE);
            } else {
                Toast.makeText(fragment.getActivity(), "Обновите базу словаря.", Toast.LENGTH_SHORT).show();
            }
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
            if (recyclerViewAdapter.isShowingAlertDialog()) {
                recyclerViewAdapter.showAlertDialog();
            }
        }
        if (dir == null) {
            dir = "tat_to_rus";
        }
        if (dir.equals("rus_to_tat")) {
            swapTextView((TextView) rootView.findViewById(R.id.fromLang), (TextView) rootView.findViewById(R.id.toLang));
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
                        search(editText.getText().toString());
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                    }
                }
        );

        editText.setOnEditorActionListener(
                new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        if (actionId == EditorInfo.IME_ACTION_DONE) {
                            return search(editText.getText().toString());
                        }
                        return false;
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
                        ra.setDuration(250);
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
                        swapTextView((TextView) rootView.findViewById(R.id.fromLang), (TextView) rootView.findViewById(R.id.toLang));
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
}
