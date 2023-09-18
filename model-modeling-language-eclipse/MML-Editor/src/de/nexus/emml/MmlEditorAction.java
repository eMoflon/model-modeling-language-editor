package de.nexus.emml;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;

/**
 * Menu entry in the file rightclick menu. Restricted to .mml files
 */
public class MmlEditorAction implements IObjectActionDelegate, IActionDelegate {

	// The plug-in ID
	public static final String PLUGIN_ID = "MML-Editor";

	@Override
	public void run(IAction action) {
		// Method run when menu entry is clicked
		Platform.getLog(getClass()).info("Action run");
		// get rightclicked item
		ISelection sel = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getSelectionService().getSelection();
		if (!(sel instanceof IStructuredSelection)) {
			return;
		}
		IStructuredSelection selection = (IStructuredSelection) sel;
		Object obj = selection.getFirstElement();
		// get file
		IFile file = (IFile) Platform.getAdapterManager().getAdapter(obj, IFile.class);
		Platform.getLog(getClass()).info("Selected file: " + file.getFullPath().toString());
		EditorActivator activ = EditorActivator.getDefault();
		activ.openFile(file.getFullPath().toPath());
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		Platform.getLog(getClass()).info("Action selection changed");
	}

	@Override
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		Platform.getLog(getClass()).info("Action set active");
	}

}
