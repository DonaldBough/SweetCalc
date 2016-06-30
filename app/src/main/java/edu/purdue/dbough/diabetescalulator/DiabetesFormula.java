package edu.purdue.dbough.diabetescalulator;

/**
 * Created by donaldbough on 6/29/16.
 */
public class DiabetesFormula {
    public int targetBloodSugar;
    public int measuredBloodSugar;
    public double carbsConsumed;
    public double correctiveFactor;
    private double insulinDosage;

    public DiabetesFormula(int targetBloodSugar, int measuredBloodSugar, double carbsConsumed,
                           double correctiveFactors)
    {
        this.targetBloodSugar = targetBloodSugar;
        this.measuredBloodSugar = measuredBloodSugar;
        this.carbsConsumed = carbsConsumed;
        this.correctiveFactor = correctiveFactors;

        SolveInsulinDosage();
    }

    private void SolveInsulinDosage() {
        insulinDosage += carbsConsumed;
        if (measuredBloodSugar - targetBloodSugar >= 0) {
            insulinDosage = ((measuredBloodSugar - targetBloodSugar) / correctiveFactor);
        }
    }

    public double GetInsulinDoseFromCarbs() {
        return carbsConsumed;
    }

    public double GetInsulinDosageFromBloodSugar() {
        return insulinDosage - carbsConsumed;
    }
}
