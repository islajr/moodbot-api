# MoodBot AI Service – Backend API

This is the backend API service for **MoodBot**, a mental health support chatbot that offers empathetic conversations and calming techniques using GPT-4o-mini. This service receives user input and returns:

- An AI-generated supportive response
- A short topic summary called a **slug** (≤ 5 words)

---

## Base URL

Local development default:  
```
http://localhost:8000
```

---

## Endpoints

### POST `/chat/`

Receives a user's message and returns the chatbot’s response + a short topic slug.

#### Request Body

```json
{
  "user_id": "string",
  "message": "string"
}
```

#### Response

```json
{
  "bot_response": "string",
  "slug": "string"
}
```

#### Example

**Request**
```json
{
  "user_id": "user_001",
  "message": "I'm feeling really down today"
}
```

**Response**
```json
{
  "bot_response": "I'm sorry you're feeling that way. Would you like to talk about it or try a calming exercise?",
  "slug": "feeling down"
}
```

---

## Slug Generation

A slug is a short GPT-generated summary of the user's message. It helps tag or label conversations.

- Maximum 5 words
- Relevant to the user's message
- Included in REST response

Example slugs:  
✅ `"feeling overwhelmed"`  
✅ `"coping with stress"`  
✅ `"need support"`

---

## How to Run the API

### 1. Clone the repository

```bash
git clone <your-repo-url>
cd moodbot-backend
```

### 2. Install dependencies

```bash
pip install -r requirements.txt
```

### 3. Start the server

```bash
uvicorn main:app --reload
```

### 4. Test with Swagger UI

Open in browser:
```
http://localhost:8000/docs
```

---

## File Structure

```bash
├── main.py              # FastAPI app
├── ai_service.py        # GPT logic and chatbot interface
├── moodbot.py           # MentalHealthChatbot class
├── models.py            # Pydantic schemas
└── requirements.txt     # Dependencies
```

---

## Notes

- WebSocket support has been removed.
- No authentication is required currently.
- Rate limiting has been **removed**.
- CORS is enabled for all origins.
- Slug is powered by GPT-4o-mini from OpenAI.

---