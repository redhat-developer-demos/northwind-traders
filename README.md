EclipseCon 2022
===============

![A diagram of the project's architecture](./diagram.png)

## Deploy the application

```shell
cd prod
jbang PrepareEnvironment.java
cd ..
mvn clean package oc:build oc:resource oc:apply
```

## Services

### Northwind traders

Module: [northwind](./northwind)

Web application for the renowned Northwind traders database.


### Mail

Module: [mail](./mail)

Sends email notifications to Mailinator.

This application is a port of a previous iteration used for Barcelona JUG presentation in 2020.

https://www.mailinator.com/v4/public/inboxes.jsp?to=jkube


## Contributing

## Northwind

### Building the Frontend

The frontend uses ES Modules in the browser, so it doesn't need any transpilation process.
However, the npm modules/libraries it relies on need to be packaged into a single file so that they can be consumed.
You can perform this step by running the following command:
```shell
cd northwind/tools
node create-bundle.js
```

