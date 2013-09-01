package com.twist.problemPool;

import java.util.List;

public class ProblemData {
	int numberOfTries;
	int numberOfTriesLeft;
	String evalString;
	String testString;
	List<String> answerFields;
	List<String> answerComments;
	boolean answerCorrect;
	
	List<Integer> fetchPrompt;
	boolean promptFetched;
	boolean fetchOnlyIfRight;
	boolean reFetchIfUpdate;
	List<Integer> dependencies;
}
