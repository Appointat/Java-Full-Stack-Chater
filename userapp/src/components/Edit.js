import axios from "axios";
import {Link} from "react-router-dom";
import {useState, useEffect} from "react";
import './styles/Edit.css'


const Edit=()=>{
    const user = JSON.parse(sessionStorage.getItem('user'));
    const [firstname,setFirstname]=useState("");
    const [lastname,setLastname]=useState("");
    const [password,setPassword]=useState("");
    const [passwordconfirm,setPasswordconfirm]=useState('');
    const [passwordType, setPasswordType] = useState('password');
    const [buttonClass, setButtonClass] = useState('');
    const [error, setError] = useState(null);
    useEffect(() => {
        setFirstname(user.firstName);
        setLastname(user.lastName);
    }, [user.firstName, user.lastName]);

    const handleEdit=async(event)=>{
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

        axios.put("http://localhost:8080/app/edit",{
            email:user.email,
            firstname:firstname,
            lastname:lastname,
            password:password,
            is_admin:sessionStorage.getItem('is_admin')
        })
            .then(res=>{
                alert("success");
                const User=res.data;
                sessionStorage.setItem('user',JSON.stringify(User));
                window.location.reload();
            })
            .catch((error)=>{
                setError("Failed. Please try again.");
            })

    }

    //get random password by length
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
        <div className="edit-container mt-3">
            <h2>Edit account</h2>
            <form onSubmit={handleEdit}>
                <div className="mb-3 mt-5">
                    <label>Email: {user.email}</label>
                </div>

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

                <div className="mb-3 edit-box">
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
                    <button type="button" className="edit-btn mt-2" onClick={handleGeneratePassword}>Generate Password</button>
                    <div className={`conceal ${buttonClass}`} onClick={togglePasswordVisibility}></div>
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
                <button type="submit" className="edit-btn">Submit</button>

                <div className="mt-3">
                    <Link to="/rooms">Return</Link>
                </div>
            </form>
        </div>
    )
}

export default Edit;