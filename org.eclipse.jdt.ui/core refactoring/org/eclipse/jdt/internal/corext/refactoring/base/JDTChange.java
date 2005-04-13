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
package org.eclipse.jdt.internal.corext.refactoring.base;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;

import org.eclipse.core.filebuffers.FileBuffers;
import org.eclipse.core.filebuffers.ITextFileBuffer;
import org.eclipse.core.filebuffers.ITextFileBufferManager;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;

import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;

import org.eclipse.jdt.internal.corext.refactoring.RefactoringCoreMessages;
import org.eclipse.jdt.internal.corext.util.JavaModelUtil;
import org.eclipse.jdt.internal.corext.util.Messages;
import org.eclipse.jdt.internal.corext.util.Resources;

/**
 * JDT specific change object.
 */
public abstract class JDTChange extends Change {

	private long fModificationStamp;

	public void initializeValidationData(IProgressMonitor pm) {
		IResource resource= getResource(getModifiedElement());
		if (resource != null) {
			fModificationStamp= resource.getModificationStamp();
		}
	}

	protected RefactoringStatus isValid(IProgressMonitor pm, boolean checkReadOnly, boolean checkDirty) throws CoreException {
		pm.beginTask("", 1); //$NON-NLS-1$
		RefactoringStatus result= new RefactoringStatus();
		IResource resource= getResource(getModifiedElement());
		if (resource != null) {
			checkIfModifiable(result, resource, checkReadOnly, checkDirty);
			checkModificationStamp(result, resource);
		}
		pm.worked(1);
		return result;
	}

	protected void checkModificationStamp(RefactoringStatus status, IResource resource) {
		if (fModificationStamp != IResource.NULL_STAMP && fModificationStamp != resource.getModificationStamp()) {
			status.addFatalError(Messages.format(
				RefactoringCoreMessages.Change_has_modifications, resource.getFullPath().toString())); //$NON-NLS-1$
		}
	}
	
	protected void checkModificationStamp(RefactoringStatus status, IJavaElement element) {
		IResource resource= element.getResource();
		if (resource != null && fModificationStamp != IResource.NULL_STAMP && fModificationStamp != resource.getModificationStamp()) {
			status.addFatalError(Messages.format(
				RefactoringCoreMessages.Change_has_modifications, resource.getFullPath().toString())); //$NON-NLS-1$
		}
	}
	
	protected static void checkIfModifiable(RefactoringStatus status, Object element, boolean checkReadOnly, boolean checkDirty) {
		IResource resource= getResource(element);
		if (resource != null)
			checkIfModifiable(status, resource, checkReadOnly, checkDirty);
	}

	protected static void checkIfModifiable(RefactoringStatus status, IResource resource, boolean checkReadOnly,
		boolean checkDirty) {
		if (checkReadOnly) {
			checkReadOnly(status, resource);
		}
		if (checkDirty) {
			checkIfDirty(status, resource);
		}
	}

	protected static void checkReadOnly(RefactoringStatus status, IResource resource) {
		if (Resources.isReadOnly(resource)) {
			status.addFatalError(Messages.format(
				RefactoringCoreMessages.Change_is_read_only, resource.getFullPath().toString())); //$NON-NLS-1$
		}
	}

	protected static void checkIfDirty(RefactoringStatus status, IResource resource) {
		if (resource instanceof IFile) {
			IFile file= (IFile)resource;
			if (file.exists()) {
				ITextFileBufferManager manager= FileBuffers.getTextFileBufferManager();
				ITextFileBuffer buffer= manager.getTextFileBuffer(file.getFullPath());
				if (buffer != null && buffer.isDirty()) {
					status.addFatalError(Messages.format(
						RefactoringCoreMessages.Change_is_unsaved, file.getFullPath().toString())); //$NON-NLS-1$
				}
			}
		}
	}

	private static IResource getResource(Object element) {
		if (element instanceof IResource) {
			return (IResource)element;
		}
		if (element instanceof ICompilationUnit) {
			return JavaModelUtil.toOriginal((ICompilationUnit)element).getResource();
		}
		if (element instanceof IAdaptable) {
			return (IResource)((IAdaptable)element).getAdapter(IResource.class);
		}
		return null;
	}

	public String toString() {
		return getName();
	}
}