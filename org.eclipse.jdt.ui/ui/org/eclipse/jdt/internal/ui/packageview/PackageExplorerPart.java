/*******************************************************************************
 * Copyright (c) 2000, 2002 International Business Machines Corp. and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v0.5 
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v05.html
 * 
 * Contributors:
 *     IBM Corporation - initial implementation
 ******************************************************************************/
package org.eclipse.jdt.internal.ui.packageview;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeViewerListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeExpansionEvent;
import org.eclipse.jface.viewers.TreeViewer;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;

import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionGroup;
import org.eclipse.ui.actions.AddBookmarkAction;
import org.eclipse.ui.actions.NewWizardMenu;
import org.eclipse.ui.actions.OpenInNewWindowAction;
import org.eclipse.ui.actions.OpenWithMenu;
import org.eclipse.ui.actions.RefreshAction;
import org.eclipse.ui.dialogs.PropertyDialogAction;
import org.eclipse.ui.part.ISetSelectionTarget;
import org.eclipse.ui.part.ResourceTransfer;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.views.framelist.BackAction;
import org.eclipse.ui.views.framelist.ForwardAction;
import org.eclipse.ui.views.framelist.FrameList;
import org.eclipse.ui.views.framelist.GoIntoAction;
import org.eclipse.ui.views.framelist.UpAction;

import org.eclipse.search.ui.IWorkingSet;
import org.eclipse.search.ui.SearchUI;

import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaModel;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IOpenable;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.IWorkingCopy;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;

import org.eclipse.jdt.internal.corext.util.JavaModelUtil;
import org.eclipse.jdt.internal.ui.IJavaHelpContextIds;
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jdt.internal.ui.actions.CompositeActionGroup;
import org.eclipse.jdt.internal.ui.actions.ContextMenuGroup;
import org.eclipse.jdt.internal.ui.actions.GenerateGroup;
import org.eclipse.jdt.internal.ui.dnd.DelegatingDragAdapter;
import org.eclipse.jdt.internal.ui.dnd.DelegatingDropAdapter;
import org.eclipse.jdt.internal.ui.dnd.LocalSelectionTransfer;
import org.eclipse.jdt.internal.ui.dnd.ResourceTransferDragAdapter;
import org.eclipse.jdt.internal.ui.dnd.TransferDragSourceListener;
import org.eclipse.jdt.internal.ui.dnd.TransferDropTargetListener;
import org.eclipse.jdt.internal.ui.javaeditor.EditorUtility;
import org.eclipse.jdt.internal.ui.javaeditor.IClassFileEditorInput;
import org.eclipse.jdt.internal.ui.javaeditor.JarEntryEditorInput;
import org.eclipse.jdt.internal.ui.preferences.JavaBasePreferencePage;
import org.eclipse.jdt.internal.ui.refactoring.actions.IRefactoringAction;
import org.eclipse.jdt.internal.ui.refactoring.actions.RefactoringGroup;
import org.eclipse.jdt.internal.ui.reorg.ReorgGroup;
import org.eclipse.jdt.internal.ui.search.JavaSearchGroup;
import org.eclipse.jdt.internal.ui.util.JavaUIHelp;
import org.eclipse.jdt.internal.ui.viewsupport.JavaElementImageProvider;
import org.eclipse.jdt.internal.ui.viewsupport.JavaElementLabels;
import org.eclipse.jdt.internal.ui.viewsupport.MemberFilterActionGroup;
import org.eclipse.jdt.internal.ui.viewsupport.ProblemTreeViewer;
import org.eclipse.jdt.internal.ui.viewsupport.StandardJavaUILabelProvider;
import org.eclipse.jdt.internal.ui.viewsupport.StatusBarUpdater;

import org.eclipse.jdt.ui.IContextMenuConstants;
import org.eclipse.jdt.ui.IPackagesViewPart;
import org.eclipse.jdt.ui.JavaElementContentProvider;
import org.eclipse.jdt.ui.JavaElementSorter;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jdt.ui.actions.GenerateActionGroup;
import org.eclipse.jdt.ui.actions.OpenActionGroup;
import org.eclipse.jdt.ui.actions.ShowActionGroup;


/**
 * The ViewPart for the ProjectExplorer. It listens to part activation events.
 * When selection linking with the editor is enabled the view selection tracks
 * the active editor page. Similarly when a resource is selected in the packages
 * view the corresponding editor is activated. 
 */

public class PackageExplorerPart extends ViewPart implements ISetSelectionTarget, IMenuListener, IPackagesViewPart,  IPropertyChangeListener {
	
	public final static String VIEW_ID= JavaUI.ID_PACKAGES;
				
	// Persistance tags.
	static final String TAG_SELECTION= "selection"; //$NON-NLS-1$
	static final String TAG_EXPANDED= "expanded"; //$NON-NLS-1$
	static final String TAG_ELEMENT= "element"; //$NON-NLS-1$
	static final String TAG_PATH= "path"; //$NON-NLS-1$
	static final String TAG_VERTICAL_POSITION= "verticalPosition"; //$NON-NLS-1$
	static final String TAG_HORIZONTAL_POSITION= "horizontalPosition"; //$NON-NLS-1$
	static final String TAG_FILTERS = "filters"; //$NON-NLS-1$
	static final String TAG_FILTER = "filter"; //$NON-NLS-1$
	static final String TAG_SHOWLIBRARIES = "showLibraries"; //$NON-NLS-1$
	static final String TAG_SHOWBINARIES = "showBinaries"; //$NON-NLS-1$
	static final String TAG_WORKINGSET = "workingset"; //$NON-NLS-1$

	private JavaElementPatternFilter fPatternFilter= new JavaElementPatternFilter();
	private LibraryFilter fLibraryFilter= new LibraryFilter();
	private BinaryProjectFilter fBinaryFilter= new BinaryProjectFilter();
	private WorkingSetFilter fWorkingSetFilter= new WorkingSetFilter();
	
	private MemberFilterActionGroup fMemberFilterActionGroup;

	private ProblemTreeViewer fViewer; 
	private StandardJavaUILabelProvider fJavaElementLabelProvider;
	private PackagesFrameSource fFrameSource;
	private FrameList fFrameList;
	private BuildGroup fBuildGroup;
	private ContextMenuGroup[] fStandardGroups;
	private Menu fContextMenu;		
	private OpenResourceAction fOpenCUAction;
	private Action fOpenToAction;
	private OpenInNewWindowAction fOpenNewWindowAction;
	private Action fShowTypeHierarchyAction;
	private Action fShowNavigatorAction;
	private PropertyDialogAction fPropertyDialogAction;
 	private IRefactoringAction fDeleteAction;
 	private RefreshAction fRefreshAction;
 	private BackAction fBackAction;
	private ForwardAction fForwardAction;
	private GoIntoAction fZoomInAction;
	private UpAction fUpAction;
	private GotoTypeAction fGotoTypeAction;
	private GotoPackageAction fGotoPackageAction;
	private AddBookmarkAction fAddBookmarkAction;
	
 	private FilterSelectionAction fFilterAction;
 	private ShowLibrariesAction fShowLibrariesAction;
	private ShowBinariesAction fShowBinariesAction;
	private FilterWorkingSetAction fFilterWorkingSetAction;
	private RemoveWorkingSetFilterAction fRemoveWorkingSetAction;
	private IMemento fMemento;
	
	private ISelectionChangedListener fSelectionListener;
	
	private CompositeActionGroup fStandardActionGroups;
	
	private IPartListener fPartListener= new IPartListener() {
		public void partActivated(IWorkbenchPart part) {
			if (part instanceof IEditorPart)
				editorActivated((IEditorPart) part);
		}
		public void partBroughtToTop(IWorkbenchPart part) {
		}
		public void partClosed(IWorkbenchPart part) {
		}
		public void partDeactivated(IWorkbenchPart part) {
		}
		public void partOpened(IWorkbenchPart part) {
		}
	};
	
	private ITreeViewerListener fExpansionListener= new ITreeViewerListener() {
		public void treeCollapsed(TreeExpansionEvent event) {
		}
		
		public void treeExpanded(TreeExpansionEvent event) {
			Object element= event.getElement();
			if (element instanceof ICompilationUnit || 
				element instanceof IClassFile)
				expandMainType(element);
		}
	};

	
	public PackageExplorerPart() { 
	}

	/* (non-Javadoc)
	 * Method declared on IViewPart.
	 */
	public void init(IViewSite site, IMemento memento) throws PartInitException {
		super.init(site, memento);
		fMemento= memento;
	}
	
	/** 
	 * Initializes the default preferences
	 */
	public static void initDefaults(IPreferenceStore store) {
		store.setDefault(TAG_SHOWLIBRARIES, true);
		store.setDefault(TAG_SHOWBINARIES, true);
	}

	/**
	 * Returns the package explorer part of the active perspective. If 
	 * there isn't any package explorer part <code>null</code> is returned.
	 */
	public static PackageExplorerPart getFromActivePerspective() {
		IViewPart view= JavaPlugin.getActivePage().findView(VIEW_ID);
		if (view instanceof PackageExplorerPart)
			return (PackageExplorerPart)view;
		return null;	
	}
	
	/**
	 * Makes the package explorer part visible in the active perspective. If there
	 * isn't a package explorer part registered <code>null</code> is returned.
	 * Otherwise the opened view part is returned.
	 */
	public static PackageExplorerPart openInActivePerspective() {
		try {
			return (PackageExplorerPart)JavaPlugin.getActivePage().showView(VIEW_ID);
		} catch(PartInitException pe) {
			return null;
		}
	} 
		
	 public void dispose() {
	 	if (fViewer != null)
			JavaPlugin.getDefault().getProblemMarkerManager().removeListener(fViewer);
		if (fContextMenu != null && !fContextMenu.isDisposed())
			fContextMenu.dispose();
		getSite().getPage().removePartListener(fPartListener);
		JavaPlugin.getDefault().getPreferenceStore().removePropertyChangeListener(this);
		if (fViewer != null)
			fViewer.removeTreeListener(fExpansionListener);
		super.dispose();	
	}
	/**
	 * Implementation of IWorkbenchPart.createPartControl(Composite)
	 */
	public void createPartControl(Composite parent) {
		fViewer= new ProblemTreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		
		boolean showCUChildren= JavaBasePreferencePage.showCompilationUnitChildren();
		fViewer.setContentProvider(new JavaElementContentProvider(showCUChildren, false));
		
		JavaPlugin.getDefault().getProblemMarkerManager().addListener(fViewer);		
		JavaPlugin.getDefault().getPreferenceStore().addPropertyChangeListener(this);
		
		fJavaElementLabelProvider= 
			new StandardJavaUILabelProvider(
				StandardJavaUILabelProvider.DEFAULT_TEXTFLAGS | JavaElementLabels.P_COMPRESSED,
				StandardJavaUILabelProvider.DEFAULT_IMAGEFLAGS | JavaElementImageProvider.SMALL_ICONS,
				StandardJavaUILabelProvider.getAdornmentProviders(true, null)
			);
		
		fViewer.setLabelProvider(new DecoratingLabelProvider(
			fJavaElementLabelProvider, PlatformUI.getWorkbench().getDecoratorManager())
		);
		fViewer.setSorter(new JavaElementSorter());
		fViewer.addFilter(new EmptyInnerPackageFilter());
		fViewer.setUseHashlookup(true);
		fViewer.addFilter(fPatternFilter);
		fViewer.addFilter(fLibraryFilter);
		
		fMemberFilterActionGroup= new MemberFilterActionGroup(fViewer, "PackageView"); // adds filter
	
		fViewer.addFilter(fWorkingSetFilter);
		if(fMemento != null) 
			restoreFilters();
		else
			initFilterFromPreferences();
			
		// Set input after filter and sorter has been set. This avoids resorting
		// and refiltering.
		fViewer.setInput(findInputElement());
		initDragAndDrop();
		initFrameList();
		initKeyListener();
		updateTitle();
		
		MenuManager menuMgr= new MenuManager("#PopupMenu"); //$NON-NLS-1$
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(this);
		fContextMenu= menuMgr.createContextMenu(fViewer.getTree());
		fViewer.getTree().setMenu(fContextMenu);
		
		// Register viewer with site. This must be done before making the actions.
		IWorkbenchPartSite site= getSite();
		site.registerContextMenu(menuMgr, fViewer);
		site.setSelectionProvider(fViewer);
		site.getPage().addPartListener(fPartListener);
		
		makeActions(); // call before registering for selection changes
			
		fSelectionListener= new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				handleSelectionChanged(event);
			}
		};
		fViewer.addSelectionChangedListener(fSelectionListener);
		
		fViewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				handleDoubleClick(event);
			}
		});

		IStatusLineManager slManager= getViewSite().getActionBars().getStatusLineManager();
		fViewer.addSelectionChangedListener(new StatusBarUpdater(slManager));
		fViewer.addTreeListener(fExpansionListener);
	
		if (fMemento != null)
			restoreState(fMemento);
		fMemento= null;

		// Set help for the view 
		JavaUIHelp.setHelp(fViewer, IJavaHelpContextIds.PACKAGES_VIEW);
		
		fillActionBars();
	}

	private void fillActionBars() {
		IActionBars actionBars= getViewSite().getActionBars();
		IToolBarManager toolBar= actionBars.getToolBarManager();
		fillToolBar(toolBar);

		actionBars.updateActionBars();
	
		IMenuManager menu= actionBars.getMenuManager();
		menu.add(fFilterAction);
		
		menu.add(fShowLibrariesAction);  
		//menu.add(fShowBinariesAction);
		menu.add(fFilterWorkingSetAction); 
		menu.add(fRemoveWorkingSetAction); 

		menu.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
		menu.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS+"-end"));//$NON-NLS-1$
		
		fStandardActionGroups.fillActionBars(actionBars);
		actionBars.setGlobalActionHandler(IWorkbenchActionConstants.DELETE, fDeleteAction);
		actionBars.setGlobalActionHandler(IWorkbenchActionConstants.REFRESH, fRefreshAction);
		actionBars.setGlobalActionHandler(IWorkbenchActionConstants.BOOKMARK, fAddBookmarkAction);
		actionBars.setGlobalActionHandler(IWorkbenchActionConstants.PROPERTIES, fPropertyDialogAction);
		
		// Navigate Go Into and Go To actions.
		actionBars.setGlobalActionHandler(IWorkbenchActionConstants.GO_INTO, fZoomInAction);
		actionBars.setGlobalActionHandler(IWorkbenchActionConstants.BACK, fBackAction);
		actionBars.setGlobalActionHandler(IWorkbenchActionConstants.FORWARD, fForwardAction);
		actionBars.setGlobalActionHandler(IWorkbenchActionConstants.UP, fUpAction);
		
		// Retarget Build and Rebuild action
		fBuildGroup.fillActionBars(actionBars);
		
		ReorgGroup.addGlobalReorgActions(actionBars, getSelectionProvider());
	}
	
	private void fillToolBar(IToolBarManager toolBar) {
		toolBar.removeAll();
		
		toolBar.add(fBackAction);
		toolBar.add(fForwardAction);
		toolBar.add(fUpAction);
		
		if (JavaBasePreferencePage.showCompilationUnitChildren()) {
			toolBar.add(new Separator());
			fMemberFilterActionGroup.contributeToToolBar(toolBar);
		}
	}
	
		
	private Object findInputElement() {
		Object input= getSite().getPage().getInput();
		if (input instanceof IWorkspace) { 
			return JavaCore.create(((IWorkspace)input).getRoot());
		} else if (input instanceof IContainer) {
			return JavaCore.create((IContainer)input);
		}
		//1GERPRT: ITPJUI:ALL - Packages View is empty when shown in Type Hierarchy Perspective
		// we can't handle the input
		// fall back to show the workspace
		return JavaCore.create(JavaPlugin.getWorkspace().getRoot());	
	}
	
	/**
	 * Answer the property defined by key.
	 */
	public Object getAdapter(Class key) {
		if (key.equals(ISelectionProvider.class))
			return fViewer;
		return super.getAdapter(key);
	}

	/**
	 * Returns the tool tip text for the given element.
	 */
	String getToolTipText(Object element) {
		String result;
		if (!(element instanceof IResource)) {
			result= JavaElementLabels.getTextLabel(element, StandardJavaUILabelProvider.DEFAULT_TEXTFLAGS);		
		} else {
			IPath path= ((IResource) element).getFullPath();
			if (path.isRoot()) {
				result= PackagesMessages.getString("PackageExplorer.title"); //$NON-NLS-1$
			} else {
				result= path.makeRelative().toString();
			}
		}
		IWorkingSet ws= fWorkingSetFilter.getWorkingSet();
		if (ws == null)
			return result;
		String wsstr= "Working Set: "+ws.getName();
		if (result.length() == 0)
			return wsstr;
		return result + " - " + wsstr;
	}
	
	public String getTitleToolTip() {
		if (fViewer == null)
			return super.getTitleToolTip();
		return getToolTipText(fViewer.getInput());
	}
	
	/**
	 * @see IWorkbenchPart#setFocus()
	 */
	public void setFocus() {
		fViewer.getTree().setFocus();
	}

	/**
	 * Sets the working set to be used for filtering this part
	 */
	public void setWorkingSet(IWorkingSet ws) {
		fWorkingSetFilter.setWorkingSet(ws);
		firePropertyChange(IWorkbenchPart.PROP_TITLE);
		fFilterWorkingSetAction.setChecked(ws != null);
		fRemoveWorkingSetAction.setEnabled(ws != null);
	} 
	
	/**
	 * Returns the shell to use for opening dialogs.
	 * Used in this class, and in the actions.
	 */
	private Shell getShell() {
		return fViewer.getTree().getShell();
	}

	/**
	 * Returns the selection provider.
	 */
	private ISelectionProvider getSelectionProvider() {
		return fViewer;
	}
	
	/**
	 * Returns the current selection.
	 */
	private ISelection getSelection() {
		return fViewer.getSelection();
	}
	  
	//---- Action handling ----------------------------------------------------------
	
	/**
	 * Called when the context menu is about to open. Override
	 * to add your own context dependent menu contributions.
	 */
	public void menuAboutToShow(IMenuManager menu) {
		JavaPlugin.createStandardGroups(menu);
		IStructuredSelection selection= (IStructuredSelection) fViewer.getSelection();
		int size= selection.size();
		Object element= selection.getFirstElement();
		
		fPropertyDialogAction.selectionChanged(selection);

		MenuManager newMenu= new MenuManager(PackagesMessages.getString("PackageExplorer.new")); //$NON-NLS-1$
		menu.appendToGroup(IContextMenuConstants.GROUP_NEW, newMenu);
		new NewWizardMenu(newMenu, getSite().getWorkbenchWindow(), false);

		// updateActions(selection);
		if (size == 1 && fViewer.isExpandable(element)) 
			menu.appendToGroup(IContextMenuConstants.GROUP_GOTO, fZoomInAction);
		addGotoMenu(menu);

		fOpenCUAction.update();
		if (fOpenCUAction.isEnabled())
			menu.appendToGroup(IContextMenuConstants.GROUP_OPEN, fOpenCUAction);
			
		addOpenWithMenu(menu, selection);
		if (size == 1)
			addOpenNewWindowAction(menu, element);
		
		addRefactoring(menu);

		ContextMenuGroup.add(menu, fStandardGroups, fViewer);
		
		// XXX fill the show menu. This is a workaround until we have converted all context menus to the
		// new action groups.
		fStandardActionGroups.get(1).fillContextMenu(menu);
		
		if (onlyFilesSelected(selection)) {
			menu.appendToGroup(IContextMenuConstants.GROUP_REORGANIZE, fAddBookmarkAction);
		}
		
		fRefreshAction.selectionChanged(selection);
		if (fRefreshAction.isEnabled())
			menu.appendToGroup(IContextMenuConstants.GROUP_BUILD, fRefreshAction);

		menu.add(new Separator());
		if (fPropertyDialogAction.isApplicableForSelection())
			menu.appendToGroup(IContextMenuConstants.GROUP_PROPERTIES, fPropertyDialogAction);	
	}

	 void addGotoMenu(IMenuManager menu) {
		MenuManager gotoMenu= new MenuManager(PackagesMessages.getString("PackageExplorer.gotoTitle")); //$NON-NLS-1$
		menu.appendToGroup(IContextMenuConstants.GROUP_GOTO, gotoMenu);
		gotoMenu.add(fBackAction);
		gotoMenu.add(fForwardAction);
		gotoMenu.add(fUpAction);
		gotoMenu.add(fGotoTypeAction);
		gotoMenu.add(fGotoPackageAction);
	}

	private void addOpenNewWindowAction(IMenuManager menu, Object element) {
		if (element instanceof IJavaElement) {
			try {
				element= ((IJavaElement)element).getCorrespondingResource();
			} catch(JavaModelException e) {
			}
		}
		if (!(element instanceof IContainer))
			return;
		menu.appendToGroup(
			IContextMenuConstants.GROUP_OPEN, 
			new OpenInNewWindowAction(getSite().getWorkbenchWindow(), (IContainer)element));
	}

	private boolean onlyFilesSelected(IStructuredSelection selection) {
		if (selection.isEmpty())
			return false;
		for (Iterator enum= selection.iterator(); enum.hasNext();) {
			Object o= enum.next();
			if (o instanceof IFile)
				continue;
			if (o instanceof IAdaptable) {
				Object resource= ((IAdaptable)o).getAdapter(IResource.class);
				if (!(resource instanceof IFile))
					return false;
			} else {
				return false;
			}
		}
		return true;
	}

	private void makeActions() {
		ISelectionProvider provider= getSelectionProvider();
		fOpenCUAction= new OpenResourceAction(provider);
		fPropertyDialogAction= new PropertyDialogAction(getShell(), provider);
		// fShowTypeHierarchyAction= new ShowTypeHierarchyAction(provider);
		fShowNavigatorAction= new ShowInNavigatorAction(provider);
		fAddBookmarkAction= new AddBookmarkAction(getShell());
		provider.addSelectionChangedListener(fAddBookmarkAction);

		fBuildGroup= new BuildGroup(this, true);
		fStandardGroups= new ContextMenuGroup[] {
			fBuildGroup,
			new ReorgGroup(),
			new GenerateGroup(),
			new JavaSearchGroup()
		};
		
		fStandardActionGroups= new CompositeActionGroup(new ActionGroup[] {
				new OpenActionGroup(this), new ShowActionGroup(this), new GenerateActionGroup(this)});
		
		fDeleteAction= ReorgGroup.createDeleteAction(provider);
		fRefreshAction= new RefreshAction(getShell());
		provider.addSelectionChangedListener(fRefreshAction);
		fFilterAction = new FilterSelectionAction(getShell(), this, PackagesMessages.getString("PackageExplorer.filters")); //$NON-NLS-1$
		fShowLibrariesAction = new ShowLibrariesAction(this, PackagesMessages.getString("PackageExplorer.referencedLibs")); //$NON-NLS-1$
		fShowBinariesAction = new ShowBinariesAction(getShell(), this, PackagesMessages.getString("PackageExplorer.binaryProjects")); //$NON-NLS-1$
		fFilterWorkingSetAction = new FilterWorkingSetAction(getShell(), this, "Filter Working Set..."); //$NON-NLS-1$
		fRemoveWorkingSetAction = new RemoveWorkingSetFilterAction(getShell(), this, "Remove Working Set Filter"); //$NON-NLS-1$
		
		fBackAction= new BackAction(fFrameList);
		fForwardAction= new ForwardAction(fFrameList);
		fZoomInAction= new GoIntoAction(fFrameList);
		fUpAction= new UpAction(fFrameList);

		fGotoTypeAction= new GotoTypeAction(this);
		fGotoPackageAction= new GotoPackageAction(this);
	}
	
	private void addRefactoring(IMenuManager menu){
		MenuManager refactoring= new MenuManager(PackagesMessages.getString("PackageExplorer.refactoringTitle"));  //$NON-NLS-1$
		ContextMenuGroup.add(refactoring, new ContextMenuGroup[] { new RefactoringGroup() }, fViewer);
		if (!refactoring.isEmpty())
			menu.appendToGroup(IContextMenuConstants.GROUP_REORGANIZE, refactoring);
	}
	
	private void addOpenWithMenu(IMenuManager menu, IStructuredSelection selection) {
		// If one file is selected get it.
		// Otherwise, do not show the "open with" menu.
		if (selection.size() != 1)
			return;

		IAdaptable element= (IAdaptable)selection.getFirstElement();
		Object resource= element.getAdapter(IResource.class);
		if (!(resource instanceof IFile))
			return; 

		// Create a menu flyout.
		MenuManager submenu= new MenuManager(PackagesMessages.getString("PackageExplorer.openWith")); //$NON-NLS-1$
		submenu.add(new OpenWithMenu(getSite().getPage(), (IFile) resource));

		// Add the submenu.
		menu.appendToGroup(IContextMenuConstants.GROUP_OPEN, submenu);

	}
	
	private boolean isSelectionOfType(ISelection s, Class clazz, boolean considerUnderlyingResource) {
		if (! (s instanceof IStructuredSelection) || s.isEmpty())
			return false;
		
		IStructuredSelection selection= (IStructuredSelection)s;
		Iterator iter= selection.iterator();
		while (iter.hasNext()) {
			Object o= iter.next();
			if (clazz.isInstance(o))
				return true;
			if (considerUnderlyingResource) {
				if (! (o instanceof IJavaElement))
					return false;
				IJavaElement element= (IJavaElement)o;
				Object resource= element.getAdapter(IResource.class);
				if (! clazz.isInstance(resource))
					return false;
			}
		}
		return true;	
	}
	
	//---- Event handling ----------------------------------------------------------
	
	private void initDragAndDrop() {
		int ops= DND.DROP_COPY | DND.DROP_MOVE;
		final LocalSelectionTransfer lt= LocalSelectionTransfer.getInstance();
		Transfer[] transfers= new Transfer[] {
			lt, 
			ResourceTransfer.getInstance(),
			FileTransfer.getInstance()};
		
		// Drop Adapter
		TransferDropTargetListener[] dropListeners= new TransferDropTargetListener[] {
			new SelectionTransferDropAdapter(fViewer),
			new FileTransferDropAdapter(fViewer)
		};
		fViewer.addDropSupport(ops, transfers, new DelegatingDropAdapter(dropListeners));
		
		// Drag Adapter
		Control control= fViewer.getControl();
		TransferDragSourceListener[] dragListeners= new TransferDragSourceListener[] {
			new SelectionTransferDragAdapter(fViewer),
			new ResourceTransferDragAdapter(fViewer),
			new FileTransferDragAdapter(fViewer)
		};
		DragSource source= new DragSource(control, ops);
		// Note, that the transfer agents are set by the delegating drag adapter itself.
		source.addDragListener(new DelegatingDragAdapter(dragListeners));
	}

	/**
	 * Handles double clicks in viewer.
	 * Opens editor if file double-clicked.
	 */
	private void handleDoubleClick(DoubleClickEvent event) {
		IStructuredSelection s= (IStructuredSelection) event.getSelection();
		Object element= s.getFirstElement();
		
		if (fOpenCUAction.isEnabled()) {
			fOpenCUAction.run();
			return;
		}
		
		if (fViewer.isExpandable(element)) {
			if (JavaBasePreferencePage.doubleClickGoesInto()) {
				// don't zoom into compilation units and class files
				if (element instanceof IOpenable && 
					!(element instanceof ICompilationUnit) && 
					!(element instanceof IClassFile)) {
					fZoomInAction.run();
				}
			} else {
				fViewer.setExpandedState(element, !fViewer.getExpandedState(element));
				//expandMainType(element);
			}
		}
	}

	/**
	 * Handles selection changed in viewer.
	 * Updates global actions.
	 * Links to editor (if option enabled)
	 */
	private void handleSelectionChanged(SelectionChangedEvent event) {
		IStructuredSelection selection= (IStructuredSelection) event.getSelection();
		//updateGlobalActions(sel);
		fZoomInAction.update();
		linkToEditor(selection);
	}

	public void selectReveal(ISelection selection) {
		ISelection javaSelection= convertSelection(selection);
	 	fViewer.setSelection(javaSelection, true);
	}

	private ISelection convertSelection(ISelection s) {
		List converted= new ArrayList();
		if (s instanceof StructuredSelection) {
			Object[] elements= ((StructuredSelection)s).toArray();
			for (int i= 0; i < elements.length; i++) {
				Object e= elements[i];
				if (e instanceof IJavaElement)	
					converted.add(e);
				else if (e instanceof IResource) {
					IJavaElement element= JavaCore.create((IResource)e);
					if (element != null) 
						converted.add(element);
					else 
						converted.add(e);
				}
			}
		}
		return new StructuredSelection(converted.toArray());
	}
	
	public void selectAndReveal(Object element) {
		selectReveal(new StructuredSelection(element));
	}
	
	/**
	 * Returns whether the preference to link selection to active editor is enabled.
	 */
	boolean isLinkingEnabled() {
		return JavaBasePreferencePage.linkPackageSelectionToEditor();
	}

	/**
	 * Links to editor (if option enabled)
	 */
	private void linkToEditor(IStructuredSelection selection) {	
		//if (!isLinkingEnabled())
		//	return; 
				
		Object obj= selection.getFirstElement();
		Object element= null;

		if (selection.size() == 1) {
			if (obj instanceof IJavaElement) {
				IJavaElement cu= JavaModelUtil.findElementOfKind((IJavaElement)obj, IJavaElement.COMPILATION_UNIT);
				if (cu != null)
					element= getResourceFor(cu);
				if (element == null)
					element= JavaModelUtil.findElementOfKind((IJavaElement)obj, IJavaElement.CLASS_FILE);
			}
			else if (obj instanceof IFile)
				element= obj;
				
			if (element == null)
				return;

			IWorkbenchPage page= getSite().getPage();
			IEditorPart editorArray[]= page.getEditors();
			for (int i= 0; i < editorArray.length; ++i) {
				IEditorPart editor= editorArray[i];
				Object input= getElementOfInput(editor.getEditorInput());					
				if (input != null && input.equals(element)) {
					page.bringToTop(editor);
					if (obj instanceof IJavaElement) 
						EditorUtility.revealInEditor(editor, (IJavaElement) obj);
					return;
				}
			}
		}
	}

	private IResource getResourceFor(Object element) {
		if (element instanceof IJavaElement) {
			if (element instanceof IWorkingCopy) {
				IWorkingCopy wc= (IWorkingCopy)element;
				IJavaElement original= wc.getOriginalElement();
				if (original != null)
					element= original;
			}
			try {
				element= ((IJavaElement)element).getUnderlyingResource();
			} catch (JavaModelException e) {
				return null;
			}
		}
		if (!(element instanceof IResource) || ((IResource)element).isPhantom()) {
			return null;
		}
		return (IResource)element;
	}
	
	public void saveState(IMemento memento) {
		if (fViewer == null) {
			// part has not been created
			if (fMemento != null) //Keep the old state;
				memento.putMemento(fMemento);
			return;
		}
		saveExpansionState(memento);
		saveSelectionState(memento);
		saveScrollState(memento, fViewer.getTree());
		savePatternFilterState(memento);
		saveFilterState(memento);
		saveWorkingSetState(memento);
		saveMemberFilterState(memento);
	}

	protected void saveFilterState(IMemento memento) {
		boolean showLibraries= getLibraryFilter().getShowLibraries();
		String show= "true"; //$NON-NLS-1$
		if (!showLibraries)
			show= "false"; //$NON-NLS-1$
		memento.putString(TAG_SHOWLIBRARIES, show);
		
		//save binary filter
		boolean showBinaries= getBinaryFilter().getShowBinaries();
		String showBinString= "true"; //$NON-NLS-1$
		if (!showBinaries)
			showBinString= "false"; //$NON-NLS-1$
		memento.putString(TAG_SHOWBINARIES, showBinString);
	}

	protected void savePatternFilterState(IMemento memento) {
		String filters[] = getPatternFilter().getPatterns();
		if(filters.length > 0) {
			IMemento filtersMem = memento.createChild(TAG_FILTERS);
			for (int i = 0; i < filters.length; i++){
				IMemento child = filtersMem.createChild(TAG_FILTER);
				child.putString(TAG_ELEMENT,filters[i]);
			}
		}
	}

	protected void saveScrollState(IMemento memento, Tree tree) {
		ScrollBar bar= tree.getVerticalBar();
		int position= bar != null ? bar.getSelection() : 0;
		memento.putString(TAG_VERTICAL_POSITION, String.valueOf(position));
		//save horizontal position
		bar= tree.getHorizontalBar();
		position= bar != null ? bar.getSelection() : 0;
		memento.putString(TAG_HORIZONTAL_POSITION, String.valueOf(position));
	}

	protected void saveSelectionState(IMemento memento) {
		Object elements[]= ((IStructuredSelection) fViewer.getSelection()).toArray();
		if (elements.length > 0) {
			IMemento selectionMem= memento.createChild(TAG_SELECTION);
			for (int i= 0; i < elements.length; i++) {
				IMemento elementMem= selectionMem.createChild(TAG_ELEMENT);
				// we can only persist JavaElements for now
				Object o= elements[i];
				if (o instanceof IJavaElement)
					elementMem.putString(TAG_PATH, ((IJavaElement) elements[i]).getHandleIdentifier());
			}
		}
	}

	protected void saveExpansionState(IMemento memento) {
		Object expandedElements[]= fViewer.getExpandedElements();
		if (expandedElements.length > 0) {
			IMemento expandedMem= memento.createChild(TAG_EXPANDED);
			for (int i= 0; i < expandedElements.length; i++) {
				IMemento elementMem= expandedMem.createChild(TAG_ELEMENT);
				// we can only persist JavaElements for now
				Object o= expandedElements[i];
				if (o instanceof IJavaElement)
					elementMem.putString(TAG_PATH, ((IJavaElement) expandedElements[i]).getHandleIdentifier());
			}
		}
	}

	protected void saveWorkingSetState(IMemento memento) {
		IWorkingSet ws= getWorkingSetFilter().getWorkingSet();
		if (ws != null) {
			memento.putString(TAG_WORKINGSET, ws.getName());
		}
	}
	/**
	 * Saves the state of the filter actions
	 */
	public void saveMemberFilterState(IMemento memento) {
		fMemberFilterActionGroup.saveState(memento);
	}

	void restoreState(IMemento memento) {
		restoreExpansionState(memento);
		restoreSelectionState(memento);
		restoreScrollState(memento, fViewer.getTree());
	}

	protected void restoreScrollState(IMemento memento, Tree tree) {
		ScrollBar bar= tree.getVerticalBar();
		if (bar != null) {
			try {
				String posStr= memento.getString(TAG_VERTICAL_POSITION);
				int position;
				position= new Integer(posStr).intValue();
				bar.setSelection(position);
			} catch (NumberFormatException e) {
				// ignore, don't set scrollposition
			}
		}
		bar= tree.getHorizontalBar();
		if (bar != null) {
			try {
				String posStr= memento.getString(TAG_HORIZONTAL_POSITION);
				int position;
				position= new Integer(posStr).intValue();
				bar.setSelection(position);
			} catch (NumberFormatException e) {
				// ignore don't set scroll position
			}
		}
	}

	protected void restoreSelectionState(IMemento memento) {
		IMemento childMem;
		childMem= memento.getChild(TAG_SELECTION);
		if (childMem != null) {
			ArrayList list= new ArrayList();
			IMemento[] elementMem= childMem.getChildren(TAG_ELEMENT);
			for (int i= 0; i < elementMem.length; i++) {
				Object element= JavaCore.create(elementMem[i].getString(TAG_PATH));
				list.add(element);
			}
			fViewer.setSelection(new StructuredSelection(list));
		}
	}

	protected void restoreExpansionState(IMemento memento) {
		IMemento childMem= memento.getChild(TAG_EXPANDED);
		if (childMem != null) {
			ArrayList elements= new ArrayList();
			IMemento[] elementMem= childMem.getChildren(TAG_ELEMENT);
			for (int i= 0; i < elementMem.length; i++) {
				Object element= JavaCore.create(elementMem[i].getString(TAG_PATH));
				elements.add(element);
			}
			fViewer.setExpandedElements(elements.toArray());
		}
	}
	
	/**
	 * Create the KeyListener for doing the refresh on the viewer.
	 */
	private void initKeyListener() {
		fViewer.getControl().addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent event) {
				doKeyPressed(event);
			}
		});
	}

	private void doKeyPressed(KeyEvent event) {
		if (event.stateMask != 0) 
			return;		
		
		int key= event.keyCode;
		if (key == SWT.F5) {
			fRefreshAction.selectionChanged(
				(IStructuredSelection) fViewer.getSelection());
			if (fRefreshAction.isEnabled())
				fRefreshAction.run();
		} if (event.character == SWT.DEL){
			fDeleteAction.update();
			if (fDeleteAction.isEnabled())
				fDeleteAction.run();
		}
	}

	void initFrameList() {
		fFrameSource= new PackagesFrameSource(this);
		fFrameList= new FrameList(fFrameSource);
		fFrameSource.connectTo(fFrameList);
	}

	/**
	 * An editor has been activated.  Set the selection in this Packages Viewer
	 * to be the editor's input, if linking is enabled.
	 */
	void editorActivated(IEditorPart editor) {
		if (!isLinkingEnabled())  
			return;
		Object input= getElementOfInput(editor.getEditorInput());
		Object element= null;
		
		if (input instanceof IFile) 
			element= JavaCore.create((IFile)input);
			
		if (element == null) // try a non Java resource
			element= input;
			
		if (element != null) {
			// if the current selection is a child of the new
			// selection then ignore it.
			IStructuredSelection oldSelection= (IStructuredSelection)getSelection();
			if (oldSelection.size() == 1) {
				Object o= oldSelection.getFirstElement();
				if (o instanceof IMember) {
					IMember m= (IMember)o;
					if (element.equals(m.getCompilationUnit()))
						return;
					if (element.equals(m.getClassFile())) 
						return;
				}
			}
			ISelection newSelection= new StructuredSelection(element);
			if (!fViewer.getSelection().equals(newSelection)) {
				try {
					fViewer.removeSelectionChangedListener(fSelectionListener);
					fViewer.setSelection(newSelection);
				} finally {
					fViewer.addSelectionChangedListener(fSelectionListener);
				}
			}
		}
	}
	
	/**
	 * A compilation unit or class was expanded, expand
	 * the main type.  
	 */
	void expandMainType(Object element) {
		try {
			IType type= null;
			if (element instanceof ICompilationUnit) {
				ICompilationUnit cu= (ICompilationUnit)element;
				IType[] types= cu.getTypes();
				if (types.length > 0)
					type= types[0];
			}
			else if (element instanceof IClassFile) {
				IClassFile cf= (IClassFile)element;
				type= cf.getType();
			}			
			if (type != null) {
				final IType type2= type;
				Control ctrl= fViewer.getControl();
				if (ctrl != null && !ctrl.isDisposed()) {
					ctrl.getDisplay().asyncExec(new Runnable() {
						public void run() {
							Control ctrl= fViewer.getControl();
							if (ctrl != null && !ctrl.isDisposed()) 
								fViewer.expandToLevel(type2, 1);
						}
					}); 
				}
			}
		} catch(JavaModelException e) {
			// no reveal
		}
	}
	
	/**
	 * Returns the element contained in the EditorInput
	 */
	Object getElementOfInput(IEditorInput input) {
		if (input instanceof IClassFileEditorInput)
			return ((IClassFileEditorInput)input).getClassFile();
		else if (input instanceof IFileEditorInput)
			return ((IFileEditorInput)input).getFile();
		else if (input instanceof JarEntryEditorInput)
			return ((JarEntryEditorInput)input).getStorage();
		return null;
	}
	
	/**
 	 * Returns the Viewer.
 	 */
	TreeViewer getViewer() {
		return fViewer;
	}
	
	/**
 	 * Returns the pattern filter for this view.
 	 * @return the pattern filter
 	 */
	JavaElementPatternFilter getPatternFilter() {
		return fPatternFilter;
	}
	
	/**
 	 * Returns the library filter for this view.
 	 * @return the library filter
 	 */
	LibraryFilter getLibraryFilter() {
		return fLibraryFilter;
	}

	/**
 	 * Returns the working set filter for this view.
 	 * @return the working set filter
 	 */
	WorkingSetFilter getWorkingSetFilter() {
		return fWorkingSetFilter;
	}

	/**
 	 * Returns the Binary filter for this view.
 	 * @return the binary filter
 	 */
	BinaryProjectFilter getBinaryFilter() {
		return fBinaryFilter;
	}

	void restoreFilters() {
		IMemento filtersMem= fMemento.getChild(TAG_FILTERS);
		if(filtersMem != null) {	
			IMemento children[]= filtersMem.getChildren(TAG_FILTER);
			String filters[]= new String[children.length];
			for (int i = 0; i < children.length; i++) {
				filters[i]= children[i].getString(TAG_ELEMENT);
			}
			getPatternFilter().setPatterns(filters);
		} else {
			getPatternFilter().setPatterns(new String[0]);
		}
		//restore library
		String show= fMemento.getString(TAG_SHOWLIBRARIES);
		if (show != null)
			getLibraryFilter().setShowLibraries(show.equals("true")); //$NON-NLS-1$
		else 
			initLibraryFilterFromPreferences();		
		
		//restore binary fileter
		String showbin= fMemento.getString(TAG_SHOWBINARIES);
		if (showbin != null)
			getBinaryFilter().setShowBinaries(showbin.equals("true")); //$NON-NLS-1$
		else 
			initBinaryFilterFromPreferences();		
			
		//restore working set
		String workingSetName= fMemento.getString(TAG_WORKINGSET);
		if (workingSetName != null) {
			IWorkingSet ws= SearchUI.findWorkingSet(workingSetName);
			if (ws != null) {
				getWorkingSetFilter().setWorkingSet(ws);
			}
		}
		fMemberFilterActionGroup.restoreState(fMemento);
	}
	
	void initFilterFromPreferences() {
		initBinaryFilterFromPreferences();
		initLibraryFilterFromPreferences();
	}

	void initLibraryFilterFromPreferences() {
		JavaPlugin plugin= JavaPlugin.getDefault();
		boolean show= plugin.getPreferenceStore().getBoolean(TAG_SHOWLIBRARIES);
		getLibraryFilter().setShowLibraries(show);
	}

	void initBinaryFilterFromPreferences() {
		JavaPlugin plugin= JavaPlugin.getDefault();
		boolean showbin= plugin.getPreferenceStore().getBoolean(TAG_SHOWBINARIES);
		getBinaryFilter().setShowBinaries(showbin);
	}
	
	boolean isExpandable(Object element) {
		if (fViewer == null)
			return false;
		return fViewer.isExpandable(element);
	}
	
	/**
	 * Updates the title text and title tool tip.
	 * Called whenever the input of the viewer changes.
	 */ 
	void updateTitle() {		
		Object input= getViewer().getInput();
		String viewName= getConfigurationElement().getAttribute("name"); //$NON-NLS-1$
		if (input == null
			|| (input instanceof IJavaModel)) {
			setTitle(viewName);
			setTitleToolTip(""); //$NON-NLS-1$
		} else {
			String inputText= JavaElementLabels.getTextLabel(input, StandardJavaUILabelProvider.DEFAULT_TEXTFLAGS);
			String title= PackagesMessages.getFormattedString("PackageExplorer.argTitle", new String[] { viewName, inputText }); //$NON-NLS-1$
			setTitle(title);
			setTitleToolTip(getToolTipText(input));
		} 
	}
	
	/**
	 * Sets the decorator for the package explorer.
	 *
	 * @param decorator a label decorator or <code>null</code> for no decorations.
	 * @deprecated To be removed
	 */
	public void setLabelDecorator(ILabelDecorator decorator) {
		if (decorator == null)
			fViewer.setLabelProvider(fJavaElementLabelProvider);
		else
			fViewer.setLabelProvider(new DecoratingLabelProvider(fJavaElementLabelProvider, decorator));
	}
	
	/*
	 * @see IPropertyChangeListener#propertyChange(PropertyChangeEvent)
	 */
	public void propertyChange(PropertyChangeEvent event) {
		if (fViewer == null)
			return;
		
		boolean refreshViewer= false;
	
		if (event.getProperty() == JavaBasePreferencePage.SHOW_CU_CHILDREN) {
			IActionBars actionBars= getViewSite().getActionBars();
			fillToolBar(actionBars.getToolBarManager());
			actionBars.updateActionBars();
			
			boolean showCUChildren= JavaBasePreferencePage.showCompilationUnitChildren();
			((JavaElementContentProvider)fViewer.getContentProvider()).setProvideMembers(showCUChildren);
			
			refreshViewer= true;
		}			

		if (refreshViewer)
			fViewer.refresh();
	}

}
