/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 ******************************************************************************/

package org.eclipse.e4.ui.tests.workbench;

import junit.framework.TestCase;

import org.eclipse.e4.core.services.IDisposable;
import org.eclipse.e4.core.services.context.IEclipseContext;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.MApplicationFactory;
import org.eclipse.e4.ui.model.application.MPart;
import org.eclipse.e4.ui.model.application.MPartSashContainer;
import org.eclipse.e4.ui.model.application.MPartStack;
import org.eclipse.e4.ui.model.application.MWindow;
import org.eclipse.e4.ui.workbench.swt.internal.E4Application;
import org.eclipse.e4.ui.workbench.swt.internal.PartRenderingEngine;
import org.eclipse.e4.workbench.ui.IPresentationEngine;
import org.eclipse.e4.workbench.ui.internal.E4Workbench;
import org.eclipse.swt.widgets.Display;

public class PartRenderingEngineTests extends TestCase {
	protected IEclipseContext appContext;
	protected E4Workbench wb;

	@Override
	protected void setUp() throws Exception {
		appContext = E4Application.createDefaultContext();
		appContext.set(E4Workbench.PRESENTATION_URI_ARG,
				PartRenderingEngine.engineURI);
	}

	@Override
	protected void tearDown() throws Exception {
		if (wb != null) {
			wb.close();
		}

		if (appContext instanceof IDisposable) {
			((IDisposable) appContext).dispose();
		}
	}

	public void testCreateView() {
		final MWindow window = createWindowWithOneView("Part Name");
		MApplication application = MApplicationFactory.eINSTANCE
				.createApplication();
		application.getChildren().add(window);
		application.setContext(appContext);
		appContext.set(MApplication.class.getName(), application);

		wb = new E4Workbench(application, appContext);
		wb.createAndRunUI(window);

		MPartSashContainer container = (MPartSashContainer) window
				.getChildren().get(0);
		MPartStack stack = (MPartStack) container.getChildren().get(0);
		MPart part = stack.getChildren().get(0);

		IPresentationEngine renderer = (IPresentationEngine) appContext
				.get(IPresentationEngine.class.getName());
		renderer.removeGui(part);
		renderer.removeGui(window);

		while (Display.getCurrent().readAndDispatch()) {
			// spin the event loop
		}
	}

	private MWindow createWindowWithOneView(String partName) {
		final MWindow window = MApplicationFactory.eINSTANCE.createWindow();
		window.setHeight(300);
		window.setWidth(400);
		window.setLabel("MyWindow");
		MPartSashContainer sash = MApplicationFactory.eINSTANCE
				.createPartSashContainer();
		window.getChildren().add(sash);
		MPartStack stack = MApplicationFactory.eINSTANCE.createPartStack();
		sash.getChildren().add(stack);
		MPart contributedPart = MApplicationFactory.eINSTANCE
				.createSaveablePart();
		stack.getChildren().add(contributedPart);
		contributedPart.setLabel(partName);
		contributedPart
				.setURI("platform:/plugin/org.eclipse.e4.ui.tests/org.eclipse.e4.ui.tests.workbench.SampleView");

		return window;
	}

}