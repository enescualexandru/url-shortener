# URL SHORTENER

Sample project for learning Kotlin. Stack used: Kotlin, Spring Boot, Thymeleaf, Postgresql, Maven,
Docker with docker-compose
---

## Functional requirements

- an application user should be able to input a URL and to be provided with a shorter version of it (an "alias")
- he should be able to copy that shorter link and share it anywhere on the web
- when a user hits that link, he should be redirected to the original url
- the shortened link should expire after a certain amount of time, after which is no longer valid
- registered application users will benefit all the above features, and additionally, will be able to set a preferential
  expiration time for their links and to see their history of urls(shortened ones and their correspondence); they can
  delete the shorter links as well

---

## Technical requirements

- shortened links should not be predictable
- different input urls having the same alias must be avoided
- two separate requests with the same input url must not return same alias links; they should be different - even for
  the same user(in case of registered users)
- it is expected that the frequency of accessing the short links will be much higher than converting the long urls, thus
  a caching mechanism should be implemented

---

## Implementation details - what we have so far

- the urls are hashed based on their db id, using an external library(https://hashids.org/). This will ensure
  theoretically billions of entries without collisions
- the input urls are validated using an external library, found in apache commons packages
  (http://commons.apache.org/proper/commons-validator/apidocs/org/apache/commons/validator/routines/UrlValidator.html)
- we are allowing a user to input an url(uri's, in the broader sense, like eg. `mailto:John.Doe@example.com` are not valid urls, thus not valid)
without a schema(normally, rendering an invalid one), by prepending to it the default schema(http).
Meaning that for eg. `google.com` will be accepted, while `$#@le.com` will not.
- when making a convert url request, the user will be provided with the shorter version, and he is able to use it
- we are using a server-side Java template engine, namely Thymeleaf, for easy and enjoyable testing(within the scope of
  this app). The application can be easily converted to a Rest API.
- for unit testing, we are using an in memory db - h2

---

## TODO's

- add security, allow user registration and login, implement the features described for the registered users
- add cashing mechanism(using an external provider, maybe `memached`)
- add integration testing(for our controllers)
- deploy the app to a PaaS(maybe `Heroku`)

---

## Prerequisites for running the app

- Git
- Jdk 11
- Maven
- Docker with docker-compose

---

## Running the app

- clone the repository at https://github.com/enescualexandru/url-shortener.git
- `mvn clean install`
- `docker-compose up --build`
- open any browser and go to `localhost` or `localhost:8080`

**NOTE:** two containers are started, one being the postgresql db and the other one, our spring boot app. The app's jvm
is started on debug mode, the debug port is `5005`. The postgres db can be reached at port `5432`, with the dbname, user
and password specified in the docker-compose file.
