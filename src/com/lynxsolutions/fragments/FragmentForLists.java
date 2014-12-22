package com.lynxsolutions.fragments;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.lynxsolutions.studentnotenewestversion.AddNoteActivity;
import com.lynxsolutions.studentnotenewestversion.ListingElementsActivity;
import com.lynxsolutions.studentnotenewestversion.R;
import com.lynxsolutions.utils.DatabaseManager;
import com.lynxsolutions.utils.ListAdapter;
import com.lynxsolutions.utils.Note;

import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar.LayoutParams;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SearchView.OnCloseListener;
import android.support.v7.widget.SearchView.OnQueryTextListener;
import android.animation.LayoutTransition;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.DocumentsContract.Root;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Toast;
import android.widget.AdapterView.OnItemLongClickListener;

public class FragmentForLists extends Fragment {
	private ListView listView;
	private ListAdapter adapter;
	private ArrayList<Note> notes;
	private DatabaseManager dbManager;
	private SearchView etSearch;
	private SearchView.SearchAutoComplete theTextArea;
	private Animation in = null;
	private Animation out = null, outForLayout = null;
	private LinearLayout linearLayout, layoutToAnimate, checkAllLayout,
			deleteLayout;
	private ArrayList<Note> listForSearch = new ArrayList<Note>();
	private ArrayList<Note> listToShowAtSearch = new ArrayList<Note>();
	private CheckBox checkBoxAll;
	private Button deleteBtn, cancelBtn;
	private DatabaseManager manager;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rowView = inflater.inflate(R.layout.fragment_for_lists, container,
				false);
		dbManager = new DatabaseManager(getActivity());
		dbManager.open();
		notes = dbManager.getAllNotes(getActivity());
		dbManager.close();

		in = AnimationUtils.loadAnimation(getActivity(),
				R.anim.animation_slide_down);
		out = AnimationUtils.loadAnimation(getActivity(),
				R.anim.animation_slide_up);
		outForLayout = AnimationUtils.loadAnimation(getActivity(),
				R.anim.animation_slide_up_layout);

		linearLayout = (LinearLayout) rowView.findViewById(R.id.linearLayout);
		layoutToAnimate = (LinearLayout) rowView
				.findViewById(R.id.layoutToAnimate);

		etSearch = new SearchView(getActivity());
		LayoutParams lparams = new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT); // Width , height
		// float mediumMargin =
		// getActivity().getResources().getDimension(R.dimen.medium_margin);
		lparams.setMargins(R.dimen.medium_margin, R.dimen.medium_margin,
				R.dimen.medium_margin, 0);
		etSearch.setLayoutParams(lparams);
		theTextArea = (SearchView.SearchAutoComplete) etSearch
				.findViewById(R.id.search_src_text);
		theTextArea.setTextColor(Color.WHITE);
		theTextArea.setTextSize(getActivity().getResources().getDimension(
				R.dimen.edittext_size));
		etSearch.setIconified(false);
		etSearch.setLayoutTransition(new LayoutTransition());
		linearLayout.addView(etSearch, 0);
		// etSearch = (SearchView) rowView.findViewById(R.id.searchEdittext);
		etSearch.setVisibility(View.GONE);
		etSearch.setOnQueryTextListener(searchQueryListener);
		etSearch.setOnCloseListener(onCloseListener);

		deleteLayout = (LinearLayout) rowView.findViewById(R.id.deleteLayout);
		checkAllLayout = (LinearLayout) rowView.findViewById(R.id.checkLayout);
		// checkAllLayout.setOnClickListener(checkAllClickListener);
		checkBoxAll = (CheckBox) rowView.findViewById(R.id.checkBoxAll);
		checkBoxAll.setOnCheckedChangeListener(oncheckedChangedListener);

		deleteBtn = (Button) rowView.findViewById(R.id.deleteButton);
		deleteBtn.setOnClickListener(deleteClickListener);

		cancelBtn = (Button) rowView.findViewById(R.id.canselButton);
		cancelBtn.setOnClickListener(cancelClickListener);
		// checkBoxAll.setOnCheckedChangeListener(oncheckedChangedListener);
		adapter = new ListAdapter(getActivity(), notes, false);
		listView = (ListView) rowView.findViewById(R.id.listView);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(noteClickListener);
		listView.setOnItemLongClickListener(longClickListener);
		return rowView;
	}

	OnCloseListener onCloseListener = new OnCloseListener() {

		@Override
		public boolean onClose() {
			if (theTextArea.getText().length() <= 0) {
				setVisibilityOfSearch(View.GONE);
			} else {
				theTextArea.setText("");
			}

			etSearch.setIconified(false);
			return false;
		}
	};

	OnCheckedChangeListener oncheckedChangedListener = new OnCheckedChangeListener() {

		@Override
		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked) {
			if (((CheckBox) buttonView).isChecked()) {
				// checkBoxAll.setChecked(false);
				adapter.checkAll(true);
			} else {
				// checkBoxAll.setChecked(true);
				adapter.checkAll(false);
			}

		}
	};

	OnClickListener deleteClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			ArrayList<Note> list = new ArrayList<Note>();
			list = adapter.getList();
			int[] notesToDelete = adapter.getNotesToDelete();
			ArrayList<Note> listToRemain = new ArrayList<Note>();
			boolean everythingIsOK = true;
			for (int i = 0; i < notesToDelete.length; ++i) {
				Note note = list.get(i);
				if (notesToDelete[i] == 1) {
					// torolni sd cardrol
					// torolni adatbazisbol
					boolean isdeleted = false;
					Uri uriToDelete = Uri.parse(note.getUri());
					File fileToDelete = new File(uriToDelete.getPath());
					if (fileToDelete.exists()) {
						isdeleted = fileToDelete.delete();
					} else {
						isdeleted = true;
					}
					if (isdeleted) {
						manager = new DatabaseManager(getActivity());
						manager.open();
						boolean deletedFromDb = manager
								.deleteNote(note.getId());
						manager.close();
						if (deletedFromDb) {
							// list.remove(i);

							Log.i("delete", "SUCCESS!!!!");
						} else {
							Log.i("delete", "File not deleted from database!");
							everythingIsOK = false;
						}
					} else {
						Log.i("delete", "File not deleted from sd card!");
						everythingIsOK = false;
					}
					// Log.i("delete", "isceleted: "+isdeleted);
					// list.remove(i);

				} else {
					listToRemain.add(note);
				}
			}

			if (everythingIsOK) {
				adapter.setList(listToRemain);
				Log.i("delete", "Adapter refreshed!");
			}

		}
	};

	OnClickListener cancelClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			deleteLayout.setVisibility(View.GONE);
			adapter.showCheckBoxes(false);

		}
	};

	// OnClickListener checkAllClickListener = new OnClickListener() {
	//
	// @Override
	// public void onClick(View v) {
	// if (checkBoxAll.isChecked()) {
	// checkBoxAll.setChecked(false);
	// adapter.checkAll(false);
	// } else {
	// checkBoxAll.setChecked(true);
	// adapter.checkAll(true);
	// }
	//
	// }
	// };

	OnQueryTextListener searchQueryListener = new OnQueryTextListener() {

		@Override
		public boolean onQueryTextSubmit(String arg0) {
			hideKeyboard();
			return false;
		}

		@Override
		public boolean onQueryTextChange(String searchedText) {
			int textlength = searchedText.length();
			Log.v("search", "keresett: " + searchedText);
			String s = "";
			listForSearch = adapter.getList();
			listToShowAtSearch = new ArrayList<Note>();
			if (textlength > 0) {
				// myMenu.getItem(0).setVisible(false);
				// myMenu.getItem(1).setVisible(false);
				// myMenu.getItem(2).setVisible(false);
				for (int i = 0; i < listForSearch.size(); i++) {
					if (textlength <= listForSearch.get(i).getTitle().length()) {
						Log.v("search",
								i + "-edik elem: " + listForSearch.get(i));
						for (char c : listForSearch.get(i).getTitle()
								.toCharArray()) {
							if (s.length() < searchedText.length())
								s = s + c;
							else
								break;

							if (searchedText.equalsIgnoreCase(s))

								listToShowAtSearch.add(listForSearch.get(i));
						}
					}
					s = "";
				}
				listView.setAdapter(new ListAdapter(getActivity(),
						listToShowAtSearch, false));
			} else
				listView.setAdapter(new ListAdapter(getActivity(),
						listForSearch, false));

			return false;
		}
	};

	OnItemClickListener noteClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int position,
				long arg3) {
			Note selectedNote = adapter.getSelectedNote(position);
			Intent i = new Intent(getActivity(), AddNoteActivity.class);
			i.putExtra("note_to_open", selectedNote);
			startActivity(i);
		}

	};

	OnItemLongClickListener longClickListener = new OnItemLongClickListener() {

		@Override
		public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
				int position, long arg3) {
			Toast.makeText(getActivity(), "show", Toast.LENGTH_SHORT).show();
			deleteLayout.setVisibility(View.FOCUS_UP);
			adapter.showCheckBoxes(true);
			return false;
		}
	};

	public void hideKeyboard() {
		InputMethodManager inputManager = (InputMethodManager) getActivity()
				.getSystemService(Context.INPUT_METHOD_SERVICE);

		// check if no view has focus:
		View view = getActivity().getCurrentFocus();
		if (view != null) {
			inputManager.hideSoftInputFromWindow(view.getWindowToken(),
					InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}

	// public void setAnimationForDialog() {
	// etSearch.setLayout(
	// LinearLayout.LayoutParams.MATCH_PARENT,
	// LinearLayout.LayoutParams.WRAP_CONTENT);
	// etSearch.getWindow().getAttributes().windowAnimations =
	// R.style.DialogAnimation;
	//
	// }

	public void changeListConetnt(String tag, String type) {
		Log.i("mylist", "fragment begin tag: " + tag + " type:" + type);
		// String type = "ALL";

		Log.i("mylist", "fragment end tag: " + tag + " type:" + type);
		notes = new ArrayList<Note>();
		dbManager = new DatabaseManager(getActivity().getApplicationContext());
		dbManager.open();
		notes = dbManager.getFilteredList(
				getActivity().getApplicationContext(), type, tag);
		dbManager.close();
		adapter = new ListAdapter(getActivity(), notes, false);
		listView.setAdapter(adapter);
		listForSearch = adapter.getList();
	}

	public int getVisibilityOfSearch() {
		if (etSearch.getVisibility() == View.GONE) {
			return View.GONE;
		}
		return View.FOCUS_UP;
	}

	public void setVisibilityOfSearch(int visibility) {
		etSearch.setIconified(false);
		if (visibility == View.GONE) {
			layoutToAnimate.startAnimation(outForLayout);
			etSearch.startAnimation(out);
		} else {
			layoutToAnimate.startAnimation(in);
			etSearch.startAnimation(in);
		}

		etSearch.setVisibility(visibility);
	}

}
