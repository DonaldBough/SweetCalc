package edu.purdue.dbough.diabetescalulator;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends Activity {
    public final static String EXTRA_MESSAGE = "com.mycompany.DiabetesCalculator.MESSAGE";
    EditText measuredSugarField;
    EditText targetSugarField;
    EditText correctiveField;
    EditText carbField;
    Spinner carbUnitSpinner;

    private int targetSugar;
    private double measuredSugar;
    private int correctiveFactor;
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

        carbUnitSpinner = (Spinner)findViewById(R.id.carbUnitSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.carbUnitArray, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        carbUnitSpinner.setAdapter(adapter);

        //Load preset target sugar and corrective factor
        SharedPreferences sharedPref = getApplicationContext()
                .getSharedPreferences("com.mycompany.DiabetesCalculator.PREFERENCE", MODE_PRIVATE);
        int defaultTargetSugar =
                sharedPref.getInt("com.mycompany.DiabetesCalculator.TARGET", 0);
        int defaultCorrectiveFactor =
                sharedPref.getInt("com.mycompany.DiabetesCalculator.FACTOR", 0);
        int defaultSpinnerOption =
                sharedPref.getInt("com.mycompany.DiabetesCalculator.UNIT", 0);

        if (defaultTargetSugar != 0)
            targetSugarField.setText(String.valueOf(defaultTargetSugar));
        if (defaultCorrectiveFactor != 0)
            correctiveField.setText(String.valueOf(defaultCorrectiveFactor));
        if (defaultSpinnerOption != 0)
            carbUnitSpinner.setSelection(defaultSpinnerOption);
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
        int currSelectedSpinner = carbUnitSpinner.getSelectedItemPosition();
        Context context = getApplicationContext();

       try {
           targetSugar = Integer.parseInt(targetSugarField.getText().toString());
           measuredSugar = Double.parseDouble(measuredSugarField.getText().toString());
           correctiveFactor = Integer.parseInt(correctiveField.getText().toString());
           carbGramsAmount = Double.parseDouble(carbField.getText().toString());

           //Hardcoded serving size is 15g per serving
           if (currSelectedSpinner == 1)
               carbGramsAmount /= 15;

           fakeBundle[0] = targetSugar;
           fakeBundle[1] = measuredSugar;
           fakeBundle[2] = correctiveFactor;
           fakeBundle[3] = carbGramsAmount;

           //Save Target and Corrective Factor values
           SharedPreferences sharedPref = context.getSharedPreferences(
                   "com.mycompany.DiabetesCalculator.PREFERENCE", Context.MODE_PRIVATE);
           SharedPreferences.Editor editor = sharedPref.edit();
           editor.putInt("com.mycompany.DiabetesCalculator.TARGET", targetSugar);
           editor.putInt("com.mycompany.DiabetesCalculator.FACTOR", correctiveFactor);
           editor.putInt("com.mycompany.DiabetesCalculator.UNIT", currSelectedSpinner);
           editor.commit();

           //Save blood sugar values for analytical algorithms one day
           String fileName = "BloodSugarValues";
           FileOutputStream outputStream;
           try {
               outputStream = openFileOutput(fileName, Context.MODE_PRIVATE);
               Date time = Calendar.getInstance().getTime();
               String currTime = time.toString();
               //Saves time, measured blood sugar, target blood sugar, carbs, & corrective factor
               String outputData = (currTime + ","
                       + measuredSugarField.getText().toString() + ","
                       + targetSugarField.getText().toString() + ","
                       + carbField.getText().toString() + ","
                       + correctiveFactor + "\n");
               outputStream.write(outputData.getBytes());
               outputStream.close();
           } catch (Exception e) {
               String message = "Whoops, couldn't save your numbers";
               Toast t = Toast.makeText(context, message, Toast.LENGTH_SHORT);
               t.show();
           }

           Intent intent = new Intent (this, ShowInsulin.class);
           intent.putExtra(EXTRA_MESSAGE, fakeBundle);
           startActivity(intent);
       }
       catch (Exception e) {
           Toast t = Toast.makeText(context, "Whoops, forgot a number?", Toast.LENGTH_SHORT);
           t.show();
       }
    }
    public void onMainActivityClick (View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
