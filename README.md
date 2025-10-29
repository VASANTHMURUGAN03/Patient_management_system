Patient Management System
A microservices-based healthcare management system built with Spring Boot, featuring JWT authentication, gRPC communication, event-driven architecture with Kafka, and cloud-native deployment on AWS using LocalStack.

🏗️ Architecture Overview
This system follows a microservices architecture with the following components:

                                    ┌─────────────────┐
                                    │   HTTP Client   │
                                    └────────┬────────┘
                                             │
                                             ▼
                              ┌──────────────────────────────┐
                              │   Application Load Balancer  │
                              │      (AWS ALB/LocalStack)    │
                              └──────────────┬───────────────┘
                                             │
                                             ▼
                              ┌──────────────────────────────┐
                              │       API Gateway            │
                              │  (Spring Cloud Gateway)      │
                              │  - JWT Validation Filter     │
                              │  - Routing & Load Balancing  │
                              └──────┬───────────────────────┘
                                     │
                    ┌────────────────┼────────────────┐
                    │                │                │
                    ▼                ▼                ▼
         ┌──────────────────┐  ┌──────────────┐  ┌──────────────┐
         │  Auth Service    │  │   Patient    │  │   Billing    │
         │  (Port 4005)     │  │   Service    │  │   Service    │
         │                  │  │  (Port 4000) │  │  (Port 4001) │
         │ - JWT Generation │  │              │  │              │
         │ - Token Validate │  │ - CRUD Ops   │  │ - gRPC Server│
         │ - BCrypt Auth    │  │ - gRPC Client│◄─┼──(Port 9001)│
         └────────┬─────────┘  └──────┬───────┘  └──────┬───────┘
                  │                   │                  │
                  ▼                   ▼                  │
         ┌──────────────────┐  ┌──────────────┐         │
         │   PostgreSQL     │  │  PostgreSQL  │         │
         │   (Auth DB)      │  │ (Patient DB) │         │
         │   Port: 5001     │  │  Port: 5433  │         │
         └──────────────────┘  └──────────────┘         │
                                      │                  │
                                      │ Kafka Producer   │ Kafka Consumer
                                      ▼                  ▼
                              ┌────────────────────────────────┐
                              │      Apache Kafka (MSK)        │
                              │  - Patient Events Topic        │
                              │  - Billing Events Topic        │
                              └────────────┬───────────────────┘
                                           │
                                           │ Kafka Consumer
                                           ▼
                                  ┌──────────────────┐
                                  │    Analytics     │
                                  │     Service      │
                                  │   (Port 4002)    │
                                  │ - Event Processing│
                                  └──────────────────┘

Communication Patterns:
━━━━━━━━━━━━━━━━━━━━
→  HTTP/REST (Synchronous)
⟿  gRPC (High-performance RPC)
⤳  Kafka Events (Asynchronous)
🚀 Services
1. API Gateway (Port 4004)
Entry point for all client requests
Routes traffic to appropriate microservices
JWT token validation using reactive WebClient
Built with Spring Cloud Gateway
2. Auth Service (Port 4005)
User authentication and authorization
JWT token generation and validation
Password encryption with BCrypt
PostgreSQL database for user storage
Technologies: Spring Security, JJWT, JPA
3. Patient Service (Port 4000)
CRUD operations for patient records
gRPC client for billing service communication
Kafka producer for patient events
PostgreSQL database
Technologies: Spring Data JPA, gRPC, Kafka
4. Billing Service (Port 4001, gRPC 9001)
Manages billing accounts
Exposes gRPC server for synchronous communication
Kafka consumer for billing events
Technologies: gRPC, Protocol Buffers, Kafka
5. Analytics Service (Port 4002)
Processes patient events from Kafka
Real-time analytics and reporting
Event-driven architecture
Technologies: Spring Kafka, Protocol Buffers
🛠️ Technology Stack
Core Framework
Spring Boot 3.5.4 - Modern Java framework
Java 17 - LTS version with modern features
Communication
REST APIs - HTTP-based communication
gRPC - High-performance RPC framework
Apache Kafka - Event streaming platform
Protocol Buffers - Efficient serialization
Security
Spring Security - Authentication & Authorization
JWT (JSON Web Tokens) - Stateless authentication
BCrypt - Password hashing
Data & Persistence
Spring Data JPA - ORM framework
PostgreSQL - Relational database
Hibernate - JPA implementation
API Gateway
Spring Cloud Gateway - Reactive gateway
WebFlux - Reactive web framework
Infrastructure as Code
AWS CDK (Java) - Cloud infrastructure definition
CloudFormation - AWS resource provisioning
LocalStack - Local AWS cloud stack
DevOps & Deployment
Docker - Containerization
AWS ECS Fargate - Serverless container orchestration
Application Load Balancer - Traffic distribution
AWS RDS - Managed PostgreSQL
AWS MSK - Managed Kafka
Build & Development
Maven - Dependency management
Protobuf Maven Plugin - Protocol Buffers compilation
SpringDoc OpenAPI - API documentation
