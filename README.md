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

##Samples

**Transactions (wbase)**

```Java
TransactionManager<JdbcEngine> tm = new JdbcTransactionManager(new DataSourceConnectionSource(dataSource));
try(UserTransaction<JdbcEngine> tx = tm.createTransaction()){ //try with resources
   JdbcEngine e = tx.engine();
   Long someResult = e.extended(mySQLQuery).longResult();
   tx.commit(); //if commit or rollback is omitted Tx will be rolled back on close
}
```

```Java
TransactionManager<JdbcEngine> tm = new JdbcTransactionManager(new DataSourceConnectionSource(dataSource));
try(UserTransaction<JdbcEngine> tx = tm.createTransaction()) {
   //this one is going to reuse outer transaction (proxy)
   //Default transaction requirement is "NEW_OR_PROPAGATED"
   try(UserTransaction<JdbcEngine> tx2 = tm.createTransaction()) {
      //only rollback is possible for this transaction
      //it is going to trigger rollback for the parent one
   }
   tx.commit(); //commit both transactions since they are mapped to the same one
}
```

```Java
TransactionManager<JdbcEngine> tm = new JdbcTransactionManager(new DataSourceConnectionSource(dataSource));
try(UserTransaction<JdbcEngine> tx = tm.createTransaction()) {
   //here we have independent transaction
   try(UserTransaction<JdbcEngine> tx2 = tm.createTransaction(TransactionDescriptors.ALWAYS_NEW)) {
   }
   tx.commit(); //commit only first one
}
```

```Java
TransactionManager<JdbcEngine> tm = new JdbcTransactionManager(new DataSourceConnectionSource(dataSource));
try(UserTransaction<JdbcEngine> tx = tm.createTransaction()) {
   //here we force new transaction even thoug inner is eglible to have propagated transaction
   tx.context().modifyDescriptor(TransactionDescriptors.ALWAYS_NEW);
   try(UserTransaction<JdbcEngine> tx2 = tm.createTransaction()) {
   }
   tx.context().restoreDescriptor(); //restore/remove descriptor modifications
}
```

```Java
Source<Connection> source = new new DataSourceConnectionSource(dataSource);
TransactionManager<JdbcEngine> tm = new JdbcTransactionManager(source);
try(UserTransaction<JdbcEngine> tx = tm.createTransaction()) {
   //even though we create new manager here, inner transaction will be mapped to the same physical one
   //as the outer transaction
   //it happens because we synchronize on the same source and requirement is "NEW_OR_PROPAGATED"
   TransactionManager<JdbcEngine> tm2 = new JdbcTransactionManager(source);
   try(UserTransaction<JdbcEngine> tx2 = tm2.createTransaction()) {
     tx2.commit();// so commit here is doing nothing. Only rollback can be called
   }
}
```