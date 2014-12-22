package com.lynxsolutions.studentnotenewestversion;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.nio.channels.FileChannel;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

import com.lynxsolutions.utils.BroadcastForReminder;
import com.lynxsolutions.utils.DatabaseManager;
import com.lynxsolutions.utils.Note;
import com.lynxsolutions.utils.Note.TYPE;
import com.lynxsolutions.utils.Utils;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TimePicker;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

public class AddNoteActivity extends ActionBarActivity {

	private static final String[] tags = new String[] { "ALL", "HOME", "WORK" };

	// private AddVideo addvideo = new AddVideo();

	private EditText textContent, titleText;
	private ImageView imageContent, imageType;
	private VideoView videoContent;
	private FrameLayout frameLayout;
	private Spinner spinner;
	private String currentTagType = "ALL";

	// to create notes
	private static String mCurrentPath;
	private static final int ACTION_TAKE_PHOTO = 1300;

	// Image
	private static String root = null;
	private static String folderPath = null;
	private String noteNAme = null;
	private static Uri fileUri = null;
	private Button addButton, saveButton, clearButton;
	private static String typeToAddNote = null;

	// Video
	private static final int ACTION_TAKE_VIDEO = 1400;

	// Audio
	private static final int ACTION_RECORD_SOUND = 1500;
	private ImageView listenImage;

	// Text

	private DatabaseManager dbManager;
	private ProgressDialog dialog;

	// For timer - video
	private Timer longClickTimer;
	private MediaController mediac;
	private long seconds = 0;
	private long millis = 0;
	private int mCount = 0;
	private boolean downClick = false;
	private boolean openedInFullScreenMode = false;

	// reminder
	private LinearLayout reminderLayout;
	private TextView tvReminderTime;
	private Calendar c;
	private int mYear;
	private int mMonth;
	private int mDay;
	private int mHour;
	private int mMinute;
	private String rmName = "Notificatioon!";
	private static final int DATE_DIALOG_ID = 1;
	private static final int TIME_DIALOG_ID = 0;
	private String time = "Not set";
	private boolean fromNotification = false;
	private boolean dialogIsActive = false,
			backPressedWhenDialogIsActive = false;

	// TO SHOW DETAILS
	private Note selectedNote;
	private TextView textView;
	private ProgressDialog progressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.add_note_screen_new);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		progressDialog = new ProgressDialog(this);
		progressDialog.setMessage("Please wait..");
		progressDialog.setCancelable(false);
		mediac = new MediaController(AddNoteActivity.this);
		Utils.setActionBarColor(getApplicationContext(), getSupportActionBar());
		initView();

		// selectMethodForAddNote(typeToAddNote);

	}

	public void scaleImage() {
		float margin = getApplicationContext().getResources().getDimension(
				R.dimen.button_height);
		float height = getApplicationContext().getResources().getDimension(
				R.dimen.small_margin);
		// int height = spinner.getHeight() + titleText.getHeight();
		int size = (int) (2 * height + 2 * margin);
		float size2 = 2 * height + 2 * margin;
		imageType.getLayoutParams().height = size;
		imageType.getLayoutParams().width = size;
		imageType.setScaleType(ScaleType.MATRIX);
		imageType.requestLayout();
		Log.i("dp", size + " " + size2);
	}

	OnLongClickListener viewClickListener = new OnLongClickListener() {
		@Override
		public boolean onLongClick(View v) {
			Toast.makeText(getApplicationContext(), "long click",
					Toast.LENGTH_SHORT).show();
			openInFullScreenMode();
			return false;
		}
	};

	OnClickListener clearClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			setContentView(R.layout.add_note_screen_new);
			initView();
		}
	};

	OnTouchListener onVideoTouchListener = new OnTouchListener() {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			//videoContent.setVisibility(View.FOCUS_UP);
			if (event.getAction() == MotionEvent.ACTION_DOWN
					&& longClickTimer == null && mediac != null) {
				Log.i("touch", "DOWN");
				mediac.hide();
				downClick = true;
				longClickTimer = new Timer();
				longClickTimer.scheduleAtFixedRate(new TimerTask() {

					@Override
					public void run() {
						timerMethod();
					}
				}, 1, 1);
			} else {
				if (event.getAction() == MotionEvent.ACTION_UP && downClick) {
					Log.i("touch", "UP");
					downClick = false;
					v.performClick();

					if (!openedInFullScreenMode) {
						mediac.show();
					}
					openedInFullScreenMode = false;
					if (longClickTimer != null) {
						longClickTimer.cancel();
						longClickTimer.purge();
						longClickTimer = null;
					}
					mCount = 0;
				}
			}
			return true;
		}
	};

	public void initView() {
		imageType = (ImageView) findViewById(R.id.imageType);
		Log.i("test",
				imageType.getLayoutParams().width + " "
						+ imageType.getLayoutParams().height);

		imageType.setScaleType(ScaleType.FIT_XY);
		imageType.requestLayout();
		titleText = (EditText) findViewById(R.id.title);

		addButton = (Button) findViewById(R.id.btnAdd);
		addButton.setOnClickListener(addClickListener);

		saveButton = (Button) findViewById(R.id.btnSave);
		saveButton.setOnClickListener(saveClickListener);

		clearButton = (Button) findViewById(R.id.btnClear);
		clearButton.setOnClickListener(clearClickListener);

		reminderLayout = (LinearLayout) findViewById(R.id.reminderLayout);
		reminderLayout.setOnClickListener(setReminderClickListener);
		tvReminderTime = (TextView) findViewById(R.id.setReminder);

		tvReminderTime = (TextView) findViewById(R.id.reminderTime);

		textContent = (EditText) findViewById(R.id.editText);
		textView = (TextView) findViewById(R.id.textView);
		imageContent = (ImageView) findViewById(R.id.imageView);
		imageContent.setOnLongClickListener(viewClickListener);
		frameLayout = (FrameLayout) findViewById(R.id.fragment_layout);

		videoContent = (VideoView) findViewById(R.id.videoView);
		videoContent.setOnTouchListener(onVideoTouchListener);
		// videoContent.setZOrderOnTop(false);
		//listenImage = (ImageView) findViewById(R.id.listenImage);
		//listenImage.setOnTouchListener(onVideoTouchListener);

		spinner = (Spinner) findViewById(R.id.tagSpinner);

		scaleImage();

		ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(
				AddNoteActivity.this, R.layout.drop_down_item, tags);
		spinner.setAdapter(spinnerAdapter);
		spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> parent, View view,
					int pos, long id) {
				Object item = parent.getItemAtPosition(pos);
				currentTagType = item.toString();
			}

			public void onNothingSelected(AdapterView<?> parent) {
			}
		});

		typeToAddNote = getIntent().getStringExtra("TYPE");
		fromNotification = getIntent().getBooleanExtra("from_notification",
				false);
		if (typeToAddNote != null) {
			refreshView(typeToAddNote);
		} else {
			selectedNote = (Note) getIntent().getSerializableExtra(
					"note_to_open");
			if (selectedNote != null) {
				refreshView(selectedNote.getType());
				fillFields();
			}
		}
	}

	public void fillFields() {
		titleText.setText(selectedNote.getTitle().toString());
		if (fromNotification) {
			// saveButton.setText("OK");
			tvReminderTime.setText("Expired");
		} else {
			// saveButton.setText("Update");
			tvReminderTime.setText(selectedNote.getNotificationDate());
		}
		hideKeyboard();
		int pos = -1;
		switch (selectedNote.getTag()) {
		case "ALL":
			pos = 0;
			break;
		case "HOME":
			pos = 1;
			break;
		case "WORK":
			pos = 2;
			break;

		default:
			break;
		}
		spinner.setSelection(pos);

		Uri uri = Uri.parse(selectedNote.getUri());
		switch (selectedNote.getType()) {
		case "IMAGE":

			imageContent.setVisibility(View.FOCUS_UP);
			try {
				String path = uri.getPath();
				File fileToOpen = new File(path);
				Bitmap b;// =
							// BitmapFactory.decodeFile(fileToOpen.getAbsolutePath());
				new rotateImage().execute();
				// b = rotateImage();
				// imageContent.setImageBitmap(b);
			} catch (Exception e) {
				e.printStackTrace();
				Toast.makeText(getApplicationContext(), "File not found!",
						Toast.LENGTH_SHORT).show();
			}

			// imageContent.setImageURI(Uri.parse(selectedNote.getUri()));
			break;
		case "TEXT":
			// textContent.setVisibility(View.GONE);
			// textContent.setVisibility(View.FOCUS_UP);
			String ret = "";
			try {
				InputStream inputStream = new FileInputStream(uri.getPath());

				if (inputStream != null) {
					InputStreamReader inputStreamReader = new InputStreamReader(
							inputStream);
					BufferedReader bufferedReader = new BufferedReader(
							inputStreamReader);
					String receiveString = "";
					StringBuilder stringBuilder = new StringBuilder();

					while ((receiveString = bufferedReader.readLine()) != null) {
						stringBuilder.append("\n");
						stringBuilder.append(receiveString);
					}

					inputStream.close();
					ret = stringBuilder.toString();
				}
			} catch (FileNotFoundException e) {
				Log.e("login activity", "File not found: " + e.toString());
			} catch (IOException e) {
				Log.e("login activity", "Can not read file: " + e.toString());
			}
			textContent.setText(ret);
			break;

		case "VIDEO":
			videoContent.setVisibility(View.FOCUS_UP);
			// videoContent.setVideoURI(uri);
			// new OpenVideo().execute(uri);
			useMediaController(uri);
			break;
		default:
		case "VOICE":
			videoContent.setVisibility(View.FOCUS_UP);
			// videoContent.setVideoURI(uri);
			useMediaController(uri);
			break;

		}
	}

	// to open video at longclick
	public void timerMethod() {
		this.runOnUiThread(generate);
	}

	public Runnable generate = new Runnable() {
		@Override
		public void run() {
			mCount++;
			Log.i("time", "here: " + mCount);
			if (mCount > 150 && !openedInFullScreenMode) {
				openInFullScreenMode();
				openedInFullScreenMode = true;
				mCount = 0;
			}
		}
	};

	public void openInFullScreenMode() {
		Intent intent = new Intent();
		intent.setAction(android.content.Intent.ACTION_VIEW);
		// intent.setDataAndType(Uri.parse(file.getAbsolutePath()),"image/*");
		Log.i("uri", "here " + fileUri);
		String typeToView = "";
		if (selectedNote != null) {
			typeToAddNote = selectedNote.getType();
			fileUri = Uri.parse(selectedNote.getUri());
		}
		if (typeToAddNote.equals("IMAGE")) {
			typeToView = "image/*";
		} else {
			typeToView = "video/*";
		}
		Log.i("IMAGE", fileUri + " " + typeToAddNote + " " + typeToView);
		intent.setDataAndType(fileUri, typeToView);
		startActivity(intent);
	}

	OnClickListener setReminderClickListener = new OnClickListener() {

		@SuppressWarnings("deprecation")
		@Override
		public void onClick(View v) {
			c = Calendar.getInstance();
			mYear = c.get(Calendar.YEAR);
			mMonth = c.get(Calendar.MONTH);
			mDay = c.get(Calendar.DAY_OF_MONTH);
			mHour = c.get(Calendar.HOUR_OF_DAY);
			mMinute = c.get(Calendar.MINUTE);
			rmName = "StudentNote";
			showDialog(DATE_DIALOG_ID);

		}
	};

	private TimePickerDialog.OnTimeSetListener mTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			if (!backPressedWhenDialogIsActive) {
				mHour = hourOfDay;
				mMinute = minute;
				Log.i("reminder", mYear + "-" + mMonth + "-" + mDay + " "
						+ mHour + ":" + mMinute);
				time = mYear + "-" + mMonth + "-" + mDay + " " + mHour + ":"
						+ mMinute;
				Log.i("reminder", tvReminderTime.getText() + "");
				tvReminderTime.setText(time);
			} else {
				tvReminderTime.setText("Not set");
			}
			backPressedWhenDialogIsActive = false;
			// ifIsSetReminder = 1;
		}
	};

	@Override
	protected Dialog onCreateDialog(int id) {
		Toast.makeText(getApplicationContext(), "dialog oncreate",
				Toast.LENGTH_SHORT).show();
		switch (id) {
		case TIME_DIALOG_ID:
			// return new TimePickerDialog(this, R.style.AppBaseThemeDark,
			// mTimeSetListener, mHour, mMinute, false);
			TimePickerDialog timeDialog = new TimePickerDialog(this,
					mTimeSetListener, mHour, mMinute, false);
			timeDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {

				@Override
				public boolean onKey(DialogInterface dialog, int keyCode,
						KeyEvent event) {
					if (keyCode == KeyEvent.KEYCODE_BACK) {
						backPressedWhenDialogIsActive = true;
						mYear = 0;
						mMonth = 0;
						mDay = 0;
					}
					return false;
				}
			});
			return timeDialog;// new TimePickerDialog(this, mTimeSetListener,
								// mHour, mMinute, false);
		case DATE_DIALOG_ID:
			final DatePickerDialog dateDialog = new DatePickerDialog(this,
					mDateSetListener, mYear, mMonth, mDay);

			dateDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {

				@Override
				public boolean onKey(DialogInterface dialog, int keyCode,
						KeyEvent event) {
					Log.e("dialog", "Key down = " + keyCode);
					if (keyCode == KeyEvent.KEYCODE_BACK) {
						Log.i("dialog", "onBackPressed active true");
						backPressedWhenDialogIsActive = true;
						dateDialog.dismiss();
						return true;
					} else {
						return false;
					}
				}
			});
			return dateDialog;

		}
		return super.onCreateDialog(id);
	}

	private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {

			if (!backPressedWhenDialogIsActive) {
				mYear = year;
				mMonth = monthOfYear + 1;
				mDay = dayOfMonth;
				showDialog(TIME_DIALOG_ID);
			}
			backPressedWhenDialogIsActive = false;
		};
	};

	public void onTimeClicked(View view) {
		showDialog(TIME_DIALOG_ID);
	}

	public void onDateClicked(View view) {
		showDialog(DATE_DIALOG_ID);
	}

	OnClickListener addClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			selectMethodForAddNote(typeToAddNote);
		}
	};

	@Override
	public void onBackPressed() {
		if (fileUri != null && selectedNote == null) {
			File file1 = new File(fileUri.getPath());
			boolean isdeleted = file1.delete();
			Toast.makeText(getApplicationContext(), "deleted",
					Toast.LENGTH_SHORT).show();
		}
		super.onBackPressed(); // optional depending on your needs
	}

	public void setReminder() {
		Log.d("timestick", "eloszor " + time);
		if (mYear != 0) {
			time = mYear + "-" + mMonth + "-" + mDay + " " + mHour + ":"
					+ mMinute;
			Log.d("timestick", time);
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm");
			Date dt = null;
			try {
				dt = df.parse(time);
				Log.d("paser time", dt.toString());
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			long when = dt.getTime();
			// Date pr = new Date();
			// SimpleDateFormat dq = new SimpleDateFormat("yyyy-MM-dd hh-mm");
			// Toast.makeText(getApplication(), dq.format(pr),
			// Toast.LENGTH_LONG).show();

			AlarmManager mgr = (AlarmManager) getApplicationContext()
					.getSystemService(Context.ALARM_SERVICE);

			Intent notificationIntent = new Intent(AddNoteActivity.this,
					BroadcastForReminder.class);

			notificationIntent.putExtra("Name", selectedNote);
			notificationIntent.putExtra("Description", time);
			notificationIntent.putExtra("NotifyCount", 50);
			PendingIntent pi = PendingIntent.getBroadcast(AddNoteActivity.this,
					50, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
			Log.i("when", when + " 2");
			mgr.set(AlarmManager.RTC, when, pi);
		} else {
			time = "Not set";
		}

	}

	OnClickListener saveClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// if (fromNotification) {
			// dbManager = new DatabaseManager(getApplicationContext());
			// dbManager.open();
			// boolean result = dbManager.editNote(getApplicationContext(),
			// selectedNote.getId(), titleText.getText().toString(),
			// selectedNote.getType(), selectedNote.getTag(),
			// fileUri.toString(), "Not set".toString());
			// dbManager.close();
			// Intent i = new Intent(AddNoteActivity.this,
			// ListingElementsActivity.class);
			// startActivity(i);
			// } else {
			Log.i("ok", "click");
			String message = "";
			message = checkDatasBeforeSave();
			if (message.equals("")) {
				if (typeToAddNote.toString().equals("TEXT")) {
					createText();
				}
				setReminder();

				dbManager = new DatabaseManager(getApplicationContext());
				dbManager.open();
				// Log.i("ok",
				// titleText.getText().toString() + " "
				// + typeToAddNote.toString() + " "
				// + currentTagType.toString() + " "
				// + fileUri.toString());
				if (selectedNote == null) {
					boolean result = dbManager
							.addNote(getApplicationContext(), titleText
									.getText().toString(), typeToAddNote
									.toString(), currentTagType.toString(),
									fileUri.toString(), time);
					if (result) {
						Toast.makeText(
								getApplicationContext(),
								getApplicationContext().getResources()
										.getString(R.string.note_saved),
								Toast.LENGTH_SHORT).show();
					} else {
						Toast.makeText(
								getApplicationContext(),
								getApplicationContext().getResources()
										.getString(R.string.note_not_saved),
								Toast.LENGTH_SHORT).show();
					}
				} else {
					boolean edited = dbManager.editNote(
							getApplicationContext(), selectedNote.getId(),
							titleText.getText().toString(),
							selectedNote.getType(), currentTagType.toString(),
							selectedNote.getUri(), time);
					if (edited) {
						Toast.makeText(
								getApplicationContext(),
								getApplicationContext().getResources()
										.getString(R.string.note_saved),
								Toast.LENGTH_SHORT).show();
					} else {
						Toast.makeText(
								getApplicationContext(),
								getApplicationContext().getResources()
										.getString(R.string.note_not_saved),
								Toast.LENGTH_SHORT).show();
					}
				}
				dbManager.close();
				if (!fromNotification) {
					AddNoteActivity.this.finish();
				} else {
					Intent i = new Intent(AddNoteActivity.this,
							ListingElementsActivity.class);
					startActivity(i);
				}

			} else {
				Toast.makeText(getApplicationContext(), message,
						Toast.LENGTH_SHORT).show();
			}
		}
		// }
	};

	public String checkDatasBeforeSave() {
		if (titleText.getText().toString().equals("")
				|| titleText.getText().toString() == null)
			return "Add a title!";
		if (selectedNote != null) {
			typeToAddNote = selectedNote.getType();
		}
		switch (typeToAddNote) {
		case "IMAGE":
			if (imageContent.getDrawable() == null)
				return "Take a picture!";
			break;
		case "TEXT":
			if (textContent.getText().toString().equals("")
					|| textContent.getText().toString() == null)
				return "Type some text!";
			break;
		case "VIDEO":
			// check videoview content
			break;
		case "VOICE":
			// check videoview content
			break;

		default:
			break;
		}
		return "";
	}

	OnClickListener playClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if (videoContent != null) {
				videoContent.start();
			}
		}
	};

	public String createTitleWithTimestamp(String extension) {
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
				Locale.getDefault()).format(new Date());
		String title = "";
		// if (selectedNote == null) {
		title = titleText.getText().toString() + timeStamp + extension;
		// } else {
		// title = selectedNote.getTitle() + timeStamp + extension;
		// }
		return title;
	}

	public File generateNameAndFile(String extension) {
		String name = createTitleWithTimestamp(extension);
		File newFile = new File(folderPath, name);

		fileUri = Uri.fromFile(newFile);
		mCurrentPath = newFile.getAbsolutePath();
		Utils.saveImageUri(fileUri, getApplicationContext());

		if (extension.equals(".txt")) {
			return newFile;
		}
		return null;
		// return name;
	}

	public void selectMethodForAddNote(String typeToAddNote) {
		if (selectedNote != null) {
			typeToAddNote = selectedNote.getType();
		}
		createFolderForNotes();
		if (!titleText.getText().toString().equals("")) {

			switch (typeToAddNote) {
			case "IMAGE": {
				hideKeyboard();
				takePicture();
			}
				break;
			case "TEXT":
				// createText();
				// textContent.requestFocus();
				// hideKeyboard();
				break;
			case "VIDEO": {
				hideKeyboard();
				takeVideo();
			}
				break;
			case "VOICE": {
				hideKeyboard();
				recordAudio("testaudio");
			}
				break;
			default:
				break;
			}
		} else
			Toast.makeText(getApplicationContext(), "Add title!",
					Toast.LENGTH_SHORT).show();

	}

	public void hideKeyboard() {
		InputMethodManager inputManager = (InputMethodManager) this
				.getSystemService(Context.INPUT_METHOD_SERVICE);

		// check if no view has focus:
		View view = this.getCurrentFocus();
		if (view != null) {
			inputManager.hideSoftInputFromWindow(view.getWindowToken(),
					InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}

	public void createText() {
		hideKeyboard();
		createFolderForNotes();
		String text = "";
		File wallpaperDirectory = new File(folderPath);
		wallpaperDirectory.mkdirs();
		String title = createTitleWithTimestamp(".txt");

		// if (isExternalStorageWritable()) {
		File file = null;
		if (selectedNote == null) {
			file = new File(wallpaperDirectory, title);
		} else {
			file = new File(Uri.parse(selectedNote.getUri()).getPath());// fileUri.getPath());
		}
		fileUri = Uri.fromFile(file);
		mCurrentPath = file.getAbsolutePath();
		Utils.saveImageUri(fileUri, getApplicationContext());
		byte[] value = textContent.getText().toString().getBytes();
		Context context = getApplicationContext();
		Log.i("size", file.length() + "");
		try {
			FileOutputStream f = new FileOutputStream(file);
			f.write(value);
			f.flush();
			f.close();
			text = context.getResources().getString(R.string.note_saved);
		} catch (IOException e) {
			text = context.getResources().getString(R.string.note_not_saved);
			e.printStackTrace();
		}
		Toast.makeText(context, text, Toast.LENGTH_LONG).show();
		Log.i("size", file.length() + "");

	}

	public void takePicture() {
		// Uri uri = fileUri;
		if (selectedNote != null) {
			fileUri = Uri.parse(selectedNote.getUri());
		} else {
			generateNameAndFile(".jpg");
		}
		Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

		takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
		startActivityForResult(takePictureIntent, ACTION_TAKE_PHOTO);

	}

	public void takeVideo() {
		if (selectedNote == null) {
			generateNameAndFile(".mp4");
		} else {
			fileUri = Uri.parse(selectedNote.getUri());
		}
		// generateNameAndFile(".3gp");
		Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
		takeVideoIntent.putExtra(MediaStore.EXTRA_SCREEN_ORIENTATION,
				ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

		takeVideoIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
		startActivityForResult(takeVideoIntent, ACTION_TAKE_VIDEO);
	}

	public void recordAudio(String fileName) {
		Intent recordAudio = new Intent(
				MediaStore.Audio.Media.RECORD_SOUND_ACTION);
		startActivityForResult(recordAudio, ACTION_RECORD_SOUND);
	}

	// public boolean isEnoughExternalStorage(byte filesize) {
	// StatFs stat = new StatFs(Environment.getExternalStorageDirectory()
	// .getPath());
	// long bytesAvailable = (long) stat.getBlockSize()
	// * (long) stat.getBlockCount();
	// long megAvailable = bytesAvailable / 1048576;
	// Log.i("size", "in function: " + bytesAvailable + " " + filesize);
	// if (filesize < bytesAvailable) {
	// return true;
	// }
	// return false;
	// }

	public void refreshView(String typeToAddNote) {

		switch (typeToAddNote) {
		case "IMAGE":
			imageContent.setVisibility(View.FOCUS_UP);
			imageType.setBackgroundResource(R.drawable.image_type_image);
			break;
		case "TEXT":
			// textContent.setVisibiliy(View.FOCUS_UP);
			hideKeyboard();
			imageType.setBackgroundResource(R.drawable.text_type_image);
			break;
		case "VIDEO":

			 videoContent.setVisibility(View.FOCUS_UP);
			//videoContent.setBackgroundColor(Color.TRANSPARENT);
			//listenImage.setVisibility(View.FOCUS_UP);

			imageType.setBackgroundResource(R.drawable.video_type_image);
			break;
		case "VOICE":
			videoContent.setBackgroundResource(R.drawable.listen_250px);
			videoContent.setVisibility(View.VISIBLE);
			
			//listenImage.setVisibility(View.FOCUS_UP);
			

			imageType.setBackgroundResource(R.drawable.audio_type_image);
			break;

		default:
			break;
		}
	}

	public void createFolderForNotes() {
		root = Environment.getExternalStorageDirectory().toString()
				+ "/StudentNote";

		// Creating folders for Image
		folderPath = root + "/Notes";
		File folder = new File(folderPath);
		folder.mkdirs();

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {

			String pathFromData = null;
			Uri uriFromData = null;
			if (data != null)
				uriFromData = data.getData();

			if (selectedNote != null) {
				fileUri = Uri.parse(selectedNote.getUri());
			} else {
				Uri savedUri = Utils.getSavedUri(getApplicationContext());
				if (fileUri == null) {
					fileUri = savedUri;
				}
			}

			switch (requestCode) {
			case ACTION_TAKE_PHOTO:
				Bitmap bitmap = null;
				if (fileUri != null) {
					Toast.makeText(getApplicationContext(), "nemnull",
							Toast.LENGTH_SHORT).show();
					bitmap = rotateImage();
					if (bitmap != null) {
						imageContent.setImageBitmap(bitmap);
					}
				}
				break;

			case ACTION_TAKE_VIDEO:
				if (uriFromData != fileUri && uriFromData != null) {
					pathFromData = Utils.getPathFromUri(
							getApplicationContext(), uriFromData,
							MediaStore.Video.Media.DATA);
				} else {
					if (fileUri != null) {
						pathFromData = Utils.getPathFromUri(
								getApplicationContext(), fileUri,
								MediaStore.Video.Media.DATA);
					}
				}
				String dest = Environment.getExternalStorageDirectory()
						.toString() + "/StudentNote/Notes";
				Log.i("path", "pathFromData " + pathFromData);
				dialog = new ProgressDialog(AddNoteActivity.this);
				dialog.setMessage("Your message..");
				// new MyCopyTask(pathFromData, dest, ".mp4").execute();
				boolean success = copyFile(pathFromData, dest, ".mp4");
				dialog.dismiss();
				Log.i("path", "to " + dest);
				Log.i("path", "success " + success);
				// delete file
				File file1 = new File(pathFromData);
				boolean isdeleted = file1.delete();
				// posibility to replay
				useMediaController(fileUri);
				break;

			case ACTION_RECORD_SOUND:// copy - default dir to destination dir
				uriFromData = data.getData();
				pathFromData = Utils.getPathFromUri(getApplicationContext(),
						uriFromData, MediaStore.Audio.Media.DATA);
				Log.i("path", "pathFromData " + pathFromData);
				String to = Environment.getExternalStorageDirectory()
						.toString() + "/StudentNote/Notes";
				Log.i("path", "to " + to);
				copyFile(pathFromData, to, ".amr");

				// delete file
				File file = new File(pathFromData);
				boolean deleted = file.delete();
				Log.i("uri", "deleted: " + deleted);

				// posibility to replay
				useMediaController(fileUri);
				break;

			default:
				break;
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	public class MyCopyTask extends AsyncTask<Void, Void, Void> {
		private String from;
		private String to;
		private String extension;

		public MyCopyTask(String pathFromData, String dest, String extension) {
			this.from = pathFromData;
			this.to = dest;
			this.extension = extension;
		}

		@Override
		public void onPreExecute() {
			dialog.show();
			Log.i("task", "onPreExecute");
		}

		@Override
		public void onPostExecute(Void unused) {
			dialog.dismiss();
			Log.i("task", "onPostExecute");
		}

		@Override
		protected Void doInBackground(Void... params) {
			Log.i("task", "doInBackground");
			copyFile(from, to, extension);

			return null;
		}
	}

	public Bitmap rotateImage() {
		Bitmap bitmap = null;
		try {
			Uri uri = null;
			if (selectedNote == null) {
				uri = fileUri;
			} else {
				uri = Uri.parse(selectedNote.getUri());
			}
			File f = new File(uri.getPath());
			ExifInterface exif = new ExifInterface(f.getPath());
			int orientation = exif.getAttributeInt(
					ExifInterface.TAG_ORIENTATION,
					ExifInterface.ORIENTATION_NORMAL);

			int angle = 0;

			if (orientation == ExifInterface.ORIENTATION_ROTATE_90) {
				angle = 90;
			} else if (orientation == ExifInterface.ORIENTATION_ROTATE_180) {
				angle = 180;
			} else if (orientation == ExifInterface.ORIENTATION_ROTATE_270) {
				angle = 270;
			}

			Matrix mat = new Matrix();
			mat.postRotate(angle);

			Bitmap bmp1 = decodeFile(f);// BitmapFactory.decodeStream(new
										// FileInputStream(f),null, null);
			// Bitmap bmp1 = BitmapFactory.decodeStream(new FileInputStream(f));
			bitmap = Bitmap.createBitmap(bmp1, 0, 0, bmp1.getWidth(),
					bmp1.getHeight(), mat, true);
		} catch (IOException e) {
			Log.w("TAG", "-- Error in setting image");
		} catch (OutOfMemoryError oom) {
			Log.w("TAG", "-- OOM Error in setting image");
		}
		return bitmap;
	}

	private static Bitmap decodeFile(File f) {
		try {
			// Decode image size
			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(new FileInputStream(f), null, o);

			// The new size we want to scale to
			final int REQUIRED_SIZE = 1024;

			// Find the correct scale value. It should be the power of 2.
			int scale = 1;
			while (o.outWidth / scale / 2 >= REQUIRED_SIZE
					&& o.outHeight / scale / 2 >= REQUIRED_SIZE)
				scale *= 2;

			// Decode with inSampleSize
			BitmapFactory.Options o2 = new BitmapFactory.Options();
			o2.inSampleSize = scale;
			o.inJustDecodeBounds = false;
			return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	public boolean copyFile(String from, String to, String extention) {
		Log.i("task", "copyFile");
		try {
			File sd = Environment.getExternalStorageDirectory();
			if (sd.canWrite()) {
				int end = from.toString().lastIndexOf("/");
				String str1 = from.toString().substring(0, end);
				String str2 = from.toString().substring(end + 1, from.length());
				File source = new File(str1, str2);

				String title = createTitleWithTimestamp(extention);

				Log.i("path", "title " + title + "; extention " + extention);
				File destinationFile = null;
				// if(selectedNote == null){
				destinationFile = new File(to, title);
				fileUri = Uri.fromFile(destinationFile);
				// }
				// else{
				// destinationFile = new
				// File(Uri.parse(selectedNote.getUri()).getPath());
				// fileUri = Uri.fromFile(destinationFile);
				// }
				if (selectedNote != null) {
					if (selectedNote.getType().equals("VOICE")
							|| selectedNote.getType().equals("VIDEO")) {
						File file = new File(Uri.parse(selectedNote.getUri())
								.getPath());
						boolean deleted = file.delete();
					}

					selectedNote.setUri(fileUri.toString());
				}

				Log.i("copy",
						Uri.fromFile(source) + " "
								+ Uri.fromFile(destinationFile));
				if (source.exists()) {
					// FileChannel src = File.getChannel();
					// FileChannel dst = extractedInput(destinationFile)
					// .getChannel();
					FileChannel src = new FileInputStream(source).getChannel();
					FileChannel dst = new FileOutputStream(destinationFile)
							.getChannel();
					dst.transferFrom(src, 0, src.size());
					src.close();
					dst.close();
				}
			}
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public void useMediaController(Uri uri) {
		// mediac = new MediaController(AddNoteActivity.this);
		// videoContent.setZOrderOnTop(false);
		mediac.setAnchorView(videoContent);
		mediac.setMediaPlayer(videoContent);
		videoContent.setMediaController(mediac);
		videoContent.setVideoURI(uri);
		// videoContent.setZOrderOnTop(true);
		videoContent.seekTo(1);
		videoContent.requestFocus();
	}

	/* Checks if external storage is available for read and write */
	public boolean isExternalStorageWritable() {
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			return true;
		}
		return false;
	}

	/* Checks if external storage is available to at least read */
	public boolean isExternalStorageReadable() {
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)
				|| Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
			return true;
		}
		return false;
	}

	// @Override
	// public boolean onCreateOptionsMenu(Menu menu) {
	// // Inflate the menu; this adds items to the action bar if it is present.
	// getMenuInflater().inflate(R.menu.add_note, menu);
	// return true;
	// }
	//
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {

		case android.R.id.home:
			onBackPressed();
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	private class rotateImage extends AsyncTask<String, Void, Bitmap> {

		@Override
		protected void onPreExecute() {
			progressDialog.show();
			super.onPreExecute();
		}

		@Override
		protected Bitmap doInBackground(String... params) {
			Bitmap b = rotateImage();
			return b;

		}

		@Override
		protected void onPostExecute(Bitmap bitmap) {
			try {
				if (progressDialog.isShowing()) {
					progressDialog.dismiss();
				}
			} catch (Exception e) {
				Log.d("AddOrEditTaskEditText post", "----");
			}
			imageContent.setImageBitmap(bitmap);
			super.onPostExecute(bitmap);
		}

		@Override
		protected void onProgressUpdate(Void... values) {
		}
	}

	private class OpenVideo extends AsyncTask<Uri, Void, Boolean> {

		@Override
		protected void onPreExecute() {
			// progressDialog = new ProgressDialog(AddNoteActivity.this);
			// progressDialog.setCancelable(false);
			// progressDialog.setTitle("Please wait...");
			try {
				progressDialog.show();
			} catch (Exception e) {
				e.printStackTrace();
			}

			super.onPreExecute();
		}

		@Override
		protected Boolean doInBackground(Uri... params) {
			useMediaController(params[0]);
			return true;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			// try {
			// if (progressDialog.isShowing()) {
			progressDialog.dismiss();
			// }
			// } catch (Exception e) {
			// Log.d("AddOrEditTaskEditText post", "----");
			// }

		}

		@Override
		protected void onProgressUpdate(Void... values) {
		}

	}
}
