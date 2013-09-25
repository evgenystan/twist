package com.twist.problemPool;

public class PromptToPull
{
	public PromptToPull(String prompt, boolean enabled, boolean regenerate)
	{
		super();
		this.prompt = prompt;
		this.enabled = enabled;
		this.regenerate = regenerate;
		this.promptFetched = false;
		this.fetchOnlyIfRight = true;
		this.reFetchIfUpdate = false;
		this.after = null;
		this.before = null;
	}
	public PromptToPull(String prompt, boolean enabled)
	{
		super();
		this.prompt = prompt;
		this.enabled = enabled;
		this.regenerate = false;
		this.promptFetched = false;
		this.fetchOnlyIfRight = true;
		this.reFetchIfUpdate = false;
		this.after = null;
		this.before = null;
	}
	public PromptToPull(String prompt)
	{
		super();
		this.prompt = prompt;
		this.enabled = true;
		this.regenerate = false;
		this.promptFetched = false;
		this.fetchOnlyIfRight = true;
		this.reFetchIfUpdate = false;
		this.after = null;
		this.before = null;
	}
	public PromptToPull()
	{
		super();
		this.prompt = "";
		this.enabled = true;
		this.regenerate = false;
		this.promptFetched = false;
		this.fetchOnlyIfRight = true;
		this.reFetchIfUpdate = false;
		this.after = null;
		this.before = null;
	}
	public String prompt;
	public boolean enabled;
	public boolean regenerate;
	public boolean promptFetched;					//If one submission already occurred, check the reFetchIfUpdate before generation a new prompt
	public boolean fetchOnlyIfRight;				//fetch the prompts only if test set answerCorrect to true
	public boolean reFetchIfUpdate;				//regenerate the prompts and send them again to the user. Probably will discard any user input that was previously submitted
	public String after,before;
}
