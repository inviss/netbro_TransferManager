package kr.co.d2net.main;

import java.util.Date;

import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.ListenerList;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

public class TransferJobManager {

	private static class WorkerThread extends Thread {

		public final static String SEC = "second";
		private Display display;
		private ListenerList listeners;

		public WorkerThread(Display display) {
			this.display = display;
			listeners = new ListenerList();
		}

		public void addPropertyChangeListener(IPropertyChangeListener listener) {
			listeners.add(listener);
		}

		@SuppressWarnings("unused")
		public void removePropertyChangeListener(
				IPropertyChangeListener listener) {
			listeners.remove(listener);
		}

		protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
			Object[] listeners = this.listeners.getListeners();
			for (int i = 0; i < listeners.length; ++i) {
				((IPropertyChangeListener)listeners[i]).propertyChange(
						new PropertyChangeEvent(this, propertyName, oldValue, newValue)
				);
			}
		}

		public void run() {
			while (true) {
				synchronized (this) {
					try {
						this.wait(1000);
					}
					catch (InterruptedException ex) {
						break;
					}
				}
				display.asyncExec(new Runnable() {
					public void run() {
						firePropertyChange(SEC, null, new Date());
					}
				});
			}
		}
	}

	public static void main(String[] args) {
		Display display = new Display();
		Shell shell = new Shell(display);
		shell.setLayout(new FillLayout());

		TimeLabel label = new TimeLabel(shell, SWT.NONE);
		WorkerThread thread = new WorkerThread(display);
		thread.addPropertyChangeListener(label);
		shell.open();
		thread.start();

		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}

		thread.interrupt();
		try {
			thread.join();
		}
		catch (InterruptedException ex) {
			ex.printStackTrace();
		}
		display.dispose();
	}

	private static class TimeLabel implements IPropertyChangeListener {
		private Label label;

		public TimeLabel(Composite parent, int style) {
			label = new Label(parent, style);
		}

		public void propertyChange(PropertyChangeEvent event) {
			label.setText(((Date)event.getNewValue()).toString());
		}
	}

}
