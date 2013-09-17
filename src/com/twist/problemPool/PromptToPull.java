package com.twist.problemPool;

public class PromptToPull
{
	public PromptToPull(String prompt, boolean enabled)
	{
		super();
		this.prompt = prompt;
		this.enabled = enabled;
	}
	public PromptToPull(String prompt)
	{
		super();
		this.prompt = prompt;
		this.enabled = true;
	}
	public PromptToPull()
	{
		super();
		this.prompt = "";
		this.enabled = true;
	}
	public String prompt;
	public boolean enabled;
}
