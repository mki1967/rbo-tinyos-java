/**
 *
 *    Placeholder for TinyOS Boot for RBO Protocol simulation in Java.
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

public class TinyBoot{

    TinyTaskQueue tinyTaskQueue;

    TinyBootEvents events;

    public void postBootedTask()
    {
	TinyTask task= new TinyTask() {
		public void run(){
		    events.booted();
		}
	    };
	// post to the queue
	tinyTaskQueue.enqueue( task );
    }


    // constructor (for the wiring of events)

    public TinyBoot(TinyBootEvents implementedEvents)
    {
	events=implementedEvents;
    }

}