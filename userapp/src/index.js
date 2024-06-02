import React from 'react';
import ReactDOM from 'react-dom/client';
import Admin from './components/Admin'
import Normal from './components/Normal'
import Signup from './components/Signup'
import Invite from './components/Invite'
import App from './App';
import reportWebVitals from './reportWebVitals';
import {BrowserRouter, Route, Routes} from "react-router-dom";
import 'bootstrap/dist/css/bootstrap.min.css';
import './index.css';

const root = ReactDOM.createRoot(document.getElementById('root'));
root.render(
    <BrowserRouter>
        <Routes>
            <Route path="/signup" element={<Signup />} />
            <Route path="/admin" element={<Admin />} />
            <Route path="/invite" element={<Invite />} />
            <Route path="/normal" element={<Normal />} />
            <Route path="/" element={<App />} />
        </Routes>
    </BrowserRouter>

);

// If you want to start measuring performance in your app, pass a function
// to log results (for example: reportWebVitals(console.log))
// or send to an analytics endpoint. Learn more: https://bit.ly/CRA-vitals
reportWebVitals();
