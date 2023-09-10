# Model Modeling Language Editor

The Model modeling language (MML) is a domain-specific modeling language implemented with the open-source
language engineering tool [Langium](https://langium.org/). This repository contains all resources to include the MML in
Eclipse.
Through this we provide an editor for MML files, as well as the export of metamodels and instances to Ecore and XMI
files.

For this purpose, an MML Language Server is started when the MML Editor is opened for the first time. Afterwards the
editor is displayed as a web page in a webview in a JavaFX window.
However, this also makes it clear that a large number of components have to be built and interact.

## How to build

1. Build the web editor first. For simplicity, we provide a Maven build script. For this, the following command can be
   executed:
   ```shell
   mvn process-resources
   ```
   (If Maven is not available, the web editor must be built separately first. Follow
   the [instructions](https://github.com/JanikNex/model-modeling-language-editor/tree/main/model-modeling-language-browser)
   for this.
   Afterwards the generated files have to be copied from `model-modeling-language-browser/public`
   to `model-modeling-language-eclipse/MML-Editor/ls`)
2. Open the workspace `model-modeling-language-eclipse` in Eclipse and import the projects `JavaFXDependencies`
   and `MML-Editor`.
3. Install required Eclipse plugins:
    - WildWebDeveloper ([Link](https://github.com/eclipse-wildwebdeveloper/wildwebdeveloper))
    - Google
      Gson ([Link](https://download.eclipse.org/oomph/archive/simrel/tcf.aggrcon/http___download.eclipse.org_tools_tcf_releases_1.7_1.7.0/com.google.gson_2.2.4.v201311231704.html))
4. Install JavaFx and check that the path to the JavaFx bin directory is added to the Path environment
   variable. Alternatively, the resources provided in `model-modeling-language-eclipse/bin` can be used.
5. Right click on the MML Editor project and select `Run As -> Eclipse Application`. This will start a new Eclipse
   instance containing the MML editor. At startup it will ask for a new workspace.
   If the provided resources are used instead of an installation of JavaFx, the path environment variable must also
   be adjusted for this. Alternatively, this can be done temporarily. For this go to `Run As -> Run Configurations...`,
   select the Eclipse Application Configuration, open the Arguments tab and set the following VM argument:
   ```text
   -Djava.library.path="${workspace_loc}\bin;${env_var:PATH}"
   ```

## Usage

First create a `.mml` file. When you right-click on it, there is a new entry in the context menu.
Click on `Open in MML Editor`. The first opening may take a few seconds (a loading screen is displayed), because
the language server is started in the background first.

We provide several functions via the menu. If the same (or another) file in the same workspace has been modified
externally, either all, only the current or all other models can be reloaded. Likewise, changes made in the editor
can be saved in the file system.

Finally, MML files can be compiled and converted to EMF files. To do this, select the `Export Model` function.
This will transfer all packages to Ecore files and all instances to XMI files. The exported files are located in the
`model` directory of the project.
