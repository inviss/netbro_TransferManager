package kr.co.d2net.main;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import kr.co.d2net.commons.SpringApplication;
import kr.co.d2net.commons.dto.EqTbl;
import kr.co.d2net.commons.dto.Transfer;
import kr.co.d2net.commons.exceptions.ServiceException;
import kr.co.d2net.commons.utils.Utility;
import kr.co.d2net.services.StatuService;
import kr.co.d2net.services.TransferService;

import org.apache.commons.lang.StringUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.swtdesigner.SWTResourceManager;


public class TransferManager {

	final Logger logger = LoggerFactory.getLogger(getClass());

	private Table listTbl;
	private TableColumn ct_num, ct_nm, ct_status, ct_smethod, reg_dt, ct_progress, ct_rcount;
	private Shell g_shell;

	static Display display;

	private Text totalQueTxtG;
	private Text useQueTxtG;
	private Text ftpIpTxtG;
	private Text transferIdsTxtG;
	private Text totalQueTxtP;
	private Text useQueTxtP;
	private Text ftpIpTxtP;
	private Text transferIdsTxtP;
	private Label lblGet;
	private Label lblPut;
	private Label lblQueCount_1;
	private Label lblFtpIp_1;
	private Label lblTargetDir_1;
	private StyledText stLogview;
	private SpringApplication springApplication;

	private File g_file;

	final int time1 = 5000;
	final int time2 = 5000;
	private static String state = null;

	public static void main(String[] args) {
		display = new Display();
		TransferManager tm = new TransferManager();
		Shell shell = tm.open(display);
		tm.mainframe(shell);
		tm.createContents(shell);
		tm.threadGroup(shell);

		while (!shell.isDisposed()) { // 윈도우가 종료 할때 까지 기다린다.
			if (!display.readAndDispatch()) // 윈도우에서 발생한 이벤트를 읽어 처리한다.
				display.sleep(); // 다른 프로그램이 CPU를 사용할 수 있도록 SLEEP 한다.
		}
		display.dispose();
	}

	public void threadGroup(final Shell shell) {

		final StatuService statuService = (StatuService)springApplication.get("statuService");

		// 5초 간격으로 화면에 보여질 전송요청 기록을 갱신한다.
		Runnable timer1 = new Runnable() {
			public void run() {
				showTransferList();
				display.timerExec(time1, this);
			}

			protected void showTransferList() {
				try {
					listTbl.removeAll();
					listTbl.redraw();

					TableItem item = null;
					List<Transfer> transfers = statuService.findTransferStatus(state);
					if(transfers != null && !transfers.isEmpty()) {
						for(Transfer transfer : transfers) {
							item = new TableItem(listTbl, SWT.NONE);
							item.setText(new String[] {
									transfer.getCtId(), transfer.getCtNm(), transfer.getStatus(), transfer.getTfGb(), 
									Integer.toString(transfer.getProgress()==null?0:transfer.getProgress()),
									Integer.toString(transfer.getRecount()==null?0:transfer.getRecount()), 
									Utility.getTimestamp(transfer.getRegDtm(), "yyyy-MM-dd HH:mm:ss") 
							});
							item.setData(transfer.getCtId()+","+transfer.getTfGb()+","+transfer.getStatus());
						}
					}
					if(logger.isInfoEnabled()) {
						logger.info("Transfer List Reload Completed!!");
					}
				} catch (Exception e) {
					logger.error("showTransferList error - "+e.getMessage());
				}
			}
		};
		display.timerExec(1000, timer1);

		Runnable timer2 = new Runnable() {
			public void run() {
				showEqList();
				display.timerExec(time2, this);
			}

			protected void showEqList() {
				try {
					List<EqTbl> eqTbls = statuService.findEqStatus();
					if(eqTbls != null && !eqTbls.isEmpty()) {
						for(EqTbl eqTbl : eqTbls) {
							if(eqTbl.getEqGb().equals("D")) {
								totalQueTxtG.redraw();
								useQueTxtG.redraw();
								transferIdsTxtG.redraw();

								totalQueTxtG.setText(String.valueOf(eqTbl.getWaitQ()));
								useQueTxtG.setText(String.valueOf(eqTbl.getUseQ()));
								ftpIpTxtG.setText(eqTbl.getEqIp());
								transferIdsTxtG.setText(eqTbl.getJobId());
							} else {
								totalQueTxtP.redraw();
								useQueTxtP.redraw();
								transferIdsTxtP.redraw();

								totalQueTxtP.setText(String.valueOf(eqTbl.getWaitQ()));
								useQueTxtP.setText(String.valueOf(eqTbl.getUseQ()));
								ftpIpTxtP.setText(eqTbl.getEqIp());
								transferIdsTxtP.setText(eqTbl.getJobId());
							}
						}
					}
					if(logger.isInfoEnabled()) {
						logger.info("EqList Reload Completed!!");
					}
				} catch (Exception e) {
					logger.error("showTransferList error - "+e.getMessage());
				}
			}
		};
		display.timerExec(1000, timer2);
	}

	protected void createContents(final Shell shell) {

		if(springApplication == null) {
			springApplication = SpringApplication.getInstance();
		}

		Group topGroup = new Group(shell, SWT.NONE);
		topGroup.setBounds(10, 10, 755, 83);

		Label totalQue = new Label(topGroup, SWT.NONE);
		totalQue.setBounds(65, 22, 60, 12);
		totalQue.setText("Total Que:");

		totalQueTxtG = new Text(topGroup, SWT.BORDER);
		totalQueTxtG.setBounds(137, 16, 40, 22);

		Label lblFtpIp = new Label(topGroup, SWT.NONE);
		lblFtpIp.setBounds(194, 22, 50, 12);
		lblFtpIp.setText("Use Que:");

		useQueTxtG = new Text(topGroup, SWT.BORDER);
		useQueTxtG.setBounds(250, 16, 40, 22);

		Label lblTargetDir = new Label(topGroup, SWT.NONE);
		lblTargetDir.setBounds(300, 22, 50, 12);
		lblTargetDir.setText("FTP IP:");

		ftpIpTxtG = new Text(topGroup, SWT.BORDER);
		ftpIpTxtG.setBounds(350, 16, 150, 22);

		Label lblAccess = new Label(topGroup, SWT.NONE);
		lblAccess.setBounds(515, 22, 65, 12);
		lblAccess.setText("Transfer IDs:");

		transferIdsTxtG = new Text(topGroup, SWT.BORDER);
		transferIdsTxtG.setBounds(580, 16, 165, 22);

		lblGet = new Label(topGroup, SWT.NONE);
		lblGet.setFont(SWTResourceManager.getFont("굴림", 10, SWT.BOLD));
		lblGet.setBounds(15, 21, 41, 12);
		lblGet.setText("GET:");

		lblPut = new Label(topGroup, SWT.NONE);
		lblPut.setFont(SWTResourceManager.getFont("굴림", 10, SWT.BOLD));
		lblPut.setBounds(15, 57, 41, 12);
		lblPut.setText("PUT:");

		lblQueCount_1 = new Label(topGroup, SWT.NONE);
		lblQueCount_1.setBounds(65, 58, 70, 12);
		lblQueCount_1.setText("Total Que:");

		totalQueTxtP = new Text(topGroup, SWT.BORDER);
		totalQueTxtP.setBounds(137, 51, 40, 22);

		lblFtpIp_1 = new Label(topGroup, SWT.NONE);
		lblFtpIp_1.setBounds(192, 57, 50, 12);
		lblFtpIp_1.setText("Use Que:");

		useQueTxtP = new Text(topGroup, SWT.BORDER);
		useQueTxtP.setBounds(250, 51, 40, 22);

		lblTargetDir_1 = new Label(topGroup, SWT.NONE);
		lblTargetDir_1.setBounds(300, 58, 50, 12);
		lblTargetDir_1.setText("FTP IP:");

		ftpIpTxtP = new Text(topGroup, SWT.BORDER);
		ftpIpTxtP.setBounds(350, 51, 150, 22);

		Label lblAccess_1 = new Label(topGroup, SWT.NONE);
		lblAccess_1.setBounds(515, 58, 65, 12);
		lblAccess_1.setText("Transfer IDs:");

		transferIdsTxtP = new Text(topGroup, SWT.BORDER);
		transferIdsTxtP.setBounds(580, 51, 165, 22);

		Group centerGroup = new Group(shell, SWT.NONE);
		centerGroup.setBounds(10, 99, 755, 51);

		Label label = new Label(centerGroup, SWT.NONE);
		label.setFont(org.eclipse.wb.swt.SWTResourceManager.getFont("맑은 고딕", 10, SWT.NORMAL));
		label.setText("진행상태:");
		label.setBounds(421, 22, 57, 15);

		final Combo combo = new Combo(centerGroup, SWT.NONE);
		combo.setBounds(480, 18, 88, 23);
		String items[] = { "대기/진행/오류", "대기", "진행", "완료", "오류" };
		combo.setItems(items);
		combo.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				int idx = ((Combo)e.widget).getSelectionIndex();
				switch(idx) {
				case 0:
					state = "";
					break;
				case 1:
					state = "W";
					break;
				case 2:
					state = "I";
					break;
				case 3:
					state = "C";
					break;
				case 4:
					state = "E";
					break;
				default:
					state = "";
					break;
				}
			}
		});

		final TransferService transferService = (TransferService)springApplication.get("transferService");

		Button btnNewButton = new Button(centerGroup, SWT.NONE);
		btnNewButton.setBounds(574, 16, 76, 25);
		btnNewButton.setText("모두삭제");
		btnNewButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(combo.getSelectionIndex() == 1 || combo.getSelectionIndex() == 2 || combo.getSelectionIndex() == 3) {
					displayMessage("오류를 조회한 후 삭제하세요.");
				} else {
					TableItem[] items = listTbl.getItems();
					List<String> deleteList = new ArrayList<String>();
					for(TableItem tableItem : items) {
						if(StringUtils.isNotBlank((String)tableItem.getData()))
							if(!deleteList.contains((String)tableItem.getData()))
								deleteList.add((String)tableItem.getData());
					}
					try {
						transferService.deleteTransfer(deleteList);
					} catch (ServiceException e1) {
						logger.error("오류 컨텐츠 삭제", e);
					}
				}
			}
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		Button btnNewButton_1 = new Button(centerGroup, SWT.NONE);
		btnNewButton_1.setBounds(656, 16, 76, 25);
		btnNewButton_1.setText("모두재요청");
		btnNewButton_1.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(combo.getSelectionIndex() == 1 || combo.getSelectionIndex() == 2 || combo.getSelectionIndex() == 3) {
					displayMessage("오류를 조회한 후 요청하세요.");
				} else {
					TableItem[] items = listTbl.getItems();
					List<String> deleteList = new ArrayList<String>();
					for(TableItem tableItem : items) {
						System.out.println((String)tableItem.getData());
						if(StringUtils.isNotBlank((String)tableItem.getData()))
							if(!deleteList.contains((String)tableItem.getData()))
								deleteList.add((String)tableItem.getData());
					}
					try {
						transferService.updateTransfer(deleteList);
					} catch (ServiceException e1) {
						logger.error("오류 컨텐츠 재요청", e);
					}
				}
			}
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		Group msgGroup = new Group(shell, SWT.NONE);
		msgGroup.setBounds(10, 156, 755, 574);

		listTbl = new Table(msgGroup, SWT.BORDER | SWT.MULTI | SWT.FULL_SELECTION);
		listTbl.setBounds(10, 21, 735, 550);
		listTbl.setHeaderVisible(true);
		listTbl.setLinesVisible(true);

		ct_num = new TableColumn(listTbl, SWT.CENTER);

		ct_num.setText("\uCF58\uD150\uCE20\uC544\uC774\uB514");

		ct_num.setWidth(100);
		ct_num.addListener(SWT.Selection, SortListenerFactory
				.getListener(SortListenerFactory.STRING_COMPARATOR));

		ct_nm = new TableColumn(listTbl, SWT.CENTER);
		ct_nm.setText("\uCF58\uD150\uCE20\uBA85");
		ct_nm.setWidth(189);
		ct_nm.addListener(SWT.Selection, SortListenerFactory
				.getListener(SortListenerFactory.STRING_COMPARATOR));

		ct_status = new TableColumn(listTbl, SWT.CENTER);
		ct_status.setText("\uC804\uC1A1\uC0C1\uD0DC");
		ct_status.setWidth(80);
		ct_status.addListener(SWT.Selection, SortListenerFactory
				.getListener(SortListenerFactory.STRING_COMPARATOR));

		ct_smethod = new TableColumn(listTbl, SWT.CENTER);
		ct_smethod.setText("\uC804\uC1A1\uBC29\uBC95");
		ct_smethod.setWidth(80);
		ct_smethod.addListener(SWT.Selection, SortListenerFactory
				.getListener(SortListenerFactory.STRING_COMPARATOR));

		ct_progress = new TableColumn(listTbl, SWT.CENTER);
		ct_progress.setText("\uC804\uC1A1\uD604\uD669");
		ct_progress.setWidth(70);
		ct_progress.addListener(SWT.Selection, SortListenerFactory
				.getListener(SortListenerFactory.STRING_COMPARATOR));

		ct_rcount = new TableColumn(listTbl, SWT.CENTER);
		ct_rcount.setText("\uD69F\uC218");
		ct_rcount.setWidth(60);
		ct_rcount.addListener(SWT.Selection, SortListenerFactory
				.getListener(SortListenerFactory.STRING_COMPARATOR));

		reg_dt = new TableColumn(listTbl, SWT.CENTER);
		reg_dt.setText("\uB4F1\uB85D\uC77C");
		reg_dt.setWidth(135);
	}

	/**
	 * shell 을 생성한다.
	 */
	private void createShell(Display display) {
		g_shell = new Shell(display);
		g_shell.setImage(getImage("/images/tm.jpg"));
		g_shell.setText("Transfer Manager v1.0");

		g_shell.addShellListener(new ShellAdapter() {
			public void shellClosed(ShellEvent e) {
				System.exit(1);
			}
		});
	}


	/**
	 * 에러 메시지 다이얼 로그를 출력한다.
	 */
	void displayMessage(String meg) {
		MessageBox box = new MessageBox(g_shell, SWT.OK | SWT.ICON_WARNING);
		box.setMessage(meg);
		box.open();
	}

	public void mainframe(final Shell shell) {
		shell.addShellListener(new ShellAdapter() {
			public void shellClosed(ShellEvent e) {
			}
		});

	}

	/**
	 * shell 을 만들고 shell을 반환한다.
	 */
	private Shell open(Display display) {
		createShell(display); // shell 을 만든다.

		g_shell.setSize(787, 782); // 두께, 높이
		g_shell.open();

		return g_shell;
	}

	public static Image getImage(String s) {
		ImageData origin = new ImageData(TransferManager.class.getResourceAsStream(s));
		ImageData mask = origin.getTransparencyMask();
		Display disp = Display.findDisplay(Thread.currentThread());
		return new Image(disp, origin, mask);
	}

	public void scrolling() {
		display.asyncExec(new Thread() {
			public void run() {
				stLogview.setSelection(stLogview.getCharCount());
				stLogview.showSelection();
			}
		});
	}
}