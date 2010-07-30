package com.jklas.search.query.bool;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

import com.jklas.search.engine.Language;
import com.jklas.search.engine.dto.ObjectKeyResult;
import com.jklas.search.engine.processor.DefaultQueryTextProcessor;
import com.jklas.search.engine.processor.QueryTextProcessor;
import com.jklas.search.index.Term;
import com.jklas.search.query.operator.AndOperator;
import com.jklas.search.query.operator.MinusOperator;
import com.jklas.search.query.operator.Operator;
import com.jklas.search.query.operator.OrOperator;
import com.jklas.search.query.operator.RetrieveOperator;

public class BooleanQueryParser {

	private static BooleanPostingListExtractor extractor = new BooleanPostingListExtractor();
	
	private final String plainTextQuery;
	
	private final Language queryLanguage;

	private final QueryTextProcessor textProcessor;
	
	public BooleanQueryParser(String originalQuery) {
		this(originalQuery, Language.UNKOWN_LANGUAGE); 
	}
	
	public BooleanQueryParser(String originalQuery, Language language) {
		this(originalQuery, language, new DefaultQueryTextProcessor() ); 
	}

	public BooleanQueryParser(String originalQuery, QueryTextProcessor queryTextProcessor) {
		this(originalQuery, Language.UNKOWN_LANGUAGE, queryTextProcessor);
	}
	
	public BooleanQueryParser(String originalQuery, Language language, QueryTextProcessor queryTextProcessor) {
		
		if(originalQuery == null) throw new IllegalArgumentException("Can't parse a null query");
		if(language == null) throw new IllegalArgumentException("Can't work with a null language... (UNKNOWN_LANGUAGE is allowed, but not null)");
		if(queryTextProcessor == null) throw new IllegalArgumentException("Can't work with a null text processor");
		
		this.plainTextQuery = originalQuery;
		this.queryLanguage = language;
		this.textProcessor = queryTextProcessor;
	}

	public BooleanQuery getQuery() {
		List<Term> queryTokens = textProcessor.processText(plainTextQuery, queryLanguage);

		removeLeadingAnd(queryTokens);

		int tokenCount = queryTokens.size();
		
		if(tokenCount == 0)
			throw new IllegalArgumentException("Query must contain at least one term");		
		else
			if(tokenCount == 1) 
				return buildOneTokenQuery(queryTokens);
			else if(tokenCount == 2)
				return buildTwoTokenQuery(queryTokens);
			else {	
				return new BooleanQuery( buildOperatorForMultiTokenQuery(queryTokens) );
			}		
	}

	private Operator<ObjectKeyResult> buildOperatorForMultiTokenQuery(List<Term> queryTokens) {
		Operator<ObjectKeyResult> root;
		int lastOrAt = -1;
		
		Stack<Operator<ObjectKeyResult>> operatorStack = new Stack<Operator<ObjectKeyResult>>();
		List<Term> notTerms = new ArrayList<Term>();

		for (Iterator<Term> iterator = queryTokens.iterator(); iterator.hasNext();) {
			Term currentTerm = iterator.next();			
			
			if(MinusOperator.isOperator(currentTerm)) {
				// tests if there's something after the +NOT
				if(!iterator.hasNext()) throw new IllegalArgumentException("Bad query syntax, +NOT must be followed by a term");

				iterator.remove();				
				Term nextTerm = iterator.next();
				if(isBooleanOperator(nextTerm)) throw new IllegalArgumentException("Bad query syntax, +NOT must be followed by a term, not an operator");
				iterator.remove();
				
				notTerms.add(nextTerm);
			}
		}

		for (int i = 0; i < queryTokens.size(); i++) {
			
			int tokenCount = queryTokens.size();
			
			Term currentTerm = queryTokens.get(i);
			boolean isLastTerm = (i == tokenCount -1);

			if(isLastTerm && isBooleanOperator(currentTerm)) throw new IllegalArgumentException("Bad query syntax, last token is a binary operator");

			if(OrOperator.isOperator(currentTerm)) {
				operatorStack.push(recursiveLeftToRightAnd(queryTokens, lastOrAt+1, i-1));
				lastOrAt = i;
			} else {
				if(isLastTerm) {
					operatorStack.push(recursiveLeftToRightAnd(queryTokens, lastOrAt+1, tokenCount-1));
				}
			}
		}
						
		// only happens when query has only +NOT operators
		if(operatorStack.size()==0) {
			root = new RetrieveOperator<ObjectKeyResult>(queryTokens.get(0),extractor);
		} else if(operatorStack.size()==1) {
			root = operatorStack.pop();
		} else {
			root = buildOperatorsFromStack(operatorStack.pop(),operatorStack);			
		}
		
		if(notTerms.size() > 0)	{
			root = buildRecursiveNot( notTerms, 0, root );
		}
		
		return root;
	}

	private Operator<ObjectKeyResult> buildRecursiveNot(List<Term> notTerms, int from, Operator<ObjectKeyResult> root) {
		if(from == notTerms.size()) {
			return root;
		} else {
			return new MinusOperator<ObjectKeyResult>(
						 buildRecursiveNot( notTerms, from + 1, root) ,
						new RetrieveOperator<ObjectKeyResult>( notTerms.get(from) , extractor ) );
		}
	}

	private boolean isBooleanOperator(Term currentToken) {
		return  OrOperator.isOperator(currentToken) || AndOperator.isOperator(currentToken) || MinusOperator.isOperator(currentToken) ; 
	}

	private BooleanQuery buildTwoTokenQuery(List<Term> queryTokens) {
		Term firstToken  = queryTokens.get(0);
		Term secondToken = queryTokens.get(1);

		if(	isBooleanOperator(firstToken) || isBooleanOperator(secondToken) )	throw new IllegalArgumentException("Bad query syntax, can't use binary operator without two terms");
		
		return new BooleanQuery(new AndOperator<ObjectKeyResult>(new RetrieveOperator<ObjectKeyResult>(firstToken, extractor),
				new RetrieveOperator<ObjectKeyResult>(secondToken,extractor)));
	}

	private BooleanQuery buildOneTokenQuery(List<Term> queryTokens) {
		Term firstToken = queryTokens.get(0);

		if(AndOperator.isOperator(firstToken) || OrOperator.isOperator(firstToken) || MinusOperator.isOperator(firstToken))
			throw new IllegalArgumentException("Bad query syntax, can't use binary operator without two terms");

		return new BooleanQuery(new RetrieveOperator<ObjectKeyResult>(firstToken,extractor));
	}

	private void removeLeadingAnd(List<Term> queryTokens) {
		if(queryTokens.size()>0 && AndOperator.isOperator(queryTokens.get(0))) queryTokens.remove(0);
	}

	public Operator<ObjectKeyResult> buildOperatorsFromStack(Operator<ObjectKeyResult> left, Stack<Operator<ObjectKeyResult>> operatorStack)
	{
		if(operatorStack.size()>1)
			return new OrOperator<ObjectKeyResult>(left, buildOperatorsFromStack(operatorStack.pop(), operatorStack));
		else
			return new OrOperator<ObjectKeyResult>(operatorStack.pop(),left);
	}

	private Operator<ObjectKeyResult> recursiveLeftToRightAnd(List<Term> queryTokens, int current, int rightmost) {
		Term currentTerm = queryTokens.get(current);
		if(current == rightmost) {
			return new RetrieveOperator<ObjectKeyResult>(currentTerm,extractor);
		} else {
			if(AndOperator.isOperator(currentTerm)) 
				return recursiveLeftToRightAnd(queryTokens, current+1, rightmost);
			else
				return new AndOperator<ObjectKeyResult>( new RetrieveOperator<ObjectKeyResult>(currentTerm ,extractor),
						recursiveLeftToRightAnd(queryTokens, current+1, rightmost) );
		}
	}	
}
