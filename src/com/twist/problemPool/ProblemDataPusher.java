package com.twist.problemPool;

import java.util.HashMap;

public class ProblemDataPusher
{
	static HashMap<Integer,String> problemDataStorage = new HashMap<Integer,String>();
	static HashMap<Integer,String> problemTestsStorage = new HashMap<Integer,String>();
	static private HashMap<Integer,ProblemData> problemStorage = new HashMap<Integer,ProblemData>();
	
	public void push(int key, ProblemData pData)
	{
		problemStorage.put(key, pData);
	}
	
	public ProblemData get(int key)
	{
		return problemStorage.get(key);
	}
}
