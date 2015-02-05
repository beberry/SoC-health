package com.example.mymeds.fragments;

import com.example.mymeds.R;

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
import android.widget.PopupWindow;

public class ThirdFragment extends Fragment {

	PopupWindow pw;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_third, container, false);

		final Button acceptBtn = (Button) rootView.findViewById(R.id.accept);

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

		return rootView;
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
}