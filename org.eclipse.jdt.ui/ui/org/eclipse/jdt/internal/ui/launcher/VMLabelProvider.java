package org.eclipse.jdt.internal.ui.launcher;import org.eclipse.jdt.launching.IVMInstall;import org.eclipse.jdt.launching.IVMInstallType;import org.eclipse.jface.viewers.ILabelProvider;import org.eclipse.jface.viewers.ILabelProviderListener;import org.eclipse.jface.viewers.ITableLabelProvider;import org.eclipse.swt.graphics.Image;

public class VMLabelProvider implements ITableLabelProvider, ILabelProvider {

	/**
	 * @see IBaseLabelProvider#removeListener(ILabelProviderListener)
	 */
	public void removeListener(ILabelProviderListener listener) {
	}

	/**
	 * @see IBaseLabelProvider#isLabelProperty(Object, String)
	 */
	public boolean isLabelProperty(Object element, String property) {
		return false;
	}

	/**
	 * @see IBaseLabelProvider#dispose()
	 */
	public void dispose() {
	}

	/**
	 * @see IBaseLabelProvider#addListener(ILabelProviderListener)
	 */
	public void addListener(ILabelProviderListener listener) {
	}

	/**
	 * @see ITableLabelProvider#getColumnText(Object, int)
	 */
	public String getColumnText(Object element, int columnIndex) {
		if (element instanceof IVMInstall)
			return ((IVMInstall)element).getName();
		if (element instanceof IVMInstallType)
			return ((IVMInstallType)element).getName();
		return element.toString();
	}

	/**
	 * @see ITableLabelProvider#getColumnImage(Object, int)
	 */
	public Image getColumnImage(Object element, int columnIndex) {
		return null;
	}

	/**
	 * @see ILabelProvider#getText(Object)
	 */
	public String getText(Object element) {
		return getColumnText(element, 0);
	}

	/**
	 * @see ILabelProvider#getImage(Object)
	 */
	public Image getImage(Object element) {
		return getColumnImage(element, 0);
	}

}
