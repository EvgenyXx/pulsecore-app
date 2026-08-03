# PulseCore

Платформа для отслеживания результатов турниров по настольному теннису.
Автоматически собирает данные с masters-league.com, уведомляет игроков о 
предстоящих матчах и подсчитывает заработок.

**Сайт:** [pulsecore-app.ru](https://pulsecore-app.ru)

---

## Возможности

- 🔔 Уведомления о добавлении в состав на ближайшие матчи
- 💰 Автоматический подсчёт заработка за выбранный период
- 📊 Статистика по турнирам и лигам
- 📅 Отображение составов на ближайшие дни
- 💳 Платная подписка через ЮKassa
- 🔐 Регистрация, вход через Яндекс ID, сброс пароля, управление профилем
- 🛡️ Админ-панель: управление игроками, подписками, ценами, массовая рассылка

---

## Технологии

- Java 17
- Spring Boot 3.4.4
- Spring Security, Spring Data JPA
- Spring Session (Redis)
- Spring WebSocket
- Spring Cache (Caffeine)
- Spring Validation
- PostgreSQL 15
- Redis 7
- Flyway (миграции БД)
- Docker / Docker Compose
- Jsoup (парсинг)
- JavaMail (email-уведомления)
- ЮKassa API (приём платежей)
- OAuth 2.0 (Яндекс ID)
- Web Push (VAPID)
- QR-коды (ZXing)
- PDF-отчёты (iText 7)
- Lombok, MapStruct

---

## Локальный запуск

### Backend
```bash
git clone -b dev git@github.com:EvgenyXx/pulsecore-app.git
cd pulsecore-app
docker compose -f docker-compose.local.yml --env-file .env.local up -d
```
### Frontend
```bash
git clone -b dev git@github.com:EvgenyXx/pulsecore-app.git
cd pulsecore-app
docker compose -f docker-compose.dev.yml --env-file .dev.local up -d
```