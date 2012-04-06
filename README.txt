This directory contains a prototype of 
the RBO protocol with a simple test application.

The files Rbo*.java contain implementation of the modules
of the protocol.



The files Tiny*.java contain emulations of the TinyOS 
components used by RBO.

The files *Events.java are specifications (parts of module's interfaces) 
of the events signalled by the corresponding modules.

 

To compile the programs run in this directory:

   javac *.java


SIMPLE TEST APPLICATION:
------------------------

To start simple test application of the protocol run:

   java TestApplication logSequenceLength timeSlotLength

where 
   logSequenceLength is logarithm of the length of the transmitted sequence
   (here it should be less than 30 - we are using Java type int)
and 
  timeSlotLength is the time between transmisions of consecutive elements
   (in milliseconds) 
This program creates two windows (for the sender and for the receiver).
The keys in the sequence broadcast by the sender are consecutive positive 
odd numbers (starting from 1),
while the receiver selects as the searched key a random integer
between 0 and 1+(maximum of the broacast sequence).
There is also simple menu in the terminal for printing statistics
and stoping/restarting the receiver/sender.

The messages in the receivers' window are printed after completion of each search.
They look like this:

 50.searchDone: ERR:2 MSG: H:ID=2 LSL=13 TSL=2 KEY=15769 RNK=7884 P:7884

ERR - status of the result ( 0 - SUCCESS, 1 - TIME_OUT, 2 - KEY_NOT_PRESENT)
ID -  sequence ID
LSL - logarithm of sequence length
TSL - time of sequence length
KEY - the key in last received message
RNK - rank of the key
P   - payload

The messages in the senders' window are printed after each round of 
sequence transmission.

The time in statistics is given in milliseconds.



--

The author can be reached at: Marcin.Kik@pwr.wroc.pl
