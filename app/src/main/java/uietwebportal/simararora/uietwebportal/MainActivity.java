package uietwebportal.simararora.uietwebportal;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity {

    private boolean doubleBackToExitPressedOnce;
    private NavigationDrawerFragment navigationDrawerFragment;
    private Fragment currentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getResources().getBoolean(R.bool.portrait_only)) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        FragmentManager fragmentManager = getFragmentManager();
        navigationDrawerFragment = (NavigationDrawerFragment) fragmentManager.findFragmentById(R.id.navigationDrawerFragment);
        navigationDrawerFragment.setUp(toolbar, (DrawerLayout) findViewById(R.id.drawerLayout), actionBar);
        switch (navigationDrawerFragment.getCurrentFragment()) {
            case 0:
                actionBar.setTitle("Result");
                break;
            case 1:
                actionBar.setTitle("Attendance");
                break;
        }
        if (savedInstanceState == null) {
            currentFragment = new ResultFragment();
            fragmentManager.beginTransaction().add(R.id.llDisplay, currentFragment, "ResultFragment").commit();
        }
    }

    public static void closeKeyboard(Context c, IBinder windowToken) {
        InputMethodManager mgr = (InputMethodManager) c
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(windowToken, 0);
    }

    @Override
    public void onBackPressed() {
        if (navigationDrawerFragment.getDrawer().isDrawerOpen(Gravity.START)) {
            navigationDrawerFragment.getDrawer().closeDrawers();
            return;
        }
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }
        doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Press BACK Again to Exit", Toast.LENGTH_SHORT)
                .show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_about:
                showAboutDialog();
                break;
            case R.id.action_feedback:
                sendFeedback();
                break;
            case R.id.action_website:
                openWebSite();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void openWebSite() {
        Uri uri = Uri.parse("https://uwp.puchd.ac.in/");
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, uri));
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "No Web Browser Found", Toast.LENGTH_SHORT).show();
        }
    }

    private void showAboutDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setTitle("About");
        dialogBuilder.setMessage("This application is created by Simarpreet Singh Arora, CSE - 3rd year, UIET.");
        dialogBuilder.setPositiveButton("Back", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialogBuilder.create().show();
    }

    private void sendFeedback() {
        String[] email = new String[]{"directoruiet@pu.ac.in"};
        Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
        emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, email);
        emailIntent.setType("plain/text");
        try {
            startActivityForResult(emailIntent, 0);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "No Email App Found", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        closeKeyboard(this, navigationDrawerFragment.getView().getWindowToken());
    }

    public Fragment getCurrentFragment(){
        return currentFragment;
    }

    public void setCurrentFragment(Fragment currentFragment){
        this.currentFragment = currentFragment;
    }
}

