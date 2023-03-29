package ua.in.dej.myEmmet.utils;

import org.slf4j.Logger;
import org.slf4j.Marker;

/**
 * The logger proxy for SLF4J {@link Logger}, provided logging methods `log()` with numeric level or default min level.
 */
public class BasicLeveledLogger implements Logger {
	
	// ===== ===== ===== ===== [Static Inner Class] ===== ===== ===== ===== //
	
	/** Log levels */
	public interface LogLevels {
		
		int TRACE = 5;
		
		int DEBUG = 4;
		
		int INFO = 3;
		
		int WARN = 2;
		
		int ERROR = 1;
		
		int CLOSE = 0;
		
	}
	
	// ===== ===== ===== ===== [Instant Variables] ===== ===== ===== ===== //
	
	/** The proxy target logger instance */
	private final Logger targetLogger;
	
	/** The basic level to print log when invoke {@link #log(String)} methods without specifying log level */
	private final int basicLevel;
	
	// ===== ===== ===== ===== [Constructor] ===== ===== ===== ===== //
	
	public BasicLeveledLogger(Logger targetLogger, int basicLevel) {
		this.targetLogger = targetLogger;
		this.basicLevel = basicLevel;
	}
	
	public BasicLeveledLogger(Logger targetLogger) {
		this.targetLogger = targetLogger;
		this.basicLevel = LogLevels.DEBUG;
	}
	
	// ===== ===== ===== ===== [Operation Methods] ===== ===== ===== ===== //
	
	public boolean isLogEnabled(int logLevel) {
		switch (logLevel) {
			case LogLevels.TRACE: return this.isTraceEnabled();
			case LogLevels.DEBUG: return this.isDebugEnabled();
			case LogLevels.INFO: return this.isInfoEnabled();
			case LogLevels.WARN: return this.isWarnEnabled();
			case LogLevels.ERROR: return this.isErrorEnabled();
			case LogLevels.CLOSE: return false;
			default: throw new RuntimeException("Not supported log level: " + logLevel);
		}
		
	}
	
	public void log(int logLevel, String s) {
		switch (logLevel) {
			case LogLevels.TRACE: this.trace(s); return;
			case LogLevels.DEBUG: this.debug(s); return;
			case LogLevels.INFO: this.info(s); return;
			case LogLevels.WARN: this.warn(s); return;
			case LogLevels.ERROR: this.error(s); return;
			case LogLevels.CLOSE: return;
			default: throw new RuntimeException("Not supported log level: " + logLevel);
		}
		
	}
	
	public void log(int logLevel, String s, Object o) {
		switch (logLevel) {
			case LogLevels.TRACE: this.trace(s, o); return;
			case LogLevels.DEBUG: this.debug(s, o); return;
			case LogLevels.INFO: this.info(s, o); return;
			case LogLevels.WARN: this.warn(s, o); return;
			case LogLevels.ERROR: this.error(s, o); return;
			case LogLevels.CLOSE: return;
			default: throw new RuntimeException("Not supported log level: " + logLevel);
		}
		
	}
	
	public void log(int logLevel, String s, Object o, Object o1) {
		switch (logLevel) {
			case LogLevels.TRACE: this.trace(s, o, o1); return;
			case LogLevels.DEBUG: this.debug(s, o, o1); return;
			case LogLevels.INFO: this.info(s, o, o1); return;
			case LogLevels.WARN: this.warn(s, o, o1); return;
			case LogLevels.ERROR: this.error(s, o, o1); return;
			case LogLevels.CLOSE: return;
			default: throw new RuntimeException("Not supported log level: " + logLevel);
		}
		
	}
	
	public void log(int logLevel, String s, Object... objects) {
		switch (logLevel) {
			case LogLevels.TRACE: this.trace(s, objects); return;
			case LogLevels.DEBUG: this.debug(s, objects); return;
			case LogLevels.INFO: this.info(s, objects); return;
			case LogLevels.WARN: this.warn(s, objects); return;
			case LogLevels.ERROR: this.error(s, objects); return;
			case LogLevels.CLOSE: return;
			default: throw new RuntimeException("Not supported log level: " + logLevel);
		}
		
	}
	
	public void log(int logLevel, String s, Throwable throwable) {
		switch (logLevel) {
			case LogLevels.TRACE: this.trace(s, throwable); return;
			case LogLevels.DEBUG: this.debug(s, throwable); return;
			case LogLevels.INFO: this.info(s, throwable); return;
			case LogLevels.WARN: this.warn(s, throwable); return;
			case LogLevels.ERROR: this.error(s, throwable); return;
			case LogLevels.CLOSE: return;
			default: throw new RuntimeException("Not supported log level: " + logLevel);
		}
		
	}
	
	public boolean isLogEnabled(int logLevel, Marker marker) {
		switch (logLevel) {
			case LogLevels.TRACE: return this.isTraceEnabled(marker);
			case LogLevels.DEBUG: return this.isDebugEnabled(marker);
			case LogLevels.INFO: return this.isInfoEnabled(marker);
			case LogLevels.WARN: return this.isWarnEnabled(marker);
			case LogLevels.ERROR: return this.isErrorEnabled(marker);
			case LogLevels.CLOSE: return false;
			default: throw new RuntimeException("Not supported log level: " + logLevel);
		}
		
	}
	
	public boolean isLogEnabled() {
		return this.isLogEnabled(this.basicLevel);
		
	}
	
	public void log(String s) {
		this.log(this.basicLevel, s);
		
	}
	
	public void log(String s, Object o) {
		this.log(this.basicLevel, s, o);
		
	}
	
	public void log(String s, Object o, Object o1) {
		this.log(this.basicLevel, s, o, o1);
		
	}
	
	public void log(String s, Object... objects) {
		this.log(this.basicLevel, s, objects);
		
	}
	
	public void log(String s, Throwable throwable) {
		this.log(this.basicLevel, s, throwable);
		
	}
	
	public boolean isLogEnabled(Marker marker) {
		return this.isLogEnabled(this.basicLevel, marker);
		
	}
	
	// ===== ===== ===== ===== [Override Methods] ===== ===== ===== ===== //
	
	@Override
	public String getName() {
		return this.targetLogger.getName();
		
	}
	
	@Override
	public boolean isTraceEnabled() {
		return this.targetLogger.isTraceEnabled();
		
	}
	
	@Override
	public void trace(String s) {
		this.targetLogger.trace(s);
		
	}
	
	@Override
	public void trace(String s, Object o) {
		this.targetLogger.trace(s, o);
		
	}
	
	@Override
	public void trace(String s, Object o, Object o1) {
		this.targetLogger.trace(s, o, o1);
		
	}
	
	@Override
	public void trace(String s, Object... objects) {
		this.targetLogger.trace(s, objects);
		
	}
	
	@Override
	public void trace(String s, Throwable throwable) {
		this.targetLogger.trace(s, throwable);
		
	}
	
	@Override
	public boolean isTraceEnabled(Marker marker) {
		return this.targetLogger.isTraceEnabled(marker);
		
	}
	
	@Override
	public void trace(Marker marker, String s) {
		this.targetLogger.trace(marker, s);
		
	}
	
	@Override
	public void trace(Marker marker, String s, Object o) {
		this.targetLogger.trace(marker, s, o);
		
	}
	
	@Override
	public void trace(Marker marker, String s, Object o, Object o1) {
		this.targetLogger.trace(marker, s, o, o1);
		
	}
	
	@Override
	public void trace(Marker marker, String s, Object... objects) {
		this.targetLogger.trace(marker, s, objects);
		
	}
	
	@Override
	public void trace(Marker marker, String s, Throwable throwable) {
		this.targetLogger.trace(marker, s, throwable);
		
	}
	
	@Override
	public boolean isDebugEnabled() {
		return this.targetLogger.isDebugEnabled();
		
	}
	
	@Override
	public void debug(String s) {
		this.targetLogger.debug(s);
		
	}
	
	@Override
	public void debug(String s, Object o) {
		this.targetLogger.debug(s, o);
		
	}
	
	@Override
	public void debug(String s, Object o, Object o1) {
		this.targetLogger.debug(s, o, o1);
		
	}
	
	@Override
	public void debug(String s, Object... objects) {
		this.targetLogger.debug(s, objects);
		
	}
	
	@Override
	public void debug(String s, Throwable throwable) {
		this.targetLogger.debug(s, throwable);
		
	}
	
	@Override
	public boolean isDebugEnabled(Marker marker) {
		return this.targetLogger.isDebugEnabled(marker);
		
	}
	
	@Override
	public void debug(Marker marker, String s) {
		this.targetLogger.debug(marker, s);
		
	}
	
	@Override
	public void debug(Marker marker, String s, Object o) {
		this.targetLogger.debug(marker, s, o);
		
	}
	
	@Override
	public void debug(Marker marker, String s, Object o, Object o1) {
		this.targetLogger.debug(marker, s, o, o1);
		
	}
	
	@Override
	public void debug(Marker marker, String s, Object... objects) {
		this.targetLogger.debug(marker, s, objects);
		
	}
	
	@Override
	public void debug(Marker marker, String s, Throwable throwable) {
		this.targetLogger.debug(marker, s, throwable);
		
	}
	
	@Override
	public boolean isInfoEnabled() {
		return this.targetLogger.isInfoEnabled();
		
	}
	
	@Override
	public void info(String s) {
		this.targetLogger.info(s);
		
	}
	
	@Override
	public void info(String s, Object o) {
		this.targetLogger.info(s, o);
		
	}
	
	@Override
	public void info(String s, Object o, Object o1) {
		this.targetLogger.info(s, o, o1);
		
	}
	
	@Override
	public void info(String s, Object... objects) {
		this.targetLogger.info(s, objects);
		
	}
	
	@Override
	public void info(String s, Throwable throwable) {
		this.targetLogger.info(s, throwable);
		
	}
	
	@Override
	public boolean isInfoEnabled(Marker marker) {
		return this.targetLogger.isInfoEnabled(marker);
		
	}
	
	@Override
	public void info(Marker marker, String s) {
		this.targetLogger.info(marker, s);
		
	}
	
	@Override
	public void info(Marker marker, String s, Object o) {
		this.targetLogger.info(marker, s, o);
		
	}
	
	@Override
	public void info(Marker marker, String s, Object o, Object o1) {
		this.targetLogger.info(marker, s, o, o1);
		
	}
	
	@Override
	public void info(Marker marker, String s, Object... objects) {
		this.targetLogger.info(marker, s, objects);
		
	}
	
	@Override
	public void info(Marker marker, String s, Throwable throwable) {
		this.targetLogger.info(marker, s, throwable);
		
	}
	
	@Override
	public boolean isWarnEnabled() {
		return this.targetLogger.isWarnEnabled();
		
	}
	
	@Override
	public void warn(String s) {
		this.targetLogger.warn(s);
		
	}
	
	@Override
	public void warn(String s, Object o) {
		this.targetLogger.warn(s, o);
		
	}
	
	@Override
	public void warn(String s, Object... objects) {
		this.targetLogger.warn(s, objects);
		
	}
	
	@Override
	public void warn(String s, Object o, Object o1) {
		this.targetLogger.warn(s, o, o1);
		
	}
	
	@Override
	public void warn(String s, Throwable throwable) {
		this.targetLogger.warn(s, throwable);
		
	}
	
	@Override
	public boolean isWarnEnabled(Marker marker) {
		return this.targetLogger.isWarnEnabled(marker);
		
	}
	
	@Override
	public void warn(Marker marker, String s) {
		this.targetLogger.warn(marker, s);
		
	}
	
	@Override
	public void warn(Marker marker, String s, Object o) {
		this.targetLogger.warn(marker, s, o);
		
	}
	
	@Override
	public void warn(Marker marker, String s, Object o, Object o1) {
		this.targetLogger.warn(marker, s, o, o1);
		
	}
	
	@Override
	public void warn(Marker marker, String s, Object... objects) {
		this.targetLogger.warn(marker, s, objects);
		
	}
	
	@Override
	public void warn(Marker marker, String s, Throwable throwable) {
		this.targetLogger.warn(marker, s, throwable);
		
	}
	
	@Override
	public boolean isErrorEnabled() {
		return this.targetLogger.isErrorEnabled();
		
	}
	
	@Override
	public void error(String s) {
		this.targetLogger.error(s);
		
	}
	
	@Override
	public void error(String s, Object o) {
		this.targetLogger.error(s, o);
		
	}
	
	@Override
	public void error(String s, Object o, Object o1) {
		this.targetLogger.error(s, o, o1);
		
	}
	
	@Override
	public void error(String s, Object... objects) {
		this.targetLogger.error(s, objects);
		
	}
	
	@Override
	public void error(String s, Throwable throwable) {
		this.targetLogger.error(s, throwable);
		
	}
	
	@Override
	public boolean isErrorEnabled(Marker marker) {
		return this.targetLogger.isErrorEnabled(marker);
		
	}
	
	@Override
	public void error(Marker marker, String s) {
		this.targetLogger.error(marker, s);
		
	}
	
	@Override
	public void error(Marker marker, String s, Object o) {
		this.targetLogger.error(marker, s, o);
		
	}
	
	@Override
	public void error(Marker marker, String s, Object o, Object o1) {
		this.targetLogger.error(marker, s, o, o1);
		
	}
	
	@Override
	public void error(Marker marker, String s, Object... objects) {
		this.targetLogger.error(marker, s, objects);
		
	}
	
	@Override
	public void error(Marker marker, String s, Throwable throwable) {
		this.targetLogger.error(marker, s, throwable);
		
	}
	
}
