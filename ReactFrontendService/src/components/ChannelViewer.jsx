import React, { Component } from 'react';
import { Button, ButtonGroup, Container, Table } from 'reactstrap';
import AppNavbar from './AppNavbar';
import RefreshImg from "../img/refresh.png";
import WebImg from "../img/www_big.png";
import UpdateImg from '../img/update.png';
import RemoveImg from '../img/remove.png';
import RemoveAllImg from '../img/removeAll.png';
import BeforeFavouriteImg from '../img/beforeFavourite.png';
import AfterFavouriteImg from '../img/afterFavourite.png';
import BeforeSaveImg from '../img/beforeSave.png';
import AfterSaveImg from '../img/afterSave.png';
import DeleteImg from '../img/delete.png';
import FavouriteImg from '../img/favourite.png';
import SavedImg from '../img/saved.png';
import ShowAllImg from '../img/showAll.png';
import RefreshOffImg from '../img/refreshOff.png';
import '../css/viewer.css';
import "../css/common.css";
import "../css/pagination.css";
import ReactPaginate from 'react-paginate';
import dayjs from 'dayjs';
import RiseLoader from "react-spinners/RiseLoader";
import Paper from '@mui/material/Paper';
import InputBase from '@mui/material/InputBase';
import IconButton from '@mui/material/IconButton';
import ClearIcon from '@mui/icons-material/Clear';
import SearchIcon from '@mui/icons-material/Search';

class Viewer extends Component {

   constructor(props) {
      super(props);
      this.state = {
         channels: [],
         articleViews: [],
         allArticleViews: [],
         descriptions: [],
         currentPage: 0,
         pageCount: 0,
         channelCurrentPage: 0,
         channelPageCount: 0,
         isLoading: false,
         online: true,
         searchChannelValue: '',
         searchArticleValue: ''
      };
      this.handleChannelChange = this.handleChannelChange.bind(this);
      this.handleArticleChange = this.handleArticleChange.bind(this);
      this.goOnline = this.goOnline.bind(this);
      this.goOffline = this.goOffline.bind(this);
   }

   goOnline() {
      this.setState({
         online: true
      });
   }

   goOffline() {
      this.setState({
         online: false
      });
   }

   componentDidMount() {
      Promise.all([
         fetch('/channel-view?page=0'),
         fetch('/article-view?page=0')
      ])
         .then(([res1, res2]) => Promise.all([res1.json(), res2.json()]))
         .then(([data1, data2]) => this.setState({
            channels: data1.channelViews,
            channelPageCount: data1.totalPages,
            articleViewResponse: data2,
            articleViews: data2.articleViews,
            allArticleViews: data2.articleViews,
            pageCount: data2.totalPages,
            paginationUrl: '/article-view',
            channelPaginationUrl: '/channel-view',
            online: typeof navigator.onLine === "boolean" ? navigator.onLine : true
         }));

      window.addEventListener("online", this.goOnline);
      window.addEventListener("offline", this.goOffline);
   }

   componentWillUnmount() {
      window.removeEventListener("online", this.goOnline);
      window.removeEventListener("offline", this.goOffline);
   }

   handleChannelChange(event) {
      const target = event.target;
      const value = target.value;
      this.setState({
         searchChannelValue: value
      });
   }

   handleArticleChange(event) {
      const target = event.target;
      const value = target.value;
      this.setState({
         searchArticleValue: value
      });
   }

   async show(id) {
      fetch(`/article-view/channel/${id}`)
         .then(response => response.json())
         .then(data => this.setState({
            articleViews: data.articleViews,
            pageCount: data.totalPages,
            paginationUrl: `/article-view/channel/${id}`,
            activeChannel: id,
            activeArticle: null,
            currentPage: 0
         }));
   }

   async showAll() {
      fetch(`/article-view`)
         .then(response => response.json())
         .then(data => this.setState({
            articleViews: data.articleViews,
            pageCount: data.totalPages,
            paginationUrl: `/article-view`,
            activeChannel: null,
            activeArticle: null,
            currentPage: 0
         }));
   }

   async showFavourites() {
      fetch(`/article-view/showFavourites`)
         .then(response => response.json())
         .then(data => this.setState({
            articleViews: data.articleViews,
            pageCount: data.totalPages,
            paginationUrl: `/article-view/showFavourites`,
            activeChannel: null,
            activeArticle: null,
            currentPage: 0
         }));
   }

   async showSaved() {
      fetch(`/article-view/showSaved`)
         .then(response => response.json())
         .then(data => this.setState({
            articleViews: data.articleViews,
            pageCount: data.totalPages,
            paginationUrl: `/article-view/showSaved`,
            activeChannel: null,
            activeArticle: null,
            currentPage: 0
         }));

   }

   async updateUnreadArticles() {
      fetch(this.state.channelPaginationUrl + '?page=' + this.state.channelCurrentPage)
         .then(response => response.json())
         .then(data => this.setState({
            channels: data.channelViews
         }));
   }

   async refreshChannel(id) {

      await fetch(`/article-view/updateChannel/${id}`, {
         method: 'PUT',
         headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json'
         },
         body: JSON.stringify(id),
      })
         .then(response => response.json())
         .then(data => this.setState({
            articleViews: data.articleViews,
            pageCount: data.totalPages,
            paginationUrl: `/article-view/channel/${id}`,
            activeChannel: id,
            activeArticle: null,
            currentPage: 0
         }))
         .catch(() => {
            alert("Unable to fetch rss channel. Try it later or check if rss source is still valid.");
         });

      this.updateUnreadArticles();
   };

   async updateAll() {
      this.setState({
         isLoading: true
      });

      await fetch(`/article-view/updateAll`, {
         method: 'PUT',
         headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json'
         },
         body: JSON.stringify(''),
      })
         .then(response => response.json())
         .then(data => this.setState({
            articleViews: data.articleViews,
            pageCount: data.totalPages,
            paginationUrl: `/article-view`,
            activeChannel: null,
            activeArticle: null,
            currentPage: 0,
            isLoading: false
         }))
         .catch(() => {
            alert("Unable to fetch rss channels.");
            this.setState({
               isLoading: false
            });
         });

      this.updateUnreadArticles();
   };

   async removeAll() {

      if (window.confirm('Do you want to delete all articles (included saved one)?')) {
         await fetch(`/article-view/removeAll`, {
            method: 'DELETE',
            headers: {
               'Accept': 'application/json',
               'Content-Type': 'application/json'
            }
         }).then(() => {
            this.setState({
               articleViews: [],
               activeChannel: null,
               activeArticle: null,
               currentPage: 0,
               pageCount: 0,
               paginationUrl: `/article-view`
            });

            this.updateUnreadArticles();
         });
      }
   };

   async removeAllUnsaved() {
      this.setState({
         currentPage: 0
      });

      if (window.confirm('Do you want to delete all unsaved articles?')) {
         await fetch(`/article-view/removeAllUnsaved`, {
            method: 'DELETE',
            headers: {
               'Accept': 'application/json',
               'Content-Type': 'application/json'
            }
         })
            .then(response => response.json())
            .then(data => this.setState({
               articleViews: data.articleViews,
               pageCount: data.totalPages,
               paginationUrl: `/article-view`,
               activeChannel: null,
               activeArticle: null,
               currentPage: 0
            }));

         this.updateUnreadArticles();
      }

   }

   async showDetails(articleView) {
      articleView.clicked = true;

      await fetch(`/article-view/clicked/${articleView.articleId}`, {
         method: 'PUT',
         headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json'
         },
         body: JSON.stringify(articleView),
      });

      this.props.history.push(`/channelviewer`);

      const updatedDescription = this.state.articleViews.filter(i => i.articleId === articleView.articleId);
      this.setState({
         descriptions: updatedDescription,
         activeArticle: articleView.articleId
      });

      this.updateUnreadArticles();
   }

   async favourite(articleView, favourite) {
      articleView.favourite = favourite;

      await fetch(`/article-view/favourite/${articleView.articleId}`, {
         method: 'PUT',
         headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json'
         },
         body: JSON.stringify(articleView),
      });

      this.props.history.push(`/channelviewer`);
   }

   async save(articleView, saved) {
      articleView.saved = saved;

      await fetch(`/article-view/saved/${articleView.articleId}`, {
         method: 'PUT',
         headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json'
         },
         body: JSON.stringify(articleView),
      });

      this.props.history.push(`/channelviewer`);
   }

   async remove(articleView) {
      if (window.confirm('Do you want to delete this article?')) {
         await fetch(`/article-view/remove/${articleView.articleId}`, {
            method: 'DELETE',
            headers: {
               'Accept': 'application/json',
               'Content-Type': 'application/json'
            }
         }).then(() => {
            this.state.paginationUrl === '/article-view/showSaved' ? this.showSaved() :
               this.state.paginationUrl === '/article-view/showFavourites' ? this.showFavourites() :
                  this.state.paginationUrl === '/article-view' ? this.showAll() :
                     this.show(articleView.channelId);

            this.updateUnreadArticles();
         });
      }

   };

   async handleChannelSearch(searchChannelValue) {
      await fetch(`/channel-view/searchChannels/${searchChannelValue}`)
         .then(response => response.json())
         .then(data => this.setState({
            channels: data.channelViews,
            channelPageCount: data.totalPages,
            channelPaginationUrl: `/channel-view/searchChannels/${searchChannelValue}`,
            activeChannel: null,
            activeArticle: null,
            channelCurrentPage: 0
         }));
   };

   async handleArticleSearch(searchArticleValue) {
      await fetch(`/article-view/searchArticles/${searchArticleValue}`)
         .then(response => response.json())
         .then(data => this.setState({
            articleViews: data.articleViews,
            pageCount: data.totalPages,
            paginationUrl: `/article-view/searchArticles/${searchArticleValue}`,
            activeChannel: null,
            activeArticle: null,
            currentPage: 0
         }));
   };

   async removeChannelFilter() {

      await fetch(`/channel-view`)
         .then(response => response.json())
         .then(data => this.setState({
            channels: data.channelViews,
            channelPageCount: data.totalPages,
            channelPaginationUrl: '/channel-view',
            searchChannelValue: '',
            channelCurrentPage: 0
         }));
   }


   returnAction(action) {
      switch (action) {
         case 'updateAll':
            return this.updateAll();
            // eslint-disable-next-line
            break;
         case 'showAll':
            // eslint-disable-next-line
            return this.showAll();
            // eslint-disable-next-line
            break;
         case 'showFavourites':
            return this.showFavourites();
            // eslint-disable-next-line
            break;
         case 'showSaved':
            return this.showSaved();
            // eslint-disable-next-line
            break;
         case 'removeAll':
            return this.removeAll();
            // eslint-disable-next-line
            break;
         case 'removeAllUnsaved':
            return this.removeAllUnsaved()
            // eslint-disable-next-line
            break;
         default:
            return this.updateAll();
      }
   }


   render() {
      const {
         articleViews,
         channels,
         descriptions,
         activeChannel,
         activeArticle,
         pageCount,
         channelPageCount,
         isLoading,
         searchChannelValue,
         searchArticleValue
      } = this.state;

      const handlePageClick = ({
         selected: selectedPage
      }) => {
         this.setState({
            currentPage: selectedPage
         });

         fetch(this.state.paginationUrl + `?page=${selectedPage}`)
            .then(response => response.json())
            .then(data => this.setState({
               articleViews: data.articleViews,
               pageCount: data.totalPages
            }));
      };

      const channelHandlePageClick = ({
         selected: selectedPage
      }) => {
         this.setState({
            channelCurrentPage: selectedPage
         });

         fetch(this.state.channelPaginationUrl + `?page=${selectedPage}`)
            .then(response => response.json())
            .then(data => this.setState({
               channels: data.channelViews,
               channelPageCount: data.totalPages
            }));
      };

      const convertDateTime = (dateTime) => {
         const date = new Date(dateTime);
         return dayjs(date).format("DD-MM-YYYY HH:mm");
      };

      const channelViewer = channels.map(channel => {
         return <tr key={channel.id}
            className="banner">
            <td className="banner-block"
               style={{
                  fontWeight: channel.unread === 0 ? 'normal' : 'bold',
                  color: channel.valid === false ? "red" : channel.active === true ? "black" : "gray",
                  background: activeChannel === channel.id ? '#ffc107' : 'none'
               }}
               onClick={() => this.show(channel.id)} >

               {channel.title.length > 30
                  ? channel.title.substring(0, 30) + '…'
                  : channel.title} ({channel.unread})
            </td>

            <td className="banner-button"
               style={{ background: activeChannel === channel.id ? '#ffc107' : 'none' }}>

               <ButtonGroup>
                  {channel.active
                     ? <Button size="sm"
                        color="none"
                        onClick={() => this.refreshChannel(channel.id)}
                        disabled={isLoading || !this.state.online} >

                        <img src={RefreshImg}
                           className="icon24"
                           alt="Active channel" />
                     </Button>
                     : <Button size="sm"
                        color="none">

                        <img src={RefreshOffImg}
                           className="icon26"
                           alt="Inactive channel" />
                     </Button>
                  }
               </ButtonGroup>
            </td>
         </tr>
      });

      const articleViewer = articleViews.map(articleView => {
         return <tr key={articleView.articleId}
            style={{ fontWeight: articleView.clicked === false ? 'bold' : 'normal' }} >
            <td className="article-channel"
               style={{ background: activeArticle === articleView.articleId ? '#ffc107' : 'none' }}
               onClick={() => this.showDetails(articleView)}>
               {articleView.channel.length > 25
                  ? articleView.channel.substring(0, 25) + '…'
                  : articleView.channel}
            </td>

            <td className="article-title"
               style={{ background: activeArticle === articleView.articleId ? '#ffc107' : 'none' }}
               onClick={() => this.showDetails(articleView)}>
               {articleView.title.length > 100
                  ? articleView.title.substring(0, 100) + '…'
                  : articleView.title
               }
            </td>

            <td className="article-time"
               style={{ background: activeArticle === articleView.articleId ? '#ffc107' : 'none' }}
               onClick={() => this.showDetails(articleView)}>
               {convertDateTime(articleView.date)}
            </td>

            <td className="article-buttons"
               style={{ background: activeArticle === articleView.articleId ? '#ffc107' : 'none' }}>
               <ButtonGroup>
                  <Button size="sm"
                     color="none"
                     onClick={() => this.favourite(articleView, !articleView.favourite)}>
                     <img src={articleView.favourite === true ? AfterFavouriteImg : BeforeFavouriteImg}
                        className="icon25"
                        alt="Favourite Article"
                        disabled={isLoading} />
                  </Button>
                  <Button size="sm"
                     color="none"
                     onClick={() => this.save(articleView, !articleView.saved)}>
                     <img src={articleView.saved === true ? AfterSaveImg : BeforeSaveImg}
                        className="icon25"
                        alt="Favourite Article"
                        disabled={isLoading} />
                  </Button>
                  <Button size="sm"
                     color="none"
                     onClick={() => this.remove(articleView)}>
                     <img src={DeleteImg}
                        className="icon25"
                        alt="Favourite Article"
                        disabled={isLoading} />
                  </Button>
               </ButtonGroup>
            </td>
         </tr>
      });

      const descriptionViewer = descriptions.map(description => {
         return <tr key={description.id}>
            <td className="description-block">
               <a href={description.link}
                  target="_blank"
                  rel="noopener noreferrer">

                  <img src={WebImg}
                     className="web-img"
                     alt="URL link" />
               </a>
            </td>

            <td className="td-blank">
            </td>

            <td className="description-text">
               {description.description}
            </td>
         </tr>
      });

      const createButton = (image, caption, action) => {
         return (
            <Button size="sm"
               color="none"
               onClick={() => this.returnAction(action)}
               disabled={isLoading || action === 'updateAll' ? !this.state.online : false}>

               <div className="figure">
                  <figure>
                     <img src={image}
                        className="big-icon"
                        alt="Generated" />
                     <figcaption>
                        {caption}
                     </figcaption>
                  </figure>
               </div>
            </Button>
         );
      }

      const buttonHeader =
         <ButtonGroup>
            {createButton(UpdateImg, 'Update All', 'updateAll')}
            {createButton(ShowAllImg, 'Show All', 'showAll')}
            {createButton(FavouriteImg, 'Show Favourites', 'showFavourites')}
            {createButton(SavedImg, 'Show Saved', 'showSaved')}
            {createButton(RemoveAllImg, 'Remove All', 'removeAll')}
            {createButton(RemoveImg, 'Remove Unsaved', 'removeAllUnsaved')}
         </ButtonGroup>
         ;

      const search = (type) => {
         return (
            <Paper component="form"
               sx={type === "channel" ? searchBoxChannel : searchBoxArticle}>

               <InputBase
                  sx={{ ml: 1, flex: 1 }}
                  placeholder={type === "channel" ? "Search Channels (case insensitive)" : "Search Articles (case insensitive)"}
                  inputProps={{ 'aria-label': type === "channel" ? 'Search Channels' : 'Search Articles' }}
                  value={type === "channel" ? searchChannelValue : searchArticleValue}
                  onChange={type === "channel" ? this.handleChannelChange : this.handleArticleChange}
               />

               <IconButton
                  type="button"
                  sx={iconButton}
                  aria-label="search"
                  onClick={() => type === "channel" ? this.handleChannelSearch(searchChannelValue) : this.handleArticleSearch(searchArticleValue)} >
                  <SearchIcon />
               </IconButton>

               <IconButton
                  type="button"
                  sx={iconButton}
                  aria-label="clearSearch"
                  onClick={() => type === "channel" ? this.removeChannelFilter() : this.showAll()}>
                  <ClearIcon />
               </IconButton>
            </Paper>
         );
      }

      const override: CSSProperties = {
         position: 'absolute',
         left: '60vh',
         right: 0,
         top: '40vh',
         bottom: 0,
         alignItems: 'center',
         justifyContent: 'center',
         opacity: '0.8'

      };

      const iconButton: CSSProperties = {
         color: '#fff', p: '5px'
      };

      const searchBoxChannel: CSSProperties = {
         mt: 1,
         mb: 2,
         p: '2px 4px',
         display: 'flex',
         alignItems: 'center',
         width: '375px',
         maxWidth: '375px',
         boxShadow: "none",
         background: '#ffc107',
         borderRadius: '10px'
      };

      const searchBoxArticle: CSSProperties = {
         mt: .3,
         mb: .5,
         p: '2px 4px',
         display: 'flex',
         alignItems: 'center',
         width: '500px',
         maxWidth: '500px',
         boxShadow: "none",
         background: '#ffc107',
         borderRadius: '10px'
      };


      return (
         <div>
            <AppNavbar />
            <Container fluid>
               {buttonHeader}
               <Table>
                  <thead />
                  <tbody>
                     <tr>
                        <td rowspan="2" width="390px">
                           <div className="channel-scroll">
                              {search("channel")}
                              {channelViewer}
                           </div>
                        </td>

                        <td>
                           <div className="description-scroll">
                              {descriptionViewer}
                           </div>
                        </td>
                     </tr>
                     <td>
                        <div className="article-scroll" >
                           {search("article")}
                           {articleViewer}
                        </div>
                     </td>
                     <tr>
                        <td rowspan="1">
                           <div className="channel_pagination-scroll">
                              <ReactPaginate
                                 previousLabel={"←"}
                                 nextLabel={"→"}
                                 pageCount={channelPageCount}
                                 onPageChange={channelHandlePageClick}
                                 containerClassName={"channel_pagination"}
                                 previousLinkClassName={"channel_pagination__link"}
                                 nextLinkClassName={"channel_pagination__link"}
                                 disabledClassName={"channel_pagination__link--disabled"}
                                 activeClassName={"channel_pagination__link--active"}
                                 marginPagesDisplayed={1}
                                 pageRangeDisplayed={1}
                                 forcePage={this.state.channelCurrentPage}
                              />
                           </div>
                        </td>
                        <td>
                           <div className="pagination-scroll">
                              <ReactPaginate
                                 previousLabel={"← Previous"}
                                 nextLabel={"Next →"}
                                 pageCount={pageCount}
                                 onPageChange={handlePageClick}
                                 containerClassName={"pagination"}
                                 previousLinkClassName={"pagination__link"}
                                 nextLinkClassName={"pagination__link"}
                                 disabledClassName={"pagination__link--disabled"}
                                 activeClassName={"pagination__link--active"}
                                 forcePage={this.state.currentPage}
                              />
                           </div>
                        </td>
                     </tr>
                  </tbody>
               </Table>
            </Container>

            <RiseLoader
               color="#ffc107"
               loading={isLoading}
               cssOverride={override}
               size={200}
               aria-label="Loading Spinner"
               data-testid="loader"
            />
         </div>
      );
   }
}

export default Viewer;