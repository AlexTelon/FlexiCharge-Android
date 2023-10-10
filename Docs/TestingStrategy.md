# Testing Documentation
## Introduction

This documentation provides an overview of the testing strategy and practices used in this
Android project. It covers the structure of the testing codebase, types of tests, and the
integration of GitHub Actions for automated testing.

## Types of Tests in an Android application

### Unit Tests
Involves testing small individual units of code such as methods or classes, to verify their
behavior and ensure that they meet the expected functionality.

### Integration Tests
Larger tests that check how various components, activities, services and the interaction
between them have expected behavior. It ensures that different parts of the application work
together as expected.

### End-to-End Test
Not yet implemented
### UI Tests (Instrumentation Tests)
Not yet implemented

## Testing Codebase Structure

### Test Folder
- **Purpose:**
The `test` folder should contain unit tests for the `FlexiCharge` Android application.
These tests are designed to verify the correctness of individual code units. Unit tests ensure
that the business logic operate as intended, promoting code reliability and maintainability.

- **Contents:** 
This folder contains integration tests and should in the future be moved to the `androidTest`folder.

## Test Structure : AAA Pattern
Test should be structured according to the AAA Pattern. The three A:s stands for arrange, act
and assert. 

Arrange: This section involves setting up the initial conditions for the test. Such as creating
objects, initiating variables, and preparing the environment. 

Act: Where the actual action is performed. The behavior that's being tested is triggered.

Assert: Check the outcomes or results of the action to determine if they match the expected 
behavior. Section where test assertions are placed.


### Android Test Folder

- **Purpose:**
The `androidTest` folder contains instrumentation tests and are designed to interact with the
Android framework and components of the Android application.
These tests are used to verify how different components of the app work together on an actual
Android device or emulator. Instrumentation tests typically test scenarios involving activities,
views, and user interactions. Tests in this folder executes slower but provides a more
realistic testing of the behavior.

- **Contents:**
The `androidTest` folder contains a series of files that include:

  - instrumentation test that aims to verify behaviour of functions sending HTTP requests.
    These files contains test classes and methods responsible for ensuring the proper functionality
    and behavior of the functions.
  
  - instrumentation test that aims to verify the behavior and functionality of different activities 
    and views. 


## Testing Libraries

### JUnit
The JUnit testing framework for is used for parts in test development. The AndroidJUnit4 class
serves as the chosen test runner, with tests being denoted by the `@RunWith(AndroidJUnit4::class)`
annotation preceding the test function name.

### MockWebserver 
A scriptable web server for testing HTTP clients. The `mockWebServer` simulate responses from 
a web server in tests.
[Example](https://android.googlesource.com/platform/external/okhttp/+/master/mockwebserver/README.md)


## GitHub Actions and Automated Testing

### GitHub Actions 
- **Purpose:**
GitHub Actions is integrated into the `Flexi Charge Android` repository and aims to optimize the
development process by automating tasks such as project building and running tests. This enhances
code quality, and facilitate efficient testing.
GitHub Actions is an integrated continuous integration (CI) automation platform provided by GitHub. 
It provides support for running automated tests and checks code at defined points in your
development workflow. 

- **Configuration:**
The `GitHub Actions workflow` for the application is defined in a YAML file
located in the GitHub Actions directory within the `Flexi Charge Android` repository. The workflow 
is triggered based on the following events:

- Pushes to Development Branch: 

  Whenever changes are pushed to the development branch of the repository, the GitHub Actions
  workflow is initiated. This ensures that code changes made to the development branch are 
  automatically tested and validated.

- Pull Requests to Development Branch: 
  The workflow is also triggered when pull requests are created against the development branch.
  This allows assessing of proposed changes and running tests before merging pull requests
  into the development branch.




