### **TICKET**

## **Описание проекта**
TICKET – это простой REST-сервис на **Spring Boot**, предоставляющий API для работы с талонами к врачу.

Функционал:
- Запуск локального REST API.
- GET-запрос с Query Parameters для фильтрации.
- GET-запрос с Path Parameters для поиска талона по ID.
- Подключение **CheckStyle** для кодстайла.

## **Задание**
1. **Создать и запустить локально REST-сервис** на Java (Spring Boot + Maven/Gradle).
2. **Добавить GET эндпоинт с Query Parameters** для фильтрации талонов.
3. **Добавить GET эндпоинт с Path Parameters** для поиска талона по ID.
4. **Настроить CheckStyle** и исправить ошибки.
5. **Формат ответа – JSON.**

## **Установка и запуск**
### **1. Клонирование репозитория**
```sh
git clone https://github.com/Zaharysh37/ticket.git
cd ticket
```

### **2. Сборка и запуск приложения**
С использованием **Maven**:
```sh
mvn clean install
mvn spring-boot:run
```
С использованием **Gradle**:
```sh
gradle build
gradle bootRun
```

## **Доступные эндпоинты**
### **Получение списка толонов с фильтрацией (Query Parameters)**
```http
GET /ticket?specialization=Врач-хирург&medicalInstitution=1-я городская детская поликлиника г. Минска
```
Пример ответа:
```json
[
  {
    "id": 1,
    "name": "Иван Иванов Львович",
    "specialization": "Врач-хирург",
    "medicalInstitution": "1-я городская детская поликлиника г. Минска",
    "appointmentTime": "2025-02-20 14:30"
  }
]
```

### **Получение талона по ID (Path Parameters)**
```http
GET /ticket/{id}
```
Пример ответа:
```json
{
  "id": 2,
  "name": "Мария Петрова Попова",
  "specialization": "Врач-невролог",
  "medicalInstitution": "5-я городская клиническая больница г. Минска",
  "appointmentTime": "2025-02-20 15:45"
}
```

### **Настройка CheckStyle**

#### **Maven**
Добавьте в `pom.xml`:
```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-checkstyle-plugin</artifactId>
    <version>3.1.2</version>
    <configuration>
        <configLocation>checkstyle.xml</configLocation>
    </configuration>
</plugin>
```
Запустите проверку:
```sh
mvn checkstyle:check
```

#### **Gradle**
Добавьте в `build.gradle`:
```groovy
plugins {
    id 'checkstyle'
}

checkstyle {
    toolVersion = '10.12.0'
    configFile = file("${rootDir}/config/checkstyle/checkstyle.xml")
}

tasks.withType(Checkstyle).configureEach {
    reports {
        xml.required.set(true)
        html.required.set(true) 
    }
}
```
Запустите проверку:
```sh
gradle check
```

## **Требования**
- Java 17+
- Spring Boot 3+
- Maven/Gradle

## **Авторы**
Zaharysh37
