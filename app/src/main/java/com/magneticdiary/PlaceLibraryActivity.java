package com.magneticdiary;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


public class PlaceLibraryActivity extends AppCompatActivity {
    static ListView lv;
    static TinyDb tinyDb;
    static ListViewAdapter adapter;
    static HashMap<String, ArrayList<String>> mappy = new HashMap<>();
    TextView emptyList;


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);




        tinyDb = new TinyDb(getApplicationContext());

        setContentView(R.layout.activity_placename_library);
        lv = findViewById(R.id.lv);
        emptyList=findViewById(R.id.emptyView);

        initialListViewMap();

        adapter = new ListViewAdapter(mappy);
        lv.setAdapter(adapter);
        showWork(mappy);

        lv.setOnItemClickListener((parent, view, position, id) -> {
            Library_window library_window = new Library_window(PlaceLibraryActivity.
                    this, String.valueOf(position));
            library_window.showMenuDialog();

        });

        lv.setEmptyView(emptyList);

        OnSwipeTouchListener touchListener = new OnSwipeTouchListener(lv,
                new OnSwipeTouchListener.OnSwipeCallback() {
                    @Override
                    public void onSwipeLeft(ListView listView, int[] reverseSortedPositions, int position) {
                        onSwipeEvent(position);

                    }

                    @Override
                    public void onSwipeRight(ListView lv, int[] reverseSortedPositions, int position) {
                        onSwipeEvent(position);
                    }
                }, false, // example : left action = dismiss
                true); // example : right action without dismiss animation
        lv.setOnTouchListener(touchListener);


    }

    private void onSwipeEvent(int position) {
        View_dialog alert_dialog = new View_dialog();
        alert_dialog.showDialog(PlaceLibraryActivity.this, getString(R.string.deleting),position,lv);
    }

    public static void initialListViewMap() {
        mappy = new HashMap<>();
        for (int i = 0; i < tinyDb.getAll().size(); i++) {
            String id = String.valueOf(i);
            mappy.put(id, tinyDb.getListString(id));
        }
    }

    public static void showWork(HashMap<String, ArrayList<String>> notes) {
        adapter = new ListViewAdapter(notes);

            lv.setAdapter(adapter);



    }

    static HashMap<String, ArrayList<String>> notesMap() {
        HashMap<String, ArrayList<String>> mappy = new HashMap<>();

            for (int i = 0; i < tinyDb.getAll().size(); i++) {
                String id = String.valueOf(i);
                mappy.put(id, tinyDb.getListString(id));
            }


        return mappy;
    }

    public static void remove(int position) {
        tinyDb.remove(String.valueOf(position));
        repairCatalog();
        initialListViewMap();
        showWork(mappy);
        lv.setAdapter(adapter);


    }


    static void repairCatalog(){
        ArrayList<ArrayList<String>> arrayLists=new ArrayList<>();
        Map<String,?> keys = tinyDb.getAll();
        for(Map.Entry<String,?> entry : keys.entrySet()){
            arrayLists.add(new ArrayList<>(Arrays.asList(TextUtils.split(entry.getValue().toString(),
                    "‚‗‚"))));

        }

        tinyDb.clear();
        mappy = new HashMap<>();

        for (int i=0;i<arrayLists.size();i++){
            tinyDb.putListString(String.valueOf(i),arrayLists.get(i));
        }
    }

}
