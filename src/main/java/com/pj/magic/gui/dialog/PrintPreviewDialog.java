package com.pj.magic.gui.dialog;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import org.springframework.stereotype.Component;

import com.pj.magic.util.ComponentUtil;

@Component
public class PrintPreviewDialog extends MagicDialog {

	private JTextArea textArea;
	private JScrollPane scrollPane;
	
	public PrintPreviewDialog() {
		setSize(800, 600);
		setLocationRelativeTo(null);
		setTitle("Print Preview");
	}
	
	@Override
	protected void doWhenEscapeKeyPressed() {
		// none
	}
	
	@PostConstruct
	public void layoutComponents() {
		scrollPane = new JScrollPane(createPrintPreviewPanel());
		add(scrollPane);
	}

	public JPanel createPrintPreviewPanel() {
		JPanel panel = new JPanel();
		
		panel.setLayout(new GridBagLayout());
		panel.setBackground(Color.WHITE);
		panel.setBorder(BorderFactory.createMatteBorder(20, 0, 0, 0, Color.white));
		
		GridBagConstraints c = new GridBagConstraints();
		c.weightx = 1.0;
		
		textArea = new JTextArea();
		textArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));
		textArea.setEditable(false);
		panel.add(textArea, c);
		
		c = new GridBagConstraints();
		c.weighty = 1.0;
		c.gridy = 1;
		panel.add(ComponentUtil.createFiller(1, 1), c);
		
		return panel;
	}
	
	public void updateDisplay(List<String> printPages) {
		textArea.setText(printPages.get(0));
		SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				scrollPane.getVerticalScrollBar().setValue(0);
			}
		});
	}
	
}
