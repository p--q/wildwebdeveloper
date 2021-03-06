/*******************************************************************************
 * Copyright (c) 2019 Red Hat Inc. and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *   Mickael Istria (Red Hat Inc.) - initial implementation
 *******************************************************************************/
package org.eclipse.wildwebdeveloper.tests;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.text.IDocument;
import org.eclipse.lsp4e.LSPEclipseUtils;
import org.eclipse.lsp4e.LanguageServerPlugin;
import org.eclipse.lsp4e.LanguageServiceAccessor;
import org.eclipse.lsp4j.Hover;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.texteditor.AbstractTextEditor;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TestTypeScript {

	private IProject project;

	@Before
	public void setUpProject() throws Exception {
		LanguageServerPlugin.getDefault().getPreferenceStore().putValue("org.eclipse.wildwebdeveloper.jsts.file.logging.enabled", Boolean.toString(true));
		LanguageServerPlugin.getDefault().getPreferenceStore().putValue("org.eclipse.wildwebdeveloper.css.file.logging.enabled", Boolean.toString(true));
		LanguageServerPlugin.getDefault().getPreferenceStore().putValue("org.eclipse.wildwebdeveloper.html.file.logging.enabled", Boolean.toString(true));
		LanguageServerPlugin.getDefault().getPreferenceStore().putValue("org.eclipse.wildwebdeveloper.json.file.logging.enabled", Boolean.toString(true));
		LanguageServerPlugin.getDefault().getPreferenceStore().putValue("org.eclipse.wildwebdeveloper.xml.file.logging.enabled", Boolean.toString(true));
		LanguageServerPlugin.getDefault().getPreferenceStore().putValue("org.eclipse.wildwebdeveloper.yaml.file.logging.enabled", Boolean.toString(true));
		this.project = ResourcesPlugin.getWorkspace().getRoot().getProject(getClass().getName() + System.nanoTime());
		project.create(null);
		project.open(null);
		IWorkbenchPage activePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		for (IViewReference ref : activePage.getViewReferences()) {
			activePage.hideView(ref);
		}
	}

	@After
	public void deleteProjectAndCloseEditors() throws Exception {
		PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().closeAllEditors(false);
		this.project.delete(true, null);
	}

	@Test
	public void testHTMLinTSXFile() throws Exception {
		IFile file = project.getFile("test.tsx");
		file.create(getClass().getResourceAsStream("/testProjects/htmlIn.tsx"), true, null);
		AbstractTextEditor editor = (AbstractTextEditor) IDE.openEditor(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage(), file);
		IDocument document = LSPEclipseUtils.getDocument(editor);
		Hover hover = LanguageServiceAccessor.getLanguageServers(document, null).get().get(0).getTextDocumentService().hover(LSPEclipseUtils.toTextDocumentPosistionParams(18, document)).get();
		Assert.assertTrue(hover.getContents().toString().contains("button"));
	}

}
