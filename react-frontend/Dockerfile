FROM registry.access.redhat.com/ubi9/nodejs-18:latest as build
WORKDIR /opt/app-root/src
ENV PATH /opt/app-root/src/node_modules/.bin:$PATH
COPY --chown=1001 maven/ ./
RUN npm install                   && \
    npm run build

FROM registry.access.redhat.com/ubi9/nodejs-18:latest as run
WORKDIR /opt/app-root/src
COPY --chown=1001 --from=build /opt/app-root/src/build /opt/app-root/src/build
COPY --chown=1001 --from=build /opt/app-root/src/index.js /opt/app-root/src/index.js
RUN npm install -g express express-http-proxy
ENV NODE_PATH /opt/app-root/src/.npm-global/lib/node_modules
EXPOSE 8080
CMD ["node", "index.js"]

# TODO nginx - Problem with multistage builds and single from
#      https://docs.openshift.com/container-platform/4.11/cicd/builds/build-strategies.html
#FROM nginx:1.23-alpine as run
#COPY --from=build /app/build /usr/share/nginx/html/
#EXPOSE 80
#CMD ["nginx", "-g", "daemon off;"]
