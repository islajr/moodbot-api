from openai import OpenAI
import os
from dotenv import load_dotenv
from collections import defaultdict
import re
from datetime import datetime
import time

# Load environment variables
load_dotenv()
client = OpenAI(api_key=os.getenv("OPENAI_API_KEY"))

class MentalHealthChatbot:
    def __init__(self):
        self.conversations = defaultdict(list)
        self.awaiting_talk_or_tech = defaultdict(lambda: None)
        self.awaiting_technique_consent = defaultdict(lambda: None)
        self.handled_emotion = defaultdict(lambda: None)
        self.daily_checkins = {}  
        self.setup_system_prompt()
        self.coping_techniques = {
            "anxiety": self.get_anxiety_techniques(),
            "depression": self.get_depression_techniques(),
            "stress": self.get_stress_techniques(),
            "sleep": self.get_sleep_techniques(),
            "panic": self.get_panic_techniques()
        }
        self.emotion_keywords = {
            "anxiety": ["anxious", "anxiety", "overwhelmed", "nervous", "worried"],
            "depression": ["depressed", "hopeless", "worthless", "sad", "empty", "numb"],
            "stress": ["stressed", "under pressure", "burned out", "tense"],
            "sleep": ["can't sleep", "insomnia", "sleep issues", "restless"],
            "panic": ["panic", "panic attack", "freaking out"]
        }
        self.empathy_phrases = {
            "anxiety":   "It sounds like a lot is weighing on you right now. I'm really sorry you're feeling anxious.",
            "stress":    "It seems you're under a great deal of pressure. I'm really sorry it's so stressful.",
            "depression":"It sounds like things have been really tough lately. I'm sorry you're feeling down.",
            "sleep":     "Not being able to rest can be exhausting. I'm sorry it's been so hard to sleep well.",
            "panic":     "Panic can feel overwhelming. I'm sorry you're experiencing this."
        }
        

    def setup_system_prompt(self):
        self.conversations["system"].append({
            "role": "system",
            "content": """You are MoodBot, a mental health support assistant. Use these guidelines:

            1. Therapeutic Approaches:
            - CBT: Help identify thought patterns
            - DBT: Validate feelings while encouraging change
            - Mindfulness: Suggest grounding techniques
            - Crisis Intervention: Immediate escalation when needed

            2. Response Style:
            - Empathetic but professional
            - 1-2 sentences + open-ended question
            - Ask and offer concrete techniques when appropriate
            - Never diagnose or suggest medications"""
        })
    
    # Coping technique libraries
    def get_anxiety_techniques(self):
        return [
            "5-4-3-2-1 Grounding: Name 5 things you see, 4 you feel, 3 you hear, 2 you smell, 1 you taste",
            "Box Breathing: Inhale 4s, hold 4s, exhale 4s, hold 4s. Repeat 4 times",
            "Progressive Muscle Relaxation: Tense and release muscles from toes to head"
        ]
    
    def get_depression_techniques(self):
        return [
            "Behavioral Activation: Do one thing you usually enjoy",
            "Gratitude Journal: Write 3 things you're grateful for",
            "Social Connection: Reach out to someone you trust for a brief conversation"
        ]
    
    def get_stress_techniques(self):
        return [
            "Time Management: Try the Pomodoro technique (25min work, 5min break)",
            "Physical Activity: Take a quick walk or stretch",
            "Mindful Breathing: Focus on slow inhales and exhales for 1 minute"
        ]
    
    def get_sleep_techniques(self):
        return [
            "Sleep Hygiene: Keep a consistent bedtime, avoid screens 1hr before sleep",
            "4-7-8 Breathing: Inhale 4s, hold 7s, exhale 8s. Repeat 4 times",
            "Body Scan: Mentally relax each body part starting from your toes"
        ]
    
    def get_panic_techniques(self):
        return [
            "Grounding Object: Focus on a nearby physical object and describe it in detail (its texture, color, etc.)",
            "Temperature Change: Hold something cold or splash cold water on your face",
            "Reassurance Mantra: Repeat 'This will pass' or 'I am safe right now'"
        ]
    
    def check_for_crisis(self, user_input):
        crisis_triggers = {
            r"\b(suicid(e|al)|end my life)\b": "high",
            r"\b(kill(ing)? myself|want to die)\b": "high",
            r"\b(self[- ]harm|cutting)\b": "medium",
            r"\b(hopeless|no point)\b": "low"
        }
        for pattern, level in crisis_triggers.items():
            if re.search(pattern, user_input, re.IGNORECASE):
                return self.get_crisis_response(level)
        return None
    
    def get_crisis_response(self, level):
        responses = {
            "high": "🚨 **IMMEDIATE HELP NEEDED**: 📞 Call 08091116264 or your local emergency line. You're not alone. 💙",
            "medium": "⚠️ Please reach out to your doctor or a trusted person. Want help finding local resources?",
            "low": "❤️ I hear you're feeling down. Would you like to talk, try a calming technique, or get info on help?"
        }
        return responses.get(level, responses["low"])
    
    def should_prompt_daily_checkin(self, user_id):
        now = datetime.now()
        last_checkin = self.daily_checkins.get(user_id)

        if not last_checkin or last_checkin.date() < now.date():
            # First interaction today
            self.daily_checkins[user_id] = now
            return True
        return False
    
    def extract_mood(self, message):
        common_moods = ["happy", "sad", "anxious", "stressed", "angry", "tired", "hopeful", "calm", "overwhelmed"]
        message_lower = message.lower()
        for mood in common_moods:
            if mood in message_lower:
                return mood
        return "unspecified"
    
    def get_chat_response(self, user_id, user_input):
        # Crisis check
        crisis_response = self.check_for_crisis(user_input)
        if crisis_response:
            self.conversations[user_id].append({"role": "assistant", "content": crisis_response})
            return crisis_response
        
        # Handle talk or tech offer response
        if self.awaiting_talk_or_tech[user_id]:
            category = self.awaiting_talk_or_tech[user_id]
            response = user_input.strip().lower()

            wants_to_talk = bool(
                re.search(r"\b(talk|chat|share|yes|yeah|yep|sure|ok|okay|please)\b", response)
            )
            wants_technique = bool(
                re.search(r"\b(technique|exercise|calm|breath|ground|relax|try it|help)\b", response)
            )

            if wants_to_talk:
                # user chose to talk
                self.awaiting_talk_or_tech[user_id] = None
                return "I'm here for you. Take your time, what’s been on your mind lately? "

            if wants_technique or response in ["no", "not really", "nah"]:
                # user chose a technique (or declined talking)
                self.awaiting_technique_consent[user_id] = category
                self.awaiting_talk_or_tech[user_id] = None
                return "Alright. Would you like to try a calming technique together?"

            # if unclear, gently prompt again
            return (
                "No pressure at all. Would you like to talk a bit more about what’s weighing on you, "
                "or would you prefer to try a short calming technique together? 💙"
            )

        # Handle pending technique consent
        if self.awaiting_technique_consent[user_id]:
            category = self.awaiting_technique_consent[user_id]
            if re.search(r"\b(yes|sure|ok|okay|please|yeah|yep|alright)\b", user_input.strip().lower()):
                technique = self.coping_techniques[category][0]
                response = f"✨ Here's something that might help:\n👉 {technique}"
            elif re.search(r"\b(no|not now|maybe later)\b", user_input.strip().lower()):
                response = "That's okay. I'm here to talk or just listen whenever you're ready. 💙"
            else:
                response = "Okay, no problem. I'm here to support however you need. 💙"
            self.conversations[user_id].append({"role": "assistant", "content": response})
            self.awaiting_technique_consent[user_id] = None
            return response

        # Save user message
        self.conversations[user_id].append({"role": "user", "content": user_input})
        
        # Daily mood check-in (only ask once per day)
        if user_id not in self.daily_checkins or self.daily_checkins[user_id].date() < datetime.now().date():
            mood = self.extract_mood(user_input)
            if mood != "unspecified":
                self.daily_checkins[user_id] = datetime.now()
                print(f"\n✅ Mood logged for {user_id} today: {mood}")
            else:
                checkin_prompt = "🌞 Hello, welcome to MoodBot! Before we chat, how are you feeling today?"
                self.conversations[user_id].append({"role": "assistant", "content": checkin_prompt})
                return checkin_prompt
        

        # Generate AI response
        try:
            # Build history with system prompt
            response = client.chat.completions.create(
                model="gpt-4o-mini",
                messages=self.conversations["system"] + self.conversations[user_id],
                temperature=0.7,
                max_tokens=200
            )
            ai_response = response.choices[0].message.content

            # Check for emotional keywords and add empathy and balanced follow-up
            category = self.detect_emotion_category(user_input)
            if (
                category
                and not self.awaiting_talk_or_tech[user_id]
                and not self.awaiting_technique_consent[user_id]
                and self.handled_emotion[user_id] != category
            ):
                # add softener
                empathy = self.empathy_phrases.get(
                    category,
                    "I'm really sorry you're feeling this way."
                )
                ai_response = f"{ai_response} {empathy}"

                # queue next step (talk or technique)
                self.awaiting_talk_or_tech[user_id] = category
                self.handled_emotion[user_id] = category  # prevent repeating for same emotion
                follow_up = (
                    f"{ai_response}\n\n"
                    "Would you like to talk a bit more about what’s weighing on you, "
                    "or would you prefer to try a short calming technique together? 💙"
                )
                self.conversations[user_id].append(
                    {"role": "assistant", "content": follow_up}
                )
                return follow_up

            # Otherwise, return normal response
            self.conversations[user_id].append({"role": "assistant", "content": ai_response})
            return ai_response

        except Exception as e:
            print(f"API Error: {e}")
            return "I'm having trouble responding right now 😔. Please try again shortly."
        
    def detect_emotion_category(self, user_input):
        input_lower = user_input.lower()
        for emotion, keywords in self.emotion_keywords.items():
            if any(keyword in input_lower for keyword in keywords):
                return emotion
        return None

    def enhance_with_techniques(self, user_input, ai_response):
        input_lower = user_input.lower()
        for emotion, keywords in self.emotion_keywords.items():
            if any(keyword in input_lower for keyword in keywords):
                technique = self.coping_techniques[emotion][0]
                return f"{ai_response}\n\n✨ You might find this helpful:\n👉 {technique}"
        return ai_response

    def get_conversation_history(self, user_id):
        return self.conversations.get(user_id, [])
    
    def clear_conversation(self, user_id):
        self.conversations[user_id] = []

