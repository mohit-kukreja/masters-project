# LightSIDE Web-APP

This project leverages the [LightSIDE workbench](https://github.com/LightSideWorkbench/LightSide)

## Follow the steps below to run the project

- Pull the official Github Repository from [LightSIDE workbench](https://github.com/LightSideWorkbench/LightSide)
```
git pull https://github.com/LightSideWorkbench/LightSide
```
- Pull this project's repository
```
git pull https://github.com/mohit-kukreja/masters-project.git
```
- Copy and replace the `src/` folder, `run.sh` and `build.xml` from this project to the official LightSIDE project directory.
- Change directory to the official lightSIDE project and perform `ant build` to start the backend Server of LightSIDE, which is started at http://localhost:8000
- Change directory to the `client/lightside-webapp` and fire `ng serve` to start the client web-app.

Access the web-app at http://localhost:4200
