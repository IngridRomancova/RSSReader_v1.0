import React, { Component } from 'react';
import { Navbar, NavbarBrand } from 'reactstrap';
import { Link } from 'react-router-dom';
import HomeImg from "../img/home.png";
import OfflineImg from "../img/offline.png";
import OnlineImg from "../img/online.png";
import "../css/navbar.css";
import { Offline, Online } from "react-detect-offline";

export default class AppNavbar extends Component {
  constructor(props) {
    super(props);
    this.state = { isOpen: false };
    this.toggle = this.toggle.bind(this);
  }

  toggle() {
    this.setState({
      isOpen: !this.state.isOpen
    });
  }

  render() {
    return <Navbar color="warning" primary expand="md">
      <NavbarBrand tag={Link} to="/"><img src={HomeImg} className="icon" alt="home" /></NavbarBrand>
      <NavbarBrand tag={Link} to="/channel-settings">Channel Settings</NavbarBrand>
      <NavbarBrand tag={Link} to="/keyword-settings">Keyword Settings</NavbarBrand>
      <NavbarBrand tag={Link} to="/channelviewer">Channel Viewer</NavbarBrand>
      <NavbarBrand tag={Link} to="/keywordviewer">Keyword Viewer</NavbarBrand>
      <NavbarBrand>
        <Online><img src={OnlineImg} className="icon" alt="online" /></Online>
        <Offline><img src={OfflineImg} className="icon" alt="offline" /></Offline>
      </NavbarBrand>
    </Navbar>;
  }
}