package com.pj.magic.gui.panels.menu;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.gui.component.DoubleClickMouseAdapter;
import com.pj.magic.gui.component.MagicToolBar;
import com.pj.magic.gui.tables.MagicListTable;
import com.pj.magic.gui.tables.MagicSubmenuTable;
import com.pj.magic.model.User;
import com.pj.magic.service.LoginService;

@Component
public class AlfonsoRaffleMenuPanel extends MenuPanel {

	private static final String TICKETS = "Alfonso Raffle Tickets";
	private static final String TICKET_CLAIMS = "Alfonso Raffle Ticket Claims";
	private static final String PARTICIPATING_ITEMS = "Alfonso Raffle Participating Items";
	
	@Autowired private LoginService loginService;
	
	private MagicListTable table;
	private MenuTableModel tableModel;
	
	@Override
	protected void initializeComponents() {
		tableModel = new MenuTableModel();
		table = new MagicSubmenuTable(tableModel);
		
		DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
		renderer.setHorizontalAlignment(JLabel.CENTER);
		table.setDefaultRenderer(Object.class, renderer);

		table.changeSelection(0, 0, false, false);
		focusOnComponentWhenThisPanelIsDisplayed(table);
	}

	@Override
	protected void layoutMainPanel(JPanel mainPanel) {
		mainPanel.setLayout(new GridBagLayout());
		
		GridBagConstraints c = new GridBagConstraints();
		
		c.fill = GridBagConstraints.BOTH;
		c.weightx = c.weighty = 1.0;
		c.gridx = 0;
		c.gridy = 0;
		
		table.setTableHeader(null);
		table.setShowGrid(false);
		
		mainPanel.add(new JScrollPane(table), c);
	}

	@Override
	protected void registerKeyBindings() {
		table.onEnterKey(new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				selectMenuItem();
			}
		});
		
		table.addMouseListener(new DoubleClickMouseAdapter() {
			
			@Override
			protected void onDoubleClick() {
				selectMenuItem();
			}
		});
	}

	public void updateDisplay() {
		tableModel.setUser(loginService.getLoggedInUser());
		table.changeSelection(0, 0, false, false);
	}
	
	private void selectMenuItem() {
		switch ((String)table.getValueAt(table.getSelectedRow(), 0)) {
		case TICKETS:
			getMagicFrame().switchToAlfonsoRaffleTicketsListPanel();
			break;
		case TICKET_CLAIMS:
			getMagicFrame().switchToAlfonsoRaffleTicketClaimsListPanel();
			break;
		case PARTICIPATING_ITEMS:
			getMagicFrame().switchToAlfonsoRaffleParticipatingItemsPanel();
			break;
		}
	}

	@Override
	protected void doOnBack() {
		getMagicFrame().switchToPromoMenuPanel();
	}

	private class MenuTableModel extends AbstractTableModel {

        private final List<String> allMenuItems = Arrays.asList(
				TICKETS,
				TICKET_CLAIMS,
				PARTICIPATING_ITEMS
        );
        
		private final List<String> menuItems = new ArrayList<>();
		
		@Override
		public int getRowCount() {
			return menuItems.size();
		}

		@Override
		public int getColumnCount() {
			return 1;
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			return menuItems.get(rowIndex);
		}
		
        public void setUser(User user) {
            menuItems.clear();
            menuItems.addAll(allMenuItems);
            if (!user.isSupervisor()) {
                menuItems.remove(PARTICIPATING_ITEMS);
            }
            fireTableDataChanged();
        }
        
	}

	@Override
	protected void addToolBarButtons(MagicToolBar toolBar) {
		// none
	}

}