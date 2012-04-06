/**
 *
 *    Tests of the basic RBO functions.
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

import java.lang.management.*;

public class RboTest extends Rbo{

    /*****************************/
    /* TESTS - BEGIN             */
    /*****************************/

    public static void testNextSlotIn(int k)
	throws Exception
    {
	System.out.println("testNextSlotIn("+k+")");
	int n=(1<<k);
	for(int r1=0; r1<n; r1++)
	    for(int r2=r1; r2<n; r2++)
		for(int t=0; t<n; t++)
		    {
			int naive=naiveNextSlotIn(k,t, r1,r2);
			if(nextIn(k,t, r1,r2) != naive)
			    throw new Exception("nextIn("+k+","+t+", "+r1+","+r2+")="+nextSlotIn(k,t, r1,r2)+
						" != naiveNextSlotIn("+k+","+t+", "+r1+","+r2+")="+naiveNextSlotIn(k,t, r1,r2));
			/*
			  if(nextSlotIn(k,t, r1,r2) != naive)
			  throw new Exception("nextSlotIn("+k+","+t+", "+r1+","+r2+")="+nextSlotIn(k,t, r1,r2)+
			  " != naiveNextSlotIn("+k+","+t+", "+r1+","+r2+")="+naiveNextSlotIn(k,t, r1,r2));
			  if(reverseNextSlotIn(k,t, r1,r2) != naive)
			  throw new Exception("reverseNextSlotIn("+k+","+t+", "+r1+","+r2+")="+reverseNextSlotIn(k,t, r1,r2)+
			  " != naiveNextSlotIn("+k+","+t+", "+r1+","+r2+")="+naiveNextSlotIn(k,t, r1,r2));
			*/
		    }
	System.out.println("OK");
    }

    public static void testSpeed1(int k)
	throws Exception
    {
	System.out.println("naiveNextSlotIn("+k+")- START");
	long time1= System.currentTimeMillis();
	int n=(1<<k);
	for(int r1=0; r1<n; r1++)
	    for(int r2=r1; r2<n; r2++)
		for(int t=0; t<n; t++) 
		    {
			int y = naiveNextSlotIn(k,t, r1,r2);
		    }
	long time2= System.currentTimeMillis();
	System.out.println("naiveNextSlotIn("+k+")- STOP. Millisecons: "+(time2-time1));

		    
    }

    /* The following get*Time() functions are  adopted from:
       http://nadeausoftware.com/articles/2008/03/java_tip_how_get_cpu_and_user_time_benchmarking
    */

    /** Get CPU time in nanoseconds. */
    public static long getCpuTime( ) {
	ThreadMXBean bean = ManagementFactory.getThreadMXBean( );
	return bean.isCurrentThreadCpuTimeSupported( ) ?
	    bean.getCurrentThreadCpuTime( ) : 0L;
    }
 

    /** Get user time in nanoseconds. */
    public static long getUserTime( ) {
	ThreadMXBean bean = ManagementFactory.getThreadMXBean( );
	return bean.isCurrentThreadCpuTimeSupported( ) ?
	    bean.getCurrentThreadUserTime( ) : 0L;
    }


    /** Get system time in nanoseconds. *
	public static long getSystemTime( ) {
	ThreadMXBean bean = ManagementFactory.getThreadMXBean( );
	return bean.isCurrentThreadCpuTimeSupported( ) ?
        (bean.getCurrentThreadCpuTime( ) - bean.getCurrentThreadUserTime( )) : 0L;
	}
    */

    public static void testSpeed2(int k)
	throws Exception
    {
	System.out.println("nextSlotIn("+k+")- START");
	long time1= System.currentTimeMillis();
	int n=(1<<k);
	for(int r1=0; r1<n; r1++)
	    for(int r2=r1; r2<n; r2++)
		for(int t=0; t<n; t++) 
		    {
			int y = nextSlotIn(k,t, r1,r2);
		    }
	long time2= System.currentTimeMillis();
	System.out.println("nextSlotIn("+k+")- STOP. Millisecons: "+(time2-time1));

		    
    }


    public static void randomTest(int k, int length, int tests, String switches, long seed )
	throws Exception
    {
	ManagementFactory.getThreadMXBean().setThreadCpuTimeEnabled(true);
	System.out.println("Supported CPU Time:"
			   +ManagementFactory.getThreadMXBean().isCurrentThreadCpuTimeSupported( ));

	int n=(1<<k);
	if(length<= 0 || length>n)
	    throw new Exception(" randomTest: (length<= 0 || length>n)");

	if(tests<=0)
	    throw new Exception("  randomTest: (tests<=0)");

	java.util.Random random=new java.util.Random(seed);
	int[] r1=new int[tests];
	int[] r2=new int[tests];
	int[] t=new int[tests];
	for(int i=0; i<tests; i++)
	    {
		r1[i]=random.nextInt(n-length+1);
		r2[i]=r1[i]+length-1;
		t[i]=random.nextInt(n);
	    }
	// tables for results
	int[] y1=new int[tests];
	int[] y2=new int[tests];
	int[] y3=new int[tests];
	int[] y4=new int[tests];
	int[] y5=new int[tests];

	long t1,t2,t3,t4,t5;
	t1=t2=t3=t4=t5=-1;

	Thread.sleep(1000);
 
	if(switches.indexOf('4')!=-1)
	    {// TEST 4  : nextSlot
		System.out.println("nextSlotIn- START. k="+k+", 2^k="+n
				   +", length="+length
				   +", tests="+tests
				   +", seed="+seed
				   );
		// long time1= System.currentTimeMillis();
		long time1= getUserTime( );
		for(int i=0; i<tests; i++)
		    {
			// System.out.println(" parameters "+k+","+t[i]+",["+r1[i]+","+r2[i]+"]");
			y4[i]=nextSlotIn(k, t[i], r1[i],r2[i]);
		    }

		// long time2= System.currentTimeMillis();
		long time2= getUserTime( );
		t4=(time2-time1);
		System.out.println("nextSlotIn- STOP. t4: "+t4);
	    }// TEST 4 : END

	Thread.sleep(1000); 

	if(switches.indexOf('1')!=-1)
	    {// TEST 1  : plogNextSlot
		System.out.println("plogNextSlotIn- START. k="+k+", 2^k="+n
				   +", length="+length
				   +", tests="+tests
				   +", seed="+seed
				   );
		// long time1= System.currentTimeMillis();
		long time1= getUserTime( );
		for(int i=0; i<tests; i++)
		    {
			// System.out.println(" parameters "+k+","+t[i]+",["+r1[i]+","+r2[i]+"]");
			y1[i]=plogNextSlotIn(k, t[i], r1[i],r2[i]);
		    }

		// long time2= System.currentTimeMillis();
		long time2= getUserTime( );
		t1=(time2-time1);
		System.out.println("plogNextSlotIn- STOP. t1: "+t1);
	    }// TEST 1 : END

	Thread.sleep(1000); 

	if(switches.indexOf('2')!=-1)
	    {// TEST 2  : naiveNextSlot
		System.out.println("naiveNextSlotIn- START. k="+k+", 2^k="+n
				   +", length="+length
				   +", tests="+tests
				   +", seed="+seed
				   );
		// long time1= System.currentTimeMillis();
		long time1= getUserTime( );

		for(int i=0; i<tests; i++)
		    {
			// System.out.println(" parameters "+k+","+t[i]+",["+r1[i]+","+r2[i]+"]");
			y2[i]=naiveNextSlotIn(k, t[i], r1[i],r2[i]);
		    }

		// long time2= System.currentTimeMillis();
		long time2= getUserTime( );
		t2=(time2-time1);
		System.out.println("naiveNextSlotIn- STOP. t2: "+t2);
	    }// TEST 2 : END

	Thread.sleep(1000); 

	if(switches.indexOf('3')!=-1)
	    {// TEST 3: reverseNextSlot
		System.out.println("reverseNextSlotIn- START. k="+k+", 2^k="+n
				   +", length="+length
				   +", tests="+tests
				   +", seed="+seed
				   );
		// long time1= System.currentTimeMillis();
		long time1= getUserTime( );

		for(int i=0; i<tests; i++)
		    {
			// System.out.println(" parameters "+k+","+t[i]+",["+r1[i]+","+r2[i]+"]");
			y3[i]=reverseNextSlotIn(k, t[i], r1[i],r2[i]);
		    }

		// long time2= System.currentTimeMillis();
		long time2= getUserTime( );

		t3=(time2-time1);

		System.out.println("reverseNextSlotIn- STOP. t3: "+t3);
	    }// TEST 3: END

	Thread.sleep(1000); 


	if(switches.indexOf('5')!=-1)
	    {// TEST 5  : nextIn
		System.out.println("nextIn- START. k="+k+", 2^k="+n
				   +", length="+length
				   +", tests="+tests
				   +", seed="+seed
				   );
		// long time1= System.currentTimeMillis();
		long time1= getUserTime( );
		for(int i=0; i<tests; i++)
		    {
			// System.out.println(" parameters "+k+","+t[i]+",["+r1[i]+","+r2[i]+"]");
			y5[i]=nextIn(k, t[i], r1[i],r2[i]);
		    }

		// long time2= System.currentTimeMillis();
		long time2= getUserTime( );
		t5=(time2-time1);
		System.out.println("nextIn- STOP. t5: "+t5);
	    }// TEST 4 : END

	Thread.sleep(1000); 

	System.out.println(    " RESULTS FOR: k="+k+", 2^k="+n
			       +", length="+length
			       +", tests="+tests
			       +", seed="+seed
			       +" ARE:\n  "
			       +"  t1 ="+(t1/1e6)
			       +", t2 ="+(t2/1e6)
			       +", t3 ="+(t3/1e6)
			       +", t4 ="+(t4/1e6)
			       +", t5 ="+(t5/1e6)
			       );

	if(switches.indexOf("12345")!=-1)
	    {
		System.out.print("Checking results ...");
		for(int i=0; i<tests; i++)
		    if(y1[i]!=y2[i] || y1[i]!=y3[i] || y1[i]!=y4[i]|| y1[i]!=y5[i])
			throw new Exception("BAD RESULTS: for i="+i
					    +": y1[i]="+y1[i]
					    +", y2[i]="+y2[i]
					    +", y3[i]="+y3[i]
					    +", y4[i]="+y4[i]
					    +", y5[i]="+y5[i]
					    );
		System.out.println("OK");
	    }

    }


    /*****************************/
    /* TESTS - END               */
    /*****************************/


    public static void main(String[] args)
    {
	try
	    {
		if(args[0].compareTo("-nextSlotIn")==0)
		    {
			int k=  Integer.valueOf(args[1]);
			testNextSlotIn(k);
		    }
		else if(args[0].compareTo("-speed")==0)
		    {
			int k=  Integer.valueOf(args[1]);
			testSpeed1(k);
			testSpeed2(k);
		    }
		else if(args[0].compareTo("-random")==0)
		    {
			int k=  Integer.valueOf(args[1]);
			int length=  Integer.valueOf(args[2]);
			int tests=   Integer.valueOf(args[3]);
			String switches=args[4];
                        long seed;
			if(args.length==6)
			    seed= Long.valueOf(args[5]);
			else
			    seed=System.currentTimeMillis();
			randomTest(k, length, tests, switches, seed);
		    }
		else 
		    throw new Exception("BAD USAGE !!!\n\n");

	    }
	catch(Exception e)
	    {
		System.out.println(e);

		System.out.println("\nUsage:      java B -options\n");
		System.out.println("where options include:");
		System.out.println(" -nextSlotIn  k                   test equivalence of *nextSlotIn_k functions");
		System.out.println(" -random k length tests switches [seed]    run tests on random intervals of given length");
	    }
    }
}
