/*******************************************************************************
 * Copyright (c) 2000, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jdt.internal.ui.util;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;

import org.eclipse.help.HelpSystem;
import org.eclipse.help.IContext;
import org.eclipse.help.IContext2;
import org.eclipse.help.IHelpResource;

import org.eclipse.ui.PlatformUI;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IPackageFragmentRoot;

import org.eclipse.jdt.internal.corext.Assert;
import org.eclipse.jdt.internal.corext.util.JavaModelUtil;
import org.eclipse.jdt.internal.corext.util.Messages;

import org.eclipse.jdt.ui.JavaElementLabels;
import org.eclipse.jdt.ui.JavaUI;

import org.eclipse.jdt.internal.ui.JavaUIMessages;

public class JavadocHelpContext implements IContext2 {
	
	
	public static void displayHelp(String contextId, Object[] selected) throws CoreException {
		IContext context= HelpSystem.getContext(contextId);
		if (context != null) {
			if (selected != null && selected.length > 0) {
				context= new JavadocHelpContext(context, selected);
			}
			PlatformUI.getWorkbench().getHelpSystem().displayHelp(context);
		}
	}
	
	
	private static class JavaUIHelpResource implements IHelpResource {

		private IJavaElement fElement;
		private String fUrl;

		public JavaUIHelpResource(IJavaElement element, String url) {
			fElement= element;
			fUrl= url;
		}

		public String getHref() {
			return fUrl;
		}

		public String getLabel() {
			String label= JavaElementLabels.getTextLabel(fElement, JavaElementLabels.ALL_DEFAULT | JavaElementLabels.ALL_FULLY_QUALIFIED);
			return Messages.format(JavaUIMessages.JavaUIHelp_link_label, label); 
		}
	}	
	

	private IHelpResource[] fHelpResources;
	private String fText;

	public JavadocHelpContext(IContext context, Object[] elements) throws CoreException {
		Assert.isNotNull(elements);

		List helpResources= new ArrayList();

//		String javadocSummary= null;
		for (int i= 0; i < elements.length; i++) {
			if (elements[i] instanceof IJavaElement) {
				IJavaElement element= (IJavaElement) elements[i];
				
				// Create Javadoc summary
				// This will have to wait due to https://bugs.eclipse.org/bugs/show_bug.cgi?id=85719
//				if (javadocSummary == null) {
//					javadocSummary= retrieveText(element);
//					if (javadocSummary != null) {
//						String elementLabel= JavaElementLabels.getTextLabel(element, JavaElementLabels.ALL_DEFAULT);
//						javadocSummary= "<b>Javadoc for " + elementLabel + ":</b><br>" + javadocSummary;
//					}
//				} else {
//					javadocSummary= ""; // no Javadoc summary for multiple selection //$NON-NLS-1$
//				}	
				
				URL url= JavaUI.getJavadocLocation(element, true);
				if (url == null || doesNotExist(url)) {
					IPackageFragmentRoot root= JavaModelUtil.getPackageFragmentRoot(element);
					if (root != null) {
						url= JavaUI.getJavadocBaseLocation(element);
						if (root.getKind() == IPackageFragmentRoot.K_SOURCE) {
							element= element.getJavaProject();
						} else {
							element= root;
						}
						url= JavaUI.getJavadocLocation(element, false);
					}
				}
				if (url != null) {
					IHelpResource javaResource= new JavaUIHelpResource(element, url.toExternalForm() + "?noframes=true" ); //$NON-NLS-1$
					helpResources.add(javaResource);
				}
			}
		}

		// Add static help topics
		if (context != null) {
			IHelpResource[] resources= context.getRelatedTopics();
			if (resources != null) {
				for (int j= 0; j < resources.length; j++) {
					helpResources.add(resources[j]);
				}
			}
		}
		
		fHelpResources= (IHelpResource[]) helpResources.toArray(new IHelpResource[helpResources.size()]);

		if (context != null)
			fText= context.getText();
		
		// This will have to wait due to https://bugs.eclipse.org/bugs/show_bug.cgi?id=85719
//		if (javadocSummary != null && javadocSummary.length() > 0) {
//			if (fText != null)
//				fText= context.getText() + "<br><br>" + javadocSummary; //$NON-NLS-1$
//			else
//				fText= javadocSummary;
//		}
		
		if (fText == null)
			fText= "";  //$NON-NLS-1$
		
	}

	private boolean doesNotExist(URL url) {
		if (url.getProtocol().equals("file")) { //$NON-NLS-1$
			File file= new File(url.getFile());
			return !file.exists();
		}
		return false;
	}

	public IHelpResource[] getRelatedTopics() {
		return fHelpResources;
	}

	public String getText() {
		return fText;
	}

	public String getStyledText() {
		return fText;
	}

	public String getCategory(IHelpResource topic) {
		if (topic instanceof JavaUIHelpResource)
			return JavaUIMessages.JavaUIHelpContext_javaHelpCategory_label; 

		return null;
	}

}

