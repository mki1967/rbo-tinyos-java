public  class RboExtraEnergyTest{

    static int test(int k, int length, java.util.Random random)
    {

	int n = (1<< k); // 2^k
	int x1= random.nextInt(n-length);
	int x2= x1+length;

	int lb=0;
	int ub=n-1;

	int used=0;
	int hit=0;

	int t0= random.nextInt(n);

	int t=t0;
	while( lb<x1 || ub>x2)
	    {
		int x=Rbo.revBits(k,t);
		if(lb<=x || x<= ub)
		    {
			used++;
			if(x1<=x && x<=x2) hit++;
			if(x<x1) lb=x+1;
			if(x2<x) ub=x-1;
		    }
		t=Rbo.nextSlotIn(k,t,lb,ub);
	    }
	// System.out.println("k="+k+" n="+n+" length="+length+" t0="+t0+" x1="+x1+" used="+used+" ee="+(used-hit)); 
	return used-hit;
    }
    ///
    static int leftTest(int k, int x1, int t)
    {
	int n=(1<<k); //2^k
        int lb=0;
        // int ub=n-1;
        int eeLeft=0;
   	while( lb<x1)
	    {
		int x=Rbo.revBits(k,t);
		if(lb<=x)
		    {
			// if(x1<=x && x<=x2) hit++;
			if(x<x1) 
			    {
				lb=x+1;
				eeLeft++;
			    }
		    }
		if(lb<=x1) t=Rbo.nextSlotIn(k,t,lb,x1);
	    }
	return eeLeft;
     
    }    

    static int rightTest(int k, int x2, int t)
    {
	int n=(1<<k); //2^k
        // int lb=0;
        int ub=n-1;
        int eeRight=0;
   	while( x2<ub)
	    {
		int x=Rbo.revBits(k,t);
		if(x<=ub)
		    {
			// if(x1<=x && x<=x2) hit++;
			if(x2<x) 
			    {
				// if(x2==7) System.out.println(x); // for tests
				ub=x-1;
				eeRight++;
			    }
		    }
		if(x2<=ub) t=Rbo.nextSlotIn(k,t,x2,ub);
	    }
	return eeRight;
     
    }    



    static void allLeftRightTests(int k)
    {
	int n=(1<<k); //2^k


	//int[] eeLT=new int[n]; // minimal x1 for which ee>=k starting from t
	//int[] eeRT=new int[n];

	System.out.println("Left side energy tests:");
	int maxeeL=0;
        int eeL=0;
        for(int t=0; t<n; t++)
	    for(int x1=0; x1<=n; x1++)
		{
		    int ee=leftTest(k,x1,t);//
		    if (maxeeL<ee)
			{
			    maxeeL=ee;
			    System.out.println("k="+k+" x1="+x1+" t="+t+" eeLeft="+ee);//
			}
		    // else if(ee >=k) System.out.println("k="+k+" x1="+x1+" t="+t+" eeLeft="+ee);//
		}
                  

  	System.out.println("Right side energy tests:");
	int maxeeR=0;
	for(int t=0; t<n; t++)
	    for(int x2=n-1; x2>=-1; x2--)
		{
		    int ee=rightTest(k,x2,t);//
		    if (maxeeR<ee)
			{
			    maxeeR=ee;
			    System.out.println("k="+k+" x2="+x2+" t="+t+" eeRight="+ee);//
			}
		    // else if(ee >=k) System.out.println("k="+k+" x1="+x1+" t="+t+" eeLeft="+ee);//
		}
	if(maxeeL>k) System.out.println("ALERT! maxeeL="+maxeeR);
	if(maxeeR>k) System.out.println("ALERT! maxeeR="+maxeeR);

                  
    }


    ///
    static int oneTest(int k, int x1, int x2, int t)
    {
	int n=(1<<k); //2^k
        int lb=0;
        int ub=n-1;
        int used=0;
        int hit=0;
   	while( lb<x1 || x2<ub)
	    {
		int x=Rbo.revBits(k,t);
		if(lb<=x || x<= ub)
		    {
			used++;
			if(x1<=x && x<=x2) hit++;
			if(x<x1) lb=x+1;
			if(x2<x) ub=x-1;
		    }
		if(lb<=ub) t=Rbo.nextSlotIn(k,t,lb,ub);
	    }
	return used-hit;
     
    }    

    static void allTests(int k)
    {
	int n=(1<<k); //2^k
	int maxee=0;
	System.out.println("non-Empty tests:");

        for(int t=0; t<n; t++)
	    for(int x1=0; x1<n; x1++)
		for(int x2=x1; x2<n; x2++)
		    {
			int ee=oneTest(k,x1,x2,t);
			if (maxee<ee)
			    {
				maxee=ee;
				System.out.println("k="+k+" x1="+x1+" x2="+x2+" t="+t+" ee="+ee);
			    }
		    }
                  
    }

    static void allEmptyTests(int k)
    {
	int n=(1<<k); //2^k
	int maxee=0;
	System.out.println("Empty tests:");
        for(int t=0; t<n; t++)
	    for(int x1=0; x1<=n; x1++)
		{
		    int x2=x1-1;
		    int ee=oneTest(k,x1,x2,t);
		    if (maxee<ee)
			{
			    maxee=ee;
			    System.out.println("k="+k+" x1="+x1+" x2="+x2+" t="+t+" ee="+ee);
			}
		}
                  
    }

    static void allSingletonTests(int k)
    {
	int n=(1<<k); //2^k
	int maxee=0;
	System.out.println("Singleton tests:");
        for(int t=0; t<n; t++)
	    for(int x1=0; x1<n; x1++)
		{
		    int x2=x1;
		    int ee=oneTest(k,x1,x2,t);
		    if (maxee<ee)
			{
			    maxee=ee;
			    System.out.println("k="+k+" x1="+x1+" x2="+x2+" t="+t+" ee="+ee);
			}
		}
                  
    }

    static void tests(int k, int length, int tests, long seed )
    {
	java.util.Random random=new java.util.Random(seed);
	int n = (1<< k); // 2^k
	int maxee=0;
	int imax=-1;
	for(int i=0; i<tests; i++)
	    {
		int ee=test(k,length, random);
		if(ee>maxee)
		    {
			imax=i;
			maxee=ee;
		    }
	    }
	System.out.println("k="+k+" n="+n+" length="+length+" seed="+seed+" imax="+imax+" maxee="+maxee); 

    }



    public static void main(String[] args)
    {
	try
	    {
		int k=  Integer.valueOf(args[0]);
                allLeftRightTests(k);
		// allEmptyTests(k);
                // allSingletonTests(k);
		// allTests(k);

	    }
	catch(Exception e)
	    {
		System.out.println(e);

		System.out.println("\nUsage:      java RboExtraEnergy  <arguments>\n");
		System.out.println("where the arguments are:");
		System.out.println("k");
	    }
    }

 
    /////////// OLD ////////
    public static void mainOld(String[] args)
    {
	try
	    {
		int k=  Integer.valueOf(args[0]);
		int length=  Integer.valueOf(args[1]);
		int tests=   Integer.valueOf(args[2]);
		long seed;
		if(args.length==4)
		    seed= Long.valueOf(args[3]);
		else
		    seed=System.currentTimeMillis();

		tests(k, length, tests, seed);

	    }
	catch(Exception e)
	    {
		System.out.println(e);

		System.out.println("\nUsage:      java RboRobustExpeiment <arguments>\n");
		System.out.println("where the arguments are:");
		System.out.println("k length  tests [seed]");
	    }
    }


}
