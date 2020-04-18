package com.magneticdiary;

import android.app.Activity;
import android.app.Dialog;
import android.view.Window;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class View_dialog {
    int position;
    ListView listView;

    public void showDialog(Activity activity, String msg, int position,ListView listView){
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.alert_dialog);
        TextView text = (TextView) dialog.findViewById(R.id.text_dialog);
        text.setText(msg);
        Button dialogButtonYes = (Button) dialog.findViewById(R.id.btn_yes);
        Button dialogButtonNo = (Button) dialog.findViewById(R.id.btn_no);
        this.position=position;
        this.listView=listView;



        dialogButtonYes.setOnClickListener(v -> {
            OnSwipeTouchListener.resetAnimation();
            PlaceLibraryActivity.remove(this.position);
            dialog.dismiss();
        });


        dialogButtonNo.setOnClickListener(v -> {
            OnSwipeTouchListener.resetAnimation();
            dialog.dismiss();
        });


        dialog.setOnDismissListener(v->{
                    OnSwipeTouchListener.resetAnimation();
                }
        );

        dialog.show();

    }
}