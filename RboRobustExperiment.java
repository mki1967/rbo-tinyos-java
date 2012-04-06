/* TESTING ROBUSTNESS  */

public  class RboRobustExperiment{

    // input
    // int tests; // number of tests
    int k;
    double receptionProbability;

    // outputs
    int[] time;
    int[] energy;


    int maxTime;
    int maxEnergy;
    double avgTime;
    double avgEnergy;

    double stDevTime;
    double stDevEnergy;

    long seed;
    java.util.Random random;

    void print()
    {
	System.out.println(""+
			   time.length
			   +";"+
			   seed
			   +";"+
			   receptionProbability
			   +";"+
			   k
			   +";"+
			   avgTime
			   +";"+
			   stDevTime
			   +";"+
			   maxTime
			   +";"+
			   avgEnergy
			   +";"+
			   stDevEnergy
			   +";"+
			   maxEnergy
			   );

		       
    }

    static void printHeader()
    {
	System.out.println(""+
			   "tests"
			   +";"+
			   "seed"
			   +";"+
			   "receptionProbability"
			   +";"+
			   "k"
			   +";"+
			   "avgTime"
			   +";"+
			   "stDevTime"
			   +";"+
			   "maxTime"
			   +";"+
			   "avgEnergy"
			   +";"+
			   "stDevEnergy"
			   +";"+
			   "maxEnergy"
			   );
    }

    RboRobustExperiment(int logSeqLen, int tests, double pr, long seed)
    {
	k=logSeqLen;
	receptionProbability=pr;

	time=new int[tests];
	energy=new int[tests];

	this.seed=seed;
	random=new java.util.Random(seed);
    }


    void singleRobustAbsentExperiment(int number)
    {
	int n= (1<<k); // 2^k
        int minr = 0;
        int maxr = n-1; // 2^k-1
	int key= 2*random.nextInt(n+1); // key = 2*i, 0<= key <= 2*n

	int t= random.nextInt(n); // starting time
	
	int rEnergy=0;
	int rTime=0;

	while(minr<= maxr)
	    {
		rEnergy++;
		int t1=Rbo.nextSlotIn(k, t, minr, maxr);
		rTime+= (t1>t) ? t1-t : n-t+t1; // note that it is n for t1==t
		t=t1; // clock update
		Boolean success= (random.nextDouble()< receptionProbability);
		if(success)
		    {
			// we can update [minr, maxr]
			int rank=Rbo.revBits(k, t); // 0<= rank <= n-1
			int mKey= 2*rank+1; // 1<= mKey <= 2*n-1, mKey is odd 
			if((mKey<key) && (minr<=rank))
			    minr=rank+1;

			if((mKey>key) && (maxr>=rank))
			    maxr=rank-1;

		    }
	    }
	
	time[number]=rTime;
	energy[number]=rEnergy;

    }

    void makeExperiments()
    {
	for(int i=0; i<time.length; i++)
	    {
	    singleRobustAbsentExperiment(i);
	    // System.out.print(".");
	    }
	// System.out.println();

    }

    void computeStatistics()
    {
	maxTime=time[0];
	for(int i=1; i<time.length; i++)
	    if(maxTime<time[i]) maxTime=time[i];

	maxEnergy=energy[0];
	for(int i=1; i<energy.length; i++)
	    if(maxEnergy<energy[i]) maxEnergy=energy[i];

	avgTime=0;
	for(int i=0; i<time.length; i++)
	    avgTime+=time[i];
	avgTime= avgTime/time.length;

	avgEnergy=0;
	for(int i=0; i<energy.length; i++)
	    avgEnergy+=energy[i];
	avgEnergy= avgEnergy/energy.length;

	{ // time standard deviation
	    double sum=0;
	    for(int i=0; i<time.length; i++)
		{
		    double dif=time[i]-avgTime;
		    sum+= (dif*dif);
		}
	    stDevTime= Math.sqrt(sum/time.length);
	}

	{ // energy standard deviation
	    double sum=0;
	    for(int i=0; i<energy.length; i++)
		{
		    double dif=energy[i]-avgEnergy;
		    sum+= (dif*dif);
		}
	    stDevEnergy= Math.sqrt(sum/energy.length);
	}

    }



    public static void testRobustness(int k, int tests, double probability, long seed)
    {
	
	RboRobustExperiment experiment= new RboRobustExperiment(k, tests, probability, seed);

	experiment.makeExperiments();
	experiment.computeStatistics();
	experiment.print();
    }

    public static void main(String[] args)
    {
	try
	    {
			int k1=  Integer.valueOf(args[0]);
			int k2=  Integer.valueOf(args[1]);
			int tests=   Integer.valueOf(args[2]);
			double pr=Double.valueOf(args[3]);
                        long seed;
			if(args.length==5)
			    seed= Long.valueOf(args[4]);
			else
			    seed=System.currentTimeMillis();
			RboRobustExperiment.printHeader();

			for(int k=k1; k<=k2; k++)
			    testRobustness(k, tests, pr, seed);

	    }
	catch(Exception e)
	    {
		System.out.println(e);

		System.out.println("\nUsage:      java RboRobustExpeiment <arguments>\n");
		System.out.println("where the arguments are:");
		System.out.println("k1 k2  tests probabilty [seed]");
	    }
    }


}
