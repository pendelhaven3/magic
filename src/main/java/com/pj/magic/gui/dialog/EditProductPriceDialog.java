package com.pj.magic.gui.dialog;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;

import javax.annotation.PostConstruct;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSeparator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.pj.magic.gui.component.MagicTextField;
import com.pj.magic.gui.tables.EditProductPriceTable;
import com.pj.magic.model.PricingScheme;
import com.pj.magic.model.Product;
import com.pj.magic.model.UnitCost;
import com.pj.magic.model.UnitPrice;
import com.pj.magic.service.ProductService;
import com.pj.magic.util.ComponentUtil;
import com.pj.magic.util.DateUtil;
import com.pj.magic.util.FormatterUtil;
import com.pj.magic.util.NumberUtil;

import net.sourceforge.jdatepicker.impl.UtilCalendarModel;

@Component
public class EditProductPriceDialog extends MagicDialog {

    private static final Logger LOGGER = LoggerFactory.getLogger(EditProductPriceDialog.class);
    
	@Autowired private ProductService productService;
	@Autowired private EditProductPriceTable table;
	@Autowired private ProductPriceHistoryDialog productPriceHistoryDialog;

	private PricingScheme pricingScheme;
	private Product product;
	private JLabel productCodeLabel;
	private JLabel productDescriptionLabel;
	private JLabel pricingSchemeNameLabel;
	private JLabel companyListPriceLabel;
	private JButton saveButton;
	private JButton showPriceHistoryButton;
    private JButton scheduleButton;
    private UtilCalendarModel effectiveDateModel;
    private MagicTextField newCompanyListPriceField;
    private ButtonGroup scheduleButtonGroup;
    private JRadioButton immediateRadio;
    private JRadioButton scheduledRadio;
	
	public EditProductPriceDialog() {
		setSize(600, 490);
		setLocationRelativeTo(null);
		setTitle("Edit Product Price");
		getRootPane().setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
	}

	@PostConstruct
	public void initialize() {
		initializeComponents();
		registerKeyBindings();
		layoutComponents();
	}

	private void initializeComponents() {
		productCodeLabel = new JLabel();
		productDescriptionLabel = new JLabel();
		pricingSchemeNameLabel = new JLabel();
		companyListPriceLabel = new JLabel();
		
		saveButton = new JButton("Save");
		saveButton.addActionListener(e -> saveOrSchedule());
		
		showPriceHistoryButton = new JButton("Show Price History");
		showPriceHistoryButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				productPriceHistoryDialog.updateDisplay(product, pricingScheme);
				productPriceHistoryDialog.setVisible(true);
			}
		});
		
		scheduleButton = new JButton("Schedule");
		scheduleButton.addActionListener(e -> schedule());
		effectiveDateModel = new UtilCalendarModel();
		newCompanyListPriceField = new MagicTextField();
		
		immediateRadio = new JRadioButton("Immediate");
        scheduledRadio = new JRadioButton("Scheduled");
		scheduleButtonGroup = new ButtonGroup();
		scheduleButtonGroup.add(immediateRadio);
        scheduleButtonGroup.add(scheduledRadio);
	}

	private void saveOrSchedule() {
	    if (immediateRadio.isSelected()) {
	        if (confirm("Save now?")) {
	            saveUnitCostsAndPrices();
	        }
	    } else if (scheduledRadio.isSelected()) {
	        if (confirm(createScheduleConfirmMessage())) {
	            schedule();
	        }
	    }
    }

    private void saveUnitCostsAndPrices() {
		if (table.isEditing()) {
			if (!table.getCellEditor().stopCellEditing()) {
				table.getEditorComponent().requestFocusInWindow();
				return;
			}
		}
		
		if (!StringUtils.isEmpty(newCompanyListPriceField.getText())) {
            BigDecimal newCompanyListPrice = newCompanyListPriceField.getTextAsBigDecimal();
            productService.updateCompanyListPrice(product, newCompanyListPrice);
            product.setCompanyListPrice(newCompanyListPrice);
            companyListPriceLabel.setText(FormatterUtil.formatAmount(newCompanyListPrice));
		}
		
		productService.saveUnitCostsAndPrices(product, pricingScheme);
		JOptionPane.showMessageDialog(this, "Saved!");
	}

	private void registerKeyBindings() {
		// none
	}

	@Override
	protected void doWhenEscapeKeyPressed() {
		if (table.isEditing()) {
			table.getCellEditor().cancelCellEditing();
		}
	}
	
	private void layoutComponents() {
		setLayout(new GridBagLayout());
		int currentRow = 0;

		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		add(ComponentUtil.createLabel(130, "Pricing Scheme:"), c);

		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		pricingSchemeNameLabel.setPreferredSize(new Dimension(100, 20));
		add(pricingSchemeNameLabel, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		add(ComponentUtil.createLabel(130, "Product Code:"), c);

		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		productCodeLabel.setPreferredSize(new Dimension(150, 20));
		add(productCodeLabel, c);

		c = new GridBagConstraints();
		c.weightx = 1.0;
		c.gridx = 2;
		c.gridy = currentRow;
		add(Box.createGlue(), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		add(ComponentUtil.createLabel(150, "Product Description:"), c);

		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		productDescriptionLabel.setPreferredSize(new Dimension(300, 20));
		add(productDescriptionLabel, c);

		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		add(ComponentUtil.createLabel(160, "Company List Price:"), c);

		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		companyListPriceLabel.setPreferredSize(new Dimension(100, 20));
		add(companyListPriceLabel, c);

		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		add(Box.createVerticalStrut(10), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.weightx = 1.0;
		c.weighty = 0.0;
		c.gridx = 0;
		c.gridy = currentRow;
		c.gridwidth = 3;
		c.fill = GridBagConstraints.HORIZONTAL;
		add(createTablePanel(), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		add(Box.createVerticalStrut(20), c);
		
        currentRow++;
        
        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = currentRow;
        c.anchor = GridBagConstraints.WEST;
        add(ComponentUtil.createLabel(190, "New Company List Price:"), c);

        c = new GridBagConstraints();
        c.gridx = 1;
        c.gridy = currentRow;
        c.anchor = GridBagConstraints.WEST;
        newCompanyListPriceField.setPreferredSize(new Dimension(100, 25));
        add(newCompanyListPriceField, c);

        currentRow++;
        
        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = currentRow;
        c.anchor = GridBagConstraints.WEST;
        add(ComponentUtil.createLabel(130, "Schedule Date:"), c);

        c = new GridBagConstraints();
        c.gridx = 1;
        c.gridy = currentRow;
        c.anchor = GridBagConstraints.WEST;
        add(ComponentUtil.createGenericPanel(immediateRadio, scheduledRadio), c);

        currentRow++;
        
        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = currentRow;
        c.anchor = GridBagConstraints.WEST;
        add(ComponentUtil.createLabel(130, "Effective Date:"), c);

        c = new GridBagConstraints();
        c.gridx = 1;
        c.gridy = currentRow;
        c.anchor = GridBagConstraints.WEST;
        add(ComponentUtil.createDatePicker(effectiveDateModel), c);

        currentRow++;
        
        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = currentRow;
        c.gridwidth = 3;
        c.insets.top = 10;
        saveButton.setPreferredSize(new Dimension(100, 25));
        add(saveButton, c);
        
        currentRow++;
        
        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = currentRow;
        c.gridwidth = 3;
        c.insets.top = 10;
        c.fill = GridBagConstraints.HORIZONTAL;
        add(new JSeparator(), c);
        
        currentRow++;
        
        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = currentRow;
        c.gridwidth = 3;
        c.insets.top = 10;
        showPriceHistoryButton.setPreferredSize(new Dimension(180, 25));
        add(showPriceHistoryButton, c);
        
		currentRow++;
		
		c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.weighty = 1.0; // bottom space filler
		c.gridx = 0;
		c.gridy = currentRow;
		add(Box.createGlue(), c);
	}
	
	private JPanel createTablePanel() {
		JPanel panel = new JPanel();
		panel.setBorder(BorderFactory.createLineBorder(Color.black));
		panel.setLayout(new GridBagLayout());
		
		GridBagConstraints c = new GridBagConstraints();
		
		c.weightx = 1.0;
		c.fill = GridBagConstraints.HORIZONTAL;
		panel.add(table.getTableHeader(), c);
		
		c.weightx = c.weighty = 1.0;
		c.gridy = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		panel.add(table, c);

		return panel;
	}

	public void updateDisplay(Product product, PricingScheme pricingScheme) {
		this.product = clone(product);
		this.pricingScheme = pricingScheme;
		
		productCodeLabel.setText(product.getCode());
		productDescriptionLabel.setText(product.getDescription());
		pricingSchemeNameLabel.setText(pricingScheme.getName());
		companyListPriceLabel.setText(FormatterUtil.formatAmount(product.getCompanyListPrice()));
		
		table.setProduct(this.product);
		table.highlight();
		
		newCompanyListPriceField.setText(null);
		immediateRadio.setSelected(false);
		scheduledRadio.setSelected(false);
		effectiveDateModel.setValue(null);
	}

	private Product clone(Product product) {
		Product clone = new Product();
		clone.setId(product.getId());
		clone.getUnits().addAll(product.getUnits());
		for (UnitPrice unitPrice : product.getUnitPrices()) {
			clone.getUnitPrices().add(new UnitPrice(unitPrice));
		}
		for (UnitCost unitCost : product.getUnitCosts()) {
			clone.getUnitCosts().add(new UnitCost(unitCost));
		}
		clone.getUnitConversions().addAll(product.getUnitConversions());
		clone.setCompanyListPrice(product.getCompanyListPrice());
		return clone;
	}

	private void schedule() {
	    if (!validateScheduleFields()) {
	        return;
	    }
	    
        product.setCompanyListPrice(newCompanyListPriceField.getTextAsBigDecimal());

        try {
            productService.schedulePriceChange(product, pricingScheme, effectiveDateModel.getValue().getTime());
        } catch (Exception e) {
            LOGGER.error("Error while saving scheduled price change", e);
            showErrorMessage("Unexpected error occurred");
            return;
        }
        
        showMessage("Price change scheduled");
	}

    private boolean validateScheduleFields() {
        if (effectiveDateModel.getValue() == null) {
            showErrorMessage("Effective Date must be specified");
            return false;
        }
        
        if (!effectiveDateModel.getValue().getTime().after(DateUtil.currentDate())) {
            showErrorMessage("Effective Date must be a future date");
            return false;
        }
        
        if (!newCompanyListPriceField.isEmpty() && !NumberUtil.isAmount(newCompanyListPriceField.getText())) {
            showErrorMessage("New Company List Price must be a valid amount");
            newCompanyListPriceField.requestFocusInWindow();
            return false;
        }
        
        return true;
    }
	
    private String createScheduleConfirmMessage() {
        StringBuilder sb = new StringBuilder();
        sb.append("Confirm Schedule Price Change?");
        return sb.toString();
    }
    
}
