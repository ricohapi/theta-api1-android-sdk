package com.theta360.sample.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;
import android.widget.TextView;

public class LogView extends ScrollView {
	private TextView textView;
	private static final String LINE_SEPARATOR = System.getProperty("line.separator");

	public LogView(Context context, AttributeSet attrs) {
		super(context, attrs);

		setFillViewport(true);
		textView = new TextView(context);
		textView.setBackgroundResource(android.R.color.darker_gray);
		textView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		this.addView(textView);
	}

	public void append(CharSequence newLine) {
		textView.append(newLine);
		textView.append(LINE_SEPARATOR);
		fullScroll(FOCUS_DOWN);
	}
}