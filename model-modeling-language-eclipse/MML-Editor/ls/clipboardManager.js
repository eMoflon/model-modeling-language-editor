addEventListener("copy", (event) => {
    if (clipboardConnector == null) {
        console.log("[CLIPBOARD MANAGER] Copy event noticed but ignored!");
        return;
    }

    event.preventDefault();
    event.stopPropagation();
    event.stopImmediatePropagation();
    const selection = window.getSelection();
    if (!selection.rangeCount) {
        return;
    }
    clipboardConnector.setCopy(selection.toString());
}, true);

addEventListener("paste", (event) => {
    if (clipboardConnector == null) {
        console.log("[CLIPBOARD MANAGER] Paste event noticed but ignored!");
        return;
    }
    const pasteText = clipboardConnector.getPaste();
    clipboardConnector.log(pasteText);

    event.preventDefault();
    event.stopPropagation();
    event.stopImmediatePropagation();
    if (pasteText !== "") {
        forcePaste(pasteText);
    }
}, true);

