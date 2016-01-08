package edu.purdue.dbough.diabetescalulator;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.text.DecimalFormat;

public class ShowInsulin extends Activity {
    private double targetSugar;
    private double measuredSugar;
    private double correctiveFactor;
    private double carbGramsAmount;
    private double finalUnits;

    TextView insulinUnitsTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_insulin);

        insulinUnitsTextView = (TextView)findViewById(R.id.insulinUnitsTextView);
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
        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(2);
        String finalUnitsStr = df.format(finalUnits);
        insulinUnitsTextView.setText(finalUnitsStr + " units");
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
