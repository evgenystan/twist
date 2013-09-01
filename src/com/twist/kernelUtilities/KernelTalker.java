package com.twist.kernelUtilities;

import com.wolfram.jlink.*;

public class KernelTalker 
{
	public static KernelLink globalMl;
	String status="Ok";
	
	public KernelTalker()
	{
	}

	public int checkKernelStatus()
	{
		if (globalMl == null) return 0;
		return 1;
	}
	
	public boolean establishLink()
	{
		if(globalMl==null)
		{
			String jLinkDir = "/Applications/Mathematica.app/SystemFiles/Links/JLink";
			System.setProperty("com.wolfram.jlink.libdir", jLinkDir);
			try {
				globalMl = MathLinkFactory.createKernelLink("-linkmode launch -linkname '\"/Applications/Mathematica.app/Contents/MacOS/MathKernel\" -mathlink'");
				globalMl.discardAnswer();
			} catch (MathLinkException e) {
				this.status ="Fatal error opening link: " + e.getMessage();
				globalMl=null;
				return false;
			}
			return true;
		}
		return true;
	}
	
	public void dropLink()
	{
		if(globalMl!=null)
		{
			globalMl.close();
			globalMl=null;
		}
	}
	
	public String executeCommand(String cmd)
	{
		String reply="";
		
		if (globalMl==null)
		{
			this.establishLink();
			reply="";
		}
		
		if (globalMl==null)
		{
			reply="can't connect to Mathematica";
		}
		else 
		{
			reply=reply+globalMl.evaluateToInputForm(cmd,0);
		}
		return reply;
	}
	
	public String evaluateToString(String cmd)
	{
		String reply="";
		
		if (globalMl==null)
		{
			this.establishLink();
			reply="";
		}
		
		if (globalMl==null)
		{
			reply="can't connect to Mathematica";
		}
		else 
		{
			try
			{
				globalMl.evaluate(cmd);
//				globalMl.discardAnswer();
				globalMl.waitForAnswer();
				
				reply=globalMl.getString();
			}
			catch(MathLinkException e)
			{
				reply="Exception occured" + e.getMessage();
			}
		}
		return reply;
	}

}
