package org.project.moodbotbackend.service.interfaces;

import org.project.moodbotbackend.entity.EmailDetails;

public interface EmailServiceInterface {

    void sendMail(EmailDetails emailDetails);
}
