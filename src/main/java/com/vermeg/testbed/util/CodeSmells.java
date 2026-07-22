package com.vermeg.testbed.util;

/**
 * ================================================================
 * 6 CODE SMELLS detectes par SonarQube (MAJOR / CRITICAL)
 * Decision attendue : NOTIFY (ou AUTO_FIX selon seuil configure)
 * ================================================================
 */
public class CodeSmells {

    // SMELL #1 : Champ public non final (java:S1104)
    public String status = "active";

    // SMELL #2 : Nombre magique non explicite (java:S109)
    public double computePrice(double base) {
        return base * 1.19 + 42;
    }

    // SMELL #3 : Complexite cognitive trop elevee (java:S3776)
    public String classify(int a, int b, int c) {
        if (a > 0) {
            if (b > 0) {
                if (c > 0) {
                    if (a > b) {
                        if (b > c) {
                            return "case1";
                        } else {
                            return "case2";
                        }
                    } else {
                        return "case3";
                    }
                }
            }
        }
        return "default";
    }

    // SMELL #4 : Variable inutilisee (java:S1481)
    public int unused() {
        int notUsed = 100;
        int result = 5;
        return result;
    }

    // SMELL #5 : Methode vide sans commentaire (java:S1186)
    public void doNothing() {
    }

    // SMELL #6 : Duplication de chaine litterale (java:S1192)
    public void logErrors() {
        System.out.println("Erreur critique dans le module");
        System.out.println("Erreur critique dans le module");
        System.out.println("Erreur critique dans le module");
    }
}
