# Workflow

Here the individual steps necessary to simulate a desired open source project are briefly described. Starting with the generation of the simulation parameters up to the actual simulation.

## General Preparation
1. Select an open source project to analyze
2. Make sure that there is a Git repository and that there is also an issue tracking system freely available
3. Clone the repository to your local drive (required for simulation parameter estimation)

## Collection of Simulation Parameters
1. Setup a MongoDB 
2. Execute the following [SmarkSHARK](https://github.com/smartshark/) plugins to populate your MongoDB with required data for the parameter estimation of simulation parameters
    - [vcsSHARK](https://smartshark.github.io/vcsSHARK)
    - [mecoSHARK](https://smartshark.github.io/mecoSHARK/intro.html)
    - [issueSHARK](https://github.com/smartshark/issueSHARK)
    - [refSHARK](https://github.com/smartshark/refSHARK)
3. Configure the [parameter estimation tool](https://github.com/dhonsel/SimParameter) as described [here](https://github.com/dhonsel/SimParameter/blob/main/README.md#configuration) according to the selected project
4. Create the simulation parameters using the  [parameter estimation tool](https://github.com/dhonsel/SimParameter)

## Simulation
1. Build or install the agent-based simulation model for software evolution [SimSE](https://github.com/dhonsel/SimSE). For building the model [Repast Simphony](https://repast.github.io/) is required. An installer for the model is uploaded with the current release and can be downloaded [here](https://github.com/dhonsel/SimSE/releases/download/v0.7-alpha/simse_setup.jar).
2. Adjust the runtime parameters  `SimSE.rs/parameters.xml` according to the project to analyze. A copy of these parameters should be stored in `SimSE.rs/parameters_projectname.xml` as backup. The parameter description can be found [here](https://github.com/dhonsel/SimSE#runtime-parameters).
3. Copy the core simulation parameters `projectname_data.json` to the `input` folder.
4. Copy the mined change coupling graph files `cc_projectname_XX.dot` to the `input` folder.
5. Start the application and execute the simulation model.
6. After a simulation run all generated date is stored in the folder `output`.
