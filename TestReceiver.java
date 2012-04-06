/**
 *
 *    Receiver part of the RBO test.
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


import java.util.Random;

public class TestReceiver extends Thread{


    TextOutputScroll out=new TextOutputScroll("RECEIVER");

    Random random;

    TinyChannel channel;

    int searchesDone=0; 

    TinyRadio radio=new TinyRadio();
    static final int QUEUE_CAPACITY=100;
    TinyTaskQueue tinyTaskQueue=new TinyTaskQueue(QUEUE_CAPACITY);
    TinyScheduler tinyScheduler=new TinyScheduler();
    //...


    TinyBoot tinyBoot=new TinyBoot(new BootEvents());
    class BootEvents implements TinyBootEvents{
	public void booted()
	{
	    generateSearchedKey();
	    out.println("booted: searching for key: "+searchedKey);
	    rboReceiver.search(searchedKey);

	    //...
	}
    } 


    RboReceiver rboReceiver=new RboReceiver(new TestRboRevceiverEvents());
    class TestRboRevceiverEvents implements RboReceiverEvents{
	public RboMessage searchDone(RboMessage message, byte error)
	{

	    // report results
	    searchesDone++;
	    out.println(""+searchesDone+".searchDone: ERR:"+error+" MSG: "+message.toString());

	    // update parameters
	    logSequenceLength=message.header.getLogSequenceLength();

	    //generate the next key
	    generateSearchedKey();

	    // post the task of searching new key
	    TinyTask task=new TinyTask(){
		    public void run()
		    {
			out.println("task: searching for key: "+searchedKey);
			rboReceiver.search(searchedKey);
		    }
	    };
	    // ... post to the queue
	    tinyTaskQueue.enqueue(task);



	    // ... 

	    return message; // return the buffer to rboReceiver
	}

    }

    public TestReceiver(TinyChannel rboChannel, long seed)
    {
	channel=rboChannel;
	random=new Random(seed);
	out.println("seed ="+seed+"\n");


	// WIRING

	// wire the task queue
	rboReceiver.tinyTaskQueue= tinyTaskQueue;
	rboReceiver.timeoutTimer.tinyTaskQueue= tinyTaskQueue;
	rboReceiver.sleepingTimer.tinyTaskQueue= tinyTaskQueue;
	rboReceiver.receive.tinyTaskQueue= tinyTaskQueue;
	rboReceiver.splitControl.tinyTaskQueue= tinyTaskQueue;

	tinyBoot.tinyTaskQueue=tinyTaskQueue;
	tinyScheduler.tinyTaskQueue=tinyTaskQueue;

	//wire the radio
	rboReceiver.receive.radio=radio;
	rboReceiver.splitControl.radio=radio;

	//wire the channel
	// rboReceiver.radio.channel=channel;
	rboReceiver.receive.channel=channel;


	rboReceiver.receive.start(); // listen to the channel
	// ...
    }



    byte logSequenceLength=5; // ...
    int searchedKey;

    // auxiliary functions and procedures
    void generateSearchedKey()
    {
	searchedKey= random.nextInt( (1<<(logSequenceLength+1))+1 ); // between 0 and 2^(logSequenceLength+1)
    }


    public void run()
    {
	out.setVisible(true);
	tinyBoot.postBootedTask();
	tinyScheduler.taskLoop();
    }


    public static void main(String[] args)
    {
	// testing receiver only
	TestReceiver receiver=new TestReceiver(new TinyChannel(), System.currentTimeMillis());
	receiver.start();

    }


}