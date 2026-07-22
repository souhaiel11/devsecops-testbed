package com.vermeg.testbed.service;

import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * ================================================================
 * 6 FAILLES DE TYPE BUG detectees par SonarQube
 * Decision attendue de la plateforme : AUTO_FIX
 * ================================================================
 */
@Service
public class BuggyService {

    // BUG #1 : Division par zero possible (java:S3518)
    // Fix attendu : verifier que count != 0
    public int average(int total, int count) {
        return total / count;
    }

    // BUG #2 : Dereferencement de null (java:S2259)
    // Fix attendu : verifier la nullite avant usage
    public int nameLength(String value) {
        String result = null;
        if (value != null && value.length() > 100) {
            result = value;
        }
        return result.length();
    }

    // BUG #3 : Bloc catch vide, exception avalee (java:S2486 / S1166)
    // Fix attendu : logger ou propager l'exception
    public int parse(String input) {
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
        }
        return -1;
    }

    // BUG #4 : Ressource non fermee, fuite (java:S2095)
    // Fix attendu : try-with-resources
    public int readByte(String path) throws IOException {
        FileInputStream fis = new FileInputStream(path);
        int b = fis.read();
        return b;
    }

    // BUG #5 : Comparaison de String avec == au lieu de equals (java:S4973)
    // Fix attendu : utiliser .equals()
    public boolean isAdmin(String role) {
        String expected = "ADMIN";
        return role == expected;
    }

    // BUG #6 : Resultat de méthode ignoré + boucle qui ne termine pas (java:S2189 / S2201)
    // Fix attendu : utiliser la valeur retournee / condition de sortie correcte
    public List<Integer> buildList(int n) {
        List<Integer> list = new ArrayList<>();
        int i = 0;
        while (i < n) {
            list.add(i);
            // i n'est jamais incremente -> boucle infinie si n > 0
        }
        return list;
    }
}
