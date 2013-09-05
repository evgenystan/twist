package com.twist.problemPool;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.twist.kernelUtilities.KernelTalker;

/**
 * Servlet implementation class CheckAnswers
 */
@WebServlet("/CheckAnswers")
public class CheckAnswers extends HttpServlet
{
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public CheckAnswers()
	{
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException
	{
		// TODO Auto-generated method stub
		response.setContentType("text/plain");

		int id = Integer.parseInt(request.getParameter("id"));
		String args = "", evalString = ProblemDataPusher.problemDataStorage.get(id);
		String test = ProblemDataPusher.problemTestsStorage.get(id);
		int i, numberOfFields = Integer.parseInt(request.getParameter("total"));

		for (i = 1; i <= numberOfFields; i++)
		{
			args += ",\"" + request.getParameter("f" + i) + "\"";
		}
		args = args.replaceAll("^[,]*", "{");
		args += "}";

		PrintWriter out = response.getWriter();
		KernelTalker kTalker = new KernelTalker();
		out.print(test + args + "\n");
		out.print(kTalker.executeCommand(test + args));
		out.close();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException
	{
		// TODO Auto-generated method stub
		response.setContentType("text/plain");

		int id = Integer.parseInt(request.getParameter("id"));
		ProblemData pData;
		ProblemDataPusher pPusher = new ProblemDataPusher();
		
		String args = "", evalString = ProblemDataPusher.problemDataStorage.get(id);
		String test = ProblemDataPusher.problemTestsStorage.get(id);
		int i, numberOfFields = Integer.parseInt(request.getParameter("total"));

		args = String.valueOf(numberOfFields);
		for (i = 1; i <= numberOfFields; i++)
		{
			args += ",\"" + request.getParameter("f" + i) + "\"";
		}
		args = "{" + args;
		args += "}";

		PrintWriter out = response.getWriter();
		KernelTalker kTalker = new KernelTalker();
		pData = pPusher.get(id);
		if(pData != null)
		{
			test = pData.getTestString();
		}
		// out.print(test+args+"\n");
		out.print("<p>" + kTalker.executeCommand(test + args) + "</p><p>"+ test + "</p>");
		out.close();
	}

}
