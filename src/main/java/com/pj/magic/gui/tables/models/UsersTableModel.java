package com.pj.magic.gui.tables.models;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.springframework.stereotype.Component;

import com.pj.magic.model.User;

@Component
public class UsersTableModel extends AbstractTableModel {

	private static final int USERNAME_COLUMN_INDEX = 0;
	private static final String[] columnNames = {"Username"};
	
	private List<User> Users = new ArrayList<>();
	
	@Override
	public int getRowCount() {
		return Users.size();
	}

	@Override
	public int getColumnCount() {
		return columnNames.length;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		User user = Users.get(rowIndex);
		switch (columnIndex) {
		case USERNAME_COLUMN_INDEX:
			return user.getUsername();
		default:
			throw new RuntimeException("Fetching invalid column index: " + columnIndex);
		}
	}

	@Override
	public String getColumnName(int column) {
		return columnNames[column];
	}
	
	public void setUsers(List<User> Users) {
		this.Users = Users;
		fireTableDataChanged();
	}
	
	public User getUser(int rowIndex) {
		return Users.get(rowIndex);
	}
	
}
