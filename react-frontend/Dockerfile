FROM node:18-alpine as build
WORKDIR /app
ENV PATH /app/node_modules/.bin:$PATH
COPY maven/ ./
RUN npm install                   && \
    npm run build

FROM node:18-alpine as run
WORKDIR /app
COPY --from=build /app/build /app/build
COPY --from=build /app/index.js /app/index.js
RUN npm install -g express
ENV NODE_PATH /usr/local/lib/node_modules
EXPOSE 8080
CMD ["node", "index.js"]

# TODO nginx - Problem with multistage builds and single from
#      https://docs.openshift.com/container-platform/4.11/cicd/builds/build-strategies.html
#FROM nginx:1.23-alpine as run
#COPY --from=build /app/build /usr/share/nginx/html/
#EXPOSE 80
#CMD ["nginx", "-g", "daemon off;"]
