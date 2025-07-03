from fastapi import FastAPI, WebSocket
from fastapi import HTTPException
from fastapi.middleware.cors import CORSMiddleware
from models import UserMessage
from ai_service import get_bot_response
from fastapi import Request
from fastapi.middleware import Middleware
from slowapi import Limiter
from slowapi.util import get_remote_address
import logging

app = FastAPI()

limiter = Limiter(key_func=get_remote_address)
app.state.limiter = limiter

logger = logging.getLogger(__name__)

# Allow frontend or any origin for now (can be restricted)
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
@limiter.limit("5/minute")
def chat_with_bot(request: Request, message: UserMessage):
    logger.info(f"Chat request from user {message.user_id}")
    try:
        response = get_bot_response(message.user_id, message.message)
        return {"response": response}
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

            response = get_bot_response(user_id, user_input)
            await websocket.send_json({
                "type": "response",
                "message": f"MoodBot 🤖: {response}"
            })
            
    except Exception as e:
        await websocket.send_json({
            "type": "error",
            "message": f"❌ Error: {str(e)}"
        })
    finally:
        await websocket.close()