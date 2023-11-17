import React, { Component } from 'react';
import { Button, ButtonGroup, Container, Table } from 'reactstrap';
import AppNavbar from './AppNavbar';
import RefreshImg from "../img/refresh.png";
import WebImg from "../img/www_big.png";
import FilterImg from '../img/filter.png';
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
import VisibleImg from '../img/visible.png';
import InvisibleImg from '../img/invisible.png';
import InvisibleBigImg from '../img/invisibleBig.png';
import RefreshOffImg from '../img/refreshOff.png';
import '../css/viewer.css';
import "../css/common.css";
import "../css/pagination.css";
import ReactPaginate from 'react-paginate';
import dayjs from 'dayjs';
import Paper from '@mui/material/Paper';
import InputBase from '@mui/material/InputBase';
import IconButton from '@mui/material/IconButton';
import ClearIcon from '@mui/icons-material/Clear';
import SearchIcon from '@mui/icons-material/Search';

class KeywordViewer extends Component {

   constructor(props) {
      super(props);
      this.state = {
         keywords: [],
         keywordViews: [],
         allKeywordViews: [],
         descriptions: [],
         currentPage: 0,
         pageCount: 0,
         searchCurrentPage: 0,
         searchPageCount: 0,
         searchKeywordValue: '',
         searchArticleValue: ''
      };
      this.handleKeywordChange = this.handleKeywordChange.bind(this);
      this.handleArticleChange = this.handleArticleChange.bind(this);
   }

   componentDidMount() {
      Promise.all([
         fetch('/search-view?page=0'),
         fetch('/keyword-view?page=0')
      ])
         .then(([res1, res2]) => Promise.all([res1.json(), res2.json()]))
         .then(([data1, data2]) => this.setState({
            keywords: data1.searchViews,
            searchPageCount: data1.totalPages,
            keywordViewResponse: data2,
            keywordViews: data2.keywordViews,
            allKeywordViews: data2.keywordViews,
            pageCount: data2.totalPages,
            paginationUrl: '/keyword-view',
            searchPaginationUrl: '/search-view'
         }));
   }

   handleKeywordChange(event) {
      const target = event.target;
      const value = target.value;
      this.setState({
         searchKeywordValue: value
      });
   }

   handleArticleChange(event) {
      const target = event.target;
      const value = target.value;
      this.setState({
         searchArticleValue: value
      });
   }

   async updateUnreadArticles() {
      fetch(this.state.searchPaginationUrl + '?page=' + this.state.searchCurrentPage)
         .then(response => response.json())
         .then(data => this.setState({
            keywords: data.searchViews
         }));
   }

   async show(keywordId) {
      fetch(`/keyword-view/keyword/${keywordId}`)
         .then(response => response.json())
         .then(data => this.setState({
            keywordViews: data.keywordViews,
            pageCount: data.totalPages,
            paginationUrl: `/keyword-view/keyword/${keywordId}`,
            activeKeyword: keywordId,
            activeArticle: null,
            currentPage: 0
         }));
   }

   async showDetails(keywordView) {
      keywordView.clicked = true;

      await fetch(`/keyword-view/clicked/${keywordView.id}`, {
         method: 'PUT',
         headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json'
         },
         body: JSON.stringify(keywordView),
      });

      this.props.history.push(`/keywordviewer`);

      const updatedDescription = this.state.keywordViews.filter(i => i.id === keywordView.id);
      this.setState({
         descriptions: updatedDescription,
         activeArticle: keywordView.articleId
      });

      this.updateUnreadArticles();
   }

   async refreshKeywords(keywordId) {

      await fetch(`/keyword-view/refresh/${keywordId}`, {
         method: 'PUT',
         headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json'
         },
         body: JSON.stringify(keywordId),
      })
         .then(response => response.json())
         .then(data => this.setState({
            keywordViews: data.keywordViews,
            pageCount: data.totalPages,
            paginationUrl: `/keyword-view/keyword/${keywordId}`,
            activeKeyword: keywordId,
            activeArticle: null,
            currentPage: 0
         }));

      this.updateUnreadArticles();
   };

   async filterAll() {

      await fetch(`/keyword-view/filterAll`, {
         method: 'PUT',
         headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json'
         },
         body: JSON.stringify(''),
      })
         .then(response => response.json())
         .then(data => this.setState({
            keywordViews: data.keywordViews,
            pageCount: data.totalPages,
            paginationUrl: `/keyword-view`,
            activeKeyword: null,
            activeArticle: null,
            currentPage: 0
         }));

      this.updateUnreadArticles();
   };

   async favourite(keywordView, favourite) {
      keywordView.favourite = favourite;

      await fetch(`/keyword-view/favourite/${keywordView.id}`, {
         method: 'PUT',
         headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json'
         },
         body: JSON.stringify(keywordView),
      });

      this.props.history.push(`/keywordviewer`);

      this.state.keywordViews.forEach((view) => {
         if (view.articleId === keywordView.articleId) {
            view.favourite = favourite;
         }
      });
   }

   async save(keywordView, saved) {
      keywordView.saved = saved;

      await fetch(`/keyword-view/saved/${keywordView.id}`, {
         method: 'PUT',
         headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json'
         },
         body: JSON.stringify(keywordView),
      });

      this.props.history.push(`/keywordviewer`);

      this.state.keywordViews.forEach((view) => {
         if (view.articleId === keywordView.articleId) {
            view.saved = saved;
         }
      });
   }

   async remove(keywordView) {
      if (window.confirm('Do you want to delete this article?')) {
         await fetch(`/article-view/remove/${keywordView.articleId}`, {
            method: 'DELETE',
            headers: {
               'Accept': 'application/json',
               'Content-Type': 'application/json'
            }
         }).then(() => {
            this.state.paginationUrl === '/keyword-view/showSaved' ? this.showSaved() :
               this.state.paginationUrl === '/keyword-view/showFavourites' ? this.showFavourites() :
                  this.state.paginationUrl === '/keyword-view' ? this.showAll() :
                     this.state.paginationUrl === '/keyword-view/showInvisible' ? this.showInvisible() :
                        this.show(keywordView.keywordId);

            this.updateUnreadArticles();
         });
      }
   };

   async invisible(keywordView, invisible) {
      if (window.confirm(invisible ? 'Do you want to hide an article from keyword search result?' : 'Do you want to unhide an article?')) {
         keywordView.invisible = invisible;

         await fetch(`/keyword-view/invisible/${keywordView.id}`, {
            method: 'PUT',
            headers: {
               'Accept': 'application/json',
               'Content-Type': 'application/json'
            },
            body: JSON.stringify(keywordView),
         }).then(() => {

            this.state.paginationUrl === '/keyword-view/showSaved' ? this.showSaved() :
               this.state.paginationUrl === '/keyword-view/showFavourites' ? this.showFavourites() :
                  this.state.paginationUrl === '/keyword-view' ? this.showAll() :
                     this.state.paginationUrl === '/keyword-view/showInvisible' ? this.showInvisible() :
                        this.show(keywordView.keywordId);

            this.updateUnreadArticles();
         });
      }
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
               keywordViews: [],
               activeKeyword: null,
               activeArticle: null,
               currentPage: 0,
               pageCount: 0,
               paginationUrl: `/keyword-view`
            });

            this.updateUnreadArticles();
         });
      }
   };

   async removeAllUnsaved() {
      if (window.confirm('Do you want to delete all unsaved articles?')) {
         await fetch(`/keyword-view/removeAllUnsaved`, {
            method: 'DELETE',
            headers: {
               'Accept': 'application/json',
               'Content-Type': 'application/json'
            }
         })
            .then(response => response.json())
            .then(data => this.setState({
               keywordViews: data.keywordViews,
               pageCount: data.totalPages,
               paginationUrl: `/keyword-view`,
               activeKeyword: null,
               activeArticle: null,
               currentPage: 0
            }));

         this.updateUnreadArticles();
      }
   }

   async showAll() {
      fetch(`/keyword-view`)
         .then(response => response.json())
         .then(data => this.setState({
            keywordViews: data.keywordViews,
            pageCount: data.totalPages,
            paginationUrl: `/keyword-view`,
            activeKeyword: null,
            activeArticle: null,
            currentPage: 0
         }));
   }

   async showFavourites() {
      fetch(`/keyword-view/showFavourites`)
         .then(response => response.json())
         .then(data => this.setState({
            keywordViews: data.keywordViews,
            pageCount: data.totalPages,
            paginationUrl: `/keyword-view/showFavourites`,
            activeKeyword: null,
            activeArticle: null,
            currentPage: 0
         }));
   }

   async showSaved() {
      fetch(`/keyword-view/showSaved`)
         .then(response => response.json())
         .then(data => this.setState({
            keywordViews: data.keywordViews,
            pageCount: data.totalPages,
            paginationUrl: `/keyword-view/showSaved`,
            activeKeyword: null,
            activeArticle: null,
            currentPage: 0
         }));

   }

   async showInvisible() {
      fetch(`/keyword-view/showInvisible`)
         .then(response => response.json())
         .then(data => this.setState({
            keywordViews: data.keywordViews,
            pageCount: data.totalPages,
            paginationUrl: `/keyword-view/showInvisible`,
            activeKeyword: null,
            activeArticle: null,
            currentPage: 0
         }));

   }

   async handleKeywordSearch(searchKeywordValue) {
      await fetch(`/search-view/searchKeywords/${searchKeywordValue}`)
         .then(response => response.json())
         .then(data => this.setState({
            keywords: data.searchViews,
            searchPageCount: data.totalPages,
            searchPaginationUrl: `/search-view/searchKeywords/${searchKeywordValue}`,
            activeKeyword: null,
            activeArticle: null,
            currentPage: 0
         }));
   };

   async handleArticleSearch(searchArticleValue) {
      await fetch(`/keyword-view/searchArticles/${searchArticleValue}`)
         .then(response => response.json())
         .then(data => this.setState({
            keywordViews: data.keywordViews,
            pageCount: data.totalPages,
            paginationUrl: `/keyword-view/searchArticles/${searchArticleValue}`,
            activeKeyword: null,
            activeArticle: null,
            currentPage: 0
         }));
   };

   async removeKeywordFilter() {
      await fetch(`/search-view`)
         .then(response => response.json())
         .then(data => this.setState({
            keywords: data.searchViews,
            searchPageCount: data.totalPages,
            searchPaginationUrl: '/search-view',
            searchKeywordValue: '',
            searchCurrentPage: 0
         }));
   }

   returnAction(action) {
      switch (action) {
         case 'filterAll':
            return this.filterAll();
            // eslint-disable-next-line
            break;
         case 'showAll':
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
         case 'showInvisible':
            return this.showInvisible();
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
            return this.filterAll();
      }
   }


   render() {
      const {
         keywordViews,
         keywords,
         descriptions,
         activeKeyword,
         activeArticle,
         pageCount,
         searchPageCount,
         searchKeywordValue,
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
               keywordViews: data.keywordViews,
               pageCount: data.totalPages
            }));
      };

      const searchHandlePageClick = ({
         selected: selectedPage
      }) => {
         this.setState({
            searchCurrentPage: selectedPage
         });

         fetch(this.state.searchPaginationUrl + `?page=${selectedPage}`)
            .then(response => response.json())
            .then(data => this.setState({
               keywords: data.searchViews,
               searchPageCount: data.totalPages
            }));
      };

      const convertDateTime = (dateTime) => {
         const date = new Date(dateTime);
         return dayjs(date).format("DD-MM-YYYY HH:mm");
      };

      const keywordViewer = keywords.map(keyword => {
         return <tr key={keyword.id}
            className="banner">
            <td className="banner-block"
               style={{
                  fontWeight: keyword.unread === 0 ? 'normal' : 'bold',
                  color: keyword.active === true ? "black" : "gray",
                  background: activeKeyword === keyword.id ? '#ffc107' : 'none'
               }}
               onClick={() => this.show(keyword.id)} >

               {keyword.keyword.length > 30
                  ? keyword.keyword.substring(0, 30) + '…'
                  : keyword.keyword} ({keyword.unread})
            </td>

            <td className="banner-button"
               style={{ background: activeKeyword === keyword.id ? '#ffc107' : 'none' }}>

               <ButtonGroup>
                  {keyword.active
                     ? <Button size="sm"
                        color="none"
                        onClick={() => this.refreshKeywords(keyword.id)}>

                        <img src={RefreshImg}
                           className="icon24"
                           alt="Active keyword" />
                     </Button>
                     : <Button size="sm"
                        color="none">

                        <img src={RefreshOffImg}
                           className="icon26"
                           alt="Inactive keyword" />
                     </Button>
                  }
               </ButtonGroup>
            </td>
         </tr>
      });

      const articleViewer = keywordViews.map(keywordView => {
         return <tr key={keywordView.id}
            style={{ fontWeight: keywordView.clicked === false ? 'bold' : 'normal' }}>
            <td className="article-channel"
               style={{ background: activeArticle === keywordView.articleId ? '#ffc107' : 'none' }}
               onClick={() => this.showDetails(keywordView)}>

               {keywordView.channel.length > 25
                  ? keywordView.channel.substring(0, 25) + '…'
                  : keywordView.channel + ' '}
            </td>

            <td className="keyword-channel"
               style={{ background: activeArticle === keywordView.articleId ? '#ffc107' : 'none' }}
               onClick={() => this.showDetails(keywordView)}>

               ({keywordView.keyword.length > 25
                  ? keywordView.keyword.substring(0, 25) + '…'
                  : keywordView.keyword})
            </td>

            <td width="50%"
               className="keyword-title"
               style={{ background: activeArticle === keywordView.articleId ? '#ffc107' : 'none' }}
               onClick={() => this.showDetails(keywordView)}>

               {keywordView.title.length > 100
                  ? keywordView.title.substring(0, 100) + '…'
                  : keywordView.title
               }
            </td>

            <td className="keyword-time"
               style={{ background: activeArticle === keywordView.articleId ? '#ffc107' : 'none' }}
               onClick={() => this.showDetails(keywordView)}>

               {convertDateTime(keywordView.date)}
            </td>

            <td className="keyword-buttons"
               style={{ background: activeArticle === keywordView.articleId ? '#ffc107' : 'none' }}>
               <ButtonGroup>
                  <Button size="sm"
                     color="none"
                     onClick={() => this.favourite(keywordView, !keywordView.favourite)}>

                     <img src={keywordView.favourite === true ? AfterFavouriteImg : BeforeFavouriteImg}
                        className="icon25"
                        alt="Favourite Filtered Record" />
                  </Button>

                  <Button size="sm"
                     color="none"
                     onClick={() => this.save(keywordView, !keywordView.saved)}>

                     <img src={keywordView.saved === true ? AfterSaveImg : BeforeSaveImg}
                        className="icon25"
                        alt="Saved Filtered Record" />
                  </Button>

                  <Button size="sm"
                     color="none"
                     onClick={() => this.invisible(keywordView, !keywordView.invisible)}>

                     <img src={keywordView.invisible === true ? InvisibleImg : VisibleImg}
                        className="icon25"
                        alt="Favourite Filtered Record" />
                  </Button>

                  <Button size="sm"
                     color="none"
                     onClick={() => this.remove(keywordView)}>

                     <img src={DeleteImg}
                        className="icon25"
                        alt="Removed Filtered Record" />
                  </Button>
               </ButtonGroup>
            </td>
         </tr>
      });

      const descriptionViewer = descriptions.map(keywordView => {
         return <tr key={keywordView.articleId}>
            <td className="description-block">
               <a href={keywordView.link}
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
               {keywordView.description}
            </td>
         </tr>
      });

      const createButton = (image, caption, action) => {
         return (
            <Button size="sm"
               color="none"
               onClick={() => this.returnAction(action)}>

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
            {createButton(FilterImg, 'Filter All', 'filterAll')}
            {createButton(ShowAllImg, 'Show All', 'showAll')}
            {createButton(FavouriteImg, 'Show Favourites', 'showFavourites')}
            {createButton(SavedImg, 'Show Saved', 'showSaved')}
            {createButton(InvisibleBigImg, 'Show Invisible', 'showInvisible')}
            {createButton(RemoveAllImg, 'Remove All', 'removeAll')}
            {createButton(RemoveImg, 'Remove Unsaved', 'removeAllUnsaved')}
         </ButtonGroup>
         ;

      const search = (type) => {
         return (
            <Paper component="form"
               sx={type === "keyword" ? searchBoxKeyword : searchBoxArticle}>

               <InputBase
                  sx={inputBase}
                  placeholder={type === "keyword" ? "Search Keyword (case insensitive)" : "Search Filtered Articles (case insensitive)"}
                  inputProps={{ 'aria-label': type === "keyword" ? 'Search Keywords' : 'Search Filtered Articles' }}
                  value={type === "keyword" ? searchKeywordValue : searchArticleValue}
                  onChange={type === "keyword" ? this.handleKeywordChange : this.handleArticleChange}
               />

               <IconButton
                  type="button"
                  sx={iconButton}
                  aria-label="search"
                  onClick={() => type === "keyword" ? this.handleKeywordSearch(searchKeywordValue) : this.handleArticleSearch(searchArticleValue)} >
                  <SearchIcon />
               </IconButton>

               <IconButton
                  type="button"
                  sx={iconButton}
                  aria-label="clearSearch"
                  onClick={() => type === "keyword" ? this.removeKeywordFilter() : this.showAll()}>
                  <ClearIcon />
               </IconButton>
            </Paper>
         );
      }

      const iconButton: CSSProperties = {
         color: '#fff', p: '5px'
      };

      const inputBase: CSSProperties = {
         ml: 1,
         flex: 1
      };

      const searchBoxKeyword: CSSProperties = {
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
                              {search("keyword")}
                              {keywordViewer}
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
                                 pageCount={searchPageCount}
                                 onPageChange={searchHandlePageClick}
                                 containerClassName={"channel_pagination"}
                                 previousLinkClassName={"channel_pagination__link"}
                                 nextLinkClassName={"channel_pagination__link"}
                                 disabledClassName={"channel_pagination__link--disabled"}
                                 activeClassName={"channel_pagination__link--active"}
                                 marginPagesDisplayed={1}
                                 pageRangeDisplayed={1}
                                 forcePage={this.state.searchCurrentPage}
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
         </div>
      );
   }
}

export default KeywordViewer;