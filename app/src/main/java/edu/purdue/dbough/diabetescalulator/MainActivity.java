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
import android.widget.*;

import java.io.FileOutputStream;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends Activity {
    public final static String INSULIN_MESSAGE = "edu.purdue.dbough.diabetescalculator.MESSAGE";
    private final int CARB_SERVING_UNIT = 1;
    private final int GRAMS_IN_CARB_UNIT = 15;
    EditText targetSugarField;
    EditText measuredBloodSugarField;
    EditText carbsConsumedField;
    EditText correctiveField;
    Spinner carbUnitSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!SignAgreement.AgreedToTerms(getApplicationContext())) {
            setContentView(R.layout.activity_main);
        }
        else {
            setContentView(R.layout.activity_sign_agreement);
        }

        LoadDefaults();
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //Sets default values to forms from preference file
    private void LoadDefaults() {
        targetSugarField = (EditText) findViewById(R.id.targetField);
        measuredBloodSugarField = (EditText) findViewById(R.id.measuredSugarField);
        carbsConsumedField = (EditText) findViewById(R.id.carbsField);
        correctiveField = (EditText) findViewById(R.id.correctionField);
        carbUnitSpinner = (Spinner) findViewById(R.id.carbUnitSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.carbUnitArray,
                android.R.layout.simple_spinner_item);
        SharedPreferences defaultValuesPref = getApplicationContext().getSharedPreferences //Load default values
                ("edu.purdue.dbough.diabetescalculator.PREFERENCE", MODE_PRIVATE);
        int defaultTargetBloodSugar = defaultValuesPref.getInt("edu.purdue.dbough.diabetescalculator.TARGET", 0);
        float defaultCorrectiveFactor = defaultValuesPref.getFloat("edu.purdue.dbough.diabetescalculator.FACTOR", 0);
        int defaultCarbUnitOption = defaultValuesPref.getInt("edu.purdue.dbough.diabetescalculator.UNIT", 0);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        carbUnitSpinner.setAdapter(adapter);

        if (defaultTargetBloodSugar != 0) {
            targetSugarField.setText(String.valueOf(defaultTargetBloodSugar));
        }
        if (defaultCorrectiveFactor != 0) {
            correctiveField.setText(String.valueOf(defaultCorrectiveFactor));
        }
        if (defaultCarbUnitOption != 0) {
            carbUnitSpinner.setSelection(defaultCarbUnitOption);
        }
    }

    public void sendMainActivity(View view) {
        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences
                ("edu.purdue.dbough.diabetescalculator.IS_INSTALLED", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("edu.purdue.dbough.diabetescalculator.IS_INSTALLED", "User has agreed to the " +
                "following: \n" + getString(R.string.legal_Agreement));
        editor.apply();
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }

    public void OnClickShowInsulinButton(View view) {
        int targetBloodSugar = Integer.parseInt(targetSugarField.getText().toString());
        int measuredBloodSugar = Integer.parseInt(measuredBloodSugarField.getText().toString());
        int currSelectedSpinner = carbUnitSpinner.getSelectedItemPosition();
        double carbsConsumed = Double.parseDouble(carbsConsumedField.getText().toString());
        double correctiveFactor = Double.parseDouble(correctiveField.getText().toString());
        double insulinFromCarbs;
        double insulinFromBloodSugar;
        double insulinTotal;
        DiabetesFormula diabetesFormula;
        Bundle bundleInsulinDose = new Bundle();
        Intent intent = new Intent (this, ShowInsulin.class);

        //Converting carbs into selected unit
        if (currSelectedSpinner == CARB_SERVING_UNIT) {
            carbsConsumed /= GRAMS_IN_CARB_UNIT;
        }

        diabetesFormula = new DiabetesFormula(targetBloodSugar, measuredBloodSugar, carbsConsumed, correctiveFactor);
        insulinTotal = diabetesFormula.GetInuslinDoseTotal();
        insulinFromCarbs = diabetesFormula.GetInsulinDoseFromCarbs();
        insulinFromBloodSugar = diabetesFormula.GetInsulinDosageFromBloodSugar();

        SaveNewDefaultsLogBloodSugar(targetBloodSugar, measuredBloodSugar, correctiveFactor, currSelectedSpinner,
                                     carbsConsumed);

        bundleInsulinDose.putDouble("insulinTotal", insulinTotal);
        bundleInsulinDose.putDouble("insulinFromCarbs", insulinFromCarbs);
        bundleInsulinDose.putDouble("insulinFromBloodSugar", insulinFromBloodSugar);
        intent.putExtra(INSULIN_MESSAGE, bundleInsulinDose);
        startActivity(intent);
    }
    
    public void SaveNewDefaultsLogBloodSugar(int targetBloodSugar, double measuredBloodSugar, double correctiveFactor,
                                             int currSelectedSpinner, double carbsConsumed) {
        //Save Target and Corrective Factor values
        Toast toast;
        Context context = getApplicationContext();
        SharedPreferences defaultValuesPref = context.getSharedPreferences("edu.purdue.dbough.diabetescalculator." +
                "PREFERENCE", Context.MODE_PRIVATE);
        SharedPreferences.Editor defaultValuesEditor = defaultValuesPref.edit();
        FileOutputStream outputStream;
        Date time = Calendar.getInstance().getTime();
        String fileName = "BloodSugarValues";
        String errorMessage = "Whoops, couldn't save your numbers";
        String currTime = time.toString();
        String outputData;

        defaultValuesEditor.putInt("edu.purdue.dbough.diabetescalculator.TARGET", targetBloodSugar);
        defaultValuesEditor.putFloat("edu.purdue.dbough.diabetescalculator.FACTOR", (float) correctiveFactor);
        defaultValuesEditor.putInt("edu.purdue.dbough.diabetescalculator.UNIT", currSelectedSpinner);
        defaultValuesEditor.commit();

        //Save blood sugar values for analytical algorithms one day
        try {
            outputStream = openFileOutput(fileName, Context.MODE_PRIVATE);


            //Saves time, measured blood sugar, target blood sugar, carbs, & corrective factor
            outputData = (currTime + ","
                        + measuredBloodSugar + ","
                        + targetBloodSugar + ","
                        + carbsConsumed + ","
                        + correctiveFactor + "\n");
            outputStream.write(outputData.getBytes());
            outputStream.close();
        } catch (Exception e) {
            toast = Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    public void OnMainActivityClick (View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }


    public void sendShowSettings(MenuItem item) {
        System.out.println("Show settings here®");
    }
}
