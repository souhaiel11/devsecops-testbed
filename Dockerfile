# ================================================================
# FAILLES IMAGE detectees par Trivy (--scanners vuln,misconfig)
# Decision attendue : BLOCK (CVE) + NOTIFY (misconfig)
# ================================================================

# IMAGE #1 : image de base ancienne = nombreuses CVE OS + JDK obsolete
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY . .
RUN mvn -q package -DskipTests

FROM eclipse-temurin:17-jre-jammy

# MISCONFIG #1 : conteneur tournant en root (pas d'utilisateur dedie)
WORKDIR /app

COPY --from=build /app/target/devsecops-testbed-1.0.0.jar app.jar

EXPOSE 8080

# MISCONFIG #2 : pas de HEALTHCHECK, pas de USER non-root
HEALTHCHECK --interval=30s --timeout=5s --retries=3 CMD curl -f http://localhost:8080/actuator/health || exit 1
RUN addgroup --system appgroup && adduser --system --ingroup appgroup appuser
USER appuser
ENTRYPOINT ["java", "-jar", "app.jar"]