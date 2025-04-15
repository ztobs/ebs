# Event Booking System

A simple Event Booking System backend built with Java and Spring Boot, demonstrating event-driven architecture using Apache Kafka. This project showcases user authentication, event management, booking operations, real-time notifications, and inventory management.

## 📝 Table of Contents

- [Event Booking System](#event-booking-system)
  - [📝 Table of Contents](#-table-of-contents)
  - [🌟 Features](#-features)
  - [🏛️ Architecture](#️-architecture)
  - [🛠️ Technologies Used](#️-technologies-used)
  - [⚙️ Prerequisites](#️-prerequisites)
  - [🚀 Getting Started](#-getting-started)
    - [📥 Clone the Repository](#-clone-the-repository)
    - [🛠️ Environment Setup](#️-environment-setup)
    - [🏃‍♂️ Running the Services](#️-running-the-services)
  - [📜 API Documentation](#-api-documentation)
  - [📁 Project Structure](#-project-structure)

## 🌟 Features

- **User Authentication:** Secure registration and login using JWT.
- **Event Management:** Create, read, update, and delete events.
- **Booking Operations:** Book tickets for events with real-time seat inventory updates.
- **Real-Time Notifications:** Send booking confirmations via email.
- **Inventory Management:** Track and update available seats to prevent overbooking.
- **Event-Driven Communication:** Utilize Apache Kafka for inter-service communication.

## 🏛️ Architecture

```
[Client] 
    |
[API Gateway / Controllers]
    |
[Booking Service]
    |--(Publish)--> [Kafka Topics]
                     |
        -------------------------------
        |                             |
[Notification Service]          [Inventory Service]
```

- **Booking Service:** Handles user bookings and publishes events to Kafka.
- **Notification Service:** Listens to Kafka events to send confirmation emails.
- **Inventory Service:** Listens to Kafka events to update seat availability.

## 🛠️ Technologies Used

- **Backend:** Java, Spring Boot
- **Messaging:** Apache Kafka
- **Database:** PostgreSQL
- **Authentication:** JWT (JSON Web Tokens)
- **Containerization:** Docker, Docker Compose
- **API Documentation:** Swagger/OpenAPI
- **Email Service:** SendGrid / JavaMailSender

## ⚙️ Prerequisites

- **Java:** JDK 17 or higher
- **Maven:** 3.6+
- **Docker & Docker Compose:** Latest versions
- **Git:** For version control

## 🚀 Getting Started

### 📥 Clone the Repository

```bash
git clone https://github.com/your-username/event-booking-system.git
cd event-booking-system
```

### 🛠️ Environment Setup

1. **Configure Environment Variables:**

   Create a `.env` file in the root directory and set the necessary environment variables (e.g., database credentials, Kafka settings, email service API keys).

2. **Docker Compose:**

   Ensure Docker is running on your machine. The Docker Compose setup includes Kafka, Zookeeper, and PostgreSQL.

### 🏃‍♂️ Running the Services

Use Docker Compose to build and run all services:

```bash
docker compose up --build
```

This command will start:

- **Zookeeper & Kafka:** Messaging backbone.
- **PostgreSQL:** Database for all services.
- **Booking Service:** API for managing bookings and events.
- **Notification Service:** Sends email notifications.
- **Inventory Service:** Manages seat availability.

## 📜 API Documentation

Once the services are running, access the Swagger UI for interactive API documentation:

- **Booking Service Swagger:** [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

Explore the available endpoints for user authentication, event management, and booking operations.

## 📁 Project Structure

```
event-booking-system/
│
├── booking-service/
│   ├── src/
│   ├── Dockerfile
│   └── ...
│
├── notification-service/
│   ├── src/
│   ├── Dockerfile
│   └── ...
│
├── inventory-service/
│   ├── src/
│   ├── Dockerfile
│   └── ...
│
├── docker-compose.yml
├── README.md
└── ...
```
