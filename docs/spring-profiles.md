# Профили Spring (PulseCore)

Документ описывает Spring-профили для настройки окружения и переключения альтернативных реализаций.

## 1. Профили окружения
Определяют конфигурацию инфраструктуры (БД, логирование, URL).
* `local` — Локальная разработка
* `dev` — Среда разработки
* `prod` — Продакшен

## 2. Профили альтернативных реализаций
Для каждой пары ниже **обязательно** должен быть активирован ровно один профиль.

### 2.1. Проверка старта турниров (`TournamentStartService`)
* `start-db` — `DatabaseTournamentStartService`. Проверяет время из БД (без сети).
* `start-api` — `ApiTournamentStartService`. Парсит статус со страницы (через API).

### 2.2. Определение переноса игрока (`TransferDetector`)
* `transfer-standard` — `PlayerTransferDetector`. Стандартный поиск.
* `transfer-optimized` — `OptimizedTransferDetector`. Оптимизированный поиск.

### 2.3. Клиент API Мастерс (`MastersApi`)
* `masters-client-get` — `MastersApiGetClient`. GET-запрос к `/api/v1/tours`.
* `masters-client-admin` — `MastersApiAdminClient`. Старый запрос через `admin-ajax.php`.

### 2.4. Хеширование турниров (`TournamentHashCalculator`)
* `hash-sha256` — `Sha256TournamentHashCalculator`. Новый алгоритм.
* `hash-legacy` — `DiscoveryHashUtil`. Старый алгоритм.

## 3. Конфигурация `SPRING_PROFILES_ACTIVE`

**Локальная разработка (все актуальные реализации):**
```env
SPRING_PROFILES_ACTIVE=local,start-db,transfer-optimized,masters-client-get,hash-sha256