package com.pj.magic.gui.dialog;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.gui.component.MagicToolBar;
import com.pj.magic.gui.component.MagicToolBarButton;
import com.pj.magic.service.PrintService;
import com.pj.magic.util.ComponentUtil;

@Component
public class PrintPreviewDialog extends MagicDialog {

	private static final int COLUMNS_PER_LINE = 80;
	
	@Autowired private PrintService printService;
	
	private JTextArea textArea;
	private JScrollPane scrollPane;
	private JButton previousButton;
	private JButton nextButton;
	private int currentPage;
	private int totalPages;
	private List<String> printPages;
	private JLabel pageNumberLabel;
	
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
		setLayout(new GridBagLayout());
		
		GridBagConstraints c = new GridBagConstraints();
		c.weightx = 1.0;
		c.gridx = 0;
		c.gridy = 0;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.WEST;
		add(createToolBar(), c);
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 1;
		c.weightx = c.weighty = 1.0;
		c.fill = GridBagConstraints.BOTH;
		scrollPane = new JScrollPane(createPrintPreviewPanel());
		add(scrollPane, c);
	}

	private MagicToolBar createToolBar() {
		MagicToolBar toolBar = new MagicToolBar();
		
		pageNumberLabel = new JLabel();
		Dimension dim = new Dimension(120, 20);
		pageNumberLabel.setPreferredSize(dim);
		pageNumberLabel.setMinimumSize(dim);
		pageNumberLabel.setMaximumSize(dim);
		pageNumberLabel.setHorizontalAlignment(JLabel.CENTER);
		toolBar.add(pageNumberLabel);
		
		previousButton = new MagicToolBarButton("left", "Previous Page");
		previousButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				previousPage();
			}
		});
		toolBar.add(previousButton);
		
		nextButton = new MagicToolBarButton("right", "Next Page");
		nextButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				nextPage();
			}
		});
		toolBar.add(nextButton);
		
		JButton printButton = new MagicToolBarButton("print", "Print");
		printButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				print();
			}
		});
		toolBar.add(printButton);
		
		return toolBar;
	}

	protected void print() {
		printService.print(printPages);
	}

	protected void previousPage() {
		displayPage(--currentPage);
		updatePageNumberLabelAndNavigation();
	}

	protected void nextPage() {
		displayPage(++currentPage);
		updatePageNumberLabelAndNavigation();
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
		textArea.setColumns(COLUMNS_PER_LINE);
		textArea.setLineWrap(true);
		panel.add(textArea, c);
		
		c = new GridBagConstraints();
		c.weighty = 1.0;
		c.gridy = 1;
		panel.add(ComponentUtil.createFiller(1, 1), c);
		
		return panel;
	}
	
	public void updateDisplay(List<String> printPages) {
		this.printPages = printPages;
		currentPage = 0;
		totalPages = printPages.size();
		displayPage(currentPage);
		updatePageNumberLabelAndNavigation();
	}

	private void displayPage(int page) {
		textArea.setText(printPages.get(page));
		SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				scrollPane.getVerticalScrollBar().setValue(0);
			}
		});
	}

	private void updatePageNumberLabelAndNavigation() {
		pageNumberLabel.setText("Page " + (currentPage + 1) + " of " + totalPages);
		previousButton.setEnabled(currentPage > 0);
		nextButton.setEnabled(currentPage + 1 < totalPages);
	}
	
}
