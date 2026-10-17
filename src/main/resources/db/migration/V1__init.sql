-- Etap 1: lokalna kopia użytkownika z Keycloak i dziennik audytu.
-- Pozostałe tabele powstają w migracjach kolejnych etapów.

CREATE TABLE app_user (
    id          UUID PRIMARY KEY,
    keycloak_id VARCHAR(64)  NOT NULL UNIQUE,
    email       VARCHAR(254) NOT NULL,
    first_name  VARCHAR(100),
    last_name   VARCHAR(100),
    blocked     BOOLEAN      NOT NULL DEFAULT FALSE,
    ical_token  VARCHAR(64) UNIQUE,
    created_at  TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at  TIMESTAMPTZ  NOT NULL DEFAULT now()
);

CREATE INDEX idx_app_user_email ON app_user (lower(email));

CREATE TABLE audit_log (
    id          UUID PRIMARY KEY,
    occurred_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    actor_id    UUID REFERENCES app_user (id),
    action      VARCHAR(64) NOT NULL,
    entity_type VARCHAR(64),
    entity_id   UUID,
    details     JSONB       NOT NULL DEFAULT '{}'
);

CREATE INDEX idx_audit_log_occurred_at ON audit_log (occurred_at);
CREATE INDEX idx_audit_log_actor ON audit_log (actor_id, occurred_at);
