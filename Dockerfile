FROM node:21-alpine
WORKDIR /home/poet/docker-pr/sprout
COPY package*.json .
RUN npm install -g nodemon
RUN npm install
COPY . .
ENV SPROUT_ENV=prod
# CMD ["npm", "start"]
CMD ["npm", "run", "dev"]