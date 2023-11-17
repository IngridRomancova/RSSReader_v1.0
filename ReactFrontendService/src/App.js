import React, { Component } from 'react';

import Home from './components/Home';
import { HashRouter as Router, Route, Switch } from 'react-router-dom';
import ChannelList from './components/ChannelList';
import ChannelEdit from "./components/ChannelEdit";
import KeywordList from './components/KeywordList';
import KeywordEdit from "./components/KeywordEdit";
import KeywordViewer from "./components/KeywordViewer";
import ChannelViewer from "./components/ChannelViewer";

class App extends Component {
    render() {
        return (
            <Router>
                <Switch>
                    <Route path='/' exact={true} component={Home} />
                    <Route path='/channel-settings' exact={true} component={ChannelList} />
                    <Route path='/keyword-settings' exact={true} component={KeywordList} />
                    <Route path='/channel-settings/:id' component={ChannelEdit} />
                    <Route path='/keyword-settings/:id' component={KeywordEdit} />
                    <Route path='/keywordviewer' exact={true} component={KeywordViewer} />
                    <Route path='/channelviewer' exact={true} component={ChannelViewer} />
                </Switch>
            </Router>
        )
    }
}

export default App;
