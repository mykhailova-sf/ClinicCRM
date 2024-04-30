package com.somihmih.entry;

import com.calendarfx.model.Entry;
import com.somihmih.er.entity.Admission;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class AdmissionEntry extends com.calendarfx.model.Entry<Admission> {

    public AdmissionEntry(Admission admission) {
        super(admission.toString());

        LocalDate date = stringToDate(
                admission.getDate().substring(0, 10)
        );
        changeStartDate(date);
        changeEndDate(date);

        String[] interval = admission.getDate().substring(11).split("-");

        changeStartTime(stringToTime(interval[0]));
        changeEndTime(stringToTime(interval[1]));

        setUserObject(admission);
    }

    public Admission getAdmission() {
        return getUserObject();
    }
    private LocalTime stringToTime(String time) {
        return LocalTime.parse(
                time,
                DateTimeFormatter.ofPattern("HH:mm")
        );
    }

    private LocalDate stringToDate(String time) {
        return LocalDate.parse(
                time,
                DateTimeFormatter.ofPattern("dd:MM:yyyy")
        );
    }
}
