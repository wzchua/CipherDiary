package domain.a.not.wz.cipherdiary;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import domain.a.not.wz.cipherdiary.data.DiaryProviderHelper;

public class LoginActivity extends AppCompatActivity implements LoginActivityFragment.OnPasswordInputListener{
    private static final String EMPTY_STRING = "";
    private AlertDialog mAlertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mAlertDialog = setupAlertDialog();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            case R.id.action_settings: {

                return true;
            }
            case R.id.action_new_diary: {
                mAlertDialog.show();
                return true;
            }
            //TODO: rename diary
            case R.id.action_delete_diary: {
                Intent intent = new Intent(this, DeleteDiaryListActivity.class);
                startActivity(intent);
                //TODO do a toast if no diary?
                return true;
            }

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public AlertDialog setupAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Create a new diary");
        builder.setView(R.layout.dialog_create_diary);

        // Set up the buttons
        builder.setPositiveButton("OK", null); //onClick override later to prevent closing
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        AlertDialog alertDialog = builder.create();

        //Ok override effects by overriding the button onClick itself
        //Default dialog onClick dismisses the dialog
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(final DialogInterface dialog) {
                final AlertDialog aDilog = (AlertDialog) dialog;
                aDilog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.v("alertDialog", "onCLick");
                        EditText diaryNameView = (EditText) aDilog.findViewById(R.id.create_diary_name);
                        EditText passwordView = (EditText) aDilog.findViewById(R.id.create_password);
                        EditText passwordConfirmationView = (EditText) aDilog.findViewById(R.id.create_password_confirmation);

                        if (TextUtils.isEmpty(diaryNameView.getText()) ||
                                TextUtils.isEmpty(passwordView.getText()) ||
                                TextUtils.isEmpty(passwordConfirmationView.getText())) {
                            Snackbar.make(v, "Please fill in all fields", Snackbar.LENGTH_SHORT)
                                    .setAction("Action", null).show();
                            return;
                        }

                        if (!TextUtils.equals(passwordView.getText(), passwordConfirmationView.getText())) {
                            Snackbar.make(v, "passwords do not match", Snackbar.LENGTH_SHORT)
                                    .setAction("Action", null).show();
                            return;
                        }
                        String diaryName = diaryNameView.getText().toString();
                        String password = passwordView.getText().toString();

                        DiaryProviderHelper.createNewDiary(getContentResolver(), diaryName, password);

                        LoginActivityFragment maf = (LoginActivityFragment) getSupportFragmentManager()
                                .findFragmentById(R.id.fragment_main);
                        maf.updateSpinnerList();

                        dialog.dismiss();
                    }
                });
            }
        });

        alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                clearAlertDialogText((AlertDialog) dialog);
            }
        });

        return alertDialog;
    }

    private void clearAlertDialogText(AlertDialog dialog) {
        EditText diaryName = (EditText) dialog.findViewById(R.id.create_diary_name);
        EditText password = (EditText) dialog.findViewById(R.id.create_password);
        EditText passwordConfirmation = (EditText) dialog.findViewById(R.id.create_password_confirmation);

        diaryName.setText(EMPTY_STRING);
        password.setText(EMPTY_STRING);
        passwordConfirmation.setText(EMPTY_STRING);
        diaryName.requestFocus();//reset focus back to first entry
    }

    @Override
    public boolean onDatabaseAuthentication(String diaryName, String dbFileName, String password) {
        Cursor c = DiaryProviderHelper.selectDiary(getContentResolver(), dbFileName, password);
        boolean hasData = c.moveToFirst();
        c.close();

        if(hasData) {
            Intent intent = new Intent(this, CoreActivity.class);
            intent.putExtra("diaryName", diaryName);
            startActivity(intent);
            finish();
            return true;
        } else {
            Log.v("MAIN_ACTIVITY", "login failure");
            return false;
        }
    }
}
