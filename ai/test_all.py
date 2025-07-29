# test_all.py

import os
import requests
from unittest.mock import patch
from ai_service import get_bot_response, generate_slug_sentence, fallback_slug
from moodbot import MentalHealthChatbot

print("✅ STEP 1: Testing MoodBot core logic...\n")

bot = MentalHealthChatbot()
user_id = "test_user"

# Core bot response
print("You: hi")
print("MoodBot 🤖:", bot.get_chat_response(user_id, "hi"), "\n")

print("You: I'm feeling anxious about exams")
print("MoodBot 🤖:", bot.get_chat_response(user_id, "I'm feeling anxious about exams"), "\n")

print("You: I feel happy today")
print("MoodBot 🤖:", bot.get_chat_response(user_id, "I feel happy today"), "\n")

print("You: bye")
print("MoodBot 🤖:", bot.get_chat_response(user_id, "bye"), "\n")

print("✅ STEP 2: Testing AI Service (response + slug)...\n")

response_data = get_bot_response("test_user", "I'm feeling very stressed about work")
print("AI Service Output:", response_data, "\n")

# ✅ STEP 2B: Simulate OpenAI API failing
print("✅ STEP 2B: Testing fallback slug generation (simulate OpenAI failure)...")

with patch("ai_service.client.chat.completions.create", side_effect=Exception("Simulated OpenAI outage")):
    fallback_response = get_bot_response("test_user", "I'm feeling sad and nervous")
    print("Fallback AI Service Output:", fallback_response)
    assert fallback_response["slug"] in ["feeling sad", "feeling anxious", "general feeling"], "Fallback slug failed!"

print("\n✅ STEP 3: Testing FastAPI endpoint /chat/...")

print("⚠️ Make sure you have run: uvicorn main:app --reload")

try:
    api_test = requests.post("http://127.0.0.1:8000/chat/", json={
        "user_id": "api_test_user",
        "message": "I'm feeling overwhelmed"
    })
    print("API Response:", api_test.json())
except Exception as e:
    print("❌ API endpoint test skipped (is uvicorn running?):", e)
