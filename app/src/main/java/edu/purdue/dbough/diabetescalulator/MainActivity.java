package edu.purdue.dbough.diabetescalulator;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
    EditText measuredBloodSugarField;
    EditText carbsConsumedField;
    Spinner carbUnitSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = new Intent (this, SignAgreement.class);
        if (!SignAgreement.AgreedToTerms(getApplicationContext())) {
            startActivity(intent);
        }
        else {
            setContentView(R.layout.activity_main);
            PreferenceManager.setDefaultValues(this, R.xml.preferences, false); //Load default settings
            LoadDefaults();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        Intent intent = new Intent (this, SettingsActivity.class);

        if (id == R.id.action_settings) {
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //Sets default carb unit, intializes view elements
    private void LoadDefaults() {
        measuredBloodSugarField = (EditText) findViewById(R.id.measuredSugarField);
        carbsConsumedField = (EditText) findViewById(R.id.carbsField);
        carbUnitSpinner = (Spinner) findViewById(R.id.carbUnitSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.carbUnitArray,
                android.R.layout.simple_spinner_item);
        SharedPreferences defaultValuesPref = PreferenceManager.getDefaultSharedPreferences(this);
        int defaultCarbUnitOption = defaultValuesPref.getInt("pref_CarbUnit", 0);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        carbUnitSpinner.setAdapter(adapter);
        carbUnitSpinner.setSelection(defaultCarbUnitOption);
    }

    public void OnClickShowInsulinButton(View view) {
        int measuredBloodSugar;
        int currSelectedSpinner;
        double carbsConsumed;
        double insulinFromFood;
        double insulinFromBloodSugar;
        double insulinTotal;
        DiabetesFormula diabetesFormula;
        Bundle bundleInsulinDose = new Bundle();
        Intent intent = new Intent (this, ShowInsulin.class);
        Toast errorToast = Toast.makeText(this.getApplicationContext(),"Whoops, something is empty", Toast.LENGTH_LONG);

        if (measuredBloodSugarField.getText().toString().equals("") ||
            carbsConsumedField.getText().toString().equals(""))
        {
            errorToast.show();
            return;
        }

        measuredBloodSugar = Integer.parseInt(measuredBloodSugarField.getText().toString());
        currSelectedSpinner = carbUnitSpinner.getSelectedItemPosition();
        carbsConsumed = Double.parseDouble(carbsConsumedField.getText().toString());

        //Converting carbs into selected unit
        if (currSelectedSpinner == CARB_SERVING_UNIT) {
            carbsConsumed /= GRAMS_IN_CARB_UNIT;
        }

        diabetesFormula = new DiabetesFormula(measuredBloodSugar, carbsConsumed, getApplicationContext());
        insulinTotal = diabetesFormula.GetInuslinDoseTotal();
        insulinFromFood = diabetesFormula.GetInsulinDoseFromFood();
        insulinFromBloodSugar = diabetesFormula.GetInsulinDosageFromBloodSugar();

        SaveNewDefaultsLogBloodSugar(measuredBloodSugar, currSelectedSpinner, carbsConsumed);

        bundleInsulinDose.putDouble("insulinTotal", insulinTotal);
        bundleInsulinDose.putDouble("insulinFromFood", insulinFromFood);
        bundleInsulinDose.putDouble("insulinFromBloodSugar", insulinFromBloodSugar);
        intent.putExtra(INSULIN_MESSAGE, bundleInsulinDose);
        startActivity(intent);
    }

    //Save Target and Corrective Factor values
    public void SaveNewDefaultsLogBloodSugar(double measuredBloodSugar, int currSelectedSpinner, double carbsConsumed) {
        Toast toast;
        Context context = getApplicationContext();
        SharedPreferences defaultValuesPref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor defaultValuesEditor = defaultValuesPref.edit();
        FileOutputStream outputStream;
        Date time = Calendar.getInstance().getTime();
        String fileName = "BloodSugarValues.csv";
        String errorMessage = "Whoops, couldn't save your numbers";
        String currTime = time.toString();
        String outputData;

        defaultValuesEditor.putInt("pref_CarbUnit", currSelectedSpinner);
        defaultValuesEditor.apply();

        //Save blood sugar values for analytical algorithms one day
        try {
            outputStream = openFileOutput(fileName, Context.MODE_APPEND);

            //Saves time, measured blood sugar, target blood sugar, carbs, & corrective factor
            outputData = (currTime + "," + measuredBloodSugar + "," + carbsConsumed + "\n");
            outputStream.write(outputData.getBytes());
            outputStream.close();
        } catch (Exception e) {
            toast = Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    public void OnClickMainActivity(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

}
