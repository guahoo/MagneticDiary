package com.magneticdiary;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import static com.magneticdiary.R.id.latitudeView;
import static com.magneticdiary.R.id.loaderCitylist;
import static com.magneticdiary.R.id.longitudeView;
import static com.magneticdiary.R.id.magneticFieldView;
import static com.magneticdiary.R.id.ok_Btn;
import static com.magneticdiary.R.id.placeNameEditText;
import static com.magneticdiary.R.id.placeNotesEditText;
import static com.magneticdiary.R.layout;


public class Library_window {

    private static final String CITY_NAME_DISPLAY_LIST = "name";
    private static final String DISPLAYED_NAME = "display_name";
    private static final String LON = "lon";
    private static final String LAT = "lat";
    private static final String NO_CONNECTION = "No connection";
    private static final String NOT_FOUND = "Not found";
    private static final String DATE_PATTERN = "dd.MM.yy";

    private Context context;

    Dialog d;
    TinyDb tinydb;

    private ImageButton ok_button;
    EditText placeName, notesEditText;
    TextView magneticFieldValue;
    static TextView latitudeTextView,longitudeTextView;
    String id;
    private static ProgressBar loader;




    public Library_window(Context context,String id) {
        this.context = context;
        this.id = id;
    }


    public void showMenuDialog() {
        init();
        ok_button.setOnClickListener(v -> {
            savePlace(id,createStringSet());
            d.hide();
            PlaceLibraryActivity.showWork(PlaceLibraryActivity.notesMap());
        });
        d.setOnDismissListener(dialog -> d.hide());
        d.show();
    }

    private void init() {
        d = new Dialog(context);
        d.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(d.getWindow()).setBackgroundDrawable
                (new ColorDrawable(android.graphics.Color.TRANSPARENT));
        Window window = d.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.TOP;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(wlp);
        d.setContentView(layout.library_window);
        ok_button = d.findViewById(ok_Btn);
        placeName = d.findViewById(placeNameEditText);
        loader = d.findViewById(loaderCitylist);
        magneticFieldValue=d.findViewById(magneticFieldView);
        latitudeTextView=d.findViewById(latitudeView);
        longitudeTextView=d.findViewById(longitudeView);
        notesEditText=d.findViewById(placeNotesEditText);
        tinydb = new TinyDb(context);
        fillingInTextView();
    }

    private void fillingInTextView(){

        setTextinLibraryTextView(placeName,0);
        setTextinLibraryTextView(magneticFieldValue,1);
        setTextinLibraryTextView(latitudeTextView,2);
        setTextinLibraryTextView(longitudeTextView,3);
        setTextinLibraryTextView(notesEditText,4);

    }

    private void setTextinLibraryTextView(TextView textView, int localposition){
        textView.setText(tinydb.getListString(id).get(localposition));
    }

    protected void savePlace(String id, ArrayList<String> params) {
       tinydb.putListString(id,params);
    }

    private ArrayList<String> createStringSet(){

        String id=String.valueOf(tinydb.getAll().size());
        ArrayList<String> nameInfo=new ArrayList<>();
        nameInfo.add(fromTextView(placeName));
        nameInfo.add(fromTextView(magneticFieldValue));
        nameInfo.add(fromTextView(latitudeTextView));
        nameInfo.add(fromTextView(longitudeTextView));
        nameInfo.add(fromTextView(notesEditText));
        nameInfo.add(id);
        nameInfo.add(dateString());
        return nameInfo;


    }

    private String fromTextView(TextView textView){
        return textView.getText().toString();
    }










    String dateString(){
        Date currentTime= java.util.Calendar.getInstance().getTime();
        SimpleDateFormat dateFormat=new SimpleDateFormat(DATE_PATTERN, Locale.ENGLISH);
        return dateFormat.format(currentTime);
    }








}
