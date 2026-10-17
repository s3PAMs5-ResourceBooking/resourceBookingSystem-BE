# Przygotowanie VPS (jednorazowo)

Wdrożenie robi workflow `Deploy` (`.github/workflows/deploy.yml`) po zielonym CI na `dev`
albo ręcznie z zakładki Actions. Workflow loguje się na VPS przez SSH hasłem i wgrywa pliki
do `~/resource-booking` w katalogu domowym użytkownika.

Wymagania: Ubuntu/Debian, min. 2 vCPU i 4 GB RAM (Keycloak + backend + PostgreSQL).

## 1. Docker i użytkownik

```bash
curl -fsSL https://get.docker.com | sudo sh   # jeśli Dockera jeszcze nie ma
sudo usermod -aG docker <user>                 # użytkownik z sekretu VPS_USER
```

Po dodaniu do grupy trzeba się przelogować. Sprawdzenie: `groups` zawiera `docker`.

## 2. Sekrety w GitHub

Repo → Settings → Secrets and variables → Actions:

| Sekret         | Wartość                              |
|----------------|--------------------------------------|
| `VPS_HOST`     | IP serwera                           |
| `VPS_USER`     | nazwa użytkownika                    |
| `VPS_PASSWORD` | hasło użytkownika                    |
| `VPS_PORT`     | port SSH (opcjonalny, domyślnie 22)  |

Serwer musi pozwalać na logowanie hasłem (`PasswordAuthentication yes` w `sshd_config`).

## 3. Plik `.env` na serwerze

```bash
mkdir -p ~/resource-booking && nano ~/resource-booking/.env
```

Wzór w `.env.example`. Ustaw co najmniej silne `POSTGRES_PASSWORD`, `KEYCLOAK_ADMIN_PASSWORD`
i `KEYCLOAK_BACKEND_CLIENT_SECRET`. `BACKEND_IMAGE` uzupełnia workflow.

Hasło do Postgresa zapisuje się przy pierwszym starcie wolumenu — późniejsza zmiana w `.env`
wymaga zmiany hasła w bazie albo usunięcia wolumenu `pgdata`.

## 4. Pierwsze wdrożenie

GitHub → Actions → Deploy → Run workflow (gałąź `dev`).

## Dostęp przed podpięciem domeny

Porty nasłuchują tylko na `127.0.0.1` (8080, 8180, 8025, 5432). Tunel SSH:

```bash
ssh -L 8080:localhost:8080 -L 8180:localhost:8180 -L 8025:localhost:8025 <user>@<IP>
```

Potem lokalnie: Swagger `http://localhost:8080/swagger-ui.html`, Keycloak `http://localhost:8180`,
Mailpit `http://localhost:8025`.

## Po podpięciu domeny (DuckDNS + nginx)

1. nginx z certyfikatem Let's Encrypt proxuje `api.<domena>` → `127.0.0.1:8080`
   i `auth.<domena>` → `127.0.0.1:8180` (z nagłówkami `X-Forwarded-*`).
2. W `.env`: `KEYCLOAK_PUBLIC_URL=https://auth.<domena>`.
3. W konsoli Keycloak dopisz adresy w klientach `swagger-ui` i `reservation-frontend`
   (Valid redirect URIs, Web origins). Realm importuje się tylko przy pierwszym starcie,
   więc zmiany w `realm-export.json` nie nadpisują istniejącego realmu.
