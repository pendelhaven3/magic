package com.pj.magic.gui.panels;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.math.BigDecimal;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.table.TableColumnModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.gui.component.MagicToolBar;
import com.pj.magic.gui.component.MagicToolBarButton;
import com.pj.magic.gui.tables.MagicListTable;
import com.pj.magic.gui.tables.models.ListBackedTableModel;
import com.pj.magic.model.ScheduledPriceChange;
import com.pj.magic.model.Unit;
import com.pj.magic.service.ProductService;
import com.pj.magic.util.FormatterUtil;

@Component
public class ScheduledPriceChangesListPanel extends StandardMagicPanel {

	private static final Logger LOGGER = LoggerFactory.getLogger(ScheduledPriceChangesListPanel.class);
	
	private static final int EFFECTIVE_DATE_COLUMN_INDEX = 0;
    private static final int PRICING_SCHEME_COLUMN_INDEX = 1;
    private static final int PRODUCT_DESCRIPTION_COLUMN_INDEX = 2;
    private static final int UNIT_PRICE_CASE_COLUMN_INDEX = 3;
    private static final int UNIT_PRICE_TIE_COLUMN_INDEX = 4;
    private static final int UNIT_PRICE_CARTON_COLUMN_INDEX = 5;
    private static final int UNIT_PRICE_DOZEN_COLUMN_INDEX = 6;
    private static final int UNIT_PRICE_PIECES_COLUMN_INDEX = 7;
    private static final int COMPANY_LIST_PRICE_COLUMN_INDEX = 8;
    private static final int APPLIED_COLUMN_INDEX = 9;
    private static final int CHECKBOX_COLUMN_INDEX = 10;
	
	@Autowired
	private ProductService productService;
	
	private MagicListTable table;
	private JButton deleteButton;
	private ScheduledPriceChangesTableModel tableModel = new ScheduledPriceChangesTableModel();
	
	@Override
	protected void initializeComponents() {
        table = new MagicListTable(tableModel);
        
        TableColumnModel columnModel = table.getColumnModel();
        columnModel.getColumn(EFFECTIVE_DATE_COLUMN_INDEX).setPreferredWidth(100);
        columnModel.getColumn(PRICING_SCHEME_COLUMN_INDEX).setPreferredWidth(100);
        columnModel.getColumn(PRODUCT_DESCRIPTION_COLUMN_INDEX).setPreferredWidth(200);
        columnModel.getColumn(UNIT_PRICE_CASE_COLUMN_INDEX).setPreferredWidth(60);
        columnModel.getColumn(UNIT_PRICE_TIE_COLUMN_INDEX).setPreferredWidth(60);
        columnModel.getColumn(UNIT_PRICE_CARTON_COLUMN_INDEX).setPreferredWidth(60);
        columnModel.getColumn(UNIT_PRICE_DOZEN_COLUMN_INDEX).setPreferredWidth(60);
        columnModel.getColumn(UNIT_PRICE_PIECES_COLUMN_INDEX).setPreferredWidth(60);
        columnModel.getColumn(COMPANY_LIST_PRICE_COLUMN_INDEX).setPreferredWidth(100);
        columnModel.getColumn(APPLIED_COLUMN_INDEX).setPreferredWidth(60);
        columnModel.getColumn(CHECKBOX_COLUMN_INDEX).setPreferredWidth(60);
        
		focusOnComponentWhenThisPanelIsDisplayed(table);
	}

    @Override
	protected void doOnBack() {
        tableModel.clear();
		getMagicFrame().switchToInventoryMenuPanel();
	}
	
	public void updateDisplay() {
	    List<ScheduledPriceChange> scheduledPriceChanges = productService.getPresentAndFutureScheduledPriceChanges();
	    tableModel.setItems(scheduledPriceChanges);
	    if (!scheduledPriceChanges.isEmpty()) {
	        table.selectFirstRow();
	    }
	}

	@Override
	protected void registerKeyBindings() {
        onEscapeKey(new AbstractAction() {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                doOnBack();
            }
        });
	}

	@Override
	protected void layoutMainPanel(JPanel mainPanel) {
		mainPanel.setLayout(new GridBagLayout());
		
		int currentRow = 0;
		
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.weightx = c.weighty = 1.0;
		c.gridx = 0;
		c.gridy = currentRow;
		mainPanel.add(new JScrollPane(table), c);
	}

	@Override
	protected void addToolBarButtons(MagicToolBar toolBar) {
		deleteButton = new MagicToolBarButton("cancel", "Delete");
		deleteButton.addActionListener(e -> deleteSelected());
		toolBar.add(deleteButton);
	}

	private void deleteSelected() {
		if (confirm("Delete selected scheduled price change?")) {
			try {
			    for (ScheduledPriceChange scheduledPriceChange : tableModel.getItems()) {
			        if (scheduledPriceChange.isSelected()) {
			            productService.deleteScheduledPriceChange(scheduledPriceChange);
			        }
			    }
				showMessage("Deleted");
				updateDisplay();
			} catch (Exception e) {
				LOGGER.error("Error when deleting scheduled price change", e);
				showMessage("Unexpected error");
			}
		}
	}

	private class ScheduledPriceChangesTableModel extends ListBackedTableModel<ScheduledPriceChange> {

	    private final String[] columnNames = {"Effective Date", "Pricing Scheme", "Product", "CSE", "TIE", "CTN", "DOZ", "PCS", "Company List Price",
	            "Applied", " "};
	    
        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            ScheduledPriceChange scheduledPriceChange = getItem(rowIndex);
            switch (columnIndex) {
            case EFFECTIVE_DATE_COLUMN_INDEX:
                return FormatterUtil.formatDate(scheduledPriceChange.getEffectiveDate());
            case PRICING_SCHEME_COLUMN_INDEX:
                return scheduledPriceChange.getPricingScheme().getName();
            case PRODUCT_DESCRIPTION_COLUMN_INDEX:
                return scheduledPriceChange.getProduct().getDescription();
            case UNIT_PRICE_CASE_COLUMN_INDEX:
                return FormatterUtil.formatAmount(scheduledPriceChange.getUnitPrice(Unit.CASE));
            case UNIT_PRICE_TIE_COLUMN_INDEX:
                return FormatterUtil.formatAmount(scheduledPriceChange.getUnitPrice(Unit.TIE));
            case UNIT_PRICE_CARTON_COLUMN_INDEX:
                return FormatterUtil.formatAmount(scheduledPriceChange.getUnitPrice(Unit.CARTON));
            case UNIT_PRICE_DOZEN_COLUMN_INDEX:
                return FormatterUtil.formatAmount(scheduledPriceChange.getUnitPrice(Unit.DOZEN));
            case UNIT_PRICE_PIECES_COLUMN_INDEX:
                return FormatterUtil.formatAmount(scheduledPriceChange.getUnitPrice(Unit.PIECES));
            case COMPANY_LIST_PRICE_COLUMN_INDEX:
                BigDecimal companyListPrice = scheduledPriceChange.getProduct().getCompanyListPrice();
                return (companyListPrice != null) ? FormatterUtil.formatAmount(companyListPrice) : null;
            case APPLIED_COLUMN_INDEX:
                return scheduledPriceChange.isApplied() ? "Yes" : "No";
            case CHECKBOX_COLUMN_INDEX:
                return scheduledPriceChange.isApplied() ? false : scheduledPriceChange.isSelected();
            default:
                throw new RuntimeException("Should not fetch column index: " + columnIndex);
            }
        }

        @Override
        protected String[] getColumnNames() {
            return columnNames;
        }
	 
        @Override
        public Class<?> getColumnClass(int columnIndex) {
            if (columnIndex == CHECKBOX_COLUMN_INDEX) {
                return Boolean.class;
            } else {
                return super.getColumnClass(columnIndex);
            }
        }
        
        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return columnIndex == CHECKBOX_COLUMN_INDEX;
        }
        
        @Override
        public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
            ScheduledPriceChange scheduledPriceChange = getItem(rowIndex);
            scheduledPriceChange.setSelected(!scheduledPriceChange.isSelected());
        }
        
	}
	
}