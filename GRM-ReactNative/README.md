# Github Repository List
Sample project developed with the basic ReactNative/Redux stack.

## Running the Project
Once downloaded, you have to get to the project's root folder (The same where this file lives) and run the following command to install all the required dependences: 
```shell 
npm install 
``` 
 Then, just run the expo client in the same folder and wait for the browser to popup giving you instructions to preoceed with the startup:
 ```shell
expo start
 ```


***Note: You need to have nodejs 8+ installed alongside with the expo-cli.***


## Project Structure
* ./App.js - The main Component
* ./src - Root folder containing the code.
* ./src/constants - Folder containing constants variables/functions for messaging, configurations and the like
* ./src/data-source - Folder containing functions to retrieve data from IO sources
* ./src/styles - Global styles
* ./src/domain - Folder containing domain related components/pages.


## File Prefixes
* cns - Constants
* dmb - Dumb Components 
* smt - Smart Components
* rdx - Redux related functions. Reducers, Actions, etc.
* rep - Data Repository


## Architecture
This project uses the classic Dumb/Smart components to handle Presentation/Data Manipulation. Dumb components should receive all data via props and report interactions via callbacks. Smart components should handle the data retrieval/modification using repositories, dispatching actions and fetching data from redux. Repositories aggregates multiple data sources and produces a standardized output.
