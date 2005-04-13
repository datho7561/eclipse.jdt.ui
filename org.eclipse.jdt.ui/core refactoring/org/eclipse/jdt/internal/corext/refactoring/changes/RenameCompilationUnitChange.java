/*******************************************************************************
 * Copyright (c) 2000, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jdt.internal.corext.refactoring.changes;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;

import org.eclipse.core.resources.IResource;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.JavaModelException;

import org.eclipse.jdt.internal.corext.Assert;
import org.eclipse.jdt.internal.corext.refactoring.AbstractJavaElementRenameChange;
import org.eclipse.jdt.internal.corext.refactoring.RefactoringCoreMessages;
import org.eclipse.jdt.internal.corext.refactoring.util.ResourceUtil;
import org.eclipse.jdt.internal.corext.util.Messages;

import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;


public class RenameCompilationUnitChange extends AbstractJavaElementRenameChange {

	public RenameCompilationUnitChange(ICompilationUnit cu, String newName) {
		this(ResourceUtil.getResource(cu).getFullPath(), cu.getElementName(), newName, IResource.NULL_STAMP);
		Assert.isTrue(!cu.isReadOnly(), "cu must not be read-only"); //$NON-NLS-1$
	}
	
	private RenameCompilationUnitChange(IPath resourcePath, String oldName, String newName, long stampToRestore){
		super(resourcePath, oldName, newName, stampToRestore);
	}
	
	public RefactoringStatus isValid(IProgressMonitor pm) throws CoreException {
		return super.isValid(pm, true, false);
	}
	
	protected IPath createNewPath() {
		if (getResourcePath().getFileExtension() != null)
			return getResourcePath().removeFileExtension().removeLastSegments(1).append(getNewName());
		else	
			return getResourcePath().removeLastSegments(1).append(getNewName());
	}
	
	public String getName() {
		return Messages.format(RefactoringCoreMessages.RenameCompilationUnitChange_name, new String[]{getOldName(), getNewName()}); 
	}
	
	/* non java-doc
	 * @see AbstractRenameChange#createUndoChange()
	 */
	protected Change createUndoChange(long stampToRestore) throws JavaModelException{
		return new RenameCompilationUnitChange(createNewPath(), getNewName(), getOldName(), stampToRestore);
	}
	
	protected void doRename(IProgressMonitor pm) throws CoreException {
		ICompilationUnit cu= (ICompilationUnit)getModifiedElement();
		if (cu != null)
			cu.rename(getNewName(), false, pm);
	}
}
