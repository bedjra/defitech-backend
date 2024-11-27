package com.defitech.GestUni.enums;

public enum NiveauEtude {
    PREMIERE_ANNEE(1),
    DEUXIEME_ANNEE(2),
    TROISIEME_ANNEE(3);

    private final int code;

    // Constructeur pour initialiser la valeur numérique
    NiveauEtude(int code) {
        this.code = code;
    }



    // Getter pour obtenir la valeur numérique
    public int getCode() {
        return code;
    }

    public static NiveauEtude fromCode(int code) {
        for (NiveauEtude niveau : NiveauEtude.values()) {
            if (niveau.getCode() == code) {
                return niveau;
            }
        }
        throw new IllegalArgumentException("Invalid code: " + code);
    }

    public static NiveauEtude[] getAllNiveaux() {
        return NiveauEtude.values();
    }
}
