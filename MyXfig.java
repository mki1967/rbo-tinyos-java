public class MyXfig extends Rbo{

    static final int step=225;
    static final int offset=1;
    static final int pixSize=40;

    static void header()
    {
	System.out.print(
			 "#FIG 3.2  Produced by xfig version 3.2.5-alpha5\n"+
			 "Landscape\n"+
			 "Center\n"+
			 "Metric\n"+
			 "A4\n"+      
			 "100.00\n"+
			 "Single\n"+
			 "-2\n"+
			 "1200 2\n"
			 );
    }

    static void vLinesDown(int[] y)
    {
	for(int x=0; x<y.length; x++)
	    System.out.print(
			     "2 1 0 1 0 7 50 -1 -1 0.000 0 0 -1 0 0 2\n"+
			     "\t "+(offset+x)*step+" "+(offset+y.length)*step+
			     " "+(offset+x)*step+" "+(offset+y[x])*step+"\n"
			     );
    }

    static void vLinesUp(int[] y)
    {
	for(int x=0; x<y.length; x++)
	    System.out.print(
			     "2 1 0 1 0 7 50 -1 -1 0.000 0 0 -1 0 0 2\n"+
			     "\t "+(offset+x)*step+" "+offset*step+
			     " "+(offset+x)*step+" "+(offset+y[x])*step+"\n"
			     );
    }

    static void line(int x1, int y1, int x2, int y2)
    {
	System.out.print(
			 "2 1 0 1 0 7 50 -1 -1 0.000 0 0 -1 0 0 2\n"+
			 "\t "+(offset+x1)*step+" "+(offset+y1)*step+
			 " "+(offset+x2)*step+" "+(offset+y2)*step+"\n"
			 );

    }

    static void dottedLine(int x1, int y1, int x2, int y2)
    {
	System.out.print(
			 "2 1 2 1 0 7 50 -1 -1 3.000 0 0 -1 0 0 2\n"+
			 "\t "+(offset+x1)*step+" "+(offset+y1)*step+
			 " "+(offset+x2)*step+" "+(offset+y2)*step+"\n"
			 );

    }

    static void grid(int x, int y)
    {
	for(int i=0; i<x; i++)
	    dottedLine( i,0, i,y-1);
	for(int i=0; i<y; i++)
	    dottedLine( 0,i, x-1,i);

    }

    static void pixel(int x, int y)
    {
	System.out.print(
			 "2 2 0 1 0 7 50 -1 0 0.000 0 0 -1 0 0 5\n"+
			 "\t "+((offset+x)*step-pixSize)+" "+((offset+y)*step-pixSize)+
			 "\t "+((offset+x)*step+pixSize)+" "+((offset+y)*step-pixSize)+
			 "\t "+((offset+x)*step+pixSize)+" "+((offset+y)*step+pixSize)+
			 "\t "+((offset+x)*step-pixSize)+" "+((offset+y)*step+pixSize)+
			 "\t "+((offset+x)*step-pixSize)+" "+((offset+y)*step-pixSize)+
			 "\n"
			 );


    }	

    static void graph(int[] y)
    {
	for(int x=0; x<y.length; x++)
	    pixel(x, y[x]);
    }


    static void revBitsGraph(int k)
    {
	int n=(1<<k);
	int[] y= new int[n];
	for(int x=0; x<n; x++)
	    y[x]=revBits(k,x);

	header();
	grid(n,n);
	graph(y);
    }


    static void revBitsGraph2(int k)
    {
	int n=(1<<k);
	int[] y= new int[n];
	for(int x=0; x<n; x++)
	    y[x]=revBits(k,x);

	header();
	grid(n,2*n);
	graph(y);
	for(int x=0; x<n; x++)
	    y[x]=y[x]+n;
	graph(y);

    }





    static void leftTrajectory(int k, int t0, int r)
    {
        int n=(1<<k);
	revBitsGraph2(k);
	line(r,0, r,2*n-1);
        leftTrajectoryDraw(k,t0,r);
    }
    static void trajectories(int k, int r)
    {
        int n=(1<<k);
	revBitsGraph2(k);
	line(r,0, r,2*n-1);
	for(int t0=0; t0<n; t0++)
	    {
		leftTrajectoryDraw(k,(t0+n-1)%n,r);
		rightTrajectoryDraw(k,(t0+n-1)%n,r);
	    }
    }


    static void trajectoriesT(int k, int t0)
    {
        int n=(1<<k);
	revBitsGraph2(k);
	// line(0,t0, n-1, t0);
        t0=(t0+1)%n;
	for(int r=0; r<n; r++)
	    {
		if(revBits(k,t0)<r) leftTrajectoryDraw(k,(t0+n-1)%n,r);
		if(revBits(k,t0)>r) rightTrajectoryDraw(k,(t0+n-1)%n,r);
	    }
    }



    static void leftTrajectoryDraw(int k, int t0, int r)
    {
        int n=(1<<k);
	int lb=0;
	int t1=nextIn(k,t0, lb,r);
	lb=revBits(k, t1);	
	while(lb<r)
	    {
		int t2=nextIn(k,t1, lb,r);
		line(revBits(k, t1),((t1>t0)? t1:t1+n), 
		     revBits(k, t2),((t2>t0)? t2:t2+n)
		     );
                t1=t2;
                lb=revBits(k, t2);
            }

    }

    static void rightTrajectoryDraw(int k, int t0, int r)
    {
        int n=(1<<k);
	int ub=n-1;
	int t1=nextIn(k,t0, r,ub);
	ub=revBits(k, t1);	
	while(r<ub)
	    {
		int t2=nextIn(k,t1, r,ub);
		line(revBits(k, t1),((t1>t0)? t1:t1+n), 
		     revBits(k, t2),((t2>t0)? t2:t2+n)
		     );
                t1=t2;
                ub=revBits(k, t2);
            }

    }





    static void revBitsGraphShiftedUp(int k, int t0)
    {
	int n=(1<<k);
	int[] y= new int[n];
	for(int x=0; x<n; x++)
	    y[x]=(revBits(k,x)+n-t0)%n;

	header();
	grid(n,n);
	graph(y);
        vLinesDown(y);
    }



    static void revBitsGraphTrace(int k)
    {
	int n=(1<<k);
	int[] ty= new int[n];
	for(int x=0; x<n; x++)
	    ty[x]=revBits(k,x);

	header();
	grid(n,n);
	graph(ty);
	{
	    for(int y=0; y<n; y++)
		line(revBits(k,y),y, revBits(k,(y+1)%n), y+1);
	}
    }

    static void revBitsTree(int k)
    {
	int n=(1<<k);
	int[] y= new int[n];
	for(int x=0; x<n; x++)
	    y[x]=revBits(k,x);

	header();
	grid(n,n);

	line(0,y[0], 0+shift(k,0), y[0+shift(k,0)]);
	for(int x=1; x<n; x++)
	    if( (x&1)==0 )
		{
		    line(x,y[x], x+shift(k,x), y[x+shift(k,x)]);
		    line(x,y[x], x-shift(k,x), y[x-shift(k,x)]);
		}
	graph(y);
    }

    /* old function bs */

    public static int irmo(int k, int x) // index of rightmost one or k if x=0
    {
    
        if(x==0) return k;
       
        int i=0;
        while( (x&1) == 0)
            {
                x=x>>1;
                i++;
            } 
        return i;    
        
    }


    public static int lbs(int k, int x) // level of x-value x in the tree of bs_k
    {
        return k-irmo(k,x);
    }


    public static int bs(int k, int x) // permutation bs_k
    {
        if(x==0) return 0;
        // here: x>0
        return (1 << (lbs(k,x)-1) )+(x >> (irmo(k,x)+1));
    }

    static void bsTree(int k)
    {
	int n=(1<<k);
	int[] y= new int[n];
	for(int x=0; x<n; x++)
	    y[x]=bs(k,x);

	header();
	grid(n,n);

	line(0,y[0], 0+shift(k,0), y[0+shift(k,0)]);
	for(int x=1; x<n; x++)
	    if( (x&1)==0 )
		{
		    line(x,y[x], x+shift(k,x), y[x+shift(k,x)]);
		    line(x,y[x], x-shift(k,x), y[x-shift(k,x)]);
		}
	graph(y);
    }

    public static int recBs(int k, int x) // permutation bs_k
    {
        if(k==0) return 0;
        // here: k>0
        return (1-(x&1))*recBs(k-1, x/2)+(x&1)*((1<< (k-1))+(x/2)); // recBs(x/2)+ (x mod 2)*(2^(k-1)+(x/2))
    }

    static void recBsTree(int k)
    {
	int n=(1<<k);
	int[] y= new int[n];
	for(int x=0; x<n; x++)
	    y[x]=recBs(k,x);

	header();
	grid(n,n);

	line(0,y[0], 0+shift(k,0), y[0+shift(k,0)]);
	for(int x=1; x<n; x++)
	    if( (x&1)==0 )
		{
		    line(x,y[x], x+shift(k,x), y[x+shift(k,x)]);
		    line(x,y[x], x-shift(k,x), y[x-shift(k,x)]);
		}
	graph(y);
    }


    /* rbs recursion */
    static int rbs(int k, int x)
    {
	if(k==0) return 0;
	return rbs(k-1, x/2)+ (x&1)*(1<<(k-1)); // x - odd
    }


    static void rbsTree(int k)
    {
	int n=(1<<k);
	int[] y= new int[n];
	for(int x=0; x<n; x++)
	    y[x]=rbs(k,x);

	header();
	grid(n,n);

	line(0,y[0], 0+shift(k,0), y[0+shift(k,0)]);
	for(int x=1; x<n; x++)
	    if( (x&1)==0 )
		{
		    line(x,y[x], x+shift(k,x), y[x+shift(k,x)]);
		    line(x,y[x], x-shift(k,x), y[x-shift(k,x)]);
		}
	graph(y);
    }

    static void drawEdgesY(int k, int[][] edgesY)
    {
    for(int i=0; i<edgesY.length; i++)
       line( revBits(k,edgesY[i][0]), edgesY[i][0],revBits(k,edgesY[i][1]), edgesY[i][1]); 
    }

    
    static void treeDecopmositions(int k,int s)
    {

    revBitsGraph2(k); // header and grid

    int lst=last(k,s);
    for(int i=0; i<=lst; i++)
       {
       int s1=minY(k,s,i);
       int[][] edgesY=edgesOfTreeY(k,s1);
       drawEdgesY(k, edgesY);
       }
    }


    public static void main(String[] args)
    {
	/*
	  int[] test= {1,2,3,4,5,6,7,8};
	  header();
	  vLines(test);
	  for(int x=0; x<32; x++)
	  for(int y=0; y<16; y++)
	  pixel(x,y);
	*/
	/*
	  header();
	  int[] y=new int[32];

	  grid(32,16);

	  for(int i=0; i<y.length; i++)
	  {
	  y[i]=i/2;
	  }
	  graph(y);
	  for(int i=0; i<y.length-1; i++)
	  {
	  line(i,y[i], i+1, y[i+1]);
	  }
	*/
	int k=  Integer.valueOf(args[0]);
	int t0=  Integer.valueOf(args[1]);
        treeDecopmositions(k,t0);
        // leftTrajectory(k,t0, Integer.valueOf(args[2]));// args[2] - granica
	// trajectories(k, Integer.valueOf(args[1]));// args[1] - granica
        // trajectoriesT(k, Integer.valueOf(args[1]));// args[1] - t0
	// revBitsGraphTrace(k);
	// revBitsGraphShiftedUp(k, t0);
	// revBitsGraph(k);
	// revBitsTree(5);
	// bsTree(5);
	// rbsTree(5);
	// recBsTree(5);
    }

}
