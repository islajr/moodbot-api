
# MoodBot Backend – AI Mental Health Support Chatbot

This is the backend service for MoodBot, a mental health chatbot powered by OpenAI. It handles real-time messaging, emotion detection, calming techniques, and daily mood tracking.

---

## Overview for Backend Integration

This API is designed to plug into your frontend app. It supports both REST and WebSocket communication for chat, and all AI logic is modularized and ready for deployment.

---

## Requirements

- Python 3.10+
- OpenAI API Key
- Docker (optional, for containerized deployment)

---

## Project Files

```
.
├── main.py            # FastAPI app (REST + WebSocket)
├── models.py          # Pydantic model (UserMessage)
├── ai_service.py      # Handles AI response via MentalHealthChatbot
├── moodbot.py         # MentalHealthChatbot class (OpenAI logic + state)
├── requirements.txt
├── Dockerfile
├── .dockerignore
└── README.md
```

---

## How to Run the Backend

### 1. Clone and set up
```bash
git clone https://github.com/your-team/moodbot-api.git
cd moodbot-api
python -m venv venv
source venv/bin/activate  # On Windows: venv\Scripts\activate
pip install -r requirements.txt
```

### 2. Add your `.env` file
Create a `.env` file in the root directory:
```env
OPENAI_API_KEY=your_openai_key_here
```

### 3. Start the server
```bash
uvicorn main:app --reload
```

API will run at `http://localhost:8000`

---

## Endpoints for Integration

### REST Endpoint
**POST /chat/**  
Send a message to the bot.

```http
POST http://localhost:8000/chat/
Content-Type: application/json

{
  "user_id": "user123",
  "message": "I'm feeling overwhelmed"
}
```

Returns:
```json
{
  "response": "I'm really sorry to hear that. Would you like to talk about it?"
}
```

### WebSocket Endpoint
**ws://localhost:8000/ws/{user_id}**  
Example: `ws://localhost:8000/ws/johndoe123`

Messages exchanged in real time. To end the chat, send `"quit"` or `"exit"`.

---

## Docker Instructions 

### 1. Build image
```bash
docker build -t moodbot-api .
```

### 2. Run container
```bash
docker run -d -p 8000:8000 --env-file .env moodbot-api
```

---

## Notes for Backend Integration

- The AI logic is in `moodbot.py` (class: `MentalHealthChatbot`)
- `ai_service.py` wraps it with a `get_bot_response(user_id, message)` function
- `main.py` exposes the endpoints
- Supports both REST and WebSocket
- Keeps user conversation context in memory (`defaultdict`)
- Daily mood check-in logic is built-in
- Detects emotional keywords and crisis language
- Sends consent-based calming technique suggestions

---

## Dependencies

- `fastapi`
- `uvicorn`
- `python-dotenv`
- `openai`

Install all with:
```bash
pip install -r requirements.txt
```
