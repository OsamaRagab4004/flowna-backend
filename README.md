# Spring Boot 3.0 Security with JWT Implementation
This project demonstrates the implementation of security using Spring Boot 3.0 and JSON Web Tokens (JWT). It includes the following features:

## Features
* User registration and login with JWT authentication
* Password encryption using BCrypt
* Role-based authorization with Spring Security
* Customized access denied handling
* Logout mechanism
* Refresh token

## Technologies
* Spring Boot 3.0
* Spring Security
* JSON Web Tokens (JWT)
* BCrypt
* Maven

  

 Workspace: Collecting workspace information# Flowna - Study App Documentation

A modern collaborative study application built with Next.js, featuring real-time communication, file processing, and interactive learning materials.
🚀 Key Features
Real-time Collaboration

    Study Rooms: Create and join collaborative study sessions
    Live Chat: Real-time messaging with typing indicators
    WebSocket Integration: Persistent connections via STOMP protocol
    Multi-user Support: Host and participant roles with different permissions

Intelligent Content Generation

    AI-powered Study Materials: Upload PDFs and generate:
        Interactive mind maps (Mermaid diagrams)
        Concept definitions with real-world examples
        Q&A sessions
        Step-by-step guides
    Lecture Management: Access pre-generated study materials by lecture ID



Technical documentation for most important files in : src/main/java/com/flowna/app/studySquad (File that is responsible for all functions of the app, other entities are not yet ready in the frontend, but only in the backend)

### File: `src/main/java/com/flowna/app/studySquad/Agents/GeminiApiService.java`
This service acts as a client for the Google Gemini API. It provides asynchronous methods to generate content based on various inputs, such as text prompts, files (PDFs), and structured JSON schemas. It handles the construction of API requests and is designed to perform these network calls in background threads using `@Async`.

### File: `src/main/java/com/flowna/app/studySquad/Agents/LinkManagerService.java`
This service orchestrates the creation of study materials. It uses `StudyService` to generate content from a file and then passes the result to `RoomLectureService` to save the materials as a lecture associated with a specific room.

### File: `src/main/java/com/flowna/app/studySquad/Agents/PractiseService.java`
This service is responsible for generating multiple-choice questions (MCQs) for practice sessions. It follows a chain of asynchronous operations: first, it extracts and cleans text from a provided document; then, it splits the text into chunks and generates MCQs for each chunk in parallel to improve performance. Finally, it saves the generated questions to the database.

### File: `src/main/java/com/flowna/app/studySquad/Agents/StudyService.java`
This is a core service for generating a variety of study materials. The main method, `chainOfStudyFunctions`, asynchronously calls the Gemini API to generate definitions, mind maps, step-by-step guides, and Q&A sections based on the content of a source document. It runs these generation tasks in parallel and includes logic to sanitize and format the API's JSON and text responses.

### File: `src/main/java/com/flowna/app/studySquad/ExamScheduler/DynamicSchedulerService.java`
This service manages time-based events, specifically for exams. It uses Spring's `TaskScheduler` to schedule tasks that send WebSocket messages. For instance, it sends an `EXAM_STARTED` message immediately and schedules an `EXAM_FINISHED` message to be sent when the exam duration elapses. It also provides functionality to cancel scheduled tasks.

### File: `src/main/java/com/flowna/app/studySquad/ExamScheduler/ExamSchedulerManager.java`
This component is a state manager that tracks the participants currently active in an exam for each room. It uses a `ConcurrentHashMap` to ensure thread-safe operations for adding, removing, and counting participants.

### File: `src/main/java/com/flowna/app/studySquad/ExamScheduler/WebSocketMessageService.java`
This is a utility service for sending WebSocket messages. It provides a centralized method, `sendMessageToRoom`, which constructs a standard `Event` object and sends it to all clients subscribed to a specific room's topic using `SimpMessagingTemplate`.

### File: `src/main/java/com/flowna/app/studySquad/Room/RoomController.java`
This controller handles all HTTP and WebSocket requests related to study rooms. It exposes REST endpoints for actions like creating, joining, and leaving rooms, as well as managing timers and other settings. It also defines WebSocket message mappings for real-time features like typing indicators. It uses the `RoomService` for business logic and `SimpMessagingTemplate` to broadcast events to clients.

### File: `src/main/java/com/flowna/app/studySquad/Room/RoomTimerSettings.java`
This is a simple data class (POJO) that holds the configuration for a room's study timer, including its duration and the goals for the session. It uses Lombok annotations for boilerplate code.

### File: `src/main/java/com/flowna/app/studySquad/Room/TypingUser.java`
This is a data class used for WebSocket communication to notify clients when a user starts or stops typing in the chat. It contains the user's name and their typing status.

### File: `src/main/java/com/flowna/app/studySquad/Room/RoomService.java`
This service contains the core business logic for managing study rooms. Its responsibilities include creating rooms, handling users joining or leaving, changing the room host, toggling user ready status, and managing room-specific settings like session times and Discord links. It interacts with the database via `RoomRepository` and manages real-time state via `RoomUserManager` and `RoomTimerManager`.

### File: `src/main/java/com/flowna/app/studySquad/Event.java`
This class defines the standard structure for all WebSocket messages sent in the application. It acts as a generic wrapper containing an `EventType` to identify the message's purpose and a `payload` object carrying the relevant data.

### File: `src/main/java/com/flowna/app/studySquad/EventType.java`
This enumeration lists all possible types of WebSocket events that can occur within the application (e.g., `NEW_USER_JOINED`, `TIMER_STARTED`, `EXAM_FINISHED`). This provides a standardized and type-safe way to handle real-time communication.

### File: `src/main/java/com/flowna/app/studySquad/UserDetails.java`
This data class represents the real-time state of a user within a room, including their username, host status, and ready status. It is used to communicate user state changes to all clients in a room for UI updates.

### File: `src/main/java/com/flowna/app/config/WebSocketConfig.java`
This configuration class sets up the WebSocket communication layer using Spring's STOMP support. It registers the WebSocket endpoint (`/ws`) that clients connect to and configures a message broker to route messages to clients subscribed to specific topics (e.g., `/topic/rooms/{roomId}`).

### File: `src/main/java/com/flowna/app/config/WebConfig.java`
This configuration class sets up global Cross-Origin Resource Sharing (CORS) policies for the application. It ensures that the frontend client application, running on a different origin (like `localhost:3000`), is allowed to make requests to the backend server.

### File: `src/main/java/com/flowna/app/config/JwtService.java`
This service is responsible for all operations related to JSON Web Tokens (JWT), which are used for user authentication. It handles the creation, parsing, and validation of tokens, ensuring that user requests are secure and authenticated.

### File: `src/main/java/com/flowna/app/studySquad/Room/RoomTimerManager.java`
This component manages the state of active timers for all study rooms.
```java
ConcurrentHashMap<String, RoomTimerSettings> rooms = new ConcurrentHashMap<>();
```
This line declares the central data structure for the manager. It is a `ConcurrentHashMap` that maps a room's unique join code (`String`) to its corresponding `RoomTimerSettings`. Using `ConcurrentHashMap` ensures that multiple rooms can have their timers managed simultaneously in a thread-safe manner, preventing race conditions.
