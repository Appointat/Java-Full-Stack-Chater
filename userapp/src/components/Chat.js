import React, {useEffect, useRef, useState} from "react";
import { useParams } from 'react-router-dom';

const Chat=()=>{
    const userName=JSON.parse(localStorage.getItem('user')).firstName+' '+JSON.parse(localStorage.getItem('user')).lastName;
    const userEmail=JSON.parse(localStorage.getItem('user')).email;
    const is_admin=localStorage.getItem('is_admin');
    const { roomId } = useParams();
    const chatContainerRef = useRef(null);
    const wsUrl = `ws://localhost:8080/ws/chatroom/${roomId}`;
    const [inputMessage, setInputMessage] = useState('');
    const [messages, setMessages] = useState([]);
    const ws = useRef(null);

    useEffect(()=>{
        let websocket

        try{
            websocket = new WebSocket(wsUrl);
        } catch (error) {
            console.log('Error connecting to:', wsUrl);
        }

        ws.current=websocket;

        websocket.onopen = () => {
            console.log('WebSocket connection opened');
            writeToScreen('System: connected to: ' + wsUrl, 'system_message');
        };

        websocket.onmessage = (event) => {
            console.log('Message received:', event.data);
            try {
                const messageJson = JSON.parse(event.data);
                let messageType=(messageJson.email===userEmail && messageJson.is_admin===is_admin)?'sent':'received';
                writeToScreen(messageJson.name+" : "+messageJson.message, messageType);
            } catch (error) {
                console.error('Error parsing message:', error);
            }
        };

        websocket.onerror = (event) => {
            console.error('WebSocket error:', event);
            writeToScreen('Error: Could not connect to the websocket server', 'system_message');
        };

        websocket.onclose = (event) => {
            console.log('WebSocket connection closed:', event);
            writeToScreen('System: disconnected.', 'system_message');
        };

        return () => {
            console.log('Cleaning up WebSocket connection');
            if (websocket) {
                websocket.close();
            }
        };
    }, [wsUrl, userEmail,is_admin]);

    const handleKeyPress = (e) => {
        if (e.key === 'Enter') {
            sendMessage();
        }
    };


    const sendMessage = () => {
        if (!inputMessage.trim()) {
            return;
        }
        if (!ws.current || ws.current.readyState !== WebSocket.OPEN) {
            console.error('WebSocket is not open. readyState:', ws.current ? ws.current.readyState : 'N/A');
            writeToScreen('Error: WebSocket connection is not open', 'system_message');
            return;
        }
        const messageJson = {
            name:userName,
            email: userEmail,
            is_admin:is_admin,
            message: inputMessage.trim(),
        };
        try {
            console.log('Sending message:', messageJson);
            ws.current.send(JSON.stringify(messageJson));
            setInputMessage('');
        } catch (error) {
            console.error('Error sending message:', error);
            writeToScreen('Error: Could not send message', 'system_message');
        }
    };

    const writeToScreen = (msg, type) => {
        const newMessage = { text: msg, type };
        setMessages((prevMessages) => [...prevMessages, newMessage]);
        setTimeout(() => {
            if (chatContainerRef.current) {
                chatContainerRef.current.scrollTop = chatContainerRef.current.scrollHeight;
            }
        }, 100);
    }









    return (
        <div className="chatroom-container">
            <header>Header - Chat Room Name</header>
            <main>Main - Content area where details are displayed and managed</main>
            <div className="chat-container" ref={chatContainerRef}>
                {messages.map((msg, index) => (
                    <div key={index} className={`message ${msg.type}`}>
                        {msg.text}
                    </div>
                ))}

                <div className="send-message-area">

                    <label htmlFor="messageInput">Message Input</label>
                    <input id="messageInput"
                           placeholder="Type a message..."
                           type="text"
                           value={inputMessage}
                           onChange={(e) => setInputMessage(e.target.value)}
                           // onKeyPress={handleKeyPress}
                    />
                    <button onClick={sendMessage}>Send</button>
                </div>

            </div>
        </div>
    )
}

export default Chat;