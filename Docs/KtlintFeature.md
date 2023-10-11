# Ktlint Documentation

## Introduction

This documentation provides basic knowledge on what Ktlint is and how it works.

### Purpose and Benefits

Ktlint serves the following purposes in the project:

1. **Code Consistency:** Enforces consistent coding styles across the project, which makes the codebase more readable.

2. **Automated Code Review:** Facilitates automated code review by highlighting style violations, making it easier to catch and address issues early in the development process.

3. **Team Collaboration:** Establishes a shared code style among team members, reducing friction and enhancing collaboration.

4. **Build Process Integration:** Seamlessly integrates into your project's build process, allowing for automated code style checks and formatting each time you build and run the application.

## Integration

Since Ktlint is already implemented, we will go over how it works.

1. **The Ktlint plugin inside project level build.gradle**
    - Include the Ktlint Gradle plugin in your project's `build.gradle` file:

      ```groovy
      plugins {
         id 'org.jlleitschuh.gradle.ktlint' version '11.3.1'
      }
      ```

    - Replace "11.3.1" with another version of the Ktlint Gradle plugin in the future if needed.

2. **The Ktlint plugin inside module (app) level build.grade file**
    - Include the Ktlint Gradle plugin to your Kotlin modules:

      ```groovy
      plugins {
         id 'com.android.application'
         id 'org.jetbrains.kotlin.android'
         id 'org.jlleitschuh.gradle.ktlint' version '11.3.1'
      }
      ```

3. **The Ktlint configuration**
    - The Ktlint configuration can be seen down below and can be modified according to your code current code standard. It is however not advised to remove anything from **disabledRules** since Ktlint cannot auto-correct those errors.

      ```groovy
      ktlint {
         android = true
         ignoreFailures = false
         disabledRules = [
                 'final-newline',
                 'no-wildcard-imports',
                 'max-line-length'
         ]
         reporters {
             reporter 'plain'
             reporter 'checkstyle'
             reporter 'sarif'
         }
       }
      ```

## Usage

### Running Ktlint

**Auto-checks and Auto-formatting the code**

Ktlint is currently set-up in a way such that it will Auto-check and Auto-format all the existing code when you either build or run the application.
If you however want to manually do checks or format you can follow these steps:


1. **Check for Violations:**

   To automatically check for code style violations:
    1. Open the terminal inside Android Studio
    2. Run the following command:

     ```bash
     ./gradlew ktlintCheck
     ```

This task will report any style violations found in the project.

2. **Auto-formatting Code:**

   To automatically format the code according to the defined style rules:
    1. Open the terminal inside Android Studio
    2. Run the following command:
     ```bash
     ./gradlew ktlintFormat
     ```
This task modifies the code in-place to comply with the specified code style.

3. **The preBuild task**

The line ```tasks.getByPath('preBuild').dependsOn('ktlintFormat')``` inside the Module (app) level ```build.gradle``` file specifies that Ktlint will check and format all the code when you build or run the application.
If any major code standard violations are present your build will fail.


### Configuration

#### Default Configuration

Ktlint uses sensible default rules, but you can customize them as needed. To view the default rules, refer to the [official documentation](https://github.com/pinterest/ktlint#standard-ktlint-ruleset).

#### Custom Configuration
1. **Modify rules by configuring `build.gradle` at both Project and Module (app) levels:**

It is not advised to remove any of the current rules since they cannot be auto-formatted. But if you want to enforce the rules anyway you can remove them from the 'disabledRules' array.

   ```groovy
   ktlint {
        disabledRules = [
            'final-newline',
            'no-wildcard-imports',
            'max-line-length'
        ]
   }
```
---

This documentation covers the essential aspects of integrating and using Ktlint in your project. If you encounter any issues or have additional questions, refer to the [official Ktlint GitHub repository](https://github.com/pinterest/ktlint) or seek assistance from your project's development community.
