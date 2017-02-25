# TODO App

Democracy Works code exercise submission

## Running in dev

Run `lein figwheel`.

Then navigate to `http://localhost:3449/` in your web browser.

Some example TODOs are pre-loaded in dev.

### Running tests

Run `lein test`.

## Deploying

Run `lein uberjar`. The compiled jar file will be in `target/dw-todo-exercise.jar`.

Then you can deploy to Heroku (for example) by creating an app in your Heroku
account and pushing the git repo to it: `git push heroku master`.

Or you can run the app in your deployment environment of choice by running the uberjar:
`java -jar dw-todo-exercise.jar`.

You can configure the port it runs on via the `PORT` env var. The default is 3000.