from fastapi import FastAPI
from pydantic import BaseModel
from ai_service import get_bot_response

app = FastAPI()

class ChatRequest(BaseModel):
    user_id: str
    message: str

class ChatResponse(BaseModel):
    response: str
    slug: str

@app.post("/chat", response_model=ChatResponse)
async def chat(request: ChatRequest):
    result = get_bot_response(request.user_id, request.message)
    return ChatResponse(**result)
