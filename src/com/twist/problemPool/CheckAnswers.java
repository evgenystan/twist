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
    
    private String parseXMLTestResponse(String id, String str,ProblemData pData, ProblemDataPusher pPusher, KernelTalker kTalker)
    {
    	String outgoingXML;
		boolean inTestResponse		=false;
		boolean inEnablePrompt		=false;
		boolean inNewPrompts		=false;
		boolean inComment			=false;
		
		boolean pushList			=false;
		
		String 	promptIdAttr,
				correctAttr,
				acceptedAttr,
				fieldIdAttr,
				beforeAttr,
				afterAttr,
				refreshAttr,
				onlyifrightAttr;
		
		if (pData == null) {return "<?xml version='1.0'?><checkResponse><error>Internal Error: null pointer is supplied to parseXMLTestResponse</error></checkResponse>";}
		try
		{
			ProblemData fetchData;
			PromptToPull ptp;
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

			ArrayList<PromptToPull> tempList = pData.getFetchPrompt();
			int event = xmlr.getEventType();
			
			while (xmlr.hasNext())
			{
				switch(event)
				{
					case XMLStreamConstants.START_ELEMENT :
						//Strings
						if	   (xmlr.getLocalName().equals("testResponse")) 
						{
							inTestResponse = true;
							xtw.writeStartElement("checkResponse");
							promptIdAttr = xmlr.getAttributeValue(null, "promptId");
							if(promptIdAttr!=null) xtw.writeAttribute("promptId", promptIdAttr); else xtw.writeAttribute("promptId", id);
							
							correctAttr = xmlr.getAttributeValue(null,"correct");
							if(correctAttr!=null) {xtw.writeAttribute("correct", correctAttr);pData.setAnswerCorrect(Boolean.parseBoolean(correctAttr));}
							
							acceptedAttr = xmlr.getAttributeValue(null,"accepted");
							if(acceptedAttr!=null) {xtw.writeAttribute("accepted", acceptedAttr);pData.setAnswerCorrect(Boolean.parseBoolean(acceptedAttr));}
						}
						else if(xmlr.getLocalName().equals("comment")) 
						{
							inComment = true;
							xtw.writeStartElement("comment");
							fieldIdAttr = xmlr.getAttributeValue(null,"fieldId");
							if(fieldIdAttr!=null) xtw.writeAttribute("fieldId", fieldIdAttr);
							xtw.writeCharacters(xmlr.getElementText());
							xtw.writeEndElement();
						}
						//Lists
						else if(xmlr.getLocalName().equals("enablePrompt")) 
						{
							inEnablePrompt = true; 
							xtw.writeStartElement("enablePrompt");
							
							if((pData.getNumberOfTries()==0)||(pData.getNumberOfTriesLeft()>0))
							{
								xtw.writeStartElement("id");
								xtw.writeCharacters(pData.getId());
								xtw.writeEndElement();
							}
						}
						else if(xmlr.getLocalName().equals("newPrompts")||xmlr.getLocalName().equals("fetchPrompt")) {inNewPrompts = true; xtw.writeStartElement("newPrompts");}
						else if(xmlr.getLocalName().equals("id")) 
						{
							pushList = true;
							if(inNewPrompts)
							{// in newPrompts, get the id and generate the prompt
								ptp = new PromptToPull();
								refreshAttr = xmlr.getAttributeValue(null,"refresh");
								if(refreshAttr!=null) 
								{
									ptp.reFetchIfUpdate = Boolean.parseBoolean(refreshAttr);
									ptp.regenerate = Boolean.parseBoolean(refreshAttr);
								}
								
								onlyifrightAttr = xmlr.getAttributeValue(null,"onlyifright");
								if(onlyifrightAttr!=null) ptp.fetchOnlyIfRight = Boolean.parseBoolean(onlyifrightAttr);
								
								afterAttr = xmlr.getAttributeValue(null,"after");
								beforeAttr = xmlr.getAttributeValue(null,"before");
								if(afterAttr!=null) ptp.after = afterAttr;
								else
								{
									if(beforeAttr!=null) ptp.before = beforeAttr;
								}
								ptp.prompt = xmlr.getElementText();
								tempList.add(ptp);
							}
							else //Not in newPrompts just copy the id attribute
							{
								xtw.writeStartElement("id");
								promptIdAttr = xmlr.getAttributeValue(null,"promptId");
								if(promptIdAttr!=null) xtw.writeAttribute("promptId", promptIdAttr);
	
								afterAttr = xmlr.getAttributeValue(null,"after");
								if(afterAttr!=null) xtw.writeAttribute("after", afterAttr);
								else
								{
									beforeAttr = xmlr.getAttributeValue(null,"before");
									if(beforeAttr!=null) xtw.writeAttribute("before", beforeAttr);
								}
								xtw.writeCharacters(xmlr.getElementText());
								xtw.writeEndElement();
							}
						}
					break;
/*					case XMLStreamConstants.ATTRIBUTE :
						if		(xmlr.getLocalName().equals("promptId")) {promptIdAttr = xmlr.getAttributeValue(0);}
						else if	(xmlr.getLocalName().equals("correct")) {correctAttr = xmlr.getAttributeValue(0);}
						else if	(xmlr.getLocalName().equals("accepted")) {acceptedAttr = xmlr.getAttributeValue(0);}
						else if	(xmlr.getLocalName().equals("fieldId")) {fieldIdAttr = xmlr.getAttributeValue(0);}
						else if	(xmlr.getLocalName().equals("before")) {beforeAttr = xmlr.getAttributeValue(0);}
						else if	(xmlr.getLocalName().equals("after")) {afterAttr = xmlr.getAttributeValue(0);}
					break;*/
/*					case XMLStreamConstants.CHARACTERS:
						if(pushList)
						{
							if	(inEnablePrompt) 	
							{
							}
							else if	(inNewPrompts) 	
							{
							}
							pushList = false;
						}
					break;*/
					case XMLStreamConstants.END_ELEMENT:
						if		(xmlr.getLocalName().equals("enablePrompt"))	{inEnablePrompt = false;xtw.writeEndElement();}
						else if	(xmlr.getLocalName().equals("newPrompts"))		{inNewPrompts = false;xtw.writeEndElement();}
						else if (xmlr.getLocalName().equals("testResponse"))	{inTestResponse = false;}
					break;
				}
				event = xmlr.next();
			}
			xmlr.close();
		
			int m = tempList.size();
			if(m>0)
			{
				boolean fetchIt;
				xtw.writeStartElement("newPrompts");
				for(int i=0; i<m;i++)
				{
					fetchIt = false;
					ptp = tempList.get(i);
					if(ptp.fetchOnlyIfRight)
					{
						if(pData.isAnswerCorrect())
						{
							fetchIt = true;
						}
					}
					else fetchIt = true;
					if(fetchIt)
					{
						fetchData = ProblemPuller.generateProblem(ptp, pPusher, kTalker);
						xtw.writeStartElement("prompt");
						xtw.writeAttribute("promptId", ptp.prompt);
						if(ptp.after != null) xtw.writeAttribute("after", ptp.after);
						else if (ptp.before!=null) xtw.writeAttribute("before", ptp.before);
						xtw.writeCharacters(fetchData.getPrompt());
						xtw.writeEndElement();
					}
				}
				xtw.writeEndElement();
			}
			xtw.writeEndElement();//Close the checkResponse tag
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
		String id=request.getParameter("id");
		KernelTalker kTalker = new KernelTalker();
		
		// TODO Check if submission is possible. If submission is happened when numberOfTriesLeft is zero log the event and generate an XML string with error report
		
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
				response.setContentType("text/plain; charset=UTF-8");
				
				String 	args = "", 
						evalString = ProblemDataPusher.problemDataStorage.get(intId);
				String test = ProblemDataPusher.problemTestsStorage.get(intId);
				int i, 
					numberOfFields = Integer.parseInt(request.getParameter("total"));
	
				args = String.valueOf(numberOfFields);
				for (i = 1; i <= numberOfFields; i++)
				{
					args += ",\"" + request.getParameter("iField"+String.valueOf(i)) + "\"";
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
					response.setContentType("text/xml; charset=UTF-8");
					int	tryCounter = pData.getNumberOfTriesLeft();
							
					if((pData.getNumberOfTries()>0)&&(tryCounter == 0))
					{
						// TODO reply with an error message that number of tries is exhausted
					}
					else
					{
						String test = pData.getTestString();
						String key, args = "",fields;
						String[] value;
						
						int argcount = 0;
						
						for (Map.Entry<String, String[]> entry : pars.entrySet()) 
						{
						    key = entry.getKey();
						    if(key.startsWith("iField"))
						    {
								if(argcount>0) args +=", ";
								argcount++;
							    value = entry.getValue();
							    args += "\"" + key.substring(6) + "\" -> \"" + value[0] + "\"";
							    pData.addAnswerField(key.substring(6),value[0]);
						    }
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
								// TODO Check if depData.getAnswerFields() is nonempty, if it is, generate an XML string with error
								fields = depData.getAllAnswerFiledsForEvaluation();
								if(fields.length()>0)
								{
									if(argcount>0) args +=", ";
									argcount++;
								    args += fields;
								}
								if(!depData.getDependencies().isEmpty()) {stack.addAll(depData.getDependencies());}
							}
						}
						
						args = "{" + args + "}";
						
						PrintWriter out = response.getWriter();
						
						out.print(parseXMLTestResponse(id,kTalker.evaluateToString(test + args),pData,pPusher,kTalker));
//						out.print("<p>" + kTalker.evaluateToString(test+args) + "</p><p>"+ test + args + "</p>");
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
