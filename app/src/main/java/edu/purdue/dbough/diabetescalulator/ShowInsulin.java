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
        getActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        Bundle insulinBundle = intent.getBundleExtra(MainActivity.INSULIN_MESSAGE);
        double insulinTotal = insulinBundle.getDouble("insulinTotal");
        double insulinFromFood = insulinBundle.getDouble("insulinFromFood");
        double insulinFromBloodSugar = insulinBundle.getDouble("insulinFromBloodSugar");

        DisplayInsulinDosage(insulinTotal, insulinFromFood, insulinFromBloodSugar);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            this.onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void DisplayInsulinDosage(double insulinTotal, double insulinFromFood, double insulinFromBloodSugar) {
        DecimalFormat decimalFormat = new DecimalFormat();
        insulinUnitsTextView = (TextView) findViewById(R.id.insulinUnitsTextView);
        foodTextView = (TextView) findViewById(R.id.foodTextView);
        slidingFactorTextView = (TextView) findViewById(R.id.slidingFactorTextView);

        decimalFormat.setMaximumFractionDigits(2);

        insulinUnitsTextView.setText(decimalFormat.format(insulinTotal) + " units");
        foodTextView.setText("From Food: " + decimalFormat.format(insulinFromFood) + " units");
        slidingFactorTextView.setText("Sliding Factor: " + decimalFormat.format(insulinFromBloodSugar) + " unit");
    }

}
