package com.pj.magic.gui.tables.models;

import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.table.AbstractTableModel;

import org.springframework.stereotype.Component;

import com.pj.magic.model.User;

@Component
public class UsersTableModel extends AbstractTableModel {

	private static final int USERNAME_COLUMN_INDEX = 0;
	private static final int SUPERVISOR_COLUMN_INDEX = 1;
	private static final String[] columnNames = {"Username", "Supervisor"};
	private static final ImageIcon checkIcon = createImageIcon("/images/small_check.png");
	
	private List<User> users = new ArrayList<>();
	
	@Override
	public int getRowCount() {
		return users.size();
	}

	@Override
	public int getColumnCount() {
		return columnNames.length;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		User user = users.get(rowIndex);
		switch (columnIndex) {
		case USERNAME_COLUMN_INDEX:
			return user.getUsername();
		case SUPERVISOR_COLUMN_INDEX:
			return user.isSupervisor() ? checkIcon : null;
		default:
			throw new RuntimeException("Fetching invalid column index: " + columnIndex);
		}
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		if (columnIndex == SUPERVISOR_COLUMN_INDEX) {
			return ImageIcon.class;
		} else {
			return Object.class;
		}
	}
	
	@Override
	public String getColumnName(int column) {
		return columnNames[column];
	}
	
	public void setUsers(List<User> Users) {
		this.users = Users;
		fireTableDataChanged();
	}
	
	public User getUser(int rowIndex) {
		return users.get(rowIndex);
	}

	private static ImageIcon createImageIcon(String path) {
		java.net.URL imgURL = UsersTableModel.class.getResource(path);
		if (imgURL != null) {
			return new ImageIcon(imgURL);
		} else {
			System.err.println("Couldn't find file: " + path);
			return null;
		}
	}

}
