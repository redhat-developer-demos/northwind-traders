#!/usr/bin/env node
/* eslint-disable no-console */

const express = require('express');
const app = express();

app.use(express.static(`${__dirname}/build`)); // Serves resources from public folder

app.listen(8080);
console.log('Server started, http://localhost:80/');
