FROM node:22-alpine AS build
WORKDIR /app
COPY package*.json ./
RUN npm ci --ignore-scripts
COPY . .
RUN npm run check && npm run build

FROM node:22-alpine
WORKDIR /app
COPY --from=build --chown=node:node /app/build ./build
COPY --from=build --chown=node:node /app/package*.json ./
RUN npm ci --omit=dev --ignore-scripts
USER node
ENV HOST=0.0.0.0 PORT=3001 NODE_ENV=production
EXPOSE 3001
CMD ["node", "build"]
