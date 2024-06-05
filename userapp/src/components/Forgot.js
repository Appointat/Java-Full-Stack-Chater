import {Link} from "react-router-dom";
import {useState} from "react";
import axios from "axios";
import './styles/Forgot.css'

const Forgot=()=>{
    const [email,setEmail]=useState('');
    const [is_admin, setIs_admin] = useState(false);
    const [error, setError] = useState(null);

    const handleForget=async(event)=>{
        event.preventDefault();
        axios.get("http://localhost:8080/app/forgot", {
            params: {
                email: email,
                is_admin: is_admin
            }
        })
            .then(res=>{
                alert("mail sent successfully.");
                window.location.reload();
            })
            .catch(err=>{
                if (err.response.status === 401) {
                    setError(err.response.data);
                } else {
                    setError("Sending failed.");
                }
            })

    }

    return(
        <div className="forgot-container mt-3">
            <h2>Forget password</h2>


            <form onSubmit={handleForget}>
                <div className="mb-3 mt-3">
                    <label htmlFor="email">Email :</label>
                    <input type="email"
                           className="form-control"
                           id="email"
                           placeholder="Enter email"
                           name="email"
                           value={email}
                           onChange={e => setEmail(e.target.value)}
                           required
                    />
                </div>

                <div className="mb-3">
                    <label htmlFor="admin">Admin:</label>
                    <input type="checkbox"
                           id="admin"
                           name="is_admin"
                           checked={is_admin}
                           onChange={e=>setIs_admin(e.target.checked)}
                    />
                </div>
                {error && <div className="alert alert-danger" role="alert">{error}</div>}
                <button type="submit" className="forgot-btn">Send password to email</button>
                <div className="mt-3">
                    <p>Turn to <Link to="/">Sign In</Link></p>
                </div>

            </form>
        </div>

    )
}

export default Forgot;