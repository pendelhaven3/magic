package com.pj.magic.gui.panels;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JPanel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.pj.magic.ApplicationProperties;
import com.pj.magic.gui.component.MagicToolBar;

@Component
public class BackupDataPanel extends StandardMagicPanel {

	private static final Logger logger = LoggerFactory.getLogger(BackupDataPanel.class);
	
	private static final String BACKUP_TIMESTAMP_FORMAT = "yyyyMMdd_HHmmss";
	
	private String backupFilename = null;
	private JButton backupButton;
	
	@Override
	protected void initializeComponents() {
		backupButton = new JButton("Backup Data");
		backupButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				backupData();
			}
		});
	}

	private void backupData() {
		try {
			Process process = Runtime.getRuntime().exec(constructCommand()); // TODO: zip output
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

	private String constructCommand() {
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
		Path directory = Paths.get(
				System.getProperty("user.home"), "Desktop", 
				ApplicationProperties.getProperty("db.backup.folder.name"));
		if (Files.notExists(directory)) {
			try {
				Files.createDirectory(directory);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		directory = directory.toAbsolutePath();
		
		StringBuilder sb = new StringBuilder(directory.toString());
		sb.append("\\").append(ApplicationProperties.getProperty("db.backup.name"));
		sb.append("_").append(new SimpleDateFormat(BACKUP_TIMESTAMP_FORMAT).format(new Date()));
		sb.append(".sql");
		return sb.toString();
	}

	@Override
	protected void layoutMainPanel(JPanel mainPanel) {
		mainPanel.setLayout(new GridBagLayout());
		
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		mainPanel.add(backupButton, c);
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

}