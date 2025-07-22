let accessToken = localStorage.getItem("accessToken");
let refreshToken = localStorage.getItem("refreshToken");
const chatForm = document.getElementById('chat-form');
// const globalURL = "https://moodbot-api.onrender.com";
const globalURL = "http://localhost:8080";

/* functions */

// refreshing access token
async function refreshAccessToken() {
    try {
        const response = await fetch(globalURL + '/api/v1/moodbot/auth/refresh', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${refreshToken}`
            }

        });
        if (response.ok) { 
            const data = await response.json();
            accessToken = data.accessToken;
            localStorage.setItem("accessToken", accessToken);
            localStorage.setItem("refreshToken", data.refreshToken);
            console.log('tokens refreshed successfully');
        } else {
            console.error('failed to refresh access token:', response.statusText);
            if (response.status === 401) {
                console.error('refresh token expired or invalid, redirecting to login...');
                window.location.href = '../html/chatpage.html'; // Redirect to login page
            }
        }
    } catch (error) {
        console.error('Error refreshing access token:', error);
        // Handle network errors or other issues
    }
    
}

// getting session ID from URL parameters
// Generates a random string like 'ABCDE-12345-XYZ12'
function generateSessionId() {
    function randomSet() {
        const chars = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789';
        let set = '';
        for (let i = 0; i < 5; i++) {
            set += chars.charAt(Math.floor(Math.random() * chars.length));
        }
        return set;
    }
    return `${randomSet()}-${randomSet()}-${randomSet()}`;
}

// Function to handle incoming messages
function onMessageReceived(payload) {

    message = JSON.parse(payload.body);
    console.log(message);
    alert(`moodbot says: ${message.content}`);

    // display the message in the chat area
    // add listener to the submit button
    //
}

/* main functionality */
chatForm.addEventListener('submit', (e) => {
    e.preventDefault();
    const promptInput = document.getElementById('prompt');
    const socket = new SockJS(globalURL +"/chat");
    stompClient = Stomp.over(socket);
    stompClient.connect({ Authorization: `Bearer ${accessToken}` }, (frame) => {
        // on connected...
        console.log('connected: ' + frame);

        // subscribe to the topic with session ID
        const sessionId = generateSessionId();
        stompClient.subscribe(`/topic/messages/${sessionId}`, onMessageReceived, {
            "Authorization": `Bearer ${accessToken}`
        });

        // send the prompt to the server
        const prompt = promptInput.value;
        if (prompt) {
            stompClient.send(`/app/chat/${sessionId}`, { Authorization: `Bearer ${accessToken}` }, JSON.stringify({
                "sessionId": sessionId,
                "userId": 1111,    // can be null since backend takes care of it
                "sender": "user",   // can be null since backend takes care of it
                "content": prompt,
                "timestamp": new Date().toISOString()
                })
            );
            promptInput.value = ''; // Clear the input field after sending
        }
    }, (error) => {
        // Handle connection error
        console.error('Connection error:', error);
        if (error.headers && error.headers['code'] === '401') {
            console.error('Access denied, refreshing token...');
            refreshAccessToken();
        }}
    );
});
