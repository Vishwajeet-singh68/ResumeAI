# 🚀 ResumeAI – Microservices-Based AI Resume Builder

ResumeAI is a **microservices-based AI-powered resume builder platform** that helps users create, optimize, and export professional resumes using AI.

This repository contains all backend services organized using **Spring Boot + Spring Cloud architecture**.

---

## 📌 Project Overview

ResumeAI allows users to:

- Create and manage resumes
- Generate AI-based content (summary, experience, etc.)
- Check ATS (Applicant Tracking System) score
- Use different resume templates
- Export resumes (PDF/DOCX/JSON)
- Authenticate securely using JWT

---

## 🧩 Microservices Included

This repo (dev branch) contains:

- **AUTH-SERVICE** → User authentication & JWT  
- **RESUME-SERVICE** → Resume CRUD operations  
- **RESUME-SECTION-SERVICE** → Manage resume sections  
- **TEMPLATE-SERVICE** → Resume templates (HTML/CSS)  
- **AICONTENT-SERVICE** → AI content generation  
- **RESUMEAI-GATEWAY** → API Gateway (routing)  
- **SERVICE-REGISTRY** → Eureka Server  

---

## 🏗️ Architecture

```
Client → API Gateway → Microservices → Database
                     ↓
               Service Registry (Eureka)
```

- API Gateway handles routing
- Eureka manages service discovery
- Each service is independent and scalable

---

## ⚙️ Tech Stack

- **Backend:** Spring Boot, Spring Cloud  
- **Security:** Spring Security + JWT  
- **Database:** MySQL / MongoDB  
- **Service Discovery:** Eureka  
- **API Gateway:** Spring Cloud Gateway  
- **Build Tool:** Maven  

---

## 🚀 How to Run

### 1️⃣ Clone the repo
```bash
git clone https://github.com/Vishwajeet-singh68/ResumeAI.git
cd ResumeAI
git checkout dev
```

### 2️⃣ Start services in order

1. SERVICE-REGISTRY  
2. API-GATEWAY  
3. AUTH-SERVICE  
4. Other services  

---

## 🔐 Authentication

Uses **JWT Token-based authentication**

Add token in request header:

```http
Authorization: Bearer <your_token>
```

---

## 📂 Project Structure

```
ResumeAI/
│── auth-service/
│── resume-service/
│── resume-section-service/
│── template-service/
│── ai-content-service/
│── api-gateway/
│── service-registry/
```

---

## ✨ Features

- Microservices architecture  
- AI-based resume generation(Service created, integration pending)  
- ATS score checking  
- Template-based resume design  
- Secure authentication  

---

## 📌 Future Enhancements

- Job matching system  
- Notification service  
- Frontend integration (React) (some part completed)
