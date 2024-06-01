import axios from "axios";
import {useState} from "react";
import './styles/Signup.css'
import {Link, useNavigate} from "react-router-dom";

const Signup=()=>{
    const [firstname,setFirstname]=useState('');
    const [lastname,setLastname]=useState('');
    const [email,setEmail]=useState('');
    const [password,setPassword]=useState('');
    const [passwordconfirm,setPasswordconfirm]=useState('');
    const [is_admin, setIs_admin] = useState(false);
    const [error, setError] = useState(null);
    const navigate = useNavigate();
    const [passwordType, setPasswordType] = useState('password');
    const [buttonClass, setButtonClass] = useState('');

    const handleSignup=async(event)=>{
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

        axios.post("http://localhost:8080/app/signup",{
                firstname:firstname,
                lastname:lastname,
                email: email,
                password:password,
                is_admin:is_admin
        })
            .then(res => {
                window.location.reload();
            })
            .catch((error) => {
                if (error.response && error.response.status === 401) {
                    setError(error.response.data);
                } else {
                    setError("Signup failed. Please try again.");
                }
            });

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

    return(
        <div className="signup-container mt-3">
            <h2 className="mb-2">Sign Up</h2>
            <form id="signupForm" onSubmit={handleSignup}>
                <div className="mb-2">
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

                <div className="mb-2">
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

                <div className="mb-2">
                    <label htmlFor="email">Email:</label>
                    <input type="email"
                           className="form-control"
                           id="email"
                           name="email"
                           placeholder="email"
                           value={email}
                           onChange={e =>setEmail(e.target.value)}
                           required
                    />
                </div>

                <div className="mb-2 box">
                    <label htmlFor="password">Password:</label>
                    <input
                           type={passwordType}
                           className="form-control mb-1"
                           id="password"
                           name="password"
                           placeholder="Password"
                           value={password}
                           onChange={e => setPassword(e.target.value)}
                           required
                    />
                    <button type="button" className="btn-signup" onClick={handleGeneratePassword}>Generate Password</button>
                    <div className={`conceal ${buttonClass}`} onClick={togglePasswordVisibility}></div>
                </div>

                <div className="mb-2">
                <label htmlFor="passwordConfirm">Confirm Password:</label>
                    <input type={passwordType}
                           className="form-control"
                           id="passwordConfirm"
                           name="passwordConfirm"
                           placeholder="Confirm password"
                           value={passwordconfirm}
                           onChange={e => setPasswordconfirm(e.target.value)}
                           required
                    />
                </div>

                <div className="mb-2">
                    <label htmlFor="admin">Admin:</label>
                    <input type="checkbox"
                           id="admin"
                           name="is_admin"
                           checked={is_admin}
                           onChange={e=>setIs_admin(e.target.checked)}
                    />
                </div>

                {error && <div className="alert alert-danger" role="alert">{error}</div>}
                <button type="submit" className="btn-signup">Sign Up</button>
                <div className="mt-2">
                    <p>Return to <Link to="/">Sign In</Link></p>
                </div>
            </form>
        </div>
    )
}

export default Signup;