package ua.in.dej.myEmmet.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The logger proxy factory for SLF4J {@link Logger}, return proxy instance {@link BasicLeveledLogger}
 */
public class BasicLeveledLoggerFactory {
	
	// ===== ===== ===== ===== [Static Factory Method] ===== ===== ===== ===== //
	
	public static BasicLeveledLogger getLogger(Class<?> clazz) {
		return new BasicLeveledLogger(LoggerFactory.getLogger(clazz));
		
	}
	
	public static BasicLeveledLogger getLogger(String name) {
		return new BasicLeveledLogger(LoggerFactory.getLogger(name));
		
	}
	
	public static BasicLeveledLogger getLogger(Class<?> clazz, int minLevel) {
		return new BasicLeveledLogger(LoggerFactory.getLogger(clazz), minLevel);
		
	}
	
	public static BasicLeveledLogger getLogger(String name, int minLevel) {
		return new BasicLeveledLogger(LoggerFactory.getLogger(name), minLevel);
		
	}
	
}
