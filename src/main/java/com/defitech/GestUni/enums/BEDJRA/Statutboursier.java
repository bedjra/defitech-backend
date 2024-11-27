package com.defitech.GestUni.enums.BEDJRA;

public enum Statutboursier {
    OUI("Oui"),
    NON("Non"),
    COMPASSION("Compassion"),
    DOUBLANT_BTS("Doublant_BTS");
    ;

    private final String label;

    Statutboursier(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public static Statutboursier fromLabel(String label) {
        for (Statutboursier status : Statutboursier.values()) {
            if (status.getLabel().equals(label)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown label: " + label);
    }
}
