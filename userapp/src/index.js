import React from 'react';
import ReactDOM from 'react-dom/client';
import Rooms from './components/Rooms'
import Forgot from './components/Forgot';
import Signup from './components/Signup'
import Invite from './components/Invite'
import Newuser from './components/Newuser'
import Edit from './components/Edit'
import Chat from './components/Chat'
import App from './App';
import reportWebVitals from './reportWebVitals';
import {BrowserRouter, Route, Routes} from "react-router-dom";
import 'bootstrap/dist/css/bootstrap.min.css';
import './index.css';
import ReportWebVitals from "./reportWebVitals";

const root = ReactDOM.createRoot(document.getElementById('root'));
root.render(
    <BrowserRouter>
        <Routes>
            <Route path="/signup" element={<Signup />} />
            <Route path="/rooms" element={<Rooms />} />
            <Route path="/invite" element={<Invite />} />
            <Route path="/forgot" element={<Forgot />} />
            <Route path="/newuser" element={<Newuser />} />
            <Route path="/chat/:roomId" element={<Chat />} />
            <Route path="/edit" element={<Edit />} />
            <Route path="/" element={<App />} />
        </Routes>
    </BrowserRouter>

);

// If you want to start measuring performance in your app, pass a function
// to log results (for example: reportWebVitals(console.log))
// or send to an analytics endpoint. Learn more: https://bit.ly/CRA-vitals
reportWebVitals();
