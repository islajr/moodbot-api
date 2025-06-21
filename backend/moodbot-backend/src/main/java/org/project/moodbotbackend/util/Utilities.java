package org.project.moodbotbackend.util;

public class Utilities {

    public static String sortIdentifier(String identifier) {
        if (!identifier.isBlank()){

            if (identifier.contains("@") && identifier.endsWith(".com")) {  // should fine-tune this later.
                return "email";
            }

            return "username";
        }

        return null;
    }
}
