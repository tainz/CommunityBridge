package org.ruhlendavis.mc.utility;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Presents a familiar interface to the built-in logging facility while giving
 * us our own level of control over logging levels.
 * 
 * @author Feaelin (Iain E. Davis) <iain@ruhlendavis.org>
 */
public class Log
{
	@SuppressWarnings("NonConstantLogger")
	private static java.util.logging.Logger log;
	private static java.util.logging.Level level;
	
	/**
	 * Constructor for only passing in reference to the logger object.
	 * 
	 * @param log Logger object referencing the Bukkit Server logger.
	 */
	public Log(Logger log)
	{
		Log.log = log;
		level = Level.INFO;
	}

	/**
	 * Constructor to specify both the logger and the initial logging level.
	 * 
	 * @param log Logger object referencing the Bukkit Server logger.
	 * @param level Level type from java.util.logging.Level
	 */
	public Log(Logger log, Level level)
	{
		Log.log = log;
		Log.level = level;
	}

	/**
	 * Set the logging level based on a string.
	 * 
	 * @param level String containing the specified level. One of: all, finest, finer, fine, config, info, warning, severe
	 */
	public void setLevel(String level)
	{
		if (level.equalsIgnoreCase("info"))
		{
			this.setLevel(Level.INFO);
		}
		else if (level.equalsIgnoreCase("config"))
		{
			this.setLevel(Level.CONFIG);
		}
		else if (level.equalsIgnoreCase("fine"))
		{
			this.setLevel(Level.FINE);
		}
		else if (level.equalsIgnoreCase("finer"))
		{
			this.setLevel(Level.FINER);
		}
		else if (level.equalsIgnoreCase("finest"))
		{
			this.setLevel(Level.FINEST);
		}
		else if (level.equalsIgnoreCase("all"))
		{
			this.setLevel(Level.ALL);
		}
		else if (level.equalsIgnoreCase("warning"))
		{
			this.setLevel(Level.WARNING);
		}
		else if (level.equalsIgnoreCase("severe"))
		{
			this.setLevel(Level.SEVERE);
		}
	}

	/**
	 * Sets the logging level using a java.util.logging.Level type
	 * 
	 * @param level Level from java.util.logging.Level
	 */
	public void setLevel(Level level)
	{
		Log.level = level;
	}

	/**
	 * Sends 'message' to the log if the logging level is high enough.
	 * 
	 * @param message String containing message to be sent.
	 */
	public void finest(String message)
	{
		// Finest: 300
		if (level.intValue() <= Level.FINEST.intValue())
		{
			log.info(message);
		}
	}
	
	/**
	 * Sends 'message' to the log if the logging level is high enough.
	 * 
	 * @param message String containing message to be sent.
	 */
	public void finer(String message)
	{
		// Finer: 400
		if (level.intValue() <= Level.FINER.intValue())
		{
			log.info(message);
		}
	}

	/**
	 * Sends 'message' to the log if the logging level is high enough.
	 * 
	 * @param message String containing message to be sent.
	 */
	public void fine(String message)
	{
		// Fine: 500
		if (level.intValue() <= Level.FINE.intValue())
		{
			log.info(message);
		}
	}
	
	/**
	 * Sends 'message' to the log if the logging level is high enough.
	 * 
	 * @param message String containing message to be sent.
	 */
	public void config(String message)
	{
		// Config: 700
		if (level.intValue() <= Level.CONFIG.intValue())
		{
		  log.info(message);
		}
	}
	
	/**
	 * Sends 'message' to the log if the logging level is high enough.
	 * 
	 * @param message String containing message to be sent.
	 */
	public void info(String message)
	{
		// Info: 800
		if (level.intValue() <= Level.INFO.intValue())
		{
			log.info(message);
		}
	}
	
	/**
	 * Sends 'message' to the log if the logging level is high enough.
	 * 
	 * @param message String containing message to be sent.
	 */
	public void warning(String message)
	{
		// Warning: 900
		if (level.intValue() <= Level.WARNING.intValue())
		{
			log.warning(message);
		}
	}
	
	/**
	 * Sends 'message' to the log if the logging level is high enough.
	 * 
	 * @param message String containing message to be sent.
	 */
	public void severe(String message)
	{
		// Severe: 1000
		if (level.intValue() <= Level.SEVERE.intValue())
		{
			log.severe(message);
		}
	}
}
