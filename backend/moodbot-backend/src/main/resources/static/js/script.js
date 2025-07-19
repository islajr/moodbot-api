const loginForm = document.getElementById('login-form');
const signupForm = document.getElementById('sign-up-form');
const signUp = document.getElementById('signup-submit-button');
const emailConfirmation = document.getElementById('email-confirmation');
const globalURL = "https://moodbot-api.onrender.com";


// signup form submission handler
document.getElementById('sign-up-form').addEventListener('submit', (e) => {
    e.preventDefault(); // prevent default form submission
})
signUp.addEventListener('click', function(event) {
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

                    // collect OTP values from input fields
                    const otpInputs = document.querySelectorAll('.otp-input');
                    let otp = '';
                    otpInputs.forEach(input => {
                        otp += input.value;
                    });

                    try {
                        const verifyResponse = await fetch(globalURL + '/api/v1/moodbot/auth/confirm', {
                            method: 'POST',
                            headers: {
                                'Content-Type': 'application/json'
                            },
                            body: JSON.stringify({ 
                                "email": otpData.email, 
                                "otp": Number(otp)  // convert OTP to number
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
loginForm.addEventListener('click', function(event) {
    event.preventDefault();
    const identifier = document.getElementById('login-identifier').value;
    const password = document.getElementById('login-password').value;

    async function getLogin() {
        try {
            const loginResponse = await fetch(globalURL + '/api/v1/moodbot/auth/login', {
                method: 'POST',
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
                case 400:
                    alert("Please fill in all fields correctly.");
                    break;
                case 401:
                    alert("Please verify your email before logging in.");
                    // redirect to email confirmation
                    break;
                case 404:
                    alert("User not found. Please check your credentials.");
                    break;
                case 500:
                    alert("An error occured, but don't worry, we're on it! Please try again later.");
                    break;
                default:
                    break;
            }        
            
            // if no errors...
            const loginData = await loginResponse.json();
            if (loginData.success) {
                alert("Login successful!");
                localStorage.setItem('accessToken', loginData.accessToken);
                localStorage.setItem('refreshToken', loginData.refreshToken);
                window.location.href = '/chatpage';
            } else {
                alert(loginData.message);
            }
        } catch (error) {
            console.error('Error:', error);
            alert('An error occurred while logging in. Please try again.');
        }
    }
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

