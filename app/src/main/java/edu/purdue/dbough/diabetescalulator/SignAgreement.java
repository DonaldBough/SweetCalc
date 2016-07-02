package edu.purdue.dbough.diabetescalulator;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class SignAgreement extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_agreement);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_sign_agreement, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //Check if terms were agreed to already
    public static boolean AgreedToTerms(Context context) {
        SharedPreferences agreementPreference = null;
        String isAgreementSigned;

        agreementPreference = context.getSharedPreferences
                ("edu.purdue.dbough.diabetescalculator.IS_INSTALLED", Context.MODE_PRIVATE);
        isAgreementSigned = agreementPreference.getString
                ("edu.purdue.dbough.diabetescalculator.IS_INSTALLED", "Agreement Unsigned");

        if (isAgreementSigned.equals("Agreement Unsigned")) return false;
        return true;
    }

    public void sendMainActivity(View view) {
        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences
                                        ("edu.purdue.dbough.diabetescalculator.IS_INSTALLED", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        Intent i = new Intent(this, MainActivity.class);

        editor.putString("edu.purdue.dbough.diabetescalculator.IS_INSTALLED", "User has agreed to the " +
                "following: \n" + getString(R.string.legal_Agreement));
        editor.apply();
        startActivity(i);
    }
}
