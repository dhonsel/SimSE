# SimSE - Simulation of Software Evolution
Agent-based simulation model to simulate software evolution. Repast Simphony is used for the implementation. A detailed description of the model, the parameters and their extraction can be found in \[1\]. A general workflow how all tools work together can be found [here](https://github.com/dhonsel/SimSE/blob/main/docs/workflow.md).

## Requirements
To build the model or to develop it further [Repast Simphony](https://repast.github.io/) (version 2.7) is required. For the development and execution of the simulation model Java 11 or newer is also required. 

## Parameters
We have different parameter types. On the one hand, we have project parameters originating from mining that cannot be changed at runtime. On the other hand, we have parameters that can be changed at runtime to compare different simulation runs.

### Core parameters
The core parameters to initialize the simulation model are generated for each project by our automated [parameter estimation tool](https://github.com/dhonsel/SimParameter). The basic data for each project are the maximum size of the project, the number and change probabilities of commits, the number of rounds to simulate, and the developers (identities) to instantiate with their role specific data. Furthermore, information about bugs, their fixes, and the categories of a project are available. The code data for each project is contained in a JSON file. As an example, a part of the parameters for the open source project [Directory Fortress Core](https://github.com/apache/directory-fortress-core) is shown below.

    {
      "maxFiles": 828,
      "numberOfAverageCommits": 1609,
      "pAverageCommitUpdate": 0.1893163901635486,
      "pAverageCommitDelete": 0.9716183574879227,
      "pAverageCommitAdd": 0.7844953681131155,
      "numberOfInitialCommits": 542,
      "pInitialCommitUpdate": 0.13415841584158417,
      "pInitialCommitDelete": 0.9409722222222222,
      "pInitialCommitAdd": 0.6062639821029085,
      "numberOfDevelopmentCommits": 1067,
      "pDevelopmentCommitUpdate": 0.23929132092397395,
      "pDevelopmentCommitDelete": 0.987962962962963,
      "pDevelopmentCommitAdd": 0.9222126188418324,
      "firstCommitDate": 1315765690000,
      "lastCommitDate": 1552862012000,
      "monthToSimulate": 91,
      "roundsToSimulate": 2745,
      "initialCommits": 550,
      "keyDeveloper": 1,
      "keyDeveloperCommits": 1238,
      "keyDeveloperFixes": 200,
      "keyDeveloperMaintainer": 1,
      "majorDeveloper": 2,
      "majorDeveloperCommits": 352,
      "majorDeveloperFixes": 6,
      "majorDeveloperMaintainer": 0,
      "minorDeveloper": 7,
      "minorDeveloperCommits": 27,
      "minorDeveloperFixes": 1,
      "minorDeveloperMaintainer": 0,
      "peripheralDeveloper": 8,
      "peripheralDeveloperCommits": 135,
      "peripheralDeveloperFixes": 5,
      "peripheralDeveloperMaintainer": 0,
      "coreDeveloper": 2,
      "coreDeveloperCommits": 1482,
      "coreDeveloperFixes": 202,
      "coreDeveloperMaintainer": 1,
      "issueInformationComplete": {
        "NONE": 0,
        "MAJOR": 230,
        "CRITICAL": 9,
        "MINOR": 26
      },
      "issueInformationCompleteFixed": {
        "NONE": 0,
        "MAJOR": 181,
        "CRITICAL": 9,
        "MINOR": 22
      },
      "issueInformationYearly": {
        "2017": {
          "NONE": 0,
          "MAJOR": 23,
          "CRITICAL": 0,
          "MINOR": 6
        },
        "2018": {
          "NONE": 0,
          "MAJOR": 26,
          "CRITICAL": 0,
          "MINOR": 2
        },
        "2019": {
          "NONE": 0,
          "MAJOR": 7,
          "CRITICAL": 0,
          "MINOR": 1
        }
      },
      "exportPackages": [
        {
          "name": "org.apache.directory.fortress",
          "files": 324,
          "percent": 52.42718446601942
        },
        {
          "name": "us.jts.fortress.rbac",
          "files": 98,
          "percent": 15.857605177993527
        },
        {
          "name": "com.jts.fortress.rbac",
          "files": 82,
          "percent": 13.268608414239482
        },
        {
          "name": "com.jts.fortress",
          "files": 49,
          "percent": 7.9288025889967635
        }
      ],
      "identities": [
        {
          "objectID": "9c9675b1-900a-40df-86d3-7d02497f7817",
          "name": "Developer 01",
          "numberOfCommits": 1238,
          "percent": 76.56153370439083,
          "type": "key",
          "role": "core",
          "maintainer": true,
          "numberOfFixes": 200,
          "numberOfTests": 0,
          "numberOfFeatures": 0,
          "numberOfMaintenance": 0,
          "numberOfRefactorings": 0,
          "numberOfDocumentation": 0
        },
        {
          "objectID": "a6bf25c1-b70c-475b-a041-92c9f1131f7c",
          "name": "Developer 02",
          "numberOfCommits": 244,
          "percent": 15.089672232529376,
          "type": "major",
          "role": "core",
          "maintainer": false,
          "numberOfFixes": 2,
          "numberOfTests": 0,
          "numberOfFeatures": 0,
          "numberOfMaintenance": 0,
          "numberOfRefactorings": 0,
          "numberOfDocumentation": 0
        },
        {
          "objectID": "919d2532-2ff7-4487-9cdc-76535b9225b7",
          "name": "Developer 03",
          "numberOfCommits": 108,
          "percent": 6.679035250463822,
          "type": "major",
          "role": "peripheral",
          "maintainer": false,
          "numberOfFixes": 4,
          "numberOfTests": 0,
          "numberOfFeatures": 0,
          "numberOfMaintenance": 0,
          "numberOfRefactorings": 0,
          "numberOfDocumentation": 0
        },
        {
          "objectID": "702f71e3-620d-44f0-815e-6d941405b685",
          "name": "Developer 04",
          "numberOfCommits": 16,
          "percent": 0.989486703772418,
          "type": "minor",
          "role": "peripheral",
          "maintainer": false,
          "numberOfFixes": 1,
          "numberOfTests": 0,
          "numberOfFeatures": 0,
          "numberOfMaintenance": 0,
          "numberOfRefactorings": 0,
          "numberOfDocumentation": 0
        }
      ]
    }

### Change Coupling Graph
The change coupling graph is also generated by our automated parameter estimation tool and is stored using the dot format. The nodes represent the files of the software and the edges with their weights represent how often files are changed together in one commit. To initialize the simulation, the nodes contain additional information like the owner, the creator, all developers who touched the file and how often they touched it, and the package the file belongs to. By default, it is generated for each year. This information is used to start the simulation at a given point in time.

### Runtime Parameters
Repast Simphony allows us to define parameters that can be changed in the running application before each simulation run. The current available parameters are depicted in the following figure.

![Simulation parameters at runtime](docs/runningSimulationParameter.png)

**Project Name**: The name of the project to simulate. According to the name, the configuration files with mined parameters are read.

**Start Year**: The starting point of the simulation. If the year is set to 0, then the simulation starts with an empty change coupling graph. Otherwise, the simulation is initialized with the change coupling graph of the given year. For this the mined graph is loaded.

**Developer**: This parameter represents the developer type to use for the next simulation run. If developer is set to type, then developers are divided into key, maintainer, major, and minor. If this parameter is set to role, then developers are classified into core and peripheral according to the onion model.

**Number of Init Rounds**: If the mining reveals two different update behaviors, for example, an initial phase with pronounced growth and, afterwards, a phase with significantly less growth, then this parameter represents the duration of the initial phase.

**Initial Boost Factor**: This parameter adjusts the effort that the developers spend in the initial phase.

**Boost Factor**: This parameter adjusts the effort that the developers spend in the second phase.

**Delete Bonus**: If not enough files are deleted by the developers work, deleting files can be rewarded using this parameter.

**Fix Runs**: Due to the fact that the bug creation probabilities are based on ITSs data and the bugfix probabilities of the developers are based on VCSs discrepancies between the number of created and the number of fixed bugs can occur. This parameter adjusts the number of trials a developer tries to fix a bug.

**Simulate Refactorings**: If this parameter is switched on, software refactorings are additionally simulated. This can slow down the runtime. (Please note that this parameter is currently disabled and refactorings are not simulated.)

**Default Random Seed**: Default parameter of Repast Simphony used for random number generation.

## Run the simulation

There are two ways to run the simulation. One is to compile the code using Repast Simphony and run it, and the other is to simply install the model of the current release.

For a selection of open source projects we have delivered simulation parameters. The runtime parameters can be found in the folder `SimSE.rs` and are named `parameters_projectname.xml` for each project. Before the simulation is started, the parameters of the project to be simulated must be copied into the file `parameters.xml` which is read in by the simulation at the start-up.

The core parameters and the change coupling graph for the mined projects are stored in the folder `input`. The project specific parts of the file names of these files correspond to the parameter **Project Name** in the file `parameters.xml`.

After a complete simulation run, the simulation data is saved in the `output` folder.

## References
\[1\] Development of Agent-Based Simulation Models for Software Evolution, Daniel Honsel, 2020, http://hdl.handle.net/21.11130/00-1735-0000-0005-1318-B