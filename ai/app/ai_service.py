import openai
from moodbot import MentalHealthChatbot

# Instantiate the chatbot
chatbot = MentalHealthChatbot()

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
        response = openai.ChatCompletion.create(
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
        print(f"Slug generation failed: {e}")
        return "general feeling"

# Main function to get bot response + slug
def get_bot_response(user_id: str, user_input: str) -> dict:
    response = chatbot.get_chat_response(user_id, user_input)
    slug = generate_slug_sentence(user_input)

    return {
        "response": response,
        "slug": slug
    }
