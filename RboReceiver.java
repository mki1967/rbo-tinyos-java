/**
 *
 *    Receiver Module of the RBO Protocol.
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

public class RboReceiver extends RboProtocol{


    // default values of parameters
    public static final int DEFAULT_TIMEOUT= (1<<13); // 8192 ms
    public static final int DEFAULT_TIME_MARGIN=4; // ms
    public static final int DEFAULT_MIN_SLEEPING_TIME= (1<<6); // 64 ms
    public static final int DEFAULT_LOG_MARGIN= 7; 
    // public static final int DEFAULT_MAX_SLEEPING_TIME= 60*1000; // ms

    // state constants. In TinyOS use rather "enum"
    public static final byte IDLE=0;
    public static final byte LISTENING=1;
    public static final byte SLEEPING=2;

    // error constans.  In TinyOS use rather "enum" combined with error_t
    public static final byte SUCCESS=0;   // the message with searchedKey has been found
    public static final byte TIMEOUT=1; // nothing received in LISTENING state
    public static final byte KEY_NOT_PRESENT=2; // the seachedKey is not present in the sequence
    public static final byte BAD_MESSAGE=3; // bad message received
    public static final byte FAILED_RADIO=4; // bad message received



    volatile int searchedKey; // the key to be found in the transmitted sequence
    // byte sequenceId;  // in RboProtocol
    // byte logSequenceLength; // in RboProtocol

    volatile int minRank; // lower bound for the rank of the searchedKey. Not valid if getSequenceId()==0
    volatile int maxRank; // upper bound for the rank of the searchedKey. Not valid if getSequenceId()==0

    volatile byte state=IDLE;    // state of the RboReceiver. Initially IDLE
    volatile RboMessage lastReceived = new RboMessage(); // buffer for the last received message
    volatile byte lastError=SUCCESS; // buffer for the last received message


    // parameters
    int timeout =DEFAULT_TIMEOUT; // timeout for listening in ms

    int timeMargin =DEFAULT_TIME_MARGIN; // time margin in ms
    int minSleepingTime =DEFAULT_MIN_SLEEPING_TIME; // minimal sleeping time in ms
    int logMargin=DEFAULT_LOG_MARGIN; // logarithm of the time margin
    // int maxSleepingTime =DEFAULT_MAX_SLEEPING_TIME; // maximal sleeping time in ms


 
    // timers and timers' events

    TinyTimer timeoutTimer = new TinyTimer(new TimeoutTimerEvents()); // wiring
    class TimeoutTimerEvents implements TinyTimerEvents{
	public void fired()
	{
	    if(state==LISTENING)
		{
		    switchToIdle();
		    postSearchDoneTask(lastReceived, TIMEOUT); // search failed due to timeout
		}
	}
    };

    TinyTimer sleepingTimer = new TinyTimer(new SleepingTimerEvents()); // wiring
    class SleepingTimerEvents implements TinyTimerEvents{
	public void fired()
	{
	    if(state==SLEEPING)
		{
		    switchToListening();
		}
	}
    };



    // ActiveMessageC.Receive
    TinyReceive receive=new TinyReceive(new ReceiveEvents());
    int receivedMessages=0; // for statistics
    class ReceiveEvents implements TinyReceiveEvents{
	public RboMessage receive(RboMessage message)
	{
	    receivedMessages++; // for statistics

	    if(state==LISTENING)
		{
		    timeoutTimer.stop(); 
		    // processing of the message

		    {
			// swap buffers 
			RboMessage tmp=lastReceived;
			lastReceived=message;
			message=tmp;
			// now the recent message is in lastReceived
		    }

		    {
			// check for vaildity
			if(lastReceived.header.getSequenceId()==INVALID_ID)
			    {
				// break searching
				switchToIdle();
				postSearchDoneTask(lastReceived, BAD_MESSAGE); // search failed due to bad message
				return message; // return buffer to the receive module
			    }
		    }

		    {
			// check whether the sequence has changed
			if((lastReceived.header.getSequenceId()!= sequenceId) || 
			   (lastReceived.header.getLogSequenceLength()!= logSequenceLength))
			    {
				// forget the previous parameters and results
				sequenceId=lastReceived.header.getSequenceId();
				logSequenceLength=lastReceived.header.getLogSequenceLength();
				minRank=0;
				maxRank=(1<<logSequenceLength)-1;
			    }
		    }

		    {
			// check for success and/or update the bounds: [minRank, maxRank]
			if(lastReceived.header.getKey()==searchedKey)
			    {
				// we have found one of the instances of the key
				switchToIdle();
				postSearchDoneTask(lastReceived, SUCCESS); // report SUCCESS
				return message; // return buffer to the receive module
			    }
			// here: lastReceived.header.getKey()!=searchedKey
			// we update either minRank or maxRank
			if((lastReceived.header.getKey()>searchedKey) && 
			   (lastReceived.header.getRank()<= maxRank) 
			   )
			    maxRank=lastReceived.header.getRank()-1; // update maxRank
			else if((lastReceived.header.getKey()<searchedKey) &&
				(lastReceived.header.getRank()>= minRank) 
				)
			    minRank=lastReceived.header.getRank()+1; // update minRank
				
			// now we may notice the absence of searchedKey in the sequence
			if(minRank>maxRank)
			    {
				// the searchedKey is not present in the transmitted sequence
				switchToIdle();
				postSearchDoneTask(lastReceived, KEY_NOT_PRESENT); // report absence of the key
				return message; // return buffer to the receive module
			    }
		    }

		    {
			// compute the time to the next useful message and decide whether to sleep
			int now= revBits(logSequenceLength, lastReceived.header.getRank()); // time slot for the lastReceived message
			int next= nextSlotIn(logSequenceLength, now, minRank, maxRank);
			int slotsToNext;
			if(now<next)
			    slotsToNext=next-now;
			else
			    slotsToNext=((1<<logSequenceLength)-now)+next;
			int remainingTime=slotsToNext*lastReceived.header.getTimeSlotLength();

			// now see if you can sleep
			
			if(remainingTime>=minSleepingTime)
			    switchToSleeping(remainingTime- (remainingTime>>logMargin)-timeMargin ); // not too short, not too long
			else
			    switchToListening(); // restart timeout

		    }

		    // ...
		}
	    return message; // return buffer to the receive module
	}
    }


    //  ActiveMessageC.SplitControl
    TinySplitControl splitControl = new TinySplitControl(new SplitControlEvents()); 
    class SplitControlEvents implements TinySplitControlEvents{
	public void startDone(byte error)
	{
	    if(error!=TinySplitControl.SUCCESS)
		{
		    switchToIdle();
		    postSearchDoneTask(lastReceived, FAILED_RADIO); // switching on failed
		}
	    else
		{
		    if(state==LISTENING)
			timeoutTimer.startOneShot(timeout);
		}
	}

	public void stopDone(byte error)
	{
	    if(error!=TinySplitControl.SUCCESS)
		{
		    switchToIdle();
		    postSearchDoneTask(lastReceived, FAILED_RADIO); // switching of failed
		}
	    else
		{
		    if(state==LISTENING)
			{
			    switchToListening(); // try to restart radio
			}
		}
	}

    }

    // other system compotnents
    TinyTaskQueue tinyTaskQueue; // the tasks queue of the system


    // changing state

    void switchToListening() // universal switch to the state: LISTENING
    {
	sleepingTimer.stop(); // doesn't hurt
	timeoutTimer.stop();  // doesn't hurt
	state=LISTENING;
	byte error=splitControl.start(); // switch radio on
	if( error== TinySplitControl.EALREADY)
	    timeoutTimer.startOneShot(timeout); // set timeout for reception; else set the timer in SplitControl.startDone() event 
	else if( error== TinySplitControl.FAIL )
	    {
		state=IDLE;
		postSearchDoneTask(lastReceived, FAILED_RADIO); // search failed due to timeout
	    }
	    
    }
 
    void switchToIdle() // universal switch to the state: IDLE // does not switch the radio
    {
	sleepingTimer.stop(); // doesn't hurt
	timeoutTimer.stop();  // doesn't hurt
	state=IDLE;
    }

    void switchToSleeping(int sleepingTime) // universal switch to the state: SLEEPING
    {
	if(state!=LISTENING) System.out.println(" switchToSleeping: state!=LISTENING, state=="+state); // debugging
	sleepingTimer.stop(); // doesn't hurt
	timeoutTimer.stop();  // doesn't hurt
	state=SLEEPING;
	sleepingTimer.startOneShot(sleepingTime);
	splitControl.stop(); // save energy: switch the radio off.
    }



    // COMMANDS

    public void search(int key) // start searching for the new key. old results can be used
    {

	if(sequenceId!=INVALID_ID) // we may use old results; we hope that any actual change of the sequence will be noticed
	    if(key<searchedKey)
		minRank=0; // old lower bound on rank is invalid
	    else if(key>searchedKey)
		maxRank=(1<<logSequenceLength)-1; // old upper bound on rank is invalid
	// else key==searchedKey -- just continue
	searchedKey=key; // set the new key to be searched
	switchToListening(); // restarts timeout timer
	//...
    }

    public void stop() // just stop current searching; may be resumed later with search(...)
    {
	switchToIdle();
    }

    public void reset() // stop searching (if any) and forget all previous results
    {
	switchToIdle();
	sequenceId=INVALID_ID;
	//...
    }

    // getting and setting parameters
    public void setTimeout(int t)
    {
	timeout=t;
    }

    public int getTimeout()
    {
	return timeout;
    }

    
    public void setTimeMargin(int t)
    {
	timeMargin=t;
    }

    public int getTimeMargin()
    {
	return timeMargin;
    }
    

    public void setLogMargin(byte l)
    {
	logMargin=l;
    }

    public int getLogMargin()
    {
	return logMargin;
    }

    public void setMinSleepingTime(int t)
    {
	minSleepingTime=t;
    }

    public int getMinSleepingTime()
    {
	return minSleepingTime;
    }

    /*
    public void setMaxSleepingTime(int t)
    {
	maxSleepingTime=t;
    }

    public int getMaxSleepingTime()
    {
	return maxSleepingTime;
    }

    */


 
    // getting current state variables of searching
    public byte getState()
    {
	return state;
    }


    public int getSearchedKey() // first check getSequenceId()
    {
	return searchedKey;
    }

    public int getMinRank() // first check getSequenceId()
    {
	return minRank;
    }

    public int getMaxRank() // first check getSequenceId()
    {
	return maxRank;
    }


    // EVENTS
    RboReceiverEvents events; // events of this RBO Receiver Protocol


    // POSTING TASKS

    void postSearchDoneTask(final RboMessage message, final byte error)
    {
	lastReceived=null;
	TinyTask task=new TinyTask(){
		public void run()
		{
		    lastError=error;
		    lastReceived=events.searchDone(message, error);
		}
	    };
	// ... post to the queue
	tinyTaskQueue.enqueue(task);
    }

   // constructor (for wiring of events)
    public RboReceiver(RboReceiverEvents implementedEvents)
    {
	events=implementedEvents;
    }

}
