package com.softtech.apps.autocallrecorder;

import java.util.ArrayList;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class KeyBoardFragment extends Fragment implements onKeyBoardEvent{
	private Button one_btn;
    private Button two_btn;
    private Button three_btn;
    private Button four_btn;
    private Button five_btn;
    private Button six_btn;
    private Button seven_btn;
    private Button eight_btn;
    private Button nine_btn;
    private Button zero_btn;
    private Button back_btn;
    private Button done_btn;

    private StringBuilder sb;

    private onKeyBoardEvent keyboardEventListener;
    DatabaseHandler db;
    private int maxLength=4;
    private int currentLength;
    private String password;
    private ImageView imgDigit1,imgDigit2,imgDigit3,imgDigit4;
    private TextView tvNote;
    ArrayList<ImageView> imgArr = new ArrayList<ImageView>();
    
	public static KeyBoardFragment newInstance()
    {
		
        KeyBoardFragment fragment=new KeyBoardFragment();
        Bundle bundle = new Bundle();
        bundle.putString("et_value", "");
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
    	
        try{

            keyboardEventListener=(onKeyBoardEvent)activity;
        }
        catch(ClassCastException e)
        {
            Log.e("ClassCastException in KeyBoardFragment row 50",activity.toString()+" must implement onKeyboardEvent");
            e.printStackTrace();
        }

        super.onAttach(activity);
    }

    @Override
	public void onResume() {
		// TODO Auto-generated method stub
    	if( MainActivity.isLoginDB == 2){
			Intent intent = new Intent(getActivity(),MainActivity.class);
		    startActivity(intent);
		}
		super.onResume();
	}

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        sb = new StringBuilder(getArguments().getString("et_value"));
        Log.d("SB", "Gia tri cua StringBuilder luc khoi tao view = " + sb.toString());
    	//sb=new StringBuilder();
        currentLength = sb.length();
        View rootView=inflater.inflate(R.layout.login, container, false);
        
        imgDigit1 = (ImageView) rootView.findViewById(R.id.imgDigit1);
        imgDigit2 = (ImageView) rootView.findViewById(R.id.imgDigit2);
        imgDigit3 = (ImageView) rootView.findViewById(R.id.imgDigit3);
        imgDigit4 = (ImageView) rootView.findViewById(R.id.imgDigit4);
        imgArr.add(imgDigit1);
        imgArr.add(imgDigit2);
        imgArr.add(imgDigit3);
        imgArr.add(imgDigit4);
        
        tvNote = (TextView) rootView.findViewById(R.id.tvNote);
        one_btn=(Button)rootView.findViewById(R.id.one_btn);
        one_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                add("1");
                Log.d("KEYBOARD","Da bam vao so 1");
            }
        });
        two_btn=(Button)rootView.findViewById(R.id.two_btn);
        two_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                add("2");
            }
        });
        three_btn=(Button)rootView.findViewById(R.id.three_btn);
        three_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                add("3");

            }
        });
        four_btn=(Button)rootView.findViewById(R.id.four_btn);
        four_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                add("4");
            }
        });
        five_btn=(Button)rootView.findViewById(R.id.five_btn);
        five_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                add("5");

            }
        });
        six_btn=(Button)rootView.findViewById(R.id.six_btn);
        six_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {


                add("6");
            }
        });
        seven_btn=(Button)rootView.findViewById(R.id.seven_btn);
        seven_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                add("7");
            }
        });
        eight_btn=(Button)rootView.findViewById(R.id.eight_btn);
        eight_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                add("8");

            }
        });
        nine_btn=(Button)rootView.findViewById(R.id.nine_btn);
        nine_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                add("9");
            }
        });
        zero_btn=(Button)rootView.findViewById(R.id.zero_btn);
        zero_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if(sb.length()>0)
                    add("0");
            }
        });
        back_btn=(Button)rootView.findViewById(R.id.back_btn);
        back_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if(sb.length()>0)
                {
                    currentLength--;
                    sb.deleteCharAt((sb.length())-1);
                    keyboardEventListener.backButtonPressed(sb.toString());
                }
            }
        });
        back_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

            	if(sb.length() > 0){
            		sb.deleteCharAt(sb.length() - 1);
            		Log.d("LENTH","Current lengh = "+sb.length());
            		imgArr.get(currentLength-1).setImageResource(R.drawable.rad_unchecked);
            		currentLength --;
            	}
                keyboardEventListener.backLongPressed();
            }
        });
        done_btn=(Button)rootView.findViewById(R.id.exit_btn);
        done_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
            	getActivity().finish();
                //keyboardEventListener.doneButtonPressed(sb.toString());
            }
        });
        return rootView;
    }


    public int getMaxLength() {
        return maxLength;
    }

    public void setMaxLength(int maxLength) {
        this.maxLength = maxLength;
    }
    public void add(String num)
    {
    	Log.d("LENTH", "Current lengh before ="+currentLength);
        currentLength++;
        if(currentLength<=maxLength)
        {
            sb.append(num);
            imgArr.get(currentLength - 1).setImageResource(R.drawable.rad_checked);
            Log.d("LENTH", "Current lengh ="+currentLength);
            Log.d("INPUT","Gia tri cua StringBuilder = "+sb.toString());
            if(sb.length() == maxLength){
            	Log.d("CHECK","Check password o day");
            	if(sb.toString().equals(MainActivity.password)){
            		Log.d("CHECK","Dang nhap thanh cong roi, xin chuc mung");
            		// Sent intent to MainActivity
            		tvNote.setText("Correct");
            		tvNote.setTextColor(Color.parseColor("#059029"));
            		Intent intent = new Intent(getActivity(), MainActivity.class);
            	    MainActivity.isLoginDB = 2;
            	    startActivity(intent);
            		sb.delete(0, sb.length());
            	}else{
            		Log.d("CHECK","Sai pass cmnr");
            		tvNote.setText("Wrong pin");
            		tvNote.setTextColor(Color.parseColor("#9a210a"));
            		resetImageSource();
            		sb.setLength(0);
            		Log.d("INPUT","Gia tri cua StringBuilder after = "+sb.toString());
            	}	
            }
        }else{
        	sb.setLength(0);
        	currentLength = 0;
        }
        keyboardEventListener.numberIsPressed(sb.toString());
        
        Log.d("INPUT","OUT Gia tri cua StringBuilder = "+sb.toString());
    }
    
    public void resetImageSource(){
    	for(ImageView iv : imgArr){
    		iv.setImageResource(R.drawable.rad_unchecked);
    	}
    }

	@Override
	public void numberIsPressed(String total) {
		// TODO Auto-generated method stub
		Log.d("KEYBOARD", "Keyboard event on fragment");
	}

	@Override
	public void doneButtonPressed(String total) {
		// TODO Auto-generated method stub
		Log.d("KEYBOARD", "Keyboard event");
	}

	@Override
	public void backLongPressed() {
		// TODO Auto-generated method stub
		Log.d("KEYBOARD", "Keyboard event");
	}

	@Override
	public void backButtonPressed(String total) {
		// TODO Auto-generated method stub
		Log.d("KEYBOARD", "Keyboard event");  
	}

}
