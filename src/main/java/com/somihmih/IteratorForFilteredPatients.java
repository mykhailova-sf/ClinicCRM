package com.somihmih;

import com.somihmih.er.entity.Patient;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * IteratorForFilteredPatients
 * @author SoMihMih
 * @version 1.0
 * @see IteratorForFilteredPatients
 * {@inheritDoc}
 */

/**
 * For Iterator pattern
 */
public class IteratorForFilteredPatients implements Iterator<Patient> {

    private final String maskName;
    private final String maskPhone;
    private final Patient[] patients;
    private int currentIndex = 0;

    public IteratorForFilteredPatients(String maskName, String maskPhone, Patient[] patients) {
        this.maskName = maskName.toLowerCase();
        this.maskPhone = maskPhone.toLowerCase();
        this.patients = patients;
        moveToNextValid();
    }

    @Override
    public boolean hasNext() {
        return currentIndex < patients.length;
    }

    @Override
    public Patient next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        Patient patient = patients[currentIndex];
        currentIndex++;
        moveToNextValid();
        return patient;
    }

    private void moveToNextValid() {
        while (currentIndex < patients.length) {
            Patient patient = patients[currentIndex];
            String lowerCaseName = patient.getName().toLowerCase();
            String lowerCasePhone = patient.getPhoneNumber().toLowerCase();

            boolean isValidByName = maskName.isEmpty() || lowerCaseName.contains(maskName);
            boolean isValidByPhone = maskPhone.isEmpty() || lowerCasePhone.contains(maskPhone);

            if (isValidByName && isValidByPhone) {
                break;
            }
            currentIndex++;
        }
    }
}
