package org.project.moodbotbackend.util;

import java.util.Random;

public class EmailUtil {

    public static String generateBody(String username, String reason, int code) {

        return switch (reason) {
            case "register" -> """
                    Hello %s,
                    
                    Please confirm your account creation with the code:
                    %s
                    
                    Please note that the code is only valid for thirty (30) minutes.
                    
                    If you didn't instigate this action, please change your password as you may be under attack.
                    Also, please do not reply to this mail.
                    
                    Love, MoodBot.
                    """.formatted(username, code);
            case "passwordReset" -> """
                    Hello %s,
                    
                    Please confirm your password reset with the code:
                    %s
                    
                    Please note that the code is only valid for thirty (30) minutes.
                    
                    If you didn't instigate this action, please change your password as you may be under attack.
                    Also, please do not reply to this mail.
                    
                    Love, MoodBot.
                    """.formatted(username, code);
            case "emailReset" -> """
                    Hello %s,
                    
                    Please confirm your e-mail reset with the code below:
                    %s
                    
                    Please note that the code is only valid for thirty (30) minutes.
                    
                    If you didn't instigate this action, please change your password as you may be under attack.
                    Also, please do not reply to this mail.
                    
                    Love, MoodBot.
                    """.formatted(username, code);
            default -> "";
        };
    }

    public static Integer generateOTP() {
        Random random = new Random();
        return (int) (random.nextFloat(6) * 1000000);
    }
}
