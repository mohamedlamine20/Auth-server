package com.personel.auth.server.exceptions;

public enum Codes {
    NUMBER("23505");

   private String value;

    Codes(String value) {
        this.value =value;
    }

    public String getValue() {
        return value;
    }
}
