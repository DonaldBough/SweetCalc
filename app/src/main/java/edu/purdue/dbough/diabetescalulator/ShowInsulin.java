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

import java.text.DecimalFormat;

public class ShowInsulin extends Activity {
    private double targetSugar;
    private double measuredSugar;
    private double correctiveFactor;
    private double carbGramsAmount;
    private double finalUnits;
    private double slidingFactorUnits;

    TextView insulinUnitsTextView;
    TextView foodTextView;
    TextView slidingFactorTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_insulin);

        insulinUnitsTextView = (TextView)findViewById(R.id.insulinUnitsTextView);
        DecimalFormat df = new DecimalFormat();

        Intent intent = getIntent();
        double[] fakeBundle = intent.getDoubleArrayExtra("com.mycompany.DiabetesCalculator.MESSAGE");
        targetSugar = fakeBundle[0];
        measuredSugar = fakeBundle[1];
        correctiveFactor = fakeBundle[2];
        carbGramsAmount = fakeBundle[3];

        if (measuredSugar - targetSugar < 0) {
            finalUnits = carbGramsAmount;
        }

        else {
            finalUnits = ((measuredSugar - targetSugar) / correctiveFactor) + carbGramsAmount;
        }
        //Formatting final output for insulin dosage
        df.setMaximumFractionDigits(2);
        String finalUnitsStr = df.format(finalUnits);
        if (finalUnits == 1){
            insulinUnitsTextView.setText(finalUnitsStr + " unit");
        }
        else {
            insulinUnitsTextView.setText(finalUnitsStr + " units");
        }

        //Showing how much units came from each part
        foodTextView = (TextView)findViewById(R.id.slidingFactorTextView);
        String carbGramAmountStr = df.format(carbGramsAmount);
        if (carbGramsAmount == 1) {
            foodTextView.setText("From Food: " + carbGramAmountStr + " unit");
        }
        else {
            foodTextView.setText("From Food: " + carbGramAmountStr + " units");
        }
        slidingFactorTextView = (TextView)findViewById(R.id.foodTextView);
        slidingFactorUnits = finalUnits - carbGramsAmount;
        String slidingFactorUnitsStr = df.format(slidingFactorUnits);
        if (slidingFactorUnits == 1) {
            slidingFactorTextView.setText("Sliding Factor: " + slidingFactorUnitsStr + " unit");
        }
        else {
            slidingFactorTextView.setText("Sliding Factor: " + slidingFactorUnitsStr + " units");
        }
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
}
