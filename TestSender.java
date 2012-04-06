/**
 *
 *    Sender part of the RBO test.
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

public class TestSender extends Thread{

    TextOutputScroll out=new TextOutputScroll("SENDER");
    TinyChannel channel;
    Random random;


    volatile byte sequenceId=1;  // ...
    volatile byte logSequenceLength=10; // ...


    volatile short timeSlotLength=5;  // ...

    RboMessage[] message; // sorted array of messages of length 2^logSequenceLength


    // for statistics
    int sentMessages=0;
    int rounds=0;


    volatile boolean stopped=false;

    void stopSending()
    {
	stopped=true;
    }

    synchronized void  restartSending()
    {
	stopped=false;
	notify(); // notify sending loop
    }

    RboMessage[] generateMessages(byte logSequenceLength, short timeSlotLength)
    {
	byte nextId=RboProtocol.nextSequenceId(sequenceId);
	out.println("GENERATING SEQUENCE: ID="+nextId
		    +" LSL="+logSequenceLength
		    +" TSL="+timeSlotLength
		    );

	// sequenceId++;
	int length=(1<<logSequenceLength);
	RboMessage[] message=new RboMessage[length];
	for(int i=0; i<message.length; i++)
	    {
		message[i]=new RboMessage();
		message[i].header.setSequenceId(nextId);
		message[i].header.setLogSequenceLength(logSequenceLength);
		message[i].header.setTimeSlotLength(timeSlotLength);
		message[i].header.setKey(2*i+1); // the keys are: 1,3, ... 2^(logSequenceLength+1)-1
		message[i].header.setRank(i);
		message[i].setPayload(i); // doesn't matter
	    }
	return message;
    }


    synchronized void replaceMessages(RboMessage[] newSequence) // newSequence is not empty
    {
	message=newSequence;
	System.gc(); // try to remove old messages
	yield();

	sequenceId= message[0].header.getSequenceId();
	logSequenceLength= message[0].header.getLogSequenceLength();
	timeSlotLength=message[0].header.getTimeSlotLength();
    }

    synchronized void sendingLoop()
    {
	int timeSlot=0;
	int round=0;

	byte seqId=0;
	byte logLength=0;
	while(true)
	    {
		try
		    {
			while(stopped) wait(); // refrain from sending
		    }
		catch(Exception e)
		    {
			System.out.println(e);
			System.exit(-1);
		    }

		// test for change
		if( (seqId!=message[0].header.getSequenceId()) ||
		    (logLength!=message[0].header.getLogSequenceLength())
		    )
		    {
			seqId=message[0].header.getSequenceId();
			logLength=message[0].header.getLogSequenceLength();
			timeSlot=0; // restart for new sequence
			round=0; // restart for new sequence
		    }

		// report start of new round
		if(timeSlot==0)
		    out.println("ROUND: "+round
				+"; ID="+message[0].header.getSequenceId()
				+" LSL="+message[0].header.getLogSequenceLength()
				+" TSL="+message[0].header.getTimeSlotLength()
				);
		int rank=Rbo.revBits(logSequenceLength, timeSlot);
		channel.setMessage(message[rank]);
		sentMessages++;

		try
		    {
			sleep(message[rank].header.getTimeSlotLength()); // keep the time distance between messages
		    }
		catch(Exception e)
		    {
			System.out.println(e);
			System.exit(-1);
		    }

		timeSlot= (timeSlot+1) % (1<<logSequenceLength); // next time slot module sequence length
		if(timeSlot==0) round++; // detect new round;

	    }
    }

    public void run()
    {
	out.setVisible(true);
	sendingLoop();

    }


    public TestSender(TinyChannel rboChannel, byte logSeqLen, short timeSlotLen)
    {
	channel=rboChannel;
	logSequenceLength=logSeqLen;
	timeSlotLength=timeSlotLen;
	replaceMessages(generateMessages(logSequenceLength, timeSlotLength));

    }


    public static void main(String[] args)
    {
	// testing sender only

	byte  logSequenceLength= Byte.valueOf(args[0]);
	short timeSlotLength= Short.valueOf(args[1]);
	TestSender sender=new TestSender(new TinyChannel(), logSequenceLength, timeSlotLength);

	sender.start();
    }


}
