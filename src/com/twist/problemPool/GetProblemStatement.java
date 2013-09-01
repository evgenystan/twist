package com.twist.problemPool;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.twist.kernelUtilities.*;

/**
 * Servlet implementation class GetProblemStatement
 */
@WebServlet("/GetProblemStatement")
public class GetProblemStatement extends HttpServlet {
	private static final long serialVersionUID = 1L;
    
    static KernelTalker kTalker;
    
    static 
    {
    	kTalker = new KernelTalker();
    }
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetProblemStatement() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
 		int problemId=Integer.parseInt(request.getParameter("id"));
 		String cmd=null;

		PrintWriter out = response.getWriter();
		out.print(kTalker.executeCommand(cmd));
		out.close();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.setContentType("text/plain");

 		int problemId=Integer.parseInt(request.getParameter("id"));

		PrintWriter out = response.getWriter();
//		out.println("doPost command = "+cmd+"<br/>");
		out.print(kTalker.executeCommand(""));
		out.close();
	}

}
