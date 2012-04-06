/**
 *
 *    Placeholder for TinyOS Task Queue for RBO Protocol simulation in Java.
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

public class TinyTaskQueue{

    TinyTask[] queue; // cyclic queue implemented in the array of fixed size

    int first;
    int nextFree;


    public  boolean isEmpty()
    {
	return queue[first]==null;
    }


    public boolean isFull()
    {
	return queue[nextFree]!=null;
    }

    public synchronized void enqueue(TinyTask task)
    {
	try
	    {
		while(isFull()) wait();
	    }
	catch(Exception e)
	    {
		System.out.println(e);
		System.exit(-1);
	    }
	queue[nextFree]=task;
	nextFree=(nextFree+1)% queue.length;
	notify();
    }



    public synchronized TinyTask dequeue()
    {
	try
	    {
		while(isEmpty()) wait();
	    }
	catch(Exception e)
	    {
		System.out.println(e);
		System.exit(-1);
	    }

	TinyTask task=queue[first];
	queue[first]=null;
	first= (first+1)%queue.length;
	notify();
	return task;
    }

    public TinyTaskQueue(int capacity)
    {
	queue= new TinyTask[capacity];
	first=nextFree=0;
    }


}