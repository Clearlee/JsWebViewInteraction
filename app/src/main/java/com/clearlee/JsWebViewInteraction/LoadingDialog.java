package com.clearlee.JsWebViewInteraction;

/**
 * Created by zerdoor_pc on 2015/11/9.
 * 加载dialog
 */

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.LinearLayout;


public class LoadingDialog extends Dialog {

    public String word = "";
     LinearLayout linearLayout;

     public LoadingDialog(Context context) {
        super(context, R.style.loadingDialogStyle);
    }

    public LoadingDialog(Context context, String word) {
        super(context, R.style.loadingDialogStyle);
        this.word = word;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_loading);

         linearLayout = (LinearLayout) this.findViewById(R.id.linearlayout);
         LinearLayout linearLayout = (LinearLayout) this.findViewById(R.id.linearlayout);
         linearLayout.getBackground().setAlpha(210);

    }

    @Override
    public void show() {
        super.show();
        if (word!=null&&word.length()>0)setText(word);
    }
    public void setText(String text){
        this.word=text;

     }

}