package com.pj.magic;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.beans.factory.InitializingBean;

// TODO: Create post about this
// TODO: Check again if relevant since context is now not closed

/*
 * This class is part of a workaround to a strange behavior observed
 * where in a Swing application, on trigger of new transaction,
 * the DataSourceTransactionManager is being recreated and assigned a new dataSource 
 * that is different from the one previously assigned to JdbcTemplate.
 * 
 * Ensures that multiple instances of this will still point to only one dataSource.
 * Must work with MagicDataSourceTransactionManager
 */
public class MagicDataSource implements DataSource, InitializingBean {

	private static BasicDataSource dataSource;
	
	private String driverClassName;
	private String url;
	private String username;
	private String password;
	private boolean defaultAutoCommit;

	@Override
	public void afterPropertiesSet() throws Exception {
		if (dataSource == null) {
			dataSource = new BasicDataSource();
			dataSource.setDriverClassName(driverClassName);
			dataSource.setUrl(url);
			dataSource.setUsername(username);
			dataSource.setPassword(password);
			dataSource.setDefaultAutoCommit(defaultAutoCommit);
		}
	}
	
	@Override
	public Connection getConnection() throws SQLException {
		return dataSource.getConnection();
	}

	public DataSource getActualDataSource() {
		return dataSource;
	}
	
	public String getDriverClassName() {
		return driverClassName;
	}

	public void setDriverClassName(String driverClassName) {
		this.driverClassName = driverClassName;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public boolean isDefaultAutoCommit() {
		return defaultAutoCommit;
	}

	public void setDefaultAutoCommit(boolean defaultAutoCommit) {
		this.defaultAutoCommit = defaultAutoCommit;
	}
	
	public int hashCode() {
		return dataSource.hashCode();
	}

	public boolean equals(Object obj) {
		return dataSource.equals(obj);
	}

	public Boolean getDefaultAutoCommit() {
		return dataSource.getDefaultAutoCommit();
	}

	public void setDefaultAutoCommit(Boolean defaultAutoCommit) {
		dataSource.setDefaultAutoCommit(defaultAutoCommit);
	}

	public Boolean getDefaultReadOnly() {
		return dataSource.getDefaultReadOnly();
	}

	public void setDefaultReadOnly(Boolean defaultReadOnly) {
		dataSource.setDefaultReadOnly(defaultReadOnly);
	}

	public int getDefaultTransactionIsolation() {
		return dataSource.getDefaultTransactionIsolation();
	}

	public void setDefaultTransactionIsolation(int defaultTransactionIsolation) {
		dataSource.setDefaultTransactionIsolation(defaultTransactionIsolation);
	}

	public Integer getDefaultQueryTimeout() {
		return dataSource.getDefaultQueryTimeout();
	}

	public void setDefaultQueryTimeout(Integer defaultQueryTimeout) {
		dataSource.setDefaultQueryTimeout(defaultQueryTimeout);
	}

	public String getDefaultCatalog() {
		return dataSource.getDefaultCatalog();
	}

	public void setDefaultCatalog(String defaultCatalog) {
		dataSource.setDefaultCatalog(defaultCatalog);
	}

	public String toString() {
		return dataSource.toString();
	}

	public boolean getCacheState() {
		return dataSource.getCacheState();
	}

	public void setCacheState(boolean cacheState) {
		dataSource.setCacheState(cacheState);
	}

	public Driver getDriver() {
		return dataSource.getDriver();
	}

	public void setDriver(Driver driver) {
		dataSource.setDriver(driver);
	}

	public ClassLoader getDriverClassLoader() {
		return dataSource.getDriverClassLoader();
	}

	public void setDriverClassLoader(ClassLoader driverClassLoader) {
		dataSource.setDriverClassLoader(driverClassLoader);
	}

	public boolean getLifo() {
		return dataSource.getLifo();
	}

	public void setLifo(boolean lifo) {
		dataSource.setLifo(lifo);
	}

	public int getMaxTotal() {
		return dataSource.getMaxTotal();
	}

	public void setMaxTotal(int maxTotal) {
		dataSource.setMaxTotal(maxTotal);
	}

	public int getMaxIdle() {
		return dataSource.getMaxIdle();
	}

	public void setMaxIdle(int maxIdle) {
		dataSource.setMaxIdle(maxIdle);
	}

	public int getMinIdle() {
		return dataSource.getMinIdle();
	}

	public void setMinIdle(int minIdle) {
		dataSource.setMinIdle(minIdle);
	}

	public int getInitialSize() {
		return dataSource.getInitialSize();
	}

	public void setInitialSize(int initialSize) {
		dataSource.setInitialSize(initialSize);
	}

	public long getMaxWaitMillis() {
		return dataSource.getMaxWaitMillis();
	}

	public void setMaxWaitMillis(long maxWaitMillis) {
		dataSource.setMaxWaitMillis(maxWaitMillis);
	}

	public boolean isPoolPreparedStatements() {
		return dataSource.isPoolPreparedStatements();
	}

	public void setPoolPreparedStatements(boolean poolingStatements) {
		dataSource.setPoolPreparedStatements(poolingStatements);
	}

	public int getMaxOpenPreparedStatements() {
		return dataSource.getMaxOpenPreparedStatements();
	}

	public void setMaxOpenPreparedStatements(int maxOpenStatements) {
		dataSource.setMaxOpenPreparedStatements(maxOpenStatements);
	}

	public boolean getTestOnCreate() {
		return dataSource.getTestOnCreate();
	}

	public void setTestOnCreate(boolean testOnCreate) {
		dataSource.setTestOnCreate(testOnCreate);
	}

	public boolean getTestOnBorrow() {
		return dataSource.getTestOnBorrow();
	}

	public void setTestOnBorrow(boolean testOnBorrow) {
		dataSource.setTestOnBorrow(testOnBorrow);
	}

	public boolean getTestOnReturn() {
		return dataSource.getTestOnReturn();
	}

	public void setTestOnReturn(boolean testOnReturn) {
		dataSource.setTestOnReturn(testOnReturn);
	}

	public long getTimeBetweenEvictionRunsMillis() {
		return dataSource.getTimeBetweenEvictionRunsMillis();
	}

	public void setTimeBetweenEvictionRunsMillis(
			long timeBetweenEvictionRunsMillis) {
		dataSource
				.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);
	}

	public int getNumTestsPerEvictionRun() {
		return dataSource.getNumTestsPerEvictionRun();
	}

	public void setNumTestsPerEvictionRun(int numTestsPerEvictionRun) {
		dataSource.setNumTestsPerEvictionRun(numTestsPerEvictionRun);
	}

	public long getMinEvictableIdleTimeMillis() {
		return dataSource.getMinEvictableIdleTimeMillis();
	}

	public void setMinEvictableIdleTimeMillis(long minEvictableIdleTimeMillis) {
		dataSource.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);
	}

	public void setSoftMinEvictableIdleTimeMillis(
			long softMinEvictableIdleTimeMillis) {
		dataSource
				.setSoftMinEvictableIdleTimeMillis(softMinEvictableIdleTimeMillis);
	}

	public long getSoftMinEvictableIdleTimeMillis() {
		return dataSource.getSoftMinEvictableIdleTimeMillis();
	}

	public String getEvictionPolicyClassName() {
		return dataSource.getEvictionPolicyClassName();
	}

	public void setEvictionPolicyClassName(String evictionPolicyClassName) {
		dataSource.setEvictionPolicyClassName(evictionPolicyClassName);
	}

	public boolean getTestWhileIdle() {
		return dataSource.getTestWhileIdle();
	}

	public void setTestWhileIdle(boolean testWhileIdle) {
		dataSource.setTestWhileIdle(testWhileIdle);
	}

	public int getNumActive() {
		return dataSource.getNumActive();
	}

	public int getNumIdle() {
		return dataSource.getNumIdle();
	}

	public String getValidationQuery() {
		return dataSource.getValidationQuery();
	}

	public void setValidationQuery(String validationQuery) {
		dataSource.setValidationQuery(validationQuery);
	}

	public int getValidationQueryTimeout() {
		return dataSource.getValidationQueryTimeout();
	}

	public void setValidationQueryTimeout(int timeout) {
		dataSource.setValidationQueryTimeout(timeout);
	}

	public List<String> getConnectionInitSqls() {
		return dataSource.getConnectionInitSqls();
	}

	public String[] getConnectionInitSqlsAsArray() {
		return dataSource.getConnectionInitSqlsAsArray();
	}

	public void setConnectionInitSqls(Collection<String> connectionInitSqls) {
		dataSource.setConnectionInitSqls(connectionInitSqls);
	}

	public boolean isAccessToUnderlyingConnectionAllowed() {
		return dataSource.isAccessToUnderlyingConnectionAllowed();
	}

	public void setAccessToUnderlyingConnectionAllowed(boolean allow) {
		dataSource.setAccessToUnderlyingConnectionAllowed(allow);
	}

	public long getMaxConnLifetimeMillis() {
		return dataSource.getMaxConnLifetimeMillis();
	}

	public void setMaxConnLifetimeMillis(long maxConnLifetimeMillis) {
		dataSource.setMaxConnLifetimeMillis(maxConnLifetimeMillis);
	}

	public String getJmxName() {
		return dataSource.getJmxName();
	}

	public void setJmxName(String jmxName) {
		dataSource.setJmxName(jmxName);
	}

	public boolean getEnableAutoCommitOnReturn() {
		return dataSource.getEnableAutoCommitOnReturn();
	}

	public void setEnableAutoCommitOnReturn(boolean enableAutoCommitOnReturn) {
		dataSource.setEnableAutoCommitOnReturn(enableAutoCommitOnReturn);
	}

	public boolean getRollbackOnReturn() {
		return dataSource.getRollbackOnReturn();
	}

	public void setRollbackOnReturn(boolean rollbackOnReturn) {
		dataSource.setRollbackOnReturn(rollbackOnReturn);
	}

	public Connection getConnection(String user, String pass)
			throws SQLException {
		return dataSource.getConnection(user, pass);
	}

	public int getLoginTimeout() throws SQLException {
		return dataSource.getLoginTimeout();
	}

	public PrintWriter getLogWriter() throws SQLException {
		return dataSource.getLogWriter();
	}

	public void setLoginTimeout(int loginTimeout) throws SQLException {
		dataSource.setLoginTimeout(loginTimeout);
	}

	public void setLogWriter(PrintWriter logWriter) throws SQLException {
		dataSource.setLogWriter(logWriter);
	}

	public boolean getRemoveAbandonedOnBorrow() {
		return dataSource.getRemoveAbandonedOnBorrow();
	}

	public void setRemoveAbandonedOnMaintenance(
			boolean removeAbandonedOnMaintenance) {
		dataSource
				.setRemoveAbandonedOnMaintenance(removeAbandonedOnMaintenance);
	}

	public boolean getRemoveAbandonedOnMaintenance() {
		return dataSource.getRemoveAbandonedOnMaintenance();
	}

	public void setRemoveAbandonedOnBorrow(boolean removeAbandonedOnBorrow) {
		dataSource.setRemoveAbandonedOnBorrow(removeAbandonedOnBorrow);
	}

	public int getRemoveAbandonedTimeout() {
		return dataSource.getRemoveAbandonedTimeout();
	}

	public void setRemoveAbandonedTimeout(int removeAbandonedTimeout) {
		dataSource.setRemoveAbandonedTimeout(removeAbandonedTimeout);
	}

	public boolean getLogAbandoned() {
		return dataSource.getLogAbandoned();
	}

	public void setLogAbandoned(boolean logAbandoned) {
		dataSource.setLogAbandoned(logAbandoned);
	}

	public PrintWriter getAbandonedLogWriter() {
		return dataSource.getAbandonedLogWriter();
	}

	public void setAbandonedLogWriter(PrintWriter logWriter) {
		dataSource.setAbandonedLogWriter(logWriter);
	}

	public boolean getAbandonedUsageTracking() {
		return dataSource.getAbandonedUsageTracking();
	}

	public void setAbandonedUsageTracking(boolean usageTracking) {
		dataSource.setAbandonedUsageTracking(usageTracking);
	}

	public void addConnectionProperty(String name, String value) {
		dataSource.addConnectionProperty(name, value);
	}

	public void removeConnectionProperty(String name) {
		dataSource.removeConnectionProperty(name);
	}

	public void setConnectionProperties(String connectionProperties) {
		dataSource.setConnectionProperties(connectionProperties);
	}

	public void close() throws SQLException {
		dataSource.close();
	}

	public boolean isClosed() {
		return dataSource.isClosed();
	}

	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		return dataSource.isWrapperFor(iface);
	}

	public <T> T unwrap(Class<T> iface) throws SQLException {
		return dataSource.unwrap(iface);
	}

	public Logger getParentLogger() throws SQLFeatureNotSupportedException {
		return dataSource.getParentLogger();
	}

	public ObjectName preRegister(MBeanServer server, ObjectName name) {
		return dataSource.preRegister(server, name);
	}

	public void postRegister(Boolean registrationDone) {
		dataSource.postRegister(registrationDone);
	}

	public void preDeregister() throws Exception {
		dataSource.preDeregister();
	}

	public void postDeregister() {
		dataSource.postDeregister();
	}

}
