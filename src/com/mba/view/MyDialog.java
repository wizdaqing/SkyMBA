package com.mba.view;

import com.example.skymba.R;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MyDialog extends Dialog implements android.view.View.OnClickListener {
	private TextView choice;
	private Button btOK;
	private Button btCancle;
    Context context;
    private int choice_question;
    private int ok;
    private int cancle;
    public MyDialog(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
        this.context = context;
    }
    public MyDialog(Context context, int theme,int choice_question,int ok,int cancle){
        super(context, theme);
        this.context = context;
        this.choice_question=choice_question;
        this.ok=ok;
        this.cancle=cancle;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        
        this.setContentView(R.layout.dialog);
        choice=(TextView)findViewById(R.id.choice_question);
        btOK=(Button)findViewById(R.id.dialog_button_ok);
        btCancle=(Button)findViewById(R.id.dialog_button_cancle);
        choice.setText(choice_question);
        btOK.setText(ok);
        btCancle.setText(cancle);
        btOK.setOnClickListener(this);
        btCancle.setOnClickListener(this);
         }
	@Override
	public void onClick(View v) {
		if(v==btOK){
			Toast.makeText(context, "Äúµã»÷ÁËOK", Toast.LENGTH_SHORT).show();
		}
		else if(v==btCancle){
			this.dismiss();
		}
	}

}
