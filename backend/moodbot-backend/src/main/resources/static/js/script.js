const loginForm = document.getElementById('login-form');
const signupForm = document.getElementById('sign-up-form');
const signupContainer = document.getElementById('sign-up-container');
const loginContainer = document.getElementById('login-container');
const signUp = document.getElementById('signup-submit-button');
const emailConfirmation = document.getElementById('email-confirmation');
// const globalURL = "https://moodbot-api.onrender.com";
const globalURL = "http://localhost:8080";

/* functions */

// collect OTP values from input fields

function getOTP() {
    const otpInputs = document.querySelectorAll('.otp-input');
    let otp = '';
    otpInputs.forEach(input => {
    otp += input.value;
    });

    return otp;
}

/* main code */

// signup form submission handler
document.getElementById('sign-up-form').addEventListener('submit', (e) => {
    e.preventDefault(); // prevent default form submission
})
signUp.addEventListener('submit', function(event) {
    event.preventDefault();
    const username = document.getElementById('signup-username').value;
    const email = document.getElementById('signup-e-mail').value;
    const password = document.getElementById('signup-password').value;

    async function getOTP() {
        try {
            const otpResponse = await fetch(globalURL + '/api/v1/moodbot/auth/register', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({ 
                    "username": username,
                    "email": email,
                    "password": password
                })
            });

            if (!otpResponse.ok) {
                throw new Error('Network response was not ok');
            }
            if (otpResponse.status === 201) {
                const otpData = await otpResponse.json();
                alert(`OTP sent to ${otpData.email}. Please check your email.`);

                // hide signup and login forms; show email confirmation
                signupForm.style.display = 'none';
                loginForm.style.display = 'none';
                emailConfirmation.style.display = 'flex';
                
                // add event listener for OTP submission
                const otpSubmit = document.getElementById('confirm-otp');
                otpSubmit.addEventListener('click', async function(otpEvent) {
                    otpEvent.preventDefault();

                    let otp = getOTP(); // get OTP from input fields

                    try {
                        const verifyResponse = await fetch(globalURL + '/api/v1/moodbot/auth/confirm', {
                            method: 'POST',
                            headers: {
                                'Content-Type': 'application/json'
                            },
                            body: JSON.stringify({ 
                                "email": otpData.email, 
                                "code": Number(otp)  // convert OTP to number
                            })
                        });

                        // dealing with possible errors
                        if (verifyResponse.status === 400) {
                            if (verifyResponse.json().message === "incorrect code") {
                                alert("Incorrect OTP. Please try again.");
                                return;
                            } else {
                                alert("An error occurred. Please try again later.");
                                return;
                            }
                        }

                        // if no errors...
                        const verifyData = await verifyResponse.json();
                        if (verifyData.success) {
                            localStorage.setItem('accessToken', verifyData.accessToken);
                            localStorage.setItem('refreshToken', verifyData.refreshToken);
                            alert("OTP verified successfully!");
                            // redirect to chat page
                            window.location.href = '/chatpage';
                        } else {
                            alert(verifyData.message);
                        }

                    } catch (error) {
                        console.error('Error:', error);
                        alert('An error occurred while verifying the OTP. Please try again.');
                    }
                })
                
                
            }
        } catch (error) {
            console.error('Error:', error);
            alert('An error occurred while sending the OTP. Please try again.');
        }
    }
    // call the function to get OTP
    getOTP();
    
});

// login form submission handler
document.getElementById('login-form').addEventListener('submit', (e) => {
    e.preventDefault(); // prevent default form submission
});
loginForm.addEventListener('submit', function(event) {
    event.preventDefault();
    const identifier = document.getElementById('login-identifier').value;
    const password = document.getElementById('login-password').value;

    async function getLogin() {
        try {
            const loginResponse = await fetch(globalURL + '/api/v1/moodbot/auth/login', {
                method: 'POST',
                mode: 'no-cors',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({ 
                    "identifier": identifier, 
                    "password": password 
                })
            });

            // dealing with possible errors
            switch (loginResponse.status) {
                case Number (400):
                    alert("Please fill in all fields correctly.");
                    break;
                case Number (401):
                    // redirect to email confirmation
                    async function redirectToEmailConfirmation() {
                        try {
                            const triggerVerification = await fetch(globalURL + '/api/v1/moodbot/auth/verify', {
                                method: 'POST',
                                headers: {
                                    'Content-Type': 'application/json'
                                },
                                body: JSON.stringify({ 
                                    "email": identifier
                                })
                            })

                            switch (triggerVerification.status) {
                                case Number(201):
                                    alert("Please check your email for the OTP to verify your account.");
                                    signupContainer.style.display = 'none';
                                    loginContainer.style.style.display = 'none';
                                    emailConfirmation.hidden = false;
                                    const otpSubmit = document.getElementById('confirm-otp');
                                    otpSubmit.addEventListener('submit', async function(otpEvent) {
                                        otpEvent.preventDefault();
                                        let otp = getOTP(); // get OTP from input fields
                                        try {
                                            const verifyResponse = await fetch(globalURL + '/api/v1/moodbot/auth/confirm', {
                                                method: 'POST',
                                                headers: {
                                                    'Content-Type': 'application/json'
                                                },
                                                body: JSON.stringify({ 
                                                    "email": identifier, 
                                                    "code": Number(otp)  // convert OTP to number
                                                })
                                            });
                                            
                                            // dealing with possible errors
                                            if (verifyResponse.status === 400) {
                                                if (verifyResponse.json().message === "incorrect code") {
                                                    alert("Incorrect OTP. Please try again.");
                                                    return;
                                                } else {
                                                    alert("An error occurred. Please try again later.");
                                                    return;
                                                }
                                            }
                                        
                                            // if no errors...
                                            const verifyData = await verifyResponse.json();
                                            localStorage.setItem('accessToken', verifyData.accessToken);
                                            localStorage.setItem('refreshToken', verifyData.refreshToken);
                                            alert("OTP verified successfully!");
                                            // redirect to chat page
                                            window.location.href = '/chatpage';
                                        } catch (error) {
                                            console.error('Error:', error);
                                            alert('An error occurred while verifying the OTP. Please try again.');
                                        }
                                    })

                                    break;
                                case Number(404):
                                    alert("User not found. Please check your credentials.");
                                    break
                                case Number(500):
                                    alert("There was an error, but don't worry, it's not yout fault. Please try again later.");
                                    break;
                                default:
                                    alert("An error occurred while verifying your email. Please try again later.");
                                    break;
                                    
                        }
                    }
                        catch (error) {
                            console.error('Error:', error);
                            alert('An error occurred while sending the verification email. Please try again.');
                        }
                    }
                    redirectToEmailConfirmation();

                    break;
                case Number(404):
                    alert("User not found. Please check your credentials.");
                    break;
                case Number(500):
                    alert("An error occured, but don't worry, we're on it! Please try again later.");
                    break;
                default:
                    // if no errors...
                    const loginData = await loginResponse.json();
                    alert("Login successful!");
                    localStorage.setItem('accessToken', loginData.accessToken);
                    localStorage.setItem('refreshToken', loginData.refreshToken);
                    window.location.href = '../html/chatpage.html';
                    break;
            }        
            
            /* // if no errors...
            const loginData = await loginResponse.json();
            if (loginData.success) {
                alert("Login successful!");
                localStorage.setItem('accessToken', loginData.accessToken);
                localStorage.setItem('refreshToken', loginData.refreshToken);
                window.location.href = '/chatpage';
            } else {
                alert(loginData.message);
            } */
        } catch (error) {
            console.error('Error:', error);
            alert('An error occurred while logging in. Please try again.');
        }
    }
    getLogin();
});

/* accessibility logic */
const otpContainer = document.getElementById('otp-container');
const otpInput = document.querySelectorAll('.otp-input');

otpInput.forEach((input, index) => {
    input.addEventListener('input', (e) => {
        if (e.target.value >=0 && e.target.value <= 9) {

            if (input.value.length === 1 && index < otpInput.length - 1) {
                otpInput[index + 1].focus();
            }
        } else {
            input.value = '';
        }
    })

    input.addEventListener('keydown', (e) => {
            if (e.key === 'Backspace' && index > 0 && input.value === '') {
                otpInput[index - 1].focus();
            }
        });
})

