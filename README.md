EclipseCon 2022
===============

![A diagram of the project's architecture](./diagram.png)

## Deploy the application

To easily deploy the required services, this project provides a JBang script that will deploy the application to OpenShift.

If you're running from an SH compatible terminal, you can run the following command:
```shell
./deploy-to-openshift.sh
```

From any other platform, you can run the following commands:

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

### Order Faker

Module: [order-faker](./order-faker)

Fake orders and send them to the Northwind application. A new order will be sent every ~6 seconds.

This application could represent a mobile-application sending requests to the Northwind REST API. 

## Contributing

### Northwind

#### Building the Frontend

The frontend uses ES Modules in the browser, so it doesn't need a transpilation process.
However, the npm modules/libraries it relies on need to be packaged into a single file so that they can be consumed.
You can perform this step by running the following command:
```shell
cd northwind/tools
node create-bundle.js
```
The generated files should be persisted in the VCS, or generated before the application is executed/packaged.

