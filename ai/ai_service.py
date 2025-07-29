from openai import OpenAI
import os
from moodbot import MentalHealthChatbot
import re

# Create OpenAI client
client = OpenAI(api_key=os.getenv("OPENAI_API_KEY"))

# Instantiate the chatbot
chatbot = MentalHealthChatbot()

# Fallback slug generator
def fallback_slug(message: str) -> str:
    """
    Simple fallback slug generator when OpenAI API is unavailable.
    Extracts main feeling/emotion from the user's message.
    """
    message_lower = message.lower()

    if any(word in message_lower for word in ["sad", "down", "unhappy", "depressed"]):
        return "feeling sad"
    elif any(word in message_lower for word in ["anxious", "worried", "nervous", "tense"]):
        return "feeling anxious"
    elif any(word in message_lower for word in ["happy", "excited", "joyful", "glad"]):
        return "feeling happy"
    elif any(word in message_lower for word in ["angry", "mad", "frustrated", "upset"]):
        return "feeling angry"
    elif any(word in message_lower for word in ["stressed", "pressure", "overwhelmed"]):
        return "feeling stressed"
    else:
        return "general feeling"


# Slug generation function
def generate_slug_sentence(message: str) -> str:
    system_prompt = (
        "You are an assistant that summarizes a user's message into a short topic sentence "
        "of no more than 5 words. The sentence should reflect the user's emotional state or intent.\n\n"
        "Examples:\n"
        "'I feel anxious about exams.' → 'feeling anxious about exams'\n"
        "'Can I talk to someone?' → 'wants to talk'\n"
        "'I'm really happy today!' → 'feeling very happy today'\n"
        "'I'm stressed and can’t focus.' → 'feeling stressed and unfocused'\n"
        "'I’m okay.' → 'feeling okay'\n"
        "\nReturn only the short sentence. No punctuation."
    )

    try:
        response = client.chat.completions.create(
            model="gpt-4o-mini",
            messages=[
                {"role": "system", "content": system_prompt},
                {"role": "user", "content": message}
            ],
            max_tokens=20,
            temperature=0.7
        )
        slug = response.choices[0].message.content.strip().lower()
        return slug

    except Exception as e:
        print(f"Slug generation failed (OpenAI error): {e}")
        print("Using fallback slug generator instead...\n")
        return fallback_slug(message)


# Main function to get bot response with slug
def get_bot_response(user_id: str, user_input: str) -> dict:
    response = chatbot.get_chat_response(user_id, user_input)
    slug = generate_slug_sentence(user_input)

    return {
        "response": response,
        "slug": slug
    }
