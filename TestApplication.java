/**
 *
 *     The test application of the RBO  protocol.
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

import java.io.*;

public class TestApplication{

    TinyChannel channel=new TinyChannel();
    TestSender sender;
    TestReceiver receiver;

    public static final byte DEFAULT_LOG_SEQUENCE_LENTGH=13;

    public static final short DEFAULT_TIME_SLOT_LENGTH=2;


    public static long startTime;

    public TestApplication(byte  logSequenceLength, short timeSlotLength)
    {
	startTime=System.currentTimeMillis();
	sender=new TestSender(channel, logSequenceLength, timeSlotLength);
	receiver=new TestReceiver(channel, System.currentTimeMillis());
    }




    public static void menu(TestReceiver receiver, TestSender sender)
    {


	BufferedReader in=new BufferedReader(new InputStreamReader(System.in));
	String choice="";

	while(true)
	    {
		System.out.println("RBO DEMO menu:");
		if(receiver.tinyScheduler.stopped)
		    System.out.println("1 - Restart receiver");
		else
		    System.out.println("1 - Stop receiver");
		if(sender.stopped)
		    System.out.println("2 - Restart sender");
		else
		    System.out.println("2 - Stop sender");
		System.out.println("s - Print statistics");
		System.out.println("x - Exit");


		try
		    {
			choice=in.readLine();
		    }
		catch(Exception e)
		    {
			System.out.println(e);
			System.exit(-1);
		    }

		if(choice.compareTo("1")==0)
		    if(receiver.tinyScheduler.stopped)
			{
			    receiver.tinyScheduler.restart();
			    receiver.out.println("RESTARTED");

			}
		    else
			{
			    receiver.tinyScheduler.stop();
			    receiver.out.println("STOPPED");
			}
		else if(choice.compareTo("2")==0)
		    if(sender.stopped)
			{
			    sender.restartSending();
			    sender.out.println("RESTARTED");
			}
		    else
			{
			    sender.stopSending();
			    sender.out.println("STOPPED");
			}
		else if(choice.compareTo("s")==0)
		    printStatistics(receiver, sender);
		else if(choice.compareTo("x")==0)
		    System.exit(0);

	    }
    }

    public static void printStatistics(TestReceiver receiver, TestSender sender)
    {
	System.out.println("NSI: "
			   +" total: "+Rbo.totalNSI
			   +"; naive: "+Rbo.naiveNSI
			   +"; plog: "+Rbo.plogNSI
			   +"; reverse: "+Rbo.reverseNSI
			   );
	System.out.println("Total time: " +(System.currentTimeMillis()-startTime));
	System.out.println("Total Radio ON: "+receiver.rboReceiver.splitControl.totalTimeOn); 
	System.out.println("Receiver searches done: "+ receiver.searchesDone);
	System.out.println("Received rbo messages: "+receiver.rboReceiver.receivedMessages);
	System.out.println("Sent messages: "+sender.sentMessages);
    }

    public static void main(String[] args)
    {
        
	byte  logSequenceLength;
	short timeSlotLength;
	try
	    {
		logSequenceLength= Byte.valueOf(args[0]);
		timeSlotLength= Short.valueOf(args[1]);
	    }
	catch(Exception e)
	    {
		logSequenceLength=DEFAULT_LOG_SEQUENCE_LENTGH;
		timeSlotLength=DEFAULT_TIME_SLOT_LENGTH;
		System.out.println("usage: java TestApplication logSequenceLength timeSlotLength");
		System.out.println("using default values: logSequenceLength="+logSequenceLength+
				   " timeSlotLength="+timeSlotLength+" (in milliseconds)");
	    }

	TestApplication app=new TestApplication(logSequenceLength, timeSlotLength );
	app.sender.start();
	app.receiver.start();

	menu(app.receiver, app.sender);
    }
}