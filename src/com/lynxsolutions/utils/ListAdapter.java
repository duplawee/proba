package com.lynxsolutions.utils;

import java.util.ArrayList;
import java.util.List;

import com.google.android.gms.internal.in;
import com.lynxsolutions.studentnotenewestversion.R;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

//public class ListAdapter extends ArrayAdapter<String> {
public class ListAdapter extends CursorAdapter {
	private final Context context;
	private LayoutInflater inflater;
	private ArrayList<Note> notes = new ArrayList<Note>();
	private boolean showCheckBoxes;
	private boolean checkAll = false;
	private int[] notesToDelete;

	public ListAdapter(Activity activity, ArrayList<Note> notes,
			boolean showCheckBoxes) {
		super(activity.getApplicationContext(), null, false);
		this.context = activity.getApplicationContext();
		this.notes = notes;
		this.notesToDelete = new int[notes.size()];
		this.showCheckBoxes = showCheckBoxes;
		inflater = LayoutInflater.from(activity);
	}

	public Note getSelectedNote(int position) {
		return notes.get(position);
	}

	@Override
	public int getCount() {
		return notes.size();
	}

	public void setList(ArrayList<Note> list) {
		this.notes = list;
		notifyDataSetChanged();
	}

	public ArrayList<Note> getList() {
		return notes;
	}

	public void showCheckBoxes(boolean show) {
		this.showCheckBoxes = show;
		notifyDataSetChanged();
	}

	public void checkAll(boolean b) {
		this.checkAll = b;
		notifyDataSetChanged();
	}

	public int[] getNotesToDelete() {
		return notesToDelete;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// LayoutInflater inflater = (LayoutInflater) context
		// .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		ViewHolder viewHolder;

		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = inflater.inflate(R.layout.list_element, null);

			viewHolder.image = (ImageView) convertView
					.findViewById(R.id.image_list_item);
			viewHolder.titleText = (TextView) convertView
					.findViewById(R.id.title_list_item);
			viewHolder.subTitleText = (TextView) convertView
					.findViewById(R.id.subTitle_list_item);
			viewHolder.checkBox = (CheckBox) convertView
					.findViewById(R.id.checkBox);

			convertView.setTag(viewHolder);

		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		// show checkboxes for delete
		if (showCheckBoxes) {
			viewHolder.checkBox.setVisibility(View.FOCUS_UP);
		}
		else
		{
			viewHolder.checkBox.setVisibility(View.GONE);
		}

		viewHolder.checkBox.setChecked(checkAll);
		if (checkAll == true) {
			notesToDelete[position] = 1;
		} else {
			notesToDelete[position] = 0;
		}
		viewHolder.checkBox
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						if (((CheckBox) buttonView).isChecked()) {
							notesToDelete[position] = 1;
						} else {
							notesToDelete[position] = 0;
						}
					}
				});

		Note currentNote = notes.get(position);
		Log.i("get", position + " " + currentNote.getTitle());
		viewHolder.image
				.setImageResource(setImageByType(currentNote.getType()));
		viewHolder.titleText.setText(currentNote.getTitle());
		// viewHolder.subTitleText.setText(subTitles[position]);

		return convertView;
	}

	@Override
	public void bindView(View arg0, Context arg1, Cursor arg2) {
		// TODO Auto-generated method stub

	}

	@Override
	public View newView(Context arg0, Cursor arg1, ViewGroup arg2) {
		// TODO Auto-generated method stub
		return null;
	}

	private int setImageByType(String type) {
		int imageId = -1;
		Log.i("get", "type " + type);
		switch (type) {
		case "IMAGE":
			imageId = R.drawable.image_list_image;
			break;
		case "VIDEO":
			imageId = R.drawable.video_list_image;
			break;
		case "VOICE":
			imageId = R.drawable.audio_list_image;
			break;
		case "TEXT":
			imageId = R.drawable.text_list_image;
			break;

		default:
			break;
		}

		return imageId;
	}

	private class ViewHolder {
		ImageView image;
		TextView titleText, subTitleText;
		CheckBox checkBox;

	}

	// public void showCheckBoxes() {
	// // TODO Auto-generated method stub
	//
	// }

}
