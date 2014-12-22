package com.lynxsolutions.studentnotenewestversion;

import java.util.ArrayList;

import com.lynxsolutions.fragments.FragmentForLists;
import com.lynxsolutions.utils.Note;
import com.lynxsolutions.utils.Utils;
import com.lynxsolutions.utils.Note.TYPE;
import com.lynxsolutions.utils.Utils.DialogManager;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.Tab;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.NinePatchDrawable;
import android.graphics.drawable.StateListDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TabWidget;
import android.widget.Toast;

public class ListingElementsActivity extends ActionBarActivity {

	private static ProgressDialog progressDialog;
	private ActionBar actionBar;
	private FragmentForLists fragmentForTab;
	private ArrayList<Note> list = new ArrayList<Note>();
	private String currentType = "ALL";
	private String currentTag = "ALL";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_listing_elements);

		getSupportActionBar().setDisplayShowCustomEnabled(true);
		getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		actionBar = getSupportActionBar();
	//	actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#0000ff")));

		//Utils.setActionBarColor(getApplicationContext(),actionBar);
		actionBar.setBackgroundDrawable(new ColorDrawable(Color
				.parseColor("#343434")));
		// ImageView tabImageAll = new ImageView(this);
		// tabImageAll.setImageResource(R.drawable.all_tabb_image);
		// ActionBar.Tab tabImage = actionBar.newTab()
		// .setCustomView(tabImageAll);
		ActionBar.Tab tabAll = actionBar.newTab().setIcon(
				R.drawable.all_tab_image);
		ActionBar.Tab tabImage = actionBar.newTab().setIcon(
				R.drawable.image_tab_image);
		ActionBar.Tab tabText = actionBar.newTab().setIcon(
				R.drawable.text_tab_image);
		ActionBar.Tab tabVideo = actionBar.newTab().setIcon(
				R.drawable.camera_tab_image);
		ActionBar.Tab tabAudio = actionBar.newTab().setIcon(
				R.drawable.audio_tab_image);

		fragmentForTab = new FragmentForLists();

		tabAll.setTabListener(new MyTabsListener(fragmentForTab));
		tabImage.setTabListener(new MyTabsListener(fragmentForTab));
		tabText.setTabListener(new MyTabsListener(fragmentForTab));
		tabVideo.setTabListener(new MyTabsListener(fragmentForTab));
		tabAudio.setTabListener(new MyTabsListener(fragmentForTab));

		actionBar.addTab(tabAll);
		actionBar.addTab(tabImage);
		actionBar.addTab(tabText);
		actionBar.addTab(tabVideo);
		actionBar.addTab(tabAudio);
		

		// progressDialog = new ProgressDialog(this);
		// progressDialog.setMessage("Please wait..");
		// progressDialog.setCancelable(false);
		//
		// getActionBar();
		// getSupportActionBar().setDisplayShowCustomEnabled(true);
		// getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
	}

	protected class MyTabsListener implements ActionBar.TabListener {

		private FragmentForLists fragment;

		public MyTabsListener(FragmentForLists fragment) {
			this.fragment = fragment;
		}

		public void onTabReselected(Tab tab, FragmentTransaction ft) {
			ft.show(fragment);

		}

		public void onTabSelected(Tab tab, FragmentTransaction ft) {
			int position = tab.getPosition();
			if (position >= 0) {
				switch (position) {
				case 0:// all
					currentType = "ALL";
					break;
				case 1:// IMAGE
					currentType = "IMAGE";
					break;
				case 2:// Text
					currentType = "TEXT";
					break;
				case 3:// Video
					currentType = "VIDEO";
					break;
				case 4:// Voice
					currentType = "VOICE";
					break;
				default:
					break;
				}
			}
			if (!fragment.isAdded()) {
				ft.add(R.id.fragment_layout, fragment);
			} else {
				Log.i("list", "tablistener tag: " + currentTag + " type:"
						+ currentType);

				fragment.changeListConetnt(currentTag, currentType);
				ft.show(fragment);
			}
		}

		public void onTabUnselected(Tab tab, FragmentTransaction ft) {
			ft.hide(fragment);
		}

	}

	public void refreshList() {
	}

	public String getDeviceName() {
		String manufacturer = Build.MANUFACTURER;
		String model = Build.MODEL;
		if (model.startsWith(manufacturer)) {
			return capitalize(model);
		} else {
			return capitalize(manufacturer) + " " + model;
		}
	}

	private String capitalize(String s) {
		if (s == null || s.length() == 0) {
			return "";
		}
		char first = s.charAt(0);
		if (Character.isUpperCase(first)) {
			return s;
		} else {
			return Character.toUpperCase(first) + s.substring(1);
		}
	}

	public void createEmailForContactUs() {
		String deviceName = getDeviceName();
		int versionCode = 0;
		String versionName = "";
		int sdkVersion = android.os.Build.VERSION.SDK_INT;
		try {
			versionCode = getPackageManager().getPackageInfo(getPackageName(),
					0).versionCode;
			versionName = getPackageManager().getPackageInfo(getPackageName(),
					0).versionName;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Intent i = new Intent(Intent.ACTION_SEND);
		i.setType("message/rfc822");
		i.putExtra(Intent.EXTRA_EMAIL,
				new String[] { "feedback@lynxsolutions.ro" });
		i.putExtra(Intent.EXTRA_SUBJECT, "Student Note App Contact us");
		i.setType("text/plain");

		i.putExtra(
				Intent.EXTRA_TEXT,
				Html.fromHtml("<br/><br/><br/><br/><b><small>Please do not remove this information!</small></b><br/>"
						+ "<i><small>Application: SecureChat<br/>Device: "
						+ deviceName
						+ "<br/>Device OS: "
						+ sdkVersion
						+ "<br/>Application version name: "
						+ versionName
						+ "<br/>Application version code: "
						+ versionCode
						+ "</small></i>"));
		try {
			startActivity(Intent.createChooser(i, "Send mail..."));
		} catch (android.content.ActivityNotFoundException ex) {
			Toast.makeText(getApplicationContext(), "No email client!",
					Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);

		return super.onCreateOptionsMenu(menu);

	}

	@Override
	public void onResume() {
		super.onResume();
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		if (!fragmentForTab.isAdded()) {
			ft.add(R.id.fragment_layout, fragmentForTab).commit();
		} else {
			Log.i("list", "tablistener tag: " + currentTag + " type:"
					+ currentType);

			fragmentForTab.changeListConetnt(currentTag, currentType);
			ft.show(fragmentForTab);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		Intent i;
		int selTabPosition;

		switch (item.getItemId()) {
		case R.id.image:
			i = new Intent();
			i.setClass(this, AddNoteActivity.class);
			i.putExtra("TYPE", "IMAGE");// TYPE.IMAGE);
			// finish();
			startActivity(i);
			break;
		case R.id.text:
			i = new Intent();
			i.setClass(this, AddNoteActivity.class);
			i.putExtra("TYPE", "TEXT");// TYPE.TEXT);
			// finish();
			startActivity(i);
			break;
		case R.id.voice:
			i = new Intent();
			i.setClass(this, AddNoteActivity.class);
			i.putExtra("TYPE", "VOICE");// TYPE.VOICE);
			// finish();
			startActivity(i);
			break;
		case R.id.video:
			i = new Intent();
			i.setClass(this, AddNoteActivity.class);
			i.putExtra("TYPE", "VIDEO");// TYPE.VIDEO);
			// finish();
			startActivity(i);
			break;
		case R.id.all:
			currentTag = "ALL";
			Log.i("mylist", "all at tag: " + currentTag + " type:"
					+ currentType);
			if (fragmentForTab != null) {
				fragmentForTab.changeListConetnt(currentTag, currentType);
			}
			break;
		// TAGS
		case R.id.home:
			currentTag = "HOME";
			Log.i("mylist", "home at tag: " + currentTag + " type:"
					+ currentType);
			if (fragmentForTab != null) {
				fragmentForTab.changeListConetnt(currentTag, currentType);
			}
			break;

		case R.id.work:
			currentTag = "WORK";
			fragmentForTab.changeListConetnt(currentTag, currentType);
			break;
		case R.id.search:
			if (fragmentForTab.getVisibilityOfSearch() == View.GONE) {
				fragmentForTab.setVisibilityOfSearch(View.FOCUS_UP);
			} else {
				fragmentForTab.setVisibilityOfSearch(View.GONE);
			}
			break;
		// SETTINGS
		case R.id.moore_apps:
			i = new Intent(
					Intent.ACTION_VIEW,
					Uri.parse("https://play.google.com/store/apps/developer?id=Lynx+Solutions+SRL&hl=ro"));
			startActivity(i);
			break;

		case R.id.contact_us:
			createEmailForContactUs();
			break;
		case R.id.about_us:
			i = new Intent(ListingElementsActivity.this, AboutUsActivity.class);
			startActivity(i);
			break;
		case R.id.sign_out:
			Utils.showAlertDialog(ListingElementsActivity.this,
					ListingElementsActivity.this,
					"Would you like to sign out?", new DialogManager() {

						@Override
						public void dialogFinished() {
							// TODO Auto-generated method stub

						}
					});
			break;
		}
		return super.onOptionsItemSelected(item);
	}

}
