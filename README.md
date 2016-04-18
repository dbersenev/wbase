wbase
=====

WBase is the Java library of reusable components and integration with JSF/Spring/Hibernate/MyBatis/Etc.

*Why WBase? At first it was like "web base" but later it became more universal. So now it is "world base".*

##Content

1. wbase - provides common utitlities for DB, XML, etc.
2. wbase-web - introduces tools for plain servlet/jsp (very small yet).
3. wbase-jsf - integrates wbase with JSF2. Provides some tools.
4. wbase-jsf-spring - simplifies JSF2 and Spring integration.
5. wbase-hibernate - integrates wbase and hibernate using Spring.
6. wbase-batis - integrates wbase and MyBatis.
7. wbase-batis-spring - simplifies MyBatis and Spring integration.
8. wbase-spring - wbase spring integration. For now transactions only.

##Development Highlights

1. Project currently in not in usable state
2. Cursors framework introduces interesting concepts including:
   a. Cursors can be used with both "result sets" and "db pagination".
   b. Cursors are separated to "Cursor" and "Factory" which provides more convenient approach
      dealing with different requirements and cursors mutability.
3. Transactions framework is response to Spring transactions. It aims to simplify creation of transaction managers 
   and synchronization between multiple transaction managers with clear API and explicit semantics.
4. It will be possible to synchronize my transactions with Spring ones.

