/**
 *
 *    Implementation of the basic RBO functions.
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


public class Rbo{

    
    public static int shift(int k, int x) // needed only by MyXfig.java 
    // shift on the level of x (and step on the next level)
    {
	int mask = (1<<k)-1;
	// while( (x&mask) != 0 ) mask = mask >>> 1;
	while( (x&mask) != 0 ) mask = mask >> 1;
	return mask^(mask>>1);
    }
    


    public static int lv(int y)  
    // level of y-value y in the tree of bs_k
    {
	int l=0;
	while(y!=0) 
	    {
		y=(y>>1);
		l++;
	    }
	return l;
    }
   

    public static int revBits(int k, int x) 
    // reverse of k lowest bits
    {
	// if(x==0) return 0;
	// here x>0
	int y= (x&1);
	for(int i=1; i<k; i++)
	    {
		y= y<<1;
		x= x>>1;
		y= (y | (x&1));
	    }
	return y;
    }


    public static int minRevBits(int k, int r1, int r2)
    // min(revBits([r1,r2]))
    // we assume: 0<=r1<=r2< 2^k
    {
	int x=0; // root
	int s= 1 << (k-1);
	while(x<r1 || r2<x)
	    {
		if(x<r1)
		    x=x+s;
		else
		    x=x-s;
		s=s>>1;
	    }
	return revBits(k, x);
    }


    public static int maxRevBits(int k, int r1, int r2)
    // max(revBits([r1,r2]))
    // we assume: 0<=r1<=r2< 2^k
    {
	int mask= (1<<k)-1;
	return mask ^ minRevBits(k, r2^ mask, r1^mask );
    }



    public static int plogNextSlotIn(int k, int t, int r1, int r2)
    // (t+min{d>0 : r1<= revBits( (t+d)mod 2^k   ) <=r2}) mod 2^k 
    // we assume 0<=r1<=r2< 2^k 
    // iterative version
    {
	plogNSI++; // for statistics only


	int rec=0; // compensates for the (removed) tail recursion
	int tFirst, tLast, l, minL, maxL, aboveL, tFirstL, tLastL;
	int shift, stepmask;

	while(true)
	    {

		if(r1<r2) // test if r1 or r2 can be removed
		    {
			int r=revBits(k, t);
			if(r==r1) r1++; // possible reduction to singleton
			else if(r==r2) r2--; // possible reduction to singleton
		    }

		if(r1==r2) // [r1,r2] is a singleton -  no choice
		    return rec+revBits(k,r1); 
	
		tFirst=minRevBits(k, r1,r2); // first slot in [r1,r2]

		if(t < tFirst)  // we are before the entrace to [r1,r2] in this round
		    return rec+(tFirst);

		tLast= maxRevBits(k, r1,r2); // last slot in [r1,r2]

		if(tLast <= t) // wait till the entrance to [r1,r2] in the next round 
		    return rec+(tFirst); 

		// here: t<tLast

		// find min{ l>= lv(t): level l intersects [r1,r2] } 
		// (It must exist since: t<= tLast) 
	        l=lv(t);
		shift=(1<<(k-l));
		stepmask= ~((shift<<1)-1);
	        minL=((r1+shift-1)&stepmask); // "&stepmask" instead of division
	        maxL=((r2-shift)&stepmask);   // "&stepmask" instead of division
		while(minL>maxL) // [r1,r2] does not intersect level l
		    {
			l++;
			shift=shift>>1;
			stepmask=stepmask>>1;
			minL=((r1+shift-1)&stepmask);
			maxL=((r2-shift)&stepmask);
		    }
		// [minL+shift, maxL+shift] is the minimal interval that 
		// contains intersection of level l with [r1,r2] 
		minL= minL>>(k-l+1); // now the division
		maxL= maxL>>(k-l+1); // now the division
		// [minL, maxL] are now the coresponding ranks within the level l
	        tFirstL=minRevBits(l-1, minL, maxL); // entrance to [minL, maxL] within the level l 
	        aboveL= 1<<(l-1); // number of nodes above the level l 
		if(t< aboveL+tFirstL) // next slot is the first within level l		       
		    return rec+ aboveL+tFirstL; 
		    

		// here: l=lv(t) and t>= aboveL+tFirstL
	        tLastL=maxRevBits(l-1, minL,maxL);
		if(t>= aboveL+tLastL)
		    {
			// here: l=lv(t) and t>=aboveL+tLastL and l<k (since t<maxRevBits(k, r1,r2))
			// next slot after t is the first one within some  level after lv(t)

			l++;
			shift=shift>>1;
			stepmask=stepmask>>1;
			minL=((r1+shift-1)&stepmask);
			maxL=((r2-shift)&stepmask);

			while(minL>maxL)
			    {
				l++;
				shift=shift>>1;
				stepmask=stepmask>>1;
				minL=((r1+shift-1)&stepmask);
				maxL=((r2-shift)&stepmask);
			    }

			minL= minL>>(k-l+1);
			maxL= maxL>>(k-l+1);

			aboveL= (1<< l-1);
			return rec+ aboveL+minRevBits(l-1, minL, maxL);
		    }
	
		// next slot after t is within level l=lv(t)
		// return aboveL+ nextSlotIn(l-1, t-aboveL, minL, maxL); 
		// RECURSION !
		rec=rec+aboveL; // accumulates for tail recursion
		// settle the new values of parametres for recursion:
		k=l-1;
		t=t-aboveL;
		r1=minL;
		r2=maxL;
	    }
    }


    
    public static int nextIn(int k, int t, int r1, int r2) // new version
    // (t+min{d>0 : r1<= revBits( (t+d)mod 2^k   ) <=r2}) mod 2^k 
    // we assume 0<=r1<=r2< 2^k 

    {
        int twoToK=(1<<k); // 2^k
	int modMaskK= twoToK-1; // 2^k-1
        int t1,x1,x2, stepDivMask; 
	int twoToL=1;
	int stepLMinusOne=modMaskK;
	int tNext=((t+1)&modMaskK); 
        do 
	    {
                t1=tNext; 
		while(twoToL<twoToK && (t1&twoToL)==0) 
		    {
			twoToL=twoToL<<1;
                        stepLMinusOne=stepLMinusOne>>1;
		    }
                tNext=((t1+twoToL)&modMaskK);
                stepDivMask=((~stepLMinusOne) & modMaskK);
		x1=revBits(k,t1);
                x2= (x1 | stepDivMask );
	    }while( r1>x2 || r2<x1 || ((r1-x1+stepLMinusOne)&stepDivMask)>((r2-x1)&stepDivMask) ); 
	int s= (twoToK>>1); // 2^(k-1)
        while(x1<r1 || x1>r2)
	    {

		if(x1<r1) x1=x1+s;
		else x1=x1-s;
		s=s/2;
	    }
	return revBits(k, x1);
    }


    public static int naiveNextSlotIn(int k, int t, int r1, int r2)
    // (t+min{d>0 : r1<= revBits( (t+d)mod 2^k   ) <=r2}) mod 2^k 
    // we assume 0<=r1<=r2< 2^k 

    {
	naiveNSI++; // for statistics only

	int mask=(1<<k)-1; // 2^k-1 
	t=((t+1) & mask);  // (t+1) mod 2^k
	int r=revBits(k, t);
	while(r<r1 || r2<r)
	    {
		t=((t+1) & mask);
		r=revBits(k, t);
	    }
	return t;
    }

    public static int reverseNextSlotIn(int k, int t, int r1, int r2)
    // (t+min{d>0 : r1<= revBits( (t+d)mod 2^k   ) <=r2}) mod 2^k 
    // we assume 0<=r1<=r2< 2^k 

    {
	reverseNSI++; // for statistics only

	int n=(1<<k);
	int t1=revBits(k, r1);
	int globalMin=t1;
	int minAfter=(t1>t)? t1: n;
	for(int r=r1+1; r<=r2; r++)
	    {
		t1=revBits(k, r);
		if(t1<globalMin) globalMin=t1;
		if(t1>t && t1<minAfter) minAfter=t1;
	    }
	if(minAfter<n) return minAfter;
	else return globalMin; 

    }

    public static int nextSlotIn(int k, int t, int r1, int r2)
    // (t+min{d>0 : r1<= revBits( (t+d)mod 2^k   ) <=r2}) mod 2^k 
    // we assume 0<=r1<=r2< 2^k 
    {
	totalNSI++; // for statistics

	if(r1==r2) return revBits(k,r1);
      	int length=r2-r1;
        int lengthReverse= (1<<k)/length;
	if(length<150 || lengthReverse<150)
	    if(lengthReverse<=length-20)
		return naiveNextSlotIn(k,t,r1,r2);
	    else
		return reverseNextSlotIn(k,t,r1,r2);
	else
	    // return plogNextSlotIn(k,t,r1,r2);
	    return nextIn(k,t,r1,r2);
    }


    // for statistics:
    public static int plogNSI=0;
    public static int naiveNSI=0;
    public static int reverseNSI=0;
    public static int totalNSI=0;


}
