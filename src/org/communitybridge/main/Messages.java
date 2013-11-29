/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.communitybridge.main;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Iain E. Davis <iain@ruhlendavis.org>
 */
public class Messages
{
		private Map<String, String> messages = new HashMap<String, String>();
		
		public void clear()
		{
			messages.clear();
		}
		
		public String get(String name)
		{
			String message = messages.get(name);
			if (message == null)
			{
				return "";
			}
			else
			{
				return message;
			}
		}
		
		public void put(String name, String message)
		{
			messages.put(name, message);
		}
}
