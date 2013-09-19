package com.twist.problemPool;

public class PromptToPull
{
	public PromptToPull(String prompt, boolean enabled, boolean regenerate)
	{
		super();
		this.prompt = prompt;
		this.enabled = enabled;
		this.regenerate = regenerate;
	}
	public PromptToPull(String prompt, boolean enabled)
	{
		super();
		this.prompt = prompt;
		this.enabled = enabled;
		this.regenerate = false;
	}
	public PromptToPull(String prompt)
	{
		super();
		this.prompt = prompt;
		this.enabled = true;
		this.regenerate = false;
	}
	public PromptToPull()
	{
		super();
		this.prompt = "";
		this.enabled = true;
		this.regenerate = false;
	}
	public String prompt;
	public boolean enabled;
	public boolean regenerate;
}
