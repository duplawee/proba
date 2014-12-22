package com.lynxsolutions.studentnotenewestversion;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.widget.Toast;

public class AddVideo {
	private Context context;
	private Activity activity;

	public AddVideo(Activity activity) {
		this.context = activity.getApplicationContext();
		this.activity = activity;

	}

	public void createVideo() {
		Toast.makeText(context, "create video",
				Toast.LENGTH_SHORT).show();
		Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		activity.startActivityForResult(takePictureIntent, 3);

	}

}
