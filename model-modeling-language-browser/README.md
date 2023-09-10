# Model Modeling Language Webeditor

The Model modeling language (MML) is a domain-specific modeling language implemented with the open-source
language engineering tool [Langium](https://langium.org/). You can find the implementation of the Language Server in
the [MML repository](https://github.com/JanikNex/model-modeling-language).

This directory contains the web server implementation of an MML editor based on the MML language server and
the [Monaco editor](https://github.com/microsoft/monaco-editor).

## Get started

Since the MML server worker is needed to build the web editor, which requires building the MML repository, the
requirements are basically identical. Node.js >= 14 and npm >= 7 are required. Then the required dependencies can
be installed with the following command:

```shell
npm install
```

This will also download MML from the MML repository.
To build the web editor completely now, the following command must be executed:

```shell
npm run fullbuild
```

After that, all the resources it needs for the web server are in the public directory.

For direct use it can now be started with a simple HTTP server. The following command starts the server on port 3000:

```shell
npx http-server public -p 3000
```

## API functionality

Basically, the provided resources serve only as a text editor in the browser without any further
functionality. For this reason we provide some interface functions to allow further integration into other tools.

Before the interface functions can be used, the workspace must first be initialized. For this we provide
`initializeWorkspace(basepath: string, models: [{ path: string, text: string }])` and an
analogous `initializeWorkspaceJson(basepath: string, models: string)`. This can be used to add any number of files
to the workspace.

If a file has been modified externally, only a single model can be updated instead of a complete reinitialization.
For this we provide `updateModel(model: { path: string, text: string })` and `updateModelJson(model: string)`.

Afterwards one of the initialized models can be opened in the editor with the function `openModel(modelPath: string)`.

Finally, we provide `exportWorkspace()` to export the workspace (for example, in case of changes 
made in the editor), and `getCombinedGeneratorResult()` to retrieve the diagnostics as well as the compiled JSON result.