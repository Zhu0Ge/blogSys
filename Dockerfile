# 构建阶段
FROM node:20-alpine AS build
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
RUN npm run build

# 运行阶段
FROM nginx:alpine
COPY --from=build /app/dist /usr/share/nginx/html

# Nginx 配置：SPA 路由 + API 代理
COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80