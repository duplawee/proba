package com.lynxsolutions.studentnotenewestversion;

import com.lynxsolutions.utils.Utils;
import com.lynxsolutions.utils.Note.TYPE;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.VideoView;
import android.widget.ImageView.ScaleType;

public class NoteDetailsActivity extends ActionBarActivity {
	private EditText textContent, titleText;
	private ImageView imageContent, imageType;
	private VideoView videoContent;
	private FrameLayout frameLayout;
	private Spinner spinner;
	private String currentTagType = "ALL";
	private Button addButton, saveButton, clearButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_note_screen_new);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		Utils.setActionBarColor(getApplicationContext(), getSupportActionBar());
		initView();
	}

	public void initView() {
		imageType = (ImageView) findViewById(R.id.imageType);
		Log.i("test",
				imageType.getLayoutParams().width + " "
						+ imageType.getLayoutParams().height);
		// imageType.getLayoutParams().width =
		// imageType.getLayoutParams().height;
		imageType.setScaleType(ScaleType.FIT_XY);
		imageType.requestLayout();
		titleText = (EditText) findViewById(R.id.title);

		addButton = (Button) findViewById(R.id.btnAdd);
		// addButton.setOnClickListener(addClickListener);

		saveButton = (Button) findViewById(R.id.btnSave);
		// saveButton.setOnClickListener(saveClickListener);

		clearButton = (Button) findViewById(R.id.btnClear);
		// clearButton.setOnClickListener(clearClickListener);

		textContent = (EditText) findViewById(R.id.editText);
		imageContent = (ImageView) findViewById(R.id.imageView);
		// imageContent.setOnLongClickListener(viewClickListener);
		frameLayout = (FrameLayout) findViewById(R.id.fragment_layout);

		videoContent = (VideoView) findViewById(R.id.videoView);
		// videoContent.setOnClickListener(playClickListener);
		// videoContent.setOnLongClickListener(viewClickListener);
		// videoContent.setOnTouchListener(onVideoTouchListener);

		spinner = (Spinner) findViewById(R.id.tagSpinner);
		// initRunnable();

		// scaleImage();
		//
		// ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(
		// AddNoteActivity.this, R.layout.drop_down_item, tags);
		// spinner.setAdapter(spinnerAdapter);
		// spinner.setOnItemSelectedListener(new
		// AdapterView.OnItemSelectedListener() {
		// public void onItemSelected(AdapterView<?> parent, View view,
		// int pos, long id) {
		// Object item = parent.getItemAtPosition(pos);
		// currentTagType = item.toString();
		// }
		//
		// public void onNothingSelected(AdapterView<?> parent) {
		// }
		// });
		// typeToAddNote = (TYPE) getIntent().getSerializableExtra("TYPE");
		// refreshView(typeToAddNote);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.note_details, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
