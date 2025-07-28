# 🏦 Bank Security Application

This is a Spring Boot-based backend banking application that provides secure RESTful APIs for managing user accounts, cards, investments, and admin functionalities. It integrates JWT-based authentication, role-based access control, and follows a modular structure separating concerns across controllers, services, repositories, DTOs, and security layers.

---

## 🔐 Features

- ✅ JWT Authentication & Authorization
- 👤 Role-based Access (Admin & Customer)
- 💼 Account Management
- 💳 Card Services
- 📈 Investment Tracking
- 🧼 DTO & Layered Architecture

---

## 📁 Project Structure

```
com.security.bank
├── accounts
├── admin
├── cards
├── config
├── dto
├── entity
├── investments
├── jwt
├── repository
├── security
├── service
├── user
└── BankApplication.java
```

---

## 🚀 Getting Started

### Prerequisites
- Java 17+
- Maven
- MySQL or H2
- Postman for testing

### Run Locally
```bash
git clone https://github.com/kumar9060/BankApp.git
cd BankApp
./mvnw spring-boot:run
```

### API Testing
Use Postman or Swagger to test APIs (like `/auth/login`, `/account/create`, etc.)

---

## 🙌 Contribution

Pull requests are welcome. For major changes, please open an issue first.

---

## 📧 Contact

Made 💻 by [Vikas Kumar](mailto:kumarvikaskv123@gmail.com)
