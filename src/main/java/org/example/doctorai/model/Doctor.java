package org.example.doctorai.model;

import java.util.UUID;

public interface Doctor {


    UUID getId();
    String getName();
    String getSpecialization();
    String getRole();
    String getContent();
}
