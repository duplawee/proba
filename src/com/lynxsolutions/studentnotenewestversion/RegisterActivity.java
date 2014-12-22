package com.lynxsolutions.studentnotenewestversion;

import java.io.File;
import java.util.ArrayList;
import java.util.regex.Pattern;

import com.lynxsolutions.utils.DatabaseManager;
import com.lynxsolutions.utils.Note;
import com.lynxsolutions.utils.Utils;

import android.support.v7.app.ActionBarActivity;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class RegisterActivity extends ActionBarActivity {

	private Button registerBtn;
	private EditText etUsername, etPassword, etConfirmPassword;
	private String account;
	private DatabaseManager manager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_register);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		Utils.setActionBarColor(getApplicationContext(), getSupportActionBar());
		account = getAccount();
		initView();
	}

	private String getAccount() {
		Pattern emailPattern = Patterns.EMAIL_ADDRESS; // API level 8+
		Account[] accounts = AccountManager.get(getApplicationContext())
				.getAccounts();
		for (Account account : accounts) {
			if (emailPattern.matcher(account.name).matches()) {
				String possibleEmail = account.name;
				Log.i("email", possibleEmail);
				return possibleEmail;
			}
		}
		return "";

	}

	public void initView() {
		etUsername = (EditText) findViewById(R.id.userName_Register);
		if (account.length() > 0) {
			etUsername.setText(account);
		}
		etPassword = (EditText) findViewById(R.id.password_Register);
		etConfirmPassword = (EditText) findViewById(R.id.confirmPassword_Register);
		registerBtn = (Button) findViewById(R.id.btnRegister_Register);
		registerBtn.setOnClickListener(registerClickListener);

		etConfirmPassword
				.setOnEditorActionListener(new EditText.OnEditorActionListener() {

					@Override
					public boolean onEditorAction(TextView v, int actionId,
							KeyEvent event) {
						if (actionId == EditorInfo.IME_ACTION_SEARCH
								|| actionId == EditorInfo.IME_ACTION_DONE
								|| event.getAction() == KeyEvent.ACTION_DOWN
								&& event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
							InputMethodManager imm = (InputMethodManager) getApplicationContext()
									.getSystemService(
											Context.INPUT_METHOD_SERVICE);
							imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
							registerBtn.performClick();
							return true;
						}
						return false;
					}
				});

	}

	OnClickListener registerClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			String message = checkFields();
			if (message.equals("")) {
				ArrayList<Note> avaliableNotes = new ArrayList<Note>();
				manager = new DatabaseManager(getApplicationContext());
				manager.open();
				avaliableNotes = manager.getAllNotes(getApplicationContext());
				manager.close();
				
				deleteNotes(avaliableNotes);
				Utils.saveUsername(getApplicationContext(), etUsername
						.getText().toString());
				Utils.savePassword(getApplicationContext(), etPassword
						.getText().toString());

				Intent i = new Intent(RegisterActivity.this,
						ListingElementsActivity.class);
				startActivity(i);
				finish();
			} else {
				Toast.makeText(getApplicationContext(), message,
						Toast.LENGTH_SHORT).show();
			}
		}
	};

	public void deleteNotes(ArrayList<Note> notesToDelete) {
		boolean everythingIsOK = true;
		for (Note note : notesToDelete) {

			Uri uriToDelete = Uri.parse(note.getUri());
			File fileToDelete = new File(uriToDelete.getPath());
			boolean isdeleted = fileToDelete.delete();

			if (isdeleted) {
				manager = new DatabaseManager(getApplicationContext());
				manager.open();
				boolean deletedFromDb = manager.deleteNote(note.getId());
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

		}
//		if (everythingIsOK) {
//			Toast.makeText(getApplicationContext(), "everythingIsOk",
//					Toast.LENGTH_SHORT).show();
//		}
	}

	public String checkFields() {
		String password, confirmPswd, username;
		username = etUsername.getText().toString();
		password = etPassword.getText().toString();
		confirmPswd = etConfirmPassword.getText().toString();

		if (username.equals("") || password.equals("")
				|| confirmPswd.equals("")) {
			return "Fill all fields!";
		}
		if (!password.equals(confirmPswd)) {
			return "Passwords do not match!";
		}
		return "";
	}

	// @Override
	// public boolean onCreateOptionsMenu(Menu menu) {
	// // Inflate the menu; this adds items to the action bar if it is present.
	// getMenuInflater().inflate(R.menu.register, menu);
	// return true;
	// }

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
}
