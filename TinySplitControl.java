/**
 *
 *    Placeholder for TinyOS SplitControl for RBO Protocol simulation in Java.
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

public class TinySplitControl{

    static final int SWITCHING_ON_TIME=2; // milliseconds
    static final int SWITCHING_OFF_TIME=2; // milliseconds

    // error constants

    public static final byte SUCCESS=0;
    public static final byte EALREADY=1;
    public static final byte EBUSY=2;
    public static final byte FAIL=3;


    // state constants

    static final byte OFF=0;
    static final byte SWITCHING_ON=1;
    static final byte ON=2;
    static final byte SWITCHING_OFF=3;

    // variables for statistics
    long lastRealStart;
    long totalTimeOn=0;



    TinyTaskQueue tinyTaskQueue;
    TinyRadio radio; // only radio will be switched on/off
    volatile byte state=OFF;

    // commands

    public byte start()
    {
	switch(state){
	case ON: 
	    return EALREADY;
	case SWITCHING_ON: 
	    return SUCCESS;
	case SWITCHING_OFF: 
	    return EBUSY; 
	case OFF: 
	    realStart(); return SUCCESS;
	}
	return FAIL;
    }

    void realStart()
    {
	lastRealStart=System.currentTimeMillis(); // for statistics
	state=SWITCHING_ON;
	Timer timer=new Timer();
	TimerTask task=new TimerTask(){
		public void run(){
		    radio.setSwitchedOn(true);
		    state=ON;
		    postStartDone(SUCCESS);
		}
	    };
	timer.schedule(task, SWITCHING_ON_TIME);

    }

    public byte stop()
    {
	switch(state){
	case OFF: 
	    return EALREADY;
	case SWITCHING_OFF: 
	    return SUCCESS;
	case SWITCHING_ON: 
	    return EBUSY; 
	case ON: 
	    realStop(); return SUCCESS;
	}
	return FAIL;
   }

    void realStop()
    {
	state=SWITCHING_OFF;
	Timer timer=new Timer();
	TimerTask task=new TimerTask(){
		public void run(){
		    radio.setSwitchedOn(false);
		    state=OFF;
		    postStopDone(SUCCESS);
		}
	    };
	timer.schedule(task, SWITCHING_ON_TIME);

    }


    TinySplitControlEvents events;

    public void postStartDone(final byte error)
    {
	TinyTask task= new TinyTask() {
		public void run(){
		    events.startDone(error);
		}
	    };
	// post to the queue
	tinyTaskQueue.enqueue( task );
    }

    public void postStopDone(final byte error)
    {
	totalTimeOn = totalTimeOn+(System.currentTimeMillis()-lastRealStart); // for statistics
	TinyTask task= new TinyTask() {
		public void run(){
		    events.stopDone(error);
		}
	    };
	// post to the queue
	tinyTaskQueue.enqueue( task );
    }

    // constructor for events wiring
    public TinySplitControl(TinySplitControlEvents implementedEvents)
    {
	events = implementedEvents;
    }



}