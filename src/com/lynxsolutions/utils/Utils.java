package com.lynxsolutions.utils;

import java.util.List;

import com.lynxsolutions.studentnotenewestversion.ListingElementsActivity;
import com.lynxsolutions.studentnotenewestversion.LoginActivity;
import com.lynxsolutions.studentnotenewestversion.R;
import com.lynxsolutions.studentnotenewestversion.RegisterActivity;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Utils {
	public static String DIR_NAME = "StudentNote";
	// for shared preferences
	public static String SHARED = "sharedPreferences";
	public static String URI = "uri_for_files";
	public static String USERNAME = "username";
	public static String PASSWORD = "password";

	public static Dialog alertDialog = null;

	public static void saveImageUri(Uri fileUri, Context context) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(
				SHARED, Context.MODE_PRIVATE);
		sharedPreferences.edit().putString(URI, fileUri.toString()).commit();
	}

	public static Uri getSavedUri(Context context) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(
				SHARED, Context.MODE_PRIVATE);
		String imageUriString = sharedPreferences.getString(URI, null);
		Uri imageUri = Uri.parse(imageUriString);
		return imageUri;
	}

	public static void saveUsername(Context context, String username) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(
				SHARED, Context.MODE_PRIVATE);
		sharedPreferences.edit().putString(USERNAME, username).commit();
	}

	public static String getSavedUsername(Context context) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(
				SHARED, Context.MODE_PRIVATE);
		String username = sharedPreferences.getString(USERNAME, null);
		return username;
	}

	public static void savePassword(Context context, String password) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(
				SHARED, Context.MODE_PRIVATE);
		sharedPreferences.edit().putString(PASSWORD, password).commit();
	}

	public static String getSavedPassword(Context context) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(
				SHARED, Context.MODE_PRIVATE);
		return sharedPreferences.getString(PASSWORD, null);
	}

	public static String getPathFromUri(Context context, Uri uri, String pos) {
		// String[] proj = { MediaStore.Images.Media.DATA };
		// Cursor cursor = context.getContentResolver().query(uri, proj, null,
		// null, null);
		Cursor cursor = context.getContentResolver().query(uri, null, null,
				null, null);
		if (cursor != null) {
			cursor.moveToFirst();
			int index = cursor.getColumnIndex(pos);
			return cursor.getString(index);
		}
		return uri.getPath();
	}

	public interface DialogManager {
		public void dialogFinished();
	}

	public static void setAnimationForDialog(Dialog currentDialog) {
		currentDialog.getWindow().setLayout(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);
		currentDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

	}

	public static void showAlertDialog(final Activity activity,
			Context context, String message, final DialogManager dialogManager) {
		if (alertDialog != null)
			alertDialog.dismiss();
		alertDialog = new Dialog(context);
		alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		alertDialog.setContentView(R.layout.dialog_alert);
		setAnimationForDialog(alertDialog);

		alertDialog.setCanceledOnTouchOutside(false);

		TextView textView = (TextView) alertDialog.findViewById(R.id.textView);
		textView.setText(message);

		Button button = (Button) alertDialog.findViewById(R.id.okBtn);
		button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (dialogManager != null)
					dialogManager.dialogFinished();
				// activity.finish();
				Intent i = new Intent(activity, LoginActivity.class);
				activity.startActivity(i);
				activity.finish();
				alertDialog.dismiss();
			}
		});

		Button button2 = (Button) alertDialog.findViewById(R.id.noBtn);
		button2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (dialogManager != null)
					dialogManager.dialogFinished();
				alertDialog.dismiss();
			}
		});
		if (alertDialog != null)
			;
		alertDialog.show();
	}

	public static void setActionBarColor(Context applicationContext,
			ActionBar actionBar) {
		actionBar.setBackgroundDrawable(new ColorDrawable(Color
				.parseColor("#262626")));
		// .parseColor("#343434")));
	}

	public static boolean isApplicationSentToBackground(final Context context) {
		ActivityManager am = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> tasks = am.getRunningTasks(1);
		if (!tasks.isEmpty()) {
			ComponentName topActivity = tasks.get(0).topActivity;
			if (!topActivity.getPackageName().equals(context.getPackageName())) {
				return true;
			}
		}
		return false;
	}
}
