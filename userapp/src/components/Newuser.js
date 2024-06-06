import React from "react";
import {useState} from "react";
import {Link, useNavigate} from "react-router-dom";
import axios from "axios";
import './styles/Newuser.css'

const Newuser=()=>{
    const email=JSON.parse(sessionStorage.getItem('user')).email;
    const is_admin=sessionStorage.getItem('is_admin')==="true";
    const [firstname,setFirstname]=useState('');
    const [lastname,setLastname]=useState('');
    const [password,setPassword]=useState('');
    const [passwordconfirm,setPasswordconfirm]=useState('');
    const [error, setError] = useState(null);
    const [passwordType, setPasswordType] = useState('password');
    const [buttonClass, setButtonClass] = useState('');
    const navigate = useNavigate();


    const handleNewuser=async(event)=>{
        event.preventDefault();
        const passwordPattern = /(?=.*\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[\W]).{8,}/;

        if (!passwordPattern.test(password)) {
            alert("Password must contain least 8 characters and include at least one digit, one uppercase letter, one lowercase letter, and one special character.");
            return;
        }

        if (password !== passwordconfirm) {
            setError("Passwords do not match.");
            return;
        }

        axios.put("http://localhost:8080/app/newuser",{
            firstname:firstname,
            lastname:lastname,
            email:email,
            password:password,
            is_admin:is_admin
        })
            .then(res=>{
                const User=res.data;
                sessionStorage.setItem('user',JSON.stringify(User));
                sessionStorage.setItem('is_admin',String(is_admin));
                navigate('/rooms');
            })
            .catch((error)=>{
                setError("Failed. Please try again.");
            })


    }

    const randomPassword = (length) => {
        const chars = "abcdefghijklmnopqrstuvwxyz!@#$%^&*()-+<>ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        let pass = "";
        pass += getRandomChar("abcdefghijklmnopqrstuvwxyz");
        pass += getRandomChar("!@#$%^&*()-+<>");
        pass += getRandomChar("ABCDEFGHIJKLMNOPQRSTUVWXYZ");
        pass += getRandomChar("1234567890");
        for (let x = 4; x < length; x++) {
            pass += getRandomChar(chars);
        }
        return pass;
    };

    const getRandomChar = (charSet) => {
        return charSet.charAt(Math.floor(Math.random() * charSet.length));
    };

    const handleGeneratePassword = () => {
        const lenPassword = 8;
        const psw = randomPassword(lenPassword);
        setPassword(psw);
        setPasswordconfirm(psw);
    };

    const togglePasswordVisibility = () => {
        if (passwordType === 'password') {
            setPasswordType('text');
            setButtonClass('hide');
        } else {
            setPasswordType('password');
            setButtonClass('');
        }
    };


    return (
        <div className="newuser-container">
            <h2 className="mb-3">Change your information</h2>

            <form onSubmit={handleNewuser}>
                <div className="info mb-2">Email: {email}</div>
                <div className="info mb-2">{is_admin ? 'Admin user' : 'Normal user'}</div>
                <div className="mb-3">
                    <label htmlFor="firstName">First Name:</label>
                    <input type="text"
                           className="form-control"
                           id="firstName"
                           name="first_name"
                           pattern="[A-Za-zÀ-ÖØ-öø-ÿ\s]+"
                           placeholder="First name"
                           value={firstname}
                           onChange={e => setFirstname(e.target.value)}
                           required
                    />
                </div>

                <div className="mb-3">
                    <label htmlFor="lastName">Last Name:</label>
                    <input type="text"
                           className="form-control"
                           id="lastName"
                           name="last_name"
                           pattern="[A-Za-zÀ-ÖØ-öø-ÿ\s]+"
                           placeholder="Last name"
                           value={lastname}
                           onChange={e => setLastname(e.target.value)}
                           required
                    />
                </div>
                <div className="box">
                    <div className="mb-3">
                        <label htmlFor="password">Password:</label>
                        <input type={passwordType}
                               className="form-control"
                               id="password"
                               name="password"
                               placeholder="Password"
                               value={password}
                               onChange={e => setPassword(e.target.value)}
                               required
                        />
                        <button type="button" className="mt-2 newuser-btn" onClick={handleGeneratePassword}>Generate
                            Password
                        </button>
                        <div className={`conceal ${buttonClass}`} onClick={togglePasswordVisibility}></div>
                    </div>
                </div>

                <div className="mb-3">
                    <label htmlFor="passwordConfirm">Confirm Password:</label>
                    <input type="password"
                           className="form-control"
                           id="passwordConfirm"
                           name="passwordConfirm"
                           placeholder="Confirm password"
                           value={passwordconfirm}
                           onChange={e => setPasswordconfirm(e.target.value)}
                           required
                    />
                </div>

                {error && <div className="alert alert-danger" role="alert">{error}</div>}
                <button type="submit" className="newuser-btn">Submit and Sign In</button>

                <div className="mt-3">
                    <p>Return to <Link to="/">Sign In</Link></p>
                </div>
            </form>
        </div>
    )

}

export default Newuser;