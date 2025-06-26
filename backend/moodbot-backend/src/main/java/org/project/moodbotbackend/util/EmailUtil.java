package org.project.moodbotbackend.util;

public class EmailUtil {

    public static String generateBody(String username, String reason, int code) {
        return """
                Hello %s,
                
                Please confirm your account creation with the code below:
                %s
                
                Please note that the code is only valid for thirty (30) minutes.
                
                If you didn't instigate this action, please change your password as you may be under attack.
                Also, please do not reply to this mail.
                
                Love, MoodBot.
                """.formatted(username, code);
    }
}
