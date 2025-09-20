# The base imaege with Node.js and npm
FROM node:20-alpine AS build

# Set the working directory inside the container
WORKDIR /app


COPY package*.json ./

# Install dependencies
RUN npm ci

# Copying the rest
COPY . .

# Build the React application
RUN npm run build


FROM nginx:alpine

# Copy built files from build stage to nginx
COPY --from=build /app/dist /usr/share/nginx/html

# Expose port 80 (nginx default)
EXPOSE 80

# Start nginx
CMD ["nginx", "-g", "daemon off;"]