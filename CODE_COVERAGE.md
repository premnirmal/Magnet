# Code Coverage Report generation

To generate the code coverage report, execute the following commands:

Windows:
> gradlew build
> gradlew jacocoTestReport

Linux/Unix/OSX:

> ./gradlew build
> ./gradlew jacocoTestReport

This will generate code coverage report for all the modules, located here:

> PROJECT_ROOT/MODULE/build/reports/jacoco/jacocoTestDebugUnitTestReport/html/index.html

For example:

> `Magnet/library/build/reports/jacoco/jacocoTestDebugUnitTestReport/html/index.html`