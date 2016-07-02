package edu.purdue.dbough.diabetescalulator;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import org.w3c.dom.Text;

import java.text.DecimalFormat;

public class ShowInsulin extends Activity {
    TextView insulinUnitsTextView;
    TextView foodTextView;
    TextView slidingFactorTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_insulin);

        Intent intent = getIntent();
        Bundle insulinBundle = intent.getBundleExtra(MainActivity.INSULIN_MESSAGE);
        double insulinTotal = insulinBundle.getDouble("insulinTotal");
        double insulinFromCarbs = insulinBundle.getDouble("insulinFromCarbs");
        double insulinFromBloodSugar = insulinBundle.getDouble("insulinFromBloodSugar");
        insulinUnitsTextView = (TextView) findViewById(R.id.insulinUnitsTextView);
        foodTextView = (TextView) findViewById(R.id.foodTextView);
        slidingFactorTextView = (TextView) findViewById(R.id.slidingFactorTextView);

        DisplayInsulinDosage(insulinTotal, insulinFromCarbs, insulinFromBloodSugar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_show_insulin, menu);
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

    private void DisplayInsulinDosage(double insulinTotal, double insulinFromCarbs, double insulinFromBloodSugar) {
        DecimalFormat decimalFormat = new DecimalFormat();

        decimalFormat.setMaximumFractionDigits(2);

        insulinUnitsTextView.setText(decimalFormat.format(insulinTotal) + " units");
        foodTextView.setText("From Food: " + decimalFormat.format(insulinFromCarbs) + " units");
        slidingFactorTextView.setText("Sliding Factor: " + decimalFormat.format(insulinFromBloodSugar) + " unit");
    }

}
