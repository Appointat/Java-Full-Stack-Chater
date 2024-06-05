import React, {useState} from 'react';
import {Link, useLocation} from 'react-router-dom';
import axios from "axios";
import './styles/Invite.css'
import { useNavigate } from 'react-router-dom';

const Invite=()=>{
    const location = useLocation();
    const {roomId}=location.state||{};
    const [email,setEmail]=useState('');
    const invitor_email = JSON.parse(sessionStorage.getItem('user')).email;
    const invitor_admin = Boolean(sessionStorage.getItem('is_admin'));
    const [is_admin, setIs_admin] = useState(false);
    const [error,setError]=useState(null);
    const navigate = useNavigate();

    const handleInvite=async(event)=>{
        event.preventDefault();

        axios.put(`http://localhost:8080/app/invite/${roomId}/${email}/${is_admin}/${invitor_email}/${invitor_admin}`)
            .then(res=>{
                alert("success")
                window.location.reload();
            })
            .catch(err=>{
                if (err.response.status === 401) {
                    setError(err.response.data);
                } else {
                    setError("Invite failed.");
                }
            })
    }

    return (
        <div className="invite-container mt-3">
            <h2>Invite a user to room {roomId}</h2>

            <form onSubmit={handleInvite}>
                <div className="mb-3">
                    <label htmlFor="email">Email : </label>
                    <input type="email"
                           id="email"
                           name="email"
                           placeholder="email"
                           value={email}
                           onChange={e => setEmail(e.target.value)}
                           required
                    />
                </div>

                <div className="mb-3">
                    <label htmlFor="admin">Admin:</label>
                    <input type="checkbox"
                           id="admin"
                           name="invite_is_admin"
                           checked={is_admin}
                           onChange={e=>setIs_admin(e.target.checked)}
                    />
                </div>
                {error && <div className="alert alert-danger" role="alert">{error}</div>}
                <button type="submit" className="invite-btn" >Invite</button>
            </form>
            <div className="mt-3">

                <Link to="/rooms">Return</Link>
            </div>
        </div>
    )

}
export default Invite;