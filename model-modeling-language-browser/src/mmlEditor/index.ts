/**
 * Configuration for monaco-editor-wrapper 1.6.0
 *
 *
 * "monaco-editor-wrapper": "^1.6.0",
 */


import {buildWorkerDefinition} from 'monaco-editor-workers';
import {CodeEditorConfig, monaco, MonacoEditorLanguageClientWrapper, WorkerConfigOptions} from 'monaco-editor-wrapper';
import {Diagnostic, DiagnosticSeverity, NotificationType} from 'vscode-languageserver/browser.js';

type DocumentChange = { uri: string, content: string, diagnostics: Diagnostic[] };

// registers a factory function determining the required bundle
//  and firing up a worker taking over some task of the monaco editor in background
buildWorkerDefinition('.', import.meta.url, true);

// hooks monaco-specific css-definitions into the dom
MonacoEditorLanguageClientWrapper.addMonacoStyles('monaco-editor-styles');

// instantiate the language wrapper collecting the editor settings, ...
const client = new MonacoEditorLanguageClientWrapper('42');

// ... add the required settings, ...
configureEditor((client.getEditorConfig()));

// ... and fire the editor within the given container, ...
client.startEditor(document.getElementById("monaco-editor-root") || undefined);
const editor = client.getEditor();


let workspacePath: string | undefined = undefined;

const generatorStorage: Map<string, string> = new Map<string, string>();
const diagnosticStorage: Map<string, number> = new Map<string, number>();

// ... and register a notification listener expecting the AST in json,
//  generating code, executing that code, and logging the result into the console.
// Such notifications are not sent by Langium by default,
//  it's a customization in the arithmetics example language implementation,
//  see node_modules/langium-arithmetics-dsl/src/language-server/main-browser.ts
client.getLanguageClient()?.onNotification(
    new NotificationType<DocumentChange>('browser/DocumentChange'),
    dc => {
        const errors = dc.diagnostics.filter(d => d.severity === DiagnosticSeverity.Error);
        if (errors.length !== 0) {
            console.log(`Input contains error in line ${errors[0].range.start.line}: ${errors[0].message}`);
            diagnosticStorage.set(dc.uri, errors.length);
            generatorStorage.delete(dc.uri);
        } else {
            const result = dc.content
            generatorStorage.set(dc.uri, result);
            diagnosticStorage.delete(dc.uri);
            console.log("Final computable expression is equal to: " + result);
        }
    }
);

function configureEditor(editorConfig: CodeEditorConfig) {
    editorConfig.setTheme('vs-dark');
    editorConfig.setAutomaticLayout(true /* 'true' is the default value! */);
    editorConfig.setUseLanguageClient(true);
    editorConfig.setUseWebSocket(false);
    editorConfig.setMainLanguageId('model-modeling-language');
    editorConfig.setMonacoEditorOptions({
        "semanticHighlighting.enabled": true,
        fontLigatures: true,
        bracketPairColorization: {enabled: true, independentColorPoolPerBracketType: true}
    });
    editorConfig.setLanguageClientConfigOptions(<WorkerConfigOptions>{
        workerType: 'module',
        workerName: 'LS',
        workerURL: new URL('./mmlServerWorker.js', import.meta.url).href
    });

    editorConfig.setMonarchTokensProvider({
        keywords: [
            '@opposite', 'abstract', 'as', 'attribute', 'bool', 'class', 'derived', 'double', 'enum', 'extends', 'false', 'float', 'for', 'function', 'id', 'implements', 'import', 'in', 'instance', 'int', 'interface', 'macro', 'ordered', 'package', 'readonly', 'reference', 'resolve', 'return', 'returns', 'string', 'transient', 'true', 'tuple', 'unique', 'unsettable', 'using', 'volatile'
        ],
        operators: [
            '%', '*', '+', ',', '-', '->', '.', '..', '/', ':', ';', '=', '^'
        ],
        symbols: /%|\(|\)|\*|\+|,|-|->|\.|\.\.|\/|:|;|=|\[|\]|\^|\{|\}/,

        tokenizer: {
            initial: [
                {regex: /[0-9]+/, action: {"token": "number"}},
                {regex: /"[^"]*"/, action: {"token": "string"}},
                {
                    regex: /[a-zA-Z_][\w_]*/,
                    action: {cases: {'@keywords': {"token": "keyword"}, '@default': {"token": "string"}}}
                },
                {include: '@whitespace'},
                {regex: /@symbols/, action: {cases: {'@operators': {"token": "operator"}, '@default': {"token": ""}}}},
            ],
            whitespace: [
                {regex: /\s+/, action: {"token": "white"}},
                {regex: /\/\*/, action: {"token": "comment", "next": "@comment"}},
                {regex: /[^:]\/\/[^\n\r]*/, action: {"token": "comment"}},
            ],
            comment: [
                {regex: /[^\/\*]+/, action: {"token": "comment"}},
                {regex: /\*\//, action: {"token": "comment", "next": "@pop"}},
                {regex: /[\/\*]/, action: {"token": "comment"}},
            ],
        }
    });
}

function initializeWorkspace(basepath: string, models: [{ path: string, text: string }]): number {
    workspacePath = basepath;
    diagnosticStorage.clear();
    generatorStorage.clear();

    for (let i = 0; i < models.length; i++) {
        const model: { path: string, text: string } = models[i];
        const deserializedPath: string = model.path.replace("\\", "/");
        monaco.editor.createModel(model.text
            , "model-modeling-language", monaco.Uri.parse("file:///" + deserializedPath));
    }
    return models.length;
}

export function initializeWorkspaceJson(basepath: string, models: string): number {
    console.log(models);
    return initializeWorkspace(basepath, JSON.parse(models));
}

export function updateModel(modelPath: string, text: string): boolean {
    if (editor == undefined) {
        return false;
    }
    const openedModel: monaco.editor.ITextModel | null = editor.getModel();
    const targetUri: monaco.Uri = monaco.Uri.parse("file://" + modelPath);
    if (openedModel != null && openedModel.uri.path == targetUri.path) {
        return false;
    }
    monaco.editor.createModel(text, "model-modeling-language", monaco.Uri.parse("file:///" + modelPath));
    return true;
}

export function openModel(modelPath: string): boolean {
    if (editor != undefined) {
        const modelUri = monaco.Uri.parse("file:///" + modelPath);
        const model: monaco.editor.ITextModel | null = monaco.editor.getModel(modelUri);
        if (model == null) {
            return false;
        }
        editor.setModel(model);
        return true;
    }
    return false;
}

export function getCombinedGeneratorResult(): string {
    if (workspacePath == undefined) {
        return "{}";
    }
    console.log(generatorStorage.size);
    const generatorResult: { uri: string, gen: string }[] = [];
    const diagnosticResult: { uri: string, err: number }[] = [];
    generatorStorage.forEach((val, key) => generatorResult.push({uri: key, gen: val}));
    diagnosticStorage.forEach((val, key) => diagnosticResult.push({uri: key, err: val}));
    const combinedResult: {
        generator: { uri: string, gen: string }[],
        diagnostic: { uri: string, err: number }[]
    } = {generator: generatorResult, diagnostic: diagnosticResult};
    console.warn(JSON.stringify(combinedResult));
    return JSON.stringify(combinedResult);
}

declare global {
    interface Window {
        getCombinedGeneratorResult: any;
        initializeWorkspaceJson: any;
        openModel: any;
        updateModel: any;
    }
}

window.getCombinedGeneratorResult = getCombinedGeneratorResult;
window.initializeWorkspaceJson = initializeWorkspaceJson;
window.openModel = openModel;
window.updateModel = updateModel;