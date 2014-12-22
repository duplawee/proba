package com.lynxsolutions.studentnotenewestversion;

import com.lynxsolutions.utils.Note;
import com.lynxsolutions.utils.Utils;

import android.support.v7.app.ActionBarActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.View.OnClickListener;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends ActionBarActivity {
	private Button registerButton, loginButton;
	private TextView forgotPassword;
	private EditText etUsername, etPassword;
	private Note notificationNote = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		Utils.setActionBarColor(getApplicationContext(), getSupportActionBar());
		notificationNote = (Note) getIntent().getSerializableExtra(
				"note_to_open");
		if (notificationNote != null) {
			Log.i("note", "" + notificationNote.getTitle());
		}
		if (!Utils.isApplicationSentToBackground(getApplicationContext())
				&& notificationNote != null) {
			Intent i = new Intent(LoginActivity.this, AddNoteActivity.class);
			i.putExtra("note_to_open", notificationNote);
			i.putExtra("from_notification", true);
			startActivity(i);
		} else {
			setContentView(R.layout.activity_login);
			initView();
		}
	}

	public void initView() {
		String savedUsername = Utils.getSavedUsername(getApplicationContext());
		etUsername = (EditText) findViewById(R.id.userName_Login);
		etPassword = (EditText) findViewById(R.id.password_Login);
		if (savedUsername != null) {
			etUsername.setText(savedUsername);
			etPassword.requestFocus();
		}

		loginButton = (Button) findViewById(R.id.login_button);
		loginButton.setOnClickListener(loginClickListener);

		etPassword
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
							loginButton.performClick();
							return true;
						}
						return false;
					}
				});

		registerButton = (Button) findViewById(R.id.register_button);
		registerButton.setOnClickListener(registerClickListener);

		// forgotPassword = (TextView) findViewById(R.id.forgot_password_Login);
		// forgotPassword.setOnClickListener(forgotClickListener);

	}

	OnClickListener loginClickListener = new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			String savedPassword = Utils
					.getSavedPassword(getApplicationContext());
			if (savedPassword != null) {
				if (!etPassword.getText().toString().equals(null)
						|| !etPassword.getText().toString().equals("")) {
					if (savedPassword.equals(etPassword.getText().toString())) {
						Intent i = null;
						if (notificationNote == null) {
							i = new Intent(LoginActivity.this,
									ListingElementsActivity.class);
						} else {
							i = new Intent(LoginActivity.this,
									AddNoteActivity.class);
							i.putExtra("note_to_open", notificationNote);
						}
						startActivity(i);
						finish();
					} else {
						Toast.makeText(getApplicationContext(),
								"Invalid password!", Toast.LENGTH_SHORT).show();
					}
				} else {
					Toast.makeText(getApplicationContext(),
							"Enter your password!", Toast.LENGTH_SHORT).show();
				}
			} else
				Toast.makeText(getApplicationContext(),
						"You are not registered yet!", Toast.LENGTH_SHORT)
						.show();
		}
	};

	OnClickListener registerClickListener = new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			Intent i = new Intent(LoginActivity.this, RegisterActivity.class);
			startActivity(i);
		}
	};

	// OnClickListener forgotClickListener = new OnClickListener() {
	//
	// @Override
	// public void onClick(View v) {
	// Intent i = new Intent(LoginActivity.this,
	// ForgotPasswordActivity.class);
	// startActivity(i);
	// }
	// };

	// @Override
	// public boolean onCreateOptionsMenu(Menu menu) {
	// // Inflate the menu; this adds items to the action bar if it is present.
	// getMenuInflater().inflate(R.menu.main, menu);
	// return true;
	// }
	//
	// @Override
	// public boolean onOptionsItemSelected(MenuItem item) {
	// // Handle action bar item clicks here. The action bar will
	// // automatically handle clicks on the Home/Up button, so long
	// // as you specify a parent activity in AndroidManifest.xml.
	// int id = item.getItemId();
	// if (id == R.id.action_settings) {
	// return true;
	// }
	// return super.onOptionsItemSelected(item);
	// }
}
