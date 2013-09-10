package com.twist.problemPool;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import com.twist.kernelUtilities.KernelTalker;

/**
 * Servlet implementation class CheckAnswers
 */
@WebServlet("/CheckAnswers")
public class CheckAnswers extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public CheckAnswers() {
        super();
        // TODO Auto-generated constructor stub
    }
    
    private String getDependencies(int id,KernelTalker kTalker)
    {
    	int i;
    	ProblemData pData = ProblemDataPusher.problemDataStorage.get(id);
    	String args;
    	
		String evalString=pData.evalString;
		
		if((evalString!=null)&&(evalString.length()>0))
		{
			kTalker.executeCommand(evalString);
		}
		
		if((pData.dependencies!=null)&&(pData.dependencies.size()>0))
		{
			args="{"+String.valueOf(pData.answerFields.size()+pData.dependencies.size());
			
			for (i=0;i<pData.dependencies.size();i++)
			{
				args += "," + getDependencies(pData.dependencies.get(i),kTalker);
			}
		}
		else
		{
			args="{"+String.valueOf(pData.answerFields.size());
		}
		
		for(i=0;i<pData.answerFields.size();i++)
		{
			args += ",\"" + pData.answerFields.get(i) + "\"";
		}

		args += "}";
		return args;
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.setContentType("text/plain");
		
		int id=Integer.parseInt(request.getParameter("id"));
		String args="",evalString=ProblemDataPusher.problemEvalStringStorage.get(id);
		String test=ProblemDataPusher.problemTestsStorage.get(id);
		int i,numberOfFields=Integer.parseInt(request.getParameter("total"));

		for(i=1;i<=numberOfFields;i++)
		{
 			args+=",\"" + request.getParameter("f"+i)+"\"";
 		}
		args = args.replaceAll("^[,]*","{");
		args+="}";

		PrintWriter out = response.getWriter();
		KernelTalker kTalker=new KernelTalker();
		out.print(test+args+"\n");
		out.print(kTalker.executeCommand(test+args));
		out.close();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		int id=Integer.parseInt(request.getParameter("id"));
		ProblemData pData = ProblemDataPusher.problemDataStorage.get(id);
		String args="";
		String evalString=null;
		String test=null;
		int i,numberOfFields=Integer.parseInt(request.getParameter("total"));
		TestResponse reply=null;
		
		KernelTalker kTalker=new KernelTalker();
		PrintWriter out = response.getWriter();

		if(pData != null)
		{
			response.setContentType("text/xml");
			XMLOutputFactory xof =  XMLOutputFactory.newInstance();
			if(pData.numberOfTries>0)
			{
				if(pData.numberOfTriesLeft>0)
				{
					pData.numberOfTriesLeft--;
				}
				else
				{
					out.print("<?xml version='1.0'?><!DOCTYPE TestResponse><response><error>Incorrect Submission: Number of tires left is zero</error></response>");
					out.close();
					return;
				}
			}
			try
			{
				StringWriter stringBuffer=new StringWriter();
				XMLStreamWriter xtw = xof.createXMLStreamWriter(stringBuffer);
				
				evalString=pData.evalString;
				test = pData.testString;
				
				if((evalString!=null)&&(evalString.length()>0))
				{
					kTalker.executeCommand(evalString);
				}
							
				if((pData.dependencies!=null)&&(pData.dependencies.size()>0))
				{
					args="{"+String.valueOf(numberOfFields+pData.dependencies.size());
					
					for (i=0;i<pData.dependencies.size();i++)
					{
						args += "," + getDependencies(pData.dependencies.get(i),kTalker);
					}
				}
				else
				{
					args="{"+String.valueOf(numberOfFields);
				}
				if(numberOfFields>0)
					pData.answerFields= new ArrayList<String>();
				
				for(i=0;i<numberOfFields;i++)
				{
					pData.answerFields.add(i, request.getParameter("f"+(i+1)));
					args += ",\"" + request.getParameter("f"+(i+1)) + "\"";
				}
				args+="}";
				
				reply = kTalker.evaluateToResponse(test+args);
				
				if(reply.correct)
					pData.answerCorrect=true;
				else
					pData.answerCorrect=false;
				
				if(reply.fatalError)
				{
					out.print("<?xml version='1.0'?><!DOCTYPE TestResponse><response><error>" + reply.errorMsg + "</error></response>");
					out.close();
					return;
				}
				
				xtw.writeStartDocument("utf-8","1.0");
//				xtw.writeDTD("<!DOCTYPE TestResponse>");
				xtw.writeStartElement("response");
				xtw.writeAttribute("correct", (reply.correct)?"true":"false");
				xtw.writeAttribute("promptId", String.valueOf(id));
				xtw.writeAttribute("enableSubmit", (pData.numberOfTries>0)?((pData.numberOfTriesLeft>0)?"true":"false"):"true");
				if((reply.comments!=null)&&(reply.comments.size()>0))
				{
					pData.answerComments = reply.comments;
					xtw.writeStartElement("comments");
					for(i=0;i<reply.comments.size();i++)
					{
						xtw.writeStartElement("field");
						xtw.writeAttribute("id", String.valueOf(reply.comments.get(i).id));
						xtw.writeCharacters(reply.comments.get(i).comment);
						xtw.writeEndElement();
					}
					xtw.writeEndElement();
				}
				if((pData.fetchPrompt!=null)&&(pData.fetchPrompt.size()>0))
				{
					if(!pData.fetchOnlyIfRight||pData.answerCorrect)
					{
						if((!pData.promptFetched)||(pData.reFetchIfUpdate))
						{
							pData.promptFetched = true;
							PromptToFetch ptf;
							xtw.writeStartElement("prompts");
							for(i=0;i<pData.fetchPrompt.size();i++)
							{
								ptf=pData.fetchPrompt.get(i);
								if((!pData.promptFetched)||(ptf.update))
								{
									xtw.writeStartElement("prompt");
									xtw.writeAttribute("id", String.valueOf(ptf.id));
									if(ptf.insertAfter>0)
										xtw.writeAttribute("insertAfter", String.valueOf(ptf.insertAfter));
									else
										if (ptf.insertBefore>0)
											xtw.writeAttribute("insertBefore", String.valueOf(ptf.insertBefore));
									
									xtw.writeCharacters("<p>Here comes another prompt id = " + ptf.id + "</p>");
									xtw.writeEndElement();
								}
							}
							xtw.writeEndElement();
						}
					}
				}
				xtw.writeEndElement();
				xtw.writeEndDocument();
				ProblemDataPusher.problemDataStorage.put(id, pData);
				xtw.flush();
				out.print(stringBuffer.toString());
				xtw.close();
				out.close();
			}
			catch(XMLStreamException e)
			{
				out.print("<?xml version='1.0'?><!DOCTYPE TestResponse><response><error>Can't write an XML output</error></response>");
				out.close();
			}
		}
		else
		{
			response.setContentType("text/plain");
	
			evalString=ProblemDataPusher.problemEvalStringStorage.get(id);
			test=ProblemDataPusher.problemTestsStorage.get(id);
			args = "{" +String.valueOf(numberOfFields);
			for(i=1;i<=numberOfFields;i++)
			{
	 			args+=",\"" + request.getParameter("f"+i)+"\"";
	 		}
			args+="}";
			
	//		out.print(test+args+"\n");
			out.print("<p>" + kTalker.executeCommand(test+args) + "</p><p>" + test + "</p>");
			out.close();
		}
	}

}
