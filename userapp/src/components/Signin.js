import {useState} from "react";
import {Link, useNavigate} from "react-router-dom";
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
    const [cartoonClass, setCartoonClass] = useState('');


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
                sessionStorage.setItem('is_admin',String(is_admin));
                if(User.is_new){
                    navigate('/newuser');
                }else{
                    navigate('/rooms');
                }
            })
            .catch((error)=>{
                if (error.response && error.response.status === 401) {
                    setError(error.response.data);
                } else {
                    setError("Signin failed. Please try again.");
                }
            })

    }

    const togglePasswordVisibility = () => {
        if (passwordType === 'password') {
            setPasswordType('text');
            setButtonClass('hide');
            setCartoonClass('hide')
        } else {
            setPasswordType('password');
            setButtonClass('');
            setCartoonClass('')
        }
    };


    return(
        <div className="signin-container mt-3">
            <h2>Sign In</h2>
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
                    <div className={`cartoon22 ${cartoonClass}`} id="cartoon22"></div>
                    <div className={`cartoon33 ${cartoonClass}`} id="cartoon33"></div>
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
                    <Link to="/forgot">Forgot password?</Link>
                    <p>Don't have an account? <Link to="/signup">Sign Up</Link></p>
                </div>
            </form>
        </div>
    )
}

export default Signin;