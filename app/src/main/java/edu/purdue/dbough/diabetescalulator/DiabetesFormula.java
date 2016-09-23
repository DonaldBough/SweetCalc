package edu.purdue.dbough.diabetescalulator;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by donaldbough on 6/29/16.
 */
public class DiabetesFormula {
    public double targetBloodSugar;
    public double measuredBloodSugar;
    public double carbsConsumed;
    public double correctiveFactor;
    private double insulinDosage = 0;
    private double sensitivityFactor;
    Context context;

    //Used to find the insulin dosage amount based on user settings
    public DiabetesFormula(double measuredBloodSugar, double carbsConsumed , Context context) {
        this.measuredBloodSugar = measuredBloodSugar;
        this.carbsConsumed = carbsConsumed;
        this.context = context;

        LoadDefaults();
        SolveInsulinDosage();
    }

    //Do simple math over user's target blood sugar and sensitivity factor
    //to get insulin dosage
    private void SolveInsulinDosage() {
        if (measuredBloodSugar - targetBloodSugar >= 0 && correctiveFactor != 0) {
            insulinDosage += (measuredBloodSugar - targetBloodSugar);
            insulinDosage /= correctiveFactor; //Extra insulin for higher measured blood sugar level
        }

        if (sensitivityFactor != 0)
            insulinDosage += (carbsConsumed / sensitivityFactor); //Insulin from food
        else insulinDosage = 0;
    }

    public double GetInsulinDoseTotal() {
        return insulinDosage;
    }

    public double GetInsulinDosageFromBloodSugar() {
        return insulinDosage - GetInsulinDosageFromFood();
    }

    public double GetInsulinDosageFromFood() {
        if (sensitivityFactor != 0)
            return (carbsConsumed / sensitivityFactor);

        return 0;
    }

    //Loads defaults from settings
    public void LoadDefaults() {
        String targetDefaultInCaseOfNull = context.getResources().getString(R.string.default_target_bloodsugar);
        String correctiveDefaultInCaseOfNull = context.getResources().getString(R.string.default_correctivefactor);
        String sensitiveDefaultInCaseOfNull = context.getResources().getString(R.string.default_sensitivityfactor);

        SharedPreferences defaultValuesPref = PreferenceManager.getDefaultSharedPreferences(context);

        String defaultTargetBloodSugar = defaultValuesPref.getString("pref_TargetBloodSugar",
                targetDefaultInCaseOfNull);
        String defaultCorrectiveFactor = defaultValuesPref.getString("pref_CorrectionFactor",
                correctiveDefaultInCaseOfNull);
        String defaultSensitivityFactor = defaultValuesPref.getString("pref_SensitivityFactor",
                sensitiveDefaultInCaseOfNull);

        this.targetBloodSugar = Double.parseDouble(defaultTargetBloodSugar);
        this.correctiveFactor = Double.parseDouble(defaultCorrectiveFactor);
        this.sensitivityFactor =Double.parseDouble(defaultSensitivityFactor);
    }
}
