package com.lynxsolutions.utils;

import com.lynxsolutions.studentnotenewestversion.AddNoteActivity;
import com.lynxsolutions.studentnotenewestversion.LoginActivity;
import com.lynxsolutions.studentnotenewestversion.R;
import com.lynxsolutions.studentnotenewestversion.R.drawable;

import android.support.v4.app.NotificationCompat;
import android.support.v7.app.ActionBarActivity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class BroadcastForReminder extends BroadcastReceiver {

	private NotificationManager mNotificationManager;
	private Notification notification;
	private CharSequence from;
	private CharSequence message;
	private int id;
	private NotificationManager mManager;
	Note note;

	@Override
	public void onReceive(Context context, Intent intent) {
		// from = intent.getStringExtra("Name");
		note = (Note) intent.getSerializableExtra("Name");
		message = intent.getStringExtra("Description");
		id = intent.getIntExtra("NotifyCount", -2);
		Log.d("when", id + " " + message + " " + from);
		showNotification(context);
	}

	private void showNotification(Context context) {

		// mNotificationManager = (NotificationManager)
		// cont.getSystemService(Context.NOTIFICATION_SERVICE);

		Log.d("ShowNotification", message.toString());
		Intent intentToShow = new Intent();
		intentToShow.setClass(context, LoginActivity.class);
		intentToShow.putExtra("TITLE", message.toString());
		intentToShow.putExtra("NotId", id);
		intentToShow.putExtra("note_to_open", note);

		PendingIntent contentIntent = (PendingIntent.getActivity(context, 0,
				intentToShow, PendingIntent.FLAG_ONE_SHOT));
		/*
		 * new Intent(cont,
		 * LoginScreen.class).putExtra("TITLE",message.toString(
		 * )).putExtra("NotId",id)
		 */
		Log.d("-----------id-------------", Integer.toString(id));
		Log.d("-----------mess-------------", message.toString());
		Log.i("when", System.currentTimeMillis() + " ido");

		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
				context).setSmallIcon(R.drawable.app_icon)
				.setWhen(System.currentTimeMillis())
				.setContentTitle("Tap for details!")
				.setTicker("Notification arrived from Student Note!")
				.setContentText(message);

		mBuilder.setContentIntent(contentIntent);
		mBuilder.setDefaults(Notification.FLAG_INSISTENT);
		mBuilder.setDefaults(Notification.DEFAULT_ALL);
		mBuilder.setAutoCancel(true);
		NotificationManager mNotificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		Log.d("------------------------", Integer.toString(id));
		mNotificationManager.notify(id, mBuilder.build());

		Toast.makeText(context, "New Notification Received", Toast.LENGTH_LONG)
				.show();

	}

}
