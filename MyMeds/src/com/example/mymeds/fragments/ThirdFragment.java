package com.example.mymeds.fragments;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.example.mymeds.R;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;

public class ThirdFragment extends Fragment {

	PopupWindow pw;
	OnDateEntrySelectedListener mCallback;
	long startDate, def = 0;
	boolean startPressed =false;
	EditText startDateText, endDateText;

	public interface OnDateEntrySelectedListener {
        public void onDateEntrySelected(boolean startDate, long defaultDate);
    }

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_third, container, false);

		final Button startBtn = (Button) rootView.findViewById(R.id.start);
		final Button endBtn = (Button) rootView.findViewById(R.id.end);
		startDateText = (EditText) rootView.findViewById(R.id.startDate);
		endDateText = (EditText) rootView.findViewById(R.id.endDate);

		startBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					startPressed = true;
					mCallback.onDateEntrySelected(true, System.currentTimeMillis());
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		});
		
		endBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(startDate==0){
					//Have the default date as tomorrow if the start date hasn't been
					//selected yet
					def = System.currentTimeMillis() + 86400000;
				}
				else{
					def = startDate + 86400000;
				}
				try {
					startPressed = false;
					mCallback.onDateEntrySelected(false, def);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		return rootView;
	}
	
	@Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (OnDateEntrySelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }



	private void initiatePopupWindow() {
		try {
			//We need to get the instance of the LayoutInflater, use the context of this activity
			LayoutInflater inflater = (LayoutInflater) getActivity()
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			//Inflate the view from a predefined XML layout
			View layout = inflater.inflate(R.layout.popup_layout,
					(ViewGroup) getActivity().findViewById(R.id.popup_element));
			// create a 300px width and 470px height PopupWindow
			pw = new PopupWindow(layout, 600, 600, true);

			pw.setAnimationStyle(R.style.Animation);
			pw.setContentView(layout);
			pw.setFocusable(true);
			pw.showAtLocation(layout, Gravity.CENTER, 0 , 0);   
			pw.setOutsideTouchable(false);

			Button nextButton = (Button) layout.findViewById(R.id.end_data_send_button);
			nextButton.setClickable(false);
			nextButton.setOnClickListener(next_button_click_listener);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private OnClickListener next_button_click_listener = new OnClickListener() {
		public void onClick(View v) {
			pw.dismiss();
		}
	};
	
	public void setDate(long date){
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(date);
		String text = formatter.format(c.getTime());
		
		if(startPressed){			
			 startDateText.setText(text);
		}
		else{
			endDateText.setText(text);
		}
	}
}