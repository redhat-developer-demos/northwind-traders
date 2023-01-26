#!/usr/bin/env node
/* eslint-disable no-console */

const express = require('express');
const url = require('url');
const proxy = require('express-http-proxy');

const app = express();

app.use(express.static(`${__dirname}/build`));

const apiProxy = proxy('http://northwind:8080', {
  proxyReqPathResolver: req => req.originalUrl
});
app.use('/api/*', apiProxy);

app.listen(8080);
console.log('Server started, http://localhost:8080/');
