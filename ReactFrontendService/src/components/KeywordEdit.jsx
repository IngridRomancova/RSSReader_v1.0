import React, { Component } from 'react';
import { Link, withRouter } from 'react-router-dom';
import { Button, Container, Form, FormGroup, Input, Label } from 'reactstrap';
import AppNavbar from './AppNavbar';
import '../css/common.css';
import '../css/tags.css';

class KeywordEdit extends Component {

   emptyItem = {
      keywordName: '',
      description: '',
      tags: []
   };

   constructor(props) {
      super(props);
      this.state = {
         item: this.emptyItem,
         tags: [],
         previousTags: []
      };
      this.handleChange = this.handleChange.bind(this);
      this.handleSubmit = this.handleSubmit.bind(this);

   }

   async componentDidMount() {
      if (this.props.match.params.id !== 'new') {
         const keyword = await (await fetch(`/keywords/${this.props.match.params.id}`)).json();
         this.setState({
            item: keyword
         });
         this.setState({
            previousTags: keyword.tags
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

      const date = new Date();
      date.setDate(date.getDate() - 14);
      item.updatedFrom = date;

      await fetch('/keywords' + (item.id ? '/' + item.id : ''), {
         method: (item.id) ? 'PUT' : 'POST',
         headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json'
         },
         body: JSON.stringify(item),
      });
      this.props.history.push('/keyword-settings');
   }

   handleKeyDown(e) {

      const value = e.target.value.trim();

      if (e.key === ',' && value.length && !this.state.tags.includes(value)) {

         this.setState({
            tags: [...this.state.tags, value]
         });
         e.target.value = ''
      }

   }

   inputKeyDown = (e) => {
      const val = e.target.value;
      if (e.key === ',' && val) {
         if (this.state.tags.find(tag => tag.toLowerCase() === val.toLowerCase())) {
            return;
         }
         e.preventDefault();
         this.setState({
            tags: [...this.state.tags, val]
         });
         e.target.value = null;
      } else if (e.key === 'Backspace' && !val) {
         this.removeTag(this.state.tags.length - 1);
      } else if (e.key === 'ArrowDown') {
         const prevTagsArray = decodeURIComponent(atob(this.state.previousTags)).split(',');
         this.setState({
            tags: prevTagsArray
         });
      }
   }

   removeTag = (i) => {
      const newTags = [...this.state.tags];
      newTags.splice(i, 1);
      this.setState({
         tags: newTags
      });
   }

   render() {
      const { item } = this.state;
      const title = <div className="title">
         <h2>{item.id ? 'Edit Keyword' : 'Add Keyword'}</h2>
      </div>;

      item.tags = btoa(encodeURIComponent(this.state.tags));

      return <div>
         <AppNavbar />
         <Container>
            {title}
            <Form onSubmit={this.handleSubmit}>
               <FormGroup>
                  <Label className="label"
                     for="keywordName">
                     Keyword
                  </Label>
                  <Input type="text"
                     name="keywordName"
                     id="keywordName"
                     value={item.keywordName || ''}
                     onChange={this.handleChange}
                     autoComplete="keywordName" />
               </FormGroup>
               <FormGroup>
                  <Label className="label"
                     for="description">
                     Description
                  </Label>
                  <Input type="text"
                     name="description"
                     id="description"
                     value={item.description || ''}
                     onChange={this.handleChange}
                     autoComplete="description" />
               </FormGroup>
               <FormGroup>
                  {item.id
                     ? <Label className="label"
                        for="tags">
                        Tags - separated by comma, for adding previous tags press ↓
                     </Label>
                     : <Label className="label"
                        for="tags">
                        Tags - separated by comma
                     </Label>
                  }

                  <div className="tags-input-container">
                     {this.state.tags.map((tag, index) => (
                        <div className="tag-item"
                           key={index}>
                           <span className="text">
                              {tag}
                           </span>
                           <span className="close"
                              onClick={() => this.removeTag(index)}>
                              &times;
                           </span>
                        </div>
                     ))}

                     <input type="text"
                        onKeyDown={this.inputKeyDown}
                        className="tags-input"
                        placeholder="Type tags separated by comma"
                        name="tags"
                        id="tags"
                        onChange={this.handleChange} autoComplete="tags" />

                  </div>

               </FormGroup>
               <FormGroup>
                  <Button color="warning"
                     type="submit">
                     Save
                  </Button>
                  {' '}
                  <Button color="secondary"
                     tag={Link}
                     to="/keyword-settings">
                     Cancel
                  </Button>
               </FormGroup>
            </Form>
         </Container>
      </div>
   }
}

export default withRouter(KeywordEdit);