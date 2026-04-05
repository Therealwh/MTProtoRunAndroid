# MTProtoRun

Менеджер MTProto-прокси для Telegram на Android

## Особенности

- Автозагрузка прокси из GitHub источника
- Проверка пинга и статуса в реальном времени
- Гибридное определение страны: region из JSON + GeoIP API (ip-api.com) + DNS resolve для доменов
- Кэширование GeoIP результатов в DataStore
- Фильтрация по стране/пингу/статусу + поиск
- Glassmorphism UI в тёмной теме
- Открытие в Telegram / копирование ссылки / поделиться
- Офлайн-кэш и фоновое обновление
- Без рекламы и телеметрии

## Технологии

- **Kotlin 2.0** + Coroutines + Flow
- **Jetpack Compose** (Material 3)
- **MVVM + Clean Architecture**
- **Hilt** (Dependency Injection)
- **Retrofit + OkHttp** (Networking)
- **DataStore Preferences** (Settings & Cache)
- **Navigation Compose**

## Сборка

### Требования

- Android Studio Hedgehog+ (2023.1.1)
- JDK 17
- Android SDK 34
- Gradle 8.5

### Шаги

1. Клонировать репозиторий
   ```bash
   git clone https://github.com/yourusername/MTProtoRun.git
   cd MTProtoRun
   ```

2. Открыть в Android Studio или выполнить:
   ```bash
   ./gradlew assembleDebug
   ```

3. APK: `app/build/outputs/apk/debug/app-debug.apk`

## Архитектура

```
app/src/main/java/com/mtprorun/
├── MTProtoRunApp.kt              # Application + Hilt
├── di/                           # DI модули
├── data/
│   ├── model/                    # DTO модели
│   ├── remote/                   # API интерфейсы
│   ├── repository/               # Реализация репозитория
│   ├── local/                    # DataStore, кэш
│   └── util/                     # PingChecker, CountryResolver, DnsResolver
├── domain/
│   ├── model/                    # UI модели
│   ├── repository/               # Интерфейс репозитория
│   └── usecase/                  # Use Cases
├── presentation/
│   ├── theme/                    # Цвета, тема, типографика
│   ├── components/               # UI компоненты
│   ├── screens/                  # Экраны
│   ├── viewmodel/                # ViewModel'и
│   └── navigation/               # Навигация
└── ui/utils/                     # Утилиты
```

## Определение страны (гибридный подход)

1. **Быстрый путь**: `region` из JSON источника (ru → 🇷🇺, us → 🇺🇸 и т.д.)
2. **GeoIP API**: Если region = "eu" или null → запрос к ip-api.com
3. **DNS Resolve**: Для доменных хостов → IP → GeoIP API
4. **Кэш**: Результаты сохраняются в DataStore (вечный кэш)
5. **Fallback**: "🌐 ??" если всё не удалось

## API Источники

- **GitHub**: `raw.githubusercontent.com/Therealwh/MTPproxyLIST/.../proxy_all_verified.json`
- **GeoIP**: `ip-api.com/json/{ip}` (бесплатный, без API ключа, 45 req/min)

## Лицензия

MIT
