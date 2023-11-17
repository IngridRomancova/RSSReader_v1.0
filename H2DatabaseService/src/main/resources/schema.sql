create table IF NOT EXISTS Channel(
  IDENT int not null AUTO_INCREMENT,
  TITLE varchar(255),
  LINK varchar(255),
  DESCRIPTION varchar(4000),
  ACTIVE boolean DEFAULT true,
  VALID boolean DEFAULT true,
  UPDATED_FROM datetime,
  UNREAD int DEFAULT 0,
  PRIMARY KEY ( IDENT ),
  UNIQUE (TITLE)
);

create table IF NOT EXISTS Article(
  IDENT int not null AUTO_INCREMENT,
  TITLE varchar(500) ,
  DESCRIPTION varchar(4000),
  LINK varchar(255),
  URI varchar(255),
  DATE datetime,
  CHANNEL int,
  CLICKED boolean DEFAULT false,
  FAVOURITE boolean DEFAULT false,
  SAVED boolean DEFAULT false,
  ARCHIVED boolean,
  PRIMARY KEY ( IDENT )
);

create or replace view CHANNEL_VIEW AS
SELECT
         ch.ident as ident,
         ch.title as title,
         ch.active as active,
         ch.valid as valid,
         ch.unread as unread
  FROM Channel ch
;

create table IF NOT EXISTS ARTICLE_JOIN(
  IDENT int not null AUTO_INCREMENT,
  CHANNEL_IDENT int,
  ARTICLE_IDENT int,
  UNIQUE (CHANNEL_IDENT, ARTICLE_IDENT)
);

create or replace view ARTICLE_VIEW AS
SELECT
         j.ident AS ident,
         a.ident AS article_ident,
         a.title AS title,
         a.description AS description,
         a.link AS link,
         a.uri AS uri,
         a.date AS date,
         a.clicked AS clicked,
         a.saved AS saved,
         a.favourite AS favourite,
         ch.title as channel,
         ch.ident as channel_ident
  FROM ARTICLE_JOIN j
       INNER JOIN Article a on  j.article_ident = a.ident
       INNER JOIN Channel ch on j.channel_ident = ch.ident;
;

create table IF NOT EXISTS Keyword(
  IDENT int not null AUTO_INCREMENT,
  KEYWORD varchar(255),
  DESCRIPTION varchar(255),
  ACTIVE boolean DEFAULT true,
  TAGS blob,
  UNREAD int DEFAULT 0,
  UPDATED_FROM datetime,
  PRIMARY KEY ( IDENT ),
  UNIQUE (KEYWORD)
);

create table IF NOT EXISTS KEYWORD_JOIN(
  IDENT int not null AUTO_INCREMENT,
  KEYWORD_IDENT int,
  ARTICLE_IDENT int,
  CHANNEL_IDENT int,
  INVISIBLE boolean DEFAULT false,
  UNIQUE (KEYWORD_IDENT, ARTICLE_IDENT)
);

create or replace view SEARCH_VIEW AS
SELECT
         k.ident as ident,
         k.keyword as keyword,
         k.active as active,
--         (SELECT count(*)
--                      FROM Keyword_Join j
--                      JOIN Article a on a.ident = j.article_ident
--                     where a.clicked = false
--                      and j.invisible = false
--                      and k.ident = j.keyword_ident) AS unread
          k.unread as unread
  FROM Keyword k
;

create or replace view KEYWORD_VIEW AS
SELECT
         j.ident AS ident,
         k.ident AS keyword_ident,
         k.keyword AS keyword,
         a.ident AS article_ident,
         a.title AS title,
         a.description AS description,
         a.link AS link,
         a.uri AS uri,
         a.date AS date,
         ch.title as channel,
         ch.ident as channel_ident,
         a.clicked AS clicked,
         j.invisible AS invisible,
         a.saved AS saved,
         a.favourite AS favourite
  FROM KEYWORD_JOIN j
       INNER JOIN Keyword k on j.keyword_ident = k.ident
       INNER JOIN Article a on  j.article_ident = a.ident
       INNER JOIN Channel ch on a.channel = ch.ident
;



