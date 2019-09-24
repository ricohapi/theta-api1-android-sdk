package com.theta360.sample.view;

import java.util.List;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.theta360.sample.R;

public class ObjectListArrayAdapter extends ArrayAdapter<ObjectRow> {
	private List<ObjectRow> rows;
	private LayoutInflater inflater;

	public ObjectListArrayAdapter(Context context, int resourceIdOfListLayout, List<ObjectRow> rows) {
		super(context, resourceIdOfListLayout, rows);
		this.rows = rows;
		this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		if (view == null) {
			view = inflater.inflate(R.layout.listlayout_object, null);
		}

		ObjectRow row = (ObjectRow) rows.get(position);

		ImageView thumbnail = (ImageView) view.findViewById(R.id.object_thumbnail);
		if (row.isPhoto()) {
			thumbnail.setVisibility(View.VISIBLE);
			byte[] thumbnailImage = row.getThumbnail();
			thumbnail.setImageBitmap(BitmapFactory.decodeByteArray(thumbnailImage, 0, thumbnailImage.length));
		} else {
			thumbnail.setVisibility(View.GONE);
		}

		TextView title = (TextView) view.findViewById(R.id.list_item_title);
		title.setText(row.getFileName());

		TextView description = (TextView) view.findViewById(R.id.list_item_description);
		description.setText(row.getCaptureDate());

		return view;
	}
}
