/**
 *
 *    Header of the RBO Message.
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

public class RboHeader extends Rbo {

    byte sequenceId;        // if sequence of keys changes it should be changed. Zero is reserved for invalid Id - should not be used 
    byte logSequenceLength; // logarithm to the base of 2 of the sequence length. The length of the sequence is integer power of two.
    short timeSlotLength;   // interval between consecutive transmissions - in milliseconds

    int key;  // key of the message
    int rank; // rank of the key in the sequence. Transmitted in the slot  revBits(logSequenceLength, rank)

    public void setSequenceId(byte Id)
    {
	sequenceId=Id;
    }

    public byte getSequenceId()
    {
	return sequenceId;
    }

    public void setLogSequenceLength(byte k)
    {
	logSequenceLength=k;
    }

    public byte getLogSequenceLength()
    {
	return logSequenceLength;
    }


    public void setTimeSlotLength(short millis)
    {
	timeSlotLength=millis;
    }

    public short getTimeSlotLength()
    {
	return timeSlotLength;
    }

    public void setKey(int key1)
    {
	key=key1;
    }

    public int getKey()
    {
	return key;
    }

    public void setRank(int rank1)
    {
	rank=rank1;
    }

    public int getRank()
    {
	return rank;
    }



    public String toString()
    {
	return ("ID="+getSequenceId()
		+" LSL="+getLogSequenceLength()
		+" TSL="+getTimeSlotLength()
		+" KEY="+getKey()
		+" RNK="+getRank()
		);
    }
}