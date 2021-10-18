# Software User Manual

This chapter presents the main interfaces of BIcenter, aiming to help users to navigate through the system.


## Initial views

The login page presented in Figure \@ref(fig:login) allows users to be authenticated using their credentials. This can be connected with the institution LDAP, which provides a central place to store usernames and passwords.

![(#fig:login) Login page.](images/login.png)

After the authentication process, the user is redirected to the application’s home page represented in Figure \@ref(fig:home). This page contains all the institutions that the user belongs to. In this case, this user has permission to create a new instance of an institution in the application using the modal illustrated in Figure \@ref(fig:newinstitution).

![(#fig:home) Home page after login with all institutions assigned to the user.](images/home.png)

![(#fig:newinstitution) Modal to create new institution.](images/newInstitution.png){width=75%}


## Institutional features

The configuration of a data source is illustrated in Figures \@ref(fig:newds1) and \@ref(fig:newds2). Firstly is created the data source instance in the institution, and then, the details of this connection are inserted in the modal represented in Figure \@ref(fig:newds2).

![(#fig:newds1) Creation of connection to a data source (first step).](images/newDataSource1.png){width=50%}

![(#fig:newds2) Modal to insert information regarding the new connection to a data source (second step).](images/newDataSource2.png){width=75%}

The connection to the remote server follows a similar flow. This configuration is illustrated in Figures \@ref(fig:newserver1) and \@ref(fig:newserver2).

![(#fig:newserver1) Creation of connection to a remote server (first step).](images/newServer1.png){width=50%}

![(#fig:newserver2) Modal to insert information regarding the new connection to a remote server (second step).](images/newServer2.png){width=75%}

Finally, the ETL tasks are also created for the institutions. This feature redirects the users to the ETL Task Editor, in which they can design and implement an ETL pipeline using the web interface. The creation of a new task can be done by using the option represented in Figure \@ref(fig:newtask).

![(#fig:newtask) Creation of a new ETL task (first step).](images/newTask.png){width=50%}


## ETL Task Editor

The ETL Task Editor allows users to create an ETL pipeline by dragging and dropping ETL components. Figure \@ref(fig:editor) shows an overview of this web editor.

![(#fig:editor) Overview of the ETL Task Editor.](images/editor.png)

Figure (#fig:example) shows an example of an ETL pipeline implemented in the ETL Task Editor using the four most common ETL components.

![(#fig:example) Simple example of an ETL pipeline represented on this editor](images/example.png)

The components available to implement these ETL pipelines are available on the left menu. Part of this menu is represented in Figure (#fig:components), which is expanded the components for input, output and transformations.

![(#fig:components) Overview of part of the ETL components currently deployed in the system.](images/componentsOverview.png){width=25%}

The navigation bar illustrated in Figure (#fig:navbar) allows the uses to operate over the ETL Task. This menu is divided into sub-menus: 1) step, which contains the features to configure each ETL component in the pipeline and analyse the input and output of each step; 2) execution, which defined if the ETL task will be executed locally or in a remote server; edit, contain the editing usual features, such as cut, copy, paste, delete, undo and redo; and 4) select, which is related with the drawing features.

![(#fig:navbar) Main features of the ETL Task Editor.](images/navBarMenus.png)
