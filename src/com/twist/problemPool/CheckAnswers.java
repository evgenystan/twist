package com.twist.problemPool;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
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
    
    private String parseXMLTestResponse(String id, String str,ProblemData pData)
    {
    	String outgoingXML;
		boolean inPrompt			=false;
		boolean inNumberOfTries		=false;
		boolean inNumberOfTriesLeft	=false;
		boolean inOptionsString		=false;
		boolean inEvalString		=false;
		boolean inTestString		=false;
		boolean inAnswerFields		=false;
		boolean inAnswerComments	=false;
		boolean inAnswerCorrect		=false;
		boolean inFetchPrompt		=false;
		boolean inSupplyPrompts		=false;
		boolean inPromptFetched		=false;
		boolean inFetchOnlyIfRight	=false;
		boolean inReFetchIfUpdate	=false;
		boolean inDependencies		=false;
		
		boolean pushList			=false;
		
		if (pData == null) {return "<?xml version='1.0'?><checkResponse><error>Internal Error: null pointer is supplied to parseXMLTestResponse</error></checkResponse>";}
		try
		{
			XMLOutputFactory xof =  XMLOutputFactory.newInstance();
			XMLInputFactory xif = XMLInputFactory.newInstance();
			xif.setProperty(XMLInputFactory.IS_REPLACING_ENTITY_REFERENCES, Boolean.TRUE);
			xif.setProperty(XMLInputFactory.IS_COALESCING, Boolean.TRUE);

			StringReader sr = new StringReader(str);
			XMLStreamReader xmlr = xif.createXMLStreamReader(sr);

			StringWriter stringBuffer=new StringWriter();
			XMLStreamWriter xtw = xof.createXMLStreamWriter(stringBuffer);

			xtw.writeStartDocument("utf-8","1.0");
//			xtw.writeDTD("<!DOCTYPE TestResponse>");
			xtw.writeStartElement("checkResponse");
			xtw.writeAttribute("promptId", id);

			int event = xmlr.getEventType();
			
			while (xmlr.hasNext())
			{
				switch(event)
				{
					case XMLStreamConstants.START_ELEMENT :
						//Strings
						if	   (xmlr.getLocalName().equals("prompt")) {inPrompt = true;}
						else if(xmlr.getLocalName().equals("numberOfTries")) {inNumberOfTries = true;}
						else if(xmlr.getLocalName().equals("evalString")) {inEvalString = true;}
						else if(xmlr.getLocalName().equals("testString")) {inTestString = true;}
						//Lists
						else if(xmlr.getLocalName().equals("optionsString")) {inOptionsString = true;}
						else if(xmlr.getLocalName().equals("fetchPrompt")) {inFetchPrompt = true; pData.resetFetchPrompt();}
						else if(xmlr.getLocalName().equals("supplyPrompts")) {inSupplyPrompts = true; pData.resetSupplyPrompts();}
						else if(xmlr.getLocalName().equals("dependencies")) {inDependencies = true; pData.resetDependencies();}
						else if(xmlr.getLocalName().equals("id")) {pushList = true;}
						//Boolean
						else if(xmlr.getLocalName().equals("fetchOnlyIfRight")) {inFetchOnlyIfRight = true;}
						else if(xmlr.getLocalName().equals("reFetchIfUpdate")) {inReFetchIfUpdate = true;}
					break;
					case XMLStreamConstants.CHARACTERS:
						if(pushList)
						{
							if		(inFetchPrompt) 	{pData.addFetchPrompt(xmlr.getText());}
							else if	(inSupplyPrompts) 	{pData.addSupplyPrompts(xmlr.getText());}
							else if	(inDependencies) 	{pData.addDependency(xmlr.getText());}
							
							pushList = false;
						}
						//Technically, optionsString is a map list, but we only support one string per problem generator. Other strings may be inserted by other problems.
						else if (inOptionsString) 	{inOptionsString = false;	pData.addOptionsString(id, xmlr.getText());}
						else if (inPrompt) 			{inPrompt = false;			pData.setPrompt(xmlr.getText());}
						else if (inNumberOfTries) 	{inNumberOfTries = false;	pData.setNumberOfTries(Integer.parseInt(xmlr.getText()));}
						else if (inTestString)		{inTestString = false;		pData.setTestString(xmlr.getText());}
						else if (inFetchOnlyIfRight){inFetchOnlyIfRight = false;pData.setFetchOnlyIfRight(Boolean.parseBoolean(xmlr.getText()));}
						else if (inReFetchIfUpdate)	{inReFetchIfUpdate = false;	pData.setReFetchIfUpdate(Boolean.parseBoolean(xmlr.getText()));}
					break;
					case XMLStreamConstants.END_ELEMENT:
						if	   (xmlr.getLocalName().equals("fetchPrompt")) {inFetchPrompt = false;}
						else if(xmlr.getLocalName().equals("supplyPrompts")) {inSupplyPrompts = false;}
						else if(xmlr.getLocalName().equals("dependencies")) {inDependencies = false;}
					break;
				}
				event = xmlr.next();
			}
		
			xtw.writeEndElement();
			xtw.writeEndDocument();
			xtw.flush();
			outgoingXML = stringBuffer.toString();
			xtw.close();
		}
		catch (FactoryConfigurationError e)
		{
			outgoingXML = "<?xml version='1.0'?><checkResponse><error>Can't write an XML output:" + e.getMessage() + "</error></checkResponse>";
			e.printStackTrace();
		}
		catch (XMLStreamException e) 
		{
			outgoingXML = "<?xml version='1.0'?><checkResponse><error>Can't write an XML output:" + e.getMessage() + "</error></checkResponse>";
			e.printStackTrace();
		}
		return outgoingXML;
    }
    
    private String getDependencies(String id,KernelTalker kTalker)
    {
    	int i;
    	
    	ProblemDataPusher pPusher = new ProblemDataPusher();
    	ProblemData pData = pPusher.get(id);
    	
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
		String 	args="";
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
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		response.setContentType("text/plain");
		String id=request.getParameter("id");
		KernelTalker kTalker = new KernelTalker();
		
		// TODO Check if submission is possible. If submission is happened when numberOfTriesLeft is zero log the event
		
		if (id == null)
		{
		}
		else
		{
			ProblemDataPusher pPusher = new ProblemDataPusher();
			ProblemData pData = pPusher.get(id);
			
			if(pData == null)
			{
				int intId = Integer.parseInt(request.getParameter("id"));
				
				String 	args = "", 
						evalString = ProblemDataPusher.problemDataStorage.get(intId);
				String test = ProblemDataPusher.problemTestsStorage.get(intId);
				int i, 
					numberOfFields = Integer.parseInt(request.getParameter("total"));
	
				args = String.valueOf(numberOfFields);
				for (i = 1; i <= numberOfFields; i++)
				{
					args += ",\"" + request.getParameter(String.valueOf(i)) + "\"";
				}
				args = "{" + args;
				args += "}";
	
				PrintWriter out = response.getWriter();
				out.print(kTalker.evaluateToString(test + args));
//				out.print(test+args+"\n");
//				out.print("<p>" + kTalker.executeCommand(test + args) + "</p><p>"+ test + "</p>");
				out.close();
			}
			else
			{
				Map<String,String[]> pars = request.getParameterMap();
				if(pData != null)
				{
					response.setContentType("text/xml");
					int	tryCounter = pData.getNumberOfTriesLeft();
							
					if(tryCounter == 0)
					{
						
					}
					else
					{
						XMLInputFactory xif = XMLInputFactory.newInstance();
						xif.setProperty(XMLInputFactory.IS_REPLACING_ENTITY_REFERENCES, Boolean.TRUE);
						xif.setProperty(XMLInputFactory.IS_COALESCING, Boolean.TRUE);

						String test = pData.getTestString();
						String key, args = "",fieldKey,fieldValue;
						String[] value;
						
						int argcount = 0;
						
						for (Map.Entry<String, String[]> entry : pars.entrySet()) 
						{
						    key = entry.getKey();
						    if((key.equals("total"))||(key.equals("id")))
						    {
						    	continue;
						    }
							if(argcount>0) args +=", ";
							argcount++;
						    value = entry.getValue();
						    args += "\"" + key + "\" -> \"" + value[0] + "\"";
						    pData.addAnswerField(key,value[0]);
						}
						
						if(!pData.getDependencies().isEmpty())
						{
							ArrayList<String> stack = new ArrayList<String>();
							ProblemData depData;
							String depId;
							
							stack.addAll(pData.getDependencies());
							
							while(!stack.isEmpty())
							{
								depId = stack.remove(0);
								depData = pPusher.get(depId);
								
								for (Map.Entry<String, String> entry : depData.getAnswerFields().entrySet()) 
								{
									if(argcount>0) args +=", ";
									argcount++;
								    fieldKey = entry.getKey();
								    fieldValue = entry.getValue();
								    args += "\"" + fieldKey + "\" -> \"" + fieldValue + "\"";
								}
								if(!depData.getDependencies().isEmpty()) {stack.addAll(depData.getDependencies());}
							}
						}
						
						args = "{" + args + "}";
						
						PrintWriter out = response.getWriter();
						
						out.print(parseXMLTestResponse(id,kTalker.evaluateToString(test + args),pData));
	//					out.print("<p>" + kTalker.evaluateToString(test+args) + "</p><p>"+ test + args + "</p>");
						out.close();
					}
				}
			}
		}

/*
		
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
		}*/
	}

}
