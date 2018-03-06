package org.openmrs.module.m2sysbiometrics.testdata;

import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.PersonName;

import java.util.Collections;

public final class PatientMother {

    public static Patient validInstance() {
        return new Patient();
    }

    public static Patient withName(String givenName, String middleName, String familyName) {
        PersonName personName = new PersonName(givenName, middleName, familyName);
        Person person = new Person();
        person.setNames(Collections.singleton(personName));
        return new Patient(person);
    }

    private PatientMother() {}
}
