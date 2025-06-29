# Authentication Documentation

This documentation explains how authentication has been implemented within this API, and also contains guidelines as regards consumption from the front-end.

## Features

- User Registration
- User Login
- User Logout
- Token Refresh
- Password Reset
- E-mail Reset
- Username Reset

## Endpoints

## Auth Endpoints

### User Registration Endpoint

This endpoint stands in front of the application and register users.

This takes in a JSON object consisting of the necessary elements: username, e-mail and password, sends a confirmation mail containaing a seven-digit OTP to the provided e-mail address, and returns a JSON object of elements, message and e-mail. This endpoint **does not** require authentication.

#### Request Example

```
/api/v1/moodbot/auth/register

    {
        "username": "someUsername",
        "email": "someEmail",
        "password": "somePassword",
    }

```

There are also some constraints and they are as follows:

- username has a min size of 1, and a max size of 20
- email has a min size of 5, and a max size of 50
- password has a min size of 8, and a max size of 75

#### Response Example

A successful registration response will return a status code of **201 - Created**, with a JSON body as follows:

```
Status Code: 201

    {
        "message": "registration successful",
        "email": "providedemail@email.com"
    }

```

However, to complete the registration process, the user still has to confirm the OTP. This can be done with the next endpoint.

### User Confirm Endpoint

This endpoint serves to confirm OTPs sent to e-mails for whatever purpose within the auth context. It receives a request with a JSON body containing elements: email and code, and returns a JSON Object of elements - accessToken and refreshToken. This endpoint does **not require** authentication.

#### Request Example

```
/api/v1/moodbot/auth/confirm

    {
        "email": "someemail@email.com",
        "code": ******
    }

```

#### Response Example

```
Status Code: 200

    {
        "accessToken": "**********",
        "refreshToken": "*********"
    }

```

### User Login Endpoint

This endpoint serves to welcome already registered users to the application. It takes in either a user's username or email, backed up with the password and returns access and refresh tokens if successful. This endpoint **does not** require authentication.

#### Request Example

```
/api/v1/moodbot/auth/login

    {
        "identifier": "username or email",
        "password": "*******"
    }

```

#### Response Example

On success, the following response is sent:

```
Status Code: 200

    {
        "accessToken": "**********",
        "refreshToken": "*********"
    }

```
If authentication has failed, the following response is sent:

```
Status Code: 400

    {
        "message": "something happened"
    }

```

If authenticaion is successful, but user is not verified, they are prompted to do so by the response:

```
Status Code: 401

    {
        "message": "please verify your e-mail first"
    }

```

### User Verify Endpoint

This endpoint follows directly form the previous one as it is responsible for re-generating and sending OTPs for the confirmation of users that have not been confirmed. This endpoint does **not require** authentication.

#### Request Example

```
/api/v1/moodbot/auth/verify

    {
        "email": "someone@email.com"
    }

```

#### Response Example

On success, the response looks like:

```
Status Code: 200

    {
        "message": "a confirmation code has been sent to your mail",
        "email": "someone@example.com"
    }

```

### Token Refresh Endpoint

This endpoint refreshes the access token, invalidates the previous one, and provides a new refresh token altogether. Authentication is **required** for this endpoint.

#### Request Example

This request has an empty body.

```
/api/v1/moodbot/auth/refresh

    {

    }

```

#### Response Example

```
Status Code: 200

    {
        "accessToken": "**********",
        "refreshToken": "*********"
    }

```

## Reset Endpoints

### Username Reset Endpoint

This endpoint lets users reset their passwords. It requires authentication

#### Request Example

```
/api/v1/moodbot/reset/username

    {
        "username: "myUsername"
    }

    *username must have a minimum of 1 character and a maximum of of 20 characters.

```

#### Response Example

Upon Success:
```
Status Code: 200
    {
        "message": "successfully updated username"
    }

```

If the username is taken:

```
Status Code: 409

    {
        "message: "username already exists"
    }

```

If any other error occurs:
```
Status Code: 404

    {
        "message": "unable to update username"
    }

```

### Password Reset Endpoint

This endpoint lets users reset their password, subject to an e-mail verification. Authentication is **required** to use this endpoint.

#### Request Example

```
/api/v1/moodbot/reset/password

    {
        "password": "newPassword"
    }

    *password must be between 8 and 75 characters.

```

#### Response Example

```
Status Code: 200

    {
        "message": "confirmation email sent"
    }

```

### Reset Verification Endpoint

This endpoint serves as an e-mail verification endpoint for all email-dependent **reset** actions. Confirmation requests should be sent to this endpoint after intiation as soon as possible. This endpoint **requires** authentication.

#### Request Example

```
/api/v1/moodbot/reset/verify?action=<email|password>

    {
        "code": *******
    }

```

#### Response Example

On Success:

```
Status Code: 200

    {
        "message": "action successfully updated"
    }

```

Incorrect code:

```
Status Code: 400

    {
        "message": "incorrect code"
    }

```

Expired code:

```
Status Code: 401

    {
        "message": "expired code. please try again."
    }

```

### Email Reset Endpoint

This endpoint lets users reset their email, subject to an e-mail verification on the new e-mail address. Authentication is **required** to use this endpoint.

#### Request Example

```
/api/v1/moodbot/reset/email

    {
        "email": "newEmail"
    }

    *email must be between 5 and 50 characters.

```

#### Response Example

```
Status Code: 200

    {
        "message": "confirmation email sent"
    }

```
As explained above, this step must be followed with **Reset Verification**.
