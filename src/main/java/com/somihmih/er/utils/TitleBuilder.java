package com.somihmih.er.utils;

import com.somihmih.er.entity.Admission;
import com.somihmih.er.entity.Patient;

// 2. TitleBuilder via Builder pattern
public class TitleBuilder {

    private final Admission admission;

    private Patient patient = null;

    public TitleBuilder(Admission admission) {
        this.admission = admission;
    }

    public TitleBuilder setPatient(Patient patient) {
        this.patient = patient;
        return this;
    }

    public String buildTitle() {
        return build();
    }

    private String build() {
        return (patient != null)
                ? patient.getName() + ", tel: " + patient.getPhoneNumber() + "; " + admission.getDescription()
                : "Reserved time";
    }
}
