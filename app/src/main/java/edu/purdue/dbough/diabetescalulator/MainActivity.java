package edu.purdue.dbough.diabetescalulator;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity {
    public final static String EXTRA_MESSAGE = "com.mycompany.DiabetesCalculator.MESSAGE";
    EditText measuredSugarField;
    EditText targetSugarField;
    EditText correctiveField;
    EditText carbField;

    private double targetSugar;
    private double measuredSugar;
    private double correctiveFactor;
    private double carbGramsAmount;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        targetSugarField = (EditText)findViewById(R.id.targetField);
        measuredSugarField = (EditText)findViewById(R.id.measuredSugarField);
        correctiveField = (EditText)findViewById(R.id.correctionField);
        carbField = (EditText)findViewById(R.id.carbsField);
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

    public void solveFormula (View view) {
        double fakeBundle[] = new double[4];

       try {
           targetSugar = Double.parseDouble(targetSugarField.getText().toString());
           measuredSugar = Double.parseDouble(measuredSugarField.getText().toString());
           correctiveFactor = Double.parseDouble(correctiveField.getText().toString());
           carbGramsAmount = Double.parseDouble(carbField.getText().toString());

           fakeBundle[0] = targetSugar;
           fakeBundle[1] = measuredSugar;
           fakeBundle[2] = correctiveFactor;
           fakeBundle[3] = carbGramsAmount;

           Intent intent = new Intent (this, ShowInsulin.class);
           intent.putExtra(EXTRA_MESSAGE, fakeBundle);
           startActivity(intent);
       }
       catch (Exception e) {
           Context context = getApplicationContext();
           Toast t = Toast.makeText(context, "Whoops, forgot a number?", Toast.LENGTH_SHORT);
           t.show();
       }
    }
    public void onMainActivityClick (View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
