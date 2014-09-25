/*
 * Copyright 2013 Bersenev Dmitry molasdin@outlook.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

                        ##########################################################
                        ############******Spring configuration*******#############
                        ##########################################################

<bean id="messages" class="org.molasdin.wbase.jsf.spring.messages.JSFMessagesFactoryBean"/>

<bean class="org.springframework.beans.factory.config.CustomScopeConfigurer">
        <property name="scopes">
            <map>
                <entry key="view">
                    <bean class="org.molasdin.wbase.jsf.spring.scope.JsfViewScope"/>
                </entry>
                <entry key="flow">
                    <bean class="org.molasdin.wbase.jsf.spring.scope.JsfFlowScope"/>
                </entry>
            </map>
        </property>
</bean>

<bean id="ormSearchResult" class="org.molasdin.wbase.hibernate.result.BasicOrmCursor" scope="prototype">
        <property name="sessionFactory" ref="sessionFactory"/>
</bean>

<bean id="querySearchResult" class="org.molasdin.wbase.hibernate.result.BasicOrmQueryCursor" scope="prototype">
        <property name="sessionFactory" ref="sessionFactory"/>
</bean>

<bean id="filteredOrmSearchResult" class="org.molasdin.wbase.hibernate.result.BasicFilteredOrmCursor" scope="prototype">
        <property name="sessionFactory" ref="sessionFactory"/>
</bean>

<bean id="searchResultFactory" class="org.molasdin.wbase.hibernate.BasicOrmSearchResultFactory">
        <property name="searchResultName" value="ormSearchResult"/>
        <property name="querySearchResultName" value="querySearchResult"/>
        <property name="filteredSearchResultName" value="filteredOrmSearchResult"/>
</bean>

<bean id="commonDao" class="org.molasdin.wbase.hibernate.BasicOrmSupport">
        <property name="sessionFactory" ref="sessionFactory"/>
</bean>

<bean id="simpleRepo" class="org.molasdin.wbase.hibernate.BasicOrmRepository">
       <constructor-arg value="com.mycompany.MyDomainClass"/>
       <property name="support" ref="commonDao"/>
</bean>


========================================================================================================================
========================================================================================================================
========================================================================================================================

                        ##########################################################
                        ############******Deployment Descriptor*******############
                        ##########################################################

<web-app xmlns="http://java.sun.com/xml/ns/javaee"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://java.sun.com/xml/ns/javaee
		  http://java.sun.com/xml/ns/javaee/web-app_3_0.xsd"
         version="3.0" metadata-complete="true">

<filter>
        <filter-name>hibernateFilter</filter-name>
        <filter-class>org.springframework.orm.hibernate4.support.OpenSessionInViewFilter</filter-class>
</filter>

<filter-mapping>
        <filter-name>hibernateFilter</filter-name>
        <url-pattern>/*</url-pattern>
</filter-mapping>

<listener>
        <listener-class>org.springframework.web.context.ContextLoaderListener</listener-class>
</listener>

<listener>
        <listener-class>org.springframework.web.context.request.RequestContextListener</listener-class>
</listener>

<listener>
        <listener-class>org.molasdin.wbase.jsf.listeners.ApplyPropertiesListener</listener-class>
</listener>

<context-param>
        <param-name>javax.faces.DEFAULT_SUFFIX</param-name>
        <param-value>.xhtml</param-value>
</context-param>

<context-param>
        <param-name>javax.faces.INTERPRET_EMPTY_STRING_SUBMITTED_VALUES_AS_NULL</param-name>
        <param-value>true</param-value>
</context-param>

<servlet>
        <servlet-name>Faces Servlet</servlet-name>
        <servlet-class>javax.faces.webapp.FacesServlet</servlet-class>
        <load-on-startup>1</load-on-startup>
    </servlet>

<servlet-mapping>
        <servlet-name>Faces Servlet</servlet-name>
        <url-pattern>/faces/*</url-pattern>
</servlet-mapping>

<servlet-mapping>
        <servlet-name>Faces Servlet</servlet-name>
        <url-pattern>*.html</url-pattern>
</servlet-mapping>

<context-param>
        <param-name>contextConfigLocation</param-name>
        <param-value>/WEB-INF/application.xml</param-value>
</context-param>

</web-app>

========================================================================================================================
========================================================================================================================
========================================================================================================================

<?xml version='1.0' encoding='UTF-8'?>
<faces-config version="2.2" xmlns="http://xmlns.jcp.org/xml/ns/javaee"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/javaee
    http://xmlns.jcp.org/xml/ns/javaee/web-facesconfig_2_2.xsd">

</faces-config>

========================================================================================================================
========================================================================================================================
========================================================================================================================

                        ##########################################################
                        #########******Facelet namespace example*******###########
                        ##########################################################


<html xmlns="http://www.w3.org/1999/xhtml"
      xmlns:h="http://xmlns.jcp.org/jsf/html"
      xmlns:f="http://xmlns.jcp.org/jsf/core"
      xmlns:ui="http://xmlns.jcp.org/jsf/facelets"
      xmlns:pf="http://primefaces.org/ui"/>


========================================================================================================================
========================================================================================================================
========================================================================================================================

                        ##########################################################
                        ###############******Index page*******####################
                        ##########################################################

<jsp:root xmlns:jsp="http://java.sun.com/JSP/Page" xmlns="http://www.w3.org/1999/xhtml" version="2.0">
    <jsp:directive.page contentType="text/html; ISO-8859-1"/>
    <jsp:forward page="/html/main.html"/>
</jsp:root>