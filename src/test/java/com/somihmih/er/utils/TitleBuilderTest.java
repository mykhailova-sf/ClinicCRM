package com.somihmih.er.utils;

import com.somihmih.er.entity.Admission;
import com.somihmih.er.entity.Patient;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TitleBuilderTest {

    @Test
    void testBuildTitleWhenNoPatient() {
        // given
        Admission admission = new Admission("2021-01-01", 1);
        Patient patient = null;
        TitleBuilder titleBuilder = new TitleBuilder();

        // when
        String title = titleBuilder.buildTitle(admission, patient);

        // then
        assertEquals("Reserved time", title);
    }

    @Test
    void testBuildTitleWhenPatient() {
        // given
        Admission admission = new Admission("2021-01-01", 1);
        Patient patient = new Patient("John Doe", "123456789");
        TitleBuilder titleBuilder = new TitleBuilder();

        // when
        String title = titleBuilder.buildTitle(admission, patient);

        // then
        assertEquals("John Doe, tel: 123456789; ", title);
    }
}