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
import '../css/tags.css';

class KeywordList extends Component {

    constructor(props) {
        super(props);
        this.state = {
            keywords: []
        };
        this.remove = this.remove.bind(this);
    }

    componentDidMount() {
        fetch('/keywords')
            .then(response => response.json())
            .then(data => this.setState({
                keywords: data
            }));
    }

    async remove(id) {
        if (window.confirm('Do you want to remove this keyword with all keyword findings (articles will remain under a channel section)?')) {
            await fetch(`/keywords/${id}`, {
                method: 'DELETE',
                headers: {
                    'Accept': 'application/json',
                    'Content-Type': 'application/json'
                }
            }).then(() => {
                let updatedKeywords = [...this.state.keywords].filter(i => i.id !== id);
                this.setState({
                    keywords: updatedKeywords
                });
            });
        }
    };

    async active(keyword, active) {
        keyword.active = active;

        await fetch(`/keywords/active/${keyword.id}`, {
            method: 'PUT',
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(keyword),
        });

        this.props.history.push(`/keyword-settings`);

    };

    render() {
        const { keywords, isLoading } = this.state;

        if (isLoading) {
            return <p>Loading...</p>;
        }

        const keywordList = keywords.map(keyword => {
            let text = decodeURIComponent(atob(keyword.tags));
            const tagArray = text.split(",");

            return <tr key={keyword.id}>
                <td style={{ whiteSpace: 'nowrap' }}>
                    {keyword.keywordName}
                </td>
                <td>
                    {keyword.description}
                </td>
                <td>
                    {tagArray.map((tag, index) => (
                        <div className="tag-item"
                            key={index}>
                            <span className="text">
                                {tag}
                            </span>
                        </div>
                    ))}
                </td>

                <td>
                    <ButtonGroup>
                        <Button size="sm"
                            color="none"
                            tag={Link}
                            to={"/keyword-settings/" + keyword.id}>
                            <img src={EditImg}
                                className="icon25"
                                alt="Edit" />
                        </Button>
                        <Button size="sm"
                            color="none"
                            onClick={() => this.active(keyword, !keyword.active)}>
                            <img src={keyword.active === true ? SwitchOnImg : SwitchOffImg}
                                alt="Switch ON/Off" />
                        </Button>
                        <Button size="sm"
                            color="none"
                            onClick={() => this.remove(keyword.id)}>
                            <img src={DeleteImg}
                                className="icon25"
                                alt="Delete" />
                        </Button>
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
                            <a href="/#/keyword-settings/new">
                                <img src={AddImg}
                                    className="big-icon"
                                    alt="Add" />
                            </a>
                            <figcaption>
                                Add Key
                            </figcaption>
                        </figure>
                    </div>
                    <Table className="mt-4">
                        <thead>
                            <tr>
                                <th className="big-column">Keyword</th>
                                <th className="big-column">Description</th>
                                <th className="big-column">Tags</th>
                                <th className="small-column">Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            {keywordList}
                        </tbody>
                    </Table>
                </Container>
            </div>
        );
    }
}

export default KeywordList;