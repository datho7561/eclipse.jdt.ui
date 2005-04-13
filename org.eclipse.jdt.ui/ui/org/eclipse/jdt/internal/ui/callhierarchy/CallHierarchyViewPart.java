/*******************************************************************************
 * Copyright (c) 2000, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Jesper Kamstrup Linnet (eclipse@kamstrup-linnet.dk) - initial API and implementation
 *             (report 36180: Callers/Callees view)
 *   Michael Fraenkel (fraenkel@us.ibm.com) - patch
 *             (report 60714: Call Hierarchy: display search scope in view title)
 *******************************************************************************/
package org.eclipse.jdt.internal.ui.callhierarchy;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import org.eclipse.help.IContextProvider;

import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.util.TransferDragSourceListener;
import org.eclipse.jface.util.TransferDropTargetListener;
import org.eclipse.jface.viewers.IOpenListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.OpenEvent;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.Viewer;

import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.actions.ActionContext;
import org.eclipse.ui.actions.ActionGroup;
import org.eclipse.ui.part.PageBook;
import org.eclipse.ui.part.ResourceTransfer;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.texteditor.ITextEditor;

import org.eclipse.ui.views.navigator.LocalSelectionTransfer;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.search.IJavaSearchScope;

import org.eclipse.jdt.internal.corext.callhierarchy.CallHierarchy;
import org.eclipse.jdt.internal.corext.callhierarchy.CallLocation;
import org.eclipse.jdt.internal.corext.callhierarchy.MethodWrapper;
import org.eclipse.jdt.internal.corext.util.Messages;

import org.eclipse.jdt.ui.IContextMenuConstants;
import org.eclipse.jdt.ui.JavaElementLabels;
import org.eclipse.jdt.ui.actions.CCPActionGroup;
import org.eclipse.jdt.ui.actions.GenerateActionGroup;
import org.eclipse.jdt.ui.actions.JavaSearchActionGroup;
import org.eclipse.jdt.ui.actions.OpenEditorActionGroup;
import org.eclipse.jdt.ui.actions.OpenViewActionGroup;
import org.eclipse.jdt.ui.actions.RefactorActionGroup;

import org.eclipse.jdt.internal.ui.IJavaHelpContextIds;
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jdt.internal.ui.actions.CompositeActionGroup;
import org.eclipse.jdt.internal.ui.dnd.DelegatingDropAdapter;
import org.eclipse.jdt.internal.ui.dnd.JdtViewerDragAdapter;
import org.eclipse.jdt.internal.ui.dnd.ResourceTransferDragAdapter;
import org.eclipse.jdt.internal.ui.javaeditor.EditorUtility;
import org.eclipse.jdt.internal.ui.packageview.SelectionTransferDragAdapter;
import org.eclipse.jdt.internal.ui.typehierarchy.SelectionProviderMediator;
import org.eclipse.jdt.internal.ui.util.JavaUIHelp;
import org.eclipse.jdt.internal.ui.viewsupport.StatusBarUpdater;

/**
 * This is the main view for the callers plugin. It builds a tree of callers/callees
 * and allows the user to double click an entry to go to the selected method.
 *
 */
public class CallHierarchyViewPart extends ViewPart implements ICallHierarchyViewPart,
    ISelectionChangedListener {
	
	private class CallHierarchySelectionProvider extends SelectionProviderMediator {
		
		public CallHierarchySelectionProvider(StructuredViewer[] viewers) {
			super(viewers);
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jdt.internal.ui.typehierarchy.SelectionProviderMediator#getSelection()
		 */
		public ISelection getSelection() {
			ISelection selection= super.getSelection();
			if (!selection.isEmpty()) {
				return CallHierarchyUI.convertSelection(selection);
			}
			return selection;
		}
	}
	
    private static final String DIALOGSTORE_VIEWORIENTATION = "CallHierarchyViewPart.orientation"; //$NON-NLS-1$
    private static final String DIALOGSTORE_CALL_MODE = "CallHierarchyViewPart.call_mode"; //$NON-NLS-1$
	/**
	 * The key to be used is <code>DIALOGSTORE_RATIO + fCurrentOrientation</code>.
	 */
	private static final String DIALOGSTORE_RATIO= "CallHierarchyViewPart.ratio"; //$NON-NLS-1$
	
    static final int VIEW_ORIENTATION_VERTICAL = 0;
    static final int VIEW_ORIENTATION_HORIZONTAL = 1;
    static final int VIEW_ORIENTATION_SINGLE = 2;
    static final int VIEW_ORIENTATION_AUTOMATIC = 3;
    static final int CALL_MODE_CALLERS = 0;
    static final int CALL_MODE_CALLEES = 1;
    static final String GROUP_SEARCH_SCOPE = "MENU_SEARCH_SCOPE"; //$NON-NLS-1$
	static final String ID_CALL_HIERARCHY = "org.eclipse.jdt.callhierarchy.view"; //$NON-NLS-1$
	private static final String GROUP_FOCUS = "group.focus"; //$NON-NLS-1$
    private static final int PAGE_EMPTY = 0;
    private static final int PAGE_VIEWER = 1;
    private Label fNoHierarchyShownLabel;
    private PageBook fPagebook;
    private IDialogSettings fDialogSettings;
    private int fCurrentOrientation;
    int fOrientation= VIEW_ORIENTATION_AUTOMATIC;
    private int fCurrentCallMode;
    private MethodWrapper fCalleeRoot;
    private MethodWrapper fCallerRoot;
    private IMemento fMemento;
    private IMethod fShownMethod;
    private CallHierarchySelectionProvider fSelectionProviderMediator;
    private List fMethodHistory;
    private LocationViewer fLocationViewer;
    private SashForm fHierarchyLocationSplitter;
    private Clipboard fClipboard;
    private SearchScopeActionGroup fSearchScopeActions;
    private ToggleOrientationAction[] fToggleOrientationActions;
    private ToggleCallModeAction[] fToggleCallModeActions;
    private CallHierarchyFiltersActionGroup fFiltersActionGroup;
    private HistoryDropDownAction fHistoryDropDownAction;
    private RefreshAction fRefreshAction;
    private OpenLocationAction fOpenLocationAction;
    private FocusOnSelectionAction fFocusOnSelectionAction;
    private CopyCallHierarchyAction fCopyAction;
    private CancelSearchAction fCancelSearchAction;
    private CompositeActionGroup fActionGroups;
    private CallHierarchyViewer fCallHierarchyViewer;
    private boolean fShowCallDetails;
	protected Composite fParent;
	private IPartListener2 fPartListener;

    public CallHierarchyViewPart() {
        super();

        fDialogSettings = JavaPlugin.getDefault().getDialogSettings();

        fMethodHistory = new ArrayList();
    }

    public void setFocus() {
        fPagebook.setFocus();
    }

    /**
     * Sets the history entries
     */
    public void setHistoryEntries(IMethod[] elems) {
        fMethodHistory.clear();

        for (int i = 0; i < elems.length; i++) {
            fMethodHistory.add(elems[i]);
        }

        updateHistoryEntries();
    }

    /**
     * Gets all history entries.
     */
    public IMethod[] getHistoryEntries() {
        if (fMethodHistory.size() > 0) {
            updateHistoryEntries();
        }

        return (IMethod[]) fMethodHistory.toArray(new IMethod[fMethodHistory.size()]);
    }

    /**
     * Method setMethod.
     * @param method
     */
    public void setMethod(IMethod method) {
        if (method == null) {
            showPage(PAGE_EMPTY);

            return;
        }
        if ((method != null) && !method.equals(fShownMethod)) {
            addHistoryEntry(method);
        }

        this.fShownMethod = method;

        refresh();
    }

    public IMethod getMethod() {
        return fShownMethod;
    }

    public MethodWrapper getCurrentMethodWrapper() {
        if (fCurrentCallMode == CALL_MODE_CALLERS) {
            return fCallerRoot;
        } else {
            return fCalleeRoot;
        }
    }
           
    /**
     * called from ToggleOrientationAction.
     * @param orientation VIEW_ORIENTATION_HORIZONTAL or VIEW_ORIENTATION_VERTICAL
     */
    void setOrientation(int orientation) {
        if (fCurrentOrientation != orientation) {
            if ((fLocationViewer != null) && !fLocationViewer.getControl().isDisposed() &&
                        (fHierarchyLocationSplitter != null) &&
                        !fHierarchyLocationSplitter.isDisposed()) {
                if (orientation == VIEW_ORIENTATION_SINGLE) {
                    setShowCallDetails(false);
                } else {
                    if (fCurrentOrientation == VIEW_ORIENTATION_SINGLE) {
                        setShowCallDetails(true);
                    }

                    boolean horizontal = orientation == VIEW_ORIENTATION_HORIZONTAL;
                    fHierarchyLocationSplitter.setOrientation(horizontal ? SWT.HORIZONTAL
                                                                         : SWT.VERTICAL);
                }

                fHierarchyLocationSplitter.layout();
            }

            updateCheckedState();

            fCurrentOrientation = orientation;
			
			restoreSplitterRatio();
        }
    }

	private void updateCheckedState() {
		for (int i= 0; i < fToggleOrientationActions.length; i++) {
			fToggleOrientationActions[i].setChecked(fOrientation == fToggleOrientationActions[i].getOrientation());
		}
	}

    /**
     * called from ToggleCallModeAction.
     * @param mode CALL_MODE_CALLERS or CALL_MODE_CALLEES
     */
    void setCallMode(int mode) {
        if (fCurrentCallMode != mode) {
            for (int i = 0; i < fToggleCallModeActions.length; i++) {
                fToggleCallModeActions[i].setChecked(mode == fToggleCallModeActions[i].getMode());
            }

            fCurrentCallMode = mode;
            fDialogSettings.put(DIALOGSTORE_CALL_MODE, mode);

            updateView();
        }
    }

    public IJavaSearchScope getSearchScope() {
        return fSearchScopeActions.getSearchScope();
    }

    public void setShowCallDetails(boolean show) {
        fShowCallDetails = show;
        showOrHideCallDetailsView();
    }

    private void initDragAndDrop() {
        addDragAdapters(fCallHierarchyViewer);
        addDropAdapters(fCallHierarchyViewer);
        addDropAdapters(fLocationViewer);

        //dnd on empty hierarchy
        DropTarget dropTarget = new DropTarget(fPagebook, DND.DROP_MOVE | DND.DROP_COPY | DND.DROP_LINK | DND.DROP_DEFAULT);
        dropTarget.setTransfer(new Transfer[] { LocalSelectionTransfer.getInstance() });
        dropTarget.addDropListener(new CallHierarchyTransferDropAdapter(this, fCallHierarchyViewer));
    }
        
	private void addDropAdapters(StructuredViewer viewer) {
		Transfer[] transfers= new Transfer[] { LocalSelectionTransfer.getInstance() };
		int ops= DND.DROP_MOVE | DND.DROP_COPY | DND.DROP_LINK | DND.DROP_DEFAULT;
		
		TransferDropTargetListener[] dropListeners= new TransferDropTargetListener[] {
			new CallHierarchyTransferDropAdapter(this, viewer)
		};
		viewer.addDropSupport(ops, transfers, new DelegatingDropAdapter(dropListeners));
	}

	private void addDragAdapters(StructuredViewer viewer) {
		int ops= DND.DROP_COPY | DND.DROP_LINK;
		Transfer[] transfers= new Transfer[] { LocalSelectionTransfer.getInstance(), ResourceTransfer.getInstance()};

		TransferDragSourceListener[] dragListeners= new TransferDragSourceListener[] {
			new SelectionTransferDragAdapter(viewer),
			new ResourceTransferDragAdapter(viewer)
		};
		viewer.addDragSupport(ops, transfers, new JdtViewerDragAdapter(viewer, dragListeners));
	}	
            
    public void createPartControl(Composite parent) {
    	fParent= parent;
    	addResizeListener(parent);
        fPagebook = new PageBook(parent, SWT.NONE);

        // Page 1: Viewers
        createHierarchyLocationSplitter(fPagebook);
        createCallHierarchyViewer(fHierarchyLocationSplitter);
        createLocationViewer(fHierarchyLocationSplitter);

        // Page 2: Nothing selected
        fNoHierarchyShownLabel = new Label(fPagebook, SWT.TOP + SWT.LEFT + SWT.WRAP);
        fNoHierarchyShownLabel.setText(CallHierarchyMessages.CallHierarchyViewPart_empty); //$NON-NLS-1$   

		initDragAndDrop();

        showPage(PAGE_EMPTY);

        fSelectionProviderMediator = new CallHierarchySelectionProvider(new StructuredViewer[] {
                    fCallHierarchyViewer, fLocationViewer
                });

        IStatusLineManager slManager = getViewSite().getActionBars().getStatusLineManager();
        fSelectionProviderMediator.addSelectionChangedListener(new StatusBarUpdater(slManager));
        getSite().setSelectionProvider(fSelectionProviderMediator);

        fClipboard= new Clipboard(parent.getDisplay());
        
        makeActions();
        fillViewMenu();
        fillActionBars();

        initOrientation();
        initCallMode();

        if (fMemento != null) {
            restoreState(fMemento);
        }
		restoreSplitterRatio();
		addPartListener();
   }

	private void restoreSplitterRatio() {
		String ratio= fDialogSettings.get(DIALOGSTORE_RATIO + fCurrentOrientation);
		if (ratio == null)
			return;
		int intRatio= Integer.parseInt(ratio);
        fHierarchyLocationSplitter.setWeights(new int[] {intRatio, 1000 - intRatio});
	}

	private void saveSplitterRatio() {
		if (fHierarchyLocationSplitter != null && ! fHierarchyLocationSplitter.isDisposed()) {
	        int[] weigths = fHierarchyLocationSplitter.getWeights();
	        int ratio = (weigths[0] * 1000) / (weigths[0] + weigths[1]);
			String key= DIALOGSTORE_RATIO + fCurrentOrientation;
	        fDialogSettings.put(key, ratio);
		}
	}

	private void addPartListener() {
		fPartListener= new IPartListener2() {
					public void partActivated(IWorkbenchPartReference partRef) { }
					public void partBroughtToTop(IWorkbenchPartReference partRef) { }
					public void partClosed(IWorkbenchPartReference partRef) {
						if (ID_CALL_HIERARCHY.equals(partRef.getId()))
							saveViewSettings();
					}
					public void partDeactivated(IWorkbenchPartReference partRef) {
						if (ID_CALL_HIERARCHY.equals(partRef.getId()))
							saveViewSettings();
					}
					public void partOpened(IWorkbenchPartReference partRef) { }
					public void partHidden(IWorkbenchPartReference partRef) { }
					public void partVisible(IWorkbenchPartReference partRef) { }
					public void partInputChanged(IWorkbenchPartReference partRef) { }
				};
		getViewSite().getPage().addPartListener(fPartListener);
	}

	protected void saveViewSettings() {
		saveSplitterRatio();
		fDialogSettings.put(DIALOGSTORE_VIEWORIENTATION, fOrientation);
	}

	private void addResizeListener(Composite parent) {
		parent.addControlListener(new ControlListener() {
			public void controlMoved(ControlEvent e) {
			}
			public void controlResized(ControlEvent e) {
				computeOrientation();
			}
		});
	}

	void computeOrientation() {
		saveSplitterRatio();
		fDialogSettings.put(DIALOGSTORE_VIEWORIENTATION, fOrientation);
		if (fOrientation != VIEW_ORIENTATION_AUTOMATIC) {
			setOrientation(fOrientation);
		}
		else {
			if (fOrientation == VIEW_ORIENTATION_SINGLE)
				return;
			Point size= fParent.getSize();
			if (size.x != 0 && size.y != 0) {
				if (size.x > size.y) 
					setOrientation(VIEW_ORIENTATION_HORIZONTAL);
				else 
					setOrientation(VIEW_ORIENTATION_VERTICAL);
			}
		}
	}

    private void showPage(int page) {
        if (page == PAGE_EMPTY) {
            fPagebook.showPage(fNoHierarchyShownLabel);
        } else {
            fPagebook.showPage(fHierarchyLocationSplitter);
        }
    }

    /**
     * Restores the type hierarchy settings from a memento.
     */
    private void restoreState(IMemento memento) {
        fSearchScopeActions.restoreState(memento);
    }

    private void initCallMode() {
        int mode;

        try {
            mode = fDialogSettings.getInt(DIALOGSTORE_CALL_MODE);

            if ((mode < 0) || (mode > 1)) {
                mode = CALL_MODE_CALLERS;
            }
        } catch (NumberFormatException e) {
            mode = CALL_MODE_CALLERS;
        }

        // force the update
        fCurrentCallMode = -1;

        // will fill the main tool bar
        setCallMode(mode);
    }

    private void initOrientation() {

        try {
            fOrientation = fDialogSettings.getInt(DIALOGSTORE_VIEWORIENTATION);

            if ((fOrientation < 0) || (fOrientation > 3)) {
            	fOrientation = VIEW_ORIENTATION_AUTOMATIC;
            }
        } catch (NumberFormatException e) {
        	fOrientation = VIEW_ORIENTATION_AUTOMATIC;
        }

        // force the update
        fCurrentOrientation = -1;
        setOrientation(fOrientation);
    }

    private void fillViewMenu() {
        IActionBars actionBars = getViewSite().getActionBars();
        IMenuManager viewMenu = actionBars.getMenuManager();
        viewMenu.add(new Separator());

        for (int i = 0; i < fToggleCallModeActions.length; i++) {
            viewMenu.add(fToggleCallModeActions[i]);
        }

        viewMenu.add(new Separator());

        for (int i = 0; i < fToggleOrientationActions.length; i++) {
            viewMenu.add(fToggleOrientationActions[i]);
        }
    }

    /**
     *
     */
    public void dispose() {
        if (fActionGroups != null)
            fActionGroups.dispose();
		
		if (fClipboard != null)
	        fClipboard.dispose();
		
		if (fPartListener != null) {
			getViewSite().getPage().removePartListener(fPartListener);
			fPartListener= null;
		}

        super.dispose();
    }

    /**
     * Goes to the selected entry, without updating the order of history entries.
     */
    public void gotoHistoryEntry(IMethod entry) {
        if (fMethodHistory.contains(entry)) {
            setMethod(entry);
        }
    }

    /* (non-Javadoc)
     * Method declared on IViewPart.
     */
    public void init(IViewSite site, IMemento memento)
        throws PartInitException {
        super.init(site, memento);
        fMemento = memento;
    }

    /**
     *
     */
    public void refresh() {
        setCalleeRoot(null);
        setCallerRoot(null);

        updateView();
    }

    public void saveState(IMemento memento) {
        if (fPagebook == null) {
            // part has not been created
            if (fMemento != null) { //Keep the old state;
                memento.putMemento(fMemento);
            }

            return;
        }

        fSearchScopeActions.saveState(memento);
    }

    public void selectionChanged(SelectionChangedEvent e) {
        if (e.getSelectionProvider() == fCallHierarchyViewer) {
            methodSelectionChanged(e.getSelection());
        }
    }

    /**
     * @param selection
     */
    private void methodSelectionChanged(ISelection selection) {
        if (selection instanceof IStructuredSelection && ((IStructuredSelection) selection).size() == 1) {
            Object selectedElement = ((IStructuredSelection) selection).getFirstElement();

            if (selectedElement instanceof MethodWrapper) {
                MethodWrapper methodWrapper = (MethodWrapper) selectedElement;

                revealElementInEditor(methodWrapper, fCallHierarchyViewer);
                updateLocationsView(methodWrapper);
            } else {
                updateLocationsView(null);
            }
        } else {
        	updateLocationsView(null);
        }
    }

    private void revealElementInEditor(Object elem, Viewer originViewer) {
        // only allow revealing when the type hierarchy is the active pagae
        // no revealing after selection events due to model changes
        if (getSite().getPage().getActivePart() != this) {
            return;
        }

        if (fSelectionProviderMediator.getViewerInFocus() != originViewer) {
            return;
        }

        if (elem instanceof MethodWrapper) {
            CallLocation callLocation = CallHierarchy.getCallLocation(elem);

            if (callLocation != null) {
                IEditorPart editorPart = CallHierarchyUI.isOpenInEditor(callLocation);

                if (editorPart != null) {
                    getSite().getPage().bringToTop(editorPart);

                    if (editorPart instanceof ITextEditor) {
                        ITextEditor editor = (ITextEditor) editorPart;
                        editor.selectAndReveal(callLocation.getStart(),
                            (callLocation.getEnd() - callLocation.getStart()));
                    }
                }
            } else {
                IEditorPart editorPart = CallHierarchyUI.isOpenInEditor(elem);
                getSite().getPage().bringToTop(editorPart);
                EditorUtility.revealInEditor(editorPart,
                    ((MethodWrapper) elem).getMember());
            }
        } else if (elem instanceof IJavaElement) {
            IEditorPart editorPart = EditorUtility.isOpenInEditor(elem);

            if (editorPart != null) {
                //            getSite().getPage().removePartListener(fPartListener);
                getSite().getPage().bringToTop(editorPart);
                EditorUtility.revealInEditor(editorPart, (IJavaElement) elem);

                //            getSite().getPage().addPartListener(fPartListener);
            }
        }
    }
    
    /**
	 * {@inheritDoc}
	 */
    public Object getAdapter(Class adapter) {
    	if (adapter == IContextProvider.class) {
    		return JavaUIHelp.getHelpContextProvider(this, IJavaHelpContextIds.CALL_HIERARCHY_VIEW);
    	}
    	return super.getAdapter(adapter);
    }

    /**
     * Returns the current selection.
     */
    protected ISelection getSelection() {
    	StructuredViewer viewerInFocus= fSelectionProviderMediator.getViewerInFocus();
		if (viewerInFocus != null) {
			return viewerInFocus.getSelection();
		}
		return StructuredSelection.EMPTY;
    }

    protected void fillLocationViewerContextMenu(IMenuManager menu) {
        JavaPlugin.createStandardGroups(menu);

        menu.appendToGroup(IContextMenuConstants.GROUP_SHOW, fOpenLocationAction);
        menu.appendToGroup(IContextMenuConstants.GROUP_SHOW, fRefreshAction);
    }

    protected void handleKeyEvent(KeyEvent event) {
        if (event.stateMask == 0) {
            if (event.keyCode == SWT.F5) {
                if ((fRefreshAction != null) && fRefreshAction.isEnabled()) {
                    fRefreshAction.run();

                    return;
                }
            }
        }
    }

    private IActionBars getActionBars() {
        return getViewSite().getActionBars();
    }

    private void setCalleeRoot(MethodWrapper calleeRoot) {
        this.fCalleeRoot = calleeRoot;
    }

    private MethodWrapper getCalleeRoot() {
        if (fCalleeRoot == null) {
            fCalleeRoot = CallHierarchy.getDefault().getCalleeRoot(fShownMethod);
        }

        return fCalleeRoot;
    }

    private void setCallerRoot(MethodWrapper callerRoot) {
        this.fCallerRoot = callerRoot;
    }

    private MethodWrapper getCallerRoot() {
        if (fCallerRoot == null) {
            fCallerRoot = CallHierarchy.getDefault().getCallerRoot(fShownMethod);
        }

        return fCallerRoot;
    }

    /**
     * Adds the entry if new. Inserted at the beginning of the history entries list.
     */
    private void addHistoryEntry(IJavaElement entry) {
        if (fMethodHistory.contains(entry)) {
            fMethodHistory.remove(entry);
        }

        fMethodHistory.add(0, entry);
        fHistoryDropDownAction.setEnabled(!fMethodHistory.isEmpty());
    }

    /**
     * @param parent
     */
    private void createLocationViewer(Composite parent) {
        fLocationViewer= new LocationViewer(parent);

        fLocationViewer.getControl().addKeyListener(createKeyListener());

        fLocationViewer.initContextMenu(new IMenuListener() {
                public void menuAboutToShow(IMenuManager menu) {
                    fillLocationViewerContextMenu(menu);
                }
            }, ID_CALL_HIERARCHY, getSite());
    }

    private void createHierarchyLocationSplitter(Composite parent) {
        fHierarchyLocationSplitter = new SashForm(parent, SWT.NONE);

        fHierarchyLocationSplitter.addKeyListener(createKeyListener());
    }

    private void createCallHierarchyViewer(Composite parent) {
        fCallHierarchyViewer = new CallHierarchyViewer(parent, this);

        fCallHierarchyViewer.addKeyListener(createKeyListener());
        fCallHierarchyViewer.addSelectionChangedListener(this);

        fCallHierarchyViewer.initContextMenu(new IMenuListener() {
                public void menuAboutToShow(IMenuManager menu) {
                    fillCallHierarchyViewerContextMenu(menu);
                }
            }, ID_CALL_HIERARCHY, getSite());
    }

    /**
     * @param menu
     */
    protected void fillCallHierarchyViewerContextMenu(IMenuManager menu) {
        JavaPlugin.createStandardGroups(menu);

        menu.appendToGroup(IContextMenuConstants.GROUP_SHOW, fRefreshAction);
        menu.appendToGroup(IContextMenuConstants.GROUP_SHOW, new Separator(GROUP_FOCUS));

        if (fFocusOnSelectionAction.canActionBeAdded()) {
            menu.appendToGroup(GROUP_FOCUS, fFocusOnSelectionAction);
        }
        if (fCopyAction.canActionBeAdded()) {
        	menu.appendToGroup(GROUP_FOCUS, fCopyAction);
        }

        fActionGroups.setContext(new ActionContext(getSelection()));
        fActionGroups.fillContextMenu(menu);
        fActionGroups.setContext(null);
    }

    private void fillActionBars() {
        IActionBars actionBars = getActionBars();
        IToolBarManager toolBar = actionBars.getToolBarManager();

        fActionGroups.fillActionBars(actionBars);
        
        toolBar.add(fCancelSearchAction);
        for (int i = 0; i < fToggleCallModeActions.length; i++) {
            toolBar.add(fToggleCallModeActions[i]);
        }
        toolBar.add(fHistoryDropDownAction);
    }

    private KeyListener createKeyListener() {
        KeyListener keyListener = new KeyAdapter() {
                public void keyReleased(KeyEvent event) {
                    handleKeyEvent(event);
                }
            };

        return keyListener;
    }

    /**
     *
     */
    private void makeActions() {
        fRefreshAction = new RefreshAction(this);

        fOpenLocationAction = new OpenLocationAction(this, getSite());
        fLocationViewer.addOpenListener(new IOpenListener() {
                public void open(OpenEvent event) {
                    fOpenLocationAction.run();
                }
            });
        
        fFocusOnSelectionAction = new FocusOnSelectionAction(this);
        fCopyAction= new CopyCallHierarchyAction(this, fClipboard, fCallHierarchyViewer);
        fSearchScopeActions = new SearchScopeActionGroup(this, fDialogSettings);
        fFiltersActionGroup = new CallHierarchyFiltersActionGroup(this,
                fCallHierarchyViewer);
        fHistoryDropDownAction = new HistoryDropDownAction(this);
        fHistoryDropDownAction.setEnabled(false);
        fCancelSearchAction = new CancelSearchAction(this);
        setCancelEnabled(false);
        fToggleOrientationActions = new ToggleOrientationAction[] {
                new ToggleOrientationAction(this, VIEW_ORIENTATION_VERTICAL),
                new ToggleOrientationAction(this, VIEW_ORIENTATION_HORIZONTAL),
                new ToggleOrientationAction(this, VIEW_ORIENTATION_AUTOMATIC),
                new ToggleOrientationAction(this, VIEW_ORIENTATION_SINGLE)
            };
        fToggleCallModeActions = new ToggleCallModeAction[] {
                new ToggleCallModeAction(this, CALL_MODE_CALLERS),
                new ToggleCallModeAction(this, CALL_MODE_CALLEES)
            };
        fActionGroups = new CompositeActionGroup(new ActionGroup[] {
                    new OpenEditorActionGroup(this), 
                    new OpenViewActionGroup(this),
                    new CCPActionGroup(this),
                    new GenerateActionGroup(this), 
                    new RefactorActionGroup(this),
                    new JavaSearchActionGroup(this),
                    fSearchScopeActions, fFiltersActionGroup
                });
    }

    private void showOrHideCallDetailsView() {
        if (fShowCallDetails) {
            fHierarchyLocationSplitter.setMaximizedControl(null);
        } else {
            fHierarchyLocationSplitter.setMaximizedControl(fCallHierarchyViewer.getControl());
        }
    }

    private void updateLocationsView(MethodWrapper methodWrapper) {
        if (methodWrapper != null && methodWrapper.getMethodCall().hasCallLocations()) {
            fLocationViewer.setInput(methodWrapper.getMethodCall().getCallLocations());
        } else {
            fLocationViewer.clearViewer();
        }
    }

    private void updateHistoryEntries() {
        for (int i = fMethodHistory.size() - 1; i >= 0; i--) {
            IMethod method = (IMethod) fMethodHistory.get(i);

            if (!method.exists()) {
                fMethodHistory.remove(i);
            }
        }

        fHistoryDropDownAction.setEnabled(!fMethodHistory.isEmpty());
    }

    /**
     * Method updateView.
     */
    private void updateView() {
        if ((fShownMethod != null)) {
            showPage(PAGE_VIEWER);
            
            CallHierarchy.getDefault().setSearchScope(getSearchScope());

            String elementName= JavaElementLabels.getElementLabel(fShownMethod, JavaElementLabels.ALL_DEFAULT);
            String scopeDescription = fSearchScopeActions.getFullDescription();
            String[] args = new String[] { elementName, scopeDescription};
			if (fCurrentCallMode == CALL_MODE_CALLERS) {
                setContentDescription(Messages.format(CallHierarchyMessages.CallHierarchyViewPart_callsToMethod, args)); 
                fCallHierarchyViewer.setMethodWrapper(getCallerRoot());
            } else {
                setContentDescription(Messages.format(CallHierarchyMessages.CallHierarchyViewPart_callsFromMethod, args)); 
                fCallHierarchyViewer.setMethodWrapper(getCalleeRoot());
            }
        }
    }

    static CallHierarchyViewPart findAndShowCallersView(IWorkbenchPartSite site) {
        IWorkbenchPage workbenchPage = site.getPage();
        CallHierarchyViewPart callersView = null;

        try {
            callersView = (CallHierarchyViewPart) workbenchPage.showView(CallHierarchyViewPart.ID_CALL_HIERARCHY);
        } catch (PartInitException e) {
            JavaPlugin.log(e);
        }

        return callersView;
    }

    /**
     * Cancels the caller/callee search jobs that are currently running.  
     */
    void cancelJobs() {
        fCallHierarchyViewer.cancelJobs();
    }

    /**
     * Sets the enablement state of the cancel button.
     * @param enabled 
     */
    void setCancelEnabled(boolean enabled) {
        fCancelSearchAction.setEnabled(enabled);
    }
}
