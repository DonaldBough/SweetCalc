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
    private int CARB_SERVING_UNIT_INDEX = 1;
    private int fingerPrickCounter = 0;
    private double gramsInCarbServing = 0;
    EditText measuredBloodSugarField;
    EditText carbsConsumedField;
    ImageView handImage;
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
            LoadDefaultsSettings();
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
    private void LoadDefaultsSettings() {
        measuredBloodSugarField = (EditText) findViewById(R.id.measuredSugarField);
        carbsConsumedField = (EditText) findViewById(R.id.carbsField);
        carbUnitSpinner = (Spinner) findViewById(R.id.carbUnitSpinner);
        handImage = (ImageView) findViewById(R.id.handEmojiImageView);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.carbUnitArray,
                android.R.layout.simple_spinner_item);
        SharedPreferences defaultValuesPref = PreferenceManager.getDefaultSharedPreferences(this);
        int settingsCarbUnit = defaultValuesPref.getInt("pref_CarbUnit", 0);
        fingerPrickCounter = defaultValuesPref.getInt("pref_FingerPrick", 0);

        //Set serving size from settings
        String defaultServing = this.getString(R.string.default_GramsInCarbServing);
        String settingsServing = defaultValuesPref.getString("pref_GramsInCarbServing", defaultServing);
        this.gramsInCarbServing = Double.parseDouble(settingsServing);

        //Sets unit choice as carb or serving
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        carbUnitSpinner.setAdapter(adapter);
        carbUnitSpinner.setSelection(settingsCarbUnit);
        
        UpdateFingerImage();
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
        if (currSelectedSpinner == CARB_SERVING_UNIT_INDEX) {
            carbsConsumed /= gramsInCarbServing;
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
        defaultValuesEditor.putInt("pref_FingerPrick", (fingerPrickCounter += 1) % 8);
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

    public void OnClickFingerPrickArrow(View view) {
        String arrowType = (String) view.getTag();

        if (arrowType.equals("leftArrow")) {
            this.fingerPrickCounter--;
            if (this.fingerPrickCounter == -1) this.fingerPrickCounter = 7;
        }
        else if (arrowType.equals("rightArrow")) {
            this.fingerPrickCounter++;
            if (this.fingerPrickCounter == 8) this.fingerPrickCounter = 0;
        }
        UpdateFingerImage();
    }

    //Sets finger prick-monitor picture to the field fingerPrickCounter
    private void UpdateFingerImage() {
        switch (this.fingerPrickCounter) {
            case 0:
                handImage.setImageResource(R.drawable.finger1);
                break;
            case 1:
                handImage.setImageResource(R.drawable.finger2);
                break;
            case 2:
                handImage.setImageResource(R.drawable.finger3);
                break;
            case 3:
                handImage.setImageResource(R.drawable.finger4);
                break;
            case 4:
                handImage.setImageResource(R.drawable.finger5);
                break;
            case 5:
                handImage.setImageResource(R.drawable.finger6);
                break;
            case 6:
                handImage.setImageResource(R.drawable.finger7);
                break;
            case 7:
                handImage.setImageResource(R.drawable.finger8);
                break;
        }

    }

    //TODO two changes = change in picture. Arrows dont save on exit
}
