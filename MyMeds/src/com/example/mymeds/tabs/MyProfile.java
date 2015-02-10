package com.example.mymeds.tabs;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;

import com.example.mymeds.R;

public class MyProfile extends Activity {

	PopupWindow pw;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tab_third);

		final Button acceptBtn = (Button) findViewById(R.id.accept);

		acceptBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					initiatePopupWindow();
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		});		
	}


	private void initiatePopupWindow() {
		try {
			//We need to get the instance of the LayoutInflater, use the context of this activity
			LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			//Inflate the view from a predefined XML layout
			View layout = inflater.inflate(R.layout.popup_layout,
					(ViewGroup) findViewById(R.id.popup_element));
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
}