package com.twist.problemPool;

import java.util.HashMap;

public class ProblemDataPusher
{
	static HashMap<Integer,String> problemDataStorage = new HashMap<Integer,String>();
	static HashMap<Integer,String> problemTestsStorage = new HashMap<Integer,String>();
	static private HashMap<String,ProblemData> problemStorage = new HashMap<String,ProblemData>();
	
	public void push(String key, ProblemData pData)
	{
		problemStorage.put(key, pData);
	}
	
	public ProblemData get(String key)
	{
		return problemStorage.get(key);
	}
}
