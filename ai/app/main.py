from fastapi import FastAPI, WebSocket, HTTPException, Request
from fastapi.middleware.cors import CORSMiddleware
from models import UserMessage
from ai_service import get_bot_response
import logging

app = FastAPI()

logger = logging.getLogger(__name__)

# CORS settings
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],  
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

@app.get("/")
def home():
    return {"message": "Welcome to MoodBot API"}

@app.post("/chat/")
def chat_with_bot(request: Request, message: UserMessage):
    logger.info(f"Chat request from user {message.user_id}")
    try:
        result = get_bot_response(message.user_id, message.message)
        return {
            "response": result["response"],
            "slug": result["slug"]
        }
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

@app.websocket("/ws/{user_id}")
async def websocket_chat(websocket: WebSocket, user_id: str):
    await websocket.accept()
    try:
        await websocket.send_json({
            "type": "welcome",
            "message": "MoodBot 🤖: Hi, I'm here for you. How are you feeling today?",
            "instructions": "Type 'bye', 'exit' or 'quit' to end chat"
        })

        while True:
            user_input = await websocket.receive_text()
            if user_input.lower().strip() in ['bye', 'exit', 'quit']:
                await websocket.send_json({
                    "type": "exit",
                    "message": "👋 Take care! MoodBot is always here when you need support."
                })
                break

            result = get_bot_response(user_id, user_input)

            await websocket.send_json({
                "type": "response",
                "message": f"MoodBot 🤖: {result['response']}",
                "slug": result["slug"]
            })

    except Exception as e:
        await websocket.send_json({
            "type": "error",
            "message": f"❌ Error: {str(e)}"
        })
    finally:
        await websocket.close()
