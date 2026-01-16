# Etape 1 : Build de l'application
FROM node:20-alpine as builder

# Copie des fichiers de dépendances
COPY package.json package-lock.json ./
RUN npm install

# Copie du code source
COPY src .

# --- GESTION DES VARIABLES D'ENVIRONNEMENT ---
# On définit des arguments de build (ARG)
ARG VITE_VIDEO_URL
ARG VITE_USER_PROFILE_URL

# On les map vers des variables d'environnement (ENV) pour que Vite les voie
ENV VITE_VIDEO_URL=$VITE_VIDEO_URL
ENV VITE_USER_PROFILE_URL=$VITE_USER_PROFILE_URL
# ---------------------------------------------

# Compilation (génère le dossier /dist)
RUN npm run build

# Etape 2 : Serveur Web (Nginx) pour servir les fichiers statiques
FROM nginx:alpine

# Copie de la config Nginx personnalisée
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Copie des fichiers buildés depuis l'étape précédente
COPY --from=builder /app/dist /usr/share/nginx/html

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]