package com.magneticdiary;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import static com.magneticdiary.R.*;
import static com.magneticdiary.R.id.*;


public class Dialog_add_place {


    private static final String DATE_PATTERN = "dd.MM.yy";

    private Context context;
    Dialog d;
    TinyDb tinydb;

    private ImageButton ok_button;
    EditText placeName, notesEditText;
    TextView magneticFieldValue;
    static TextView latitudeTextView,longitudeTextView;


    private static ProgressBar loader;


    public static void setLatitude(String latitude) {
        latitude = latitude;
    }
    public static void setLongitude(String longitude) {longitude = longitude;
    }
    static String latitude, longitude;


    public Dialog_add_place( Context context) {
        this.context = context;



    }


    public void showMenuDialog() {
        init();





        ok_button.setOnClickListener(v -> {
            String id=String.valueOf(tinydb.getAll().size());
            savePlace(id,createStringSet());
            d.hide();
            Toast.makeText(context, R.string.saved,Toast.LENGTH_LONG).show();
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




        d.setContentView(layout.input_dialog);
        ok_button = d.findViewById(ok_Btn);
        placeName = d.findViewById(placeNameEditText);
        loader = d.findViewById(loaderCitylist);
        latitudeTextView=d.findViewById(latitudeView);
        longitudeTextView=d.findViewById(longitudeView);
        magneticFieldValue=d.findViewById(magneticFieldView);
        notesEditText=d.findViewById(placeNotesEditText);
        loader.setVisibility(View.VISIBLE);
        tinydb = new TinyDb(context);
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


    public static void removeLoader() {

        loader.setVisibility(View.INVISIBLE);
    }

    public static void setTextCityName(String text) {


    }

    public void hideMenuDialog() {
        d.hide();
    }

    public void setMagneticFieldValue(String value) {

        value = value == null ? context.getString(string.Notmeter) : value;
        magneticFieldValue.setText(value);
    }

    String dateString(){
        Date currentTime= java.util.Calendar.getInstance().getTime();
        SimpleDateFormat dateFormat=new SimpleDateFormat(DATE_PATTERN, Locale.ENGLISH);
        return dateFormat.format(currentTime);
    }










}
