/**
 *
 *    Placeholder for TinyOS Scheduler for RBO Protocol simulation in Java.
 *    
 *    Copyright (C) 2009  Marcin Kik
 *
 *    This program is free software; you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation; either version 2 of the License, or
 *    (at your option) any later version.
 *
 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with this program; if not, write to the Free Software
 *    Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 *
 *
 *    The author can be reached at Marcin.Kik@pwr.wroc.pl
 *
 */

public class TinyScheduler{

    TinyTaskQueue tinyTaskQueue;

    volatile boolean stopped;

    public void stop()
    {
	stopped=true;
    }


    public synchronized void restart()
    {
	stopped=false;
	notify();
    }

    public synchronized  void taskLoop()
    {
	while(true)
	    {
		try
		    {
			while(stopped) wait();
		    }
		catch(Exception e)
		    {
			System.out.println(e);
			System.exit(-1);
		    }

		TinyTask task= tinyTaskQueue.dequeue();
		task.run();
	    }
    }
}