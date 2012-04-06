/**
 *
 *    Placeholder for TinyOS Timer for RBO Protocol simulation in Java.
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


import java.util.Timer;
import java.util.TimerTask;


public class TinyTimer{


    TinyTaskQueue tinyTaskQueue; // system tasks queue
    TinyTimerEvents events;
    Timer timer;

    // commands
    public void startOneShot(int millis)
    {
	if(timer!=null) timer.cancel();

	TimerTask timerTask=new TimerTask(){
		public void run()
		{
		    postFiredTask();
		}
	    };
	
	timer=new Timer();
	timer.schedule(timerTask, millis);
    }


    public void stop()
    {
	if(timer!=null) timer.cancel();
	timer=null;
    }





    public void postFiredTask()
    {
	TinyTask task= new TinyTask() {
		public void run(){
		    events.fired();
		}
	    };
	// post to the queue
	tinyTaskQueue.enqueue( task );

    }


    // constructor (for wiring of events)

    public TinyTimer(TinyTimerEvents implementedEvents)
    {
	events=implementedEvents;
    }


}