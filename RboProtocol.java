/**
 *
 *    Common Part of the RBO Protocol Modules.
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


public class RboProtocol extends Rbo{

    static final byte INVALID_ID=0;

    byte sequenceId=INVALID_ID;
    byte logSequenceLength; // not valid if sequenceId==0
    short timeSlotLength;   // not valid if sequenceId==0


    public void setSequenceId(byte Id)
    {
	sequenceId=Id;
    }

    public byte getSequenceId()
    {
	return sequenceId;
    }


    public static byte nextSequenceId(byte id)
    {
	return (id== (byte) 255)? (byte) 1: (byte)(id+1); // never return zero
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

}
