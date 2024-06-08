import React, { useEffect, useState } from "react";
import {Link, useNavigate} from "react-router-dom";
import axios from "axios";
import './styles/Rooms.css'

const Rooms=()=>{
    const user = JSON.parse(sessionStorage.getItem('user'));    //get logged user information
    const is_admin=sessionStorage.getItem('is_admin')==="true";
    const navigate = useNavigate();
    const [title, setTitle] = useState("");
    const [description, setDescription] = useState("");
    const [expiredate, setExpiredate] = useState("");
    const [createdrooms, setCreatedrooms] = useState([]);
    const [invitedrooms, setInvitedrooms] = useState([]);




    useEffect(() => {
        if (user && is_admin!=null) {
            axios.get("http://localhost:8080/app/createdrooms", {   //get created room list
                params: {
                    email: user.email,
                    is_admin: is_admin
                }
            })
                .then(res => {
                    setCreatedrooms(res.data);
                })
            axios.get("http://localhost:8080/app/invitedrooms", {   //get invited rooms list
                params: {
                    email: user.email,
                    is_admin: is_admin
                }
            })
                .then(res => {
                    setInvitedrooms(res.data);
                })
        }
    }, [navigate]);


    const handleCreateRoom=async(event)=>{
        event.preventDefault();
        const createdate = new Date();  //the time of submit
        const expiredateObj = new Date(expiredate);
        if(expiredateObj<createdate){
            alert("Expire date cannot be earlier than the creation date.")
            return;
        }
        axios.post("http://localhost:8080/app/createroom",{
            email: user.email,
            is_admin:is_admin,
            title:title,
            description:description,
            createdate:createdate.toISOString(),
            expiredate:expiredate
        })
            .then(res => {
                window.location.reload();   //success, reload
            })
            .catch(err => {
                alert("Failed to create a room.");
            });
    }

    const handleDelete=async(roomId)=>{
        if(window.confirm('Are you sure to delete this room ? ')) {
            axios.delete(`http://localhost:8080/app/deleteroom/${roomId}`)
                .then(res => {
                    window.location.reload();   //success, reload
                })
                .catch(err => {
                    alert("Failed to delete the room.");
                });
        }
    }

    const handleQuit=async(roomId)=>{
        if(window.confirm('Are you sure to quit this room ? ')) {
            axios.put(`http://localhost:8080/app/quitroom`,{
                email: user.email,
                is_admin:is_admin,
                roomId:roomId
            })
                .then(res=>{
                    window.location.reload();
                })
                .catch(err=>{
                    alert("Failed to quit the room.");
                })

        }
    }

    const handleEnter = (roomId) => {
        localStorage.setItem('user', sessionStorage.getItem('user'));
        localStorage.setItem('is_admin',sessionStorage.getItem('is_admin'));
        window.open(`/chat/${roomId}`, '_blank', 'noopener,noreferrer');
    }


    return(
        <div className="admin-container" >

            <header className="header">
                <h1 className="title">Welcome to Chatter</h1>
                <a className="logout-btn" href="/">Logout</a>
                <nav className="main-nav">
                    <Link className="nav-btn" to="/edit">Edit my Account</Link>
                </nav>
            </header>

            <main className="main">
                <aside className="aside-info">
                    <span id="login_email">{user.email}</span>
                    <span id="login_email">{is_admin?'Admin user':'Normal user'}</span>
                    <form onSubmit={handleCreateRoom}>
                        <div>
                            <input type="hidden" id="create_date" name="create_date"/>
                        </div>
                        <div className="mb-3">
                            <label htmlFor="chat_room_name">Chat Room Title:</label>
                            <input type="text"
                                   id="chat_room_name"
                                   className="form-control"
                                   name="chat_room_name"
                                   value={title}
                                   onChange={e => setTitle(e.target.value)}
                                   required
                            />
                        </div>
                        <div className="mb-3">
                            <label htmlFor="description">Description:</label>
                            <input type="text"
                                   id="description"
                                   className="form-control"
                                   name="description"
                                   value={description}
                                   onChange={e => setDescription(e.target.value)}
                                   required
                            />
                        </div>

                        <div className="mb-5">
                            <label htmlFor="expire_date">Expire Date:</label>
                            <input type="datetime-local"
                                   id="expire_date"
                                   className="form-control"
                                   name="expire_date"
                                   value={expiredate}
                                   onChange={e => setExpiredate(e.target.value)}
                                   required
                            />
                        </div>
                        <div>
                            <button type="submit" className="room-btn">Create Chat Room</button>
                        </div>
                    </form>
                </aside>


                <section className="main-section">
                <h2>Created rooms</h2>
                    <table className="table" id="created-rooms">
                        <tr className="table_header">
                            <td className="room-id">ID</td>
                            <td className="title">Title</td>
                            <td className="description">Description</td>
                            <td className="created-date">Created Date</td>
                            <td className="expired-date">Expire Date</td>
                            <td>Action</td>
                        </tr>
                        {createdrooms.map(createdroom=>(
                            <tr key={createdroom.id} className="data-row">
                                <td>{createdroom.id}</td>
                                <td>{createdroom.title}</td>
                                <td>{createdroom.description}</td>
                                <td>{createdroom.createdDate}</td>
                                <td>{createdroom.expiredDate}</td>
                                <td>
                                    <a className="room-btn" onClick={() => handleEnter(createdroom.id)}>Enter</a>
                                    <a className="room-btn" onClick={() => handleDelete(createdroom.id)}>Delete</a>
                                    <Link to= "/invite" state={{ roomId: createdroom.id }} className="room-btn">Invite</Link>
                                </td>
                            </tr>
                        ))}
                    </table>

                    <h2>Invited rooms</h2>
                    <table className="table" id="invited-rooms">
                        <tr className="table_header">
                            <td className="room-id">ID</td>
                            <td className="title">Title</td>
                            <td className="description"> Description</td>
                            <td className="created-date">Created Date</td>
                            <td className="expired-ate">Expire Date</td>
                            <td>Action</td>
                        </tr>
                        {invitedrooms.map(invitedroom=>(
                            <tr key={invitedroom.id} className="data-row">
                                <td>{invitedroom.id}</td>
                                <td>{invitedroom.title}</td>
                                <td>{invitedroom.description}</td>
                                <td>{invitedroom.createdDate}</td>
                                <td>{invitedroom.expiredDate}</td>
                                <td>
                                    <a className="room-btn" onClick={() => handleEnter(invitedroom.id)}>Enter</a>
                                    <a className="room-btn" onClick={() => handleQuit(invitedroom.id)}>Quit</a>
                                </td>
                            </tr>
                        ))}
                    </table>
                </section>
            </main>

            <footer className="footer">Java Full-Stack Chatter 2024 | CHEN Zikang HOU Longhao</footer>
        </div>
    )
}

export default Rooms;