/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ruhlendavis.mc.utility;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Presents a familiar interface to the built-in logging facility while giving
 * us our own level of control over logging levels.
 * 
 * @author Feaelin
 */
public class Log
{
	@SuppressWarnings("NonConstantLogger")
	private static java.util.logging.Logger log;
	private static java.util.logging.Level level;
	
	public Log(Logger log)
	{
		Log.log = log;
		level = Level.INFO;
	}

	public Log(Logger log, Level level)
	{
		Log.log = log;
		Log.level = level;
	}

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
	
	public void setLevel(Level level)
	{
		Log.level = level;
	}

	public void finest(String message)
	{
		// Finest: 300
		if (level.intValue() <= Level.FINEST.intValue())
		{
			log.info(message);
		}
	}
	
	public void finer(String message)
	{
		// Finer: 400
		if (level.intValue() <= Level.FINER.intValue())
		{
			log.info(message);
		}
	}

	public void fine(String message)
	{
		// Fine: 500
		if (level.intValue() <= Level.FINE.intValue())
		{
			log.info(message);
		}
	}
	
	public void config(String message)
	{
		// Config: 700
		if (level.intValue() <= Level.CONFIG.intValue())
		{
		  log.info(message);
		}
	}
	
	public void info(String message)
	{
		// Info: 800
		if (level.intValue() <= Level.INFO.intValue())
		{
			log.info(message);
		}
	}
	
	public void warning(String message)
	{
		// Warning: 900
		if (level.intValue() <= Level.WARNING.intValue())
		{
			log.warning(message);
		}
	}
	
	public void severe(String message)
	{
		// Severe: 1000
		if (level.intValue() <= Level.SEVERE.intValue())
		{
			log.severe(message);
		}
	}
}
