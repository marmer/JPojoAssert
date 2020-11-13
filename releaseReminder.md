Release Step reminder (until fully automated)
=============================================
1. Update Readme 
1. Release Notes
1. set version to release version
    ```
    mvn versions:set
    mvn versions:commit
   ```
1. Create Tag for Version
1. Commit and push
1. Wait for a green build
1. deploy (make sure not to deploy test modules)
    ```
   mvn deploy
   ```
1. set version to next development version
    ```
    mvn versions:set
    mvn versions:commit
   ```
