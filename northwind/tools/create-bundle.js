#!/usr/bin/env node
/**
 * Creates the required libraries bundle for the application
 */
const fs = require('fs');
const path = require('path');
const {execSync} = require('child_process');

const buildDir = path.resolve(__dirname, '..', 'build');
const packageJson = path.resolve(buildDir, 'package.json');
const bundleCjs = path.resolve(buildDir, 'bundle.cjs.js');
const webpack = path.resolve(buildDir, 'webpack.js');
const target = path.resolve(__dirname, '..', 'src', 'main', 'resources', 'META-INF', 'resources', 'lib');

const clean = () => {
  if (fs.statSync(buildDir, {throwIfNoEntry: false})) {
    fs.rmSync(buildDir, {recursive: true, force: true});
  }
};

clean();
fs.mkdirSync(buildDir);

fs.writeFileSync(packageJson, JSON.stringify({
  name: 'temp-package',
  dependencies: {
    '@reduxjs/toolkit': '1.8.6',
    htm: '3.1.1',
    react: '18.2.0',
    'react-dom': '18.2.0',
    'react-redux': '8.0.4',
    webpack: '5.74.0'
  }
}));

execSync('npm install', {
  cwd: buildDir,
  stdio: 'inherit'
});

fs.writeFileSync(bundleCjs, `
  const React = require('react');
  const htm = require('htm').default;
  const {createRoot} = require('react-dom/client');
  const {Provider} = require('react-redux');
  const {combineReducers, configureStore} = require('@reduxjs/toolkit');
  const {createApi, fetchBaseQuery} = require('@reduxjs/toolkit/query/react');
  const html = htm.bind(React.createElement);
  window.lib = {
    React, htm, html, createRoot, Provider, combineReducers, configureStore, createApi, fetchBaseQuery
  }
`);

fs.writeFileSync(webpack, `
  const webpack = require('webpack');
  webpack({
    entry: {'bundle': '${bundleCjs}'},
    output: {
      filename: '[name].js',
      path: '${target}',
    },
    mode: 'development',
    target: 'web',
    devtool: false,
  }).run((err, stats) => {
    if (err) {
      stats.compilation.errors.forEach(error => console.error(error));
    } else {
      console.log(stats.toString({colors: true}));
    }
  });
`);

execSync('node webpack.js', {
  cwd: buildDir,
  stdio: 'inherit'
});

clean();
