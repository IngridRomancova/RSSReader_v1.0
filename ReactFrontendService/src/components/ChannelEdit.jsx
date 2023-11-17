import React, { Component } from 'react';
import { Link, withRouter } from 'react-router-dom';
import { Button, Container, Form, FormGroup, Input, Label } from 'reactstrap';
import AppNavbar from './AppNavbar';
import '../css/common.css';

class ChannelEdit extends Component {

   emptyItem = {
      title: '',
      description: '',
      link: ''
   };

   constructor(props) {
      super(props);
      this.state = {
         item: this.emptyItem
      };
      this.handleChange = this.handleChange.bind(this);
      this.handleSubmit = this.handleSubmit.bind(this);
   }

   async componentDidMount() {
      if (this.props.match.params.id !== 'new') {
         const channel = await (await fetch(`/channels/${this.props.match.params.id}`)).json();
         this.setState({
            item: channel
         });
      }
   }

   handleChange(event) {
      const target = event.target;
      const value = target.value;
      const name = target.name;
      let item = {
         ...this.state.item
      };
      item[name] = value;
      this.setState({
         item
      });
   }

   async handleSubmit(event) {
      event.preventDefault();
      const {
         item
      } = this.state;
      item.active = true;
      item.valid = true;

      const date = new Date();
      date.setDate(date.getDate() - 14);
      item.updatedFrom = date;


      try {
         await fetch('/channels' + (item.id ? '/' + item.id : ''), {
            method: (item.id) ? 'PUT' : 'POST',
            headers: {
               'Accept': 'application/json',
               'Content-Type': 'application/json'
            },
            body: JSON.stringify(item),
         }).then(response => response.json());

         this.props.history.push('/channel-settings/');

      } catch (error) {
         alert("Invalid URl");
      }
   }

   render() {
      const { item } = this.state;
      const title = <div className="title"><h2>{item.id ? 'Edit Channel' : 'Add Channel'}</h2></div>;

      return <div>
         <AppNavbar />
         <Container>
            {title}
            <Form onSubmit={this.handleSubmit}>
               <FormGroup>
                  <Label className="label" for="title">Title</Label>
                  <Input type="text" name="title" id="title" value={item.title || ''}
                     onChange={this.handleChange} autoComplete="title" />
               </FormGroup>
               <FormGroup>
                  <Label className="label" for="description">Description</Label>
                  <Input type="text" name="description" id="description" value={item.description || ''}
                     onChange={this.handleChange} autoComplete="description" />
               </FormGroup>
               <FormGroup>
                  <Label className="label" for="link">Link</Label>
                  <Input type="text" name="link" id="link" value={item.link || ''}
                     onChange={this.handleChange} autoComplete="link" />
               </FormGroup>
               <FormGroup className="button">
                  <Button color="warning" type="submit">Save</Button>{' '}
                  <Button color="secondary" tag={Link} to="/channel-settings">Cancel</Button>
               </FormGroup>
            </Form>
         </Container>
      </div>
   }
}

export default withRouter(ChannelEdit);