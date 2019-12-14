# OpenHome instructions


There are 3 templates for building and deploying. When you want to switch between them just copy the contents to `application.yml` and `constants/index.js`
```
- application_build_dev.yml and index_build_dev.js (for development, this is the default)
- application_build_test.yml and index_build_test.js (for compiling combined jar and running it locally)
- application_build_prod.yml and index_build_prod.js (for compiling combined jar for production)
```

## Building and Running Locally each end (for development)

### To run the backend server from CLI
```
cd openhome-backend

mvn spring-boot:run
```


### To run the backend server from an IDE (Eclipse) (for development)
```
import openhome-backend project
configure the Maven Build Goal to 'spring-boot:run'
```
https://books.sonatype.com/m2eclipse-book/reference/running-sect-running-maven-builds.html

### to run the frontend
```
cd openhome-frontend

npm install

npm start
```

## Building a combined jar

### Copy the contents for the test or prod template to 'application.yml' and 'constants/index.js'
```
cp openhome-backend/src/main/resources/application_build_test.yml openhome-backend/src/main/resources/application.yml
cp openhome-frontend/src/component/constants/index_build_test.js openhome-frontend/src/component/constants/index.js

OR

cp openhome-backend/src/main/resources/application_build_prod.yml openhome-backend/src/main/resources/application.yml
cp openhome-frontend/src/component/constants/index_build_prod.js openhome-frontend/src/component/constants/index.js
```

### Use mvn to build combined jar
```
cd openhome-backend

mvn clean install  <--- this will create a jar under openhome-backend/target
```

### Use java to run combined jar
```
java -jar target/openhome-0.0.1-SNAPSHOT.jar
```
