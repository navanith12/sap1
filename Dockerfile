FROM node:latest as builder
RUN mkdir /angular
WORKDIR /angular
#COPY package.json /angular/package.json

ENV PATH /angular/node_modules/.bin:$PATH

COPY . /angular

EXPOSE 4200

# start app
CMD ["npm", "run", "deploy"]





#FROM node:latest
#WORKDIR /angular
#COPY package.json /angular/package.json
#RUN npm update && npm rebuild node-sass && npm i -g @angular/cli@8.0.0 && ng version && npm install
#RUN . /angular
#CMD ng serve --host 0.0.0.0 --port 4200
