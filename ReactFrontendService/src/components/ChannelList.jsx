import React, { Component } from 'react';
import { Button, ButtonGroup, Container, Table } from 'reactstrap';
import AppNavbar from './AppNavbar';
import { Link } from 'react-router-dom';
import AddImg from '../img/add.png';
import EditImg from '../img/edit.png';
import DeleteImg from '../img/delete.png';
import SwitchOnImg from '../img/switchOn.png';
import SwitchOffImg from '../img/switchOff.png';
import "../css/common.css";

class ChannelList extends Component {

    constructor(props) {
        super(props);
        this.state = {
            channels: []
        };
        this.remove = this.remove.bind(this);
    }

    componentDidMount() {
        fetch('/channels')
            .then(response => response.json())
            .then(data => this.setState({
                channels: data
            }));
    }

    async remove(id) {
        if (window.confirm('Do you want to remove channel with all articles and keyword findings (also saved articles will be removed)?')) {
            await fetch(`/channels/${id}`, {
                method: 'DELETE',
                headers: {
                    'Accept': 'application/json',
                    'Content-Type': 'application/json'
                }
            }).then(() => {
                let updatedChannels = [...this.state.channels].filter(i => i.id !== id);
                this.setState({
                    channels: updatedChannels
                });
            });
        }
    }

    async active(channel, active) {
        channel.active = active;

        await fetch(`/channels/active/${channel.id}`, {
            method: 'PUT',
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(channel),
        });

        this.props.history.push(`/channel-settings`);

    };

    render() {
        const { channels, isLoading } = this.state;

        if (isLoading) {
            return <p>Loading...</p>;
        }

        const channelList = channels.map(channel => {
            return <tr key={channel.id}>
                <td style={{ whiteSpace: 'nowrap' }}>{channel.title}</td>
                <td>{channel.description}</td>
                <td>{channel.link}</td>
                <td>
                    <ButtonGroup>
                        <Button size="sm" color="none" tag={Link} to={"/channel-settings/" + channel.id}><img src={EditImg} className="small-icon" alt="Edit" /></Button>
                        <Button size="sm" color="none" onClick={() => this.active(channel, !channel.active)}><img src={channel.active === true ? SwitchOnImg : SwitchOffImg} alt="Switch ON/Off" /></Button>
                        <Button size="sm" color="none" onClick={() => this.remove(channel.id)}><img src={DeleteImg} className="small-icon" alt="Delete" /></Button>
                    </ButtonGroup>
                </td>
            </tr>
        });

        return (
            <div>
                <AppNavbar />
                <Container fluid>
                    <div className="figure">
                        <figure>
                            <a href="/#/channel-settings/new"><img src={AddImg} className="big-icon" alt="Add" /></a>
                            <figcaption>
                                Add RSS
                            </figcaption>
                        </figure>
                    </div>
                    <Table className="mt-4">
                        <thead>
                            <tr>
                                <th className="big-column">Title</th>
                                <th className="big-column">Description</th>
                                <th className="big-column">Link</th>
                                <th className="small-column">Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            {channelList}
                        </tbody>
                    </Table>
                </Container>
            </div>
        );
    }
}
export default ChannelList;