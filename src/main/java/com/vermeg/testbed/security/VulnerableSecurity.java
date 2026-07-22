package com.vermeg.testbed.security;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.security.MessageDigest;
import java.util.List;
import java.util.Map;

/**
 * ================================================================
 * 6 FAILLES DE TYPE BLOCKER (Vulnerability) detectees par SonarQube
 * Decision attendue de la plateforme : AUTO_FIX
 * ================================================================
 */
@Component
public class VulnerableSecurity {

    private final JdbcTemplate jdbcTemplate;

    public VulnerableSecurity(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // BLOCKER #1 : Injection SQL par concatenation (java:S3649)
    // Fix attendu : requete parametree (?)
    public List<Map<String, Object>> findUser(String username) {
        String sql = "SELECT * FROM users WHERE username = '" + username + "'";
        return jdbcTemplate.queryForList(sql);
    }

    // BLOCKER #2 : Mot de passe / secret en dur (java:S2068)
    // Fix attendu : externaliser en variable d'environnement
    private static final String DB_PASSWORD = "SuperSecret@2024";
    public String getDbPassword() {
        return DB_PASSWORD;
    }

    // BLOCKER #3 : Cle de chiffrement en dur + algo faible (java:S6437 / S5542)
    // Fix attendu : cle hors code + AES/GCM au lieu de ECB
    public byte[] weakEncrypt(byte[] data) throws Exception {
        byte[] key = "1234567890123456".getBytes();
        SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, keySpec);
        return cipher.doFinal(data);
    }

    // BLOCKER #4 : Hachage cryptographique faible - MD5 (java:S4790)
    // Fix attendu : SHA-256 ou bcrypt
    public byte[] hashPassword(String password) throws Exception {
        MessageDigest md = MessageDigest.getInstance("MD5");
        return md.digest(password.getBytes());
    }

    // BLOCKER #5 : Injection de commande OS (java:S2076)
    // Fix attendu : liste blanche / eviter Runtime.exec avec entree utilisateur
    public Process runPing(String host) throws Exception {
        return Runtime.getRuntime().exec("ping -c 1 " + host);
    }

    // BLOCKER #6 : Path traversal - lecture de fichier arbitraire (java:S6096)
    // Fix attendu : valider/normaliser le chemin, confiner a un repertoire
    public String readFile(String filename) throws Exception {
        File file = new File("/data/reports/" + filename);
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
        }
        return sb.toString();
    }
}
