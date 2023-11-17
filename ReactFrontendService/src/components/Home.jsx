import React, { Component } from 'react';
import AppNavbar from './AppNavbar';
import GrainImg from "../img/grain.png";
import "../css/navbar.css";

class Home extends Component {

    render() {
        return (
            <div>
                <AppNavbar />
                <div className="welcome-img">
                    <img src={GrainImg}
                        className="grain"
                        alt="Grain" />
                </div>
            </div>
        );
    }
}
export default Home;