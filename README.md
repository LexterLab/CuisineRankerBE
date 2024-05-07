# Cuisine Ranker API

![logo](logo.svg)

# About

**Cuisine Ranker is a culinary web application that is built to spice up the mundane cooking life.**

**It offers a way to learn cooking/recipes with an interactive system of learning, instead of just following recipe guides.**

**You can also use it for storing recipes and sharing them with your friends and family.**

**There is also a competitive side, in which you can participate in different competitions and track your position**
**on different kinds of leaderboards!**

**You can find out more at: https://github.com/users/LexterLab/projects/5?pane=info**

## What is API?

**Cuisine Ranker API is the server side of the web application, which you must have running in order for the website to**
**properly display information and function.**

## Where to find frontend?
**You can find the frontend side of the web application at https://github.com/LexterLab/CuisineRankerFE**

# Dev Environment
**You can also find pre-released features and updates before their release by checking out the develop branch.**

**You can do so by either manually selecting develop branch in your IDE/text editor or via console:**
```bash
git switch develop
```

# Releases

**You can find every release(version) of the API by going to:  https://github.com/LexterLab/CuisineRankerBE/releases**

**The latest release will always have the "Latest" tag!**

# How to Run

## Prerequisites
**Currently, Cuisine Ranker API can only be run with Docker.**
+ Docker https://www.docker.com/ - Need to have docker running to run commands

### Docker Installation Tutorials:
+ For Mac Users: https://www.youtube.com/watch?v=gcacQ29AjOo
+ For Windows 11: https://www.youtube.com/watch?v=AAWNQ2wDVAg
+ For Windows 10:https://www.youtube.com/watch?v=5nX8U8Fz5S0
+ For Linux/Ubuntu: https://www.youtube.com/watch?v=TDLKQWsrSyk

## Setup backend environment
**0. Open cmd with "Run as administrator"**


**1. Clone the repository**

```bash
git clone https://github.com/aparpEdu/CuisineRankerBE.git
```

**2.Navigate to project directory**
```bash
cd <path to project>
```
replace < path to project > with the path where you cloned the repository



**3. After navigating to the project directory, run the backend with docker script**

In Command Prompt

For Windows:
```bash
docker-script.bat
```

For MacOS/Linux:
```bash
./docker-sh.sh
```

Or in Intellij/ Or any other IDE/Editor

Windows:
```bash
./docker-script.bat
```

MacOS/Linux:
```bash
./docker-sh.sh
```


REST API will start running at <http://localhost:443>

**4. You can stop the backend from running with**

```bash
docker compose down
```
## Setup properties


**1. Navigate to resources directory: "src/main/resources"**

**2. Setup gcp-config.json**

Replace the values in the curly brackets with your own google cloud credentials you can find on 
https://console.cloud.google.com/

```json
{
  "type": "service_account",
  "project_id": "{google_cloud_project_id}",
  "private_key_id": "{private_key_id}",
  "private_key": "{private_key}",
  "client_email": "{client_email}",
  "client_id": "{client_id}",
  "auth_uri": "https://accounts.google.com/o/oauth2/auth",
  "token_uri": "https://oauth2.googleapis.com/token",
  "auth_provider_x509_cert_url": "https://www.googleapis.com/oauth2/v1/certs",
  "client_x509_cert_url": "{cert_url}",
  "universe_domain": "googleapis.com"
}
```


**3. Setup google cloud properties in *"application.docker.properties"***

Replace values with your own google cloud credentials

```properties
spring.cloud.gcp.project-id=cuisine-ranker
gcp.bucket.id=cuisine-media
```

## Database Management
You can manage your database via 2 ways:

**1. Managing your databse through docker postgreSQL image command line:**

```bash
 psql -U postgres -d mydb
```

**2. Or through pgAdmin docker image, by visiting in your browser: http://localhost:5050/browser/**

## SMTP

SMTP image is already setup and to see sent emails, you can just use maildev, by visiting in your browser: http://localhost:1080/#/

## Endpoints
+ http://localhost:443/swagger-ui/index.html - Every endpoint is documented here.

![swagger](https://i.ibb.co/pRCtGHw/swagger.jpg)