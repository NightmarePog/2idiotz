# `app = build $ replicate 2 Idiot`

Jsme dvoučlenný tým a náš název odkazuje na programovací jazyk **Haskell**.

## Technologie

| Vrstva | Technologie |
| --- | --- |
| Backend | Java 21, Spring Boot, Gradle |
| API | OpenAPI generované z backendu, TypeScript SDK a Zod validátory |
| Frontend | SvelteKit, TypeScript, Tailwind CSS, shadcn-svelte |
| Databáze | PostgreSQL 17, Flyway migrace |
| Nasazení | Docker Compose, Caddy jako reverzní proxy |

## Nasazení přes Docker Compose

Potřebujete Docker s pluginem Compose. Příkazy spouštějte z kořene repozitáře;
Java, Node.js ani Gradle nemusí být nainstalované na hostiteli.

### První spuštění

1. Vytvořte lokální konfiguraci, pokud ještě nemáte `.env`:

   ```sh
   cp -n .env.example .env
   ```

2. V souboru `.env` nastavte vlastní `POSTGRES_PASSWORD` místo ukázkového hesla.
   Soubor je v `.gitignore`.

3. Sestavte a spusťte všechny služby:

   ```sh
   docker compose up --build -d
   docker compose ps
   ```

Aplikace je dostupná na **[http://localhost:8080](http://localhost:8080)**.
Caddy směruje `/api` na backend a ostatní požadavky na frontend. Migrace databáze
se provedou automaticky při startu backendu. První sestavení může chvíli trvat.

### Ověření a logy

Po dokončení startu ověřte API:

```sh
curl --fail http://localhost:8080/api/v1/health
```

Očekávaná odpověď je `{"status":"ok"}`. Pro kontrolu služeb a sledování logů:

```sh
docker compose ps
docker compose logs --tail=100 -f
```

Sledování logů ukončíte pomocí `Ctrl+C`; kontejnery zůstanou spuštěné.

### Aktualizace a zastavení

Po stažení nebo úpravě zdrojového kódu znovu sestavte a spusťte služby:

```sh
docker compose up --build -d
```

Pro zastavení a odstranění kontejnerů:

```sh
docker compose down
```

Data PostgreSQL zůstávají ve svazku `postgres_data` i po odstranění kontejnerů.
**Přepínač `-v` u `docker compose down` smaže i databázová data.** Změna hesla
v `.env` sama nezmění heslo v již vytvořené databázi.

Compose zpřístupňuje aplikaci přes HTTP na portu `8080`; HTTPS zde není nastavené.
Konfigurace pro Tour de Cloud je v souboru [tourdeapp.yaml](tourdeapp.yaml)
a používá tajnou proměnnou `POSTGRES_PASSWORD`.

## Lokální vývoj frontendu

Potřebujete Node.js **22.22.2 nebo novější** a pnpm **10.34.5**.

```sh
cd frontend
pnpm install --frozen-lockfile
pnpm dev
```

Vývojový server používá testovací API s daty z Faker.js, takže nepotřebuje běžící
backend ani databázi. Adresu aplikace vypíše do terminálu; změny testovacích dat
se při restartu serveru ztratí.


Aplikace sse vyvíjí v rámci soutěže Tour de App
