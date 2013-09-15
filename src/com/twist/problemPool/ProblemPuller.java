package com.twist.problemPool;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import com.twist.kernelUtilities.KernelTalker;

public class ProblemPuller
{
	KernelTalker kTalker = new KernelTalker();
	ProblemDataPusher pPusher = new ProblemDataPusher();
	
	public String getProblemStatementAsString(int id)
	{
		String reply=null,temp=null,test=null, evalString = null;
		ProblemData pData;
		
		kTalker.establishLink();
		
		switch(id)
		{
			case 001 : 
				kTalker.executeCommand("ClearAll[a, x, fList, func, degree, center, evalString];a = RandomInteger[{1, 4}];fList = RandomChoice[{    {Exp[a x^2], {3, 4, 5, 6}, {0, 1, -1, 2, -2}},    {Log[a x + 1], {3, 4, 5, 6}, {0, 1, 2}}    }   ];func = fList[[1]];degree = RandomChoice[fList[[2]]];center = RandomChoice[fList[[3]]];");
				pData = new ProblemData();
				pData.setNumberOfTries(6);
				evalString=kTalker.executeCommand("evalString =   \"ClearAll[a,x,func,degree,center];a=\" <> ToString[a] <> \";func=\" <>    ToString[InputForm[func]] <> \";degree=\" <> ToString[degree] <>    \";center=\" <> ToString[center] <> \";\"");
				pData.setEvalString(evalString);
				String degree=kTalker.executeCommand("degree");

				reply = "<div><h2>Taylor Series</h2></div><div><p>Compute the Taylor polynomial <script type=\"math/tex\">P_"+ degree +"(x)</script> of order "+ degree +" for the function <script type=\"math/tex\">"+ kTalker.executeCommand("TeXForm[func]") +"</script> about the point <script type=\"math/tex\">x_0="+ kTalker.executeCommand("center") +"</script></p></div>";
				reply = reply + "<div promptId = '1' class='inputbox'><script type=\"math/tex\">P_"+ degree +"(x)=</script>&nbsp;<span class=\"inputfield\" style=\"font-size: 100%; font-family: STIXGeneral-Regular; \" contenteditable=\"true\" fieldId = \"1\"></span>&nbsp;<input promptId = '1' class=\"evalButton\" type=\"button\" value=\"Submit\" />&nbsp;<span id='resultsField1'></span></div>";
				pData.setPrompt(reply);

				test = "Catch[If[Not[SyntaxQ[#2]], Throw[\"Syntax Error\"]];   If[PossibleZeroQ[Simplify[" + kTalker.executeCommand("Normal[Series[func, {x, center, degree}]]") + " - ToExpression[StringReplace[StringReplace[#2, WordBoundary ~~ \"e\" ~~ WordBoundary -> \"E\"], {WordBoundary ~~ \"x\" ~~ Whitespace ~~ \"(\" -> \"x*(\", WordBoundary ~~ \"x(\" -> \"x*(\", WordBoundary ~~ \"E\" ~~ Whitespace ~~ \"(\" -> \"E*(\", WordBoundary ~~ \"E(\" -> \"E*(\"}],TraditionalForm]]], Throw[\"&#10003;\"], Throw[\"&#10007;\"]] ] &@@";
				pData.setTestString(test);
				
				pPusher.push("1",pData);
				ProblemDataPusher.problemTestsStorage.put(id, test);
				break;
			case 002:
				kTalker.executeCommand("ClearAll[A, u, v, rank];Block[{a, n, n1, n2},rank = RandomInteger[] + 1;u = 0;v = RandomSample[{-5, -4, -3, -2, -1, 0, 1, 2, 3, 4, 5, 1/2, 1/3, 1/4, 1/5, -(1/2), -(1/3), -(1/4), -(1/5)}, 3];If[rank == 1,u = RandomSample[{-5, -4, -3, -2, -1, 1, 2, 3, 4, 5, 1/2, 1/3, 1/4,1/5, -(1/2), -(1/3), -(1/4), -(1/5)}, 3];n = Cross[v, u];A = Table[n a, {a, RandomChoice[{-5, -4, -3, -2, -1, 1, 2, 3, 4, 5, 1/2, 1/3, 1/4, 1/5, -(1/2), -(1/3), -(1/4), -(1/5)}, 3]}];,{n1, n2} = NullSpace[{v}];A = Table[n1 a[[1]] + n2 a[[2]], {a, RandomChoice[{-5, -4, -3, -2, -1, 1, 2, 3, 4, 5, 1/2, 1/3, 1/4, 1/5, -(1/2), -(1/3), -(1/4), -(1/5)}, {3, 2}]}];];];");
				
				evalString=kTalker.executeCommand("evalString = \"ClearAll[A,u,v,rank];A=\" <> ToString[InputForm[A]] <> \";u=\" <> ToString[InputForm[u]] <> \";v=\" <> ToString[InputForm[v]] <> \";rank=\" <> ToString[InputForm[rank]] <> \";\"");
				pData = new ProblemData();
				pData.setEvalString(evalString);
				
				temp = "<div promptId = '2'><h2>Solving Linear Systems</h2></div><div><p>Let <script type=\"math/tex\">A</script> be a 3&times;3 matrix</p><p><div style='margin-left:auto;margin-right:auto'><script type=\"math/tex\">" + kTalker.executeCommand("TeXForm[MatrixForm[A]]") + "</script></div></p></div>";
				pData.setPrompt(temp);
				pPusher.push("2",pData);
				
				reply = temp;

				pData = new ProblemData();
				pData.setEvalString(evalString);
				temp = "<div promptId = '3'><h2>Question 1</h2><p>Compute the rank of the matrix <script type=\"math/tex\">A</script></p><div promptId = '3' class='inputbox'><script type=\"math/tex\">\\mathrm{rank}(A)=</script>&nbsp;<span class=\"inputfield\" style=\"font-size: 100%; font-family: STIXGeneral-Regular; \" contenteditable=\"true\" fieldId = \"1\"></span>&nbsp;<input promptId = '3' class=\"evalButton\" type=\"button\" value=\"Submit\" />&nbsp;<span id='resultsField3'></span></div></div>";
				pData.setPrompt(temp);
				reply = reply + temp;

				test = "Function[{total,rank},Catch[If[Not[SyntaxQ[rank]],Throw[\"Syntax Error\"]];Module[{inRank},inRank = ToExpression[rank];If[Not[IntegerQ[inRank]], Throw[\"Rank must be an integer.\"]];If[inRank < 0, Throw[\"Rank must be a non-negative integer.\"]];If[PossibleZeroQ[" + kTalker.executeCommand("rank") + "-inRank], Throw[\"&#10003;\"], Throw[\"&#10007;\"]]]]] @@";
				pData.setTestString(test);
				pPusher.push("3",pData);
				ProblemDataPusher.problemTestsStorage.put(3, test);

				pData = new ProblemData();
				pData.setEvalString(evalString);
				temp = "<div promptId = '4'><h2>Question 2</h2><p>Compute the appropriate number of linearly independent vectors <script type=\"math/tex\">\\bar{v}_i</script> that generate the solution set for the homogeneous system <script type=\"math/tex\">A\\cdot \\bar{x} = \\bar{0}</script></p><div fieldId = '1'><script type=\"math/tex\" TeXtemplate='\\bar{v}_{fieldId}='>\\bar{v}_{1}=</script>&nbsp;<span class=\"inputfield\" style=\"font-size: 100%; font-family: STIXGeneral-Regular; \" contenteditable=\"true\" fieldId = \"1\"></span><span class='addField'>&oplus;</span><span class='removeField hidden'>&otimes;</span></div><p><input promptId = '4' class=\"evalButton\" type=\"button\" value=\"Submit\" />&nbsp;<span id='resultsField4'></span></p></div>";
				pData.setPrompt(temp);
				reply = reply + temp;

				test = "(Catch[Scan[If[Not[SyntaxQ[#]], Throw[\"Syntax error is found in \" <> #]] &, {##2}];Module[{exprList, i},exprList = Map[ToExpression, {##2}];For[i = 1, i <= #1, i++,If[Not[VectorQ[exprList[[i]], NumericQ]], Throw[\"Answer (\" <> ToString[i] <> \") is not a valid vector\"]];If[Length[exprList[[i]]] != 3, Throw[\"Answer (\" <> ToString[i] <> \") is not a three-dimensional vector\"]];If[Norm[exprList[[i]]] < 10^(-6), Throw[\"Your answer (\" <> ToString[i] <> \") seems to be a zero vector\"]]];If[3-#1 != " + kTalker.executeCommand("rank") + ", Throw[\"You have provided an incorrect number of solutions (either too many, or too few).\"]];If[" + kTalker.executeCommand("rank") + " == 1,If[MatrixRank[exprList, Tolerance -> 10^(-6)] < 2, Throw[\"You must provide linearly independent vectors\"]];If[Norm[(" + kTalker.executeCommand("A") + ").exprList[[1]]] > 10^(-6), Throw[\"Your answer (1) does not seem to be a solution\"]];If[Norm[(" + kTalker.executeCommand("A") + ").exprList[[2]]] > 10^(-6), Throw[\"Your answer (2) does not seem to be a solution\"]];Throw[\"&#10003;\"];,If[Norm[(" + kTalker.executeCommand("A") + ").exprList[[1]]] > 10^(-6), Throw[\"Your answer (1) does not seem to be a solution\"]];Throw[\"&#10003;\"];]];]) & @@";
				pData.setTestString(test);
				pPusher.push("4",pData);
				ProblemDataPusher.problemTestsStorage.put(4, test);
				break;
			case 003:
				kTalker.executeCommand("ClearAll[x, y, f, g, if, ig, a, b, c, fa, fb, graph, graphString,density];Block[{},density = RandomChoice[{1/2, 1/3, 1/4, 1/5, 1, 2, 3, 4, 5}];c = RandomChoice[{1/2, 1/3, 1/4, 1, 2, 3, 4}];a = RandomChoice[{1/5, 1/4, 1/3, 1/2, 1}];b = RandomChoice[{2, 3, 4}];f = (c #^2) &;fa = f[a]; fb = f[b];g = Simplify[fa (# - b)/(a - b) + fb (# - a)/(b - a)] &;graph = Show[{Plot[{f[x], g[x]}, {x, a - 1, b + 1}, PlotStyle -> None, Filling -> {1 -> {2}},PlotRange -> {{a, b}, {fa, fb}}],Plot[{f[x], g[x]}, {x, a - 1, b + 1}, PlotStyle -> {Directive[Thick, Purple], Directive[Thick, Red]},PlotRange -> {{Min[a - 1, -1/2], b + 1}, {Min[fa - 1, -1/2], fb + 1}}]},Axes -> True;AspectRatio -> 1;AxesStyle -> Directive[FontSize -> 16, Thickness[0.003]],AxesOrigin -> {-0.01, -0.01},PlotRange -> {{Min[a - 1, -1/2], b + 1}, {Min[fa - 1, -1/2], fb + 1}},AxesLabel -> {Style[TraditionalForm[x], FontSize -> 20], Style[TraditionalForm[y], FontSize -> 20]}];if = Sqrt[#/c] &;ig = Simplify[a (# - fb)/(fa - fb) + b (# - fa)/(fb - fa)] &;]");
//				KernelTalker.globalMl.discardAnswer();
//				KernelTalker.globalMl.evaluate("ExportString[graph, \"SVG\"]");
//				
				String graphString=kTalker.evaluateToString("ExportString[graph, \"SVG\"]");

				reply = "<div promptId = '5'><h2>Integrals</h2></div><div promptId = '5'><p>Let the region of density <script type=\"math/tex\">\\delta = " + kTalker.executeCommand("TeXForm[density]") + "</script> in <script type=\"math/tex\"> x y</script>-plane be bounded by two functions <script type=\"math/tex\"> f(x) = " + kTalker.executeCommand("TeXForm[TraditionalForm[f[x]]]") + "</script> and <script type=\"math/tex\"> g(x) = " + kTalker.executeCommand("TeXForm[TraditionalForm[g[x]]]") + "</script></p><div>" + graphString + "</div></div>";
				reply+= "<div><h2>Center of Mass</h2></div><div promptId = '6'><p>Set up the integral for computing the moment <script type=\"math/tex\">M_x</script> about the <script type=\"math/tex\">x</script>-axis (you may use either vertical or horizontal slices)</p><div promptId = '6' class='inputbox'><script type=\"math/tex\">M_{x}=</script>&nbsp;<span style='display: inline-block; position: relative; font-size: 104%;'><span class='msubsup' style='display: inline-block; position: relative;'><span class='mo' style='font-family: STIXIntegralsD; vertical-align: -0.615em; '>&#x222B;</span><span style='display:inline-block; '><span class='mrow' style='display:block; position:relative; left:0.5em;'><span fieldId = '1' class='mn inputfield' contenteditable='true' style='font-size: 70.7%; font-family: STIXGeneral-Regular; '></span></span><span class='mrow'  style='display:block; position:relative; top: 1.1em; left: -0.2em;'><span fieldId = '2' class='mn inputfield' contenteditable='true' style='font-size: 70.7%; font-family: STIXGeneral-Regular; '></span></span>	</span>	</span><span class='mrow'><span fieldId = '3' class='mi inputfield' contenteditable='true' style='font-family: STIXGeneral-Regular; '></span></span><span class='mspace' style='height: 0em; vertical-align: 0em; width: 0.188em; display: inline-block; overflow: hidden; '></span><span class='mi' style='font-family: STIXGeneral-Italic; '>d</span><span class='mrow'><span fieldId = '4' class='mi inputfield' contenteditable='true' style='font-family: STIXGeneral-Regular; '></span></span></span>&nbsp;<input promptId = '6' class=\"evalButton\" type=\"button\" value=\"Submit\" />&nbsp;<span id='resultsField6'></span></div></div>";
				test = "(Catch[Scan[If[Not[SyntaxQ[#]], Throw[\"Syntax error is found in '\" <> # <> \"'\"]] &, {##2}];Module[{upperLimit, lowerLimit, func, iVar, strippedIVar, transformedFunc},strippedIVar = StringReplace[#5, Whitespace -> \"\"];If[Or[StringLength[strippedIVar] > 1, StringLength[strippedIVar] < 1, Not[LetterQ[strippedIVar]]], Throw[\"Integration variable must be a single letter\"]];transformedFunc = StringReplace[#4, {strippedIVar ~~ Whitespace ~~ \"(\" -> strippedIVar <> \"*(\", strippedIVar <> \"(\" -> strippedIVar <> \"*(\"}];Block[{x},{upperLimit, lowerLimit, func, iVar} = Map[ToExpression[#, TraditionalForm] &, {#2, #3, transformedFunc, strippedIVar}];func = ReplaceAll[func, iVar -> x];If[Or[Not[NumericQ[upperLimit]], Not[NumericQ[lowerLimit]]], Throw[\"Limits of integration must be real numbers\"]];If[PossibleZeroQ[Simplify[ (" + kTalker.executeCommand("density (g[x]^2 - f[x]^2)/2") + ") - func]],If[And[PossibleZeroQ[upperLimit - (" + kTalker.executeCommand("b") + ")], PossibleZeroQ[lowerLimit - (" + kTalker.executeCommand("a") + ")]],Throw[\"&#10003;\"],Throw[\"Your limits of integration are not correct\"]],If[PossibleZeroQ[Simplify[ (" + kTalker.executeCommand("density x (if[x] - ig[x])") + ") - func]],If[And[PossibleZeroQ[upperLimit - (" + kTalker.executeCommand("fb") + ")], PossibleZeroQ[lowerLimit - (" + kTalker.executeCommand("fa") + ")]],Throw[\"&#10003;\"],Throw[\"Your limits of integration are not correct\"]],Throw[\"The integrand is not correct\"]]]];];]) & @@";
				ProblemDataPusher.problemTestsStorage.put(6, test);
				break;
			case 004:
				reply = "<div promptId='7'><h2>Linear Algebra in Integration</h2></div><div promptId='7'><p>Consider the integration of a family of functions <script type=\"math/tex\">f_k(x)=x\\,\\ln^k(x)</script>, where <script type='math/tex'>k=1,\\,2,\\,\\ldots,\\,n</script>.</p><p>The integration operation can be viewed as a linear transformation <script type='math/tex; mode=display'>T:V\\to W </script>where <script type='math/tex'>V=\\mathrm{span} \\left(\\{x\\, \\ln^k(x)\\})\\right)</script> and <script type='math/tex'>W</script> is the space of antiderivatives.</p></div>";
				
				reply+= "<div promptId='8'><h2>Basis of Antiderivatives</h2></div><div promptId='8'><p>Describe the basis of the space <script type='math/tex'>W</script>. You may use constants <script type='math/tex'>k</script> and <script type='math/tex'>n</script> in your answers.</p><p>Hint: integrate by parts once and try to deduce a general pattern.</p><div fieldId = '1'><script type=\"math/tex\" TeXtemplate='g_{k,fieldId}(x)='>g_k(x)=</script>&nbsp;<span class=\"inputfield\" style=\"font-size: 100%; font-family: STIXGeneral-Regular; \" contenteditable=\"true\" fieldId = \"1\"></span><span class='addField'>&oplus;</span><span class='removeField hidden'>&otimes;</span></div><p><input promptId = '8' class=\"evalButton\" type=\"button\" value=\"Submit\" />&nbsp;<span id='resultsField8'></span></p></div>";
				break;
		}
		ProblemDataPusher.problemDataStorage.put(id,evalString);
		return reply;
	}
	
	public String getProblemStatementAsStringV2(String course, String id)
	{
		KernelTalker kTalker = new KernelTalker();
		ProblemDataPusher pPusher = new ProblemDataPusher();
		ProblemData pData;
		ArrayList<String> promptStack = new ArrayList<String>(),tempList;
		String 	reply ="", 
				problemId,
				xmlString, 
				tempOptionString;
		
		promptStack.add(course+"."+id);
		kTalker.executeCommand("SetDirectory[\"/Users/evgeny/Google Drive/Eclipse Workspace/twist/src/Mathematica\"]");
		while(!promptStack.isEmpty())
		{
			id = promptStack.remove(0);
			problemId = id;
			pData = pPusher.get(problemId);
			if(pData != null)
			{
				tempOptionString = pData.getAllOptionsStrings();
				if(tempOptionString!=null)
				{
					xmlString = kTalker.evaluateToString("Needs[\"math1206`\"];makeproblem[{\""+id+"\"}, \"A\"," + tempOptionString + "]");
				}
				else
				{
					xmlString = kTalker.evaluateToString("Needs[\"math1206`\"];makeproblem[{\""+id+"\"}, \"A\"]");
				}
			}
			else
			{
				xmlString = kTalker.evaluateToString("Needs[\"math1206`\"];makeproblem[{\""+id+"\"}, \"A\"]");
			}
			
			pData = parseXMLResponse(problemId, xmlString, pData);
			reply += pData.getPrompt();
			pPusher.push(problemId, pData);
			
			tempList = pData.getSupplyPrompts();
			if ((tempList != null)&&(tempList.size()>0))
			{
				promptStack.addAll(tempList);
			}
			
			tempOptionString = pData.getAllOptionsStrings();
			if((tempOptionString!=null)&&(tempOptionString.length()>0))
			{
				for(int i=0; i<tempList.size();i++)
				{
					pData = new ProblemData();
					pData.addOptionsString(problemId, tempOptionString);
					pPusher.push(tempList.get(i), pData);
				}
				
				tempList = pData.getFetchPrompt();
				for(int i=0; i<tempList.size();i++)
				{
					pData = new ProblemData();
					pData.addOptionsString(problemId, tempOptionString);
					pPusher.push(tempList.get(i), pData);
				}
			}
		}
		return reply;
	}
	
	private ProblemData parseXMLResponse(String id, String str, ProblemData pData)
	{
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
		
		if (pData == null) {pData = new ProblemData();}
		try
		{
			XMLInputFactory xif = XMLInputFactory.newInstance();
			xif.setProperty(XMLInputFactory.IS_REPLACING_ENTITY_REFERENCES, Boolean.TRUE);
			xif.setProperty(XMLInputFactory.IS_COALESCING, Boolean.TRUE);

			StringReader sr = new StringReader(str);
			XMLStreamReader xmlr = xif.createXMLStreamReader(sr);
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
		
		}
		catch (FactoryConfigurationError e)
		{
			e.printStackTrace();
		}
		catch (XMLStreamException e) 
		{
			e.printStackTrace();
		}
		return pData;
	}
	
	public String getProblemQuestionsAsString(int id)
	{
		String reply="";
		
		return reply;
	}
	
	public ProblemData getProblem(String course, String id)
	{
		ProblemData pData = new ProblemData();
		
		return pData;
	}
}
