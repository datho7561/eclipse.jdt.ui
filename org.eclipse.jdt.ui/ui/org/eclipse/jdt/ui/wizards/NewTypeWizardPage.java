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
package org.eclipse.jdt.ui.wizards;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;

import org.eclipse.core.resources.IResource;

import org.eclipse.swt.SWT;
import org.eclipse.swt.accessibility.AccessibleAdapter;
import org.eclipse.swt.accessibility.AccessibleEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Text;

import org.eclipse.jface.contentassist.SubjectControlContentAssistant;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.window.Window;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.templates.Template;
import org.eclipse.jface.text.templates.TemplateException;

import org.eclipse.ui.contentassist.ContentAssistHandler;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;
import org.eclipse.ui.dialogs.PreferencesUtil;

import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.IBuffer;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.ISourceRange;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaConventions;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Signature;
import org.eclipse.jdt.core.ToolFactory;
import org.eclipse.jdt.core.compiler.IProblem;
import org.eclipse.jdt.core.compiler.IScanner;
import org.eclipse.jdt.core.compiler.ITerminalSymbols;
import org.eclipse.jdt.core.compiler.InvalidInputException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.AbstractTypeDeclaration;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.ParameterizedType;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.formatter.CodeFormatter;
import org.eclipse.jdt.core.search.IJavaSearchConstants;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.eclipse.jdt.core.search.SearchEngine;

import org.eclipse.jdt.internal.corext.codemanipulation.AddUnimplementedConstructorsOperation;
import org.eclipse.jdt.internal.corext.codemanipulation.AddUnimplementedMethodsOperation;
import org.eclipse.jdt.internal.corext.codemanipulation.CodeGenerationSettings;
import org.eclipse.jdt.internal.corext.codemanipulation.IImportsStructure;
import org.eclipse.jdt.internal.corext.codemanipulation.ImportsStructure;
import org.eclipse.jdt.internal.corext.codemanipulation.StubUtility;
import org.eclipse.jdt.internal.corext.codemanipulation.StubUtility2;
import org.eclipse.jdt.internal.corext.dom.ASTNodes;
import org.eclipse.jdt.internal.corext.dom.NodeFinder;
import org.eclipse.jdt.internal.corext.dom.TokenScanner;
import org.eclipse.jdt.internal.corext.refactoring.StubTypeContext;
import org.eclipse.jdt.internal.corext.refactoring.TypeContextChecker;
import org.eclipse.jdt.internal.corext.template.java.JavaContext;
import org.eclipse.jdt.internal.corext.util.CodeFormatterUtil;
import org.eclipse.jdt.internal.corext.util.JavaModelUtil;
import org.eclipse.jdt.internal.corext.util.Messages;
import org.eclipse.jdt.internal.corext.util.Strings;

import org.eclipse.jdt.ui.CodeGeneration;
import org.eclipse.jdt.ui.JavaElementLabelProvider;

import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jdt.internal.ui.JavaPluginImages;
import org.eclipse.jdt.internal.ui.JavaUIStatus;
import org.eclipse.jdt.internal.ui.dialogs.StatusInfo;
import org.eclipse.jdt.internal.ui.dialogs.TableTextCellEditor;
import org.eclipse.jdt.internal.ui.dialogs.TypeSelectionDialog2;
import org.eclipse.jdt.internal.ui.javaeditor.ASTProvider;
import org.eclipse.jdt.internal.ui.preferences.CodeTemplatePreferencePage;
import org.eclipse.jdt.internal.ui.preferences.JavaPreferencesSettings;
import org.eclipse.jdt.internal.ui.refactoring.contentassist.CompletionContextRequestor;
import org.eclipse.jdt.internal.ui.refactoring.contentassist.ControlContentAssistHelper;
import org.eclipse.jdt.internal.ui.refactoring.contentassist.JavaPackageCompletionProcessor;
import org.eclipse.jdt.internal.ui.refactoring.contentassist.JavaTypeCompletionProcessor;
import org.eclipse.jdt.internal.ui.util.SWTUtil;
import org.eclipse.jdt.internal.ui.wizards.NewWizardMessages;
import org.eclipse.jdt.internal.ui.wizards.StringWrapper;
import org.eclipse.jdt.internal.ui.wizards.SuperInterfaceSelectionDialog;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.DialogField;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.IDialogFieldListener;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.IListAdapter;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.IStringButtonAdapter;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.LayoutUtil;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.ListDialogField;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.SelectionButtonDialogField;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.SelectionButtonDialogFieldGroup;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.Separator;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.StringButtonDialogField;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.StringButtonStatusDialogField;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.StringDialogField;

/**
 * The class <code>NewTypeWizardPage</code> contains controls and validation routines 
 * for a 'New Type WizardPage'. Implementors decide which components to add and to enable. 
 * Implementors can also customize the validation code. <code>NewTypeWizardPage</code> 
 * is intended to serve as base class of all wizards that create types like applets, servlets, classes, 
 * interfaces, etc.
 * <p>
 * See {@link NewClassWizardPage} or {@link NewInterfaceWizardPage} for an
 * example usage of the <code>NewTypeWizardPage</code>.
 * </p>
 * 
 * @see org.eclipse.jdt.ui.wizards.NewClassWizardPage
 * @see org.eclipse.jdt.ui.wizards.NewInterfaceWizardPage
 * 
 * @since 2.0
 */
public abstract class NewTypeWizardPage extends NewContainerWizardPage {

	/**
	 * Class used in stub creation routines to add needed imports to a 
	 * compilation unit.
	 */
	public static class ImportsManager {

		private ImportsStructure fImportsStructure;
		private Set fAddedTypes;
		
		/* package */ ImportsManager(IImportsStructure importsStructure) {
			fImportsStructure= (ImportsStructure) importsStructure;
		}
		
		/* package */ ImportsManager(ICompilationUnit createdWorkingCopy) throws CoreException {
			this(createdWorkingCopy, new HashSet());
		}

		/* package */ ImportsManager(ICompilationUnit createdWorkingCopy, Set addedTypes) throws CoreException {
			IJavaProject javaProject= createdWorkingCopy.getJavaProject();
			String[] prefOrder= JavaPreferencesSettings.getImportOrderPreference(javaProject);
			int threshold= JavaPreferencesSettings.getImportNumberThreshold(javaProject);
			fAddedTypes= addedTypes;
			
			fImportsStructure= new ImportsStructure(createdWorkingCopy, prefOrder, threshold, true);
		}

		/* package */ ICompilationUnit getCompilationUnit() {
			return fImportsStructure.getCompilationUnit();
		}
						
		/**
		 * Adds a new import declaration that is sorted in the existing imports.
		 * If an import already exists or the import would conflict with an import
		 * of an other type with the same simple name, the import is not added.
		 * 
		 * @param qualifiedTypeName The fully qualified name of the type to import
		 * (dot separated).
		 * @return Returns the simple type name that can be used in the code or the
		 * fully qualified type name if an import conflict prevented the import.
		 */				
		public String addImport(String qualifiedTypeName) {
			fAddedTypes.add(qualifiedTypeName);
			return fImportsStructure.addImport(qualifiedTypeName);
		}
				
		/**
		 * Adds a new import declaration that is sorted in the existing imports.
		 * If an import already exists or the import would conflict with an import
		 * of an other type with the same simple name, the import is not added.
		 * 
		 * @param typeBinding the binding of the type to import
		 * 
		 * @return Returns the simple type name that can be used in the code or the
		 * fully qualified type name if an import conflict prevented the import.
		 */				
		public String addImport(ITypeBinding typeBinding) {
			// don't know what's added -> add created imports in getAddedTypes()
			return fImportsStructure.addImport(typeBinding);
		}
				
		/* package */ void create(boolean needsSave, SubProgressMonitor monitor) throws CoreException {
			fImportsStructure.create(needsSave, monitor);
		}
		
		/* package */ void removeImport(String qualifiedName) {
			if (fAddedTypes.contains(qualifiedName)) {
				fImportsStructure.removeImport(qualifiedName);
			}
		}
		
		/* package */ Set getAddedTypes() {
			String[] createdImports= fImportsStructure.getCreatedImports();
			for (int i= 0; i < createdImports.length; i++) {
				fAddedTypes.add(createdImports[i]);
			}
			return fAddedTypes;
		}
	}
		
	
	/** Public access flag. See The Java Virtual Machine Specification for more details. */
	public int F_PUBLIC = Flags.AccPublic;
	/** Private access flag. See The Java Virtual Machine Specification for more details. */
	public int F_PRIVATE = Flags.AccPrivate;
	/**  Protected access flag. See The Java Virtual Machine Specification for more details. */
	public int F_PROTECTED = Flags.AccProtected;
	/** Static access flag. See The Java Virtual Machine Specification for more details. */
	public int F_STATIC = Flags.AccStatic;
	/** Final access flag. See The Java Virtual Machine Specification for more details. */
	public int F_FINAL = Flags.AccFinal;
	/** Abstract property flag. See The Java Virtual Machine Specification for more details. */
	public int F_ABSTRACT = Flags.AccAbstract;

	private final static String PAGE_NAME= "NewTypeWizardPage"; //$NON-NLS-1$
	
	private final static String DIALOGSETTINGS_ADDCOMMENTS= "NewTypeWizardPage.add_comments"; //$NON-NLS-1$
	
	/** Field ID of the package input field. */
	protected final static String PACKAGE= PAGE_NAME + ".package";	 //$NON-NLS-1$
	/** Field ID of the enclosing type input field. */
	protected final static String ENCLOSING= PAGE_NAME + ".enclosing"; //$NON-NLS-1$
	/** Field ID of the enclosing type checkbox. */
	protected final static String ENCLOSINGSELECTION= ENCLOSING + ".selection"; //$NON-NLS-1$
	/** Field ID of the type name input field. */	
	protected final static String TYPENAME= PAGE_NAME + ".typename"; //$NON-NLS-1$
	/** Field ID of the super type input field. */
	protected final static String SUPER= PAGE_NAME + ".superclass"; //$NON-NLS-1$
	/** Field ID of the super interfaces input field. */
	protected final static String INTERFACES= PAGE_NAME + ".interfaces"; //$NON-NLS-1$
	/** Field ID of the modifier check boxes. */
	protected final static String MODIFIERS= PAGE_NAME + ".modifiers"; //$NON-NLS-1$
	/** Field ID of the method stubs check boxes. */
	protected final static String METHODS= PAGE_NAME + ".methods"; //$NON-NLS-1$

	private class InterfacesListLabelProvider extends LabelProvider {
		
		private Image fInterfaceImage;
		
		public InterfacesListLabelProvider() {
			super();
			fInterfaceImage= JavaPluginImages.get(JavaPluginImages.IMG_OBJS_INTERFACE);
		}
		
		public String getText(Object element) {
			return ((StringWrapper) element).getString();
		}
		
		public Image getImage(Object element) {
			return fInterfaceImage;
		}
	}	

	private StringButtonStatusDialogField fPackageDialogField;
	
	private SelectionButtonDialogField fEnclosingTypeSelection;
	private StringButtonDialogField fEnclosingTypeDialogField;
		
	private boolean fCanModifyPackage;
	private boolean fCanModifyEnclosingType;
	
	private IPackageFragment fCurrPackage;
	
	private IType fCurrEnclosingType;
	private IType fCurrType;
	private StringDialogField fTypeNameDialogField;
	
	private StringButtonDialogField fSuperClassDialogField;
	private ListDialogField fSuperInterfacesDialogField;
	
	private SelectionButtonDialogFieldGroup fAccMdfButtons;
	private SelectionButtonDialogFieldGroup fOtherMdfButtons;
	
	private SelectionButtonDialogField fAddCommentButton;
	private boolean fUseAddCommentButtonValue; // used for compatibilty: Wizards that don't show the comment button control
	// will use the preferences settings
	
	private IType fCreatedType;
	
	private JavaPackageCompletionProcessor fCurrPackageCompletionProcessor;
	private JavaTypeCompletionProcessor fEnclosingTypeCompletionProcessor;
	private StubTypeContext fSuperClassStubTypeContext;
	private StubTypeContext fSuperInterfaceStubTypeContext;
	
	protected IStatus fEnclosingTypeStatus;
	protected IStatus fPackageStatus;
	protected IStatus fTypeNameStatus;
	protected IStatus fSuperClassStatus;
	protected IStatus fModifierStatus;
	protected IStatus fSuperInterfacesStatus;	
	
	private final int PUBLIC_INDEX= 0, DEFAULT_INDEX= 1, PRIVATE_INDEX= 2, PROTECTED_INDEX= 3;
	private final int ABSTRACT_INDEX= 0, FINAL_INDEX= 1, STATIC_INDEX= 2, ENUM_ANNOT_STATIC_INDEX= 1;
	
	private int fTypeKind;
	
	/**
	 * Constant to signal that the created type is a class.
	 * @since 3.1
	 */
	public static final int CLASS_TYPE = 1;
	
	/**
	 * Constant to signal that the created type is a interface.
	 * @since 3.1
	 */
	public static final int INTERFACE_TYPE = 2;
	
	/**
	 * Constant to signal that the created type is an enum.
	 * @since 3.1
	 */
	public static final int ENUM_TYPE = 3;
	
	/**
	 * Constant to signal that the created type is an annotation.
	 * @since 3.1
	 */
	public static final int ANNOTATION_TYPE = 4;

	/**
	 * Creates a new <code>NewTypeWizardPage</code>.
	 * 
	 * @param isClass <code>true</code> if a new class is to be created; otherwise
	 * an interface is to be created
	 * @param pageName the wizard page's name
	 */
	public NewTypeWizardPage(boolean isClass, String pageName) {
		this(isClass ? CLASS_TYPE : INTERFACE_TYPE, pageName);
	}
	
	/**
	 * Creates a new <code>NewTypeWizardPage</code>.
	 * 
	 * @param typeKind Signals the kind of the type to be created. Valid kinds are
	 * {@link #CLASS_TYPE}, {@link #INTERFACE_TYPE}, {@link #ENUM_TYPE} and {@link #ANNOTATION_TYPE}
	 * @param pageName the wizard page's name
	 * @since 3.1
	 */
	public NewTypeWizardPage(int typeKind, String pageName) {
	    super(pageName);
	    fTypeKind= typeKind;

	    fCreatedType= null;
		
		TypeFieldsAdapter adapter= new TypeFieldsAdapter();
		
		fPackageDialogField= new StringButtonStatusDialogField(adapter);
		fPackageDialogField.setDialogFieldListener(adapter);
		fPackageDialogField.setLabelText(NewWizardMessages.NewTypeWizardPage_package_label); 
		fPackageDialogField.setButtonLabel(NewWizardMessages.NewTypeWizardPage_package_button); 
		fPackageDialogField.setStatusWidthHint(NewWizardMessages.NewTypeWizardPage_default); 
				
		fEnclosingTypeSelection= new SelectionButtonDialogField(SWT.CHECK);
		fEnclosingTypeSelection.setDialogFieldListener(adapter);
		fEnclosingTypeSelection.setLabelText(NewWizardMessages.NewTypeWizardPage_enclosing_selection_label); 
		
		fEnclosingTypeDialogField= new StringButtonDialogField(adapter);
		fEnclosingTypeDialogField.setDialogFieldListener(adapter);
		fEnclosingTypeDialogField.setButtonLabel(NewWizardMessages.NewTypeWizardPage_enclosing_button); 
		
		fTypeNameDialogField= new StringDialogField();
		fTypeNameDialogField.setDialogFieldListener(adapter);
		fTypeNameDialogField.setLabelText(NewWizardMessages.NewTypeWizardPage_typename_label); 
		
		fSuperClassDialogField= new StringButtonDialogField(adapter);
		fSuperClassDialogField.setDialogFieldListener(adapter);
		fSuperClassDialogField.setLabelText(NewWizardMessages.NewTypeWizardPage_superclass_label); 
		fSuperClassDialogField.setButtonLabel(NewWizardMessages.NewTypeWizardPage_superclass_button); 
		
		String[] addButtons= new String[] {
			NewWizardMessages.NewTypeWizardPage_interfaces_add, 
			/* 1 */ null,
			NewWizardMessages.NewTypeWizardPage_interfaces_remove
		}; 
		fSuperInterfacesDialogField= new ListDialogField(adapter, addButtons, new InterfacesListLabelProvider());
		fSuperInterfacesDialogField.setDialogFieldListener(adapter);
		fSuperInterfacesDialogField.setTableColumns(new ListDialogField.ColumnsDescription(1, false));
		String interfaceLabel= getInterfaceLabel();
		fSuperInterfacesDialogField.setLabelText(interfaceLabel);
		fSuperInterfacesDialogField.setRemoveButtonIndex(2);
	
		String[] buttonNames1= new String[] {
			NewWizardMessages.NewTypeWizardPage_modifiers_public, 
			NewWizardMessages.NewTypeWizardPage_modifiers_default, 
			NewWizardMessages.NewTypeWizardPage_modifiers_private, 
			NewWizardMessages.NewTypeWizardPage_modifiers_protected
		};
		fAccMdfButtons= new SelectionButtonDialogFieldGroup(SWT.RADIO, buttonNames1, 4);
		fAccMdfButtons.setDialogFieldListener(adapter);
		fAccMdfButtons.setLabelText(NewWizardMessages.NewTypeWizardPage_modifiers_acc_label);		 
		fAccMdfButtons.setSelection(0, true);
		
		String[] buttonNames2;
		if (fTypeKind == CLASS_TYPE) {
			buttonNames2= new String[] {
				NewWizardMessages.NewTypeWizardPage_modifiers_abstract, 
				NewWizardMessages.NewTypeWizardPage_modifiers_final, 
				NewWizardMessages.NewTypeWizardPage_modifiers_static
			};
		} else {
		    if (fTypeKind == ENUM_TYPE || fTypeKind == ANNOTATION_TYPE) {
		        buttonNames2= new String[] {
					NewWizardMessages.NewTypeWizardPage_modifiers_abstract, 
					NewWizardMessages.NewTypeWizardPage_modifiers_static
		        };
		    }
		    else
		        buttonNames2= new String[] {};
		}

		fOtherMdfButtons= new SelectionButtonDialogFieldGroup(SWT.CHECK, buttonNames2, 4);
		fOtherMdfButtons.setDialogFieldListener(adapter);
		
		fAccMdfButtons.enableSelectionButton(PRIVATE_INDEX, false);
		fAccMdfButtons.enableSelectionButton(PROTECTED_INDEX, false);
		fOtherMdfButtons.enableSelectionButton(STATIC_INDEX, false);
		
		if (fTypeKind == ENUM_TYPE || fTypeKind == ANNOTATION_TYPE) {
		    fOtherMdfButtons.enableSelectionButton(ABSTRACT_INDEX, false);
		    fOtherMdfButtons.enableSelectionButton(ENUM_ANNOT_STATIC_INDEX, false);
		}
		
		fAddCommentButton= new SelectionButtonDialogField(SWT.CHECK);
		fAddCommentButton.setLabelText(NewWizardMessages.NewTypeWizardPage_addcomment_label); 
		fAddCommentButton.setSelection(JavaPlugin.getDefault().getDialogSettings().getBoolean(DIALOGSETTINGS_ADDCOMMENTS));
		
		fUseAddCommentButtonValue= false; // only used when enabled
		
		fCurrPackageCompletionProcessor= new JavaPackageCompletionProcessor();
		fEnclosingTypeCompletionProcessor= new JavaTypeCompletionProcessor(false, false);
		
		fPackageStatus= new StatusInfo();
		fEnclosingTypeStatus= new StatusInfo();
		
		fCanModifyPackage= true;
		fCanModifyEnclosingType= true;
		updateEnableState();
					
		fTypeNameStatus= new StatusInfo();
		fSuperClassStatus= new StatusInfo();
		fSuperInterfacesStatus= new StatusInfo();
		fModifierStatus= new StatusInfo();
	}
		
	private String getInterfaceLabel() {
	    if (fTypeKind != INTERFACE_TYPE)
	        return NewWizardMessages.NewTypeWizardPage_interfaces_class_label; 
	    return NewWizardMessages.NewTypeWizardPage_interfaces_ifc_label; 
	}
	
	/**
	 * Initializes all fields provided by the page with a given selection.
	 * 
	 * @param elem the selection used to initialize this page or <code>
	 * null</code> if no selection was available
	 */
	protected void initTypePage(IJavaElement elem) {
		String initSuperclass= "java.lang.Object"; //$NON-NLS-1$
		ArrayList initSuperinterfaces= new ArrayList(5);

		IPackageFragment pack= null;
		IType enclosingType= null;
				
		if (elem != null) {
			// evaluate the enclosing type
			pack= (IPackageFragment) elem.getAncestor(IJavaElement.PACKAGE_FRAGMENT);
			IType typeInCU= (IType) elem.getAncestor(IJavaElement.TYPE);
			if (typeInCU != null) {
				if (typeInCU.getCompilationUnit() != null) {
					enclosingType= typeInCU;
				}
			} else {
				ICompilationUnit cu= (ICompilationUnit) elem.getAncestor(IJavaElement.COMPILATION_UNIT);
				if (cu != null) {
					enclosingType= cu.findPrimaryType();
				}
			}
			
			try {
				IType type= null;
				if (elem.getElementType() == IJavaElement.TYPE) {
					type= (IType)elem;
					if (type.exists()) {
						String superName= JavaModelUtil.getFullyQualifiedName(type);
						if (type.isInterface()) {
							initSuperinterfaces.add(superName);
						} else {
							initSuperclass= superName;
						}
					}
				}
			} catch (JavaModelException e) {
				JavaPlugin.log(e);
				// ignore this exception now
			}			
		}
		
		String typeName= ""; //$NON-NLS-1$
		
		ITextSelection selection= getCurrentTextSelection();
		if (selection != null) {
			String text= selection.getText();
			if (JavaConventions.validateJavaTypeName(text).isOK()) {
				typeName= text;
			}
		}

		setPackageFragment(pack, true);
		setEnclosingType(enclosingType, true);
		setEnclosingTypeSelection(false, true);
	
		setTypeName(typeName, true);
		setSuperClass(initSuperclass, true);
		setSuperInterfaces(initSuperinterfaces, true);
	}		
	
	// -------- UI Creation ---------
	
	/**
	 * Creates a separator line. Expects a <code>GridLayout</code> with at least 1 column.
	 * 
	 * @param composite the parent composite
	 * @param nColumns number of columns to span
	 */
	protected void createSeparator(Composite composite, int nColumns) {
		(new Separator(SWT.SEPARATOR | SWT.HORIZONTAL)).doFillIntoGrid(composite, nColumns, convertHeightInCharsToPixels(1));		
	}

	/**
	 * Creates the controls for the package name field. Expects a <code>GridLayout</code> with at 
	 * least 4 columns.
	 * 
	 * @param composite the parent composite
	 * @param nColumns number of columns to span
	 */	
	protected void createPackageControls(Composite composite, int nColumns) {
		fPackageDialogField.doFillIntoGrid(composite, nColumns);
		Text text= fPackageDialogField.getTextControl(null);
		LayoutUtil.setWidthHint(text, getMaxFieldWidth());	
		LayoutUtil.setHorizontalGrabbing(text);
		ControlContentAssistHelper.createTextContentAssistant(text, fCurrPackageCompletionProcessor);
	}

	/**
	 * Creates the controls for the enclosing type name field. Expects a <code>GridLayout</code> with at 
	 * least 4 columns.
	 * 
	 * @param composite the parent composite
	 * @param nColumns number of columns to span
	 */		
	protected void createEnclosingTypeControls(Composite composite, int nColumns) {
		// #6891
		Composite tabGroup= new Composite(composite, SWT.NONE);
		GridLayout layout= new GridLayout();
		layout.marginWidth= 0;
		layout.marginHeight= 0;
 		tabGroup.setLayout(layout);

		fEnclosingTypeSelection.doFillIntoGrid(tabGroup, 1);

		Text text= fEnclosingTypeDialogField.getTextControl(composite);
		text.getAccessible().addAccessibleListener(new AccessibleAdapter() {
			public void getName(AccessibleEvent e) {
				e.result= NewWizardMessages.NewTypeWizardPage_enclosing_field_description;
			}
		});
		GridData gd= new GridData(GridData.FILL_HORIZONTAL);
		gd.widthHint= getMaxFieldWidth();
		gd.horizontalSpan= 2;
		text.setLayoutData(gd);
		
		Button button= fEnclosingTypeDialogField.getChangeControl(composite);
		gd= new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		gd.widthHint = SWTUtil.getButtonWidthHint(button);
		button.setLayoutData(gd);
		ControlContentAssistHelper.createTextContentAssistant(text, fEnclosingTypeCompletionProcessor);
	}	

	/**
	 * Creates the controls for the type name field. Expects a <code>GridLayout</code> with at 
	 * least 2 columns.
	 * 
	 * @param composite the parent composite
	 * @param nColumns number of columns to span
	 */		
	protected void createTypeNameControls(Composite composite, int nColumns) {
		fTypeNameDialogField.doFillIntoGrid(composite, nColumns - 1);
		DialogField.createEmptySpace(composite);
		
		LayoutUtil.setWidthHint(fTypeNameDialogField.getTextControl(null), getMaxFieldWidth());
	}

	/**
	 * Creates the controls for the modifiers radio/checkbox buttons. Expects a 
	 * <code>GridLayout</code> with at least 3 columns.
	 * 
	 * @param composite the parent composite
	 * @param nColumns number of columns to span
	 */		
	protected void createModifierControls(Composite composite, int nColumns) {
		LayoutUtil.setHorizontalSpan(fAccMdfButtons.getLabelControl(composite), 1);
		
		Control control= fAccMdfButtons.getSelectionButtonsGroup(composite);
		GridData gd= new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		gd.horizontalSpan= nColumns - 2;
		control.setLayoutData(gd);
		
		DialogField.createEmptySpace(composite);
		
		if (fTypeKind == CLASS_TYPE) {
			DialogField.createEmptySpace(composite);
			
			control= fOtherMdfButtons.getSelectionButtonsGroup(composite);
			gd= new GridData(GridData.HORIZONTAL_ALIGN_FILL);
			gd.horizontalSpan= nColumns - 2;
			control.setLayoutData(gd);		
	
			DialogField.createEmptySpace(composite);
		}
	}

	/**
	 * Creates the controls for the superclass name field. Expects a <code>GridLayout</code> 
	 * with at least 3 columns.
	 * 
	 * @param composite the parent composite
	 * @param nColumns number of columns to span
	 */		
	protected void createSuperClassControls(Composite composite, int nColumns) {
		fSuperClassDialogField.doFillIntoGrid(composite, nColumns);
		Text text= fSuperClassDialogField.getTextControl(null);
		LayoutUtil.setWidthHint(text, getMaxFieldWidth());
		
		JavaTypeCompletionProcessor superClassCompletionProcessor= new JavaTypeCompletionProcessor(false, false);
		superClassCompletionProcessor.setCompletionContextRequestor(new CompletionContextRequestor() {
			public StubTypeContext getStubTypeContext() {
				return getSuperClassStubTypeContext();
			}
		});

		ControlContentAssistHelper.createTextContentAssistant(text, superClassCompletionProcessor);
	}

	/**
	 * Creates the controls for the superclass name field. Expects a <code>GridLayout</code> with 
	 * at least 3 columns.
	 * 
	 * @param composite the parent composite
	 * @param nColumns number of columns to span
	 */			
	protected void createSuperInterfacesControls(Composite composite, int nColumns) {
		final String INTERFACE= "interface"; //$NON-NLS-1$
		fSuperInterfacesDialogField.doFillIntoGrid(composite, nColumns);
		final TableViewer tableViewer= fSuperInterfacesDialogField.getTableViewer();
		tableViewer.setColumnProperties(new String[] {INTERFACE});
		
		TableTextCellEditor cellEditor= new TableTextCellEditor(tableViewer, 0) {
		    protected void doSetFocus() {
		        if (text != null) {
		            text.setFocus();
		            text.setSelection(text.getText().length());
		            checkSelection();
		            checkDeleteable();
		            checkSelectable();
		        }
		    }
		};
		JavaTypeCompletionProcessor superInterfaceCompletionProcessor= new JavaTypeCompletionProcessor(false, false);
		superInterfaceCompletionProcessor.setCompletionContextRequestor(new CompletionContextRequestor() {
			public StubTypeContext getStubTypeContext() {
				return getSuperInterfaceStubTypeContext();
			}
		});
		SubjectControlContentAssistant contentAssistant= ControlContentAssistHelper.createJavaContentAssistant(superInterfaceCompletionProcessor);
		ContentAssistHandler.createHandlerForText(cellEditor.getText(), contentAssistant);
		cellEditor.setContentAssistant(contentAssistant);
		
		tableViewer.setCellEditors(new CellEditor[] { cellEditor });
		tableViewer.setCellModifier(new ICellModifier() {
			public void modify(Object element, String property, Object value) {
				if (element instanceof Item)
					element = ((Item) element).getData();
				
				((StringWrapper) element).setString((String) value);
				fSuperInterfacesDialogField.elementChanged(element);
			}
			public Object getValue(Object element, String property) {
				return ((StringWrapper) element).getString();
			}
			public boolean canModify(Object element, String property) {
				return true;
			}
		});
		tableViewer.getTable().addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent event) {
				if (event.keyCode == SWT.F2 && event.stateMask == 0) {
					ISelection selection= tableViewer.getSelection();
					if (! (selection instanceof IStructuredSelection))
						return;
					IStructuredSelection structuredSelection= (IStructuredSelection) selection;
					tableViewer.editElement(structuredSelection.getFirstElement(), 0);
				} 
			}
		});
		GridData gd= (GridData)fSuperInterfacesDialogField.getListControl(null).getLayoutData();
		if (fTypeKind == CLASS_TYPE) {
			gd.heightHint= convertHeightInCharsToPixels(3);
		} else {
			gd.heightHint= convertHeightInCharsToPixels(6);
		}
		gd.grabExcessVerticalSpace= false;
		gd.widthHint= getMaxFieldWidth();
	}
	
	/**
	 * Creates the controls for the preference page links. Expects a <code>GridLayout</code> with 
	 * at least 3 columns.
	 * 
	 * @param composite the parent composite
	 * @param nColumns number of columns to span
	 * 
	 * @since 3.1
	 */			
	protected void createCommentControls(Composite composite, int nColumns) {
    	Link link= new Link(composite, SWT.NONE);
    	link.setText(NewWizardMessages.NewTypeWizardPage_addcomment_description);
    	link.addSelectionListener(new TypeFieldsAdapter());
    	link.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, false, false, nColumns, 1));
		DialogField.createEmptySpace(composite);
		fAddCommentButton.doFillIntoGrid(composite, nColumns - 1);
	}


	
	/**
	 * Sets the focus on the type name input field.
	 */		
	protected void setFocus() {
		fTypeNameDialogField.setFocus();
	}
				
	// -------- TypeFieldsAdapter --------

	private class TypeFieldsAdapter implements IStringButtonAdapter, IDialogFieldListener, IListAdapter, SelectionListener {
		
		// -------- IStringButtonAdapter
		public void changeControlPressed(DialogField field) {
			typePageChangeControlPressed(field);
		}
		
		// -------- IListAdapter
		public void customButtonPressed(ListDialogField field, int index) {
			typePageCustomButtonPressed(field, index);
		}
		
		public void selectionChanged(ListDialogField field) {}
		
		// -------- IDialogFieldListener
		public void dialogFieldChanged(DialogField field) {
			typePageDialogFieldChanged(field);
		}
		
		public void doubleClicked(ListDialogField field) {
		}


		public void widgetSelected(SelectionEvent e) {
			typePageLinkActivated(e);
		}

		public void widgetDefaultSelected(SelectionEvent e) {
			typePageLinkActivated(e);
		}
	}
	
	private void typePageLinkActivated(SelectionEvent e) {
		IPackageFragmentRoot root= getPackageFragmentRoot();
		if (root != null) {
			PreferenceDialog dialog= PreferencesUtil.createPropertyDialogOn(getShell(), root.getJavaProject().getProject(), CodeTemplatePreferencePage.PROP_ID, null, null);
			dialog.open();
		} else {
			String title= NewWizardMessages.NewTypeWizardPage_configure_templates_title; 
			String message= NewWizardMessages.NewTypeWizardPage_configure_templates_message; 
			MessageDialog.openInformation(getShell(), title, message);
		}
	}
	
	private void typePageChangeControlPressed(DialogField field) {
		if (field == fPackageDialogField) {
			IPackageFragment pack= choosePackage();	
			if (pack != null) {
				fPackageDialogField.setText(pack.getElementName());
			}
		} else if (field == fEnclosingTypeDialogField) {
			IType type= chooseEnclosingType();
			if (type != null) {
				fEnclosingTypeDialogField.setText(JavaModelUtil.getFullyQualifiedName(type));
			}
		} else if (field == fSuperClassDialogField) {
			IType type= chooseSuperType();
			if (type != null) {
				fSuperClassDialogField.setText(JavaModelUtil.getFullyQualifiedName(type));
			}
		}
	}
	
	private void typePageCustomButtonPressed(DialogField field, int index) {		
		if (field == fSuperInterfacesDialogField) {
			chooseSuperInterfaces();
		}
	}
	
	/*
	 * A field on the type has changed. The fields' status and all dependent
	 * status are updated.
	 */
	private void typePageDialogFieldChanged(DialogField field) {
		String fieldName= null;
		if (field == fPackageDialogField) {
			fPackageStatus= packageChanged();
			updatePackageStatusLabel();
			fTypeNameStatus= typeNameChanged();
			fSuperClassStatus= superClassChanged();			
			fieldName= PACKAGE;
		} else if (field == fEnclosingTypeDialogField) {
			fEnclosingTypeStatus= enclosingTypeChanged();
			fTypeNameStatus= typeNameChanged();
			fSuperClassStatus= superClassChanged();				
			fieldName= ENCLOSING;
		} else if (field == fEnclosingTypeSelection) {
			updateEnableState();
			boolean isEnclosedType= isEnclosingTypeSelected();
			if (!isEnclosedType) {
				if (fAccMdfButtons.isSelected(PRIVATE_INDEX) || fAccMdfButtons.isSelected(PROTECTED_INDEX)) {
					fAccMdfButtons.setSelection(PRIVATE_INDEX, false);
					fAccMdfButtons.setSelection(PROTECTED_INDEX, false); 
					fAccMdfButtons.setSelection(PUBLIC_INDEX, true);
				}
				if (fOtherMdfButtons.isSelected(STATIC_INDEX)) {
					fOtherMdfButtons.setSelection(STATIC_INDEX, false);
				}
			}
			fAccMdfButtons.enableSelectionButton(PRIVATE_INDEX, isEnclosedType);
			fAccMdfButtons.enableSelectionButton(PROTECTED_INDEX, isEnclosedType);
			fOtherMdfButtons.enableSelectionButton(STATIC_INDEX, isEnclosedType);
			fTypeNameStatus= typeNameChanged();
			fSuperClassStatus= superClassChanged();
			fieldName= ENCLOSINGSELECTION;
		} else if (field == fTypeNameDialogField) {
			fTypeNameStatus= typeNameChanged();
			fieldName= TYPENAME;
		} else if (field == fSuperClassDialogField) {
			fSuperClassStatus= superClassChanged();
			fieldName= SUPER;
		} else if (field == fSuperInterfacesDialogField) {
			fSuperInterfacesStatus= superInterfacesChanged();
			fieldName= INTERFACES;
		} else if (field == fOtherMdfButtons || field == fAccMdfButtons) {
			fModifierStatus= modifiersChanged();
			fieldName= MODIFIERS;
		} else {
			fieldName= METHODS;
		}
		// tell all others
		handleFieldChanged(fieldName);
	}		
	
	// -------- update message ----------------		
	
	/*
	 * @see org.eclipse.jdt.ui.wizards.NewContainerWizardPage#handleFieldChanged(String)
	 */
	protected void handleFieldChanged(String fieldName) {
		super.handleFieldChanged(fieldName);
		if (fieldName == CONTAINER) {
			fPackageStatus= packageChanged();
			fEnclosingTypeStatus= enclosingTypeChanged();			
			fTypeNameStatus= typeNameChanged();
			fSuperClassStatus= superClassChanged();
			fSuperInterfacesStatus= superInterfacesChanged();
		}
	}
	
	// ---- set / get ----------------
	
	/**
	 * Returns the text of the package input field.
	 * 
	 * @return the text of the package input field
	 */
	public String getPackageText() {
		return fPackageDialogField.getText();
	}

	/**
	 * Returns the text of the enclosing type input field.
	 * 
	 * @return the text of the enclosing type input field
	 */	
	public String getEnclosingTypeText() {
		return fEnclosingTypeDialogField.getText();
	}	
	
	
	/**
	 * Returns the package fragment corresponding to the current input.
	 * 
	 * @return a package fragment or <code>null</code> if the input 
	 * could not be resolved.
	 */
	public IPackageFragment getPackageFragment() {
		if (!isEnclosingTypeSelected()) {
			return fCurrPackage;
		} else {
			if (fCurrEnclosingType != null) {
				return fCurrEnclosingType.getPackageFragment();
			}
		}
		return null;
	}
	
	/**
	 * Sets the package fragment to the given value. The method updates the model 
	 * and the text of the control.
	 * 
	 * @param pack the package fragment to be set
	 * @param canBeModified if <code>true</code> the package fragment is
	 * editable; otherwise it is read-only.
	 */
	public void setPackageFragment(IPackageFragment pack, boolean canBeModified) {
		fCurrPackage= pack;
		fCanModifyPackage= canBeModified;
		String str= (pack == null) ? "" : pack.getElementName(); //$NON-NLS-1$
		fPackageDialogField.setText(str);
		updateEnableState();
	}	

	/**
	 * Returns the enclosing type corresponding to the current input.
	 * 
	 * @return the enclosing type or <code>null</code> if the enclosing type is 
	 * not selected or the input could not be resolved
	 */
	public IType getEnclosingType() {
		if (isEnclosingTypeSelected()) {
			return fCurrEnclosingType;
		}
		return null;
	}

	/**
	 * Sets the enclosing type. The method updates the underlying model 
	 * and the text of the control.
	 * 
	 * @param type the enclosing type
	 * @param canBeModified if <code>true</code> the enclosing type field is
	 * editable; otherwise it is read-only.
	 */	
	public void setEnclosingType(IType type, boolean canBeModified) {
		fCurrEnclosingType= type;
		fCanModifyEnclosingType= canBeModified;
		String str= (type == null) ? "" : JavaModelUtil.getFullyQualifiedName(type); //$NON-NLS-1$
		fEnclosingTypeDialogField.setText(str);
		updateEnableState();
	}
	
	/**
	 * Returns the selection state of the enclosing type checkbox.
	 * 
	 * @return the selection state of the enclosing type checkbox
	 */
	public boolean isEnclosingTypeSelected() {
		return fEnclosingTypeSelection.isSelected();
	}

	/**
	 * Sets the enclosing type checkbox's selection state.
	 * 
	 * @param isSelected the checkbox's selection state
	 * @param canBeModified if <code>true</code> the enclosing type checkbox is
	 * modifiable; otherwise it is read-only.
	 */
	public void setEnclosingTypeSelection(boolean isSelected, boolean canBeModified) {
		fEnclosingTypeSelection.setSelection(isSelected);
		fEnclosingTypeSelection.setEnabled(canBeModified);
		updateEnableState();
	}
	
	/**
	 * Returns the type name entered into the type input field.
	 * 
	 * @return the type name
	 */
	public String getTypeName() {
		return fTypeNameDialogField.getText();
	}

	/**
	 * Sets the type name input field's text to the given value. Method doesn't update
	 * the model.
	 * 
	 * @param name the new type name
	 * @param canBeModified if <code>true</code> the type name field is
	 * editable; otherwise it is read-only.
	 */	
	public void setTypeName(String name, boolean canBeModified) {
		fTypeNameDialogField.setText(name);
		fTypeNameDialogField.setEnabled(canBeModified);
	}	
	
	/**
	 * Returns the selected modifiers.
	 * 
	 * @return the selected modifiers
	 * @see Flags 
	 */	
	public int getModifiers() {
		int mdf= 0;
		if (fAccMdfButtons.isSelected(PUBLIC_INDEX)) {
			mdf+= F_PUBLIC;
		} else if (fAccMdfButtons.isSelected(PRIVATE_INDEX)) {
			mdf+= F_PRIVATE;
		} else if (fAccMdfButtons.isSelected(PROTECTED_INDEX)) {	
			mdf+= F_PROTECTED;
		}
		if (fOtherMdfButtons.isSelected(ABSTRACT_INDEX)) {	
			mdf+= F_ABSTRACT;
		}
		if (fOtherMdfButtons.isSelected(FINAL_INDEX)) {	
			mdf+= F_FINAL;
		}
		if (fOtherMdfButtons.isSelected(STATIC_INDEX)) {	
			mdf+= F_STATIC;
		}
		return mdf;
	}

	/**
	 * Sets the modifiers.
	 * 
	 * @param modifiers <code>F_PUBLIC</code>, <code>F_PRIVATE</code>, 
	 * <code>F_PROTECTED</code>, <code>F_ABSTRACT</code>, <code>F_FINAL</code>
	 * or <code>F_STATIC</code> or a valid combination.
	 * @param canBeModified if <code>true</code> the modifier fields are
	 * editable; otherwise they are read-only
	 * @see Flags 
	 */		
	public void setModifiers(int modifiers, boolean canBeModified) {
		if (Flags.isPublic(modifiers)) {
			fAccMdfButtons.setSelection(PUBLIC_INDEX, true);
		} else if (Flags.isPrivate(modifiers)) {
			fAccMdfButtons.setSelection(PRIVATE_INDEX, true);
		} else if (Flags.isProtected(modifiers)) {
			fAccMdfButtons.setSelection(PROTECTED_INDEX, true);
		} else {
			fAccMdfButtons.setSelection(DEFAULT_INDEX, true);
		}
		if (Flags.isAbstract(modifiers)) {
			fOtherMdfButtons.setSelection(ABSTRACT_INDEX, true);
		}
		if (Flags.isFinal(modifiers)) {
			fOtherMdfButtons.setSelection(FINAL_INDEX, true);
		}		
		if (Flags.isStatic(modifiers)) {
			fOtherMdfButtons.setSelection(STATIC_INDEX, true);
		}
		
		fAccMdfButtons.setEnabled(canBeModified);
		fOtherMdfButtons.setEnabled(canBeModified);
	}
		
	/**
	 * Returns the content of the superclass input field.
	 * 
	 * @return the superclass name
	 */
	public String getSuperClass() {
		return fSuperClassDialogField.getText();
	}

	/**
	 * Sets the super class name.
	 * 
	 * @param name the new superclass name
	 * @param canBeModified  if <code>true</code> the superclass name field is
	 * editable; otherwise it is read-only.
	 */		
	public void setSuperClass(String name, boolean canBeModified) {
		fSuperClassDialogField.setText(name);
		fSuperClassDialogField.setEnabled(canBeModified);
	}	
	
	/**
	 * Returns the chosen super interfaces.
	 * 
	 * @return a list of chosen super interfaces. The list's elements
	 * are of type <code>String</code>
	 */
	public List getSuperInterfaces() {
		List interfaces= fSuperInterfacesDialogField.getElements();
		ArrayList result= new ArrayList(interfaces.size());
		for (Iterator iter= interfaces.iterator(); iter.hasNext();) {
			StringWrapper superInterface= (StringWrapper) iter.next();
			result.add(superInterface.getString());
		}
		return result;
	}

	/**
	 * Sets the super interfaces.
	 * 
	 * @param interfacesNames a list of super interface. The method requires that
	 * the list's elements are of type <code>String</code>
	 * @param canBeModified if <code>true</code> the super interface field is
	 * editable; otherwise it is read-only.
	 */	
	public void setSuperInterfaces(List interfacesNames, boolean canBeModified) {
		ArrayList interfaces= new ArrayList(interfacesNames.size());
		for (Iterator iter= interfacesNames.iterator(); iter.hasNext();) {
			String name= (String) iter.next();
			interfaces.add(new StringWrapper(name));
		}
		fSuperInterfacesDialogField.setElements(interfaces);
		fSuperInterfacesDialogField.setEnabled(canBeModified);
	}
	
	/**
	 * Sets 'Add comment' checkbox. The value set will only be used when creating source when
	 * the comment control is enabled (see {@link #enableCommentControl(boolean)}
	 * 
	 * @param doAddComments if <code>true</code>, comments are added.
	 * @param canBeModified if <code>true</code> check box is
	 * editable; otherwise it is read-only.
	 * 	@since 3.1
	 */	
	public void setAddComments(boolean doAddComments, boolean canBeModified) {
		fAddCommentButton.setSelection(doAddComments);
		fAddCommentButton.setEnabled(canBeModified);
	}
	
	/**
	 * Sets to use the 'Add comment' checkbox value. Clients that use the 'Add comment' checkbox
	 * additionally have to enable the control. This has been added for backwards compatibility.
	 * 
	 * @param useAddCommentValue if <code>true</code>, 
	 * 	@since 3.1
	 */	
	public void enableCommentControl(boolean useAddCommentValue) {
		fUseAddCommentButtonValue= useAddCommentValue;
	}
	
	
	/**
	 * Returns if comments are added. This method can be overridden by clients.
	 * The selection of the comment control is taken if enabled (see {@link #enableCommentControl(boolean)}, otherwise
	 * the settings as specified in the preferences is used.
	 * 
	 * @return Returns <code>true</code> if comments can be added
	 * @since 3.1
	 */	
	public boolean isAddComments() {
		if (fUseAddCommentButtonValue) {
			return fAddCommentButton.isSelected();
		}
		IPackageFragmentRoot root= getPackageFragmentRoot();
		IJavaProject project= (root != null) ? root.getJavaProject() : null; // use project settings 
		return StubUtility.doAddComments(project); 
	}
			
	/**
	 * Returns the resource handle that corresponds to the compilation unit to was or
	 * will be created or modified.
	 * @return A resource or null if the page contains illegal values.
	 * @since 3.0
	 */
	public IResource getModifiedResource() {
		IType enclosing= getEnclosingType();
		if (enclosing != null) {
			return enclosing.getResource();
		}
		IPackageFragment pack= getPackageFragment();
		if (pack != null) {
			return pack.getCompilationUnit(getTypeNameWithoutParameters() + ".java").getResource(); //$NON-NLS-1$
		}
		return null;
	}
			
	// ----------- validation ----------
			
	/*
	 * @see org.eclipse.jdt.ui.wizards.NewContainerWizardPage#containerChanged()
	 */
	protected IStatus containerChanged() {
		IStatus status= super.containerChanged();
	    if ((fTypeKind == ANNOTATION_TYPE || fTypeKind == ENUM_TYPE) && !status.matches(IStatus.ERROR)) {
	    	IPackageFragmentRoot root= getPackageFragmentRoot();
	    	if (root != null && !JavaModelUtil.is50OrHigher(root.getJavaProject())) {
				return new StatusInfo(IStatus.WARNING, Messages.format(NewWizardMessages.NewTypeWizardPage_warning_NotJDKCompliant, root.getJavaProject().getElementName()));  
	    	}
	    	if (fTypeKind == ENUM_TYPE) {
		    	try {
		    	    // if findType(...) == null then Enum is unavailable
		    	    if (findType(root.getJavaProject(), "java.lang.Enum") == null) //$NON-NLS-1$
		    	        return new StatusInfo(IStatus.WARNING, NewWizardMessages.NewTypeWizardPage_warning_EnumClassNotFound);  
		    	} catch (JavaModelException e) {
		    	    JavaPlugin.log(e);
		    	}
	    	}
	    }
		
		fCurrPackageCompletionProcessor.setPackageFragmentRoot(getPackageFragmentRoot());
		if (getPackageFragmentRoot() != null) {
			fEnclosingTypeCompletionProcessor.setPackageFragment(getPackageFragmentRoot().getPackageFragment("")); //$NON-NLS-1$
		}
		return status;
	}
	
	/**
	 * A hook method that gets called when the package field has changed. The method 
	 * validates the package name and returns the status of the validation. The validation
	 * also updates the package fragment model.
	 * <p>
	 * Subclasses may extend this method to perform their own validation.
	 * </p>
	 * 
	 * @return the status of the validation
	 */
	protected IStatus packageChanged() {
		StatusInfo status= new StatusInfo();
		fPackageDialogField.enableButton(getPackageFragmentRoot() != null);
		
		String packName= getPackageText();
		if (packName.length() > 0) {
			IStatus val= JavaConventions.validatePackageName(packName);
			if (val.getSeverity() == IStatus.ERROR) {
				status.setError(Messages.format(NewWizardMessages.NewTypeWizardPage_error_InvalidPackageName, val.getMessage())); 
				return status;
			} else if (val.getSeverity() == IStatus.WARNING) {
				status.setWarning(Messages.format(NewWizardMessages.NewTypeWizardPage_warning_DiscouragedPackageName, val.getMessage())); 
				// continue
			}
		} else {
			status.setWarning(NewWizardMessages.NewTypeWizardPage_warning_DefaultPackageDiscouraged); 
		}
		
		IPackageFragmentRoot root= getPackageFragmentRoot();
		if (root != null) {
			if (root.getJavaProject().exists() && packName.length() > 0) {
				try {
					IPath rootPath= root.getPath();
					IPath outputPath= root.getJavaProject().getOutputLocation();
					if (rootPath.isPrefixOf(outputPath) && !rootPath.equals(outputPath)) {
						// if the bin folder is inside of our root, don't allow to name a package
						// like the bin folder
						IPath packagePath= rootPath.append(packName.replace('.', '/'));
						if (outputPath.isPrefixOf(packagePath)) {
							status.setError(NewWizardMessages.NewTypeWizardPage_error_ClashOutputLocation); 
							return status;
						}
					}
				} catch (JavaModelException e) {
					JavaPlugin.log(e);
					// let pass			
				}
			}
			
			fCurrPackage= root.getPackageFragment(packName);
		} else {
			status.setError(""); //$NON-NLS-1$
		}
		return status;
	}

	/*
	 * Updates the 'default' label next to the package field.
	 */	
	private void updatePackageStatusLabel() {
		String packName= getPackageText();
		
		if (packName.length() == 0) {
			fPackageDialogField.setStatus(NewWizardMessages.NewTypeWizardPage_default); 
		} else {
			fPackageDialogField.setStatus(""); //$NON-NLS-1$
		}
	}
	
	/*
	 * Updates the enable state of buttons related to the enclosing type selection checkbox.
	 */
	private void updateEnableState() {
		boolean enclosing= isEnclosingTypeSelected();
		fPackageDialogField.setEnabled(fCanModifyPackage && !enclosing);
		fEnclosingTypeDialogField.setEnabled(fCanModifyEnclosingType && enclosing);
		if (fTypeKind == ENUM_TYPE || fTypeKind == ANNOTATION_TYPE) {
		    fOtherMdfButtons.enableSelectionButton(ABSTRACT_INDEX, enclosing);
		    fOtherMdfButtons.enableSelectionButton(ENUM_ANNOT_STATIC_INDEX, enclosing);
		}
	}	

	/**
	 * Hook method that gets called when the enclosing type name has changed. The method 
	 * validates the enclosing type and returns the status of the validation. It also updates the 
	 * enclosing type model.
	 * <p>
	 * Subclasses may extend this method to perform their own validation.
	 * </p>
	 * 
	 * @return the status of the validation
	 */
	protected IStatus enclosingTypeChanged() {
		StatusInfo status= new StatusInfo();
		fCurrEnclosingType= null;
		
		IPackageFragmentRoot root= getPackageFragmentRoot();
		
		fEnclosingTypeDialogField.enableButton(root != null);
		if (root == null) {
			status.setError(""); //$NON-NLS-1$
			return status;
		}
		
		String enclName= getEnclosingTypeText();
		if (enclName.length() == 0) {
			status.setError(NewWizardMessages.NewTypeWizardPage_error_EnclosingTypeEnterName); 
			return status;
		}
		try {
			IType type= findType(root.getJavaProject(), enclName);
			if (type == null) {
				status.setError(NewWizardMessages.NewTypeWizardPage_error_EnclosingTypeNotExists); 
				return status;
			}

			if (type.getCompilationUnit() == null) {
				status.setError(NewWizardMessages.NewTypeWizardPage_error_EnclosingNotInCU); 
				return status;
			}
			if (!JavaModelUtil.isEditable(type.getCompilationUnit())) {
				status.setError(NewWizardMessages.NewTypeWizardPage_error_EnclosingNotEditable); 
				return status;			
			}
			
			fCurrEnclosingType= type;
			IPackageFragmentRoot enclosingRoot= JavaModelUtil.getPackageFragmentRoot(type);
			if (!enclosingRoot.equals(root)) {
				status.setWarning(NewWizardMessages.NewTypeWizardPage_warning_EnclosingNotInSourceFolder); 
			}
			return status;
		} catch (JavaModelException e) {
			status.setError(NewWizardMessages.NewTypeWizardPage_error_EnclosingTypeNotExists); 
			JavaPlugin.log(e);
			return status;
		}
	}
	
	private IType findType(IJavaProject project, String typeName) throws JavaModelException {
		if (project.exists()) {
			return project.findType(typeName);
		}
		return null;
	}
	
	private String getTypeNameWithoutParameters() {
		String typeNameWithParameters= getTypeName();
		int angleBracketOffset= typeNameWithParameters.indexOf('<');
		if (angleBracketOffset == -1) {
			return typeNameWithParameters;
		} else {
			return typeNameWithParameters.substring(0, angleBracketOffset);
		}
	}
	
	/**
	 * Hook method that gets called when the type name has changed. The method validates the 
	 * type name and returns the status of the validation.
	 * <p>
	 * Subclasses may extend this method to perform their own validation.
	 * </p>
	 * 
	 * @return the status of the validation
	 */
	protected IStatus typeNameChanged() {
		StatusInfo status= new StatusInfo();
		fCurrType= null;
		String typeNameWithParameters= getTypeName();
		// must not be empty
		if (typeNameWithParameters.length() == 0) {
			status.setError(NewWizardMessages.NewTypeWizardPage_error_EnterTypeName); 
			return status;
		}
		
		String typeName= getTypeNameWithoutParameters();
		if (typeName.indexOf('.') != -1) {
			status.setError(NewWizardMessages.NewTypeWizardPage_error_QualifiedName); 
			return status;
		}
		IStatus val= JavaConventions.validateJavaTypeName(typeName);
		if (val.getSeverity() == IStatus.ERROR) {
			status.setError(Messages.format(NewWizardMessages.NewTypeWizardPage_error_InvalidTypeName, val.getMessage())); 
			return status;
		} else if (val.getSeverity() == IStatus.WARNING) {
			status.setWarning(Messages.format(NewWizardMessages.NewTypeWizardPage_warning_TypeNameDiscouraged, val.getMessage())); 
			// continue checking
		}		

		// must not exist
		if (!isEnclosingTypeSelected()) {
			IPackageFragment pack= getPackageFragment();
			if (pack != null) {
				ICompilationUnit cu= pack.getCompilationUnit(typeName + ".java"); //$NON-NLS-1$
				fCurrType= cu.getType(typeName);
				IResource resource= cu.getResource();

				if (resource.exists()) {
					status.setError(NewWizardMessages.NewTypeWizardPage_error_TypeNameExists); 
					return status;
				}
				IPath location= resource.getLocation();
				if (location != null && location.toFile().exists()) {
					status.setError(NewWizardMessages.NewTypeWizardPage_error_TypeNameExistsDifferentCase); 
					return status;
				}
			}
		} else {
			IType type= getEnclosingType();
			if (type != null) {
				fCurrType= type.getType(typeName);
				if (fCurrType.exists()) {
					status.setError(NewWizardMessages.NewTypeWizardPage_error_TypeNameExists); 
					return status;
				}
			}
		}
		
		if (typeNameWithParameters != typeName) {
			if (getPackageFragmentRoot() != null && ! JavaModelUtil.is50OrHigher(getPackageFragmentRoot().getJavaProject())) {
				status.setError(NewWizardMessages.NewTypeWizardPage_error_TypeParameters); 
				return status;
			}
			String typeDeclaration= "class " + typeNameWithParameters + " {}"; //$NON-NLS-1$//$NON-NLS-2$
			ASTParser parser= ASTParser.newParser(AST.JLS3);
			parser.setSource(typeDeclaration.toCharArray());
			if (getPackageFragmentRoot() != null) {
				parser.setProject(getPackageFragmentRoot().getJavaProject());
			}
			CompilationUnit compilationUnit= (CompilationUnit) parser.createAST(null);
			IProblem[] problems= compilationUnit.getProblems();
			if (problems.length > 0) {
				status.setError(Messages.format(NewWizardMessages.NewTypeWizardPage_error_InvalidTypeName, problems[0].getMessage())); 
				return status;
			}
		}
		return status;
	}
	
	/**
	 * Hook method that gets called when the superclass name has changed. The method 
	 * validates the superclass name and returns the status of the validation.
	 * <p>
	 * Subclasses may extend this method to perform their own validation.
	 * </p>
	 * 
	 * @return the status of the validation
	 */
	protected IStatus superClassChanged() {
		StatusInfo status= new StatusInfo();
		IPackageFragmentRoot root= getPackageFragmentRoot();
		fSuperClassDialogField.enableButton(root != null);
		
		fSuperClassStubTypeContext= null;
		
		String sclassName= getSuperClass();
		if (sclassName.length() == 0) {
			// accept the empty field (stands for java.lang.Object)
			return status;
		}
		
		if (root != null) {
			Type type= TypeContextChecker.parseSuperClass(sclassName);
			if (type == null) {
				status.setError(NewWizardMessages.NewTypeWizardPage_error_InvalidSuperClassName); 
				return status;
			}
			if (type instanceof ParameterizedType && ! JavaModelUtil.is50OrHigher(root.getJavaProject())) {
				status.setError(NewWizardMessages.NewTypeWizardPage_error_SuperClassNotParameterized); 
				return status;
			}
		} else {
			status.setError(""); //$NON-NLS-1$
		}
		return status;
	}

	private StubTypeContext getSuperClassStubTypeContext() {
		if (fSuperClassStubTypeContext == null) {
			String typeName;
			if (fCurrType != null) {
				typeName= getTypeName();
			} else {
				typeName= JavaTypeCompletionProcessor.DUMMY_CLASS_NAME;
			}
			fSuperClassStubTypeContext= TypeContextChecker.createSuperClassStubTypeContext(typeName, getEnclosingType(), getPackageFragment());
		}
		return fSuperClassStubTypeContext;
	}

	/**
	 * Hook method that gets called when the list of super interface has changed. The method 
	 * validates the super interfaces and returns the status of the validation.
	 * <p>
	 * Subclasses may extend this method to perform their own validation.
	 * </p>
	 * 
	 * @return the status of the validation
	 */
	protected IStatus superInterfacesChanged() {
		StatusInfo status= new StatusInfo();
		
		IPackageFragmentRoot root= getPackageFragmentRoot();
		fSuperInterfacesDialogField.enableButton(0, root != null);
						
		if (root != null) {
			List elements= fSuperInterfacesDialogField.getElements();
			int nElements= elements.size();
			for (int i= 0; i < nElements; i++) {
				String intfname= ((StringWrapper) elements.get(i)).getString();
				Type type= TypeContextChecker.parseSuperInterface(intfname);
				if (type == null) {
					status.setError(Messages.format(NewWizardMessages.NewTypeWizardPage_error_InvalidSuperInterfaceName, intfname)); 
					return status;
				}
				if (type instanceof ParameterizedType && ! JavaModelUtil.is50OrHigher(root.getJavaProject())) {
					status.setError(Messages.format(NewWizardMessages.NewTypeWizardPage_error_SuperInterfaceNotParameterized, intfname)); 
					return status;
				}
			}				
		}
		return status;
	}

	private StubTypeContext getSuperInterfaceStubTypeContext() {
		if (fSuperInterfaceStubTypeContext == null) {
			String typeName;
			if (fCurrType != null) {
				typeName= getTypeName();
			} else {
				typeName= JavaTypeCompletionProcessor.DUMMY_CLASS_NAME;
			}
			fSuperInterfaceStubTypeContext= TypeContextChecker.createSuperInterfaceStubTypeContext(typeName, getEnclosingType(), getPackageFragment());
		}
		return fSuperInterfaceStubTypeContext;
	}
	
	/**
	 * Hook method that gets called when the modifiers have changed. The method validates 
	 * the modifiers and returns the status of the validation.
	 * <p>
	 * Subclasses may extend this method to perform their own validation.
	 * </p>
	 * 
	 * @return the status of the validation
	 */
	protected IStatus modifiersChanged() {
		StatusInfo status= new StatusInfo();
		int modifiers= getModifiers();
		if (Flags.isFinal(modifiers) && Flags.isAbstract(modifiers)) {
			status.setError(NewWizardMessages.NewTypeWizardPage_error_ModifiersFinalAndAbstract); 
		}
		return status;
	}
	
	// selection dialogs
	
	private IPackageFragment choosePackage() {
		IPackageFragmentRoot froot= getPackageFragmentRoot();
		IJavaElement[] packages= null;
		try {
			if (froot != null && froot.exists()) {
				packages= froot.getChildren();
			}
		} catch (JavaModelException e) {
			JavaPlugin.log(e);
		}
		if (packages == null) {
			packages= new IJavaElement[0];
		}
		
		ElementListSelectionDialog dialog= new ElementListSelectionDialog(getShell(), new JavaElementLabelProvider(JavaElementLabelProvider.SHOW_DEFAULT));
		dialog.setIgnoreCase(false);
		dialog.setTitle(NewWizardMessages.NewTypeWizardPage_ChoosePackageDialog_title); 
		dialog.setMessage(NewWizardMessages.NewTypeWizardPage_ChoosePackageDialog_description); 
		dialog.setEmptyListMessage(NewWizardMessages.NewTypeWizardPage_ChoosePackageDialog_empty); 
		dialog.setElements(packages);
		IPackageFragment pack= getPackageFragment();
		if (pack != null) {
			dialog.setInitialSelections(new Object[] { pack });
		}

		if (dialog.open() == Window.OK) {
			return (IPackageFragment) dialog.getFirstResult();
		}
		return null;
	}
	
	private IType chooseEnclosingType() {
		IPackageFragmentRoot root= getPackageFragmentRoot();
		if (root == null) {
			return null;
		}
		
		IJavaSearchScope scope= SearchEngine.createJavaSearchScope(new IJavaElement[] { root });
	
		TypeSelectionDialog2 dialog= new TypeSelectionDialog2(getShell(), 
			false, getWizard().getContainer(), scope, IJavaSearchConstants.TYPE);
		dialog.setTitle(NewWizardMessages.NewTypeWizardPage_ChooseEnclosingTypeDialog_title); 
		dialog.setMessage(NewWizardMessages.NewTypeWizardPage_ChooseEnclosingTypeDialog_description); 
		dialog.setFilter(Signature.getSimpleName(getEnclosingTypeText()));
		
		if (dialog.open() == Window.OK) {	
			return (IType) dialog.getFirstResult();
		}
		return null;
	}	
	
	private IType chooseSuperType() {
		IPackageFragmentRoot root= getPackageFragmentRoot();
		if (root == null) {
			return null;
		}	
		
		IJavaElement[] elements= new IJavaElement[] { root.getJavaProject() };
		IJavaSearchScope scope= SearchEngine.createJavaSearchScope(elements);

		TypeSelectionDialog2 dialog= new TypeSelectionDialog2(getShell(), false,
			getWizard().getContainer(), scope, IJavaSearchConstants.CLASS);
		dialog.setTitle(NewWizardMessages.NewTypeWizardPage_SuperClassDialog_title); 
		dialog.setMessage(NewWizardMessages.NewTypeWizardPage_SuperClassDialog_message); 
		dialog.setFilter(getSuperClass());

		if (dialog.open() == Window.OK) {
			return (IType) dialog.getFirstResult();
		}
		return null;
	}
	
	private void chooseSuperInterfaces() {
		IPackageFragmentRoot root= getPackageFragmentRoot();
		if (root == null) {
			return;
		}	

		IJavaProject project= root.getJavaProject();
		SuperInterfaceSelectionDialog dialog= new SuperInterfaceSelectionDialog(getShell(), getWizard().getContainer(), fSuperInterfacesDialogField, project);
		dialog.setTitle(getInterfaceDialogTitle());
		dialog.setMessage(NewWizardMessages.NewTypeWizardPage_InterfacesDialog_message); 
		dialog.open();
		List interfaces= fSuperInterfacesDialogField.getElements();
		if (interfaces.size() > 0) {
			Object element= interfaces.get(interfaces.size() - 1);
			TableViewer tableViewer= fSuperInterfacesDialogField.getTableViewer();
			tableViewer.refresh(element);
			tableViewer.editElement(element, 0);
		}
		return;
	}
	
	private String getInterfaceDialogTitle() {
	    if (fTypeKind == INTERFACE_TYPE)
	        return NewWizardMessages.NewTypeWizardPage_InterfacesDialog_interface_title; 
	    return NewWizardMessages.NewTypeWizardPage_InterfacesDialog_class_title; 
	}
	
	
		
	// ---- creation ----------------

	/**
	 * Creates the new type using the entered field values.
	 * 
	 * @param monitor a progress monitor to report progress.
	 * @throws CoreException Thrown when the creation failed.
	 * @throws InterruptedException Thrown when the operation was cancelled.
	 */
	public void createType(IProgressMonitor monitor) throws CoreException, InterruptedException {		
		if (monitor == null) {
			monitor= new NullProgressMonitor();
		}

		monitor.beginTask(NewWizardMessages.NewTypeWizardPage_operationdesc, 10); 
		
		ICompilationUnit createdWorkingCopy= null;
		try {
			IPackageFragmentRoot root= getPackageFragmentRoot();
			IPackageFragment pack= getPackageFragment();
			if (pack == null) {
				pack= root.getPackageFragment(""); //$NON-NLS-1$
			}
			
			if (!pack.exists()) {
				String packName= pack.getElementName();
				pack= root.createPackageFragment(packName, true, null);
			}		
			
			monitor.worked(1);
			
			String clName= getTypeNameWithoutParameters();
			
			boolean isInnerClass= isEnclosingTypeSelected();
			
			IType createdType;
			ImportsManager imports;
			int indent= 0;

			String lineDelimiter= null;	
			if (!isInnerClass) {
				lineDelimiter= StubUtility.getLineDelimiterUsed(pack.getJavaProject());
										
				ICompilationUnit parentCU= pack.createCompilationUnit(clName + ".java", "", false, new SubProgressMonitor(monitor, 2)); //$NON-NLS-1$ //$NON-NLS-2$
				// create a working copy with a new owner
				createdWorkingCopy= parentCU.getWorkingCopy(null);
				
				// use the compiler template with an empty type content to get the imports right
				String content= CodeGeneration.getCompilationUnitContent(createdWorkingCopy, getFileComment(createdWorkingCopy, lineDelimiter), getTypeComment(createdWorkingCopy, lineDelimiter), "", lineDelimiter); //$NON-NLS-1$
				if (content != null) {
					createdWorkingCopy.getBuffer().setContents(content);
				}
							
				imports= new ImportsManager(createdWorkingCopy);
				// add an import that will be removed again. Having this import solves 14661
				imports.addImport(JavaModelUtil.concatenateName(pack.getElementName(), clName));
				
				String typeContent= constructTypeStub(imports, lineDelimiter);
				
				String cuContent= constructCUContent(parentCU, typeContent, lineDelimiter);
				
				createdWorkingCopy.getBuffer().setContents(cuContent);
				
				createdType= createdWorkingCopy.getType(clName);
			} else {
				IType enclosingType= getEnclosingType();
					
				ICompilationUnit parentCU= enclosingType.getCompilationUnit();
				imports= new ImportsManager(parentCU);
	
				// add imports that will be removed again. Having the imports solves 14661
				IType[] topLevelTypes= parentCU.getTypes();
				for (int i= 0; i < topLevelTypes.length; i++) {
					imports.addImport(topLevelTypes[i].getFullyQualifiedName('.'));
				}
				
				lineDelimiter= StubUtility.getLineDelimiterUsed(enclosingType);
				StringBuffer content= new StringBuffer();
				
				String comment= getTypeComment(parentCU, lineDelimiter);
				if (comment != null) {
					content.append(comment);
					content.append(lineDelimiter);
				}

				content.append(constructTypeStub(imports, lineDelimiter));
				IJavaElement[] elems= enclosingType.getChildren();
				IJavaElement sibling= elems.length > 0 ? elems[0] : null;
				
				createdType= enclosingType.createType(content.toString(), sibling, false, new SubProgressMonitor(monitor, 1));
			
				indent= StubUtility.getIndentUsed(enclosingType) + 1;
			}
			if (monitor.isCanceled()) {
				throw new InterruptedException();
			}
			
			// add imports for superclass/interfaces, so types can be resolved correctly
			
			ICompilationUnit cu= createdType.getCompilationUnit();	
			boolean needsSave= !cu.isWorkingCopy();
			
			imports.create(needsSave, new SubProgressMonitor(monitor, 1));
				
			JavaModelUtil.reconcile(cu);

			if (monitor.isCanceled()) {
				throw new InterruptedException();
			}
			
			// set up again
			imports= new ImportsManager(imports.getCompilationUnit(), imports.getAddedTypes());
			
			createTypeMembers(createdType, imports, new SubProgressMonitor(monitor, 1));
	
			// add imports
			imports.create(needsSave, new SubProgressMonitor(monitor, 1));
			
			removeUnusedImports(cu, imports.getAddedTypes(), needsSave);
			
			JavaModelUtil.reconcile(cu);
			
			ISourceRange range= createdType.getSourceRange();
			
			IBuffer buf= cu.getBuffer();
			String originalContent= buf.getText(range.getOffset(), range.getLength());
			
			String formattedContent= CodeFormatterUtil.format(CodeFormatter.K_CLASS_BODY_DECLARATIONS, originalContent, indent, null, lineDelimiter, pack.getJavaProject());
			formattedContent= Strings.trimLeadingTabsAndSpaces(formattedContent);
			buf.replace(range.getOffset(), range.getLength(), formattedContent);
			if (!isInnerClass) {
				String fileComment= getFileComment(cu);
				if (fileComment != null && fileComment.length() > 0) {
					buf.replace(0, 0, fileComment + lineDelimiter);
				}
				cu.commitWorkingCopy(false, new SubProgressMonitor(monitor, 1));
			} else {
				if (needsSave) {
					buf.save(null, false);
				}
				monitor.worked(1);
			}

			if (createdWorkingCopy != null) {
				fCreatedType= (IType) createdType.getPrimaryElement();
			} else {
				fCreatedType= createdType;
			}
		} finally {
			if (createdWorkingCopy != null) {
				createdWorkingCopy.discardWorkingCopy();
			}
			monitor.done();
		}
	}	
	
	private void removeUnusedImports(ICompilationUnit cu, Set addedTypes, boolean needsSave) throws CoreException {
		ASTParser parser= ASTParser.newParser(ASTProvider.AST_LEVEL);
		parser.setSource(cu);
		parser.setResolveBindings(true);
		CompilationUnit root= (CompilationUnit) parser.createAST(null);
		List importsDecls= root.imports();
		if (importsDecls.isEmpty()) {
			return;
		}
		
		int importsEnd= ASTNodes.getExclusiveEnd((ASTNode) importsDecls.get(importsDecls.size() - 1));
		IProblem[] problems= root.getProblems();
		ArrayList res= new ArrayList();
		for (int i= 0; i < problems.length; i++) {
			IProblem curr= problems[i];
			if (curr.getSourceEnd() < importsEnd) {
				int id= curr.getID();
				if (id == IProblem.UnusedImport || id == IProblem.NotVisibleType) { // not visibles hide unused -> remove both  	 
					String imp= problems[i].getArguments()[0];
					res.add(imp);
				}
			}
		}
		if (!res.isEmpty()) {
			ImportsManager imports= new ImportsManager(cu, addedTypes);
			for (int i= 0; i < res.size(); i++) {
				String curr= (String) res.get(i);
				imports.removeImport(curr);
			}
			imports.create(needsSave, null);
		}
	}

	/**
	 * Uses the New Java file template from the code template page to generate a
	 * compilation unit with the given type content.
	 * @param cu The new created compilation unit
	 * @param typeContent The content of the type, including signature and type
	 * body.
	 * @param lineDelimiter The line delimiter to be used.
	 * @return String Returns the result of evaluating the new file template
	 * with the given type content.
	 * @throws CoreException
	 * @since 2.1
	 */
	protected String constructCUContent(ICompilationUnit cu, String typeContent, String lineDelimiter) throws CoreException {
		String fileComment= getFileComment(cu, lineDelimiter);
		String typeComment= getTypeComment(cu, lineDelimiter);
		IPackageFragment pack= (IPackageFragment) cu.getParent();
		String content= CodeGeneration.getCompilationUnitContent(cu, fileComment, typeComment, typeContent, lineDelimiter);
		if (content != null) {
			ASTParser parser= ASTParser.newParser(AST.JLS3);
			parser.setProject(cu.getJavaProject());
			parser.setSource(content.toCharArray());
			CompilationUnit unit= (CompilationUnit) parser.createAST(null);
			if ((pack.isDefaultPackage() || unit.getPackage() != null) && !unit.types().isEmpty()) {
				return content;
			}
		}
		StringBuffer buf= new StringBuffer();
		if (!pack.isDefaultPackage()) {
			buf.append("package ").append(pack.getElementName()).append(';'); //$NON-NLS-1$
		}
		buf.append(lineDelimiter).append(lineDelimiter);
		if (typeComment != null) {
			buf.append(typeComment).append(lineDelimiter);
		}
		buf.append(typeContent);
		return buf.toString();
	}
	

	/**
	 * Returns the created type. The method only returns a valid type 
	 * after <code>createType</code> has been called.
	 * 
	 * @return the created type
	 * @see #createType(IProgressMonitor)
	 */			
	public IType getCreatedType() {
		return fCreatedType;
	}
	
	// ---- construct CU body----------------
		
	private void writeSuperClass(StringBuffer buf, ImportsManager imports) {
		String superclass= getSuperClass();
		if (fTypeKind == CLASS_TYPE && superclass.length() > 0 && !"java.lang.Object".equals(superclass)) { //$NON-NLS-1$
			buf.append(" extends "); //$NON-NLS-1$
			
			ITypeBinding binding= TypeContextChecker.resolveSuperClass(superclass, fCurrType, getSuperClassStubTypeContext());
			if (binding != null) {
				buf.append(imports.addImport(binding));
			} else {
				buf.append(imports.addImport(superclass));
			}
		}
	}
	
	private void writeSuperInterfaces(StringBuffer buf, ImportsManager imports) {
		List interfaces= getSuperInterfaces();
		int last= interfaces.size() - 1;
		if (last >= 0) {
		    if (fTypeKind != INTERFACE_TYPE) {
				buf.append(" implements "); //$NON-NLS-1$
			} else {
				buf.append(" extends "); //$NON-NLS-1$
			}
			String[] intfs= (String[]) interfaces.toArray(new String[interfaces.size()]);
			ITypeBinding[] bindings= TypeContextChecker.resolveSuperInterfaces(intfs, fCurrType, getSuperInterfaceStubTypeContext());
			for (int i= 0; i <= last; i++) {
				ITypeBinding binding= bindings[i];
				if (binding != null) {
					buf.append(imports.addImport(binding));
				} else {
					buf.append(imports.addImport(intfs[i]));
				}
				if (i < last) {
					buf.append(',');
				}
			}
		}
	}

	/*
	 * Called from createType to construct the source for this type
	 */		
	private String constructTypeStub(ImportsManager imports, String lineDelimiter) {	
		StringBuffer buf= new StringBuffer();
			
		int modifiers= getModifiers();
		buf.append(Flags.toString(modifiers));
		if (modifiers != 0) {
			buf.append(' ');
		}
		String type=""; //$NON-NLS-1$
		switch (fTypeKind) {
			case CLASS_TYPE: type= "class "; break; //$NON-NLS-1$
			case INTERFACE_TYPE: type= "interface "; break; //$NON-NLS-1$
			case ENUM_TYPE: type= "enum "; break; //$NON-NLS-1$
			case ANNOTATION_TYPE: type= "@interface "; break; //$NON-NLS-1$
		}
		buf.append(type);
		buf.append(getTypeName());
		writeSuperClass(buf, imports);
		writeSuperInterfaces(buf, imports);	
		buf.append('{');
		buf.append(lineDelimiter);
		buf.append(lineDelimiter);
		buf.append('}');
		buf.append(lineDelimiter);
		return buf.toString();
	}
	
	/**
	 * Hook method that gets called from <code>createType</code> to support adding of 
	 * unanticipated methods, fields, and inner types to the created type.
	 * <p>
	 * Implementers can use any methods defined on <code>IType</code> to manipulate the
	 * new type.
	 * </p>
	 * <p>
	 * The source code of the new type will be formatted using the platform's formatter. Needed 
	 * imports are added by the wizard at the end of the type creation process using the given 
	 * import manager.
	 * </p>
	 * 
	 * @param newType the new type created via <code>createType</code>
	 * @param imports an import manager which can be used to add new imports
	 * @param monitor a progress monitor to report progress. Must not be <code>null</code>
	 * 
	 * @see #createType(IProgressMonitor)
	 */		
	protected void createTypeMembers(IType newType, ImportsManager imports, IProgressMonitor monitor) throws CoreException {
		// call for compatibility
		createTypeMembers(newType, imports.fImportsStructure, monitor);
		
		// default implementation does nothing
		// example would be
		// String mainMathod= "public void foo(Vector vec) {}"
		// createdType.createMethod(main, null, false, null);
		// imports.addImport("java.lang.Vector");
	}
	
	/**
	 * @deprecated Overwrite createTypeMembers(IType, IImportsManager, IProgressMonitor) instead
	 */		
	protected void createTypeMembers(IType newType, IImportsStructure imports, IProgressMonitor monitor) throws CoreException {
		//deprecated
		if (false) {
			throw new CoreException(JavaUIStatus.createError(IStatus.ERROR, null));
		}
	}
	
		
	/**
	 * @deprecated Instead of file templates, the new type code template
	 * specifies the stub for a compilation unit.
	 */		
	protected String getFileComment(ICompilationUnit parentCU) {
		return null;
	}
	
	/**
	 * Hook method that gets called from <code>createType</code> to retrieve 
	 * a file comment. This default implementation returns the content of the 
	 * 'file comment' template or <code>null</code> if no comment should ne created.
	 * 
	 * @param parentCU the parent compilation unit
	 * @param lineDelimiter the line delimiter to use
	 * @return the file comment or <code>null</code> if a file comment 
	 * is not desired
	 * @throws CoreException 
     *
     * @since 3.1
	 */		
	protected String getFileComment(ICompilationUnit parentCU, String lineDelimiter) throws CoreException {
		if (isAddComments()) {
			return CodeGeneration.getFileComment(parentCU, lineDelimiter);
		}
		return null;
		
	}
	
	private boolean isValidComment(String template) {
		IScanner scanner= ToolFactory.createScanner(true, false, false, false);
		scanner.setSource(template.toCharArray());
		try {
			int next= scanner.getNextToken();
			while (TokenScanner.isComment(next)) {
				next= scanner.getNextToken();
			}
			return next == ITerminalSymbols.TokenNameEOF;
		} catch (InvalidInputException e) {
		}
		return false;
	}
	
	/**
	 * Hook method that gets called from <code>createType</code> to retrieve 
	 * a type comment. This default implementation returns the content of the 
	 * 'type comment' template.
	 * 
	 * @param parentCU the parent compilation unit
	 * @param lineDelimiter the line delimiter to use
	 * @return the type comment or <code>null</code> if a type comment 
	 * is not desired
     *
     * @since 3.0
	 */		
	protected String getTypeComment(ICompilationUnit parentCU, String lineDelimiter) {
		if (isAddComments()) {
			try {
				StringBuffer typeName= new StringBuffer();
				if (isEnclosingTypeSelected()) {
					typeName.append(JavaModelUtil.getTypeQualifiedName(getEnclosingType())).append('.');
				}
				typeName.append(getTypeNameWithoutParameters());
				String[] typeParamNames= new String[0];
				String comment= CodeGeneration.getTypeComment(parentCU, typeName.toString(), typeParamNames, lineDelimiter);
				if (comment != null && isValidComment(comment)) {
					return comment;
				}
			} catch (CoreException e) {
				JavaPlugin.log(e);
			}
		}
		return null;
	}

	/**
	 * @deprecated Use getTypeComment(ICompilationUnit, String)
	 */
	protected String getTypeComment(ICompilationUnit parentCU) {
		if (StubUtility.doAddComments(parentCU.getJavaProject()))
			return getTypeComment(parentCU, StubUtility.getLineDelimiterUsed(parentCU));
		return null;
	}

	/**
	 * @deprecated Use getTemplate(String,ICompilationUnit,int)
	 */
	protected String getTemplate(String name, ICompilationUnit parentCU) {
		return getTemplate(name, parentCU, 0);
	}
		
	
	/**
	 * Returns the string resulting from evaluation the given template in
	 * the context of the given compilation unit. This accesses the normal
	 * template page, not the code templates. To use code templates use
	 * <code>constructCUContent</code> to construct a compilation unit stub or
	 * getTypeComment for the comment of the type.
	 * 
	 * @param name the template to be evaluated
	 * @param parentCU the templates evaluation context
	 * @param pos a source offset into the parent compilation unit. The
	 * template is evaluated at the given source offset
	 */
	protected String getTemplate(String name, ICompilationUnit parentCU, int pos) {
		try {
			Template template= JavaPlugin.getDefault().getTemplateStore().findTemplate(name);
			if (template != null) {
				return JavaContext.evaluateTemplate(template, parentCU, pos);
			}
		} catch (CoreException e) {
			JavaPlugin.log(e);
		} catch (BadLocationException e) {
			JavaPlugin.log(e);
		} catch (TemplateException e) {
			JavaPlugin.log(e);
		}
		return null;
	}	
	

	/**
	 * Creates the bodies of all unimplemented methods and constructors and adds them to the type.
	 * Method is typically called by implementers of <code>NewTypeWizardPage</code> to add
	 * needed method and constructors.
	 * 
	 * @param type the type for which the new methods and constructor are to be created
	 * @param doConstructors if <code>true</code> unimplemented constructors are created
	 * @param doUnimplementedMethods if <code>true</code> unimplemented methods are created
	 * @param imports an import manager to add all needed import statements
	 * @param monitor a progress monitor to report progress
	 * @return the created methods.
	 * @throws CoreException thrown when the creation fails.
	 */
	protected IMethod[] createInheritedMethods(IType type, boolean doConstructors, boolean doUnimplementedMethods, ImportsManager imports, IProgressMonitor monitor) throws CoreException {
		final ICompilationUnit cu= type.getCompilationUnit();
		JavaModelUtil.reconcile(cu);
		IMethod[] typeMethods= type.getMethods();
		Set handleIds= new HashSet(typeMethods.length);
		for (int index= 0; index < typeMethods.length; index++)
			handleIds.add(typeMethods[index].getHandleIdentifier());
		ArrayList newMethods= new ArrayList();
		CodeGenerationSettings settings= JavaPreferencesSettings.getCodeGenerationSettings(type.getJavaProject());
		settings.createComments= isAddComments();
		ITypeBinding binding= null;
		ASTParser parser= ASTParser.newParser(AST.JLS3);
		parser.setResolveBindings(true);
		parser.setSource(cu);
		CompilationUnit unit= (CompilationUnit) parser.createAST(new SubProgressMonitor(monitor, 1));
		if (type.isAnonymous()) {
			ClassInstanceCreation creation= (ClassInstanceCreation) ASTNodes.getParent(NodeFinder.perform(unit, type.getNameRange()), ClassInstanceCreation.class);
			if (creation != null)
				binding= creation.resolveTypeBinding();
		} else {
			AbstractTypeDeclaration declaration= (AbstractTypeDeclaration) ASTNodes.getParent(NodeFinder.perform(unit, type.getNameRange()), AbstractTypeDeclaration.class);
			if (declaration != null)
				binding= declaration.resolveBinding();
		}
		if (binding != null) {
			if (doUnimplementedMethods) {
				AddUnimplementedMethodsOperation operation= new AddUnimplementedMethodsOperation(type, null, unit, createBindingKeys(StubUtility2.getUnimplementedMethods(binding)), settings, false, true, true);
				operation.run(monitor);
				createImports(imports, operation.getCreatedImports());
			}
			if (doConstructors) {
				AddUnimplementedConstructorsOperation operation= new AddUnimplementedConstructorsOperation(type, null, unit, createBindingKeys(StubUtility2.getVisibleConstructors(binding, false)), settings, false, true, true);
				operation.run(monitor);
				createImports(imports, operation.getCreatedImports());
			}
		}
		JavaModelUtil.reconcile(cu);
		typeMethods= type.getMethods();
		for (int index= 0; index < typeMethods.length; index++)
			if (!handleIds.contains(typeMethods[index].getHandleIdentifier()))
				newMethods.add(typeMethods[index]);
		IMethod[] methods= new IMethod[newMethods.size()];
		newMethods.toArray(methods);
		return methods;
	}

	private void createImports(ImportsManager imports, String[] createdImports) {
		for (int index= 0; index < createdImports.length; index++)
			imports.addImport(createdImports[index]);
	}

	private String[] createBindingKeys(IBinding[] bindings) {
		String[] keys= new String[bindings.length];
		for (int index= 0; index < bindings.length; index++)
			keys[index]= bindings[index].getKey();
		return keys;
	}

	/**
	 * @deprecated Use createInheritedMethods(IType,boolean,boolean,IImportsManager,IProgressMonitor)
	 */
	protected IMethod[] createInheritedMethods(IType type, boolean doConstructors, boolean doUnimplementedMethods, IImportsStructure imports, IProgressMonitor monitor) throws CoreException {
		return createInheritedMethods(type, doConstructors, doUnimplementedMethods, new ImportsManager(imports), monitor);
	}
	
	// ---- creation ----------------

	/**
	 * Returns the runnable that creates the type using the current settings.
	 * The returned runnable must be executed in the UI thread.
	 * 
	 * @return the runnable to create the new type
	 */		
	public IRunnableWithProgress getRunnable() {				
		return new IRunnableWithProgress() {
			public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
				try {
					if (monitor == null) {
						monitor= new NullProgressMonitor();
					}
					createType(monitor);
				} catch (CoreException e) {
					throw new InvocationTargetException(e);
				} 				
			}
		};
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.DialogPage#dispose()
	 */
	public void dispose() {
		JavaPlugin.getDefault().getDialogSettings().put(DIALOGSETTINGS_ADDCOMMENTS, fAddCommentButton.isSelected());
		super.dispose();
	}
}
