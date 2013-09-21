package com.twist.problemPool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProblemData
{
	public ProblemData()
	{
		super();
		this.XMLString = null;
		this.enabled = true;
		this.generated = false;
		this.id = null;
		this.numberOfTries = 0;
		this.numberOfTriesLeft = 0;
		this.evalString = "";
		this.testString = "";
		this.optionsStrings = new HashMap<String,String>();;
		this.answerFields = new HashMap<String,String>();
		this.answerComments = new HashMap<String,String>();
		this.answerCorrect = false;
		this.fetchPrompt = new ArrayList<PromptToPull>();
		this.supplyPrompts = new ArrayList<PromptToPull>();
		this.dependencies = new ArrayList<String>();
	}

	public boolean isGenerated()
	{
		return generated;
	}

	public void setGenerated(boolean generated)
	{
		this.generated = generated;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public String getXMLString()
	{
		return XMLString;
	}

	public void setXMLString(String xMLString)
	{
		XMLString = xMLString;
	}

	public String getId()
	{
		return id;
	}

	public void setId(String id)
	{
		this.id = id;
	}

	public HashMap<String, String> getOptionsStrings()
	{
		return optionsStrings;
	}
	
	public String getAllOptionsStrings()
	{
		String temp, options="";
		int stringcount = 0;
		
		if(!optionsStrings.isEmpty())
		{
			Iterator<String> it = optionsStrings.values().iterator();
			
			while(it.hasNext())
			{
				temp =it.next();
				if(temp.length()>0)
				{
					if(stringcount>0) options +=", "+temp;
					else options = temp;
					stringcount++;
				}
			}
		}
		
		if(!this.dependencies.isEmpty())
		{
			ProblemData depData;
			ProblemDataPusher pPusher = new ProblemDataPusher();
			
			for(int i = 0, m = this.dependencies.size();i<m;i++)
			{
				depData = pPusher.get(this.dependencies.get(i));
				if(depData != null)
				{
					temp = depData.getAllAnswerFiledsForEvaluation();
					if(temp.length()>0)
					{
						if(stringcount>0) options +=", "+temp;
						else options = temp;
						stringcount++;
					}
				}
			}
		}
		return options;
	}

	public void setOptionsStrings(HashMap<String, String> optionsStrings)
	{
		if(!optionsStrings.isEmpty()) this.optionsStrings.putAll(optionsStrings);
	}

	/* Stores optionsString for future evaluations. A string is trimmed of starting '{' and trailing '}' symbols
	 * */
	public void addOptionsString(String key, String optionsString)
	{
		Matcher m = optsPattern.matcher(optionsString);
		
		this.optionsStrings.put(key, m.replaceAll(""));
	}

	public ArrayList<PromptToPull> getSupplyPrompts()
	{
		return supplyPrompts;
	}

	public void setSupplyPrompts(ArrayList<PromptToPull> supplyPrompts)
	{
		this.supplyPrompts = supplyPrompts;
	}

	public void resetSupplyPrompts()
	{
		this.supplyPrompts.clear();
	}

	public void addSupplyPrompts(PromptToPull supplyPrompt)
	{
		this.supplyPrompts.add(supplyPrompt);
	}

	public int getNumberOfTries()
	{
		return numberOfTries;
	}

	public void setNumberOfTries(int numberOfTries)
	{
		this.numberOfTries = numberOfTries;
		this.numberOfTriesLeft = numberOfTries;
	}

	public int getNumberOfTriesLeft()
	{
		return numberOfTriesLeft;
	}

	public void setNumberOfTriesLeft(int numberOfTriesLeft)
	{
		this.numberOfTriesLeft = numberOfTriesLeft;
	}

	public String getEvalString()
	{
		return evalString;
	}

	public void setEvalString(String evalString)
	{
		this.evalString = evalString;
	}

	public String getTestString()
	{
		return testString;
	}

	public void setTestString(String testString)
	{
		this.testString = testString;
	}

	public boolean isAnswerCorrect()
	{
		return answerCorrect;
	}

	public void setAnswerCorrect(boolean answerCorrect)
	{
		this.answerCorrect = answerCorrect;
	}

	public ArrayList<PromptToPull> getFetchPrompt()
	{
		return fetchPrompt;
	}

	public void resetFetchPrompt()
	{
		this.fetchPrompt.clear();
	}

	public void setFetchPrompt(ArrayList<PromptToPull> fetchPrompt)
	{
		this.fetchPrompt = fetchPrompt;
	}

	public void addFetchPrompt(PromptToPull fetchPrompt)
	{
		this.fetchPrompt.add(fetchPrompt);
	}

	public ArrayList<String> getDependencies()
	{
		return dependencies;
	}

	public void resetDependencies()
	{
		this.dependencies.clear();
	}

	public void setDependencies(ArrayList<String> dependencies)
	{
		this.dependencies.addAll(dependencies);
	}

	public void addDependency(String dependency)
	{
		this.dependencies.add(dependency);
	}

	public HashMap<String, String> getAnswerFields()
	{
		return answerFields;
	}

	public String getSpecificAnswerField(int i)
	{
		return answerFields.get(i);
	}
	
	public String getAllAnswerFiledsForEvaluation()
	{
		String fieldKey,fieldValue, args = "";
		int argcount = 0;
		
		for (Map.Entry<String, String> entry : this.answerFields.entrySet()) 
		{
			if(argcount > 0) args +=", ";
		    fieldKey = entry.getKey();
		    fieldValue = entry.getValue();
		    args += "\"" + fieldKey + "\" -> \"" + fieldValue + "\"";
		}
		return args;
	}

	public void setAnswerFields(HashMap<String, String> answerFields)
	{
		this.answerFields.putAll(answerFields);
	}

	public void addAnswerField(String key, String value)
	{
		this.answerFields.put(key, value);
	}

	public HashMap<String, String> getAnswerComments()
	{
		return answerComments;
	}

	public String getSpecificAnswerComment(int i)
	{
		return answerComments.get(i);
	}

	public void setAnswerComments(HashMap<String, String> answerComments)
	{
		this.answerComments.putAll(answerComments);
	}

	public void addAnswerComment(String key, String value)
	{
		this.answerComments.put(key, value);
	}
	
	public String getPrompt()
	{
		return prompt;
	}

	public void setPrompt(String prompt)
	{
		this.prompt = prompt;
	}

	private static final Pattern optsPattern = Pattern.compile("^\\s*\\{|\\}\\s*$");
	
	boolean enabled;
	boolean generated;
	String XMLString;
	String id;
	String prompt;			//Stores the html code that will be sent to a user
	int numberOfTries;		//Number of attempts at the answer. 0 = infinite
	int numberOfTriesLeft;	//Counts backward to zero. Once zero, no submissions will be tested and recorded
	String evalString;		//String to evaluate before tests are performed
	String testString;		//Test command evaluated against the inputfields.
	HashMap<String, String> optionsStrings;	//Storage for problem setup to pass to subordinate questions. Mathematica format is expected, i.e. "func->Exp[2x]"
	HashMap<String, String> answerFields;	//Storage for inputfields. Keyed by fieldname
	HashMap<String, String> answerComments;	//Storage for comments generated by tests. Keyed by fieldname. Will be sent back to the user
	boolean answerCorrect;					//If testString is nonempty, this will be set after tests are run

	ArrayList<PromptToPull> fetchPrompt;			//List of prompts to fetch and send to the user after user submitted an answer
	ArrayList<PromptToPull> supplyPrompts;		//List of prompts to supply together with this prompt
	ArrayList<String> dependencies;			//collect answerFields input from the prompts listed before evaluating the tests
}
