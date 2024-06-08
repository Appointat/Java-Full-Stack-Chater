import React, {useEffect, useRef, useState} from "react";
import {Link, useParams} from 'react-router-dom';
import axios from "axios";
import './styles/Chat.css'

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
    const [chatRoom, setChatRoom] = useState(null);
    const [title, setTitle] = useState("");
    const [description, setDescription] = useState("");
    const [adminMembers, setAdminMembers] = useState([]);
    const [normalMembers, setNormalMembers] = useState([]);
    const [creator, setCreator] = useState("");
    const [creatorAdmin, setCreatorAdmin] = useState("");
    const disconnectSent = useRef(false);  // 标志位


    useEffect(() => {
        axios.get(`http://localhost:8080/app/chatroom/${roomId}`)
            .then((response) => {
                setChatRoom(response.data);
                setTitle(response.data.title);
                setDescription(response.data.description);
                setAdminMembers(response.data.adminUsers);
                setNormalMembers(response.data.normalUsers);
                setCreator(response.data.admin_creator?response.data.admin_creator:response.data.normal_creator);
                setCreatorAdmin(response.data.admin_creator?"admin":"normal");
            })
            .catch((error) => {
                console.log(error);
            })
    }, []);


    useEffect(()=>{
        ws.current = new WebSocket(wsUrl);

        ws.current.onopen = () => {
            const messageJson = {
                name:userName,
                email: userEmail,
                is_admin:is_admin,
                message: userName+" connected",
                system:true
            };
            try {
                console.log('Sending message:', messageJson);
                ws.current.send(JSON.stringify(messageJson));
                setInputMessage('');
            } catch (error) {
                console.error('Error sending message:', error);
                writeToScreen('Error: Could not send message', 'system_message');
            }
            // writeToScreen('System: connected to: ' + wsUrl, 'system_message');
        };

        ws.current.onmessage = (event) => {
            try {
                const messageJson = JSON.parse(event.data);
                if (String(messageJson.system)==="true") {
                    writeToScreen(messageJson.message, 'system_message');
                }else {
                    let messageType = (messageJson.email === userEmail && String(messageJson.is_admin) === is_admin) ? "sent" : "received";
                    writeToScreen(messageJson.name + " : " + messageJson.message, messageType);
                }
            } catch (error) {
                console.error('Error parsing message:', error);
            }
        };

        ws.current.onerror = (event) => {
            writeToScreen('Error: Could not connect to the websocket server', 'system_message');
        };

        ws.current.onclose = (event) => {
            writeToScreen('System: disconnected.', 'system_message');
        };

        return () => {
            ws.current.close();
        };
    }, [wsUrl, userEmail,is_admin]);



    const handleBeforeUnload = () => {
        if (!disconnectSent.current) {
            const messageJson = {
                name:userName,
                email: userEmail,
                is_admin:is_admin,
                message: userName+" disconnected",
                system:true
            };
            try {
                console.log('Sending message:', messageJson);
                ws.current.send(JSON.stringify(messageJson));
            } catch (error) {
                console.error('Error sending message:', error);
                writeToScreen('Error: Could not send message', 'system_message');
            }
            disconnectSent.current = true;
        }
    };
    window.addEventListener('beforeunload', handleBeforeUnload);

    const handleKeyPress = (e) => {
        if (e.key === 'Enter') {
            sendMessage();
        }
    };


    const sendMessage = () => {
        if (!inputMessage.trim()) {
            return;
        }
        const messageJson = {
            name:userName,
            email: userEmail,
            is_admin:is_admin,
            message: inputMessage.trim(),
            system:false
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
        // chatContainerRef.current.scrollTop = chatContainerRef.current.scrollHeight;
    }

    useEffect(() => {
        if (chatContainerRef.current) {
            chatContainerRef.current.scrollTop = chatContainerRef.current.scrollHeight;
        }
    }, [messages]);

    const handleClose = () => {
        window.open('', '_self', ''); // This is required for some browsers
        window.close();
    }


    return (
        <div className="chatroom-container">
            <header className="chatroom-header">

                <h1 className="chatroom-title">{title}</h1>
                <h2 className="chatroom-description">{description}</h2>
                <a className="close-btn" onClick={handleClose}>Close</a>
            </header>


            <main className="chat-main">
                <aside className="chat-info">
                    <div id="login_email">Creator</div>
                    <table className="creator-table chat-table">
                        <tr className="table_header">
                            <td className="room-id">ID</td>
                            <td className="title">Name</td>
                            <td className="description">Type</td>
                        </tr>
                        <tbody>
                        <tr>
                            <td>{creator.id}</td>
                            <td>{creator.firstName} {creator.lastName}</td>
                            <td>{creatorAdmin}</td>
                        </tr>
                        </tbody>
                    </table>
                    <div id="login_email">Members</div>
                    <table className="member-table chat-table">
                        <tr className="table_header">
                            <td className="room-id">ID</td>
                            <td className="title">Name</td>
                            <td className="description">Type</td>
                        </tr>
                        {adminMembers.map(admin => (
                            <tr key={admin.id} className="data-row">
                                <td>{admin.id}</td>
                                <td>{admin.firstName} {admin.lastName}</td>
                                <td>admin</td>
                            </tr>
                        ))}
                        {normalMembers.map(normal => (
                            <tr key={normal.id} className="data-row">
                                <td>{normal.id}</td>
                                <td>{normal.firstName} {normal.lastName}</td>
                                <td>normal</td>
                            </tr>
                        ))}


                    </table>
                </aside>
                <div className="chat-container" >
                    <div className="messages" ref={chatContainerRef}>
                        {messages.map((msg, index) => (
                            <div key={index} className={`message ${msg.type}`}>
                            {msg.text}
                            </div>
                        ))}
                    </div>
                    <div className="send-message-area">
                        <input id="messageInput"
                               className="message-input"
                               placeholder="Type a message..."
                               type="text"
                               value={inputMessage}
                               onChange={(e) => setInputMessage(e.target.value)}
                               onKeyDown={(e) => {
                                   if (e.key === 'Enter') {
                                       sendMessage();
                                   }
                               }}
                        />
                        <button className="send-btn" onClick={sendMessage}>Send</button>
                    </div>
                </div>
            </main>
        </div>
    )
}

export default Chat;