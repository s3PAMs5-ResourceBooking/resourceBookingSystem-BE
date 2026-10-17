-- Uruchamiany raz, przy pierwszym starcie kontenera postgres (pusty wolumen pgdata).
-- Baza aplikacji i osobna baza Keycloak, do której backend nie sięga.
CREATE DATABASE reservations OWNER app;
CREATE DATABASE keycloak OWNER app;
