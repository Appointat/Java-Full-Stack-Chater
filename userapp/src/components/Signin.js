import {useEffect, useState} from "react";
import {useNavigate} from "react-router-dom";
import './styles/Signin.css'
import axios from "axios";

const Signin=()=>{
    const [email,setEmail]=useState('');
    const [password,setPassword]=useState('');
    const [is_admin, setIs_admin] = useState(false);
    const [error, setError] = useState(null);
    const navigate = useNavigate();
    const [passwordType, setPasswordType] = useState('password');
    const [buttonClass, setButtonClass] = useState('');


    const handleSignin=async(event)=> {
        event.preventDefault();
        axios.get("http://localhost:8080/app/signin",{
            params:{
                email: email,
                password: password,
                is_admin: is_admin
            }
        })
            .then(res=> {
                const User=res.data;
                sessionStorage.setItem('user',JSON.stringify(User));
                if(is_admin){
                    navigate('/admin');
                }else{
                    navigate('/normal');
                }
            })
            .catch((error)=>{
                if (error.response && error.response.status === 401) {
                    setError(error.response.data);
                } else {
                    setError("Sign in failed. Please try again.");
                }
            })

    }

    const togglePasswordVisibility = () => {
        if (passwordType === 'password') {
            setPasswordType('text');
            setButtonClass('hide');
        } else {
            setPasswordType('password');
            setButtonClass('');
        }
    };


    return(
        <div className="signin-container mt-3">
            <h2>Sign In</h2>
            <div className="cartoon22" id="cartoon22"></div>
            <div className="cartoon33" id="cartoon33"></div>

            <form onSubmit={handleSignin}>
                <div className="mb-3 mt-3">
                    <label htmlFor="email">Email:</label>
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
                <div className="mb-3 box">
                    <label htmlFor="password">Password:</label>
                    <input className="form-control"
                           id="password"
                           name="password"
                           pattern=".{8,}"
                           placeholder="Enter password"
                           type={passwordType}
                           value={password}
                           onChange={e => setPassword(e.target.value)}
                           required
                    ></input>
                    <div className={`conceal ${buttonClass}`} onClick={togglePasswordVisibility}></div>
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
                <button type="submit" className="btn btn-primary">Sign In</button>
                <div className="mt-3">
                    <a href="/forget">forgot password?</a>
                    <p>Don't have an account? <a href="/signup">Sign Up</a></p>
                </div>
            </form>
        </div>
    )
}

export default Signin;