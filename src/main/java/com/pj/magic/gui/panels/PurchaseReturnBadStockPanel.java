package com.pj.magic.gui.panels;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.dao.SystemDao;
import com.pj.magic.excel.PurchaseReturnBadStockExcelGenerator;
import com.pj.magic.exception.NotEnoughStocksException;
import com.pj.magic.gui.component.EllipsisButton;
import com.pj.magic.gui.component.ExcelFileFilter;
import com.pj.magic.gui.component.MagicFileChooser;
import com.pj.magic.gui.component.MagicTextField;
import com.pj.magic.gui.component.MagicToolBar;
import com.pj.magic.gui.component.MagicToolBarButton;
import com.pj.magic.gui.dialog.PrintPreviewDialog;
import com.pj.magic.gui.dialog.SelectSupplierDialog;
import com.pj.magic.gui.tables.PurchaseReturnBadStockItemsTable;
import com.pj.magic.model.PurchaseReturnBadStock;
import com.pj.magic.model.Supplier;
import com.pj.magic.service.BadStockAdjustmentInService;
import com.pj.magic.service.PrintService;
import com.pj.magic.service.PurchaseReturnBadStockService;
import com.pj.magic.service.SupplierService;
import com.pj.magic.util.ComponentUtil;
import com.pj.magic.util.ExcelUtil;
import com.pj.magic.util.FileUtil;
import com.pj.magic.util.FormatterUtil;

@Component
public class PurchaseReturnBadStockPanel extends StandardMagicPanel {

	private static final Logger logger = LoggerFactory.getLogger(PurchaseReturnBadStockPanel.class);
	
	private static final String SAVE_SUPPLIER_ACTION_NAME = "SAVE_CUSTOMER_ACTION_NAME";
	private static final String OPEN_SELECT_SUPPLIER_DIALOG_ACTION_NAME = 
			"OPEN_SELECT_CUSTOMER_DIALOG_ACTION_NAME";
	
	@Autowired private PurchaseReturnBadStockItemsTable itemsTable;
	@Autowired private PurchaseReturnBadStockService purchaseReturnBadStockService;
	@Autowired private SupplierService supplierService;
	@Autowired private SelectSupplierDialog selectSupplierDialog;
	@Autowired private PrintService printService;
	@Autowired private BadStockAdjustmentInService badStockAdjustmentInService;
	@Autowired private PrintPreviewDialog printPreviewDialog;
	
	@Autowired
	private SystemDao systemDao;
	
	private PurchaseReturnBadStock purchaseReturnBadStock;
	private JLabel purchaseReturnBadStockNumberField;
	private JTextField supplierCodeField;
	private JLabel supplierNameField;
	private JButton selectSupplierButton;
	private JLabel statusField;
	private JLabel postDateField;
	private JLabel postedByField;
	private MagicTextField remarksField;
	private MagicTextField receivedDateField;
	private JLabel totalItemsField;
	private JLabel totalAmountField;
	private JButton addItemButton;
	private JButton deleteItemButton;
    private JButton addAllSupplierItemsButton;
    private JButton deleteAllItemsButton;
    private JButton postButton;
    private JButton markAsPaidButton;
	private JButton printPreviewButton;
	private JButton printButton;
	private JButton generateExcelButton;
	private JButton generateBadStockAdjustmentInButton;
    private MagicFileChooser excelFileChooser;
	
	@Override
	protected void initializeComponents() {
		supplierCodeField = new MagicTextField();
		supplierNameField = new JLabel();
		
		selectSupplierButton = new EllipsisButton();
		selectSupplierButton.setToolTipText("Select Supplier (F5)");
		selectSupplierButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				selectSupplier();
			}
		});;
		
		remarksField = new MagicTextField();
		remarksField.setMaximumLength(100);
		remarksField.addFocusListener(new FocusAdapter() {
			
			@Override
			public void focusLost(FocusEvent e) {
				saveRemarks();
			}
		});
		
		receivedDateField = new MagicTextField();
		receivedDateField.addFocusListener(new FocusAdapter() {
			
			@Override
			public void focusLost(FocusEvent e) {
				saveReceivedDate();
			}
		});
		
        excelFileChooser = new MagicFileChooser();
        excelFileChooser.setCurrentDirectory(new File(FileUtil.getDesktopFolderPath()));
        excelFileChooser.setFileFilter(ExcelFileFilter.getInstance());
        
		focusOnComponentWhenThisPanelIsDisplayed(supplierCodeField);
		updateTotalAmountFieldWhenItemsTableChanges();
	}

	private void saveRemarks() {
		if (!remarksField.getText().equals(purchaseReturnBadStock.getRemarks())) {
			purchaseReturnBadStock.setRemarks(remarksField.getText());
			purchaseReturnBadStockService.save(purchaseReturnBadStock);
		}
	}
	
	private void saveReceivedDate() {
		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
			
			Date currentReceivedDate = purchaseReturnBadStock.getReceivedDate();
			String currentReceivedDateString = (currentReceivedDate != null) ? 
					dateFormat.format(currentReceivedDate) : null;
			
			if (!receivedDateField.getText().equals(currentReceivedDateString)) {
				if (!StringUtils.isEmpty(receivedDateField.getText())) {
					purchaseReturnBadStock.setReceivedDate(dateFormat.parse(receivedDateField.getText()));
				} else {
					purchaseReturnBadStock.setReceivedDate(null);
				}
				purchaseReturnBadStockService.save(purchaseReturnBadStock);
			}
		} catch (Exception e) {
			showMessageForUnexpectedError(e);
		}
	}
	
	private void selectSupplier() {
		selectSupplierDialog.searchSuppliers(supplierCodeField.getText());
		selectSupplierDialog.setVisible(true);
		
		Supplier supplier = selectSupplierDialog.getSelectedSupplier();
		if (supplier != null) {
			if (purchaseReturnBadStock.getSupplier() != null && purchaseReturnBadStock.getSupplier().equals(supplier)) {
				// skip saving since there is no change
				remarksField.requestFocusInWindow();
				return;
			} else {
				purchaseReturnBadStock.setSupplier(supplier);
				try {
					purchaseReturnBadStockService.save(purchaseReturnBadStock);
					updateDisplay(purchaseReturnBadStock);
					remarksField.requestFocusInWindow();
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
					showMessageForUnexpectedError();
				}
			}
		}
	}

	@Override
	protected void registerKeyBindings() {
		supplierCodeField.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), SAVE_SUPPLIER_ACTION_NAME);
		supplierCodeField.getActionMap().put(SAVE_SUPPLIER_ACTION_NAME, new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				saveSupplier();
			}
		});
		
		supplierCodeField.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0), OPEN_SELECT_SUPPLIER_DIALOG_ACTION_NAME);
		supplierCodeField.getActionMap().put(OPEN_SELECT_SUPPLIER_DIALOG_ACTION_NAME, new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				selectSupplier();
			}
		});
		
		remarksField.onEnterKey(() -> receivedDateField.requestFocusInWindow());
		receivedDateField.onEnterKey(() -> itemsTable.highlight());
	}

	private void saveSupplier() {
		if (purchaseReturnBadStock.getSupplier() != null) {
			if (purchaseReturnBadStock.getSupplier().getCode().equals(supplierCodeField.getText())) {
				// skip saving since there is no change in supplier
				remarksField.requestFocusInWindow();
				return;
			}
		}
		
		if (StringUtils.isEmpty(supplierCodeField.getText())) {
			showErrorMessage("Supplier must be specified");
			return;
		}
		
		Supplier supplier = supplierService.findSupplierByCode(supplierCodeField.getText());
		if (supplier == null) {
			showErrorMessage("No supplier matching code specified");
			return;
		} else {
			purchaseReturnBadStock.setSupplier(supplier);
			try {
				purchaseReturnBadStockService.save(purchaseReturnBadStock);
				updateDisplay(purchaseReturnBadStock);
				remarksField.requestFocusInWindow();
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				showMessageForUnexpectedError();
			}
		}
	}

	@Override
	protected void doOnBack() {
		if (itemsTable.isEditing()) {
			itemsTable.getCellEditor().cancelCellEditing();
		}
		getMagicFrame().switchToPurchaseReturnBadStockListPanel();
	}
	
	private void updateTotalAmountFieldWhenItemsTableChanges() {
		itemsTable.getModel().addTableModelListener(new TableModelListener() {
			
			@Override
			public void tableChanged(TableModelEvent e) {
				totalItemsField.setText(String.valueOf(purchaseReturnBadStock.getTotalItems()));
				totalAmountField.setText(FormatterUtil.formatAmount(purchaseReturnBadStock.getTotalAmount()));
			}
		});
	}

	public void updateDisplay(PurchaseReturnBadStock purchaseReturnBadStock) {
		if (purchaseReturnBadStock.getId() == null) {
			this.purchaseReturnBadStock = purchaseReturnBadStock;
			clearDisplay();
			return;
		}
		
		this.purchaseReturnBadStock = purchaseReturnBadStock = purchaseReturnBadStockService.getPurchaseReturnBadStock(purchaseReturnBadStock.getId());
		
		purchaseReturnBadStockNumberField.setText(purchaseReturnBadStock.getPurchaseReturnBadStockNumber().toString());
		supplierCodeField.setText(purchaseReturnBadStock.getSupplier().getCode());
		supplierCodeField.setEnabled(!purchaseReturnBadStock.isPosted());
		supplierNameField.setText(purchaseReturnBadStock.getSupplier().getName());
		selectSupplierButton.setEnabled(!purchaseReturnBadStock.isPosted());
		statusField.setText(purchaseReturnBadStock.getStatus());
		if (purchaseReturnBadStock.getPostDate() != null) {
			postDateField.setText(FormatterUtil.formatDate(purchaseReturnBadStock.getPostDate()));
		} else {
			postDateField.setText(null);
		}
		if (purchaseReturnBadStock.getPostedBy() != null) {
			postedByField.setText(purchaseReturnBadStock.getPostedBy().getUsername());
		} else {
			postedByField.setText(null);
		}
		remarksField.setText(purchaseReturnBadStock.getRemarks());
		remarksField.setEnabled(!purchaseReturnBadStock.isPosted());
		totalItemsField.setText(String.valueOf(purchaseReturnBadStock.getTotalItems()));
		totalAmountField.setText(purchaseReturnBadStock.getTotalAmount().toString());
		
		if (purchaseReturnBadStock.getReceivedDate() != null) {
			receivedDateField.setText(FormatterUtil.formatDate(purchaseReturnBadStock.getReceivedDate()));
		} else {
			receivedDateField.setText(null);
		}
		receivedDateField.setEnabled(!purchaseReturnBadStock.isPosted());
		
		ComponentUtil.enableButtons(!purchaseReturnBadStock.isPosted(),
		        postButton, addItemButton, deleteItemButton, addAllSupplierItemsButton, deleteAllItemsButton);
		markAsPaidButton.setEnabled(purchaseReturnBadStock.isPosted() && !purchaseReturnBadStock.isPaid());
		
		printPreviewButton.setEnabled(true);
		printButton.setEnabled(true);
		
		itemsTable.setPurchaseReturnBadStock(purchaseReturnBadStock);
	}

	private void clearDisplay() {
		purchaseReturnBadStockNumberField.setText(null);
		supplierCodeField.setText(null);
		supplierCodeField.setEnabled(true);
		supplierNameField.setText(null);
		selectSupplierButton.setEnabled(true);
		statusField.setText(null);
		postDateField.setText(null);
		postedByField.setText(null);
		remarksField.setText(null);
		remarksField.setEnabled(false);
		receivedDateField.setText(null);
		receivedDateField.setEnabled(false);
		totalItemsField.setText(null);
		totalAmountField.setText(null);
		postButton.setEnabled(false);
		markAsPaidButton.setEnabled(false);
		
		itemsTable.setPurchaseReturnBadStock(purchaseReturnBadStock);
		
        ComponentUtil.enableButtons(false,
                addItemButton, deleteItemButton, addAllSupplierItemsButton, deleteAllItemsButton, printPreviewButton, printButton);
	}

	@Override
	protected void layoutMainPanel(JPanel mainPanel) {
		mainPanel.setLayout(new GridBagLayout());
		int currentRow = 0;

		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(Box.createHorizontalStrut(50), c);

		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(100, "PRBS No.:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		purchaseReturnBadStockNumberField = ComponentUtil.createLabel(200, "");
		mainPanel.add(purchaseReturnBadStockNumberField, c);
		
		c = new GridBagConstraints();
		c.gridx = 3;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(Box.createHorizontalStrut(50), c);
		
		c = new GridBagConstraints();
		c.gridx = 4;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(100, "Status:"), c);
		
		c = new GridBagConstraints();
		c.weightx = 1.0;
		c.gridx = 5;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		statusField = ComponentUtil.createLabel(150);
		mainPanel.add(statusField, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(100, "Supplier:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(createSupplierPanel(), c);

		c = new GridBagConstraints();
		c.gridx = 4;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(100, "Post Date:"), c);
		
		c = new GridBagConstraints();
		c.weightx = 1.0;
		c.gridx = 5;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		postDateField = ComponentUtil.createLabel(100, "");
		mainPanel.add(postDateField, c);
		
		currentRow++;
		
        c = new GridBagConstraints();
        c.gridx = 1;
        c.gridy = currentRow;
        c.anchor = GridBagConstraints.WEST;
        mainPanel.add(ComponentUtil.createLabel(100, "Remarks:"), c);
        
        c = new GridBagConstraints();
        c.gridx = 2;
        c.gridy = currentRow;
        c.anchor = GridBagConstraints.WEST;
        remarksField.setPreferredSize(new Dimension(300, 25));
        mainPanel.add(remarksField, c);
        
		c = new GridBagConstraints();
		c.gridx = 4;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(100, "Posted By:"), c);
		
		c = new GridBagConstraints();
		c.weightx = 1.0;
		c.gridx = 5;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		postedByField = ComponentUtil.createLabel(100, "");
		mainPanel.add(postedByField, c);
		
		currentRow++;
		
        c = new GridBagConstraints();
        c.gridx = 1;
        c.gridy = currentRow;
        c.anchor = GridBagConstraints.WEST;
        mainPanel.add(ComponentUtil.createLabel(130, "Received Date:"), c);
        
        c = new GridBagConstraints();
        c.gridx = 2;
        c.gridy = currentRow;
        c.anchor = GridBagConstraints.WEST;
        receivedDateField.setPreferredSize(new Dimension(100, 25));
        mainPanel.add(receivedDateField, c);
        
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(Box.createVerticalStrut(10), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.gridwidth = 6;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(createItemsTableToolBar(), c);

		currentRow++;
		
		c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.weightx = c.weighty = 1.0;
		c.gridx = 0;
		c.gridy = currentRow;
		c.gridwidth = 6;
		c.anchor = GridBagConstraints.CENTER;
		JScrollPane itemsTableScrollPane = new JScrollPane(itemsTable);
		itemsTableScrollPane.setPreferredSize(new Dimension(600, 100));
		mainPanel.add(itemsTableScrollPane, c);

		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.gridwidth = 6;
		c.anchor = GridBagConstraints.EAST;
		mainPanel.add(createTotalsPanel(), c);
	}
	
	private JPanel createSupplierPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.anchor = GridBagConstraints.WEST;
		supplierCodeField.setPreferredSize(new Dimension(100, 25));
		panel.add(supplierCodeField, c);
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = 0;
		c.anchor = GridBagConstraints.WEST;
		selectSupplierButton.setPreferredSize(new Dimension(30, 24));
		panel.add(selectSupplierButton, c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = 0;
		c.anchor = GridBagConstraints.WEST;
		panel.add(Box.createHorizontalStrut(10), c);
		
		c = new GridBagConstraints();
		c.gridx = 3;
		c.gridy = 0;
		c.anchor = GridBagConstraints.WEST;
		supplierNameField.setPreferredSize(new Dimension(200, 20));
		panel.add(supplierNameField, c);
		
		return panel;
	}
	
	private JPanel createTotalsPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		
		int currentRow = 0;
		
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		panel.add(ComponentUtil.createLabel(100, "Total Items:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		totalItemsField = ComponentUtil.createLabel(60);
		panel.add(totalItemsField, c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		panel.add(Box.createHorizontalStrut(10), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		panel.add(ComponentUtil.createLabel(120, "Total Amount:"), c);
		
		c = new GridBagConstraints();
		c.weightx = 1.0;
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		totalAmountField = ComponentUtil.createLabel(100);
		panel.add(totalAmountField, c);
		
		return panel;
	}
	
	private JPanel createItemsTableToolBar() {
		JPanel panel = new JPanel();
		
		addItemButton = new MagicToolBarButton("plus_small", "Add Item", true);
		addItemButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				itemsTable.switchToAddMode();
			}
		});
		panel.add(addItemButton, BorderLayout.WEST);
		
		deleteItemButton = new MagicToolBarButton("minus_small", "Delete Item", true);
		deleteItemButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				itemsTable.removeCurrentlySelectedItem();
			}
		});
		panel.add(deleteItemButton, BorderLayout.WEST);
		
        addAllSupplierItemsButton = new MagicToolBarButton("add_all_small", "Add All Supplier Items", true);
        addAllSupplierItemsButton.addActionListener(e -> allAllSupplierItems());
        panel.add(addAllSupplierItemsButton, BorderLayout.WEST);
		
        deleteAllItemsButton = new MagicToolBarButton("delete_all_small", "Delete All Items", true);
        deleteAllItemsButton.addActionListener(e -> deleteAllSupplierItems());
        panel.add(deleteAllItemsButton, BorderLayout.WEST);
        
		return panel;
	}

	@Override
	protected void addToolBarButtons(MagicToolBar toolBar) {
		postButton = new MagicToolBarButton("post", "Post", e -> postPurchaseReturnBadStock());
		toolBar.add(postButton);
		
		markAsPaidButton = new MagicToolBarButton("coins", "Mark As Paid", e -> markPurchaseReturnBadStockAsPaid());
		toolBar.add(markAsPaidButton);
		
		printPreviewButton = new MagicToolBarButton("print_preview", "Print Preview");
		printPreviewButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				printPreviewDialog.updateDisplay(printService.generateReportAsString(purchaseReturnBadStock));
				printPreviewDialog.setVisible(true);
			}
		});
		toolBar.add(printPreviewButton);
		
		printButton = new MagicToolBarButton("print", "Print");
		printButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				printService.print(purchaseReturnBadStock);
			}
		});
		toolBar.add(printButton);
		
        generateExcelButton = new MagicToolBarButton("excel", "Generate Excel", e -> generateExcel());
        toolBar.add(generateExcelButton);
        
        generateBadStockAdjustmentInButton = new MagicToolBarButton("adjustment_in", "Generate Bad Stock Adjustment In",
        		e -> generateAndPostBadStockAdjustmentIn());
        toolBar.add(generateBadStockAdjustmentInButton);
	}
	
	private void allAllSupplierItems() {
	    if (confirm("Add all available bad stock for supplier items?")) {
	        try {
	            purchaseReturnBadStockService.addAllBadStockForSupplier(purchaseReturnBadStock);
	        } catch (Exception e) {
                logger.error("Unable to add all available supplier bad stock to Purchase Return Bad Stock", e);
                showMessageForUnexpectedError();
                return;
	        }
            updateDisplay(purchaseReturnBadStock);
	    }
	}

    private void deleteAllSupplierItems() {
        if (confirm("Delete all items?")) {
            try {
                purchaseReturnBadStockService.deleteAllItems(purchaseReturnBadStock);
            } catch (Exception e) {
                logger.error("Unable to delete all Purchase Return Bad Stock items", e);
                showMessageForUnexpectedError();
                return;
            }
            updateDisplay(purchaseReturnBadStock);
        }
    }

    private void generateExcel() {
        File file = new File(generateDefaultSpreadsheetName() + ".xlsx");
        excelFileChooser.setSelectedFile(file);
        
        if (!excelFileChooser.selectSaveFile(this)) {
            return;
        }

        PurchaseReturnBadStockExcelGenerator excelGenerator = new PurchaseReturnBadStockExcelGenerator(systemDao);
        purchaseReturnBadStock = purchaseReturnBadStockService.getPurchaseReturnBadStock(purchaseReturnBadStock.getId());
        
        try (
            Workbook workbook = excelGenerator.generate(purchaseReturnBadStock);
            FileOutputStream out = new FileOutputStream(excelFileChooser.getSelectedFile());
        ) {
            workbook.write(out);
        } catch (IOException e) {
            logger.error("Unable to generate Excel file", e);
            showErrorMessage("Unexpected error during excel generation");
            return;
        }

        if (confirm("Excel file generated.\nDo you wish to open the file?")) {
            openExcelFile(excelFileChooser.getSelectedFile());
        }
    }

    private String generateDefaultSpreadsheetName() {
        return new StringBuilder()
            .append(purchaseReturnBadStock.getSupplier().getName())
            .append(" - ")
            .append(new SimpleDateFormat("MMM-dd-yyyy").format(new Date()))
            .append(" - PRBS ")
            .append(purchaseReturnBadStock.getPurchaseReturnBadStockNumber())
            .toString();
    }
    
    private void openExcelFile(File file) {
        try {
            ExcelUtil.openExcelFile(file);
        } catch (IOException e) {
            logger.error("Unable to open Excel file", e);
            showMessageForUnexpectedError();
        }
    }
    
    private void generateAndPostBadStockAdjustmentIn() {
    	try {
        	badStockAdjustmentInService.generateAndPost(purchaseReturnBadStock);
    	} catch (Exception e) {
    		logger.error("Error during generation of Bad Stock Adjustment In", e);
    		showErrorMessage("Error during generation of Bad Stock Adjustment In");
    		return;
    	}
    	
    	showMessage("Bad Stock Adjustment In generated");
    }
    
    private void postPurchaseReturnBadStock() {
		if (itemsTable.isAdding()) {
			itemsTable.switchToEditMode();
		}
		
		if (!purchaseReturnBadStock.hasItems()) {
			showErrorMessage("Cannot post a Purchase Return Bad Stock with no items");
			itemsTable.requestFocusInWindow();
			return;
		}
		
		if (confirm("Do you want to post this Purchase Return Bad Stock?")) {
			try {
				purchaseReturnBadStockService.post(purchaseReturnBadStock);
			} catch (NotEnoughStocksException e) {
				showErrorMessage(e.getMessage());
				return;
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				showMessageForUnexpectedError();
				return;
			}
			
			showMessage("Purchase Return Bad Stock posted");
			updateDisplay(purchaseReturnBadStock);
		}
	}
    
	private void markPurchaseReturnBadStockAsPaid() {
		if (confirm("Mark Purchase Return Bad Stock as paid?")) {
			try {
				purchaseReturnBadStockService.markAsPaid(purchaseReturnBadStock);
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				showMessageForUnexpectedError();
				return;
			}
			
			showMessage("Purchase Return Bad Stock marked as paid");
			updateDisplay(purchaseReturnBadStock);
		}
	}
    
}