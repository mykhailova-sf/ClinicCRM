package com.somihmih.er.utils;

import com.somihmih.er.entity.Admission;
import com.somihmih.er.entity.Patient;

public class TitleBuilder {
    public String buildTitle(Admission admission, Patient patient) {
        return (patient != null)
                ? patient.getName() + ", tel: " + patient.getPhoneNumber() + "; " + admission.getDescription()
                : "Reserved time";
    }
}
