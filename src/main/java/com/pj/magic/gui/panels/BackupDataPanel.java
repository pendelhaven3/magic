package com.pj.magic.gui.panels;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutionException;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.ApplicationProperties;
import com.pj.magic.gui.component.MagicToolBar;
import com.pj.magic.gui.dialog.WaitDialog;

@Component
public class BackupDataPanel extends StandardMagicPanel {

	private static final Logger logger = LoggerFactory.getLogger(BackupDataPanel.class);
	
	private static final String BACKUP_TIMESTAMP_FORMAT = "yyyyMMdd_HHmmss";
	
	@Autowired private WaitDialog waitDialog;
	
	private String backupFilename = null;
	private JButton backupButton;
	private JFileChooser restoreFileChooser;
	private JButton restoreButton;
	
	@Override
	protected void initializeComponents() {
		backupButton = new JButton("Backup Data");
		backupButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				backupData();
			}
		});
		
		restoreFileChooser = new JFileChooser();
		
		restoreButton = new JButton("Restore Data");
		restoreButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				restoreData();
			}
		});
	}

	private void restoreData() {
		restoreFileChooser.setCurrentDirectory(new File(
				getBackupFolderAbsolutePath()));
		int returnVal = restoreFileChooser.showOpenDialog(this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			final File file = restoreFileChooser.getSelectedFile();
			SwingWorker<Integer, Integer> worker = new SwingWorker<Integer, Integer>() {

				public Integer doInBackground() throws Exception {
					Process process = Runtime.getRuntime().exec(
							new String[] { "cmd.exe", "/c", constructRestoreCommand(file) });
					return process.waitFor();
				}

				public void done() {
					SwingUtilities.invokeLater(new Runnable() {
						public void run() {
							waitDialog.setVisible(false);
							waitDialog.dispose();
						}
					});
				}

			};
			worker.execute();
			waitDialog.setVisible(true);
			System.out.println("Hark");
			
			try {
				if (worker.get() == 0) {
					showMessage("Restore completed");
				} else {
					showMessage("Restore failed");
				}
			} catch (InterruptedException | ExecutionException e) {
				logger.error(e.getMessage(), e);
				showMessageForUnexpectedError();
			}
		}
	}

	private String constructRestoreCommand(File file) {
		String command = "mysql -u{0} -p{1} {2} < \"{3}\"";
		backupFilename = constructBackupFilename();
		return MessageFormat.format(command, 
				ApplicationProperties.getProperty("db.user"),
				ApplicationProperties.getProperty("db.password"),
				ApplicationProperties.getProperty("db.name"),
				file.getAbsolutePath()
		);
	}

	private void backupData() {
		try {
			Process process = Runtime.getRuntime().exec(
					new String[] {"cmd.exe", "/c", constructBackupCommand()}); // TODO: zip output
			int result = process.waitFor();
			if (result == 0) {
				showMessage("Backup completed.\n" + backupFilename);
			} else {
				showMessage("Backup failed");
			}
		} catch (IOException | InterruptedException e) {
			logger.error(e.getMessage(), e);
			showMessageForUnexpectedError();
		}
	}

	private String constructBackupCommand() {
		String command = "mysqldump -u{0} -p{1} --database {2} -r \"{3}\"";
		backupFilename = constructBackupFilename();
		return MessageFormat.format(command, 
				ApplicationProperties.getProperty("db.user"),
				ApplicationProperties.getProperty("db.password"),
				ApplicationProperties.getProperty("db.name"),
				backupFilename
		);
	}

	private String constructBackupFilename() {
		StringBuilder sb = new StringBuilder(getBackupFolderAbsolutePath());
		sb.append("\\").append(ApplicationProperties.getProperty("db.backup.name"));
		sb.append("_").append(new SimpleDateFormat(BACKUP_TIMESTAMP_FORMAT).format(new Date()));
		sb.append(".sql");
		return sb.toString();
	}

	@Override
	protected void layoutMainPanel(JPanel mainPanel) {
		mainPanel.setLayout(new GridBagLayout());
		int currentRow = 0;
		
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		mainPanel.add(backupButton, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		mainPanel.add(restoreButton, c);
	}

	@Override
	protected void registerKeyBindings() {
		// none
	}

	public void updateDisplay() {
	}
	
	@Override
	protected void doOnBack() {
		getMagicFrame().switchToMainMenuPanel();
	}

	@Override
	protected void addToolBarButtons(MagicToolBar toolBar) {
		// none
	}
	
	private String getBackupFolderAbsolutePath() {
		Path directory = Paths.get(System.getProperty("user.home"),
				"Desktop",
				ApplicationProperties.getProperty("db.backup.folder.name"));
		if (Files.notExists(directory)) {
			try {
				Files.createDirectory(directory);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		directory = directory.toAbsolutePath();
		return directory.toString();
	}
	
}