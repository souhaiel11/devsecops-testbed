# ================================================================
# FAILLES IMAGE detectees par Trivy (--scanners vuln,misconfig)
# Decision attendue : BLOCK (CVE) + NOTIFY (misconfig)
# ================================================================

# IMAGE #1 : image de base ancienne = nombreuses CVE OS + JDK obsolete
FROM openjdk:8-jdk

# MISCONFIG #1 : conteneur tournant en root (pas d'utilisateur dedie)
WORKDIR /app

COPY target/devsecops-testbed-1.0.0.jar app.jar

EXPOSE 8080

# MISCONFIG #2 : pas de HEALTHCHECK, pas de USER non-root
ENTRYPOINT ["java", "-jar", "app.jar"]
