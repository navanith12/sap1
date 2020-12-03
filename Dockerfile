FROM node:latest
WORKDIR /angular
COPY package.json /angular/package.json
RUN npm update && npm rebuild node-sass && npm i -g @angular/cli@8.0.0 && ng version && npm install
RUN . /angular
CMD ng serve --host 0.0.0.0 --port 4200
