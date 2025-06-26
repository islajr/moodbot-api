from moodbot import MentalHealthChatbot

# Instantiate the chatbot
chatbot = MentalHealthChatbot()

def get_bot_response(user_id: str, user_input: str) -> str:
    return chatbot.get_chat_response(user_id, user_input)
